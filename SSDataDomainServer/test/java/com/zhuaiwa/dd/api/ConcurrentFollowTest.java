package com.zhuaiwa.dd.api;

import java.util.concurrent.CountDownLatch;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

import me.prettyprint.cassandra.serializers.StringSerializer;
import me.prettyprint.hector.api.factory.HFactory;
import me.prettyprint.hector.api.mutation.Mutator;

import org.junit.After;
import org.junit.AfterClass;
import org.junit.Assert;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;

import com.zhuaiwa.api.Common.SSResultCode;
import com.zhuaiwa.api.Rpc.FollowRequest;
import com.zhuaiwa.api.Rpc.GetProfileRequest;
import com.zhuaiwa.api.Rpc.GetProfileResponse;
import com.zhuaiwa.api.SSDataDomain.SSDataDomainSvc.BlockingInterface;
import com.zhuaiwa.api.netty.NettyRpcController;
import com.zhuaiwa.dd.domain.Follower;
import com.zhuaiwa.dd.domain.Following;
import com.zhuaiwa.dd.domain.Profile;
import com.zhuaiwa.dd.hector.HectorFactory;
import com.zhuaiwa.dd.utils.TestInitializer;

public class ConcurrentFollowTest {
    private static BlockingInterface api;

    @BeforeClass
    public static void init() throws Exception {
        api = TestInitializer.getSync();
    }
    
    @AfterClass
    public static void shutdown() throws Exception {
//        HectorFactory.shutdown();
    }
    
    
    @Before
    public void setUp() throws Exception {
        TestInitializer.initKeyspace();
    }
    
    @After
    public void tearDown() throws Exception {
        TestInitializer.dropKeyspace();
    }
    
    private String getUserId() {
        return "test";
    }
    
    private void clearData() throws Exception {
        Mutator<String> mutator = HFactory.createMutator(HectorFactory.getKeyspace(), StringSerializer.get());
        mutator.addDeletion(getUserId(), Profile.CN_PROFILE);
        mutator.addDeletion(getUserId(), Following.CN_FOLLOWING);
        for (int i = 0; i < 1000; i++) {
            mutator.addDeletion(getUserId() + i, Profile.CN_PROFILE);
            mutator.addDeletion(getUserId() + i, Follower.CN_FOLLOWER);
        }
        mutator.execute();
    }
    
    private void assertFollowingCountEqual(String userid, int expected) throws Exception {
        NettyRpcController controller = new NettyRpcController();
        GetProfileRequest request = GetProfileRequest.newBuilder()
            .addUseridList(userid)
            .build();
        GetProfileResponse response = api.getProfile(controller, request);
        Assert.assertEquals(SSResultCode.RC_OK.getNumber(), controller.getCode());
        Assert.assertEquals(expected, response.getProfileList(0).getFollowingCount());
    }
    
    private void testFollowN(ExecutorService es) throws Exception {
        final CountDownLatch cdl = new CountDownLatch(1000);
        for (int i = 0; i < 1000; i++) {
            final int index = i;
            es.submit(new Runnable() {
                @Override
                public void run() {
                    try {
                    api.follow(new NettyRpcController(), FollowRequest.newBuilder()
                            .setFollowerUserid(getUserId())
                            .addFollowingUseridList(getUserId()+index)
                            .build());
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                    cdl.countDown();
                }
            });
        }
        cdl.await();
    }
    
    private void testFollow1(ExecutorService es) throws Exception {
        final CountDownLatch cdl = new CountDownLatch(1000);
        for (int i = 0; i < 1000; i++) {
            es.submit(new Runnable() {
                @Override
                public void run() {
                    try {
                    api.follow(new NettyRpcController(), FollowRequest.newBuilder()
                            .setFollowerUserid(getUserId())
                            .addFollowingUseridList(getUserId()+0)
                            .build());
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                    cdl.countDown();
                }
            });
        }
        cdl.await();
    }
    
    @Test
    public void testSingleThreadN() throws Exception {
        // 连续关注多人
        clearData();
        testFollowN(Executors.newSingleThreadExecutor());
        assertFollowingCountEqual(getUserId(), 1000);
        clearData();
    }
    
    @Test
    public void testSingleThread1() throws Exception {
        // 连续关注同一个人
        clearData();
        testFollow1(Executors.newSingleThreadExecutor());
        assertFollowingCountEqual(getUserId(), 1);
        clearData();
    }
    
    @Test
    public void testMultiThreadN() throws Exception {
        // 并发关注多人
        clearData();
        testFollowN(Executors.newFixedThreadPool(10));
        assertFollowingCountEqual(getUserId(), 1000);
        clearData();
    }
    
    @Test
    public void testMultiThread1() throws Exception {
        // 并发关注同一个人
        clearData();
        testFollow1(Executors.newFixedThreadPool(10));
        assertFollowingCountEqual(getUserId(), 1);
        clearData();
    }
}
