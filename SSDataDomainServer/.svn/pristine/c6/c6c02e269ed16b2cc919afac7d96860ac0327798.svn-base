package com.zhuaiwa.dd.domain;

import java.lang.reflect.Field;
import java.lang.reflect.Modifier;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;

import com.zhuaiwa.dd.util.ReflectUtils;

public abstract class BaseObject {
	public static StringBuilder appendTab(StringBuilder sb, int level) {
		for (int i = 0; i < level; i++) {
			sb.append("  ");
		}
		return sb;
	}
	
	@Override
	public String toString() {
		return toString(0);
	}
	
	@SuppressWarnings("unchecked")
	public String toString(int level) {
		try {
			StringBuilder sb = new StringBuilder();
			appendTab(sb, level).append("{\n");
			
			for (Field field : ReflectUtils.getFields(this.getClass())) {
				if (Modifier.isStatic(field.getModifiers()) && Modifier.isFinal(field.getModifiers()))
					continue;
				
				if (field.getType().isAssignableFrom(List.class)) {
					List<BaseObject> list = (List<BaseObject>)ReflectUtils.getFieldValue(field, this);
					
					appendTab(sb, level+1).append(field.getName()).append(": [\n");
					for (BaseObject o : list) {
						sb.append(o.toString(level+2)).append(",\n");
					}
					appendTab(sb, level+1).append("]\n");
				} else if (field.getType().isAssignableFrom(Map.class)) {
					Map<?, ?> map = (Map<?, ?>)ReflectUtils.getFieldValue(field, this);
					
					appendTab(sb, level+1).append(field.getName()).append(": [\n");
					for (Entry<?, ?> e : map.entrySet()) {
						appendTab(sb, level+2).append(e.getKey()).append(",\n");
					}
					appendTab(sb, level+1).append("]\n");
				} else {
					appendTab(sb, level+1).append(field.getName()).append(": ").append(ReflectUtils.getFieldValue(field, this)).append(";\n");
				}
			}
			
			appendTab(sb, level).append("}");
			return sb.toString();
		} catch (IllegalArgumentException e) {
			e.printStackTrace();
		} catch (IllegalAccessException e) {
			e.printStackTrace();
		}
		return super.toString();
	}
}
