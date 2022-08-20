package com.channelsoft.zhuaiwa.dal.dao.impl;

import java.lang.reflect.Field;
import java.lang.reflect.ParameterizedType;
import java.lang.reflect.Type;
import java.util.Arrays;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;

import org.apache.cassandra.thrift.Cassandra;

import com.channelsoft.zhuaiwa.dal.annotation.ColumnFamily;
import com.channelsoft.zhuaiwa.dal.cmd.CreateCommand;
import com.channelsoft.zhuaiwa.dal.cmd.ReadCommand;
import com.channelsoft.zhuaiwa.dal.dao.BaseDao;
import com.channelsoft.zhuaiwa.dal.domain.BaseObject;
import com.channelsoft.zhuaiwa.dal.domain.Contact;
import com.channelsoft.zhuaiwa.dal.exception.DALException;
import com.channelsoft.zhuaiwa.dal.model.Model;
import com.channelsoft.zhuaiwa.dal.model.ModelUtils;
import com.channelsoft.zhuaiwa.dal.model.Model.KeyType;
import com.channelsoft.zhuaiwa.dal.model.Model.ModelType;
import com.channelsoft.zhuaiwa.dal.model.Model.ValueType;
import com.channelsoft.zhuaiwa.dal.util.KeyUtils;
import com.channelsoft.zhuaiwa.dal.util.ReflectUtils;

public abstract class BaseDaoImpl<T extends BaseObject> implements BaseDao<T> {
	private Cassandra.Iface cassandra;

	public void setCassandra(Cassandra.Iface cassandra) {
		this.cassandra = cassandra;
	}

	public Cassandra.Iface getCassandra() {
		return cassandra;
	}
	
	protected Class<T> clazz;
	
	protected Class<T> getClazz() {
		return clazz;
	}

	protected String columnFamilyName;
	
	protected Class<?> getSuperClassGenricType() {
		Class<?> t = getClass();
		Class<?> s = t.getSuperclass();
		while (!s.equals(BaseDaoImpl.class)){
			t = s;
			s = s.getSuperclass();
		}
		
		ParameterizedType ptype = (ParameterizedType)(t.getGenericSuperclass());
		Type[] types = ptype.getActualTypeArguments();
		Class<?> clazz= (Class<?>)types[0];
		return clazz;
	}
	
	protected String getColumnFamilyName() {
		return columnFamilyName;
	}
	
	@SuppressWarnings("unchecked")
	public BaseDaoImpl() {
		this.clazz = (Class<T>)getSuperClassGenricType();
		ColumnFamily cf = this.clazz.getAnnotation(ColumnFamily.class);
		if (cf == null) {
			throw new NullPointerException(this.clazz.getSimpleName() + "必须要有@ColumnFamily。");
		}
		this.columnFamilyName = cf.value();
	}
	
	@Override
	public T get(String id) throws DALException {
		if (id == null)
			throw new NullPointerException();
		Map<String, T> objs = get(Arrays.asList(id));
		if (objs == null)
			return null;
		return objs.get(id);
	}
	
	@Override
	public void delete(String id) throws DALException {
		if (id == null)
			throw new NullPointerException();
		delete(Arrays.asList(id));
	}
	
	@Override
	public void delete(List<String> idlist) throws DALException {
		throw new UnsupportedOperationException();
	}
	
	@Override
	public Map<String, T> get(List<String> idlist)
			throws DALException {
		if (idlist == null)
			throw new NullPointerException();
		if (idlist.isEmpty())
			throw new DALException("参数中ID列表不能为空。");
		
		ReadCommand command =
			new ReadCommand(cassandra)
			.Object(this.clazz)
			.Select()
			.Where(KeyUtils.toByteBuffer(idlist));
		return KeyUtils.toStringMap(command.<T>execute());
	}
	
	@Override
	public String insert(T t) throws DALException {
		String key;
		try {
			Model m = ModelUtils.parser(this.clazz);
			
			key = null;
			Field keyfield = m.getKeyField();
			if (keyfield != null) {
				key = (String)ReflectUtils.getFieldValue(keyfield, t);
				if (key == null) {
					ReflectUtils.setFieldValue(keyfield, t, KeyUtils.toString(t.generateId()));
				}
			}
			persist(t);
			
			return key;
		} catch (IllegalArgumentException e) {
			throw new DALException(e);
		} catch (IllegalAccessException e) {
			throw new DALException(e);
		}
	}
	
	@Override
	public void persist(T obj) throws DALException {
		
		try {
			CreateCommand command =
				new CreateCommand(this.getCassandra())
				.Object(this.getColumnFamilyName());
			
			Model m = ModelUtils.parser(this.clazz);
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
				command.Where(KeyUtils.toByteBuffer(key));
				
				//value
				assert(m.getValueType() != null);
				if (m.getValueType() == ValueType.LIST) {
					assert(false);
				}
				if (m.getValueType() == ValueType.MAP) {
					Map<?,?> map = (Map<?,?>)m.getMap();
					for (Entry<?, ?> e : map.entrySet()) {
						command.Insert(KeyUtils.toByteBuffer((String)e.getKey()),
								ReflectUtils.getFieldValue((Field)e.getValue(), obj));
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
				command.Where(KeyUtils.toByteBuffer(key));
				
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
						
						// value
						if (submodel.getValueType() == ValueType.LIST) {
							assert(false);
						}
						if (submodel.getValueType() == ValueType.MAP) {
							Map<?,?> submap = (Map<?,?>)submodel.getMap();
							for (Entry<?, ?> se : submap.entrySet()) {
								command.Insert(
										KeyUtils.toByteBuffer(subkey),
										KeyUtils.toByteBuffer((String)se.getKey()),
										ReflectUtils.getFieldValue((Field)se.getValue(), subobj));
							}
						}
					}
				}
				if (m.getValueType() == ValueType.MAP) {
					Map<?,?> map = (Map<?,?>)m.getMap();
					for (Entry<?, ?> subobj : map.entrySet()) {
						// key
						String subkey = (String)subobj.getKey();
						
						// value
						Model submodel = (Model)m.getMap().get(subkey);
						if (submodel.getValueType() == ValueType.LIST) {
							assert(false);
						}
						if (submodel.getValueType() == ValueType.MAP) {
							Map<?,?> submap = (Map<?,?>)submodel.getMap();
							for (Entry<?, ?> se : submap.entrySet()) {
								command.Insert(
										KeyUtils.toByteBuffer(subkey),
										KeyUtils.toByteBuffer((String)se.getKey()),
										ReflectUtils.getFieldValue((Field)se.getValue(), subobj));
							}
						}
					}
				}
			}
			
			command.execute();
		
		} catch (IllegalAccessException e) {
			throw new DALException(e);
		}
	}
}
