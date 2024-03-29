package com.code31.common.baseservice.common.annotation;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

@Retention(RetentionPolicy.RUNTIME)  
@Target(ElementType.METHOD)  
public @interface Listener {
	int[] value();
	//子类型 下属多个属性 只能选一种
	String[] subStr() default {};	//字符类型子事件
	int[] subInt() default {};		//Int类型子事件
	long[] subLong() default {};	//Long类型子事件
}