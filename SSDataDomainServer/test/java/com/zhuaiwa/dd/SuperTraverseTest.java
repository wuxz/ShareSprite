package com.zhuaiwa.dd;

import java.nio.ByteBuffer;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.Iterator;
import java.util.List;
import java.util.Map.Entry;

import me.prettyprint.cassandra.serializers.ByteBufferSerializer;
import me.prettyprint.cassandra.serializers.LongSerializer;
import me.prettyprint.cassandra.serializers.StringSerializer;
import me.prettyprint.hector.api.beans.HColumn;
import me.prettyprint.hector.api.beans.HSuperColumn;
import me.prettyprint.hector.api.factory.HFactory;
import me.prettyprint.hector.api.mutation.Mutator;

import org.junit.After;
import org.junit.AfterClass;
import org.junit.Assert;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;

import com.zhuaiwa.dd.domain.FollowInfo;
import com.zhuaiwa.dd.domain.Following;
import com.zhuaiwa.dd.hector.HectorFactory;
import com.zhuaiwa.dd.hector.SuperTraverse;
import com.zhuaiwa.dd.utils.TestInitializer;

public class SuperTraverseTest {
    @BeforeClass
    public static void init() throws Exception {
        TestInitializer.getSync();
    }
    
    @AfterClass
    public static void shutdown() throws Exception {
    }

    @SuppressWarnings("unchecked")
    @Before
    public void setUp() throws Exception {
        TestInitializer.initKeyspace();
        
        for (int j = 0; j < 100; j++) {
            String userid = String.format("%02d", j);
            Mutator<String> mutator = HFactory.createMutator(HectorFactory.getKeyspace(), StringSerializer.get());
            for (int i = 0; i < 100; i++) {
                String following = String.format("%02d", i);
                mutator.addInsertion(userid, Following.CN_FOLLOWING, 
                        HFactory.createSuperColumn(following, 
                                Arrays.asList(
                                        (HColumn<String, ByteBuffer>)HFactory.createColumn(FollowInfo.FN_USERID, StringSerializer.get().toByteBuffer(following), StringSerializer.get(), ByteBufferSerializer.get()),
                                        (HColumn<String, ByteBuffer>)HFactory.createColumn(FollowInfo.FN_TIMESTAMP, LongSerializer.get().toByteBuffer(System.currentTimeMillis()), StringSerializer.get(), ByteBufferSerializer.get())
                                        ), 
                                StringSerializer.get(), StringSerializer.get(), ByteBufferSerializer.get()));
            }
            mutator.execute();
        }
    }

    @After
    public void tearDown() throws Exception {
        TestInitializer.dropKeyspace();
    }

    @Test
    public void test() {
        String[] expects = new String[100];
        for (int i = 0; i < 100; i++) {
            expects[i] = String.format("%02d", i);
        }

        List<String> users = new ArrayList<String>();
        SuperTraverse<String,String,String,ByteBuffer> t = new SuperTraverse<String,String,String,ByteBuffer>(
                Following.CN_FOLLOWING, null, null, 10, 10,
                StringSerializer.get(), StringSerializer.get(), StringSerializer.get(), ByteBufferSerializer.get());
        while(t.hasNext()) {
            Entry<String, Iterator<HSuperColumn<String, String, ByteBuffer>>> row = t.next();
            users.add(row.getKey());

            List<String> followings = new ArrayList<String>();
            Iterator<HSuperColumn<String, String, ByteBuffer>> columns = row.getValue();
            while (columns.hasNext()) {
                HSuperColumn<String, String, ByteBuffer> column = columns.next();
                followings.add(column.getName());
            }
            Assert.assertArrayEquals(expects, followings.toArray());
        }
        Collections.sort(users);
        Assert.assertArrayEquals(expects, users.toArray());
    }

}
