package com.zhuaiwa.session.service;

import java.util.ArrayList;
import java.util.List;

import org.apache.commons.pool.impl.GenericObjectPool.Config;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import redis.clients.jedis.JedisShardInfo;
import redis.clients.jedis.ShardedJedis;
import redis.clients.jedis.ShardedJedisPool;
import redis.clients.util.Pool;

import com.zhuaiwa.api.Rpc.GetFollowingRequest;
import com.zhuaiwa.api.Rpc.GetFollowingResponse;
import com.zhuaiwa.api.netty.NettyRpcController;
import com.zhuaiwa.util.PropertiesHelper;

public abstract class BaseService
{
	private static Logger LOG = LoggerFactory.getLogger(BaseService.class);

	static Pool<ShardedJedis> pool = null;

	static boolean redisDebugMode = "true".equalsIgnoreCase(PropertiesHelper
			.getValue("redis.debugmode"));

	static List<JedisShardInfo> shards = new ArrayList<JedisShardInfo>();

	static
	{
		if (LOG.isDebugEnabled())
		{
			LOG.debug("start init sharded jedis pool...");
		}
		String[] ips = PropertiesHelper
				.getValue("redis.domain.ip", "127.0.0.1").split(" ");
		String[] ports = PropertiesHelper.getValue("redis.domain.port", "6379")
				.split(" ");

		for (int i = 0; i < ips.length; i++)
		{
			shards.add(new JedisShardInfo(ips[i], Integer.parseInt(ports[i])));
		}

		Config cf = new Config();
		cf.minEvictableIdleTimeMillis = -1;
		cf.testOnBorrow = true;
		pool = new ShardedJedisPool(cf, shards);
		if (LOG.isDebugEnabled())
		{
			LOG.debug("init sharded jedis pool ok.");
		}
	}

	public static String getFollowingKey(String userid)
	{
		return "00" + userid;
	}

	public static String getPublicShareKey(String userid)
	{
		// return KEY_PREFIX.PSS_.name() + userid;
		return "01" + userid;
	}

	/**
	 * 初始化following userid set
	 * 
	 * @param userId
	 * @param refresh
	 *            是否强制刷新
	 */
	static private void initFollowingSet(UserService_v_1_0 userService,
			NettyRpcController controller, String userId, boolean refresh)
	{
		boolean exists = false;
		ShardedJedis sj = null;
		try
		{
			sj = pool.getResource();

			exists = sj.exists(getFollowingKey(userId));
		}
		catch (Throwable e)
		{
			LOG.error("", e);
			sj = null;
		}
		finally
		{
			if (sj != null)
			{
				pool.returnResource(sj);
			}
		}

		if (!refresh && exists)
		{
			return;
		}
		if (exists)
		{
			try
			{
				sj = pool.getResource();
				sj.expireAt(getFollowingKey(userId), 0);
			}
			catch (Throwable e)
			{
				sj = null;
			}
			finally
			{
				if (sj != null)
				{
					pool.returnResource(sj);
				}
			}
		}

		GetFollowingResponse resp = userService.getFollowing(controller,
				GetFollowingRequest.newBuilder().setUserid(userId).build());
		List<String> following_userids = resp.getFollowingUseridListList();
		double score = Integer.MAX_VALUE;
		for (String following_userid : following_userids)
		{
			try
			{
				sj = pool.getResource();
				sj.zadd(getFollowingKey(userId), score--, following_userid);
			}
			catch (Throwable e)
			{
				sj = null;
			}
			finally
			{
				if (sj != null)
				{
					pool.returnResource(sj);
				}
			}
		}
	}

	static void onFollow(UserService_v_1_0 userService,
			NettyRpcController controller, String userId, String followingUserId)
	{
		initFollowingSet(userService, controller, userId, false);

		ShardedJedis sj = null;
		try
		{
			sj = pool.getResource();

			sj.zadd(getFollowingKey(userId), System.currentTimeMillis(),
					followingUserId);

			LOG.debug("follow in jedis. userId=" + userId
					+ ", followingUserId=" + followingUserId);
		}
		catch (Throwable e)
		{
			LOG.error("", e);
			sj = null;
		}
		finally
		{
			if (sj != null)
			{
				pool.returnResource(sj);
			}
		}
	}

	static void onUnfollow(UserService_v_1_0 userService,
			NettyRpcController controller, String userId, String followingUserId)
	{
		initFollowingSet(userService, controller, userId, false);

		ShardedJedis sj = null;
		try
		{
			sj = pool.getResource();

			sj.zrem(getFollowingKey(userId), followingUserId);
			LOG.debug("unfollow in jedis. userId=" + userId
					+ ", followingUserId=" + followingUserId);
		}
		catch (Throwable e)
		{
			LOG.error("", e);
			sj = null;
		}
		finally
		{
			if (sj != null)
			{
				pool.returnResource(sj);
			}
		}
	}

	protected void failed(NettyRpcController controller, int code, String reason)
	{
		controller.setCode(code);
		controller.setFailed(reason);
		LOG.debug(reason + ",code=" + code);
	}

	protected void failed(NettyRpcController controller, int code,
			String reason, Throwable e)
	{
		controller.setCode(code);
		controller.setFailed(reason);
		LOG.debug(reason + ",code=" + code, e);
	}

	protected NettyRpcController getRpcController()
	{
		return new NettyRpcController();
	}
}
