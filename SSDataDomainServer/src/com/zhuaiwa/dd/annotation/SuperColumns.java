package com.zhuaiwa.dd.annotation;

import static java.lang.annotation.ElementType.FIELD;
import static java.lang.annotation.RetentionPolicy.RUNTIME;

import java.lang.annotation.Retention;
import java.lang.annotation.Target;

@Target({FIELD})
@Retention(RUNTIME)
public @interface SuperColumns {
	Class<?> nameType() default String.class;
	Class<?> valueType();
	byte[] maxName() default {};
	byte[] minName() default {};
	boolean descending() default false;
}
