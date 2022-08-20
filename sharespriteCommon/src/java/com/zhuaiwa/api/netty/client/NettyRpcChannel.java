package com.zhuaiwa.api.netty.client;

import java.net.InetSocketAddress;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.List;
import java.util.concurrent.TimeoutException;

import org.jboss.netty.bootstrap.ClientBootstrap;
import org.jboss.netty.channel.ChannelHandlerContext;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.google.protobuf.BlockingRpcChannel;
import com.google.protobuf.Descriptors.MethodDescriptor;
import com.google.protobuf.Message;
import com.google.protobuf.RpcCallback;
import com.google.protobuf.RpcChannel;
import com.google.protobuf.RpcController;
import com.google.protobuf.ServiceException;
import com.zhuaiwa.api.netty.NettyRpcController;

/**
 * <p>
 * 负责维护多个连接，将RPC路由到可使用的连接上。具有连接检测、重连和负载均衡功能。 <br/>
 * <p>
 * 需要注意的是：
 * <li>当提供的集群地址列表超过3个时，只随机选取3个建立连接。
 * <li>当连接断开或连接上的未完成rpc个数超过阈值后负载均衡才发生作用
 * <li>负载均衡策略是寻找下一个未超过阈值的连接或选取其中未完成rpc个数最少的连接
 * 
 * @author yaosw
 */
public class NettyRpcChannel implements RpcChannel, BlockingRpcChannel
{
	private static final Logger logger = LoggerFactory
			.getLogger(NettyRpcChannel.class);

	private List<InetSocketAddress> addresses = new ArrayList<InetSocketAddress>();

	private List<JMXEnabledClientRpcChannel> channels = new ArrayList<JMXEnabledClientRpcChannel>();

	private volatile JMXEnabledClientRpcChannel current = null;

	private long pendingThreshold = 64;

	public NettyRpcChannel(ClientBootstrap bootstrap,
			Collection<InetSocketAddress> addresses)
	{
		this(bootstrap, addresses, null);
	}

	/**
	 * 当集群地址列表超过3个时，只随机选取3个建立连接。
	 * 
	 * @param bootstrap
	 * @param addresses
	 *            集群节点的地址列表
	 */
	public NettyRpcChannel(ClientBootstrap bootstrap,
			Collection<InetSocketAddress> addresses,
			ConnectionStatusListener connectionStatusListener)
	{
		if ((addresses == null) || (addresses.size() == 0))
		{
			throw new IllegalArgumentException("addresses must be non-empty.");
		}

		//
		// 将列表顺序随机打乱，这样可以均衡多个客户端的调用
		//
		this.addresses.addAll(addresses);
		Collections.shuffle(this.addresses);

		//
		// 最多选3台服务器作为负载均衡和备用
		//
		int count = 0;
		for (InetSocketAddress address : this.addresses)
		{
			JMXEnabledClientRpcChannel channel = new JMXEnabledClientRpcChannel(
					bootstrap, address, connectionStatusListener);
			channels.add(channel);
			if (++count >= 3)
			{
				return;
			}
		}
	}

	public ClientRpcChannel awaitConnected()
	{
		while (true)
		{
			try
			{
				return awaitConnected(1000);
			}
			catch (TimeoutException e)
			{
				// ignore
			}
		}
	}

	public ClientRpcChannel awaitConnected(long timeout)
			throws TimeoutException
	{
		long end = System.currentTimeMillis() + timeout;
		while (true)
		{
			long start = System.currentTimeMillis();

			long left = end - start;
			if (left <= 0)
			{
				throw new TimeoutException();
			}

			long await = Math.min(Math.max(1, left / channels.size()), 100);
			for (JMXEnabledClientRpcChannel channel : channels)
			{
				channel.awaitConnected(await);
				if (isAlive(channel))
				{
					return channel;
				}
			}

			await = (start + await) - System.currentTimeMillis();
			if (await > 0)
			{
				try
				{
					Thread.sleep(await);
				}
				catch (InterruptedException e)
				{
					// ignore
				}
			}
		}
	}

	@Override
	public Message callBlockingMethod(MethodDescriptor method,
			RpcController controller, Message request, Message responsePrototype)
			throws ServiceException
	{
		Object attachment = null;
		ChannelHandlerContext context = ((NettyRpcController) controller)
				.getChannelHandlerContext();
		if (context != null)
		{
			attachment = context.getAttachment();
		}
		ClientRpcChannel channel = getChannel((JMXEnabledClientRpcChannel) attachment);
		if (channel == null)
		{
			controller.setFailed("Network is disconnected.");
			throw new ServiceException("Network is disconnected.");
		}
		return channel.callBlockingMethod(method, controller, request,
				responsePrototype);
	}

	@Override
	public void callMethod(MethodDescriptor method, RpcController controller,
			Message request, Message responsePrototype,
			RpcCallback<Message> done)
	{
		ClientRpcChannel channel = getChannel();
		if (channel == null)
		{
			controller.setFailed("Network is disconnected.");
			throw new RuntimeException("Network is disconnected.");
		}
		channel.callMethod(method, controller, request, responsePrototype, done);
	}

	public void close()
	{
		for (ClientRpcChannel channel : channels)
		{
			channel.close();
		}
	}

	public ClientRpcChannel getChannel()
	{
		return getChannel(null);
	}

	public ClientRpcChannel getChannel(JMXEnabledClientRpcChannel hint)
	{
		if (hint != null)
		{
			return isAlive(hint) ? routeTo(hint) : null;
		}

		long minPendingCount = Long.MAX_VALUE;
		JMXEnabledClientRpcChannel best = null;
		if (current == null)
		{
			for (JMXEnabledClientRpcChannel channel : channels)
			{
				if (isAlive(channel))
				{
					return routeTo(channel);
				}
			}
			return null;
		}

		//
		// 如果当前通道积累的未完成RPC超过阈值（10个），则尝试其他通道。
		//
		long pendingCount = current.getPendingCount();
		if (isAlive(current))
		{
			if (pendingCount < pendingThreshold)
			{
				return routeTo(current);
			}
			else
			{
				minPendingCount = pendingCount;
				best = current;
			}
		}

		//
		// 负载均衡的策略：
		// 从所有通道里挑选找到的第一个未完成RPC个数小于阈值的通道，如果不存在这样的通道，
		// 则挑选未完成RPC个数最小的通道。为了公平，从当前通道的下一个通道开始尝试，再绕
		// 回到列表前面
		//
		int i, j;
		i = j = channels.indexOf(current);
		while (((++i) % channels.size()) != j)
		{
			JMXEnabledClientRpcChannel channel = channels.get(i
					% channels.size());
			if (isAlive(channel))
			{
				pendingCount = channel.getPendingCount();
				if (pendingCount < pendingThreshold)
				{
					return routeTo(channel);
				}
				else if (pendingCount < minPendingCount)
				{
					minPendingCount = pendingCount;
					best = channel;
				}
			}
		}

		if (best != null)
		{
			return routeTo(best);
		}

		return null;
	}

	public ClientRpcChannel getChannelBlocking()
	{
		ClientRpcChannel channel = getChannel();
		if (channel != null)
		{
			return channel;
		}

		//
		// 到这里表明channels全部是断开的
		//
		channel = awaitConnected();
		return routeTo((JMXEnabledClientRpcChannel) channel);
	}

	public long getPendingThreshold()
	{
		return pendingThreshold;
	}

	public long getTimeout()
	{
		for (ClientRpcChannel channel : channels)
		{
			return channel.getTimeout();
		}
		return 0;
	}

	private boolean isAlive(JMXEnabledClientRpcChannel channel)
	{
		return ((channel != null) && !channel.isClosed() && channel.isEnabled() && channel
				.isConnected());
	}

	public boolean isClosed()
	{
		for (ClientRpcChannel channel : channels)
		{
			if (!channel.isClosed())
			{
				return false;
			}
		}
		return true;
	}

	public boolean isConnected()
	{
		for (ClientRpcChannel channel : channels)
		{
			if (!channel.isClosed() && channel.isConnected())
			{
				return true;
			}
		}
		return false;
	}

	public NettyRpcController newRpcController()
	{
		return new NettyRpcController();
	}

	private ClientRpcChannel routeTo(JMXEnabledClientRpcChannel channel)
	{
		if (channel != current)
		{
			logger.info("route rpc to " + channel.getRemoteAddress().toString());
			if (current != null)
			{
				current.setUsed(false);
			}
			current = channel;
			current.setUsed(true);
		}
		return channel;
	}

	public void setPendingThreshold(long pendingThreshold)
	{
		this.pendingThreshold = pendingThreshold;
	}

	public void setTimeout(long timeout)
	{
		for (ClientRpcChannel channel : channels)
		{
			channel.setTimeout(timeout);
		}
	}
}
