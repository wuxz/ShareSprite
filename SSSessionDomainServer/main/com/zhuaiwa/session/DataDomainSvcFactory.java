package com.zhuaiwa.session;

import java.net.InetSocketAddress;
import java.util.Arrays;
import java.util.concurrent.Executors;

import org.apache.commons.lang.StringUtils;
import org.jboss.netty.channel.socket.nio.NioClientSocketChannelFactory;

import com.zhuaiwa.api.Common.SSAccount;
import com.zhuaiwa.api.SSDataDomain.SSDataDomainSvc;
import com.zhuaiwa.api.SSDataDomain.SetAccountRequest;
import com.zhuaiwa.api.SSDataDomain.SSDataDomainSvc.BlockingInterface;
import com.zhuaiwa.api.SSDataDomain.SSDataDomainSvc.Interface;
import com.zhuaiwa.api.netty.NettyRpcController;
import com.zhuaiwa.api.netty.client.NettyRpcChannel;
import com.zhuaiwa.api.netty.client.NettyRpcClient;
import com.zhuaiwa.dd.protobuf.DataDomainApiProtobufExtension;
import com.zhuaiwa.util.PropertiesHelper;

public class DataDomainSvcFactory
{

	private static class BlockingInterfaceHolder
	{
		private static BlockingInterface dataDomainClientSvc;
		static
		{
			DataDomainApiProtobufExtension.register();
			NettyRpcClient client = new NettyRpcClient(
					new NioClientSocketChannelFactory(
							Executors.newCachedThreadPool(),
							Executors.newCachedThreadPool()));

			if (StringUtils.isNotEmpty(PropertiesHelper
					.getValue("data.domain.ip")))
			{
				String[] ips = PropertiesHelper.getValue("data.domain.ip")
						.split(" ");
				String[] ports = PropertiesHelper.getValue("data.domain.port")
						.split(" ");
				InetSocketAddress[] isas = new InetSocketAddress[ips.length];
				for (int i = 0; i < ips.length; i++)
				{
					isas[i] = new InetSocketAddress(ips[i],
							Integer.parseInt(ports[i]));
				}

				NettyRpcChannel channel = client.connect(Arrays.asList(isas));
				dataDomainClientSvc = SSDataDomainSvc.newBlockingStub(channel);
			}
		}
	}

	private static class InterfaceHolder
	{
		private static Interface dataDomainClientSvc;
		static
		{
		    DataDomainApiProtobufExtension.register();
			NettyRpcClient client = new NettyRpcClient(
					new NioClientSocketChannelFactory(
							Executors.newCachedThreadPool(),
							Executors.newCachedThreadPool()));
			// NettyRpcChannel channel = client.blockingConnect(new
			// InetSocketAddress("59.151.117.231", 29163));
			NettyRpcChannel channel = client.connect(Arrays.asList(
					new InetSocketAddress("p0", 29163), new InetSocketAddress(
							"p1", 29163), new InetSocketAddress("p2", 29163)));
			dataDomainClientSvc = SSDataDomainSvc.newStub(channel);
		}
	}

	public static BlockingInterface getBlockingService()
	{
		return BlockingInterfaceHolder.dataDomainClientSvc;
	}

	public static Interface getService()
	{
		return InterfaceHolder.dataDomainClientSvc;
	}

	public static void main(String[] args) throws Exception
	{
		BlockingInterface dataDomainClientSvc = getBlockingService();
		Thread.sleep(2000);
		SSAccount account = SSAccount.newBuilder().setIsActive(0).build();
		dataDomainClientSvc.setAccount(
				new NettyRpcController(),
				SetAccountRequest.newBuilder().setAccount(account)
						.setUserid("07299a7485d48ecc065e3407f75e468a").build());
		System.out.println("OK");
	}
}
