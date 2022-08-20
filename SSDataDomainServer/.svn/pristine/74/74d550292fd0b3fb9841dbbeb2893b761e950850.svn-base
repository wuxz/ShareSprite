package com.zhuaiwa.dd;

import java.nio.ByteBuffer;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Iterator;
import java.util.List;
import java.util.Map.Entry;

import me.prettyprint.cassandra.serializers.ByteBufferSerializer;
import me.prettyprint.cassandra.serializers.LongSerializer;
import me.prettyprint.cassandra.serializers.StringSerializer;
import me.prettyprint.hector.api.beans.HColumn;
import me.prettyprint.hector.api.factory.HFactory;
import me.prettyprint.hector.api.mutation.Mutator;

import org.junit.After;
import org.junit.AfterClass;
import org.junit.Assert;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;

import com.zhuaiwa.dd.domain.Profile;
import com.zhuaiwa.dd.hector.HectorFactory;
import com.zhuaiwa.dd.hector.Traverse;
import com.zhuaiwa.dd.utils.TestInitializer;

public class TraverseTest {
    @BeforeClass
    public static void init() throws Exception {
        TestInitializer.getSync();
    }
    
    @AfterClass
    public static void shutdown() throws Exception {
    }

    @Before
    public void setUp() throws Exception {
        TestInitializer.initKeyspace();
        
        for (int j = 0; j < 100; j++) {
            String userid = String.format("%02d", j);
            Mutator<String> mutator = HFactory.createMutator(HectorFactory.getKeyspace(), StringSerializer.get());
            for (int i = 0; i < 100; i++) {
                String following = String.format("%02d", i);
                mutator.addInsertion(userid, Profile.CN_PROFILE, 
                        HFactory.createColumn(
                                following, 
                                LongSerializer.get().toByteBuffer(System.currentTimeMillis()),
                                StringSerializer.get(), ByteBufferSerializer.get()));
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
        Traverse<String,String,ByteBuffer> t = new Traverse<String,String,ByteBuffer>(
                Profile.CN_PROFILE, null, null, 10, 10,
                StringSerializer.get(), StringSerializer.get(), ByteBufferSerializer.get());
        while(t.hasNext()) {
            Entry<String, Iterator<HColumn<String, ByteBuffer>>> row = t.next();
            users.add(row.getKey());

            List<String> followings = new ArrayList<String>();
            Iterator<HColumn<String, ByteBuffer>> columns = row.getValue();
            while (columns.hasNext()) {
                HColumn<String, ByteBuffer> column = columns.next();
                followings.add(column.getName());
            }
            Assert.assertArrayEquals(expects, followings.toArray());
        }
        Collections.sort(users);
        Assert.assertArrayEquals(expects, users.toArray());
    }

}
