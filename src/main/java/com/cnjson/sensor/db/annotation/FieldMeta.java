package com.cnjson.sensor.db.annotation;

import java.lang.annotation.Documented;
import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

@Retention(RetentionPolicy.RUNTIME) // 注解会在class字节码文件中存在，在运行时可以通过反射获取到  
@Target({ ElementType.FIELD, ElementType.METHOD }) // 定义注解的作用目标**作用范围字段、枚举的常量/方法  
@Documented // 说明该注解将被包含在javadoc中
public @interface FieldMeta {
	
	/**
	 * 标识是否 是主键
	 * @return
	 */
	boolean isPrimay() default false;

	/**
	 * 数据表中对应的列名称
	 * @return
	 */
	String name() default "";

	/**
	 * 数据表中列的说明内容
	 * @return
	 */
	String description() default "";
}
