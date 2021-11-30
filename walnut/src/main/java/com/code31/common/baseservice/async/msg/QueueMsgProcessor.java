package com.code31.common.baseservice.async.msg;

import com.code31.common.baseservice.utils.ErrorsUtil;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.LinkedList;
import java.util.List;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.LinkedBlockingQueue;

/**
 * 基于队列的消息处理器,使用单线程依次用于处理队列中消息
 * 
 * @author songlin.luo
 * 
 * @see {@link NoQueueMessageProcessor}
 */
public class QueueMsgProcessor implements Runnable, IMsgProcessor {
	
	/** 消息处理时发生未知的异常 */
	public static final String MSG_PRO_ERR_EXP = "MSG.PRO.ERR.EXP";
	/** 消息处理时收到空的消息 */
	public static final String MSG_PRO_ERR_NULL_MSG = "MSG.PRO.ERR.NULL.MSG";
	/** 线程被中断 */
	public static final String THRAD_ERR_INTERRUPTED = "THR.ERR.INTRRUPTED";
	
	/** 内置线程默认名称 */
	private static final String INNER_THREAD_DEFAULT_NAME = "MessageProcessor Thread";

	private static final Logger queue_logger = LoggerFactory.getLogger(QueueMsgProcessor.class);
	private static final Logger logger = LoggerFactory.getLogger("app.msg");

	/** 消息队列 * */
	private final BlockingQueue<Runnable> queue;

	/** 消息处理线程停止时剩余的还未处理的消息 **/
	private volatile List<Runnable> leftQueue;

	/** 消息处理器 * */
	private final IMsgHandler handler;

	/** 消息处理线程 */
	private volatile Thread messageProcessorThread;

	/** 运行的线程id * */
	private long threadId;

	/** 是否停止 */
	private volatile boolean stop = false;

	/** 处理的消息总数 */
	private long statisticsMessageCount = 0;

	/** 等待消息处理线程退出 */
	private volatile CountDownLatch stopLatch;

	private final boolean processLeft;

	/** 内置线程名称 */
	private String _innerThreadName;

	/**
	 * 类参数构造器
	 * 
	 * @param messageHandler
	 * @param innerThreadName 内置线程名称
	 * 
	 */
	public QueueMsgProcessor(
		IMsgHandler messageHandler, 
		String innerThreadName) {
		this(messageHandler, false, innerThreadName);
	}

	/**
	 * 类参数构造器
	 * 
	 * @param messageHandler
	 * @param processLeft
	 * @param innerThreadName 内置线程名称
	 * 
	 */
	public QueueMsgProcessor(
		IMsgHandler messageHandler,
		boolean processLeft, 
		String innerThreadName) {
		queue = new LinkedBlockingQueue<Runnable>();
		handler = messageHandler;
		this.processLeft = processLeft;
		this._innerThreadName = innerThreadName;
	}

	/**
	 * 类默认构造器
	 * 
	 * @see ExecutableMsgHandler 
	 * 
	 */
	public QueueMsgProcessor() {
		this(new ExecutableMsgHandler(), INNER_THREAD_DEFAULT_NAME);
	}

	/**
	 * 类参数构造器
	 * 
	 * @param innerThreadName
	 * 
	 * @see ExecutableMsgHandler 
	 * 
	 */
	public QueueMsgProcessor(String innerThreadName) {
		this(new ExecutableMsgHandler(), innerThreadName);
	}

	@Override
	public void put(Runnable msg) {
		if (this.stop) {
			return;
		}
		try {
			queue.put(msg);
			if (queue_logger.isDebugEnabled()) {
				queue_logger.debug("put queue size:" + queue.size());
			}
		} catch (InterruptedException e) {
			if (logger.isErrorEnabled()) {
				logger.error(THRAD_ERR_INTERRUPTED, e);
			}
		}
	}

	/**
	 * 主处理函数，从消息队列中阻塞取出消息，然后处理
	 */
	public void run() {
		threadId = Thread.currentThread().getId();
		final CountDownLatch countDownLatch = new CountDownLatch(1);
		this.stopLatch = countDownLatch;
		long tickcount = 0;
		try {
			while (!stop) {
				try {
					process(queue.take());
//					if (queue_logger.isDebugEnabled()) {
//						queue_logger.debug("run queue size:" + queue.size());
//					}
					
					tickcount++;
					
//					if ( (tickcount % 1000) == 0){
//						logger.info("queue len:" + this.getQueueLength());
//					}
					
				} catch (InterruptedException e) {
					if (logger.isWarnEnabled()) {
						logger
								.warn("[#CORE.QueueMessageProcessor.run] [Stop process]");
					}
					break;
				} catch (Exception e) {
					logger.error(MSG_PRO_ERR_EXP, e);
				}
			}
		} finally {
			countDownLatch.countDown();
		}
	}

	/**
	 * 处理具体的消息，每个消息有自己的参数和来源,如果在处理消息的过程中发生异常,则马上将此消息的发送者断掉
	 * @param msg
	 */
	public void process(Runnable msg) {
		if (msg == null) {
			if (logger.isWarnEnabled()) {
				logger.debug("[#CORE.QueueMessageProcessor.process] ["
						+ MSG_PRO_ERR_NULL_MSG + "]");
			}
			return;
		}
		long begin = 0;
		if (logger.isInfoEnabled()) {
			begin = System.nanoTime();
		}
		this.statisticsMessageCount++;
		try {
			this.handler.execute(msg);
		} catch (Exception e) {
			if (logger.isErrorEnabled()) {
				logger.error(ErrorsUtil.error("Error",
						"#.QueueMessageProcessor.process", "param"), e);
			}
			
		} finally {
			if (logger.isInfoEnabled()) {
				// 特例，统计时间跨度
				long time = (System.nanoTime() - begin) / (1000 * 1000);
				if (time > 1) {
					logger.debug("ThreadName : " + this._innerThreadName
						+ ", MsgClazz : " + msg.getClass()
						+ ", Time : " + time + " ms" 
						+ ", Total : " + this.statisticsMessageCount);
				}
			}
		}
	}

	public IMsgHandler getHandler() {
		return handler;
	}

	public long getThreadId() {
		return threadId;
	}

	/**
	 * 取得未处理消息队列的长度
	 * 
	 * @return
	 */
	public int getQueueLength() {
		return queue.size();
	}

	public synchronized void stop() {
		logger.info("Message processor thread " + this.messageProcessorThread
				+ " stopping ...");
		this.stop = true;
		if (this.messageProcessorThread != null) {
			this.messageProcessorThread.interrupt();
		}
		logger.info("Message processor thread " + this.messageProcessorThread
				+ " finish");
		this.messageProcessorThread = null;
		// 需要处理完未处理的消息
		// 等待消息处理线程退出
		try {
			final CountDownLatch _stopLatch = this.stopLatch;
			if (_stopLatch != null) {
				_stopLatch.await();
			}
			// 将未处理的消息放入到leftQueue中,以备后续处理
			if (this.processLeft) {
				this.leftQueue = new LinkedList<Runnable>();
				while (true) {
					Runnable _msg = this.queue.poll();
					if (_msg != null) {
						this.leftQueue.add(_msg);
					} else {
						break;
					}
				}
			}
		} catch (InterruptedException e) {
		}
		this.queue.clear();
	}

	/**
	 * 开始消息处理
	 */
	public synchronized void start() {
		this.queue.clear();
		stop = false;
		if (this.messageProcessorThread != null) {
			throw new IllegalStateException("The thread has already run"
					+ this.messageProcessorThread);
		}

		if (this._innerThreadName == null || 
			this._innerThreadName.isEmpty()) {
			this._innerThreadName = INNER_THREAD_DEFAULT_NAME;
		}

		this.messageProcessorThread = new Thread(this, this._innerThreadName);
		this.messageProcessorThread.start();

		logger.info("Message processor thread started ["
				+ this.messageProcessorThread + "]");
	}

	/**
	 * 用于监控主消息处理线程异常退出的情况,如果发现线程退出,则尝试重新启动它
	 * 
	  *
	 * 
	 */

	/**
	 * 达到5000上限时认为满了
	 */
	@Override
	public boolean isFull() {
		return this.queue.size() > 5000;
	}

	/**
	 * 取得消息处理器停止后的遗留的消息
	 * 
	 * @return the leftQueue
	 */
	public List<Runnable> getLeftQueue() {
		return leftQueue;
	}

	/**
	 * 重置
	 */
	public void resetLeftQueue() {
		this.leftQueue = null;
	}

	/**
	 * 用于监控主消息处理线程异常退出的情况,如果发现线程退出,则尝试重新启动它
	 * 
	  *
	 * 
	 */
	@Deprecated
	protected class MessageProcessorThreadMonitor implements
			Thread.UncaughtExceptionHandler {
		private long lastCreateTime = 0;

		@Override
		public void uncaughtException(Thread t, Throwable e) {
			logger.error("Uncaught exception in thread [" + t + "]", e);
			if (t == messageProcessorThread) {
				if (System.currentTimeMillis() - lastCreateTime < 1000 * 60) {
					logger.warn("The message process's last create time is ["
							+ lastCreateTime
							+ "],too frequently to restart the thread.");
					return;
				}
				logger.warn("The message process thread has broken,restart it.");
				// 主消息处理线程挂了,尝试重新启动一个
				System.gc();
				System.runFinalization();
				start();
				logger.warn("The message process thread has restarted.");
			} else {
				throw new RuntimeException(e);
			}
		}
	}

	public boolean isStop() {
		return stop;
	}
}