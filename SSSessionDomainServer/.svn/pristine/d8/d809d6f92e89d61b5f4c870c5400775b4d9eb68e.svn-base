package com.zhuaiwa.session;

import java.util.Collection;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.UUID;
import java.util.concurrent.ConcurrentHashMap;

import org.jboss.netty.channel.Channel;
import org.jboss.netty.channel.ChannelFuture;
import org.jboss.netty.channel.ChannelFutureListener;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.channelsoft.sharesprite.pubsub.client.PubSubClient;
import com.channelsoft.sharesprite.pubsub.client.jedis.JedisClient;
import com.zhuaiwa.api.Common.SSAccount;
import com.zhuaiwa.session.PubSubManager.TopicType;
import com.zhuaiwa.util.PropertiesHelper;

/**
 * 会话管理
 * 
 * @author tangjun
 */
public class SessionManager implements SessionManagerMBean
{
	public class HttpSessionCleaner implements Runnable
	{

		@Override
		public void run()
		{
			try
			{
				Thread.sleep(5000);
			}
			catch (InterruptedException e1)
			{
				LOG.error("", e1);
			}

			LOG.info("http session清理线程已启动。");

			while (true)
			{
				LOG.info("开始清理http session。");

				if (!type.equals(SessionType.HTTP))
				{
					LOG.info("非HTTP会话模式，停止http session 超时清理线程");
					break;
				}
				try
				{
					for (Session session : sessionMap.values())
					{
						if (session.type.equals(SessionType.HTTP))
						{
							if ((System.currentTimeMillis() - session.lastActiveTime) > HTTP_SESSION_TIMEOUT)
							{
								LOG.info("清理http session，sid=" + session.sid);
								destorySession(session);
							}
						}
					}

					Thread.sleep(30 * 1000L);
				}
				catch (InterruptedException e)
				{
					LOG.error("http session clean error", e);
				}
			}
		}
	}

	public static class Session implements java.lang.Comparable<Session>
	{
		public boolean active;

		public Channel channel;

		public long lastActiveTime;// 上一次动作时间

		public long loginTime;

		public String sid;

		public Map<String, Long> topics;// 订阅的主题

		public SessionType type;

		public String userid;

		public Session(String userid, Channel channel, SessionType type)
		{
			this.sid = UUID.randomUUID().toString();
			this.userid = userid;
			this.loginTime = System.currentTimeMillis();
			this.channel = channel;
			active = true;
			this.topics = new ConcurrentHashMap<String, Long>();
			lastActiveTime = System.currentTimeMillis();
		}

		@Override
		public int compareTo(Session o)
		{
			return this.sid.compareTo(o.sid);
		}

		@Override
		public boolean equals(Object obj)
		{
			if (this == obj)
			{
				return true;
			}
			if (obj == null)
			{
				return false;
			}
			if (getClass() != obj.getClass())
			{
				return false;
			}
			Session other = (Session) obj;
			if (sid == null)
			{
				if (other.sid != null)
				{
					return false;
				}
			}
			else if (!sid.equals(other.sid))
			{
				return false;
			}
			return true;
		}

		@Override
		public int hashCode()
		{
			final int prime = 31;
			int result = 1;
			result = (prime * result) + ((sid == null) ? 0 : sid.hashCode());
			return result;
		}

		public void subscribe(TopicType topicPrefix, List<String> userids)
		{
			for (String userid : userids)
			{
				subscribe(topicPrefix, userid);
			}
		}

		public void subscribe(TopicType topicPrefix, String userid)
		{
			PubSubManager.getInstance().subscribe(topicPrefix, userid, this);
			this.topics.put(PubSubUtil.getTopicId(topicPrefix, userid),
					System.currentTimeMillis());
		}

		@Override
		public String toString()
		{
			return "Session [sid=" + sid + ", userid=" + userid
					+ ", loginTime=" + loginTime + ", channel=" + channel
					+ ", active=" + active + ", type="
					+ SessionManager.getInstance().type + ", topics size ="
					+ topics.size() + "]";
		}

		public void unSubscribe(TopicType topicPrefix, List<String> userids)
		{
			for (String userid : userids)
			{
				unSubscribe(topicPrefix, userid);
			}
		}

		public void unSubscribe(TopicType topicPrefix, String userid)
		{
			String topicId = PubSubUtil.getTopicId(topicPrefix, userid);
			PubSubManager.getInstance().unsubscribe(topicId, this);
			this.topics.remove(topicId);
		}

		public void unSubscribeAll()
		{
			for (String topic : topics.keySet())
			{
				PubSubManager.getInstance().unsubscribe(topic, this);
			}
			this.topics.clear();
		}

	}

	static class SessionManagerHolder
	{
		static SessionManager instance = new SessionManager();
	}

	public static enum SessionType
	{
		HTTP, TCP
	}

	public static long HTTP_SESSION_TIMEOUT = 30 * 60 * 1000L;

	private static Logger LOG = LoggerFactory.getLogger(SessionManager.class);

	private static PubSubClient pubsubClient = null;

	public static SessionManager getInstance()
	{
		return SessionManagerHolder.instance;
	}

	// channel->map<sid,Session>
	private final ConcurrentHashMap<Channel, ConcurrentHashMap<String, Session>> channelMap = new ConcurrentHashMap<Channel, ConcurrentHashMap<String, Session>>();

	// sid->Session
	private final ConcurrentHashMap<String, Session> sessionMap = new ConcurrentHashMap<String, Session>();

	public SessionType type = SessionType.TCP;

	private SessionManager()
	{
		pubsubClient = new JedisClient();
		try
		{
			pubsubClient.initialize(PropertiesHelper
					.getValue("pubsub.redis.ip") + ":6379");
			LOG.info("init pubsub client ok.");
		}
		catch (Exception e)
		{
			LOG.error("init pubsub client error.", e);
		}

		// 启动HTTP会话超时清理
		Thread cleaner = new Thread(new HttpSessionCleaner());
		cleaner.setName("Http Session cleaner");
		cleaner.setDaemon(true);
		cleaner.start();
	}

	public void closeChannel(Channel channel)
	{
		Collection<Session> sessions = null;
		ConcurrentHashMap<String, Session> map = channelMap.get(channel);
		if (map != null)
		{
			sessions = map.values();
		}
		channelMap.remove(channel);
		if (sessions != null)
		{
			for (Session session : sessions)
			{
				session.active = false;
				session.unSubscribeAll();
				sessionMap.remove(session.sid);
			}
		}
	}

	public void destorySession(Session session)
	{
		session.active = false;
		session.unSubscribeAll();
		sessionMap.remove(session.sid);
		ConcurrentHashMap<String, Session> map = channelMap
				.get(session.channel);
		if (map != null)
		{
			map.remove(session.sid);
		}

		// 当channel上的session为空，channel也要清理掉
		if (map.size() == 0)
		{
			channelMap.remove(session.channel);
		}

		try
		{
			pubsubClient.setValue("presence_" + session.userid, "0");
		}
		catch (Exception e)
		{
			LOG.warn("failed to set userid:" + session.userid + " offline.", e);
		}
		if (LOG.isDebugEnabled())
		{
			LOG.debug(session.toString());
		}
	}

	public void destroySession(String sid)
	{
		Session session = getSession(sid);
		if (session != null)
		{
			destorySession(session);
		}
	}

	@Override
	public SSAccount getAccountBySid(String sid)
	{
		Session session = getSession(sid);
		if (session == null)
		{
			return null;
		}

		return null;
	}

	@Override
	public int getChannels()
	{
		return channelMap.size();
	}

	@Override
	public Set<Session> getLoginSessions()
	{
		return null;
	}

	@Override
	public String getLoginUsers()
	{
		StringBuffer sb = new StringBuffer();
		for (String key : sessionMap.keySet())
		{
			sb.append(sessionMap.get(key).toString() + "\r\n");
		}
		return sb.toString();
	}

	public Session getSession(String sid)
	{
		return sessionMap.get(sid);
	}

	@Override
	public int getSessions()
	{
		return sessionMap.size();
	}

	public Session newSession(String userid, Channel channel, SessionType type)
	{
		Session session = new Session(userid, channel, type);
		sessionMap.put(session.sid, session);

		ConcurrentHashMap<String, Session> map = channelMap.get(channel);
		if (map == null)
		{
			ConcurrentHashMap<String, Session> alt = new ConcurrentHashMap<String, Session>();
			map = channelMap.putIfAbsent(channel, alt);
			if (map == null)
			{
				map = alt;
			}
			if (type.equals(SessionType.TCP))
			{
				// 当channel断开，取消订阅
				channel.getCloseFuture().addListener(
						new ChannelFutureListener()
						{
							@Override
							public void operationComplete(ChannelFuture future)
									throws Exception
							{
								if (LOG.isDebugEnabled())
								{
									LOG.debug("closing channel "
											+ future.getChannel());
								}
								closeChannel(future.getChannel());
								if (LOG.isDebugEnabled())
								{
									LOG.debug("channel " + future.getChannel()
											+ " closed.");
								}
							}
						});
			}
		}
		map.put(session.sid, session);
		if (LOG.isDebugEnabled())
		{
			LOG.debug(session.toString());
		}
		try
		{
			pubsubClient.setValue("presence_" + userid, "1");
		}
		catch (Exception e)
		{
			LOG.warn("failed to set userid:" + userid + " online.", e);
		}
		return session;
	}

	public boolean sessionExist(String sid)
	{
		return sessionMap.containsKey(sid);
	}

	@Override
	public void setMaxSession(int max)
	{

	}
}
