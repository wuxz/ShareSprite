package com.channelsoft.zhuaiwa.dal.annotation;

import static java.lang.annotation.ElementType.FIELD;
import static java.lang.annotation.ElementType.TYPE;
import static java.lang.annotation.RetentionPolicy.RUNTIME;

import java.lang.annotation.Retention;
import java.lang.annotation.Target;

@Target({FIELD,TYPE})
@Retention(RUNTIME)
public @interface Key {
	String value() default "";
}
