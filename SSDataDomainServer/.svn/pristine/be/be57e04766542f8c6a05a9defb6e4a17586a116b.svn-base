package com.zhuaiwa.dd.dao;

import java.nio.ByteBuffer;
import java.util.ArrayList;
import java.util.List;

import me.prettyprint.cassandra.serializers.ByteBufferSerializer;
import me.prettyprint.cassandra.serializers.StringSerializer;
import me.prettyprint.hector.api.beans.SuperRow;
import me.prettyprint.hector.api.beans.SuperRows;
import me.prettyprint.hector.api.factory.HFactory;
import me.prettyprint.hector.api.query.MultigetSuperSliceQuery;
import me.prettyprint.hector.api.query.QueryResult;

import com.zhuaiwa.dd.domain.Following;

public class FollowingDao extends AbstractDao<Following> {
    List<String> isFollower(String userid, List<String> followers) {
        MultigetSuperSliceQuery<String, String, String, ByteBuffer> query = HFactory.createMultigetSuperSliceQuery(keyspace, StringSerializer.get(), StringSerializer.get(), StringSerializer.get(), ByteBufferSerializer.get());
        query.setColumnFamily(this.columnFamilyName);
        query.setKeys(followers);
        query.setColumnNames(userid);
        QueryResult<SuperRows<String, String, String, ByteBuffer>> result = query.execute();
        SuperRows<String, String, String, ByteBuffer> rows = (result == null ? null : result.get());
        
        List<String> exists = new ArrayList<String>();
        if (rows == null)
            return exists;
        for (SuperRow<String, String, String, ByteBuffer> row : rows) {
            if (row.getSuperSlice() != null && row.getSuperSlice().getColumnByName(userid) != null) {
                exists.add(row.getKey());
            }
        }
        return exists;
    }
}
