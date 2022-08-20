package com.zhuaiwa.dd.dao;

import java.lang.reflect.ParameterizedType;
import java.lang.reflect.Type;
import java.nio.ByteBuffer;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

import me.prettyprint.cassandra.serializers.ByteBufferSerializer;
import me.prettyprint.cassandra.serializers.IntegerSerializer;
import me.prettyprint.cassandra.serializers.LongSerializer;
import me.prettyprint.cassandra.serializers.StringSerializer;
import me.prettyprint.hector.api.Keyspace;
import me.prettyprint.hector.api.beans.ColumnSlice;
import me.prettyprint.hector.api.beans.HColumn;
import me.prettyprint.hector.api.beans.Row;
import me.prettyprint.hector.api.beans.Rows;
import me.prettyprint.hector.api.beans.SuperRow;
import me.prettyprint.hector.api.beans.SuperRows;
import me.prettyprint.hector.api.beans.SuperSlice;
import me.prettyprint.hector.api.factory.HFactory;
import me.prettyprint.hector.api.mutation.Mutator;
import me.prettyprint.hector.api.query.MultigetSliceQuery;
import me.prettyprint.hector.api.query.MultigetSuperSliceQuery;
import me.prettyprint.hector.api.query.QueryResult;
import me.prettyprint.hector.api.query.SliceQuery;
import me.prettyprint.hector.api.query.SuperSliceQuery;

import com.zhuaiwa.dd.annotation.ColumnFamily;
import com.zhuaiwa.dd.domain.BaseObject;
import com.zhuaiwa.dd.hector.RowIterator;
import com.zhuaiwa.dd.hector.SuperRowIterator;
import com.zhuaiwa.dd.model.ModelUtils;

public abstract class AbstractDao<T extends BaseObject> implements BaseDao<T> {
    protected static final ThreadLocal<Mutator<String>> currentThreadMutator = new ThreadLocal<Mutator<String>>();
    
    public static void beginBatch(Keyspace keyspace) {
        assert(currentThreadMutator.get() == null);
        currentThreadMutator.set(HFactory.createMutator(keyspace, StringSerializer.get()));
    }
    public static void cancelBatch() {
        currentThreadMutator.remove();
    }
    public static void endBatch() {
        Mutator<String> mutator = currentThreadMutator.get();
        if (mutator == null)
            throw new RuntimeException("You didn't invoke beginBatch() before endBatch().");
        mutator.execute();
        currentThreadMutator.remove();
    }
    
    protected Keyspace keyspace;
    protected Class<T> clazz;
    protected String columnFamilyName;

    public Keyspace getKeyspace() {
        return keyspace;
    }
    public void setKeyspace(Keyspace keyspace) {
        this.keyspace = keyspace;
    }
    protected Class<T> getClazz() {
        return clazz;
    }
    protected String getColumnFamilyName() {
        return columnFamilyName;
    }
    
    protected Class<?> getSuperClassGenricType() {
        Class<?> t = getClass();
//      Class<?> s = t.getSuperclass();
//      while (!s.equals(AbstractDao.class)){
//          t = s;
//          s = s.getSuperclass();
//      }
        
        ParameterizedType ptype = (ParameterizedType)(t.getGenericSuperclass());
        Type[] types = ptype.getActualTypeArguments();
        Class<?> clazz= (Class<?>)types[0];
        return clazz;
    }
    
    @SuppressWarnings("unchecked")
    public AbstractDao() {
        this.clazz = (Class<T>)getSuperClassGenricType();
        ColumnFamily cf = this.clazz.getAnnotation(ColumnFamily.class);
        if (cf == null) {
            throw new NullPointerException(this.clazz.getSimpleName() + "必须要有@ColumnFamily。");
        }
        this.columnFamilyName = cf.value();
    }
    
    public static HColumn<String, String> createStringColumn(String name, String value) {
        return HFactory.createStringColumn(name, value);
    }
    public static HColumn<String, Long> createLongColumn(String name, Long value) {
        return HFactory.createColumn(name, value, StringSerializer.get(), LongSerializer.get());
    }
    public static HColumn<String, Integer> createIntegerColumn(String name, Integer value) {
        return HFactory.createColumn(name, value, StringSerializer.get(), IntegerSerializer.get());
    }
    
    protected void insert(Mutator<String> mutator, T obj) {
        ModelUtils.mutatePojo(ModelUtils.parser(getClazz()), mutator, obj);
    }

    protected void delete(Mutator<String> mutator, String id) {
        mutator.addDeletion(id, this.columnFamilyName);
    }
    
    @Override
    public void insert(T obj) {
        Mutator<String> mutator = currentThreadMutator.get();
        boolean batch = (mutator != null);
        if (mutator == null) {
            mutator = HFactory.createMutator(keyspace, StringSerializer.get());
        }
        insert(mutator, obj);
        if (!batch)
            mutator.execute();
    }
    @Override
    public void delete(String id) {
        Mutator<String> mutator = currentThreadMutator.get();
        boolean batch = (mutator != null);
        if (mutator == null) {
            mutator = HFactory.createMutator(keyspace, StringSerializer.get());
        }
        delete(mutator, id);
        if (!batch)
            mutator.execute();
    }
    @SuppressWarnings("unchecked")
    @Override
    public T get(String id) {
        if (getClazz().getAnnotation(ColumnFamily.class).isSuper()) {
            SuperSliceQuery<String,ByteBuffer,ByteBuffer,ByteBuffer> query = HFactory.createSuperSliceQuery(keyspace, StringSerializer.get(), ByteBufferSerializer.get(), ByteBufferSerializer.get(), ByteBufferSerializer.get());
            query.setKey(id);
            query.setColumnFamily(this.columnFamilyName);
            query.setRange(null, null, false, Integer.MAX_VALUE);
            QueryResult<SuperSlice<ByteBuffer, ByteBuffer, ByteBuffer>> result = query.execute();
            return (T)ModelUtils.ctorFromSuperColumn(ModelUtils.parser(getClazz()), id, result.get().getSuperColumns());
        } else {
            SliceQuery<String,ByteBuffer,ByteBuffer> query = HFactory.createSliceQuery(keyspace, StringSerializer.get(), ByteBufferSerializer.get(), ByteBufferSerializer.get());
            query.setKey(id);
            query.setColumnFamily(this.columnFamilyName);
            query.setRange(null, null, false, Integer.MAX_VALUE);
            QueryResult<ColumnSlice<ByteBuffer, ByteBuffer>> result = query.execute();
            return (T)ModelUtils.ctorFromColumn(ModelUtils.parser(getClazz()), id, result.get().getColumns());
        }
    }
    @Override
    public void insert(List<T> objlist) {
        Mutator<String> mutator = currentThreadMutator.get();
        boolean batch = (mutator != null);
        if (mutator == null) {
            mutator = HFactory.createMutator(keyspace, StringSerializer.get());
        }
        for (T obj : objlist) {
            insert(mutator, obj);
        }
        if (!batch)
            mutator.execute();
    }
    @Override
    public void delete(List<String> idlist) {
        Mutator<String> mutator = HFactory.createMutator(keyspace, StringSerializer.get());
        for (String id : idlist) {
            delete(mutator, id);
        }
        mutator.execute();
    }
    @SuppressWarnings("unchecked")
    @Override
    public Map<String, T> get(List<String> idlist) {
        if (getClazz().getAnnotation(ColumnFamily.class).isSuper()) {
            MultigetSuperSliceQuery<String,ByteBuffer,ByteBuffer,ByteBuffer> query = HFactory.createMultigetSuperSliceQuery(keyspace, StringSerializer.get(), ByteBufferSerializer.get(), ByteBufferSerializer.get(), ByteBufferSerializer.get());
            query.setKeys(idlist);
            query.setColumnFamily(this.columnFamilyName);
            query.setRange(null, null, false, Integer.MAX_VALUE);
            QueryResult<SuperRows<String, ByteBuffer, ByteBuffer, ByteBuffer>> result = query.execute();
            
            Map<String, T> objs = new HashMap<String, T>();
            SuperRows<String, ByteBuffer, ByteBuffer, ByteBuffer> rows = result.get();
            if (rows == null)
                return objs;
            for (SuperRow<String, ByteBuffer, ByteBuffer, ByteBuffer> row : rows) {
                objs.put(row.getKey(), (T)ModelUtils.ctorFromSuperColumn(ModelUtils.parser(getClazz()), row.getKey(), row.getSuperSlice().getSuperColumns()));
            }
            return objs;
        } else {
            MultigetSliceQuery<String,ByteBuffer,ByteBuffer> query = HFactory.createMultigetSliceQuery(keyspace, StringSerializer.get(), ByteBufferSerializer.get(), ByteBufferSerializer.get());
            query.setKeys(idlist);
            query.setColumnFamily(this.columnFamilyName);
            query.setRange(null, null, false, Integer.MAX_VALUE);
            QueryResult<Rows<String, ByteBuffer, ByteBuffer>> result = query.execute();
            
            Map<String, T> objs = new HashMap<String, T>();
            Rows<String, ByteBuffer, ByteBuffer> rows = result.get();
            if (rows == null)
                return objs;
            for (Row<String, ByteBuffer, ByteBuffer> row : rows) {
                objs.put(row.getKey(), (T)ModelUtils.ctorFromColumn(ModelUtils.parser(getClazz()), row.getKey(), row.getColumnSlice().getColumns()));
            }
            return objs;
        }
    }
    @Override
    public Iterator<T> getAll() {
        return getAll(100);
    }
    @Override
    public Iterator<T> getAll(int pageSize) {
        if (getClazz().getAnnotation(ColumnFamily.class).isSuper()) {
            final SuperRowIterator<String, ByteBuffer, ByteBuffer, ByteBuffer> iterator = new SuperRowIterator<String, ByteBuffer, ByteBuffer, ByteBuffer>(keyspace, columnFamilyName, null, null, pageSize,
                    StringSerializer.get(), ByteBufferSerializer.get(), ByteBufferSerializer.get(), ByteBufferSerializer.get());
            return new Iterator<T>() {
                @Override
                public boolean hasNext() {
                    return iterator.hasNext();
                }
                @SuppressWarnings("unchecked")
                @Override
                public T next() {
                    SuperRow<String, ByteBuffer, ByteBuffer, ByteBuffer> row = iterator.next();
                    if (row == null)
                        return null;
                    return (T)ModelUtils.ctorFromSuperColumn(ModelUtils.parser(getClazz()), row.getKey(), row.getSuperSlice().getSuperColumns());
                }
                @Override
                public void remove() {
                    throw new UnsupportedOperationException();
                }
            };
        } else {
            final RowIterator<String, ByteBuffer, ByteBuffer> iterator = new RowIterator<String, ByteBuffer, ByteBuffer>(keyspace, columnFamilyName, null, null, pageSize,
                    StringSerializer.get(), ByteBufferSerializer.get(), ByteBufferSerializer.get());
            return new Iterator<T>() {
                @Override
                public boolean hasNext() {
                    return iterator.hasNext();
                }
                @SuppressWarnings("unchecked")
                @Override
                public T next() {
                    Row<String, ByteBuffer, ByteBuffer> row = iterator.next();
                    if (row == null)
                        return null;
                    return (T)ModelUtils.ctorFromColumn(ModelUtils.parser(getClazz()), row.getKey(), row.getColumnSlice().getColumns());
                }
                @Override
                public void remove() {
                    throw new UnsupportedOperationException();
                }
            };
        }
    }
    
}
