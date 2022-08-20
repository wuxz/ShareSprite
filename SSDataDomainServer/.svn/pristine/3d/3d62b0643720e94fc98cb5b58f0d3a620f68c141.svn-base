package com.zhuaiwa.dd.model;

import java.lang.reflect.Field;
import java.nio.ByteBuffer;
import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;

import me.prettyprint.cassandra.serializers.StringSerializer;
import me.prettyprint.cassandra.serializers.TypeInferringSerializer;
import me.prettyprint.hector.api.beans.HColumn;
import me.prettyprint.hector.api.beans.HSuperColumn;
import me.prettyprint.hector.api.factory.HFactory;
import me.prettyprint.hector.api.mutation.Mutator;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.zhuaiwa.dd.annotation.ColumnFamily;
import com.zhuaiwa.dd.cmd.Command;
import com.zhuaiwa.dd.exception.DALException;
import com.zhuaiwa.dd.model.Model.KeyType;
import com.zhuaiwa.dd.model.Model.ModelType;
import com.zhuaiwa.dd.model.Model.ValueType;
import com.zhuaiwa.dd.util.ReflectUtils;

public class ModelUtils {
    private static Logger logger = LoggerFactory.getLogger(ModelUtils.class);
    
	public static Model parser(Class<?> clazz) {
		return ModelParser.parser(clazz);
	}

	static public Object ctorFromColumn(Model model, Object key, List<HColumn<ByteBuffer, ByteBuffer>> columns) {
		assert(model.getModelType() == ModelType.COLUMN);
		
		try {
			Object obj = null;
			
			if (model.getKeyType() == KeyType.FIELD) {
				assert(model.getKeyField() != null);
				assert(model.getKeyField().getType().equals(key.getClass()));
				if(model.getKeyField().getAnnotation(com.zhuaiwa.dd.annotation.Column.class) == null) {
					if (obj == null)
						obj = model.getClazz().newInstance();
					ReflectUtils.setFieldValue(model.getKeyField(), obj, key);
				}
			}
			
			if (model.getValueType() == ValueType.MAP) {
				assert(model.getMapValueType().equals(Field.class));
				
				for (HColumn<ByteBuffer, ByteBuffer> column : columns) {
					Object columnName;
					try {
						columnName = Command.ByteArray2Object(column.getName(), model.getMapKeyType());
					} catch (Exception e) {
						logger.warn("Can't get field name " + column.getName() + " while read " + model.getClazz().getSimpleName(), e);
						continue;
					}
					Field field = (Field)model.getMap().get(columnName);
					if (field == null) {
						logger.warn("Can't get field " + columnName + " while read " + model.getClazz().getSimpleName());
						continue;
					}
					
					Object columnValue;
					try {
						columnValue = Command.ByteArray2Object(column.getValue(), field.getType());
					} catch (Exception e) {
						logger.warn("Can't get field value " + columnName + " while read " + model.getClazz().getSimpleName(), e);
						continue;
					}
					if (obj == null)
						obj = model.getClazz().newInstance();
					ReflectUtils.setFieldValue(field, obj, columnValue);
				}
			} else if (model.getValueType() == ValueType.LIST) {
				assert(model.getListField() != null);
				assert(model.getListField().getType().isAssignableFrom(Map.class));

				Field field = model.getListField();
				for (HColumn<ByteBuffer, ByteBuffer> column : columns) {
					Object columnName;
					try {
						columnName = Command.ByteArray2Object(column.getName(), model.getListNameType());
					} catch (Exception e) {
						logger.warn("Can't get field name " + column.getName() + " while read " + model.getClazz().getSimpleName(), e);
						continue;
					}
					Object columnValue;
					try {
						columnValue = Command.ByteArray2Object(column.getValue(), model.getListValueType());
					} catch (Exception e) {
						logger.warn("Can't get field value " + columnName + " while read " + model.getClazz().getSimpleName(), e);
						continue;
					}
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

	static public Object ctorFromSuperColumn(Model model, Object key, List<HSuperColumn<ByteBuffer, ByteBuffer, ByteBuffer>> columns) {
		assert(model.getModelType() == ModelType.SUPERCOLUMN);
		
		try {
			Object obj = null;
			
			if (model.getKeyType() == KeyType.FIELD) {
				assert(model.getKeyField() != null);
				assert(model.getKeyField().getType().equals(key.getClass()));
				if(model.getKeyField().getAnnotation(com.zhuaiwa.dd.annotation.Column.class) == null) {
					if (obj == null)
						obj = model.getClazz().newInstance();
					ReflectUtils.setFieldValue(model.getKeyField(), obj, key);
				}
			}
			
			for (HSuperColumn<ByteBuffer, ByteBuffer, ByteBuffer> sc : columns) {
				if (model.getValueType() == ValueType.MAP) {
					assert(model.getMapValueType().equals(Model.class));

					Object sn;
					try {
						sn = Command.ByteArray2Object(sc.getName(), model.getMapKeyType());
					} catch (Exception e) {
						logger.warn("Can't get field name " + sc.getName() + " while read " + model.getClazz().getSimpleName(), e);
						continue;
					}
					Model submodel = (Model)model.getMap().get(sn);
					if (submodel == null) {
						logger.warn("Can't get field model " + sc.getName() + " while read " + model.getClazz().getSimpleName());
						continue;
					}
					assert(submodel.getParentField() != null);
					Object subobj = ctorFromColumn(submodel, sn, sc.getColumns());
					if (subobj == null) {
						logger.warn("Can't construct object from " + sn + "'s columns");
						continue;
					}
					
					if (obj == null)
						obj = model.getClazz().newInstance();
					ReflectUtils.setFieldValue(submodel.getParentField(), obj, subobj);
				} else if (model.getValueType() == ValueType.LIST) {
					assert(model.getListModel() != null);
					assert(model.getListField().getType().isAssignableFrom(List.class));
					
					Object sn = null;
					try {
						sn = Command.ByteArray2Object(sc.getName(), model.getListNameType());
					} catch (Exception e) {
						logger.warn("Can't get field name " + sc.getName() + " while read " + model.getClazz().getSimpleName(), e);
						continue;
					}
					Model submodel = model.getListModel();
					assert(submodel.getParentField() != null);
					Object subobj = ctorFromColumn(submodel, sn, sc.getColumns());
					if (subobj == null) {
						logger.warn("Can't construct object from " + sn + "'s columns");
						continue;
					}
					
					if (obj == null)
						obj = model.getClazz().newInstance();
					assignToList(model.getListField(), obj, subobj);
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
	
	public static void mutatePojo(Model m, Mutator<String> mutator, Object obj) {
        try {
            ColumnFamily cf = m.getClazz().getAnnotation(ColumnFamily.class);
            if (cf == null) {
                throw new NullPointerException(m.getClazz().getSimpleName() + "必须要有@ColumnFamily。");
            }
            String columnFamilyName = cf.value();
            
            if (m.getModelType() == ModelType.COLUMN) {
                
                //key
                String key = null;
                assert(m.getKeyType() != null);
                if (m.getKeyType() == KeyType.CONSTANT) {
                    key = m.getKeyConstant();
                }
                if (m.getKeyType() == KeyType.FIELD) {
                    key = (String) ReflectUtils.getFieldValue(m.getKeyField(), obj);
                }
                if (key == null)
                    throw new NullPointerException("此实体不存在key。");
                
                //value
                assert(m.getValueType() != null);
                if (m.getValueType() == ValueType.LIST) {
                    Map<?, ?> map = (Map<?, ?>)ReflectUtils.getFieldValue(m.getListField(), obj);
                    for (Entry<?,?> e : map.entrySet()) {
                        if (e.getKey() != null && e.getValue() != null) {
                            mutator.addInsertion(key, columnFamilyName, 
                                    HFactory.createColumn((String)e.getKey(), e.getValue(), StringSerializer.get(), TypeInferringSerializer.get()));
                        }
                    }
                }
                if (m.getValueType() == ValueType.MAP) {
                    Map<?,?> map = (Map<?,?>)m.getMap();
                    for (Entry<?, ?> e : map.entrySet()) {
                        Object v = ReflectUtils.getFieldValue((Field)e.getValue(), obj);
                        if (v != null) {
                            mutator.addInsertion(key, columnFamilyName, 
                                    HFactory.createColumn((String)e.getKey(), v, StringSerializer.get(), TypeInferringSerializer.get()));
                        }
                    }
                }
            }
            if (m.getModelType() == ModelType.SUPERCOLUMN) {
                
                // key
                String key = null;
                assert(m.getKeyType() != null);
                if (m.getKeyType() == KeyType.CONSTANT) {
                    key = m.getKeyConstant();
                }
                if (m.getKeyType() == KeyType.FIELD) {
                    key = (String) ReflectUtils.getFieldValue(m.getKeyField(), obj);
                }
                if (key == null)
                    throw new NullPointerException("此实体不存在key。");
                
                // value
                assert(m.getValueType() != null);
                if (m.getValueType() == ValueType.LIST) {
                    
                    List<?> list = (List<?>)ReflectUtils.getFieldValue(m.getListField(), obj);
                    for (Object subobj : list) {
                        
                        Model submodel = m.getListModel();
                        
                        // key
                        String subkey = null;
                        assert(submodel.getKeyType() != null);
                        if (submodel.getKeyType() == KeyType.CONSTANT) {
                            subkey = submodel.getKeyConstant();
                        }
                        if (submodel.getKeyType() == KeyType.FIELD) {
                            subkey = (String) ReflectUtils.getFieldValue(submodel.getKeyField(), subobj);
                        }
                        if (subkey == null)
                            throw new NullPointerException("此实体不存在key。");
                        
                        List<HColumn<String, Object>> subcolumns = new ArrayList<HColumn<String, Object>>();
                        
                        // value
                        if (submodel.getValueType() == ValueType.LIST) {
                            assert(false);
                        }
                        if (submodel.getValueType() == ValueType.MAP) {
                            Map<?,?> submap = (Map<?,?>)submodel.getMap();
                            for (Entry<?, ?> se : submap.entrySet()) {
                                Object v = ReflectUtils.getFieldValue((Field)se.getValue(), subobj);
                                if (v != null) {
                                    subcolumns.add(HFactory.createColumn((String)se.getKey(), v, StringSerializer.get(), TypeInferringSerializer.get()));
                                }
                            }
                        }
                        
                        mutator.addInsertion(key, columnFamilyName, HFactory.createSuperColumn(subkey, subcolumns, StringSerializer.get(), StringSerializer.get(), TypeInferringSerializer.get()));
                    }
                }
                if (m.getValueType() == ValueType.MAP) {
                    Map<?,?> map = (Map<?,?>)m.getMap();
                    for (Entry<?, ?> subobj : map.entrySet()) {
                        // key
                        String subkey = (String)subobj.getKey();
                        if (subkey == null)
                            throw new NullPointerException("此实体不存在key。");
                        
                        List<HColumn<String, Object>> subcolumns = new ArrayList<HColumn<String, Object>>();
                        
                        // value
                        Model submodel = (Model)m.getMap().get(subkey);
                        if (submodel.getValueType() == ValueType.LIST) {
                            assert(false);
                        }
                        if (submodel.getValueType() == ValueType.MAP) {
                            Map<?,?> submap = (Map<?,?>)submodel.getMap();
                            for (Entry<?, ?> se : submap.entrySet()) {
                                Object v = ReflectUtils.getFieldValue((Field)se.getValue(), subobj);
                                if (v != null) {
                                    subcolumns.add(HFactory.createColumn((String)se.getKey(), v, StringSerializer.get(), TypeInferringSerializer.get()));
                                }
                            }
                        }
                        mutator.addInsertion(key, columnFamilyName, HFactory.createSuperColumn(subkey, subcolumns, StringSerializer.get(), StringSerializer.get(), TypeInferringSerializer.get()));
                    }
                }
            }
        
        } catch (IllegalAccessException e) {
            throw new RuntimeException(e);
        }
    }
}
