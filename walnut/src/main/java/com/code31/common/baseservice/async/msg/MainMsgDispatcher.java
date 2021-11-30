package com.code31.common.baseservice.async.msg;

import java.util.HashMap;
import java.util.Map;

/**
 * 消息分发器, 将不同的消息分发给不同的队列来处理
 * 
 * @author songlin.luo
 */
public class MainMsgDispatcher<T extends IMsgProcessor> implements IMsgProcessor {
	/**
	 * 各子系统的消息分类 {@link IMsgProcessor} 的分类型配置, 
	 * 用于根据消息的类型分配到对应的 {@link IMsgProcessor} 中处理, 
	 * 处理一些相对独立的子系统的消息, 比如好友系统、公会系统的消息
	 * 
	 */
	private final Map<Class<?>, IMsgProcessor> _subSystemProcessorMap = new HashMap<Class<?>, IMsgProcessor>();

	/** 主消息处理器,用于处理一些状态全局共享的数据 */
	private final T mainProcessor;

	/** 停止处理标识 */
	private volatile boolean stop = false;
	private volatile boolean isStarted = false;

	/**
	 * 类参数构造器
	 *  
	 * @param mainProcessor 
	 * 
	 */
	public MainMsgDispatcher(T mainProcessor) {
		this.mainProcessor = mainProcessor;
	}

	/**
	 * 注册消息处理器
	 * 
	 * @param msgTypeClazz
	 * @param processor
	 */
	public void registerMessageProcessor(
		Class<?> msgTypeClazz, 
		IMsgProcessor processor) {

		if (msgTypeClazz == null || 
			processor == null) {
			return;
		}

		IMsgProcessor _processor = getSubSystemMessageProcessor(msgTypeClazz);

		if (_processor != null) {
			throw new IllegalArgumentException("The message type class["
				+ msgTypeClazz + "] has already been registed with ["
				+ _processor + "]");
		}

		this._subSystemProcessorMap.put(msgTypeClazz, processor);
	}

	/**
	 * 将待处理消息放入队列与消息类型相匹配的{@link IMsgProcessor}的处理队列中
	 */
	public void put(Runnable msg) {
		if (!stop) {
			final Class<?> _msgClass = msg.getClass();
			IMsgProcessor _processor = getSubSystemMessageProcessor(_msgClass);

			if (_processor != null) {
				_processor.put(msg);
			} else {
				// 如果没有注册, 
				// 由全局共享的处理器处理. 
				// XXX 注意: 所有消息都是由 mainProcessor 处理的, 
				// 也就是 GameMsgProcessor!
				this.mainProcessor.put(msg);
			}
		}
	}

	/**
	 * 开始处理
	 */
	public void start() {
		if (isStarted) {
			return;
		}
		isStarted = true;
		stop = false;
		this.mainProcessor.start();
		for (Map.Entry<Class<?>, IMsgProcessor> _entry : this._subSystemProcessorMap
				.entrySet()) {
			_entry.getValue().start();
		}
	}

	/**
	 * 停止处理
	 */
	public void stop() {
		stop = true;
		this.mainProcessor.stop();
		for (Map.Entry<Class<?>, IMsgProcessor> _entry : this._subSystemProcessorMap
				.entrySet()) {
			_entry.getValue().stop();
		}
	}
	
	public T getMainProcessor() {
		return mainProcessor;
	}

	/**
	 * 根据消息的类型取得对应的子系统的{@link IMsgProcessor}
	 * 
	 * @param msgClass
	 * @return
	 */
	private IMsgProcessor getSubSystemMessageProcessor(final Class<?> msgClass) {
		for (Map.Entry<Class<?>, IMsgProcessor> _entry : this._subSystemProcessorMap
				.entrySet()) {
			Class<?> _type = _entry.getKey();
			if (_type.isAssignableFrom(msgClass)) {
				return _entry.getValue();
			}
		}
		return null;
	}

	@Override
	public boolean isFull() {
		return false;
	}

}