package com.channelsoft.zhuaiwa.dal.model;

import java.lang.reflect.Field;
import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

import org.apache.cassandra.thrift.Column;
import org.apache.cassandra.thrift.ColumnOrSuperColumn;
import org.apache.cassandra.thrift.SuperColumn;
import org.apache.log4j.Logger;

import com.channelsoft.zhuaiwa.dal.cmd.Command;
import com.channelsoft.zhuaiwa.dal.exception.DALException;
import com.channelsoft.zhuaiwa.dal.model.Model.KeyType;
import com.channelsoft.zhuaiwa.dal.model.Model.ModelType;
import com.channelsoft.zhuaiwa.dal.model.Model.ValueType;
import com.channelsoft.zhuaiwa.dal.util.ReflectUtils;

public class ModelUtils {
    private static Logger logger = Logger.getLogger(ModelUtils.class);
    
	public static Model parser(Class<?> clazz) {
		return ModelParser.parser(clazz);
	}

	static public Object ctorFromColumn(Model model, Object key, List<Column> columns) throws DALException {
		assert(model.getModelType() == ModelType.COLUMN);
		
		try {
			Object obj = null;
			
			if (model.getKeyType() == KeyType.FIELD) {
				assert(model.getKeyField() != null);
				assert(model.getKeyField().getType().equals(key.getClass()));
				if(model.getKeyField().getAnnotation(com.channelsoft.zhuaiwa.dal.annotation.Column.class) == null) {
					if (obj == null)
						obj = model.getClazz().newInstance();
					ReflectUtils.setFieldValue(model.getKeyField(), obj, key);
				}
			}
			
			if (model.getValueType() == ValueType.MAP) {
				assert(model.getMapValueType().equals(Field.class));
				
				for (Column column : columns) {
					Object columnName = Command.ByteArray2Object(column.getName(), model.getMapKeyType());
					Field field = (Field)model.getMap().get(columnName);
					if (field == null) {
						logger.warn("Can't get field " + columnName + " while read " + model.getClazz().getSimpleName());
						continue;
					}
					
					Object columnValue = Command.ByteArray2Object(column.getValue(), field.getType());
					if (obj == null)
						obj = model.getClazz().newInstance();
					ReflectUtils.setFieldValue(field, obj, columnValue);
				}
			} else if (model.getValueType() == ValueType.LIST) {
				assert(model.getListField() != null);
				assert(model.getListField().getType().isAssignableFrom(Map.class));

				Field field = model.getListField();
				for (Column column : columns) {
					Object columnName = Command.ByteArray2Object(column.getName(), model.getListNameType());
					Object columnValue = Command.ByteArray2Object(column.getValue(), model.getListValueType());
					if (obj == null)
						obj = model.getClazz().newInstance();
					assignToMap(field, obj, columnName, columnValue);
				}
			} else {
				assert(false);
			}
			
			return obj;
		} catch (InstantiationException e) {
			throw new DALException(e);
		} catch (IllegalAccessException e) {
			throw new DALException(e);
		} catch (SecurityException e) {
			throw new DALException(e);
		} catch (Throwable e) {
			throw new DALException(e);
		}
	}

	static public Object ctorFromSuperColumn(Model model, Object key, List<SuperColumn> columns) throws DALException {
		assert(model.getModelType() == ModelType.SUPERCOLUMN);
		
		try {
			Object obj = null;
			
			if (model.getKeyType() == KeyType.FIELD) {
				assert(model.getKeyField() != null);
				assert(model.getKeyField().getType().equals(key.getClass()));
				if(model.getKeyField().getAnnotation(com.channelsoft.zhuaiwa.dal.annotation.Column.class) == null) {
					if (obj == null)
						obj = model.getClazz().newInstance();
					ReflectUtils.setFieldValue(model.getKeyField(), obj, key);
				}
			}
			
			for (SuperColumn sc : columns) {
				if (model.getValueType() == ValueType.MAP) {
					assert(model.getMapValueType().equals(Model.class));

					Object sn = Command.ByteArray2Object(sc.getName(), model.getMapKeyType());
					Model submodel = (Model)model.getMap().get(sn);
					if (submodel == null) {
						logger.warn("Can't get field model " + sc.getName() + " while read " + model.getClazz().getSimpleName());
						continue;
					}
					assert(submodel.getParentField() != null);
					Object subobj = ctorFromColumn(submodel, sn, sc.getColumns());
					
					if (obj == null)
						obj = model.getClazz().newInstance();
					ReflectUtils.setFieldValue(submodel.getParentField(), obj, subobj);
				} else if (model.getValueType() == ValueType.LIST) {
					assert(model.getListModel() != null);
					assert(model.getListField().getType().isAssignableFrom(List.class));
					
					Object sn = Command.ByteArray2Object(sc.getName(), model.getListNameType());
					Model submodel = model.getListModel();
					assert(submodel.getParentField() != null);
					Object subobj = ctorFromColumn(submodel, sn, sc.getColumns());
					
					if (obj == null)
						obj = model.getClazz().newInstance();
					assignToList(submodel.getParentField(), obj, subobj);
				} else {
					assert(false);
				}
			}
			
			return obj;
		} catch (InstantiationException e) {
			throw new DALException(e);
		} catch (IllegalAccessException e) {
			throw new DALException(e);
		} catch (Throwable e) {
			throw new DALException(e);
		}
	}
	
	static public Object ctorFromColumnOrSuperColumn(Model model, Object key, List<ColumnOrSuperColumn> columns) throws DALException {
		try {
			Object obj = null;
			
			List<Column> columnList = new ArrayList<Column>();
			List<SuperColumn> supercolumnList = new ArrayList<SuperColumn>();
			for (ColumnOrSuperColumn cosc : columns) {
				if (cosc.isSetSuper_column()) {
					assert(model.getModelType() == ModelType.SUPERCOLUMN);
					supercolumnList.add(cosc.getSuper_column());
				} else {
					assert(model.getModelType() == ModelType.COLUMN);
					columnList.add(cosc.getColumn());
				}
			}
			if (!columnList.isEmpty()) {
				assert(model.getModelType() == ModelType.COLUMN);
				obj = ctorFromColumn(model, key, columnList);
			}
			if (!supercolumnList.isEmpty()) {
				assert(model.getModelType() == ModelType.SUPERCOLUMN);
				obj = ctorFromSuperColumn(model, key, supercolumnList);
			}
			
			return obj;
		} catch (Throwable e) {
			throw new DALException(e);
		}
	}

	@SuppressWarnings("unchecked")
	static public void assignToList(Field field, Object instance, Object value) throws DALException {
		try {
			if (field.getType().isAssignableFrom(List.class) && !value.getClass().isAssignableFrom(List.class)) {
				if (ReflectUtils.getFieldValue(field, instance) == null) {
					List l = new ArrayList();
					l.add(value);
					ReflectUtils.setFieldValue(field, instance, l);
				} else {
					((List)ReflectUtils.getFieldValue(field, instance)).add(value);
				}
			} else {
				ReflectUtils.setFieldValue(field, instance, value);
			}
		} catch (IllegalArgumentException e) {
			throw new DALException(e);
		} catch (IllegalAccessException e) {
			throw new DALException(e);
		}
	}
	
	@SuppressWarnings("unchecked")
	static public void assignToMap(Field field, Object instance, Object key, Object value) throws DALException {
		try {
			if (field.getType().isAssignableFrom(Map.class)) {
				if (ReflectUtils.getFieldValue(field, instance) == null) {
					Map m = new LinkedHashMap();
					m.put(key, value);
					ReflectUtils.setFieldValue(field, instance, m);
				} else {
					((Map)ReflectUtils.getFieldValue(field, instance)).put(key, value);
				}
			}
		} catch (IllegalArgumentException e) {
			throw new DALException(e);
		} catch (IllegalAccessException e) {
			throw new DALException(e);
		}
	}
}
