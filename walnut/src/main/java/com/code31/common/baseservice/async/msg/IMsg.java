package com.code31.common.baseservice.async.msg;

public interface IMsg extends Runnable {
	/**
	 * 执行消息的处理
	 */
	public void execute();
}
