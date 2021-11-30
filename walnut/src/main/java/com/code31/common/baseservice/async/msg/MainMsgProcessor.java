package com.code31.common.baseservice.async.msg;

public final class MainMsgProcessor implements IMsgProcessor {
	/** 主消息处理器实例 */
	private static volatile MainMsgProcessor _instance = null;

	/** 日志对象 */
//	protected final Logger LOG = CoreLoggers.serverLogger;
	/** 内置消息处理器 */
	private QueueMsgProcessor _innerMsgProc = null;

	/**
	 * 类默认构造器
	 * 
	 */
	private MainMsgProcessor() {
		this._innerMsgProc = new QueueMsgProcessor(
			new ExecutableMsgHandler(), 
			"Game-MainMsgProcessor"
		);
	}

	/**
	 * 获取单例对象
	 * 
	 * @return 
	 * 
	 */
	public static MainMsgProcessor theInstance() {
		if (_instance == null) {
			synchronized (MainMsgProcessor.class) {
				if (_instance == null) {
					_instance = new MainMsgProcessor();
				}
			}
		}

		return _instance;
	}

	@Override
	public boolean isFull() {
		return _innerMsgProc.isFull();
	}

	/**
	 * <pre>
	 * 1、服务器内部消息、玩家不属于任何场景时发送的消息，单独一个消息队列进行处理
	 * 2、玩家在场景中发送过来的消息，添加到玩家的消息队列中，在场景的线程中进行处理
	 * </pre>
	 */
	@Override
	public void put(Runnable msg) {
		{
			// 执行消息
			this.execute(msg);
		}
	}

	@Override
	public void start() {
		this._innerMsgProc.start();
	}

	@Override
	public void stop() {
		this._innerMsgProc.stop();
	}

	/**
	 * 获得主消息处理线程Id
	 * 
	 * @return
	 */
	public long getThreadId() {
		return this._innerMsgProc.getThreadId();
	}

	public int getQueueLength(){
		return  this._innerMsgProc.getQueueLength();
	}

	/**
	 * @return
	 */
	public boolean isStop() {
		return this._innerMsgProc.isStop();
	}

	/**
	 * 执行 Runnable 
	 * 
	 * @param r
	 * @param countFlag 
	 * 
	 */
	private void execute(final Runnable r) {
		if (r == null) {
			// 如果参数对象为空, 
			// 则直接退出!
			return;
		}

		// 添加消息到处理器
		this._innerMsgProc.put(r);
	}
}