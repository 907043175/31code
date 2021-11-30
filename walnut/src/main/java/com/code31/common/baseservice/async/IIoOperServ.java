package com.code31.common.baseservice.async;

import java.util.HashMap;


public interface IIoOperServ {
	/**
	 * 执行异步操作
	 * 
	 * @param op
	 * @return
	 * 
	 */
	public void execute(IIoOper op);

	/**
	 * 停止服务
	 * 
	 */
	public void stop();
	
	/**
	 * 获取统计结果
	 * @return
	 */
	public HashMap<String,Object> getStatistics();
	
	
	/**
	 * 未完成的任务大概需要多久能执行完成(秒)
	 * @return
	 */
	public long getQueueTaskWillFinishedTime();
	

}