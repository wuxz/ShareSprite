package com.channelsoft.zhuaiwa.dal.model;

import java.lang.reflect.Field;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

import com.channelsoft.zhuaiwa.dal.annotation.Key;
import com.channelsoft.zhuaiwa.dal.model.Model.KeyType;
import com.channelsoft.zhuaiwa.dal.model.Model.ModelType;
import com.channelsoft.zhuaiwa.dal.model.Model.ValueType;

public class ModelParser {
	private static Map<Class<?>, Model> modelMap = new ConcurrentHashMap<Class<?>, Model>();
	@SuppressWarnings("unchecked")
	static Model parser(Class<?> clazz) {
		Model cachemodel = modelMap.get(clazz);
		if (cachemodel != null)
			return cachemodel;
		
		Model model = new Model();
		
//		// columnfamily
//		com.channelsoft.ss.annotation.ColumnFamily columnFamilyAnnotation = 
//			clazz.getAnnotation(com.channelsoft.ss.annotation.ColumnFamily.class);
//		if (columnFamilyAnnotation == null) {
//			return null;
//		}
//		if (columnFamilyAnnotation.value() != null && !columnFamilyAnnotation.value().isEmpty()) {
//			model.setColumnFamily(columnFamilyAnnotation.value());
//		} else {
//			model.setColumnFamily(clazz.getSimpleName());
//		}
		
		model.setClazz(clazz);
		
		for (Field field: clazz.getDeclaredFields()) {
			if (field.getAnnotation(Key.class) != null) {
				model.setKeyType(KeyType.FIELD);
				model.setKeyField(field);
			}
			
			com.channelsoft.zhuaiwa.dal.annotation.Column columnAnotation = null;
			if ((columnAnotation = field.getAnnotation(com.channelsoft.zhuaiwa.dal.annotation.Column.class)) != null) {
				if (model.getModelType() == null)
					model.setModelType(ModelType.COLUMN);
				assert(model.getModelType() == ModelType.COLUMN);
				
				if (model.getValueType() == null)
					model.setValueType(ValueType.MAP);
				assert(model.getValueType() == ValueType.MAP);
				
				if (model.getMapKeyType() == null)
					model.setMapKeyType(String.class);
				if (model.getMapValueType() == null)
					model.setMapValueType(Field.class);
				if (columnAnotation.name() != null && !columnAnotation.name().isEmpty()) {
					model.getMap().put(columnAnotation.name(), field);
				} else {
					model.getMap().put(field.getName(), field);
				}
				continue;
			}
			
			com.channelsoft.zhuaiwa.dal.annotation.SuperColumn superColumnAnotation = null;
			if ((superColumnAnotation = field.getAnnotation(com.channelsoft.zhuaiwa.dal.annotation.SuperColumn.class)) != null) {
				if (model.getModelType() == null)
					model.setModelType(ModelType.SUPERCOLUMN);
				assert(model.getModelType() == ModelType.SUPERCOLUMN);
				
				if (model.getValueType() == null)
					model.setValueType(ValueType.MAP);
				assert(model.getValueType() == ValueType.MAP);
				
				if (model.getMapKeyType() == null)
					model.setMapKeyType(String.class);
				if (model.getMapValueType() == null)
					model.setMapValueType(Model.class);
				
				Model fieldModel = parser(field.getType());
				fieldModel.setParentField(field);
				if (superColumnAnotation.name() != null && !superColumnAnotation.name().isEmpty()) {
					model.getMap().put(superColumnAnotation.name(), fieldModel);
				} else {
					model.getMap().put(field.getName(), fieldModel);
				}
				continue;
			}
			
			com.channelsoft.zhuaiwa.dal.annotation.Columns columnsAnotation = null;
			if ((columnsAnotation = field.getAnnotation(com.channelsoft.zhuaiwa.dal.annotation.Columns.class)) != null) {
				assert(model.getModelType() == null);
				assert(model.getValueType() == null);
				assert(field.getType().isAssignableFrom(Map.class));
				
				model.setModelType(ModelType.COLUMN);
				model.setValueType(ValueType.LIST);
				model.setListNameType(columnsAnotation.nameType());
				model.setListValueType(columnsAnotation.valueType());
				model.setListField(field);
				continue;
			}
			
			com.channelsoft.zhuaiwa.dal.annotation.SuperColumns supercolumnsAnotation = null;
			if ((supercolumnsAnotation = field.getAnnotation(com.channelsoft.zhuaiwa.dal.annotation.SuperColumns.class)) != null) {
				assert(model.getModelType() == null);
				assert(model.getValueType() == null);
				assert(field.getType().isAssignableFrom(List.class));
				
				model.setModelType(ModelType.SUPERCOLUMN);
				model.setValueType(ValueType.LIST);
				model.setListNameType(supercolumnsAnotation.nameType());
				model.setListValueType(Model.class);
				model.setListField(field);
				
				Model listModel = parser(supercolumnsAnotation.valueType());
				listModel.setParentField(field);
				model.setListModel(listModel);
				continue;
			}
		}
		
		if (cachemodel == null)
			modelMap.put(clazz, model);
		return model;
	}
}
