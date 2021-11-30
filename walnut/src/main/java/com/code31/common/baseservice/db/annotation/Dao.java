package com.code31.common.baseservice.db.annotation;


import com.code31.common.baseservice.db.orm.IEntity;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

@Target(ElementType.TYPE)
@Retention(RetentionPolicy.RUNTIME)
public @interface Dao {
    /**
     * Dao所操作的对象类型
     *
     * @return
     */
    Class<? extends IEntity> entityClass();

    Class implClass()default void.class;
}
