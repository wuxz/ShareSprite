package com.zhuaiwa.dd;

import java.net.InetSocketAddress;
import java.util.concurrent.Executors;

import org.jboss.netty.channel.socket.nio.NioClientSocketChannelFactory;

import com.google.protobuf.ServiceException;
import com.zhuaiwa.api.Common.SSMessage;
import com.zhuaiwa.api.Common.SSMessageType;
import com.zhuaiwa.api.Common.SSPerson;
import com.zhuaiwa.api.Common.SSShareType;
import com.zhuaiwa.api.Rpc.SendMessageRequest;
import com.zhuaiwa.api.Rpc.SendMessageResponse;
import com.zhuaiwa.api.SSDataDomain.SSDataDomainSvc;
import com.zhuaiwa.api.SSDataDomain.SSDataDomainSvc.BlockingInterface;
import com.zhuaiwa.api.netty.NettyRpcController;
import com.zhuaiwa.api.netty.client.NettyRpcChannel;
import com.zhuaiwa.api.netty.client.NettyRpcClient;
import com.zhuaiwa.api.util.SSIdUtils;
import com.zhuaiwa.dd.config.DDProperties;
import com.zhuaiwa.dd.dao.MessageDao;
import com.zhuaiwa.dd.domain.Message;
import com.zhuaiwa.dd.hector.HectorFactory;
import com.zhuaiwa.dd.service.impl.DataServiceImpl;
import com.zhuaiwa.session.search.Indexer;
import com.zhuaiwa.session.search.json.Contents;


public class Test {

//    public static void main(String[] args) throws Exception {
//        NettyRpcClient client = new NettyRpcClient(
//                new NioClientSocketChannelFactory(
//                        Executors.newCachedThreadPool(), 
//                        Executors.newCachedThreadPool()));
//        NettyRpcChannel channel = client.blockingConnect(new InetSocketAddress("10.130.29.243", 19161));
//        BlockingInterface dd = SSDataDomainSvc.newBlockingStub(channel);
//        
//        NettyRpcController controller = new NettyRpcController();
//        SendMessageRequest request = SendMessageRequest.newBuilder()
//                .setUserid("test")
//                .setMsg(SSMessage.newBuilder()
//                        .setShareType(SSShareType.SHARE_TYPE_PUBLIC.getNumber())
//                        .setMsgType(SSMessageType.MESSAGE_TYPE_CONTENT.getNumber())
//                        .setSender(SSIdUtils.fromUserId("test"))
//                        .setBody("{\"contents\":[{\"body\":\"This is a test public message.\",\"content_type\":\"self\"}]}")
//                        .setAgent("junit")
//                        .addTags("测试消息")
//                        .addTags("hello world")
//                        .build())
//                .build();
//        SendMessageResponse response = dd.sendMessage(controller, request);
//        System.out.println(response.getMsgid());
//        channel.close();
//        client.shutdown();
//    }
    
    public static void main(String[] args) throws Exception {
        DDProperties.setProperty("dd.indexer.queue.url", "tcp://10.130.29.237:61616");
        DDProperties.setProperty("dd.cassandra.addresses", "10.130.29.240");
        Indexer indexer = Indexer.getInstance();
//        SSMessage ssmessage = SSMessage.newBuilder()
//          .setMsgid("cccc")
//          .setShareType(SSShareType.SHARE_TYPE_PUBLIC.getNumber())
//          .setMsgType(SSMessageType.MESSAGE_TYPE_CONTENT.getNumber())
//          .setSender(SSIdUtils.fromUserId("test"))
//          .setBody("{\"contents\":[{\"body\":\"This is a test public message.\",\"content_type\":\"external\"}]}")
//          .setAgent("junit")
//          .addTags("测试消息")
//          .addTags("hello world")
//          .build();
        MessageDao dao = new MessageDao();
        dao.setKeyspace(HectorFactory.getKeyspace());
        Message message = dao.get("00000132E7F5D59Faff65f94c9fe0f1276afa985fee15373");
        SSMessage.Builder ssmessage = SSMessage.newBuilder();
        DataServiceImpl.build(ssmessage, message);
        indexer.addMessage(message.getMsgid(), ssmessage.build());
        System.exit(0);
    }
}
