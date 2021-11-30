package com.code31.common.baseservice.async;


import com.code31.common.baseservice.async.msg.IMsgProcessor;
import com.code31.common.baseservice.async.msg.IoOperMsg;
import com.code31.common.baseservice.utils.ExecutorUtil;
import com.code31.common.baseservice.utils.TimeUtils;
import com.google.common.collect.Maps;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.HashMap;
import java.util.concurrent.ConcurrentMap;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.ThreadFactory;
import java.util.concurrent.atomic.AtomicLong;


public class AsyncIoOperServImpl implements IIoOperServ {
	/** 日志对象 */
	private static final Logger LOG = LoggerFactory.getLogger("app.msg");

// 常量定义
/////////////////////////////////////////////////////////////////////////////

	/** 普通的(非绑定 UUID 的) IO 线程名称 */
	private static final String S_COMMON = "io::comservice::";
	/** 普通的(非绑定 UUID 的) IO 线程数量 */
	private static final int N_COMMON = 5;
	
	/** 绑定 UUID 的 IO 线程名称 */
	private static final String S_BIND_UUID = "io::bindUUID::";
	/** 绑定 UUID 的 IO 线程数量 */
	private static final int N_BIND_UUID = 20;


// 私有成员
/////////////////////////////////////////////////////////////////////////////

	/** 普通线程池 */
	private ExecutorService _commonES = null;
	/** 绑定 UUID 线程池数组 */
	private ExecutorService[] _bindUUIDESArray;


	/** 消息处理器 */
	private IMsgProcessor _msgProcessor = null;
	
	/** 处理的任务总数 */
	private AtomicLong statisticsCount = new AtomicLong(0);
	/** 完成的个数 */
	private AtomicLong finishedCount = new AtomicLong(0);
	
	
	/** 每5分钟统计一次 */
	private static final long _STATISTICS_TIME = 5;
	
	/** 时间->任务处理个数 */
	private ConcurrentMap<Long,AtomicLong> statisticsTimeCountMap = null;
	/** 时间->完成个数*/
	private ConcurrentMap<Long,AtomicLong> finishedCountMap = null;

	
	/**
	 * 类默认构造器
	 * 
	 * @param msgProcessor
	 * 
	 */
	public AsyncIoOperServImpl(IMsgProcessor msgProcessor) {
		// 消息处理器
		this._msgProcessor = msgProcessor;
		// 初始化
		this.init();
	}

	/**
	 * 初始化
	 * 
	 */
	private void init() {
		// 初始化线程池
		//
		// 普通线程池
		this._commonES = Executors.newFixedThreadPool(
			N_COMMON, 
			new MyThreadFactory(S_COMMON)
		);

		// 绑定 UUID 线程池
		this._bindUUIDESArray = new ExecutorService[N_BIND_UUID];

		for (int i = 0; i < N_BIND_UUID; i++) {
			this._bindUUIDESArray[i] = Executors.newFixedThreadPool(
				1, new MyThreadFactory(S_BIND_UUID, i)
			);
		}
		
		statisticsTimeCountMap = Maps.newConcurrentMap();
		finishedCountMap = Maps.newConcurrentMap();
	

	}

	@Override
	public void execute(final IIoOper op) {
		if (op == null) {
			// 如果参数对象为空, 
			// 则直接退出!
			return;
		}

		//防止跨线程的问题
		IoOperMsg msg = new IoOperMsg() {
		
			@Override
			public void execute() {
				onOperDone(op);
				
			}
		};

		//放置到消息处理器中
		if (this._msgProcessor != null) {
			this._msgProcessor.put(msg);
		}
		
		
	}

	/**
	 * 处理已经完成准备工作的 IO 操作, 在该函数中 IO 操作对象将被分派到各 IO 线程池中!
	 * 
	 * @param innerOp
	 * 
	 */
	private void onOperDone(final IIoOper innerOp) {
		if (innerOp == null) {
			return;
		}

		// 线程池
		ExecutorService es = null;
		
		final long timeNow = System.currentTimeMillis();
		
		final long begin = TimeUtils.getBeginOfDay(timeNow);
		final long hour = TimeUtils.getHourTime(timeNow);
		final long min = TimeUtils.getMinTime(timeNow);
		
		final long timekey = begin + ((hour*60+min)/_STATISTICS_TIME);
		
		if (innerOp instanceof AbstractBindTaskOper) {
			//
			// 如果内置 IO 操作是绑定 UUID 的,
			// 则将当前 IO 操作提交给 bindUUIDESArray 中!
			// 具体提交给哪一个,
			// 是根据 bindUUID % N_BIND_UUID 计算得出
			//
			// 获取绑定的 UUID
			String bindUUID = ((AbstractBindTaskOper) innerOp).getBindTaskID();
		
			int hashcode = bindUUID.hashCode();
			if (hashcode < 0)
				hashcode = hashcode*-1;
			// 计算余数
			int index = (hashcode % N_BIND_UUID);
			// 获取线程池
			es = this._bindUUIDESArray[index];
		} 
		else if (innerOp instanceof AbstractJobTaskOper) {

			// 均匀放到队列中
			int index = (int)(statisticsCount.get() % N_BIND_UUID);
			// 获取线程池
			es = this._bindUUIDESArray[index];
		} 
		else {
			//
			// 如果内置 IO 操作既没有绑定 UUID,
			// 也不是与 local 相关的,
			// 则将当前 IO 操作提交给 commonES!
			//
			es = this._commonES;
		}
		
		
		// 将 IO 操作提交到线程池
		es.submit(new Runnable() {
			@Override
			public void run() {
				// IO 开始时间
				long t0 = System.currentTimeMillis();
				// 执行 IO 操作
				innerOp.doIo();
				
				
				// .doIo();
				// IO 结束时间
				long t1 = System.currentTimeMillis();

				taskfinishedCallback( timekey);

				//大于一秒中
				if (t1 - t0 > 1000){
					LOG.info(
							"IO clazz = " + innerOp.getClass().getSimpleName()
									+ ", IO time = " + (t1 - t0)
					);
				}

				// 记录 IO 执行时间
				LOG.debug(
					"IO clazz = " + innerOp.getClass().getSimpleName() 
					+ ", IO time = " + (t1 - t0)
				);

			}
		});
		
		try{
			statisticsCount.incrementAndGet();
	
			//每个时间段的次数
			AtomicLong timeCount = statisticsTimeCountMap.get(timekey);
			if (timeCount == null)
				timeCount = new AtomicLong(0);
			timeCount.incrementAndGet();
			statisticsTimeCountMap.putIfAbsent(timekey, timeCount);
	
			
		}catch(Exception e){
			e.printStackTrace();
		}
		
	}


	/**
	 * 任务完成时的回调
	 * @param
	 */
	private void taskfinishedCallback(long timekey){
		try{
			
			finishedCount.incrementAndGet();

			//每个时间段的次数
			AtomicLong timeCount = finishedCountMap.get(timekey);
			if (timeCount == null)
				timeCount = new AtomicLong(0);
			timeCount.incrementAndGet();
			finishedCountMap.putIfAbsent(timekey, timeCount);
			
		}catch(Exception e){
			
		}
		
	}
	
	/**
	 * 获取没有完成的任务个数
	 * @return
	 */
	public long getUnfinishedTaskCount(){
		return statisticsCount.get() - finishedCount.get();
	}
	
	
	
	
	/**
	 * 未完成的任务大概需要多久能执行完成(秒)
	 * @return
	 */
	@Override
	public long getQueueTaskWillFinishedTime(){
		
		try{

			long timeNow = System.currentTimeMillis();
			
			long begin = TimeUtils.getBeginOfDay(timeNow);
			long hour = TimeUtils.getHourTime(timeNow);
			long min = TimeUtils.getMinTime(timeNow);
			
			long timekey = begin + ((hour*60+min)/_STATISTICS_TIME);
				
			//前段时间完成个数
			AtomicLong timeCount1 = finishedCountMap.get(timekey-1);
//			AtomicLong totalCount1 = statisticsTimeCountMap.get(timekey-1);
			
			//当前段时间完成个数
			AtomicLong timeCount2 = finishedCountMap.get(timekey);
			AtomicLong totalCount2 = statisticsTimeCountMap.get(timekey);
			
			if ( totalCount2 == null)
				return 60;
			
			if ( (timeCount1 == null) || (timeCount2 == null) || (timeCount1.get() < 1) || (timeCount2.get() < 1)){
				//都处理不过来！？？
				if (finishedCountMap.size() > 1)
					return _STATISTICS_TIME;
				
				return 30;
				
			}

			if (totalCount2.get() - timeCount2.get() < N_BIND_UUID ){
				return 0;
			}
			
			
			long futuretime = (totalCount2.get() -timeCount2.get())*_STATISTICS_TIME*60/timeCount1.get();
			
			return futuretime;
			
		}catch(Exception e){
			
		}
		
		return 30;
	}
	
	/**
	 * 获取统计信息：
	 * 任务队列个数；以完成个数；最近一段时间队列个数；最近一段时间完成个数；最近一段时间
	 * @return
	 */
	@Override
	public HashMap<String,Object> getStatistics(){
		HashMap<String, Object> statisticsInfo = Maps.newHashMap();
		
		try{
			statisticsInfo.put("queue", statisticsCount.get());
			statisticsInfo.put("finished", finishedCount.get());
		
			
			long timeNow = System.currentTimeMillis();
			
			long begin = TimeUtils.getBeginOfDay(timeNow);
			long hour = TimeUtils.getHourTime(timeNow);
			long min = TimeUtils.getMinTime(timeNow);
			
			long segment1 = (hour*60+min)/_STATISTICS_TIME;
			long timekey1 = begin + segment1;
		
			//两端时间的统计信息
			for (int i = 0; i < 2; i++){
				
				//最近一段时间队列个数
				AtomicLong timeCount = statisticsTimeCountMap.get(timekey1-i);
				if (timeCount != null){
					statisticsInfo.put("time("+(segment1-i) + ") queue", timeCount.get());
				}
				
				//最近一段时间完成的个数
				AtomicLong timeFinishedCount = finishedCountMap.get(timekey1-i);
				if (timeFinishedCount != null){
					statisticsInfo.put("time("+(segment1-i) + ") finished", timeFinishedCount.get());
				}

			}
			
		}catch(Exception e){
			e.printStackTrace();
			LOG.error(e.toString());
		}
		
		return statisticsInfo;
	}
	
	@Override
	public void stop() {
		try {
			for (ExecutorService _executor : this._bindUUIDESArray) {
				ExecutorUtil.shutdownAndAwaitTermination(_executor);
			}
			ExecutorUtil.shutdownAndAwaitTermination(this._commonES);

		} catch (Exception e) {
			if (LOG.isErrorEnabled()) {
				LOG.error("#GS.AsyncManagerImpl.stop", e);
			}
		}
	}

	/**
	 * 线程命名工厂类
	 * 
	 * @author haijiang.jin
	 * 
	 */
	private static class MyThreadFactory implements ThreadFactory {
		/** 当前名称 */
		private String _prefix;
		/** 索引 */
		private int _index;

		/**
		 * 类参数构造器
		 * 
		 * @param prefix
		 * 
		 */
		public MyThreadFactory(String prefix) {
			this(prefix, 0);
		}

		/**
		 * 类参数构造器
		 * 
		 * @param prefix
		 * @param startIndex
		 * 
		 */
		public MyThreadFactory(String prefix, int startIndex) {
			this._prefix = (prefix == null) ? "" : prefix;
			this._index = startIndex;
		}

		@Override
		public Thread newThread(Runnable r) {
			if (r == null) {
				return null;
			} else {
				return new Thread(r, this._prefix + (this._index++));
			}
		}
	}

}
