package com.zhuaiwa.session.service;

import java.util.ArrayList;
import java.util.List;
import java.util.StringTokenizer;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import redis.clients.jedis.ShardedJedis;

import com.google.protobuf.ServiceException;
import com.zhuaiwa.api.Common.SSAccount;
import com.zhuaiwa.api.Common.SSBOX;
import com.zhuaiwa.api.Common.SSId;
import com.zhuaiwa.api.Common.SSMessage;
import com.zhuaiwa.api.Common.SSMessageType;
import com.zhuaiwa.api.Common.SSPerson;
import com.zhuaiwa.api.Common.SSResultCode;
import com.zhuaiwa.api.Common.SSShareType;
import com.zhuaiwa.api.Rpc.GetMessageByIdRequest;
import com.zhuaiwa.api.Rpc.GetMessageByIdResponse;
import com.zhuaiwa.api.Rpc.GetMessageByTimestampRequest;
import com.zhuaiwa.api.Rpc.GetMessageByTimestampResponse;
import com.zhuaiwa.api.Rpc.GetMessageRequest;
import com.zhuaiwa.api.Rpc.GetMessageResponse;
import com.zhuaiwa.api.Rpc.GetTorrentRequest;
import com.zhuaiwa.api.Rpc.GetTorrentResponse;
import com.zhuaiwa.api.Rpc.IsFollowerRequest;
import com.zhuaiwa.api.Rpc.IsFollowerResponse;
import com.zhuaiwa.api.Rpc.RemoveMessageRequest;
import com.zhuaiwa.api.Rpc.RemoveMessageResponse;
import com.zhuaiwa.api.Rpc.SendMessageRequest;
import com.zhuaiwa.api.Rpc.SendMessageResponse;
import com.zhuaiwa.api.Rpc.SetTorrentRequest;
import com.zhuaiwa.api.Rpc.SetTorrentResponse;
import com.zhuaiwa.api.SSDataDomain.SSDataDomainSvc.BlockingInterface;
import com.zhuaiwa.api.SSSessionDomain.FollowTagRequest;
import com.zhuaiwa.api.SSSessionDomain.FollowTagResponse;
import com.zhuaiwa.api.SSSessionDomain.GetFollowingTagRequest;
import com.zhuaiwa.api.SSSessionDomain.GetFollowingTagResponse;
import com.zhuaiwa.api.SSSessionDomain.GetLatestShareRequest;
import com.zhuaiwa.api.SSSessionDomain.GetLatestShareResponse;
import com.zhuaiwa.api.SSSessionDomain.GetMessageByTagRequest;
import com.zhuaiwa.api.SSSessionDomain.GetMessageByTagResponse;
import com.zhuaiwa.api.SSSessionDomain.GetTopContentRequest;
import com.zhuaiwa.api.SSSessionDomain.GetTopContentResponse;
import com.zhuaiwa.api.SSSessionDomain.GetTopTagRequest;
import com.zhuaiwa.api.SSSessionDomain.GetTopTagResponse;
import com.zhuaiwa.api.SSSessionDomain.SearchMessageRequest;
import com.zhuaiwa.api.SSSessionDomain.SearchMessageResponse;
import com.zhuaiwa.api.SSSessionDomain.UnfollowTagRequest;
import com.zhuaiwa.api.SSSessionDomain.UnfollowTagResponse;
import com.zhuaiwa.api.netty.NettyRpcController;
import com.zhuaiwa.api.util.SSIdUtils;
import com.zhuaiwa.dd.util.MessageUtils;
import com.zhuaiwa.session.PubSubManager;
import com.zhuaiwa.session.PubSubUtil;
import com.zhuaiwa.session.SessionManager;
import com.zhuaiwa.session.SessionManager.Session;
import com.zhuaiwa.session.search.SearchResult;
import com.zhuaiwa.session.search.Searcher;
import com.zhuaiwa.session.search.json.Contents;
import com.zhuaiwa.session.util.FreemarkerUtil;
import com.zhuaiwa.session.util.RpcUtils;
import com.zhuaiwa.util.MailUtil;
import com.zhuaiwa.util.SmsUtil;

public class MessageService_v_1_0 extends BaseService
{
	public static String removeHtmlTag(String s)
	{
		String s1 = "";
		try
		{
			StringTokenizer stringtokenizer = new StringTokenizer(s, "<");
			while (stringtokenizer.hasMoreTokens())
			{
				String s3 = stringtokenizer.nextToken();
				int i = s3.indexOf(">");
				if (i != -1)
				{
					s1 = s1 + s3.substring(i + 1, s3.length());
				}
				else
				{
					s1 = s1 + s3;
				}
			}
			return s1;
		}
		catch (Exception e)
		{
			return s;
		}
	}

	private BlockingInterface dataDomainClientSvc;

	private Logger LOG = LoggerFactory.getLogger(this.getClass());

	UserService_v_1_0 userService;

	public MessageService_v_1_0()
	{

	}

	public MessageService_v_1_0(BlockingInterface dataDomainClientSvc)
	{
		this.dataDomainClientSvc = dataDomainClientSvc;
	}

	public void followTag(NettyRpcController controller,
			FollowTagRequest request, FollowTagResponse.Builder response)
	{
		failed(controller, SSResultCode.RC_SERVICE_NOT_IMPLEMENT.getNumber(),
				"接口未实现.");
	}

	public void getFollowingTag(NettyRpcController controller,
			GetFollowingTagRequest request,
			GetFollowingTagResponse.Builder response)
	{
		failed(controller, SSResultCode.RC_SERVICE_NOT_IMPLEMENT.getNumber(),
				"接口未实现.");
	}

	public GetLatestShareResponse getLatestShare(NettyRpcController controller,
			GetLatestShareRequest request)
	{

		GetLatestShareResponse.Builder getLatestShareResponse = GetLatestShareResponse
				.newBuilder();

		List<String> latestMessageIdList = new ArrayList<String>();

		getLatestShareResponse.addAllMsgidList(latestMessageIdList);

		return getLatestShareResponse.build();
	}

	public GetMessageResponse getMessage(NettyRpcController controller,
			GetMessageRequest request)
	{
		String userid = null;

		if (request.getSourceBox().equals(SSBOX.PUBBOX))
		{
			userid = RpcUtils.getUserId(controller, request);
			if (userid == null)
			{
				failed(controller,
						SSResultCode.RC_SERVICE_EXCEPTION.getNumber(), "未登录.");
				return null;
			}
		}
		else
		{
			userid = RpcUtils.getUserId(controller, null);
			if (userid == null)
			{
				failed(controller,
						SSResultCode.RC_SERVICE_EXCEPTION.getNumber(), "未登录.");
				return null;
			}
			// 只许查询自己的收件箱、发件箱、收藏夹
			if (request.getSourceBox().equals(SSBOX.FAVBOX)
					|| request.getSourceBox().equals(SSBOX.INBOX)
					|| request.getSourceBox().equals(SSBOX.OUTBOX))
			{
				if (!request.getUserid().equals(userid))
				{
					failed(controller,
							SSResultCode.RC_SERVICE_EXCEPTION.getNumber(),
							"不允许查询别人的" + request.getSourceBox().name());
					return null;
				}
			}
		}
		com.zhuaiwa.api.Rpc.GetMessageRequest req = com.zhuaiwa.api.Rpc.GetMessageRequest
				.newBuilder().setUserid(request.getUserid()).mergeFrom(request)
				.build();
		try
		{
			return dataDomainClientSvc.getMessage(getRpcController(), req);
		}
		catch (ServiceException e)
		{
			failed(controller, SSResultCode.RC_SERVICE_EXCEPTION.getNumber(),
					"查询消息体失败.", e);
			return null;
		}
	}

	public GetMessageByIdResponse getMessageById(NettyRpcController controller,
			GetMessageByIdRequest request)
	{
		String userid = null;
		Session session = SessionManager.getInstance().getSession(
				controller.getApiHeader().getSid());
		if (session != null)
		{
			userid = session.userid;
		}
		LOG.info("userid : " + userid);
		try
		{
			// 指定分享需要判断权限
			com.zhuaiwa.api.Rpc.GetMessageByIdResponse resp = dataDomainClientSvc
					.getMessageById(getRpcController(), request);
			List<SSMessage> messages = resp.getMsgListList();
			List<SSMessage> result = new ArrayList<SSMessage>();
			if (messages != null)
			{
				LOG.info("message count: " + messages.size());
				for (SSMessage msg : messages)
				{
					LOG.info(msg.getMsgid());
					if (msg.getShareType() == SSShareType.SHARE_TYPE_PUBLIC
							.getNumber())
					{
						result.add(msg);
					}
					else if (msg.getShareType() == SSShareType.SHARE_TYPE_PROTECTED
							.getNumber())
					{

						// 如果消息的发送者是自己，不过滤
						if (msg.hasSender() && (msg.getSender() != null)
								&& msg.getSender().hasId()
								&& (msg.getSender().getId() != null)
								&& msg.getSender().getId().equals(userid))
						{
							result.add(msg);
							continue;
						}

						// 如果消息的接收者是自己，不过滤
						List<SSPerson> receivers = msg.getReceiverList();
						if (receivers == null)
						{
							continue;
						}
						for (SSPerson p : receivers)
						{
							if (!p.hasUserid() || (p.getUserid() == null))
							{
								continue;
							}

							if (p.getUserid().equals(userid))
							{
								result.add(msg);
								break;
							}
						}
					}
					else if (msg.getShareType() == SSShareType.SHARE_TYPE_PRIVATE
							.getNumber())
					{
						if (!msg.hasSender() || (msg.getSender() == null)
								|| !msg.getSender().hasId()
								|| (msg.getSender().getId() == null))
						{
							continue;
						}

						if (msg.getSender().getId().equals(userid))
						{
							result.add(msg);
						}
					}
				}
			}
			else
			{
				LOG.info("No message in response.");
			}
			LOG.info("Result list size is " + result.size());
			return com.zhuaiwa.api.Rpc.GetMessageByIdResponse.newBuilder()
					.addAllMsgList(result).build();
		}
		catch (ServiceException e)
		{
			failed(controller, SSResultCode.RC_SERVICE_EXCEPTION.getNumber(),
					"查询消息体失败.", e);
			return null;
		}
	}

	public void getMessageByTag(NettyRpcController controller,
			GetMessageByTagRequest request,
			GetMessageByTagResponse.Builder response)
	{
		failed(controller, SSResultCode.RC_SERVICE_NOT_IMPLEMENT.getNumber(),
				"接口未实现.");
	}

	// public GetTopContentResponse getTopContent(NettyRpcController controller,
	// GetTopContentRequest request) {
	//
	// GetTopContentResponse.Builder getTopContentResponse =
	// GetTopContentResponse.newBuilder();
	//
	// DataMiningService dms = DataMiningServiceImpl.getInstance();
	// NavigableMap<String, String> tops = dms.getTopContent();
	// if (tops == null)
	// {
	// return null;
	// }
	//
	// List<String> topContentIdList = new ArrayList<String>();
	//
	// for (String id : tops.keySet()) {
	// String s = tops.get(id);
	// String[] entries = s.split(";", 2);
	// if (entries.length != 2)
	// {
	// continue;
	// }
	// topContentIdList.add(entries[0]);
	// }
	// if ((topContentIdList == null) || (topContentIdList.size() == 0)) {
	// return getTopContentResponse.build();
	// }
	//
	// getTopContentResponse.addAllMsgidList(topContentIdList);
	//
	// // 获取Message
	// if (request.hasMode() && (request.getMode() > 0)) {
	// try {
	// GetMessageByIdResponse getMessageByIdResponse =
	// dataDomainClientSvc.getMessageById(getRpcController(),
	// GetMessageByIdRequest.newBuilder().addAllMsgidList(topContentIdList).build());
	// for (SSMessage message : getMessageByIdResponse.getMsgListList()) {
	// getTopContentResponse.addMsgList(message);
	// }
	// } catch (ServiceException e) {
	// failed(controller, SSResultCode.RC_SERVICE_EXCEPTION.getNumber(),
	// "查询Message发生异常.", e);
	// return null;
	// }
	// }
	//
	// return getTopContentResponse.build();
	// }

	public GetMessageByTimestampResponse getMessageByTimestamp(
			NettyRpcController controller, GetMessageByTimestampRequest request)
	{
		String userid = SessionManager.getInstance().getSession(
				controller.getApiHeader().getSid()).userid;
		// 只许查询自己的收件箱、发件箱、收藏夹
		if (request.getSourceBox().equals(SSBOX.FAVBOX)
				|| request.getSourceBox().equals(SSBOX.INBOX)
				|| request.getSourceBox().equals(SSBOX.OUTBOX))
		{
			if (!request.getUserid().equals(userid))
			{
				failed(controller,
						SSResultCode.RC_SERVICE_EXCEPTION.getNumber(),
						"不允许查询别人的" + request.getSourceBox().name());
			}
		}
		com.zhuaiwa.api.Rpc.GetMessageByTimestampRequest req = com.zhuaiwa.api.Rpc.GetMessageByTimestampRequest
				.newBuilder().setUserid(request.getUserid()).mergeFrom(request)
				.build();
		try
		{
			return dataDomainClientSvc.getMessageByTimestamp(
					getRpcController(), req);
		}
		catch (ServiceException e)
		{
			failed(controller, SSResultCode.RC_SERVICE_EXCEPTION.getNumber(),
					"根据时间查询消息列表失败.", e);
			return null;
		}

	}

	public GetTopContentResponse getTopContent(NettyRpcController controller,
			GetTopContentRequest request)
	{

		GetTopContentResponse.Builder getTopContentResponse = GetTopContentResponse
				.newBuilder();

		List<String> topContentIdList = new ArrayList<String>();

		getTopContentResponse.addAllMsgidList(topContentIdList);

		return getTopContentResponse.build();
	}

	// public GetTopTagResponse getTopTag(NettyRpcController controller,
	// GetTopTagRequest request) {
	// GetTopTagResponse.Builder getTopTagResponse =
	// GetTopTagResponse.newBuilder();
	//
	// DataMiningService dms = DataMiningServiceImpl.getInstance();
	// NavigableMap<String, String> toptags = dms.getTopTag();
	// if (toptags == null)
	// {
	// return null;
	// }
	//
	// List<String> topTagList = new ArrayList<String>();
	//
	// for (String id : toptags.keySet()) {
	// String s = toptags.get(id);
	// String[] entries = s.split(";", 2);
	// if (entries.length != 2)
	// {
	// continue;
	// }
	// topTagList.add(entries[0]);
	// }
	//
	// getTopTagResponse.addAllTagList(topTagList);
	// return getTopTagResponse.build();
	// }
	public GetTopTagResponse getTopTag(NettyRpcController controller,
			GetTopTagRequest request)
	{
		GetTopTagResponse.Builder getTopTagResponse = GetTopTagResponse
				.newBuilder();

		List<String> topTagList = new ArrayList<String>();

		getTopTagResponse.addAllTagList(topTagList);
		return getTopTagResponse.build();
	}

	public GetTorrentResponse getTorrent(NettyRpcController controller,
			GetTorrentRequest request)
	{
		try
		{
			return dataDomainClientSvc.getTorrent(getRpcController(), request);
		}
		catch (ServiceException e)
		{
			failed(controller, SSResultCode.RC_SERVICE_EXCEPTION.getNumber(),
					e.getMessage(), e);
			return null;
		}
	}

	/**
	 * @return the userService
	 */
	public UserService_v_1_0 getUserService()
	{
		return userService;
	}

	public void removeMessage(NettyRpcController controller,
			RemoveMessageRequest request, RemoveMessageResponse.Builder response)
	{
		if (!controller.getApiHeader().hasSid()
				|| (SessionManager.getInstance().getSession(
						controller.getApiHeader().getSid()) == null))
		{
			failed(controller, SSResultCode.RC_SERVICE_EXCEPTION.getNumber(),
					"未登录不允许删除消息");
			return;
		}
		if (!SessionManager.getInstance().getSession(
				controller.getApiHeader().getSid()).userid.equals(request
				.getUserid()))
		{
			failed(controller, SSResultCode.RC_SERVICE_EXCEPTION.getNumber(),
					"不允许删除非登录用户的消息");
			return;
		}
		// String userid = request.getUserid();
		// if (!request.hasUserid()) {
		// userid =
		// SessionManager.getInstance().getSession(controller.getApiHeader().getSid()).userid;
		// }
		String userid = SessionManager.getInstance().getSession(
				controller.getApiHeader().getSid()).userid;
		List<String> msgIds = request.getMsgidListList();
		RemoveMessageRequest req = RemoveMessageRequest.newBuilder()
				.setUserid(userid).setSourceBox(request.getSourceBox())
				.addAllMsgidList(msgIds).build();
		try
		{
			dataDomainClientSvc.removeMessage(getRpcController(), req);
		}
		catch (ServiceException e)
		{
			e.printStackTrace();
		}
	}

	public SearchMessageResponse searchMessage(NettyRpcController controller,
			SearchMessageRequest request)
	{
		SearchResult result = new Searcher().searchMessage(request
				.getQueryString());
		return SearchMessageResponse.newBuilder().setTotal(result.getTotal())
				.addAllMessageidList(result.getIds()).build();
	}

	public void sendMessage(NettyRpcController controller,
			SendMessageRequest request, SendMessageResponse.Builder response)
	{
		String userid = SessionManager.getInstance().getSession(
				controller.getApiHeader().getSid()).userid;
		if (request.getMsg().getSender().getId()
				.equalsIgnoreCase("system_user"))
		{
			userid = "system_user";
		}
		long timestamp = System.currentTimeMillis();
		SSMessage msg = SSMessage
				.newBuilder(request.getMsg())
				.setTimestamp(timestamp)
				.setSender(
						SSId.newBuilder().setDomain("userid").setId(userid)
								.build()).build();
		int msgType = msg.getMsgType();
		int shareType = msg.getShareType();
		String msgId = null;
		LOG.debug("msgType = " + msgType);

		// 持久化
		if (msgType == SSMessageType.MESSAGE_TYPE_CONTENT.getNumber())
		{
			com.zhuaiwa.api.Rpc.SendMessageRequest req = com.zhuaiwa.api.Rpc.SendMessageRequest
					.newBuilder().setUserid(userid).setMsg(msg).build();
			try
			{
				com.zhuaiwa.api.Rpc.SendMessageResponse resp = dataDomainClientSvc
						.sendMessage(getRpcController(), req);
				msgId = resp.getMsgid();
				LOG.debug("msgId : " + msgId);
				response.setMsgid(resp.getMsgid());
				response.setTimestamp(timestamp);
			}
			catch (ServiceException e)
			{
				failed(controller,
						SSResultCode.RC_SERVICE_EXCEPTION.getNumber(),
						"保存消息失败.", e);
				return;
			}
		}
		else
		{
			LOG.debug("不持久化 " + msgType);
		}

		try
		{
			// 发布
			if (shareType == SSShareType.SHARE_TYPE_PUBLIC.getNumber())
			{
				if (msgType == SSMessageType.MESSAGE_TYPE_CONTENT.getNumber())
				{
					PubSubManager.getInstance().publish(
							PubSubUtil.getPublicShareTopic(msg.getSender()
									.getId()), msgId);

					ShardedJedis sj = null;
					try
					{
						sj = pool.getResource();

						sj.zadd(getPublicShareKey(msg.getSender().getId()),
								MessageUtils.getMessageTimestamp(msgId), msgId);
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
				else if (msgType == SSMessageType.MESSAGE_TYPE_PRESENCE
						.getNumber())
				{

				}
			}
			else if (shareType == SSShareType.SHARE_TYPE_PROTECTED.getNumber())
			{
				Contents contents = null;
				String nickname = null;

				for (SSPerson person : msg.getReceiverList())
				{
					if (person.hasUserid())
					{
						// 检查收件人的消息过滤级别
						try
						{
							SSAccount account = userService
									.getAccount(SSIdUtils.fromUserId(person
											.getUserid()));
							if (account.hasMessageFilter()
									&& (account.getMessageFilter() == 1))
							{// 仅接受自己关注的人发送过来的消息
								IsFollowerResponse isFollowerResponse = userService
										.isFollower(
												controller,
												IsFollowerRequest
														.newBuilder()
														.setUserid(
																msg.getSender()
																		.getId())
														.addUseridList(
																person.getUserid())
														.setMode(0).build());
								if (isFollowerResponse
										.getFollowerUseridListCount() > 0)
								{
									PubSubManager
											.getInstance()
											.publish(
													PubSubUtil.getProtecedShareTopic(person
															.getUserid()),
													msgId);
								}
								else
								{
									LOG.debug("msg:" + msg.getMsgid()
											+ ", from:"
											+ msg.getSender().getId() + " to:"
											+ person.getUserid() + " blocked.");
								}
							}
							else
							{
								PubSubManager.getInstance().publish(
										PubSubUtil.getProtecedShareTopic(person
												.getUserid()), msgId);
							}
						}
						catch (ServiceException e)
						{
							LOG.debug("查询帐户消息过滤级别出错", e);
						}
					}

					if (person.hasEmail() || person.hasPhone())
					{
						if (contents == null)
						{
							contents = new Contents(msg.getBody());
						}
						if (nickname == null)
						{
							nickname = userService.getNickname(userid);
						}

						if (person.hasEmail())
						{
							StringBuffer sb = new StringBuffer();
							if (contents.getSelfContent() != null)
							{
								sb.append("<strong>");
								sb.append(contents.getSelfContent());
								sb.append("</strong><br />");
							}
							if (contents.getHtmlContent() != null)
							{
								sb.append(contents.getHtmlContent());
								sb.append("<br />");
							}
							if (contents.getFileContent() != null)
							{
								sb.append(contents.getFileContent());
								sb.append("<br />请安装<a href=\"http://www.baiku.cn\">百库客户端</a>以下载文件。");
							}
							MailUtil.sendMessage(person.getEmail(), nickname
									+ "与您分享", FreemarkerUtil
									.getShareMsgMailOutput(sb.toString(),
											person.getEmail(), nickname));
						}

						if (person.hasPhone())
						{
							try
							{
								String body = null;
								body = contents.getSelfContent();
								if ((body == null) || body.isEmpty())
								{
									body = contents.getHtmlContent();
								}
								if ((body == null) || body.isEmpty())
								{
									body = contents.getFileContent();
								}
								if (body != null)
								{
									body = removeHtmlTag(body);
								}

								SmsUtil.sendSms(FreemarkerUtil
										.getShareMsgSmsOutput(null, body,
												nickname, msgId), person
										.getPhone());
							}
							catch (Exception e)
							{
							}
						}

					}
				}
			}
		}
		catch (Exception e)
		{
			LOG.warn("", e);
		}
	}

	public void setDataDomainClientSvc(BlockingInterface dataDomainClientSvc)
	{
		this.dataDomainClientSvc = dataDomainClientSvc;
	}

	public SetTorrentResponse setTorrent(NettyRpcController controller,
			SetTorrentRequest request)
	{
		try
		{
			return dataDomainClientSvc.setTorrent(getRpcController(), request);
		}
		catch (ServiceException e)
		{
			failed(controller, SSResultCode.RC_SERVICE_EXCEPTION.getNumber(),
					e.getMessage(), e);
			return null;
		}
	}

	/**
	 * @param userService
	 *            the userService to set
	 */
	public void setUserService(UserService_v_1_0 userService)
	{
		this.userService = userService;
	}

	public void unfollowTag(NettyRpcController controller,
			UnfollowTagRequest request, UnfollowTagResponse.Builder response)
	{
		failed(controller, SSResultCode.RC_SERVICE_NOT_IMPLEMENT.getNumber(),
				"接口未实现.");
	}
}
