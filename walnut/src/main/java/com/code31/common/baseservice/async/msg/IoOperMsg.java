package com.code31.common.baseservice.async.msg;

public abstract class IoOperMsg  implements IMsg {
	
	
	/**
	 * 运行消息
	 * 
	 */
	@Override
	public void run() {
		this.execute();
	}
}