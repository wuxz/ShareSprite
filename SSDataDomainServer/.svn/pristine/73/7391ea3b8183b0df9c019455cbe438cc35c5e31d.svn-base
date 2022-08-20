package com.zhuaiwa.dd.api;


import java.util.Arrays;

import me.prettyprint.cassandra.serializers.StringSerializer;
import me.prettyprint.hector.api.factory.HFactory;
import me.prettyprint.hector.api.mutation.Mutator;

import org.junit.After;
import org.junit.AfterClass;
import org.junit.Assert;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;

import com.zhuaiwa.api.Common.SSBOX;
import com.zhuaiwa.api.Common.SSResultCode;
import com.zhuaiwa.api.Rpc.GetMessageByTimestampRequest;
import com.zhuaiwa.api.Rpc.GetMessageByTimestampResponse;
import com.zhuaiwa.api.Rpc.GetMessageRequest;
import com.zhuaiwa.api.Rpc.GetMessageResponse;
import com.zhuaiwa.api.SSDataDomain.SSDataDomainSvc.BlockingInterface;
import com.zhuaiwa.api.netty.NettyRpcController;
import com.zhuaiwa.dd.domain.MessageInfo;
import com.zhuaiwa.dd.domain.PubBox;
import com.zhuaiwa.dd.hector.HectorFactory;
import com.zhuaiwa.dd.utils.TestInitializer;

public class PubBoxTest {
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
        prepareData();
    }

    @After
    public void tearDown() throws Exception {
        TestInitializer.dropKeyspace();
    }
    
    private String getKey() {
        return "test";
    }

    @SuppressWarnings("unchecked")
    public void prepareData() throws Exception {
        Mutator<String> mutator = HFactory.createMutator(HectorFactory.getKeyspace(), StringSerializer.get());
        for (int i = 0; i < 100; i++) {
            String msgid = String.format("%016X%s", i, getKey());
            mutator.addInsertion(getKey(), PubBox.CN_PUBBOX,
                    HFactory.createSuperColumn(msgid, Arrays.asList(
                            HFactory.createColumn(MessageInfo.FN_MSGID, msgid, StringSerializer.get(), StringSerializer.get())
                            ), StringSerializer.get(), StringSerializer.get(), StringSerializer.get())
                    );
        }
        mutator.execute();
    }
    
    public void cleanupData() throws Exception {
        Mutator<String> mutator = HFactory.createMutator(HectorFactory.getKeyspace(), StringSerializer.get());
        mutator.addDeletion(getKey(), PubBox.CN_PUBBOX);
        mutator.execute();
    }
    
    /**
     * 无光标，升序
     */
    @Test
    public void testGetMessage1() throws Exception {
        NettyRpcController controller = new NettyRpcController();
        GetMessageRequest request = GetMessageRequest.newBuilder()
            .setUserid(getKey())
            .setSourceBox(SSBOX.PUBBOX)
            .setCount(5)
            .build();
        GetMessageResponse response = api.getMessage(controller, request);
        Assert.assertEquals(SSResultCode.RC_OK.getNumber(), controller.getCode());
        
        Assert.assertArrayEquals(new String[]{
                "0000000000000000test",
                "0000000000000001test",
                "0000000000000002test",
                "0000000000000003test",
                "0000000000000004test"}, response.getMsgidListList().toArray());
    }

    /**
     * 无光标，降序
     */
    @Test
    public void testGetMessage2() throws Exception {
        NettyRpcController controller = new NettyRpcController();
        GetMessageRequest request = GetMessageRequest.newBuilder()
            .setUserid(getKey())
            .setSourceBox(SSBOX.PUBBOX)
            .setCount(-5)
            .build();
        GetMessageResponse response = api.getMessage(controller, request);
        Assert.assertEquals(SSResultCode.RC_OK.getNumber(), controller.getCode());
        
        Assert.assertArrayEquals(new String[]{
                "0000000000000063test",
                "0000000000000062test",
                "0000000000000061test",
                "0000000000000060test",
                "000000000000005Ftest"}, response.getMsgidListList().toArray());
    }

    /**
     * 有光标，升序
     */
    @Test
    public void testGetMessage3() throws Exception {
        NettyRpcController controller = new NettyRpcController();
        GetMessageRequest request = GetMessageRequest.newBuilder()
            .setUserid(getKey())
            .setSourceBox(SSBOX.PUBBOX)
            .setCursorId("0000000000000005test")
            .setCount(5)
            .build();
        GetMessageResponse response = api.getMessage(controller, request);
        Assert.assertEquals(SSResultCode.RC_OK.getNumber(), controller.getCode());
        
        Assert.assertArrayEquals(new String[]{
                "0000000000000006test",
                "0000000000000007test",
                "0000000000000008test",
                "0000000000000009test",
                "000000000000000Atest"}, response.getMsgidListList().toArray());
    }

    /**
     * 有光标，降序
     */
    @Test
    public void testGetMessage4() throws Exception {
        NettyRpcController controller = new NettyRpcController();
        GetMessageRequest request = GetMessageRequest.newBuilder()
            .setUserid(getKey())
            .setSourceBox(SSBOX.PUBBOX)
            .setCursorId("0000000000000005test")
            .setCount(-5)
            .build();
        GetMessageResponse response = api.getMessage(controller, request);
        Assert.assertEquals(SSResultCode.RC_OK.getNumber(), controller.getCode());
        
        Assert.assertArrayEquals(new String[]{
                "0000000000000004test",
                "0000000000000003test",
                "0000000000000002test",
                "0000000000000001test",
                "0000000000000000test"}, response.getMsgidListList().toArray());
    }

    /**
     * 升序
     */
    @Test
    public void testGetMessageByTimestamp1() throws Exception {
        NettyRpcController controller = new NettyRpcController();
        GetMessageByTimestampRequest request = GetMessageByTimestampRequest.newBuilder()
            .setUserid(getKey())
            .setSourceBox(SSBOX.PUBBOX)
            .setStartTimestamp(5)
            .setEndTimestamp(10)
            .setCount(5)
            .build();
        GetMessageByTimestampResponse response = api.getMessageByTimestamp(controller, request);
        Assert.assertEquals(SSResultCode.RC_OK.getNumber(), controller.getCode());

        Assert.assertArrayEquals(new String[]{
                "0000000000000006test",
                "0000000000000007test",
                "0000000000000008test",
                "0000000000000009test",
                "000000000000000Atest"}, response.getMsgidListList().toArray());
    }

    /**
     * 降序
     */
    @Test
    public void testGetMessageByTimestamp2() throws Exception {
        NettyRpcController controller = new NettyRpcController();
        GetMessageByTimestampRequest request = GetMessageByTimestampRequest.newBuilder()
            .setUserid(getKey())
            .setSourceBox(SSBOX.PUBBOX)
            .setStartTimestamp(5)
            .setEndTimestamp(0)
            .setCount(5)
            .build();
        GetMessageByTimestampResponse response = api.getMessageByTimestamp(controller, request);
        Assert.assertEquals(SSResultCode.RC_OK.getNumber(), controller.getCode());

        Assert.assertArrayEquals(new String[]{
                "0000000000000004test",
                "0000000000000003test",
                "0000000000000002test",
                "0000000000000001test",
                "0000000000000000test"}, response.getMsgidListList().toArray());
    }
}
