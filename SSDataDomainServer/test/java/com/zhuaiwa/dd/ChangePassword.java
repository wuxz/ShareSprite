package com.zhuaiwa.dd;

import java.net.InetSocketAddress;
import java.util.concurrent.Executors;

import org.jboss.netty.channel.socket.nio.NioClientSocketChannelFactory;

import com.zhuaiwa.api.Common.SSAccount;
import com.zhuaiwa.api.SSDataDomain.GetAccountRequest;
import com.zhuaiwa.api.SSDataDomain.GetAccountResponse;
import com.zhuaiwa.api.SSDataDomain.SSDataDomainSvc;
import com.zhuaiwa.api.SSDataDomain.SSDataDomainSvc.BlockingInterface;
import com.zhuaiwa.api.SSDataDomain.SetAccountRequest;
import com.zhuaiwa.api.SSDataDomain.SetAccountResponse;
import com.zhuaiwa.api.netty.NettyRpcController;
import com.zhuaiwa.api.netty.client.NettyRpcChannel;
import com.zhuaiwa.api.netty.client.NettyRpcClient;

public class ChangePassword {
	public static void main(String[] args) throws Exception {
	NettyRpcClient client = new NettyRpcClient(
			new NioClientSocketChannelFactory(
					Executors.newCachedThreadPool(),
					Executors.newCachedThreadPool()));
	
	NettyRpcChannel channel = client.blockingConnect(new InetSocketAddress("59.151.117.231", 29161));
	
	BlockingInterface svc = SSDataDomainSvc.newBlockingStub(channel);
//	Interface svc = SSDataDomainSvc.newStub(channel);
	
	// Get a new RpcController to use for this rpc call
	NettyRpcController controller = channel.newRpcController();
	GetAccountRequest getAccountRequest = GetAccountRequest
	.newBuilder()
	//.addPhoneNumber("13301202666")
	.addEmail("tangjun@channelsoft.com")
	.build();
	GetAccountResponse getAccountResponse = svc.getAccount(controller, getAccountRequest);
	String userId = getAccountResponse.getAccount(0).getUserid();
	
	// Create the request
	SetAccountRequest changePasswordRequest = SetAccountRequest
	.newBuilder()
	.setUserid(userId)
	.setAccount(SSAccount.newBuilder()
			.setUserid(userId)
			.setPassword("7CC0F8222C26B474B6A525C92F92D35C")
			)
	.build();
	
	controller = channel.newRpcController();
	SetAccountResponse changePasswordResponse = svc.setAccount(controller, changePasswordRequest);
	System.out.println(controller.getCode());
	
	// Close the channel
	channel.close();
	
	// Close the client
	client.shutdown();
	
	System.out.println("Done!");
		
	}
}
