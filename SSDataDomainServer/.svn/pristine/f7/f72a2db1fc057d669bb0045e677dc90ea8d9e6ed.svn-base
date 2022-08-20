package com.zhuaiwa.dd;

import java.net.InetSocketAddress;
import java.util.concurrent.Executors;

import org.apache.log4j.BasicConfigurator;
import org.jboss.netty.channel.socket.nio.NioClientSocketChannelFactory;

import com.zhuaiwa.api.Common.SSProfile;
import com.zhuaiwa.api.Rpc.GetProfileRequest;
import com.zhuaiwa.api.Rpc.SetProfileRequest;
import com.zhuaiwa.api.SSDataDomain.GetAccountRequest;
import com.zhuaiwa.api.SSDataDomain.SSDataDomainSvc;
import com.zhuaiwa.api.SSDataDomain.SSDataDomainSvc.BlockingInterface;
import com.zhuaiwa.api.netty.NettyRpcController;
import com.zhuaiwa.api.netty.client.NettyRpcChannel;
import com.zhuaiwa.api.netty.client.NettyRpcClient;
import com.zhuaiwa.dd.protobuf.DataDomainApiProtobufExtension;

public class UpdateNickname {
    public static void main(String[] args) throws Exception {
        BasicConfigurator.configure();
        DataDomainApiProtobufExtension.register();
        
        NettyRpcClient client = new NettyRpcClient(
                new NioClientSocketChannelFactory(
                        Executors.newCachedThreadPool(), 
                        Executors.newCachedThreadPool()));
        
        NettyRpcChannel channel = client.blockingConnect(new InetSocketAddress("10.130.29.243", 19161));
        
        BlockingInterface svc = SSDataDomainSvc.newBlockingStub(channel);
        String userid =
            svc.getAccount(new NettyRpcController(), GetAccountRequest.newBuilder().addEmail("yaosw@channelsoft.com").build()).getUseridList(0);
        System.out.println(svc.getProfile(new NettyRpcController(), GetProfileRequest.newBuilder().addUseridList(userid).build()).getProfileList(0).getNickname());
        svc.setProfile(new NettyRpcController(), SetProfileRequest.newBuilder().setUserid(userid).setProfile(SSProfile.newBuilder().setNickname("姚松文")).build());
        System.out.println(svc.getProfile(new NettyRpcController(), GetProfileRequest.newBuilder().addUseridList(userid).build()).getProfileList(0).getNickname());
        channel.close();
        client.shutdown();
    }
    
//    public static void main(String[] args) throws Exception {
//        BasicConfigurator.configure();
//
//        DDProperties.setProperty("dd.cassandra.connections", "1");
//        DDProperties.setProperty("dd.cassandra.addresses", "10.130.29.240");
//        DDProperties.setProperty("dd.cassandra.port", "9160");
//        
//        BlockingInterface svc = TestInitializer.getSync();
//        String userid =
//            svc.getAccount(new NettyRpcController(), GetAccountRequest.newBuilder().addEmail("yaosw@channelsoft.com").build()).getUseridList(0);
//        System.out.println(svc.getProfile(new NettyRpcController(), GetProfileRequest.newBuilder().addUseridList(userid).build()).getProfileList(0).getNickname());
//        svc.setProfile(new NettyRpcController(), SetProfileRequest.newBuilder().setUserid(userid).setProfile(SSProfile.newBuilder().setNickname("姚松文")).build());
//        System.out.println(svc.getProfile(new NettyRpcController(), GetProfileRequest.newBuilder().addUseridList(userid).build()).getProfileList(0).getNickname());
////        svc.follow(new NettyRpcController(), FollowRequest.newBuilder().setFollowerUserid(userid).addFollowingUseridList("test").build());
////        svc.unfollow(new NettyRpcController(), UnfollowRequest.newBuilder().setFollowerUserid(userid).addFollowingUseridList("test").build());
//        HectorFactory.shutdown();
//    }
}
