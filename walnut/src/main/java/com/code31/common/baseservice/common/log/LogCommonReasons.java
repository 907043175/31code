package com.code31.common.baseservice.common.log;

import java.lang.annotation.*;

public interface LogCommonReasons {

    @Documented
    @Retention(RetentionPolicy.RUNTIME)
    @Target({ ElementType.FIELD, ElementType.TYPE })
    public @interface ReasonDesc {
        /**
         * 原因的文字描述
         *
         * @return
         */
        String value();
    }

    @Documented
    @Retention(RetentionPolicy.RUNTIME)
    @Target({ ElementType.FIELD, ElementType.TYPE })
    public @interface LogDesc {
        /**
         * 日志描述
         *
         * @return
         */
        String desc();
    }

    /**
     * LogReason的通用接口
     */
    public static interface ILogReason {
        /**
         * 取得原因的序号
         *
         * @return
         */
        public int getReason();

        /**
         * 获取原因的文本
         *
         * @return
         */
        public String getReasonText();

        String getReasonType();
    }

    /**
     * 原因接口
     *
     * @param <E>
     *            枚举类型
     */
    public static interface IItemLogReason<E extends Enum<E>> extends
            ILogReason {
        public E getReasonEnum();
    }


}
