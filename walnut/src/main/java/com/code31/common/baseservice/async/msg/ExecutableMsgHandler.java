package com.code31.common.baseservice.async.msg;

import com.code31.common.baseservice.utils.ErrorsUtil;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * 可自执行的消息处理器
 * 
 * 
 */
public class ExecutableMsgHandler implements IMsgHandler {
	
	private static final Logger logger = LoggerFactory.getLogger("app.msg");
	
	public static final String MSG_PRO_ERR_EXP = "MSG.PRO.ERR.EXP";
	
	@Override
	public void execute(Runnable msg) {
		try {
			// 运行消息
			msg.run();
		} catch (Exception ex) {
			// 异常信息
			String exMsg = ErrorsUtil.errorAllThrowableStackMessage(
				MSG_PRO_ERR_EXP, ex);

			// 记录异常信息
			logger.error(
				MSG_PRO_ERR_EXP
				+ "\n | Clazz : " + msg.getClass().getSimpleName()
				+ "\n | Message : " + msg.toString()
				+ "\n | Exception : \n" + exMsg);
		}
	}

	@Override
	public short[] getTypes() {
		return null;
	}
}
