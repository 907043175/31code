package com.code31.common.baseservice.db.transactional;


import org.apache.ibatis.session.ExecutorType;
import org.apache.ibatis.session.TransactionIsolationLevel;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;


@Retention(RetentionPolicy.RUNTIME)
@Target({ ElementType.METHOD, ElementType.TYPE })
public @interface Transactional {


    ExecutorType executorType() default ExecutorType.SIMPLE;

    TransactionIsolationLevel isolationLevel() default TransactionIsolationLevel.REPEATABLE_READ;

    boolean force() default false;

}
