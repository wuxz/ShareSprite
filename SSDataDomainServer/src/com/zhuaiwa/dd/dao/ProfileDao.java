package com.zhuaiwa.dd.dao;

import java.nio.ByteBuffer;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

import me.prettyprint.cassandra.serializers.ByteBufferSerializer;
import me.prettyprint.cassandra.serializers.StringSerializer;
import me.prettyprint.hector.api.beans.ColumnSlice;
import me.prettyprint.hector.api.beans.Row;
import me.prettyprint.hector.api.beans.Rows;
import me.prettyprint.hector.api.factory.HFactory;
import me.prettyprint.hector.api.mutation.Mutator;
import me.prettyprint.hector.api.query.MultigetSliceQuery;
import me.prettyprint.hector.api.query.QueryResult;
import me.prettyprint.hector.api.query.SliceQuery;

import com.zhuaiwa.dd.domain.NameAccount;
import com.zhuaiwa.dd.domain.Profile;
import com.zhuaiwa.dd.hector.RowIterator;
import com.zhuaiwa.dd.model.ModelUtils;

public class ProfileDao extends AbstractDao<Profile> {

    @Override
    public void insert(Profile obj) {
        Mutator<String> mutator = currentThreadMutator.get();
        boolean batch = (mutator != null);
        if (mutator == null) {
            mutator = HFactory.createMutator(keyspace, StringSerializer.get());
        }
        if (obj.getUserid() == null)
            throw new IllegalArgumentException("userid can't be null.");
        
        insertPojo(mutator, obj);
        
        // 对用户名的修改需要更新索引
        if (obj.getNickname() != null) {
            Profile old = get(obj.getUserid());
            if (old != null && old.getNickname() != null) {
                mutator.addDeletion(old.getNickname(), NameAccount.CN_NAME_ACCOUNT);
            }
            insertIndex(mutator, obj);
        }
        
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
        mutator.addDeletion(id, this.columnFamilyName);
        if (!batch)
            mutator.execute();
    }
    @Override
    public Profile get(String id) {
        SliceQuery<String,ByteBuffer,ByteBuffer> query = HFactory.createSliceQuery(keyspace, StringSerializer.get(), ByteBufferSerializer.get(), ByteBufferSerializer.get());
        query.setKey(id);
        query.setColumnFamily(this.columnFamilyName);
        query.setRange(null, null, false, Integer.MAX_VALUE);
        QueryResult<ColumnSlice<ByteBuffer, ByteBuffer>> result = query.execute();
        return (Profile)ModelUtils.ctorFromColumn(ModelUtils.parser(getClazz()), id, result.get().getColumns());
    }

    @Override
    public void insert(List<Profile> objlist) {
        Mutator<String> mutator = currentThreadMutator.get();
        boolean batch = (mutator != null);
        if (mutator == null) {
            mutator = HFactory.createMutator(keyspace, StringSerializer.get());
        }
        for (Profile profile : objlist) {
            insertPojo(mutator, profile);

            // 对用户名的修改需要更新索引
            if (profile.getNickname() != null) {
                Profile old = get(profile.getUserid());
                if (old != null && old.getNickname() != null) {
                    mutator.addDeletion(old.getNickname(), NameAccount.CN_NAME_ACCOUNT);
                }
                insertIndex(mutator, profile);
            }
        }
        if (!batch)
            mutator.execute();
    }
    @Override
    public void delete(List<String> idlist) {
        Mutator<String> mutator = HFactory.createMutator(keyspace, StringSerializer.get());
        for (String id : idlist) {
            mutator.addDeletion(id, this.columnFamilyName);
        }
        mutator.execute();
    }
    @Override
    public Map<String, Profile> get(List<String> idlist) {
        MultigetSliceQuery<String,ByteBuffer,ByteBuffer> query = HFactory.createMultigetSliceQuery(keyspace, StringSerializer.get(), ByteBufferSerializer.get(), ByteBufferSerializer.get());
        query.setKeys(idlist);
        query.setColumnFamily(this.columnFamilyName);
        query.setRange(null, null, false, Integer.MAX_VALUE);
        QueryResult<Rows<String, ByteBuffer, ByteBuffer>> result = query.execute();
        
        Map<String, Profile> profiles = new HashMap<String, Profile>();
        Rows<String, ByteBuffer, ByteBuffer> rows = result.get();
        if (rows == null)
            return profiles;
        for (Row<String, ByteBuffer, ByteBuffer> row : rows) {
            profiles.put(row.getKey(), (Profile)ModelUtils.ctorFromColumn(ModelUtils.parser(getClazz()), row.getKey(), row.getColumnSlice().getColumns()));
        }
        return profiles;
    }

    @Override
    public Iterator<Profile> getAll(int pageSize) {
        final RowIterator<String, ByteBuffer, ByteBuffer> iterator = new RowIterator<String, ByteBuffer, ByteBuffer>(keyspace, columnFamilyName, null, null, pageSize,
                StringSerializer.get(), ByteBufferSerializer.get(), ByteBufferSerializer.get());
        return new Iterator<Profile>() {
            @Override
            public boolean hasNext() {
                return iterator.hasNext();
            }
            @Override
            public Profile next() {
                Row<String, ByteBuffer, ByteBuffer> row = iterator.next();
                if (row == null)
                    return null;
                return (Profile)ModelUtils.ctorFromColumn(ModelUtils.parser(getClazz()), row.getKey(), row.getColumnSlice().getColumns());
            }
            @Override
            public void remove() {
                throw new UnsupportedOperationException();
            }
        };
    }

    public static void insertPojo(Mutator<String> mutator, Profile profile) {
        ModelUtils.mutatePojo(ModelUtils.parser(Profile.class), mutator, profile);
    }
    public static void insertIndex(Mutator<String> mutator, Profile profile) {
        String userid = profile.getUserid();
        if (userid == null)
            throw new IllegalArgumentException("userid can't be empty");
        if (profile.getNickname() != null) {
            mutator.addInsertion(profile.getNickname(), NameAccount.CN_NAME_ACCOUNT, createStringColumn(NameAccount.FN_USERID, userid));
            mutator.addInsertion(profile.getNickname(), NameAccount.CN_NAME_ACCOUNT, createStringColumn(NameAccount.FN_NAME, profile.getNickname()));
        }
    }
}
