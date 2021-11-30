package com.code31.common.baseservice.common;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;


public final class CoreLoggers {

    public static final Logger testLogger = getLogger("app.test");

    public static final Logger errorLogger = getLogger("app.error");

    /**
     * 类默认构造器
     */
    private CoreLoggers() {
    }

    /**
     * 获取日志对象
     *
     * @param name
     * @return
     */
    private static Logger getLogger(String name) {
        // 断言参数不为空
        // 获取日志
        return LoggerFactory.getLogger(name);
    }
}
