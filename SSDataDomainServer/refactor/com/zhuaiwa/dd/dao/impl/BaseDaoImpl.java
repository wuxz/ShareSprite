package com.zhuaiwa.dd.dao.impl;

import java.lang.reflect.Field;
import java.lang.reflect.ParameterizedType;
import java.lang.reflect.Type;
import java.util.Arrays;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;

import org.apache.cassandra.thrift.Cassandra;
import org.apache.cassandra.utils.GuidGenerator;

import com.zhuaiwa.dd.annotation.ColumnFamily;
import com.zhuaiwa.dd.cmd.CreateCommand;
import com.zhuaiwa.dd.cmd.DeleteCommand;
import com.zhuaiwa.dd.cmd.IterateCommand;
import com.zhuaiwa.dd.cmd.ReadCommand;
import com.zhuaiwa.dd.dao.BaseDao;
import com.zhuaiwa.dd.domain.BaseObject;
import com.zhuaiwa.dd.exception.DALException;
import com.zhuaiwa.dd.model.Model;
import com.zhuaiwa.dd.model.ModelUtils;
import com.zhuaiwa.dd.model.Model.KeyType;
import com.zhuaiwa.dd.model.Model.ModelType;
import com.zhuaiwa.dd.model.Model.ValueType;
import com.zhuaiwa.dd.util.ReflectUtils;

public class BaseDaoImpl<T extends BaseObject> implements BaseDao<T> {
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
//		Class<?> s = t.getSuperclass();
//		while (!s.equals(BaseDaoImpl.class)){
//			t = s;
//			s = s.getSuperclass();
//		}
		
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

	public String genGuid() {
		byte[] array = GuidGenerator.guidAsBytes().array();
        
        StringBuilder sb = new StringBuilder();
        for (int j = 0; j < array.length; ++j) {
            int b = array[j] & 0xFF;
            if (b < 0x10) sb.append('0');
            sb.append(Integer.toHexString(b));
        }
        return sb.toString();
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
		DeleteCommand command =
			new DeleteCommand(cassandra)
			.Object(this.getColumnFamilyName());
//			.Where(idlist.toArray(new String[idlist.size()]));
		for (String id : idlist)
			command.DeleteKey(id);
		command.execute();
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
			.Where(idlist.toArray(new String[idlist.size()]));
		return command.<T>execute();
	}
	
	@Override
	public Iterator<T> getAll() {
		return getAll(100);
	}

	@Override
	@SuppressWarnings("unchecked")
	public Iterator<T> getAll(int pageCount) {
		IterateCommand command = 
			new IterateCommand(cassandra)
			.Object(this.clazz)
			.Select()
			.Where("", pageCount);
		return (Iterator<T>)command.iterator();
	}
	
	@Override
	public void insert(T obj) throws DALException {
		
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
				if (key == null)
					throw new NullPointerException("此实体不存在key。");
				command.Where(key);
				
				//value
				assert(m.getValueType() != null);
				if (m.getValueType() == ValueType.LIST) {
					Map<?, ?> map = (Map<?, ?>)ReflectUtils.getFieldValue(m.getListField(), obj);
					for (Entry<?,?> e : map.entrySet()) {
						if (e.getKey() != null && e.getValue() != null) {
							command.Insert((String)e.getKey(), e.getValue());
						}
					}
				}
				if (m.getValueType() == ValueType.MAP) {
					Map<?,?> map = (Map<?,?>)m.getMap();
					for (Entry<?, ?> e : map.entrySet()) {
						Object v = ReflectUtils.getFieldValue((Field)e.getValue(), obj);
						if (v != null) {
							command.Insert((String)e.getKey(), v);
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
				command.Where(key);
				
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
						
						// value
						if (submodel.getValueType() == ValueType.LIST) {
							assert(false);
						}
						if (submodel.getValueType() == ValueType.MAP) {
							Map<?,?> submap = (Map<?,?>)submodel.getMap();
							for (Entry<?, ?> se : submap.entrySet()) {
								Object v = ReflectUtils.getFieldValue((Field)se.getValue(), subobj);
								if (v != null) {
									command.Insert(subkey,(String)se.getKey(),v);
								}
							}
						}
					}
				}
				if (m.getValueType() == ValueType.MAP) {
					Map<?,?> map = (Map<?,?>)m.getMap();
					for (Entry<?, ?> subobj : map.entrySet()) {
						// key
						String subkey = (String)subobj.getKey();
						if (subkey == null)
							throw new NullPointerException("此实体不存在key。");
						
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
									command.Insert(
											subkey,
											(String)se.getKey(),
											v);
								}
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

	@Override
	public void insert(List<T> objlist) throws DALException {
		throw new UnsupportedOperationException();
	}
}
