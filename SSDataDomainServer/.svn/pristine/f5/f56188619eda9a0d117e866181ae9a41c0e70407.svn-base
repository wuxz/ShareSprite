package com.zhuaiwa.dd.api;

import org.junit.After;
import org.junit.AfterClass;
import org.junit.Assert;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;

import com.google.protobuf.TextFormat;
import com.zhuaiwa.api.Common.SSProfile;
import com.zhuaiwa.api.Common.SSResultCode;
import com.zhuaiwa.api.Rpc.GetProfileRequest;
import com.zhuaiwa.api.Rpc.GetProfileResponse;
import com.zhuaiwa.api.Rpc.SetProfileRequest;
import com.zhuaiwa.api.Rpc.SetProfileResponse;
import com.zhuaiwa.api.SSDataDomain.GetAccountRequest;
import com.zhuaiwa.api.SSDataDomain.GetAccountResponse;
import com.zhuaiwa.api.SSDataDomain.SSDataDomainSvc.BlockingInterface;
import com.zhuaiwa.api.netty.NettyRpcController;
import com.zhuaiwa.dd.utils.TestInitializer;

public class ProfileTest {
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
        
        NettyRpcController controller = new NettyRpcController();
        SetProfileRequest request = SetProfileRequest.newBuilder()
            .setUserid("test")
            .setProfile(SSProfile.newBuilder()
                    .setNickname("a")
                    )
            .build();
        SetProfileResponse response = api.setProfile(controller, request);
        System.out.println(TextFormat.printToString(response));
        Assert.assertEquals(SSResultCode.RC_OK.getNumber(), controller.getCode());
    }

    @After
    public void tearDown() throws Exception {
        TestInitializer.dropKeyspace();
    }
    
    @Test
    public void testGetProfile() throws Exception {
        NettyRpcController controller = new NettyRpcController();
        GetProfileRequest request = GetProfileRequest.newBuilder()
            .addUseridList("test")
            .build();
        GetProfileResponse response = api.getProfile(controller, request);
        Assert.assertEquals(SSResultCode.RC_OK.getNumber(), controller.getCode());
        Assert.assertEquals("a", response.getProfileList(0).getNickname());
    }
    
    @Test
    public void testGetUseridByName() throws Exception {
        NettyRpcController controller = new NettyRpcController();
        GetAccountRequest request = GetAccountRequest.newBuilder()
            .addName("a")
            .build();
        GetAccountResponse response = api.getAccount(controller, request);
        Assert.assertEquals(SSResultCode.RC_OK.getNumber(), controller.getCode());
        Assert.assertEquals("test", response.getUseridList(0));
    }
    
    @Test
    public void testUpdateProfileNickname() throws Exception {
        {
        NettyRpcController controller = new NettyRpcController();
        SetProfileRequest request = SetProfileRequest.newBuilder()
            .setUserid("test")
            .setProfile(SSProfile.newBuilder()
                    .setNickname("b")
                    )
            .build();
        SetProfileResponse response = api.setProfile(controller, request);
        Assert.assertEquals(SSResultCode.RC_OK.getNumber(), controller.getCode());
        }
        {
        NettyRpcController controller = new NettyRpcController();
        GetAccountRequest request = GetAccountRequest.newBuilder()
            .addName("a")
            .build();
        GetAccountResponse response = api.getAccount(controller, request);
        Assert.assertEquals(SSResultCode.RC_OK.getNumber(), controller.getCode());
        Assert.assertEquals(0, response.getUseridListCount());
        }
        {
        NettyRpcController controller = new NettyRpcController();
        GetAccountRequest request = GetAccountRequest.newBuilder()
            .addName("b")
            .build();
        GetAccountResponse response = api.getAccount(controller, request);
        Assert.assertEquals(SSResultCode.RC_OK.getNumber(), controller.getCode());
        Assert.assertEquals("test", response.getUseridList(0));
        }
    }
}
