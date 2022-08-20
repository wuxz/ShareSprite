package com.zhuaiwa.session.service;

import java.net.InetSocketAddress;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

import org.apache.commons.codec.digest.DigestUtils;
import org.apache.commons.lang.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.google.protobuf.Descriptors.Descriptor;
import com.google.protobuf.Descriptors.FieldDescriptor;
import com.google.protobuf.Descriptors.FieldDescriptor.Type;
import com.google.protobuf.Message;
import com.google.protobuf.ServiceException;
import com.zhuaiwa.api.Common.ApiHeader;
import com.zhuaiwa.api.Common.SSAccount;
import com.zhuaiwa.api.Common.SSContact;
import com.zhuaiwa.api.Common.SSId;
import com.zhuaiwa.api.Common.SSMessage;
import com.zhuaiwa.api.Common.SSMessageType;
import com.zhuaiwa.api.Common.SSPerson;
import com.zhuaiwa.api.Common.SSProfile;
import com.zhuaiwa.api.Common.SSResultCode;
import com.zhuaiwa.api.Common.SSShareType;
import com.zhuaiwa.api.Rpc.BindAccountRequest;
import com.zhuaiwa.api.Rpc.BindAccountResponse;
import com.zhuaiwa.api.Rpc.FollowRequest;
import com.zhuaiwa.api.Rpc.FollowResponse;
import com.zhuaiwa.api.Rpc.GetFollowerRequest;
import com.zhuaiwa.api.Rpc.GetFollowerResponse;
import com.zhuaiwa.api.Rpc.GetFollowingRequest;
import com.zhuaiwa.api.Rpc.GetFollowingResponse;
import com.zhuaiwa.api.Rpc.GetInviterRequest;
import com.zhuaiwa.api.Rpc.GetInviterResponse;
import com.zhuaiwa.api.Rpc.GetInvitingRequest;
import com.zhuaiwa.api.Rpc.GetInvitingResponse;
import com.zhuaiwa.api.Rpc.GetProfileRequest;
import com.zhuaiwa.api.Rpc.GetProfileResponse;
import com.zhuaiwa.api.Rpc.InviteRequest;
import com.zhuaiwa.api.Rpc.InviteResponse;
import com.zhuaiwa.api.Rpc.IsFollowerRequest;
import com.zhuaiwa.api.Rpc.IsFollowerResponse;
import com.zhuaiwa.api.Rpc.SendMessageRequest;
import com.zhuaiwa.api.Rpc.SendMessageResponse;
import com.zhuaiwa.api.Rpc.SetProfileRequest;
import com.zhuaiwa.api.Rpc.SetProfileResponse;
import com.zhuaiwa.api.Rpc.UnbindAccountRequest;
import com.zhuaiwa.api.Rpc.UnbindAccountResponse;
import com.zhuaiwa.api.Rpc.UnfollowRequest;
import com.zhuaiwa.api.Rpc.UnfollowResponse;
import com.zhuaiwa.api.SSDataDomain.CreateAccountRequest;
import com.zhuaiwa.api.SSDataDomain.CreateAccountResponse;
import com.zhuaiwa.api.SSDataDomain.GetAccountRequest;
import com.zhuaiwa.api.SSDataDomain.GetAccountResponse;
import com.zhuaiwa.api.SSDataDomain.SSDataDomainSvc.BlockingInterface;
import com.zhuaiwa.api.SSDataDomain.SetAccountRequest;
import com.zhuaiwa.api.SSSessionDomain.ActivateAccountRequest;
import com.zhuaiwa.api.SSSessionDomain.ActivateAccountResponse;
import com.zhuaiwa.api.SSSessionDomain.BindAccountOkRequest;
import com.zhuaiwa.api.SSSessionDomain.BindAccountOkResponse;
import com.zhuaiwa.api.SSSessionDomain.ChangePasswordRequest;
import com.zhuaiwa.api.SSSessionDomain.ChangePasswordResponse;
import com.zhuaiwa.api.SSSessionDomain.ForgetPasswordRequest;
import com.zhuaiwa.api.SSSessionDomain.ForgetPasswordResponse;
import com.zhuaiwa.api.SSSessionDomain.GetLatestRegisterUserRequest;
import com.zhuaiwa.api.SSSessionDomain.GetLatestRegisterUserResponse;
import com.zhuaiwa.api.SSSessionDomain.GetRecommendUserRequest;
import com.zhuaiwa.api.SSSessionDomain.GetRecommendUserResponse;
import com.zhuaiwa.api.SSSessionDomain.GetSettingsRequest;
import com.zhuaiwa.api.SSSessionDomain.GetSettingsResponse;
import com.zhuaiwa.api.SSSessionDomain.GetTopUserRequest;
import com.zhuaiwa.api.SSSessionDomain.GetTopUserResponse;
import com.zhuaiwa.api.SSSessionDomain.GetUserIdRequest;
import com.zhuaiwa.api.SSSessionDomain.GetUserIdResponse;
import com.zhuaiwa.api.SSSessionDomain.LoginRequest;
import com.zhuaiwa.api.SSSessionDomain.LoginResponse;
import com.zhuaiwa.api.SSSessionDomain.LogoutRequest;
import com.zhuaiwa.api.SSSessionDomain.LogoutResponse;
import com.zhuaiwa.api.SSSessionDomain.RegisterAccountRequest;
import com.zhuaiwa.api.SSSessionDomain.RegisterAccountResponse;
import com.zhuaiwa.api.SSSessionDomain.ResetPasswordRequest;
import com.zhuaiwa.api.SSSessionDomain.ResetPasswordResponse;
import com.zhuaiwa.api.SSSessionDomain.SearchUserRequest;
import com.zhuaiwa.api.SSSessionDomain.SearchUserResponse;
import com.zhuaiwa.api.SSSessionDomain.SendActivateCodeRequest;
import com.zhuaiwa.api.SSSessionDomain.SendActivateCodeResponse;
import com.zhuaiwa.api.SSSessionDomain.SetSettingsRequest;
import com.zhuaiwa.api.SSSessionDomain.SetSettingsResponse;
import com.zhuaiwa.api.netty.NettyRpcController;
import com.zhuaiwa.api.util.SSIdUtils;
import com.zhuaiwa.api.util.SSIdUtils.SSIdDomain;
import com.zhuaiwa.session.PubSubManager.TopicType;
import com.zhuaiwa.session.SessionManager;
import com.zhuaiwa.session.SessionManager.Session;
import com.zhuaiwa.session.search.SearchResult;
import com.zhuaiwa.session.search.Searcher;
import com.zhuaiwa.session.util.FreemarkerUtil;
import com.zhuaiwa.session.util.MemcachedService;
import com.zhuaiwa.session.util.RpcUtils;
import com.zhuaiwa.session.util.ValidatorUtil;
import com.zhuaiwa.util.MailUtil;
import com.zhuaiwa.util.PropertiesHelper;
import com.zhuaiwa.util.SmsUtil;

public class UserService_v_1_0 extends BaseService
{
	private static final Logger LOG = LoggerFactory
			.getLogger(UserService_v_1_0.class);

	public static String genSecurityCode()
	{
		String securityCode = String.format("%06d",
				((int) ((Math.random() * 1000000) + 100000)) % 1000000);
		return securityCode;
	}

	@SuppressWarnings("unchecked")
	public static <T extends com.google.protobuf.GeneratedMessage> T SetProfileIconUrl(
			T response)
	{
		final FieldDescriptor fd_iconurl = SSProfile.getDescriptor()
				.findFieldByName("icon_url");

		if (response == null)
		{
			return null;
		}

		Message.Builder response_builder = null;
		Descriptor d = response.getDescriptorForType();
		for (FieldDescriptor fd : d.getFields())
		{
			LOG.info(fd.getType().name() + ": " + fd.getFullName());
			if ((fd.getType() != Type.MESSAGE)
					|| !fd.getMessageType().equals(SSProfile.getDescriptor()))
			{
				continue;
			}

			if (response_builder == null)
			{
				response_builder = response.toBuilder();
			}

			if (fd.isRepeated())
			{
				int count = response_builder.getRepeatedFieldCount(fd);
				LOG.info("It is repeated field, count = " + count);
				for (int i = 0; i < count; i++)
				{
					SSProfile profile = (SSProfile) response_builder
							.getRepeatedField(fd, i);
					response_builder.setRepeatedField(
							fd,
							i,
							profile.toBuilder()
									.setField(
											fd_iconurl,
											"http://api.baiku.cn/do/getIcon?userid="
													+ profile.getUserid())
									.build());
				}
			}
			else if (response_builder.hasField(fd))
			{
				LOG.info("It is normal field.");
				SSProfile profile = (SSProfile) response_builder.getField(fd);
				response_builder.setField(
						fd,
						profile.toBuilder()
								.setField(
										fd_iconurl,
										"http://api.baiku.cn/do/getIcon?userid="
												+ profile.getUserid()).build());
			}
		}

		if (response_builder != null)
		{
			return (T) response_builder.build();
		}

		return response;
	}

	private BlockingInterface dataDomainClientSvc;

	MessageService_v_1_0 messageService;

	String shareMsgQ;

	public UserService_v_1_0()
	{
		shareMsgQ = PropertiesHelper.getValue("memecached.Q.queue");
		if (StringUtils.isEmpty(shareMsgQ))
		{
			shareMsgQ = null;
		}
	}

	public UserService_v_1_0(BlockingInterface dataDomainClientSvc)
	{
		super();
		this.dataDomainClientSvc = dataDomainClientSvc;
	}

	public SSAccount activateAccount(NettyRpcController controller,
			ActivateAccountRequest request,
			ActivateAccountResponse.Builder response)
	{
		try
		{
			SSAccount account = getAccount(request.getUser());
			if (account == null)
			{
				failed(controller,
						SSResultCode.RC_SERVICE_EXCEPTION.getNumber(),
						"此账号未注册.");
				return null;
			}
			else
			{
				if (account.getIsActive() == 0)
				{// 未激活
					if ((System.currentTimeMillis() - account
							.getSecurityCodeTime()) > (2 * 60 * 60 * 1000L))
					{// 激活码超时无效
						failed(controller,
								SSResultCode.RC_SERVICE_EXCEPTION.getNumber(),
								"激活码超时无效.");
						return null;
					}
					else
					{
						if (request.getCode() == null)
						{// 激活码为空
							failed(controller,
									SSResultCode.RC_SERVICE_EXCEPTION
											.getNumber(), "激活码不能为空.");
							return null;
						}
						if (request.getCode().equals(account.getSecurityCode()))
						{// 激活成功
							SSAccount a = SSAccount.newBuilder().setIsActive(1)
									.build();
							try
							{
								setAccount(account.getUserid(), a);
							}
							catch (ServiceException e)
							{
								failed(controller,
										SSResultCode.RC_SERVICE_EXCEPTION
												.getNumber(), "保存帐户发生异常.", e);
								return null;
							}

							try
							{
								if (shareMsgQ != null)
								{
									// 向消息审核模块抄送消息。
									MemcachedService.sendMsg(
											shareMsgQ,
											"{ \"ActiveAccount\": \""
													+ account.getUserid()
													+ "\" }");
									LOG.debug("抄送消息成功。");
								}
							}
							catch (Throwable e1)
							{
								LOG.error("向业务平台发送激活账户的消息失败", e1);
							}

							return account;
						}
						else
						{// 激活码不匹配
							failed(controller,
									SSResultCode.RC_SERVICE_EXCEPTION
											.getNumber(), "激活码不匹配.");
							return null;
						}
					}
				}
				else
				{// 帐户已经激活，不能再次激活
					failed(controller,
							SSResultCode.RC_SERVICE_EXCEPTION.getNumber(),
							"帐户已经激活，不能再次激活.");
					return null;
				}
			}
		}
		catch (ServiceException e)
		{
			failed(controller, SSResultCode.RC_SERVICE_EXCEPTION.getNumber(),
					"查询帐户发生异常.", e);
			return null;
		}
	}

	// 绑定第一步，发送验证码
	public BindAccountResponse bindAccount(NettyRpcController controller,
			BindAccountRequest request)
	{
		SSId newId = request.getNewId();
		if (SSIdUtils.isEmailId(newId))
		{
			if (!ValidatorUtil.isValidEmail(newId.getId()))
			{
				failed(controller,
						SSResultCode.RC_SERVICE_EXCEPTION.getNumber(),
						newId.getId() + "不是有效的电子邮件地址.");
				return null;
			}
		}
		else if (SSIdUtils.isPhoneId(newId))
		{
			if (!ValidatorUtil.isValidPhoneNumber(newId.getId()))
			{
				failed(controller,
						SSResultCode.RC_SERVICE_EXCEPTION.getNumber(),
						newId.getId() + "不是有效的电话号码.");
				return null;
			}
		}

		Session session = SessionManager.getInstance().getSession(
				controller.getApiHeader().getSid());
		SSAccount account = null;
		try
		{
			account = getAccount(SSId.newBuilder()
					.setDomain(SSIdUtils.SSIdDomain.userid.name())
					.setId(session.userid).build());
			if (account == null)
			{
				failed(controller,
						SSResultCode.RC_SERVICE_EXCEPTION.getNumber(), "帐户不存在.");
				return null;
			}
			/* 修改绑定，允许先发验证码
			else
			{
				String id = SSIdUtils.getId(account.getAliasIdListList(),
						SSIdUtils.getDomain(request.getNewId().getDomain()));
				if (id != null)
				{
					failed(controller,
							SSResultCode.RC_SERVICE_EXCEPTION.getNumber(),
					"不能重复绑定.");
					return null;
				}
			}
			 */
		}
		catch (ServiceException e)
		{
			failed(controller, SSResultCode.RC_SERVICE_EXCEPTION.getNumber(),
					"查询帐户信息发生异常.", e);
			return null;
		}

		String securityCode = genSecurityCode();
		try
		{
			SSAccount acc = SSAccount.newBuilder()
					.setSecurityCode(securityCode)
					.setSecurityCodeTime(System.currentTimeMillis()).build();
			setAccount(account.getUserid(), acc);
		}
		catch (ServiceException e)
		{
			failed(controller, SSResultCode.RC_SERVICE_EXCEPTION.getNumber(),
					"保存帐户信息发生异常.", e);
			return null;
		}

		if (SSIdUtils.isEmailId(request.getNewId()))
		{
			// 发送邮件
			MailUtil.sendMessage(request.getNewId().getId(), "激活帐号",
					FreemarkerUtil.getActiveCodeMailOutput(
							getProfile(account.getUserid(), 0).getNickname(),
							account.getUserid(), securityCode));

		}
		else if (SSIdUtils.isPhoneId(request.getNewId()))
		{
			// 发送短信
			SmsUtil.sendSms("您通过百库绑定的手机验证码为：" + securityCode, request
					.getNewId().getId() + "，感谢您使用！");
		}
		return BindAccountResponse.newBuilder().build();
	}

	// 绑定第二步，验证验证码，完成绑定
	public BindAccountOkResponse bindAccountOk(NettyRpcController controller,
			BindAccountOkRequest request)
	{
		SSId newId = request.getNewId();
		if (SSIdUtils.isEmailId(newId))
		{
			if (!ValidatorUtil.isValidEmail(newId.getId()))
			{
				failed(controller,
						SSResultCode.RC_SERVICE_EXCEPTION.getNumber(),
						newId.getId() + "不是有效的电子邮件地址.");
				return null;
			}
		}
		else if (SSIdUtils.isPhoneId(newId))
		{
			if (!ValidatorUtil.isValidPhoneNumber(newId.getId()))
			{
				failed(controller,
						SSResultCode.RC_SERVICE_EXCEPTION.getNumber(),
						newId.getId() + "不是有效的电话号码.");
				return null;
			}
		}

		Session session = SessionManager.getInstance().getSession(
				controller.getApiHeader().getSid());
		SSAccount account = null;
		try
		{
			account = getAccount(SSId.newBuilder()
					.setDomain(SSIdUtils.SSIdDomain.userid.name())
					.setId(session.userid).build());
			if (account == null)
			{
				failed(controller,
						SSResultCode.RC_SERVICE_EXCEPTION.getNumber(), "帐户不存在.");
				return null;
			}
			else
			{
				// 验证验证码
				if ((System.currentTimeMillis() - account.getSecurityCodeTime()) > (2 * 60 * 60 * 1000L))
				{// 激活码超时无效
					failed(controller,
							SSResultCode.RC_SERVICE_EXCEPTION.getNumber(),
							"激活码超时无效.");
					return null;
				}
				else
				{
					if (request.getCode() == null)
					{// 激活码为空
						failed(controller,
								SSResultCode.RC_SERVICE_EXCEPTION.getNumber(),
								"激活码不能为空.");
						return null;
					}
					if (request.getCode().equals(account.getSecurityCode()))
					{// 验证成功

						// 验证是否已经绑定该类型的id
						String id = SSIdUtils.getId(account
								.getAliasIdListList(), SSIdUtils
								.getDomain(request.getNewId().getDomain()));
						if (id != null)
						{
							failed(controller,
									SSResultCode.RC_SERVICE_EXCEPTION
											.getNumber(), "不能重复绑定.");
							return null;
						}
					}
					else
					{
						failed(controller,
								SSResultCode.RC_SERVICE_EXCEPTION.getNumber(),
								"验证码不匹配.");
						return null;
					}
				}
			}
		}
		catch (ServiceException e)
		{
			failed(controller, SSResultCode.RC_SERVICE_EXCEPTION.getNumber(),
					"查询帐户信息发生异常.", e);
			return null;
		}

		try
		{
			com.zhuaiwa.api.Rpc.BindAccountRequest req = com.zhuaiwa.api.Rpc.BindAccountRequest
					.newBuilder()
					// .setUserid(request.getUserid())
					.setUserid(session.userid)
					.setNewId(
							SSId.newBuilder()
									.setDomain(request.getNewId().getDomain())
									.setId(request.getNewId().getId()
											.toLowerCase())).build();
			dataDomainClientSvc.bindAccount(getRpcController(), req);

			return BindAccountOkResponse.newBuilder().build();
		}
		catch (ServiceException e)
		{
			failed(controller, SSResultCode.RC_SERVICE_EXCEPTION.getNumber(),
					newId.getId() + "绑定帐户失败.", e);
			return null;
		}
	}

	// 新旧密码均为明文
	public ChangePasswordResponse changePassword(NettyRpcController controller,
			ChangePasswordRequest request)
	{
		SSId id;
		boolean logon = false;
		Session session = SessionManager.getInstance().getSession(
				controller.getApiHeader().getSid());
		if (session == null)
		{
			id = request.getUser();
		}
		else
		{
			id = SSId.newBuilder().setDomain(SSIdDomain.userid.name())
					.setId(session.userid).build();
			logon = true;
		}
		try
		{
			SSAccount account = getAccount(id);
			if (account == null)
			{
				failed(controller,
						SSResultCode.RC_SERVICE_EXCEPTION.getNumber(),
						"帐户不存在不能修改密码.");
				return null;
			}
			else
			{
				if (!logon)
				{
					if ((request.getOldPassword() == null)
					// || !DigestUtils.md5Hex(request.getOldPassword())
							|| !request.getOldPassword().equalsIgnoreCase(
									account.getPassword()))
					{
						// 旧密码不正确
						failed(controller,
								SSResultCode.RC_SERVICE_EXCEPTION.getNumber(),
								"旧密码不正确.");
						return null;
					}
				}
				SSAccount a = SSAccount.newBuilder().setPassword(
				// DigestUtils.md5Hex(request.getNewPassword()))
						request.getNewPassword()).setSecurityCodeTime(0)
						.build();
				try
				{
					setAccount(account.getUserid(), a);
				}
				catch (ServiceException e)
				{
					failed(controller,
							SSResultCode.RC_SERVICE_EXCEPTION.getNumber(),
							"保存新密码发生异常.", e);
					return null;
				}

			}
		}
		catch (ServiceException e)
		{
			failed(controller, SSResultCode.RC_SERVICE_EXCEPTION.getNumber(),
					"查询帐户发生异常.", e);
			return null;
		}
		return ChangePasswordResponse.newBuilder().build();
	}

	public void checkForgetPassword(NettyRpcController controller,
			ForgetPasswordRequest request, String code,
			ForgetPasswordResponse.Builder response)
	{
		try
		{
			SSAccount account = getAccount(request.getUser());
			if (account == null)
			{
				failed(controller,
						SSResultCode.RC_SERVICE_EXCEPTION.getNumber(),
						"帐户不存在不能找回密码.");
				return;
			}
			else
			{
				if (account.getIsActive() != 1)
				{
					failed(controller,
							SSResultCode.RC_ACCOUNT_NOT_ACTIVATED.getNumber(),
							"帐户未激活不能找回密码.");
					return;
				}

				if ((System.currentTimeMillis() - account.getSecurityCodeTime()) > (2 * 60 * 60 * 1000L))
				{// 验证码超时无效
					failed(controller,
							SSResultCode.RC_SERVICE_EXCEPTION.getNumber(),
							"验证码超时无效.");
					return;
				}
				else
				{
					if (account.getSecurityCode().equals(code))
					{
						return;
					}
					else
					{
						failed(controller,
								SSResultCode.RC_SERVICE_EXCEPTION.getNumber(),
								"验证码不匹配.");
						return;
					}
				}
			}
		}
		catch (ServiceException e)
		{
			failed(controller, SSResultCode.RC_SERVICE_EXCEPTION.getNumber(),
					"查询帐户发生异常.", e);
		}
	}

	public void follow(NettyRpcController controller, FollowRequest request,
			FollowResponse.Builder response)
	{
		Session session = SessionManager.getInstance().getSession(
				controller.getApiHeader().getSid());
		session.subscribe(TopicType.PS_, request.getFollowingUseridListList());
		com.zhuaiwa.api.Rpc.FollowRequest req = com.zhuaiwa.api.Rpc.FollowRequest
				.newBuilder().setFollowerUserid(session.userid)
				.mergeFrom(request).build();
		try
		{
			dataDomainClientSvc.follow(getRpcController(), req);

			if (SessionManager.getInstance().type
					.equals(SessionManager.SessionType.TCP))
			{
				SSMessage.Builder msgBuilder = SSMessage.newBuilder();
				msgBuilder
						.setMsgType(
								SSMessageType.MESSAGE_TYPE_CONTENT.getNumber())
						.setSender(
								SSId.newBuilder().setId("system_user").build())
						.setShareType(
								SSShareType.SHARE_TYPE_PROTECTED.getNumber())
						.setBody(
								"{\"contents\":[{\"content_type\":\"command\", \"command_id\":\"befollowed\", \"command_desc\":{\"desc\":\"\", \"sender\":\""
										+ session.userid + "\"}}]}");
				for (Iterator<String> it = request.getFollowingUseridListList()
						.iterator(); it.hasNext();)
				{
					msgBuilder.addReceiver(SSPerson.newBuilder().setUserid(
							it.next()));
				}

				messageService.sendMessage(
						controller,
						SendMessageRequest.newBuilder()
								.setMsg(msgBuilder.build())
								.setUserid("system_user").build(),
						SendMessageResponse.newBuilder());

				// 更新baikuService的缓存
				for (String followingId : request.getFollowingUseridListList())
				{
					BaseService.onFollow(this, controller,
							request.getFollowerUserid(), followingId);
				}
			}
		}
		catch (ServiceException e)
		{
			failed(controller, SSResultCode.RC_SERVICE_EXCEPTION.getNumber(),
					"关注失败.", e);
			return;
		}
	}

	public SSAccount forgetPassword(NettyRpcController controller,
			ForgetPasswordRequest request,
			ForgetPasswordResponse.Builder response)
	{
		try
		{
			SSAccount account = getAccount(request.getUser());
			if (account == null)
			{
				failed(controller,
						SSResultCode.RC_SERVICE_EXCEPTION.getNumber(),
						"帐户不存在不能找回密码.");
				return account;
			}
			else
			{
				if (account.getIsActive() != 1)
				{
					failed(controller,
							SSResultCode.RC_ACCOUNT_NOT_ACTIVATED.getNumber(),
							"帐户未激活不能找回密码.");
					return account;
				}

				String name = getNickname(account.getUserid());
				String securityCode = genSecurityCode();
				if (SSIdUtils.isEmailId(request.getUser()))
				{
					// 发送邮件
					MailUtil.sendMessage(request.getUser().getId(), "找回密码",
							FreemarkerUtil.getForgetPasswordMailOutput(name,
									account.getUserid(), securityCode));

				}
				else if (SSIdUtils.isPhoneId(request.getUser()))
				{
					// 发送短信
					SmsUtil.sendSms("重置密码，验证码:" + securityCode, request
							.getUser().getId());
				}

				account = SSAccount.newBuilder().setUserid(account.getUserid())
						.setSecurityCode(securityCode)
						.setSecurityCodeTime(System.currentTimeMillis())
						.build();
				setAccount(account.getUserid(), account);

				return account;
			}
		}
		catch (ServiceException e)
		{
			failed(controller, SSResultCode.RC_SERVICE_EXCEPTION.getNumber(),
					"查询帐户发生异常.", e);
			return null;
		}
	}

	protected SSAccount getAccount(SSId id) throws ServiceException
	{
		if (null == id)
		{
			return null;
		}
		if (id.hasDomain())
		{
			GetAccountRequest.Builder builder = GetAccountRequest.newBuilder();
			if (SSIdUtils.isEmailId(id))
			{
				builder.addEmail(id.getId().toLowerCase());// 邮件地址统一转换成小写
			}
			else if (SSIdUtils.isPhoneId(id))
			{
				builder.addPhoneNumber(id.getId());
			}
			else if (SSIdUtils.isUserId(id))
			{
				builder.addUserid(id.getId());
			}
			GetAccountResponse resp = dataDomainClientSvc.getAccount(
					getRpcController(), builder.build());
			if (resp.getAccountCount() > 0)
			{
				return resp.getAccount(0);
			}
			else
			{
				return null;
			}
		}
		else
		{
			throw new ServiceException("id没有类型");
		}
	}

	public GetFollowerResponse getFollower(NettyRpcController controller,
			GetFollowerRequest request)
	{
		String userid = RpcUtils.getUserId(controller, request);
		if (userid == null)
		{
			failed(controller, SSResultCode.RC_SERVICE_EXCEPTION.getNumber(),
					"未登录.");
			return null;
		}
		com.zhuaiwa.api.Rpc.GetFollowerRequest req = com.zhuaiwa.api.Rpc.GetFollowerRequest
				.newBuilder().mergeFrom(request).setUserid(userid).build();
		try
		{
			GetFollowerResponse response = dataDomainClientSvc.getFollower(
					getRpcController(), req);
			if (req.hasMode() && (req.getMode() == 1))
			{
				return SetProfileIconUrl(response);
			}
			return response;
		}
		catch (ServiceException e)
		{
			failed(controller, SSResultCode.RC_SERVICE_EXCEPTION.getNumber(),
					"查询粉丝列表失败.", e);
			return null;
		}
	}

	public GetFollowingResponse getFollowing(NettyRpcController controller,
			GetFollowingRequest request)
	{
		try
		{
			String userid = RpcUtils.getUserId(controller, request);
			if (userid == null)
			{
				failed(controller,
						SSResultCode.RC_SERVICE_EXCEPTION.getNumber(), "未登录.");
				return null;
			}
			com.zhuaiwa.api.Rpc.GetFollowingRequest req = com.zhuaiwa.api.Rpc.GetFollowingRequest
					.newBuilder().mergeFrom(request).setUserid(userid).build();
			GetFollowingResponse response = dataDomainClientSvc.getFollowing(
					getRpcController(), req);
			if (req.hasMode() && (req.getMode() == 1))
			{
				return SetProfileIconUrl(response);
			}
			return response;
		}
		catch (ServiceException e)
		{
			failed(controller, SSResultCode.RC_SERVICE_EXCEPTION.getNumber(),
					"查询关注列表失败.", e);
			return null;
		}
	}

	public GetInviterResponse getInviter(NettyRpcController controller,
			GetInviterRequest request)
	{
		String userid = request.getUserid();
		com.zhuaiwa.api.Rpc.GetInviterRequest req = com.zhuaiwa.api.Rpc.GetInviterRequest
				.newBuilder().setUserid(userid).mergeFrom(request).build();
		try
		{
			InetSocketAddress sa = (InetSocketAddress) controller
					.getChannelHandlerContext().getChannel().getRemoteAddress();
			String ip = sa.getAddress().getHostAddress();
			if (ip.equals("127.0.0.1"))
			{
				GetInviterResponse.Builder response = GetInviterResponse
						.newBuilder();
				response.addInviterUserList(getProfile(
						"389b01ce424f019046db730a11bd292e", 2));
				return response.build();
			}

			return dataDomainClientSvc.getInviter(getRpcController(), req);
		}
		catch (ServiceException e)
		{
			failed(controller, SSResultCode.RC_SERVICE_EXCEPTION.getNumber(),
					"查询邀请失败.", e);
			return null;
		}
	}

	public GetInvitingResponse getInviting(NettyRpcController controller,
			GetInvitingRequest request)
	{
		String userid = SessionManager.getInstance().getSession(
				controller.getApiHeader().getSid()).userid;
		com.zhuaiwa.api.Rpc.GetInvitingRequest req = com.zhuaiwa.api.Rpc.GetInvitingRequest
				.newBuilder().setUserid(userid).mergeFrom(request).build();
		try
		{
			return dataDomainClientSvc.getInviting(getRpcController(), req);
		}
		catch (ServiceException e)
		{
			failed(controller, SSResultCode.RC_SERVICE_EXCEPTION.getNumber(),
					"查询邀请失败.", e);
			return null;
		}
	}

	public GetLatestRegisterUserResponse getLatestRegisterUser(
			NettyRpcController controller, GetLatestRegisterUserRequest request)
	{
		GetLatestRegisterUserResponse.Builder getLatestRegisterUserResponse = GetLatestRegisterUserResponse
				.newBuilder();

		List<String> latestRegisterUserIdList = new ArrayList<String>();

		getLatestRegisterUserResponse
				.addAllUseridList(latestRegisterUserIdList);

		return getLatestRegisterUserResponse.build();
	}

	/**
	 * @return the messageService
	 */
	public MessageService_v_1_0 getMessageService()
	{
		return messageService;
	}

	public String getNickname(String userid)
	{
		if (userid == null)
		{
			return "";
		}
		SSProfile profile = getProfile(userid, 1);
		if ((profile != null) && profile.hasNickname())
		{
			return profile.getNickname();
		}
		return "";
	}

	public GetProfileResponse getProfile(NettyRpcController controller,
			GetProfileRequest request)
	{
		try
		{
			GetProfileResponse response = dataDomainClientSvc.getProfile(
					getRpcController(), request);
			if (response == null)
			{
				return null;
			}
			return SetProfileIconUrl(response);
		}
		catch (ServiceException e)
		{
			failed(controller, SSResultCode.RC_SERVICE_EXCEPTION.getNumber(),
					"查询个人资料失败.", e);
			return null;
		}
	}

	protected SSProfile getProfile(String userid, int flag)
	{
		try
		{
			GetProfileResponse response = dataDomainClientSvc.getProfile(
					getRpcController(), GetProfileRequest.newBuilder()
							.addUseridList(userid).setFlag(flag).build());
			if (response == null)
			{
				return null;
			}
			response = SetProfileIconUrl(response);
			if (response.getProfileListCount() > 0)
			{
				return response.getProfileList(0);
			}
			else
			{
				return null;
			}
		}
		catch (ServiceException e)
		{
			return null;
		}
	}

	public void getRecommendUser(NettyRpcController controller,
			GetRecommendUserRequest request,
			GetRecommendUserResponse.Builder response)
	{
		// Session session = SessionManager.getInstance().newSession(
		// request.getUserid(),
		// controller.getChannelHandlerContext().getChannel(),
		// SessionType.TCP);
		InetSocketAddress sa = (InetSocketAddress) controller
				.getChannelHandlerContext().getChannel().getRemoteAddress();
		String ip = sa.getAddress().getHostAddress();
		if (ip.equals("127.0.0.1"))
		{
			response.addRecommendUserList(getProfile(
					"389b01ce424f019046db730a11bd292e", 2));
			return;
		}
	}

	public GetSettingsResponse getSettings(NettyRpcController controller,
			GetSettingsRequest request)
	{
		try
		{
			Session session = SessionManager.getInstance().getSession(
					controller.getApiHeader().getSid());
			SSAccount account = getAccount(SSIdUtils.fromUserId(session.userid));

			return GetSettingsResponse.newBuilder()
					.setIsNicknameHidden(account.getIsNicknameHidden())
					.setIsEmailHidden(account.getIsEmailHidden())
					.setIsPhoneHidden(account.getIsPhoneHidden())
					.setIsEducationHidden(account.getIsEducationHidden())
					.setIsWorkHidden(account.getIsWorkHidden())
					.setMessageFilter(account.getMessageFilter()).build();
		}
		catch (ServiceException e)
		{
			failed(controller, SSResultCode.RC_SERVICE_EXCEPTION.getNumber(),
					"查询帐户失败.", e);
			return null;
		}
	}

	// public GetTopUserResponse getTopUser(NettyRpcController controller,
	// GetTopUserRequest request) {
	// GetTopUserResponse.Builder getTopUserResponse =
	// GetTopUserResponse.newBuilder();
	//
	// DataMiningService dms = DataMiningServiceImpl.getInstance();
	// NavigableMap<String, String> topusers = dms.getTopUser();
	// if (topusers == null)
	// {
	// return null;
	// }
	//
	// List<String> topUserIdList = new ArrayList<String>();
	//
	// for (String id : topusers.keySet()) {
	// String s = topusers.get(id);
	// String[] entries = s.split(";", 2);
	// if (entries.length != 2)
	// {
	// continue;
	// }
	// topUserIdList.add(entries[0]);
	// }
	// if ((topUserIdList == null) || (topUserIdList.size() == 0)) {
	// return getTopUserResponse.build();
	// }
	//
	// getTopUserResponse.addAllUseridList(topUserIdList);
	//
	// // 获取Profile
	// if (request.hasMode() && (request.getMode() > 0)) {
	// try {
	// GetProfileResponse getProfileResponse = dataDomainClientSvc.getProfile(
	// getRpcController(),
	// GetProfileRequest.newBuilder()
	// .addAllUseridList(topUserIdList)
	// .setFlag(1)
	// .build());
	// for (SSProfile profile : getProfileResponse.getProfileListList()) {
	// getTopUserResponse.addUserList(profile);
	// }
	// } catch (ServiceException e) {
	// failed(controller, SSResultCode.RC_SERVICE_EXCEPTION.getNumber(),
	// "查询Profile发生异常.", e);
	// return null;
	// }
	// }
	// return getTopUserResponse.build();
	// }
	public GetTopUserResponse getTopUser(NettyRpcController controller,
			GetTopUserRequest request)
	{
		GetTopUserResponse.Builder getTopUserResponse = GetTopUserResponse
				.newBuilder();

		List<String> topUserIdList = new ArrayList<String>();

		getTopUserResponse.addAllUseridList(topUserIdList);
		return getTopUserResponse.build();
	}

	public void getUserId(NettyRpcController controller,
			GetUserIdRequest request, GetUserIdResponse.Builder response)
	{
		try
		{
			SSAccount account = getAccount(request.getUser());
			if ((account != null) && (account.getIsActive() == 1))// 帐户不存在或未激活不返回
			{
				if (SSIdUtils.isEmailId(request.getUser()))
				{
					if (!account.hasIsEmailHidden()
							|| (account.getIsEmailHidden() != 1))
					{
						response.setUserid(account.getUserid());
					}
					else
					{
						LOG.debug("userid : " + account.getUserid()
								+ "设置成不允许根据email反查用户");
					}
				}
				if (SSIdUtils.isPhoneId(request.getUser()))
				{
					if (!account.hasIsPhoneHidden()
							|| (account.getIsPhoneHidden() != 1))
					{
						response.setUserid(account.getUserid());
					}
					else
					{
						LOG.debug("userid : " + account.getUserid()
								+ "设置成不允许根据手机号反查用户");
					}
				}
			}
			else
			{
				failed(controller,
						SSResultCode.RC_SERVICE_EXCEPTION.getNumber(), request
								.getUser().getId() + "帐户不存在.");
				return;
			}
		}
		catch (ServiceException e)
		{
			failed(controller, SSResultCode.RC_SERVICE_EXCEPTION.getNumber(),
					"查询帐户发生异常.", e);
			return;
		}
	}

	public InviteResponse invite(NettyRpcController controller,
			InviteRequest request)
	{
		String userid = SessionManager.getInstance().getSession(
				controller.getApiHeader().getSid()).userid;
		com.zhuaiwa.api.Rpc.InviteRequest req = com.zhuaiwa.api.Rpc.InviteRequest
				.newBuilder().mergeFrom(request).setInviterUserid(userid)
				.build();

		try
		{
			String inviterName = getNickname(userid);
			for (String user : request.getInvitingUserListList())
			{
				if (ValidatorUtil.isValidEmail(user))
				{
					MailUtil.sendMessage(user, "来自百库的邀请",
							FreemarkerUtil.getInviteMailOutput(inviterName));
				}
				else if (ValidatorUtil.isValidPhoneNumber(user))
				{
					SmsUtil.sendSms("你的朋友" + inviterName
							+ "邀请你逛逛百库。http://wap.baiku.cn", user);
				}
			}

			return dataDomainClientSvc.invite(getRpcController(), req);
		}
		catch (ServiceException e)
		{
			failed(controller, SSResultCode.RC_SERVICE_EXCEPTION.getNumber(),
					"邀请失败.", e);
			return null;
		}
	}

	public IsFollowerResponse isFollower(NettyRpcController controller,
			IsFollowerRequest request)
	{
		String userid = SessionManager.getInstance().getSession(
				controller.getApiHeader().getSid()).userid;
		com.zhuaiwa.api.Rpc.IsFollowerRequest req = com.zhuaiwa.api.Rpc.IsFollowerRequest
				.newBuilder().mergeFrom(request).setUserid(userid).build();
		try
		{
			return dataDomainClientSvc.isFollower(getRpcController(), req);
		}
		catch (ServiceException e)
		{
			failed(controller, SSResultCode.RC_SERVICE_EXCEPTION.getNumber(),
					"查询关注关系失败.", e);
			return null;
		}
	}

	// 密码为密文，不区分大小写
	public void login(NettyRpcController controller, LoginRequest request,
			LoginResponse.Builder response)
	{
		try
		{
			SSAccount account = getAccount(request.getUser());
			if (account == null)
			{// 帐户不存在
				failed(controller,
						SSResultCode.RC_SERVICE_EXCEPTION.getNumber(), "帐户不存在.");
				return;
			}
			else
			{
				if (!account.hasIsActive() || (account.getIsActive() != 1))
				{
					failed(controller,
							SSResultCode.RC_ACCOUNT_NOT_ACTIVATED.getNumber(),
							"帐号未激活");
					return;
				}
				if (!request.hasPassword()
						|| !request.getPassword().equalsIgnoreCase(
								account.getPassword()))
				{
					failed(controller,
							SSResultCode.RC_SERVICE_EXCEPTION.getNumber(),
							"帐号密码不匹配.");
					return;
				}
				else
				{// 密码验证完毕
					Session session = SessionManager.getInstance().newSession(
							account.getUserid(),
							controller.getChannelHandlerContext().getChannel(),
							SessionManager.getInstance().type);
					// 订阅公开分享
					try
					{
						com.zhuaiwa.api.Rpc.GetFollowingRequest req = com.zhuaiwa.api.Rpc.GetFollowingRequest
								.newBuilder().setUserid(account.getUserid())
								.setMode(0).build();
						List<String> userids = dataDomainClientSvc
								.getFollowing(getRpcController(), req)
								.getFollowingUseridListList();
						session.subscribe(TopicType.PS_, userids);
					}
					catch (ServiceException e)
					{
						e.printStackTrace();
					}

					// 订阅自己的收件箱
					session.subscribe(TopicType.RS_, account.getUserid());

					// 订阅状态和Profile
					try
					{
						com.zhuaiwa.api.Rpc.GetContactRequest req = com.zhuaiwa.api.Rpc.GetContactRequest
								.newBuilder().setUserid(account.getUserid())
								.build();
						com.zhuaiwa.api.Rpc.GetContactResponse resp = dataDomainClientSvc
								.getContact(getRpcController(), req);
						List<SSContact> contacts = resp.getContactListList();
						for (SSContact contact : contacts)
						{
							String userid = SSIdUtils.getId(
									contact.getIdListList(), SSIdDomain.userid);
							if (null != userid)
							{
								session.subscribe(TopicType.PP_, userid);
							}
						}
					}
					catch (ServiceException e)
					{
						e.printStackTrace();
					}
					// 登录模式：在线，0；隐身，1；
					if (request.hasMode() && (request.getMode() == 0))
					{
					}
					response.setSid(session.sid);
					response.addIdList(SSIdUtils.fromUserId(account.getUserid()));
					response.addAllIdList(account.getAliasIdListList());
					if (account.getIsFirstLogin() == 1)
					{
						response.setFirstLogin(account.getIsFirstLogin());
						// }
						// else
						// {
						// 修改为非第一次登录
						try
						{
							SSAccount that = SSAccount.newBuilder()
									.setIsFirstLogin(0).build();
							setAccount(account.getUserid(), that);
						}
						catch (Exception e)
						{
							if (LOG.isDebugEnabled())
							{
								LOG.debug("修改帐户非第一次登录出错", e);
							}
						}
					}
					controller.setApiHeader(ApiHeader
							.newBuilder(controller.getApiHeader())
							.setSid(session.sid).build());
				}
			}
		}
		catch (ServiceException e)
		{
			failed(controller, SSResultCode.RC_SERVICE_EXCEPTION.getNumber(),
					"查询帐户发生异常.", e);
			return;
		}
	}

	public void logout(NettyRpcController controller, LogoutRequest request,
			LogoutResponse.Builder response)
	{
		SessionManager.getInstance().destroySession(
				controller.getApiHeader().getSid());
	}

	// 根据传入的密码长度判断同时支持明文和密文密码
	public void register(NettyRpcController controller,
			RegisterAccountRequest request,
			RegisterAccountResponse.Builder response)
	{
		String userid = null;

		// if (request.hasNickname()
		// && !ValidatorUtil.isValidNickname(request.getNickname()))
		// {
		// failed(controller, SSResultCode.RC_SERVICE_EXCEPTION.getNumber(),
		// request.getNickname() + "不是有效的姓名.");
		// return;
		// }

		if (!ValidatorUtil.isValidPassword(request.getPassword()))
		{
			failed(controller, SSResultCode.RC_SERVICE_EXCEPTION.getNumber(),
					request.getPassword() + "不是有效的密码.");
			return;
		}

		if (!request.hasUser() || SSIdUtils.isEmpty(request.getUser()))
		{
			failed(controller, SSResultCode.RC_SERVICE_EXCEPTION.getNumber(),
					request.getUser() + "不是有效的用户名.");
			return;
		}

		boolean createdAccount = false; // 是否创建了新帐号
		String securityCode = genSecurityCode();
		LOG.debug("securityCode:" + securityCode);
		SSId id = request.getUser();
		if (SSIdUtils.isEmailId(id))
		{
			if (!ValidatorUtil.isValidEmail(id.getId()))
			{
				failed(controller,
						SSResultCode.RC_SERVICE_EXCEPTION.getNumber(),
						id.getId() + "不是有效的电子邮件地址.");
				return;
			}

			try
			{
				// 检查email是否已被注册
				SSAccount account = getAccount(id);
				if (account != null)
				{// 帐户已存在
					userid = account.getUserid();

					// 判断是否处于未激活状态
					if (account.getIsActive() == 1)
					{// 已激活
						failed(controller,
								SSResultCode.RC_SERVICE_EXCEPTION.getNumber(),
								id.getId() + "已注册.");
						return;
					}
				}
			}
			catch (ServiceException e)
			{
				failed(controller,
						SSResultCode.RC_SERVICE_EXCEPTION.getNumber(), "检查"
								+ id.getId() + "是否已注册发生异常.", e);
				return;
			}

			if (userid == null)
			{// 创建帐号
				try
				{
					CreateAccountRequest req = CreateAccountRequest
							.newBuilder().setEmail(id.getId().toLowerCase())
							.build();
					CreateAccountResponse resp = dataDomainClientSvc
							.createAccount(getRpcController(), req);
					if (resp.hasUserid())
					{
						userid = resp.getUserid();
						createdAccount = true;
					}
					else
					{
						throw new ServiceException(
								"createAccountResponse has no userid.");
					}
				}
				catch (ServiceException e)
				{
					failed(controller,
							SSResultCode.RC_SERVICE_EXCEPTION.getNumber(),
							"创建帐户发生异常.", e);
					return;
				}
			}

			try
			{
				// SSAccount account = SSAccount.newBuilder()
				// .setSecurityCode(securityCode)
				// .setPassword(DigestUtils.md5Hex(request.getPassword()))
				// .setSecurityCodeTime(System.currentTimeMillis())
				// .setRegisterTime(System.currentTimeMillis())
				// .setRegisterAgent(request.getAgent()).setIsActive(0)
				// .setIsFirstLogin(1).build();
				SSAccount.Builder ssab = SSAccount.newBuilder();
				ssab.setSecurityCode(securityCode)
						.setSecurityCodeTime(System.currentTimeMillis())
						.setRegisterTime(System.currentTimeMillis())
						.setRegisterAgent(request.getAgent()).setIsActive(0)
						.setIsFirstLogin(1);
				if (request.getPassword().length() == 32)
				{
					ssab.setPassword(request.getPassword());
				}
				else
				{
					ssab.setPassword(DigestUtils.md5Hex(request.getPassword()));
				}
				SSAccount account = ssab.build();
				setAccount(userid, account);
			}
			catch (ServiceException e)
			{
				failed(controller,
						SSResultCode.RC_SERVICE_EXCEPTION.getNumber(),
						"保存帐户信息发生异常.", e);
				return;
			}

			SSProfile profile = SSProfile.newBuilder()
					.setNickname(request.getNickname()).build();
			try
			{
				dataDomainClientSvc.setProfile(getRpcController(),
						com.zhuaiwa.api.Rpc.SetProfileRequest.newBuilder()
								.setUserid(userid).setProfile(profile).build());
			}
			catch (ServiceException e)
			{
				failed(controller,
						SSResultCode.RC_SERVICE_EXCEPTION.getNumber(),
						"保存个人资料发生异常.", e);
				return;
			}

			// 发送邮件
			MailUtil.sendMessage(
					id.getId(),
					"请激活你的账号以完成注册",
					FreemarkerUtil.getActiveCodeMailOutput(
							request.getNickname(), userid, securityCode));

			response.setUserid(userid);
		}
		else if (SSIdUtils.isPhoneId(id))
		{
			if (!ValidatorUtil.isValidPhoneNumber(id.getId()))
			{
				failed(controller,
						SSResultCode.RC_SERVICE_EXCEPTION.getNumber(),
						id.getId() + "不是有效的电话号码.");
				return;
			}

			try
			{
				// 检查phone是否已被注册
				SSAccount account = getAccount(id);
				if (account != null)
				{// 帐户已存在
					userid = account.getUserid();

					// 判断是否处于未激活状态
					if (account.getIsActive() == 1)
					{// 已激活
						failed(controller,
								SSResultCode.RC_SERVICE_EXCEPTION.getNumber(),
								id.getId() + "已注册.");
						return;
					}
				}
			}
			catch (ServiceException e)
			{
				failed(controller,
						SSResultCode.RC_SERVICE_EXCEPTION.getNumber(), "检查"
								+ id.getId() + "是否已注册发生异常.", e);
				return;
			}

			if (userid == null)
			{// 创建帐号
				try
				{
					CreateAccountRequest req = CreateAccountRequest
							.newBuilder().setPhoneNumber(id.getId()).build();
					CreateAccountResponse resp = dataDomainClientSvc
							.createAccount(getRpcController(), req);
					userid = resp.getUserid();
					createdAccount = true;
				}
				catch (ServiceException e)
				{
					failed(controller,
							SSResultCode.RC_SERVICE_EXCEPTION.getNumber(),
							"创建帐户发生异常.", e);
					return;
				}
			}

			try
			{
				SSAccount account = SSAccount.newBuilder()
						.setSecurityCode(securityCode)
						.setPassword(request.getPassword())
						.setSecurityCodeTime(System.currentTimeMillis())
						.build();
				setAccount(userid, account);
			}
			catch (ServiceException e)
			{
				failed(controller,
						SSResultCode.RC_SERVICE_EXCEPTION.getNumber(),
						"保存帐户信息发生异常.", e);
				return;
			}

			SSProfile profile = SSProfile.newBuilder()
					.setNickname(request.getNickname()).build();
			try
			{
				dataDomainClientSvc.setProfile(getRpcController(),
						com.zhuaiwa.api.Rpc.SetProfileRequest.newBuilder()
								.setUserid(userid).setProfile(profile).build());
			}
			catch (ServiceException e)
			{
				failed(controller,
						SSResultCode.RC_SERVICE_EXCEPTION.getNumber(),
						"保存个人资料发生异常.", e);
				return;
			}

			// 发送短信
			SmsUtil.sendSms("激活码:" + securityCode, id.getId());

			// 返回
			response.setUserid(userid);
		}
		else
		{
			failed(controller, SSResultCode.RC_SERVICE_EXCEPTION.getNumber(),
					"传入id's domain错误.");
			return;
		}

		if (createdAccount)
		{
			InetSocketAddress sa = (InetSocketAddress) controller
					.getChannelHandlerContext().getChannel().getRemoteAddress();
			sa.getAddress().getHostAddress();
		}
	}

	// 密码为明文
	public void resetPassword(NettyRpcController controller,
			ResetPasswordRequest request, ResetPasswordResponse.Builder response)
	{
		try
		{
			SSAccount account = getAccount(request.getUser());
			if (account == null)
			{
				failed(controller,
						SSResultCode.RC_SERVICE_EXCEPTION.getNumber(),
						"帐户不存在不能重置密码.");
				return;
			}
			else
			{
				if (account.getIsActive() != 1)
				{
					failed(controller,
							SSResultCode.RC_ACCOUNT_NOT_ACTIVATED.getNumber(),
							"帐户未激活不能重置密码.");
					return;
				}
				if ((System.currentTimeMillis() - account.getSecurityCodeTime()) > (2 * 60 * 60 * 1000L))
				{// 验证码超时无效
					failed(controller,
							SSResultCode.RC_SERVICE_EXCEPTION.getNumber(),
							"验证码超时无效.");
					return;
				}
				else
				{
					if (request.getCode() == null)
					{// 验证码为空
						failed(controller,
								SSResultCode.RC_SERVICE_EXCEPTION.getNumber(),
								"验证码不能为空.");
						return;
					}
					if (request.getNewPassword().isEmpty())
					{
						failed(controller,
								SSResultCode.RC_SERVICE_EXCEPTION.getNumber(),
								"新密码不能为空.");
						return;
					}
					if (request.getCode().equals(account.getSecurityCode()))
					{// 验证成功
						// SSAccount a = SSAccount
						// .newBuilder()
						// .setPassword(
						// DigestUtils.md5Hex(request
						// .getNewPassword()))
						// .setSecurityCodeTime(0).build();
						// 对传过来的新密码长度进行判断以同时支持传入明文或密文，兼顾网页（传明文）和客户端（传密文）重设密码
						SSAccount.Builder ssab = SSAccount.newBuilder();
						if (request.getNewPassword().length() == 32)
						{
							ssab.setPassword(request.getNewPassword());
						}
						else
						{
							ssab.setPassword(DigestUtils.md5Hex(request
									.getNewPassword()));
						}
						ssab.setSecurityCodeTime(0);
						SSAccount a = ssab.build();
						try
						{
							setAccount(account.getUserid(), a);
						}
						catch (ServiceException e)
						{
							failed(controller,
									SSResultCode.RC_SERVICE_EXCEPTION
											.getNumber(), "保存新密码发生异常.", e);
							return;
						}
					}
					else
					{// 验证码不匹配
						failed(controller,
								SSResultCode.RC_SERVICE_EXCEPTION.getNumber(),
								"验证码不匹配.");
						return;
					}
				}
			}
		}
		catch (ServiceException e)
		{
			failed(controller, SSResultCode.RC_SERVICE_EXCEPTION.getNumber(),
					"查询帐户发生异常.", e);
			return;
		}
	}

	public SearchUserResponse searchUser(NettyRpcController controller,
			SearchUserRequest request)
	{
		SearchResult result = new Searcher().searchUser(request
				.getQueryString());
		return SearchUserResponse.newBuilder().setTotal(result.getTotal())
				.addAllUseridList(result.getIds()).build();
	}

	public SendActivateCodeResponse sendActivateCode(
			NettyRpcController controller, SendActivateCodeRequest request)
	{
		try
		{
			SSAccount account = getAccount(request.getUser());
			if (account == null)
			{
				failed(controller,
						SSResultCode.RC_SERVICE_EXCEPTION.getNumber(),
						"帐户不存在不能找回密码.");
				return null;
			}
			else
			{
				if (account.getIsActive() == 1)
				{
					failed(controller,
							SSResultCode.RC_SERVICE_EXCEPTION.getNumber(),
							"帐户已激活不能再发验证码.");
					return null;
				}

				String securityCode = genSecurityCode();
				if (SSIdUtils.isEmailId(request.getUser()))
				{
					String name = getNickname(account.getUserid());

					// 发送邮件
					MailUtil.sendMessage(request.getUser().getId(), "激活账号:",
							FreemarkerUtil.getActiveCodeMailOutput(name,
									account.getUserid(), securityCode));

				}
				else if (SSIdUtils.isPhoneId(request.getUser()))
				{
					// 发送短信
					SmsUtil.sendSms("激活码:" + securityCode, request.getUser()
							.getId());
				}

				account = SSAccount.newBuilder().setUserid(account.getUserid())
						.setSecurityCode(securityCode)
						.setSecurityCodeTime(System.currentTimeMillis())
						.build();
				setAccount(account.getUserid(), account);

				return SendActivateCodeResponse.newBuilder().build();
			}
		}
		catch (ServiceException e)
		{
			failed(controller, SSResultCode.RC_SERVICE_EXCEPTION.getNumber(),
					"查询帐户发生异常.", e);
			return null;
		}
	}

	protected void setAccount(String userid, com.zhuaiwa.api.Common.SSAccount a)
			throws ServiceException
	{
		SetAccountRequest req = SetAccountRequest.newBuilder()
				.setUserid(userid).setAccount(a).build();
		dataDomainClientSvc.setAccount(getRpcController(), req);
	}

	public void setDataDomainClientSvc(BlockingInterface dataDomainClientSvc)
	{
		this.dataDomainClientSvc = dataDomainClientSvc;
	}

	/**
	 * @param messageService
	 *            the messageService to set
	 */
	public void setMessageService(MessageService_v_1_0 messageService)
	{
		this.messageService = messageService;
	}

	public void setProfile(NettyRpcController controller,
			SetProfileRequest request, SetProfileResponse.Builder response)
	{
		// if (request.hasProfile()
		// && request.getProfile().hasNickname()
		// && !ValidatorUtil.isValidNickname(request.getProfile()
		// .getNickname()))
		// {
		// failed(controller, SSResultCode.RC_SERVICE_EXCEPTION.getNumber(),
		// request.getProfile().getNickname() + "不是有效的姓名.");
		// return;
		// }

		if (request.hasProfile() && request.getProfile().hasIntroduction()
				&& (request.getProfile().getIntroduction().length() > 140))
		{
			failed(controller, SSResultCode.RC_SERVICE_EXCEPTION.getNumber(),
					"自我介绍内容过多.");
			return;
		}

		Session session = SessionManager.getInstance().getSession(
				controller.getApiHeader().getSid());
		com.zhuaiwa.api.Rpc.SetProfileRequest req = com.zhuaiwa.api.Rpc.SetProfileRequest
				.newBuilder().mergeFrom(request).setUserid(session.userid)
				.build();
		try
		{
			dataDomainClientSvc.setProfile(getRpcController(), req);
		}
		catch (ServiceException e)
		{
			failed(controller, SSResultCode.RC_SERVICE_EXCEPTION.getNumber(),
					"设置个人资料失败.", e);
			return;
		}
	}

	public SetSettingsResponse setSettings(NettyRpcController controller,
			SetSettingsRequest request)
	{
		try
		{
			Session session = SessionManager.getInstance().getSession(
					controller.getApiHeader().getSid());
			SSAccount.Builder ab = SSAccount.newBuilder();
			if (request.hasIsNicknameHidden())
			{
				ab.setIsNicknameHidden(request.getIsNicknameHidden());
			}
			if (request.hasIsEmailHidden())
			{
				ab.setIsEmailHidden(request.getIsEmailHidden());
			}
			if (request.hasIsPhoneHidden())
			{
				ab.setIsPhoneHidden(request.getIsPhoneHidden());
			}
			if (request.hasIsEducationHidden())
			{
				ab.setIsEducationHidden(request.getIsEducationHidden());
			}
			if (request.hasIsWorkHidden())
			{
				ab.setIsWorkHidden(request.getIsWorkHidden());
			}
			if (request.hasMessageFilter())
			{
				ab.setMessageFilter(request.getMessageFilter());
			}

			SetAccountRequest req = SetAccountRequest.newBuilder()
					.setUserid(session.userid).setAccount(ab.build()).build();
			dataDomainClientSvc.setAccount(getRpcController(), req);

			return SetSettingsResponse.newBuilder().build();
		}
		catch (ServiceException e)
		{
			failed(controller, SSResultCode.RC_SERVICE_EXCEPTION.getNumber(),
					"设置帐户失败.", e);
			return null;
		}
	}

	public void unbindAccount(NettyRpcController controller,
			UnbindAccountRequest request, UnbindAccountResponse.Builder response)
	{
		Session session = SessionManager.getInstance().getSession(
				controller.getApiHeader().getSid());
		SSAccount account = null;
		try
		{
			account = getAccount(SSId.newBuilder()
					.setDomain(SSIdUtils.SSIdDomain.userid.name())
					.setId(session.userid).build());
			if (account == null)
			{
				failed(controller,
						SSResultCode.RC_SERVICE_EXCEPTION.getNumber(), "帐户不存在.");
				return;
			}
			else
			{
				String id = SSIdUtils.getId(account.getAliasIdListList(),
						SSIdUtils.getDomain(request.getOldId().getDomain()));
				if (id == null)
				{
					failed(controller,
							SSResultCode.RC_SERVICE_EXCEPTION.getNumber(),
							"不存在的绑定.");
					return;
				}
				else
				{
					if (!id.equals(request.getOldId().getId()))
					{
						failed(controller,
								SSResultCode.RC_SERVICE_EXCEPTION.getNumber(),
								"待取消绑定帐号与系统中已存在的账号不匹配.");
						return;
					}
				}
				/*临时注释
								if (account.getAliasIdListList().size() <= 1)
								{
									failed(controller,
											SSResultCode.RC_SERVICE_EXCEPTION.getNumber(),
											"一个账户至少有一个绑定.");
									return;
								}
				*/
			}
		}
		catch (ServiceException e)
		{
			failed(controller, SSResultCode.RC_SERVICE_EXCEPTION.getNumber(),
					"查询帐户发生异常.", e);
			return;
		}

		SSId id = request.getOldId();
		try
		{
			com.zhuaiwa.api.Rpc.UnbindAccountRequest req = com.zhuaiwa.api.Rpc.UnbindAccountRequest
					.newBuilder()
					.setUserid(session.userid)
					.setOldId(
							SSId.newBuilder()
									.setDomain(request.getOldId().getDomain())
									.setId(request.getOldId().getId()
											.toLowerCase()).build()).build();
			dataDomainClientSvc.unbindAccount(getRpcController(), req);
		}
		catch (ServiceException e)
		{
			failed(controller, SSResultCode.RC_SERVICE_EXCEPTION.getNumber(),
					id.getId() + "取消绑定帐户失败.", e);
			return;
		}
	}

	public void unfollow(NettyRpcController controller,
			UnfollowRequest request, UnfollowResponse.Builder response)
	{
		Session session = SessionManager.getInstance().getSession(
				controller.getApiHeader().getSid());
		session.unSubscribe(TopicType.PS_, request.getFollowingUseridListList());
		com.zhuaiwa.api.Rpc.UnfollowRequest req = com.zhuaiwa.api.Rpc.UnfollowRequest
				.newBuilder().setFollowerUserid(session.userid)
				.mergeFrom(request).build();
		try
		{
			dataDomainClientSvc.unfollow(getRpcController(), req);

			// 更新baikuService的缓存
			for (String followingId : request.getFollowingUseridListList())
			{
				BaseService.onUnfollow(this, controller,
						request.getFollowerUserid(), followingId);
			}

		}
		catch (ServiceException e)
		{
			failed(controller, SSResultCode.RC_SERVICE_EXCEPTION.getNumber(),
					"取消关注失败.", e);
			return;
		}
	}

}
