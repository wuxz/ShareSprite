package com.zhuaiwa.session;

import java.net.InetSocketAddress;
import java.util.concurrent.Executors;

import org.jboss.netty.channel.socket.nio.NioClientSocketChannelFactory;

import com.google.protobuf.ServiceException;
import com.zhuaiwa.api.Common.SSId;
import com.zhuaiwa.api.Rpc.FollowRequest;
import com.zhuaiwa.api.SSSessionDomain.LoginRequest;
import com.zhuaiwa.api.SSSessionDomain.SSSessionDomainSvc;
import com.zhuaiwa.api.netty.NettyRpcController;
import com.zhuaiwa.api.netty.client.NettyRpcChannel;
import com.zhuaiwa.api.netty.client.NettyRpcClient;
import com.zhuaiwa.api.util.SSIdUtils;
import com.zhuaiwa.util.JsonFormat;

public class SSSessionDomianClient {
	private static SSId ssid = SSId.newBuilder().setDomain(SSIdUtils.SSIdDomain.email.name())
	.setId("tangjun@channelsoft.com").build();

	/**
	 * @param args
	 */
	public static void main(String[] args) {
		SSSessionDomainServer.registerSessionDomainApiProtobufExtension();
		NettyRpcClient client = new NettyRpcClient(new NioClientSocketChannelFactory(Executors.newCachedThreadPool(),
				Executors.newCachedThreadPool()));
		NettyRpcChannel channel = client.blockingConnect(new InetSocketAddress("127.0.0.1", 8000));
		//		NettyRpcChannel channel = client.connect(Arrays.asList(
		//				new InetSocketAddress("59.151.117.236", 8000)
		//				new InetSocketAddress("59.151.117.232", 29163),
		//				new InetSocketAddress("59.151.117.233", 29163)
		//				));
		//		NettyRpcChannel channel = client.blockingConnect(new InetSocketAddress("59.151.117.236", 8000));
		com.zhuaiwa.api.SSSessionDomain.SSSessionDomainSvc.BlockingInterface sessionDomainClientSvc = SSSessionDomainSvc.newBlockingStub(channel);
		//		com.zhuaiwa.api.SSSessionDomain.SSSessionDomainSvc.Interface sessionDomainClientSvc = SSSessionDomainSvc.newStub(channel);
		try {
			LoginRequest.Builder req = LoginRequest.newBuilder().setUser(ssid).setPassword("7CC0F8222C26B474B6A525C92F92D35C");
			LoginRequest request = req.build();
			//			System.out.println("json request:" + JsonFormat.printToString(request));
			com.zhuaiwa.api.SSSessionDomain.LoginResponse resp = sessionDomainClientSvc.login(new NettyRpcController(),
					request);
			System.out.println("sid : " + resp.getSid());
			System.out.println(resp.hasFirstLogin());
			System.out.println("json response:" + JsonFormat.printToString(resp));

			try {
				sessionDomainClientSvc.follow(new NettyRpcController(), FollowRequest.newBuilder().addFollowingUseridList("5172caf958403e7ca854f0d360486fce").build());
			} catch (ServiceException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		} catch (ServiceException e) {
			e.printStackTrace();
		}
	}

}
