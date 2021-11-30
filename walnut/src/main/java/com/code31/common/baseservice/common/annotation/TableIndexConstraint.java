package com.code31.common.baseservice.common.annotation;

import com.google.inject.BindingAnnotation;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

@Retention(RetentionPolicy.RUNTIME)
@Target({ ElementType.FIELD, ElementType.PARAMETER})
@BindingAnnotation
public @interface TableIndexConstraint {

    String[] columnNames();

    String name();

    boolean unique() default false;

    String desc() default "";
}
