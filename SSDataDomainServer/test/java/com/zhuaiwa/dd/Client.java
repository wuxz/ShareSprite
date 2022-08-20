package com.zhuaiwa.dd;

import java.net.InetSocketAddress;
import java.util.concurrent.Executors;

import org.apache.log4j.BasicConfigurator;
import org.jboss.netty.channel.socket.nio.NioClientSocketChannelFactory;

import com.google.protobuf.ServiceException;
import com.zhuaiwa.api.SSDataDomain.GetAccountRequest;
import com.zhuaiwa.api.SSDataDomain.GetAccountResponse;
import com.zhuaiwa.api.SSDataDomain.SSDataDomainSvc;
import com.zhuaiwa.api.SSDataDomain.SSDataDomainSvc.BlockingInterface;
import com.zhuaiwa.api.netty.NettyRpcController;
import com.zhuaiwa.api.netty.client.NettyRpcChannel;
import com.zhuaiwa.api.netty.client.NettyRpcClient;

public class Client {

	public static void main(String[] args) throws ServiceException {
		
		BasicConfigurator.configure();
		
		NettyRpcClient client = new NettyRpcClient(
				new NioClientSocketChannelFactory(
						Executors.newCachedThreadPool(), 
						Executors.newCachedThreadPool()));
		
		NettyRpcChannel channel = client.blockingConnect(new InetSocketAddress("59.151.117.231", 29161));
		
		BlockingInterface svc = SSDataDomainSvc.newBlockingStub(channel);
//		Interface svc = SSDataDomainSvc.newStub(channel);
		
		// Get a new RpcController to use for this rpc call
		final NettyRpcController controller = channel.newRpcController();
		
		// Create the request
		GetAccountRequest request = GetAccountRequest
		.newBuilder()
		.addEmail("yaosw@channelsoft.com")
		.build();
		
		GetAccountResponse response = svc.getAccount(controller, request);
		System.out.println(controller.getCode());

//		svc.getAccount(controller, request, new RpcCallback<GetAccountResponse>() {
//			public void run(GetAccountResponse response) {
//				if (response != null) {
//					System.out.println("The answer is: " + response.getResult());
//				} else { 
//					System.out.println("Oops, there was an error: " + controller.errorText());
//				}
//			}
//		});
//		
//		try {
//			Thread.sleep(1000L);
//		} catch (InterruptedException e) {
//			// Ignore
//		}
		
//		FutureResponse<GetAccountResponse> response = new FutureResponse<GetAccountResponse>();
//		svc.getAccount(controller, request, response);
//		try {
//			System.out.println("The answer is: " + response.get().getResult());
//		} catch (InterruptedException e) {
//			e.printStackTrace();
//		} catch (ExecutionException e) {
//			e.printStackTrace();
//		}
		
		// Close the channel
		channel.close();
		
		// Close the client
		client.shutdown();
		
		System.out.println("Done!");
	}
	
}
