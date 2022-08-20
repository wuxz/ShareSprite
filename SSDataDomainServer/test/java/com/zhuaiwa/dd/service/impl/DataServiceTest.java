package com.zhuaiwa.dd.service.impl;

import java.util.Arrays;
import java.util.List;

import me.prettyprint.hector.api.Keyspace;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;

import com.zhuaiwa.dd.config.DDProperties;
import com.zhuaiwa.dd.domain.FollowInfo;
import com.zhuaiwa.dd.hector.HectorFactory;

public class DataServiceTest {
    Keyspace cassandra;

    @Before
    public void setUp() throws Exception {
        DDProperties.setProperty("dd.cassandra.addresses", "p0");
        DDProperties.setProperty("dd.cassandra.port", "9160");
        cassandra = HectorFactory.getKeyspace();
    }

    @After
    public void tearDown() throws Exception {
    }

    @Test
    public void testIsFollower() throws Exception {
        DataServiceImpl ds = new DataServiceImpl(cassandra);
        List<FollowInfo> followers = ds.isFollower("4b96cc0599b1c94ba49e9cc2c8cec944", Arrays.asList(
                "848c8b50f32edcb95876b2c7153ed585",
                "3c808c935cf82e6e3cd17bf2ee3ebf25"));
        for (FollowInfo fi : followers) {
            System.out.println(fi);
        }
    }

}
