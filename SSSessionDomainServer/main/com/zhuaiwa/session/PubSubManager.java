package com.zhuaiwa.session;

import java.util.List;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentSkipListSet;
import java.util.concurrent.LinkedBlockingQueue;
import java.util.concurrent.ThreadPoolExecutor;
import java.util.concurrent.TimeUnit;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.channelsoft.sharesprite.pubsub.client.Message;
import com.channelsoft.sharesprite.pubsub.client.PubSubClient;
import com.channelsoft.sharesprite.pubsub.client.jedis.JedisClient;
import com.google.protobuf.ServiceException;
import com.zhuaiwa.api.Common;
import com.zhuaiwa.api.Common.ApiHeader;
import com.zhuaiwa.api.Common.ApiMessage;
import com.zhuaiwa.api.Common.ApiNotification;
import com.zhuaiwa.api.Common.ApiType;
import com.zhuaiwa.api.Common.SSBOX;
import com.zhuaiwa.api.SSDataDomain.SSDataDomainSvc.BlockingInterface;
import com.zhuaiwa.api.SSSessionDomain.OnMessageNotification;
import com.zhuaiwa.api.netty.NettyRpcController;
import com.zhuaiwa.api.util.ApiExtensionHelper;
import com.zhuaiwa.dd.util.MessageUtils;
import com.zhuaiwa.session.SessionManager.Session;
import com.zhuaiwa.util.PropertiesHelper;

/**
 * 发布/订阅管理
 * 
 * @author tangjun
 */
public class PubSubManager implements PubSubManagerMBean
{
	/**
	 * pub/sub客户端收消息轮询器
	 * 
	 * @author tangjun
	 */
	public static class MessagePuller implements Runnable
	{

		@Override
		public void run()
		{
			while (running)
			{
				try
				{
					Message msg = pubsubClient.pollMessage();
					if (msg != null)
					{
						if (msg.topic.equals("__ping__"))
						{
							continue;
						}
						if (LOG.isDebugEnabled())
						{
							LOG.debug("msg from redis " + msg);
						}
						int msgType = msg.type;
						Topic topic;
						switch (msgType)
						{
						case PubSubClient.MESSAGE_TYPE_MESSAGE:
							PubSubManager.getInstance().onMessage(msg.topic,
									msg.body);
							break;
						case PubSubClient.MESSAGE_TYPE_SERVERFAIL:
							topic = PubSubManager.getInstance().getTopic(
									msg.topic);
							if (topic != null)
							{
								topic.status = TopicStatus.Offline;
							}
							break;
						case PubSubClient.MESSAGE_TYPE_SUBSCRIBE:
							topic = PubSubManager.getInstance().getTopic(
									msg.topic);
							if (topic != null)
							{
								topic.status = TopicStatus.Online;
							}
							break;
						case PubSubClient.MESSAGE_TYPE_UNSUBSCRIBE:
							break;
						default:
							break;
						}
					}
					else
					{
						Thread.sleep(1L);
					}
				}
				catch (Exception e)
				{
					e.printStackTrace();
				}

			}

		}
	}

	static class SubscriptionManagerHolder
	{
		static PubSubManager instance = new PubSubManager();
	}

	public static class Topic
	{
		public String id;// 同一类型范围内唯一标识

		public long lastPollTimestamp;

		public long latestMsgTimestamp;

		public TopicStatus status;

		public ConcurrentSkipListSet<Session> subscriptions;

		public String topicId;// 全局唯一标识

		public TopicType topicType;

		public Topic(TopicType topicType, String tid)
		{
			this.topicType = topicType;
			this.id = tid;
			this.topicId = PubSubUtil.getTopicId(topicType, id);
			this.status = TopicStatus.Offline;
			this.latestMsgTimestamp = System.currentTimeMillis();
			this.lastPollTimestamp = 0;
			this.subscriptions = new ConcurrentSkipListSet<SessionManager.Session>();
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
			Topic other = (Topic) obj;
			if (topicId == null)
			{
				if (other.topicId != null)
				{
					return false;
				}
			}
			else if (!topicId.equals(other.topicId))
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
			result = (prime * result)
					+ ((topicId == null) ? 0 : topicId.hashCode());
			return result;
		}

		@Override
		public String toString()
		{
			return "Topic [topicId=" + topicId + ", status=" + status
					+ ", latestMsgTimestamp=" + latestMsgTimestamp
					+ ", lastPollTimestamp=" + lastPollTimestamp
					+ ", subscriptions size =" + subscriptions.size() + "]";
		}

	}

	public static enum TopicStatus
	{
		Offline, Online
	}

	/**
	 * 检查Topic的向redis的订阅状态
	 * 
	 * @author tangjun
	 */
	public static class TopicStatusChecker implements Runnable
	{

		@Override
		public void run()
		{
			while (running)
			{
				try
				{
					for (final Topic topic : PubSubManager.getInstance().topicMap
							.values())
					{
						if ((topic.topicType.equals(TopicType.PS_) || topic.topicType
								.equals(TopicType.RS_))
								&& topic.status.equals(TopicStatus.Offline))
						{
							if (LOG.isDebugEnabled())
							{
								LOG.debug("topic is offline, topic=" + topic);
							}
							// TODO 注意提交任务的节奏，以防止redis不可用给数据域的访问带到瞬间的压力
							pollMsgExecutor.execute(new Runnable()
							{

								@Override
								public void run()
								{
									if (LOG.isDebugEnabled())
									{
										LOG.debug("starting pull msg from data domain, topic="
												+ topic.toString());
									}
									// 查询
									com.zhuaiwa.api.Common.SSBOX box = null;
									if (topic.topicType.equals(TopicType.PS_))
									{
										box = SSBOX.PUBBOX;
									}
									else if (topic.topicType
											.equals(TopicType.RS_))
									{
										box = SSBOX.INBOX;
									}
									else
									{
										return;
									}
									try
									{
										com.zhuaiwa.api.Rpc.GetMessageByTimestampRequest req = com.zhuaiwa.api.Rpc.GetMessageByTimestampRequest
												.newBuilder()
												.setUserid(topic.id)
												.setMode(0)
												.setSourceBox(box)
												.setCount(Integer.MAX_VALUE)
												.setStartTimestamp(
														topic.latestMsgTimestamp)
												.setEndTimestamp(
														System.currentTimeMillis())
												.build();
										com.zhuaiwa.api.Rpc.GetMessageByTimestampResponse resp = dataDomainClientSvc
												.getMessageByTimestamp(
														new NettyRpcController(),
														req);
										topic.lastPollTimestamp = System
												.currentTimeMillis();
										List<String> msgIds = resp
												.getMsgidListList(); // 队列已经是按照时间排序的了
										if ((msgIds != null)
												&& (msgIds.size() > 0))
										{
											if (LOG.isDebugEnabled())
											{
												LOG.debug("pool "
														+ msgIds.size()
														+ " items from data domain.");
											}
											String lastestMsgId = msgIds
													.get(msgIds.size() - 1);
											topic.latestMsgTimestamp = MessageUtils
													.getMessageTimestamp(lastestMsgId);
											for (String msgId : msgIds)
											{
												PubSubManager.getInstance()
														.onMessage(
																topic.topicId,
																msgId);
											}
										}
									}
									catch (ServiceException e)
									{
										LOG.warn(
												"pull public share has error.",
												e);
									}
									if (LOG.isDebugEnabled())
									{
										LOG.debug("pull msg from data domain finished, Topic="
												+ topic.topicId);
									}
								}
							});
						}
					}
					Thread.sleep(30 * 1000L);
				}
				catch (InterruptedException e)
				{
					LOG.error("check topic status has error.", e);
				}
			}
		}
	}

	/*
	 * UN->未知 ；PS->公开分享 ；PP->在线状态； RS->指定分享；
	 */
	public enum TopicType
	{
		PP_, PS_, RS_, UN_
	}

	private static BlockingInterface dataDomainClientSvc = null;

	private static Logger LOG = LoggerFactory.getLogger(PubSubManager.class);

	// 拽消息线程池
	protected static ThreadPoolExecutor pollMsgExecutor = new ThreadPoolExecutor(
			30, 30, 60L, TimeUnit.SECONDS, new LinkedBlockingQueue<Runnable>(),
			new ThreadPoolExecutor.DiscardOldestPolicy());

	private static PubSubClient pubsubClient = null;

	// 推送消息线程池
	protected static ThreadPoolExecutor pushMsgExecutor = new ThreadPoolExecutor(
			30, 30, 60L, TimeUnit.SECONDS, new LinkedBlockingQueue<Runnable>(),
			new ThreadPoolExecutor.CallerRunsPolicy());

	private static boolean running = false;

	public static PubSubManager getInstance()
	{
		return SubscriptionManagerHolder.instance;
	}

	// topicId -> topic
	protected final ConcurrentHashMap<String, Topic> topicMap = new ConcurrentHashMap<String, Topic>();

	private PubSubManager()
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
		dataDomainClientSvc = DataDomainSvcFactory.getBlockingService();

		start();
	}

	/**
	 * 查询topic的订阅数
	 */
	@Override
	public int getSubscriber(String tid)
	{
		Topic topic = topicMap.get(tid);
		if (topic != null)
		{
			return topic.subscriptions.size();
		}
		else
		{
			return 0;
		}
	}

	/**
	 * 根据topicId查询topic
	 * 
	 * @param topicId
	 * @return
	 */
	public Topic getTopic(String topicId)
	{
		return topicMap.get(topicId);
	}

	/**
	 * 查询总的topic数
	 */
	@Override
	public int getTopics()
	{
		return topicMap.size();
	}

	/**
	 * 收到消息通知给客户端，来源于pub/sub或本地从数据域拽取的
	 * 
	 * @param topicId
	 * @param msgId
	 */
	public void onMessage(String topicId, String msgId)
	{
		Topic topic = topicMap.get(topicId);
		if (topic != null)
		{
			topic.latestMsgTimestamp = MessageUtils.getMessageTimestamp(msgId);
			for (Session session : topic.subscriptions)
			{
				if ((session != null) && session.active)
				{
					pushMessage(session, msgId);
				}
			}
		}
	}

	/**
	 * 向pub/sub发布消息
	 * 
	 * @param topicId
	 * @param message
	 */
	public void publish(String topicId, String message)
	{
		try
		{
			if (LOG.isDebugEnabled())
			{
				LOG.debug("publish to redis, topicId=" + topicId + ", message="
						+ message);
			}
			pubsubClient.publish(topicId, message);
		}
		catch (Exception e)
		{
			LOG.error("publish to redis error, topicId=" + topicId
					+ ", message=" + message, e);
		}
	}

	/**
	 * 向客户端推送消息通知
	 * 
	 * @param session
	 * @param msgId
	 */
	private void pushMessage(final Session session, final String msgId)
	{
		pushMsgExecutor.execute(new Runnable()
		{

			@Override
			public void run()
			{
				try
				{
					// header
					ApiHeader.Builder apiHeader = ApiHeader.newBuilder();
					apiHeader.setSeq(0);
					apiHeader.setVersion(0x00010000);
					apiHeader.setSid(session.sid);
					apiHeader.setType(ApiType.API_TYPE_NOTIFICATION.getNumber());// 包类型，取值为：请求为0、响应为1

					// body
					// ApiRequest.Builder apiRequest = ApiRequest.newBuilder();
					// apiRequest.setService("SSSessionDomainSvc");
					// apiRequest.setMethod("OnMessage");

					ApiNotification.Builder apiNotification = ApiNotification
							.newBuilder();
					apiNotification.setService("SSSessionDomainSvc");
					apiNotification.setMethod("OnMessage");

					apiNotification.setExtension(ApiExtensionHelper
							.getNotificationByMethodName("onMessage"),
							OnMessageNotification.newBuilder().setMsgid(msgId)
									.build());

					// api message
					ApiMessage.Builder responseApiMessage = ApiMessage
							.newBuilder();
					responseApiMessage.setHeader(apiHeader);
					// responseApiMessage.setBody(apiRequest.build().toByteString());
					responseApiMessage.setExtension(Common.notification,
							apiNotification.build());
					ApiMessage resp = responseApiMessage.build();
					// LOG.info(JsonFormat.printToString(resp));
					// send
					session.channel.write(resp);
					if (LOG.isDebugEnabled())
					{
						LOG.debug("push message to sid=" + session.sid
								+ ", userid=" + session.userid + ", msgid="
								+ msgId);
					}
				}
				catch (Exception e)
				{
					LOG.warn(
							"push message error, sid=" + session.sid
									+ ", userid=" + session.userid + ", msgid="
									+ msgId, e);
				}
			}
		});
	}

	// PubSubManager启动
	public void start()
	{
		running = true;

		// 启动topic状态检查
		Thread checker = new Thread(new TopicStatusChecker());
		checker.setName("PubSubManager.TopicStatusChecker");
		checker.setDaemon(true);
		checker.start();

		// 启动收取pub/sub消息
		Thread puller = new Thread(new MessagePuller());
		puller.setName("PubSubManager.MessagePuller");
		puller.setDaemon(true);
		puller.start();
	}

	// PubSubManager停止
	public void stop()
	{
		running = false;
	}

	/**
	 * 客户端本地订阅，同时检查是否已向pub/sub订阅
	 * 
	 * @param topicType
	 * @param id
	 * @param session
	 */
	public void subscribe(TopicType topicType, String id, Session session)
	{
		String topicId = PubSubUtil.getTopicId(topicType, id);
		if (LOG.isDebugEnabled())
		{
			// LOG.debug("session subscribe, topic=" + topicId + ", Session="
			// + session.sid);
		}
		Topic topic = topicMap.get(topicId);
		if (topic == null)
		{
			Topic alt = new Topic(topicType, id);
			topic = topicMap.putIfAbsent(alt.topicId, alt);
			if (topic == null)
			{
				topic = alt;
			}
			try
			{
				if (LOG.isDebugEnabled())
				{
					// LOG.debug("subscribe to redis, topic=" + topic.topicId);
				}
				pubsubClient.subscribe(topic.topicId);
			}
			catch (Exception e)
			{
				LOG.warn("subscribe to redis error, topic=" + topic.topicId, e);
			}
		}
		topic.subscriptions.add(session);
	}

	/**
	 * 客户端取消订阅，若该topic无本地订阅则取消向pub/sub订阅
	 * 
	 * @param topicId
	 * @param session
	 */
	public void unsubscribe(String topicId, Session session)
	{
		if (LOG.isDebugEnabled())
		{
			// LOG.debug("session unsubscribe, topic=" + topicId + ", Session="
			// + session.toString());
		}
		Topic topic = topicMap.get(topicId);
		if (topic != null)
		{
			topic.subscriptions.remove(session);
			if (topic.subscriptions.size() == 0)
			{
				try
				{
					if (LOG.isDebugEnabled())
					{
						// LOG.debug("unsubscribe to redis, topic="
						// + topic.topicId);
					}
					pubsubClient.unsubscribe(topic.topicId);
				}
				catch (Exception e)
				{
					LOG.warn("unsubscribe to redis error, topic="
							+ topic.topicId, e);
				}
				topicMap.remove(topicId);
			}
		}
	}
}
