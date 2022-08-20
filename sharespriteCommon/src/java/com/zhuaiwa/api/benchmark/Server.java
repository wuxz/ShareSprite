package com.zhuaiwa.api.benchmark;

import java.net.InetSocketAddress;
import java.util.concurrent.Executors;

import org.jboss.netty.channel.socket.nio.NioServerSocketChannelFactory;

import com.google.protobuf.Service;
import com.zhuaiwa.api.Common;
import com.zhuaiwa.api.Common.TestSvc;
import com.zhuaiwa.api.netty.server.NettyRpcServer;
import com.zhuaiwa.api.util.ApiExtensionHelper;

public class Server
{

	public static void main(String[] args)
	{
		ApiExtensionHelper.registerProto(Common.getDescriptor(), Common.class);
		NettyRpcServer server = new NettyRpcServer(
				new NioServerSocketChannelFactory(
						Executors.newCachedThreadPool(),
						Executors.newCachedThreadPool()));
		Service service = TestSvc.newReflectiveService(new AsyncTestSvc());
		server.registerDefaultService(service);
		server.registerVersionService(service, 0x00010000);
		server.serve(new InetSocketAddress(9958));
	}
}
