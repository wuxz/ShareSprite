package com.zhuaiwa.dd.util;

import java.lang.reflect.Field;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.List;

/**
 * 反射工具类
 * 
 * @author yaosw
 *
 */
public class ReflectUtils {
	public static List<Field> getFields(Class<?> clazz) {
		List<Field> fields = new ArrayList<Field>();
		do {
			Field[] f = clazz.getDeclaredFields();
			for (Field i:f) {
				fields.add(i);
			}
			clazz = clazz.getSuperclass();
		} while (clazz != null);
		
		return fields;
	}
	
	public static Object getFieldValue(Class<?> clazz, String fieldName, Object instance) throws IllegalArgumentException, IllegalAccessException {
		Field field = getField(clazz, fieldName);
		if (field != null) {
			return getFieldValue(field, instance);
		}
		return null;
	}
	
	public static Object getFieldValue(Field field, Object instance) throws IllegalArgumentException, IllegalAccessException {
		Object fieldValue = null;
		
		String fieldName = field.getName();
		Method m = null;
		try {
			String methodName = "get" + fieldName.substring(0,1).toUpperCase() + fieldName.substring(1);
			m = instance.getClass().getMethod(methodName);
		} catch (NoSuchMethodException e) {
			m = null;
		} catch (SecurityException e) {
			m = null;
		}
		if (m != null) {
			try {
				fieldValue = m.invoke(instance);
				return fieldValue;
			} catch (InvocationTargetException e) {
			}
		}

		if (!field.isAccessible()) {
			try {
				field.setAccessible(true);
				return field.get(instance);
			} catch (SecurityException e) {
			}
		} else {
			return field.get(instance);
		}
		return null;
	}
	
	public static void setFieldValue(Field field, Object instance, Object fieldValue) throws IllegalArgumentException, IllegalAccessException {
		String fieldName = field.getName();
		Method m = null;
		try {
			m = field.getDeclaringClass().getMethod(
					"set" + fieldName.substring(0,1).toUpperCase() + fieldName.substring(1));
		} catch (NoSuchMethodException e) {
			m = null;
		} catch (SecurityException e) {
			m = null;
		}
		if (m != null) {
			try {
				m.invoke(instance, fieldValue);
				return;
			} catch (InvocationTargetException e) {
			}
		}
		if (!field.isAccessible()) {
			try {
				field.setAccessible(true);
				field.set(instance, fieldValue);
				return;
			} catch (SecurityException e) {
			}
		} else {
			field.set(instance, fieldValue);
			return;
		}
	}
	
	public static Field getField(Class<?> clazz, String fieldName) {
		do {
			Field f = null;
			try {
				f = clazz.getDeclaredField(fieldName);
			} catch (SecurityException e) {
				f = null;
			} catch (NoSuchFieldException e) {
				f = null;
			}
			if (f != null)
				return f;
			clazz = clazz.getSuperclass();
		} while (clazz != null);
		
		return null;
	}
}
