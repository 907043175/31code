package com.code31.common.baseservice.async.msg;

public interface IMsgHandler {
	/**
	 * 处理消息
	 * 
	 * @param msg 需要被处理的消息
	 * 
	 */
	public void execute(Runnable msg);
	
	/**
	 * 取得此处理器可以处理的消息类型
	 * 
	 * @return
	 * 
	 */
	public short[] getTypes();
}