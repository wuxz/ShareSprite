package com.zhuaiwa.dd;

import java.nio.ByteBuffer;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Iterator;
import java.util.Map.Entry;

import me.prettyprint.cassandra.serializers.ByteBufferSerializer;
import me.prettyprint.cassandra.serializers.LongSerializer;
import me.prettyprint.cassandra.serializers.StringSerializer;
import me.prettyprint.hector.api.beans.HColumn;
import me.prettyprint.hector.api.beans.HSuperColumn;

import com.zhuaiwa.dd.config.DDProperties;
import com.zhuaiwa.dd.domain.FollowInfo;
import com.zhuaiwa.dd.domain.Following;
import com.zhuaiwa.dd.hector.SuperTraverse;

public class TraverseFollowing {

    private static SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd hh:mm:ss.SSS");
    public static void main(String[] args) {

        DDProperties.setProperty("dd.cassandra.addresses", "10.130.29.240");
        DDProperties.setProperty("dd.cassandra.port", "9160");
        
        SuperTraverse<String,String,String,ByteBuffer> t = new SuperTraverse<String,String,String,ByteBuffer>(Following.CN_FOLLOWING, null, null, 100, 100,
                StringSerializer.get(), StringSerializer.get(), StringSerializer.get(), ByteBufferSerializer.get());
        while (t.hasNext()) {
            Entry<String, Iterator<HSuperColumn<String, String, ByteBuffer>>> row = t.next();
            String key = row.getKey();
            Iterator<HSuperColumn<String, String, ByteBuffer>> columns = row.getValue();
            
            System.out.println(String.format("%21s following list:", key));
            
            while (columns.hasNext()) {
                HSuperColumn<String, String, ByteBuffer> column = columns.next();
                String following = column.getName();
                Long timestamp = null;
                
                HColumn<String, ByteBuffer> timestampColumn = column.getSubColumnByName(FollowInfo.FN_TIMESTAMP);
                if (timestampColumn != null)
                    timestamp = LongSerializer.get().fromByteBuffer(timestampColumn.getValue());
                if (timestamp == null) {
                    HColumn<String, ByteBuffer> useridColumn = column.getSubColumnByName(FollowInfo.FN_USERID);
                    if (useridColumn != null) {
                        timestamp = useridColumn.getClock();
                        while (timestamp > System.currentTimeMillis())
                            timestamp /= 1000;
                    }
                }
                if (timestamp == null || timestamp == 0)
                    throw new NullPointerException();
                System.out.println(String.format("    %21s %21s", following, sdf.format(new Date(timestamp))));
            }
        }
    }
}
