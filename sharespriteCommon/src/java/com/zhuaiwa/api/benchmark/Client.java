package com.zhuaiwa.api.benchmark;

import java.net.InetSocketAddress;
import java.util.concurrent.BrokenBarrierException;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.CyclicBarrier;
import java.util.concurrent.Executors;
import java.util.concurrent.atomic.AtomicLong;

import org.jboss.netty.channel.socket.nio.NioClientSocketChannelFactory;

import com.google.protobuf.ByteString;
import com.google.protobuf.RpcCallback;
import com.google.protobuf.ServiceException;
import com.zhuaiwa.api.Common;
import com.zhuaiwa.api.Common.TestRequest;
import com.zhuaiwa.api.Common.TestResponse;
import com.zhuaiwa.api.Common.TestSvc;
import com.zhuaiwa.api.netty.NettyRpcController;
import com.zhuaiwa.api.netty.client.JMXEnabledClientRpcChannel;
import com.zhuaiwa.api.netty.client.NettyRpcChannel;
import com.zhuaiwa.api.netty.client.NettyRpcClient;
import com.zhuaiwa.api.util.ApiExtensionHelper;

public class Client
{
	public static void main(String[] args) throws Exception
	{

		ApiExtensionHelper.registerProto(Common.getDescriptor(), Common.class);
		NettyRpcClient client = new NettyRpcClient(
				new NioClientSocketChannelFactory(
						Executors.newCachedThreadPool(),
						Executors.newCachedThreadPool()));
		NettyRpcChannel channel = client.blockingConnect(new InetSocketAddress(
				"127.0.0.1", 9958));
		final AtomicLong count = new AtomicLong();
		final long begintime = System.currentTimeMillis() + 15000;
		final long endtime = begintime + 30000;
		final String mode = args[0];
		final int concurrents = Integer.parseInt(args[1]);

		System.out.println("mode : " + mode);
		System.out.println("concurrents : " + concurrents);

		final ByteString payload = ByteString.copyFrom(new byte[0]);

		if (mode.equalsIgnoreCase("async"))
		{
			final TestSvc.Interface async = TestSvc.newStub(channel);
			while (System.currentTimeMillis() < endtime)
			{
				async.test(new NettyRpcController(), TestRequest.newBuilder()
						.setPayload(payload).build(),
						new RpcCallback<Common.TestResponse>()
						{
							@Override
							public void run(TestResponse response)
							{
								long now = System.currentTimeMillis();
								if (now >= endtime)
								{
									return;
								}
								if (now >= begintime)
								{
									count.incrementAndGet();
								}
							}
						});
				break;
			}
		}
		else if (mode.equalsIgnoreCase("sync"))
		{
			final TestSvc.BlockingInterface sync = TestSvc
					.newBlockingStub(channel);
			final CyclicBarrier barrier = new CyclicBarrier(concurrents);
			final CountDownLatch latch = new CountDownLatch(concurrents);
			for (int i = 0; i < concurrents; i++)
			{
				new Thread()
				{
					@Override
					public void run()
					{
						try
						{
							barrier.await();
						}
						catch (InterruptedException e)
						{
							e.printStackTrace();
						}
						catch (BrokenBarrierException e)
						{
							e.printStackTrace();
						}

						while (true)
						{
							try
							{
								sync.test(new NettyRpcController(), TestRequest
										.newBuilder().setPayload(payload)
										.build());
							}
							catch (ServiceException e)
							{
								e.printStackTrace();
							}
							long now = System.currentTimeMillis();
							if (now >= endtime)
							{
								break;
							}
							if (now >= begintime)
							{
								count.incrementAndGet();
							}
						}

						latch.countDown();
					};
				}.start();
			}
			latch.await();
		}

		System.out.println("tps:" + (count.get() / 30));

		for (long i = ((JMXEnabledClientRpcChannel) channel.getChannel())
				.getPendingCount(); i > 0; i = ((JMXEnabledClientRpcChannel) channel
				.getChannel()).getPendingCount())
		{
			Thread.sleep(1000);
			if (i == ((JMXEnabledClientRpcChannel) channel.getChannel())
					.getPendingCount())
			{
				break;
			}
		}
		channel.close();
		client.shutdown();
	}
}
