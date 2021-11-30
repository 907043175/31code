package com.code31.common.baseservice.db.annotation;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;


@Target(ElementType.METHOD)
@Retention(RetentionPolicy.RUNTIME)
public @interface Sql {
    /**
     * 查询的类型
     *
     * @return
     */
    SqlType type() default SqlType.QUERY_ENTITY;

    /**
     * 查询的列,当{@link #type()}的类型为{@link SqlType#QUERY_COLUMNS}时使用该属性指定列
     *
     * @return
     */
    String columns() default "";
    /**
     * condition  查询的条件
     *
     * @return
     */
    String condition();
}
