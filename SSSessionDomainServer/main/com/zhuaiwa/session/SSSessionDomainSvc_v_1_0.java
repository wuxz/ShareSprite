package com.zhuaiwa.session;

import java.util.HashSet;
import java.util.Set;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.google.protobuf.Descriptors.EnumValueDescriptor;
import com.google.protobuf.Descriptors.FieldDescriptor;
import com.google.protobuf.Descriptors.MethodDescriptor;
import com.google.protobuf.Message;
import com.google.protobuf.RpcCallback;
import com.google.protobuf.RpcController;
import com.zhuaiwa.api.Common.SSBOX;
import com.zhuaiwa.api.Common.SSResultCode;
import com.zhuaiwa.api.Rpc.AddContactResponse;
import com.zhuaiwa.api.Rpc.AddGroupResponse;
import com.zhuaiwa.api.Rpc.AddMemberResponse;
import com.zhuaiwa.api.Rpc.FollowResponse;
import com.zhuaiwa.api.Rpc.GetTorrentRequest;
import com.zhuaiwa.api.Rpc.GetTorrentResponse;
import com.zhuaiwa.api.Rpc.RemoveContactResponse;
import com.zhuaiwa.api.Rpc.RemoveGroupResponse;
import com.zhuaiwa.api.Rpc.RemoveMemberResponse;
import com.zhuaiwa.api.Rpc.RemoveMessageResponse;
import com.zhuaiwa.api.Rpc.SendMessageResponse;
import com.zhuaiwa.api.Rpc.SetProfileResponse;
import com.zhuaiwa.api.Rpc.SetTorrentRequest;
import com.zhuaiwa.api.Rpc.SetTorrentResponse;
import com.zhuaiwa.api.Rpc.UnbindAccountResponse;
import com.zhuaiwa.api.Rpc.UnfollowResponse;
import com.zhuaiwa.api.SSSessionDomain.ActivateAccountResponse;
import com.zhuaiwa.api.SSSessionDomain.BindAccountOkRequest;
import com.zhuaiwa.api.SSSessionDomain.BindAccountOkResponse;
import com.zhuaiwa.api.SSSessionDomain.ChangePasswordRequest;
import com.zhuaiwa.api.SSSessionDomain.ChangePasswordResponse;
import com.zhuaiwa.api.SSSessionDomain.FollowTagResponse;
import com.zhuaiwa.api.SSSessionDomain.ForgetPasswordResponse;
import com.zhuaiwa.api.SSSessionDomain.GetFollowingTagResponse;
import com.zhuaiwa.api.SSSessionDomain.GetLatestRegisterUserRequest;
import com.zhuaiwa.api.SSSessionDomain.GetLatestRegisterUserResponse;
import com.zhuaiwa.api.SSSessionDomain.GetLatestShareRequest;
import com.zhuaiwa.api.SSSessionDomain.GetLatestShareResponse;
import com.zhuaiwa.api.SSSessionDomain.GetMessageByTagResponse;
import com.zhuaiwa.api.SSSessionDomain.GetNumberAttributionRequest;
import com.zhuaiwa.api.SSSessionDomain.GetNumberAttributionResponse;
import com.zhuaiwa.api.SSSessionDomain.GetRecommendUserResponse;
import com.zhuaiwa.api.SSSessionDomain.GetSessionInfoRequest;
import com.zhuaiwa.api.SSSessionDomain.GetSessionInfoResponse;
import com.zhuaiwa.api.SSSessionDomain.GetSettingsRequest;
import com.zhuaiwa.api.SSSessionDomain.GetSettingsResponse;
import com.zhuaiwa.api.SSSessionDomain.GetSystemInfoRequest;
import com.zhuaiwa.api.SSSessionDomain.GetSystemInfoResponse;
import com.zhuaiwa.api.SSSessionDomain.GetTopContentRequest;
import com.zhuaiwa.api.SSSessionDomain.GetTopContentResponse;
import com.zhuaiwa.api.SSSessionDomain.GetTopTagRequest;
import com.zhuaiwa.api.SSSessionDomain.GetTopTagResponse;
import com.zhuaiwa.api.SSSessionDomain.GetTopUserRequest;
import com.zhuaiwa.api.SSSessionDomain.GetTopUserResponse;
import com.zhuaiwa.api.SSSessionDomain.GetUserIdResponse;
import com.zhuaiwa.api.SSSessionDomain.LoginResponse;
import com.zhuaiwa.api.SSSessionDomain.LogoutResponse;
import com.zhuaiwa.api.SSSessionDomain.RegisterAccountResponse;
import com.zhuaiwa.api.SSSessionDomain.ResetPasswordResponse;
import com.zhuaiwa.api.SSSessionDomain.SSSessionDomainSvc.Interface;
import com.zhuaiwa.api.SSSessionDomain.SearchMessageRequest;
import com.zhuaiwa.api.SSSessionDomain.SearchMessageResponse;
import com.zhuaiwa.api.SSSessionDomain.SearchUserRequest;
import com.zhuaiwa.api.SSSessionDomain.SearchUserResponse;
import com.zhuaiwa.api.SSSessionDomain.SendActivateCodeRequest;
import com.zhuaiwa.api.SSSessionDomain.SendActivateCodeResponse;
import com.zhuaiwa.api.SSSessionDomain.SetSettingsRequest;
import com.zhuaiwa.api.SSSessionDomain.SetSettingsResponse;
import com.zhuaiwa.api.SSSessionDomain.SuggestRequest;
import com.zhuaiwa.api.SSSessionDomain.SuggestResponse;
import com.zhuaiwa.api.SSSessionDomain.UnfollowTagResponse;
import com.zhuaiwa.api.netty.Authenticateable;
import com.zhuaiwa.api.netty.NettyRpcController;
import com.zhuaiwa.session.SessionManager.Session;
import com.zhuaiwa.session.service.BaikuTelServiceImpl;
import com.zhuaiwa.session.service.ContactService_v_1_0;
import com.zhuaiwa.session.service.MessageService_v_1_0;
import com.zhuaiwa.session.service.UserService_v_1_0;
import com.zhuaiwa.util.MailUtil;

public class SSSessionDomainSvc_v_1_0 implements Interface, Authenticateable
{

	private static final Set<String> AuthenticateMethods = new HashSet<String>();

	private static final Logger logger = LoggerFactory
			.getLogger(SSSessionDomainSvc_v_1_0.class);
	static
	{
		AuthenticateMethods.add("ChangePassword");
		AuthenticateMethods.add("BindAccount");
		AuthenticateMethods.add("UnbindAccount");
		AuthenticateMethods.add("SetProfile");
		AuthenticateMethods.add("GetLastTime");
		AuthenticateMethods.add("SetLastTime");
		AuthenticateMethods.add("Follow");
		AuthenticateMethods.add("CancelFollow");
		AuthenticateMethods.add("Invite");
		AuthenticateMethods.add("GetMessage");
		AuthenticateMethods.add("SendMessage");
		AuthenticateMethods.add("RemoveMessage");
		AuthenticateMethods.add("AddContact");
		AuthenticateMethods.add("RemoveContact");
		AuthenticateMethods.add("GetContact");
		AuthenticateMethods.add("AddGroup");
		AuthenticateMethods.add("RemoveGroup");
		AuthenticateMethods.add("GetGroup");
		AuthenticateMethods.add("AddMember");
		AuthenticateMethods.add("RemoveMember");
		AuthenticateMethods.add("GetMember");
		AuthenticateMethods.add("GetSessionInfo");
	}

	BaikuTelServiceImpl baikuTelService;

	ContactService_v_1_0 contactService;

	MessageService_v_1_0 messageService;

	long TIMEOUT = 15000;

	// ClientManager clientManager;
	UserService_v_1_0 userService;

	@Override
	public void activateAccount(
			com.google.protobuf.RpcController controller,
			com.zhuaiwa.api.SSSessionDomain.ActivateAccountRequest request,
			com.google.protobuf.RpcCallback<com.zhuaiwa.api.SSSessionDomain.ActivateAccountResponse> done)
	{

		ActivateAccountResponse.Builder response = ActivateAccountResponse
				.newBuilder();

		try
		{

			userService.activateAccount((NettyRpcController) controller,
					request, response);
			done.run(response.build());

		}
		catch (Exception e)
		{
			logger.error("", e);

			((NettyRpcController) controller)
					.setCode(SSResultCode.RC_SERVER_INTERNAL_ERROR.getNumber());
			((NettyRpcController) controller).setFailed(e.getMessage());
			done.run(response.build());
		}
	}

	@Override
	public void addContact(
			com.google.protobuf.RpcController controller,
			com.zhuaiwa.api.Rpc.AddContactRequest request,
			com.google.protobuf.RpcCallback<com.zhuaiwa.api.Rpc.AddContactResponse> done)
	{

		AddContactResponse.Builder response = AddContactResponse.newBuilder();

		try
		{

			done.run(contactService.addContact((NettyRpcController) controller,
					request));

		}
		catch (Exception e)
		{
			logger.error("", e);

			((NettyRpcController) controller)
					.setCode(SSResultCode.RC_SERVER_INTERNAL_ERROR.getNumber());
			((NettyRpcController) controller).setFailed(e.getMessage());
			done.run(response.build());
		}
	}

	@Override
	public void addGroup(
			com.google.protobuf.RpcController controller,
			com.zhuaiwa.api.Rpc.AddGroupRequest request,
			com.google.protobuf.RpcCallback<com.zhuaiwa.api.Rpc.AddGroupResponse> done)
	{

		AddGroupResponse.Builder response = AddGroupResponse.newBuilder();

		try
		{

			done.run(contactService.addGroup((NettyRpcController) controller,
					request));

		}
		catch (Exception e)
		{
			logger.error("", e);

			((NettyRpcController) controller)
					.setCode(SSResultCode.RC_SERVER_INTERNAL_ERROR.getNumber());
			((NettyRpcController) controller).setFailed(e.getMessage());
			done.run(response.build());
		}
	}

	@Override
	public void addMember(
			com.google.protobuf.RpcController controller,
			com.zhuaiwa.api.Rpc.AddMemberRequest request,
			com.google.protobuf.RpcCallback<com.zhuaiwa.api.Rpc.AddMemberResponse> done)
	{

		AddMemberResponse.Builder response = AddMemberResponse.newBuilder();

		try
		{

			done.run(contactService.addMember((NettyRpcController) controller,
					request));

		}
		catch (Exception e)
		{
			logger.error("", e);

			((NettyRpcController) controller)
					.setCode(SSResultCode.RC_SERVER_INTERNAL_ERROR.getNumber());
			((NettyRpcController) controller).setFailed(e.getMessage());
			done.run(response.build());
		}
	}

	@Override
	public boolean authenticate(MethodDescriptor methodDescriptor,
			NettyRpcController controller, Message request,
			RpcCallback<Message> done)
	{

		// logger.info("authenticate................a");

		boolean auth = false;
		String methodName = methodDescriptor.getName();

		auth = AuthenticateMethods.contains(methodName);
		if (methodName.equals("GetMessage"))
		{
			FieldDescriptor fd_source_box = methodDescriptor.getInputType()
					.findFieldByName("source_box");
			// Object sb = request.getField(fd_source_box);
			// logger.info(sb.toString());
			if (request.hasField(fd_source_box)
					&& (((EnumValueDescriptor) request.getField(fd_source_box))
							.getNumber() == SSBOX.PUBBOX.getNumber()))
			{
				auth = false;
			}
		}

		if (auth)
		{
			String sid = controller.getApiHeader().getSid();
			if (!SessionManager.getInstance().sessionExist(sid))
			{
				com.google.protobuf.GeneratedMessage.Builder<?> response = methodDescriptor
						.getOutputType().toProto().newBuilderForType();
				controller.setCode(SSResultCode.RC_NOT_LOGIN
						.getNumber());
				controller.setFailed(sid == null ? "请求中未携带sid" : "sid失效，请重新登录");
				done.run(response.build());
				return false;
			}
		}
		else
		{
			// logger.info("authenticate................b");
			FieldDescriptor fd = methodDescriptor.getInputType()
					.findFieldByName("userid");
			if (fd != null)
			{
				// logger.info("authenticate................c");
				String userid = null;
				Session session = SessionManager.getInstance().getSession(
						controller.getApiHeader().getSid());
				if (session != null)
				{
					userid = session.userid;
				}
				else if (request.hasField(fd))
				{
					userid = (String) request.getField(fd);
				}
				if (userid == null)
				{
					// logger.info("authenticate................d");
					controller.setCode(SSResultCode.RC_NOT_LOGIN.getNumber());
					controller.setFailed("未登录");
					com.google.protobuf.GeneratedMessage.Builder<?> response = methodDescriptor
							.getOutputType().toProto().newBuilderForType();
					done.run(response.build());
					return false;
				}
			}
		}
		return true;
	}

	@Override
	public void bindAccount(
			com.google.protobuf.RpcController controller,
			com.zhuaiwa.api.Rpc.BindAccountRequest request,
			com.google.protobuf.RpcCallback<com.zhuaiwa.api.Rpc.BindAccountResponse> done)
	{
		done.run(userService.bindAccount((NettyRpcController) controller,
				request));
	}

	@Override
	public void bindAccountOk(RpcController controller,
			BindAccountOkRequest request,
			RpcCallback<BindAccountOkResponse> done)
	{
		done.run(userService.bindAccountOk((NettyRpcController) controller,
				request));
	}

	@Override
	public void changePassword(RpcController controller,
			ChangePasswordRequest request,
			RpcCallback<ChangePasswordResponse> done)
	{
		done.run(userService.changePassword((NettyRpcController) controller,
				request));
	}

	public void Dispose()
	{
	}

	@Override
	public void follow(
			com.google.protobuf.RpcController controller,
			com.zhuaiwa.api.Rpc.FollowRequest request,
			com.google.protobuf.RpcCallback<com.zhuaiwa.api.Rpc.FollowResponse> done)
	{

		FollowResponse.Builder response = FollowResponse.newBuilder();

		try
		{

			userService.follow((NettyRpcController) controller, request,
					response);
			done.run(response.build());

		}
		catch (Exception e)
		{
			logger.error("", e);

			((NettyRpcController) controller)
					.setCode(SSResultCode.RC_SERVER_INTERNAL_ERROR.getNumber());
			((NettyRpcController) controller).setFailed(e.getMessage());
			done.run(response.build());
		}
	}

	@Override
	public void followTag(
			com.google.protobuf.RpcController controller,
			com.zhuaiwa.api.SSSessionDomain.FollowTagRequest request,
			com.google.protobuf.RpcCallback<com.zhuaiwa.api.SSSessionDomain.FollowTagResponse> done)
	{

		FollowTagResponse.Builder response = FollowTagResponse.newBuilder();

		try
		{

			messageService.followTag((NettyRpcController) controller, request,
					response);
			done.run(response.build());

		}
		catch (Exception e)
		{
			logger.error("", e);

			((NettyRpcController) controller)
					.setCode(SSResultCode.RC_SERVER_INTERNAL_ERROR.getNumber());
			((NettyRpcController) controller).setFailed(e.getMessage());
			done.run(response.build());
		}
	}

	@Override
	public void forgetPassword(
			com.google.protobuf.RpcController controller,
			com.zhuaiwa.api.SSSessionDomain.ForgetPasswordRequest request,
			com.google.protobuf.RpcCallback<com.zhuaiwa.api.SSSessionDomain.ForgetPasswordResponse> done)
	{

		ForgetPasswordResponse.Builder response = ForgetPasswordResponse
				.newBuilder();

		try
		{

			userService.forgetPassword((NettyRpcController) controller,
					request, response);
			done.run(response.build());

		}
		catch (Exception e)
		{
			logger.error("", e);

			((NettyRpcController) controller)
					.setCode(SSResultCode.RC_SERVER_INTERNAL_ERROR.getNumber());
			((NettyRpcController) controller).setFailed(e.getMessage());
			done.run(response.build());
		}
	}

	@Override
	public void getContact(
			com.google.protobuf.RpcController controller,
			com.zhuaiwa.api.Rpc.GetContactRequest request,
			com.google.protobuf.RpcCallback<com.zhuaiwa.api.Rpc.GetContactResponse> done)
	{
		done.run(contactService.getContact((NettyRpcController) controller,
				request));
	}

	@Override
	public void getFollower(
			com.google.protobuf.RpcController controller,
			com.zhuaiwa.api.Rpc.GetFollowerRequest request,
			com.google.protobuf.RpcCallback<com.zhuaiwa.api.Rpc.GetFollowerResponse> done)
	{
		done.run(userService.getFollower((NettyRpcController) controller,
				request));
	}

	@Override
	public void getFollowing(
			com.google.protobuf.RpcController controller,
			com.zhuaiwa.api.Rpc.GetFollowingRequest request,
			com.google.protobuf.RpcCallback<com.zhuaiwa.api.Rpc.GetFollowingResponse> done)
	{
		done.run(userService.getFollowing((NettyRpcController) controller,
				request));
	}

	@Override
	public void getFollowingTag(
			com.google.protobuf.RpcController controller,
			com.zhuaiwa.api.SSSessionDomain.GetFollowingTagRequest request,
			com.google.protobuf.RpcCallback<com.zhuaiwa.api.SSSessionDomain.GetFollowingTagResponse> done)
	{

		GetFollowingTagResponse.Builder response = GetFollowingTagResponse
				.newBuilder();

		try
		{

			messageService.getFollowingTag((NettyRpcController) controller,
					request, response);
			done.run(response.build());

		}
		catch (Exception e)
		{
			logger.error("", e);

			((NettyRpcController) controller)
					.setCode(SSResultCode.RC_SERVER_INTERNAL_ERROR.getNumber());
			((NettyRpcController) controller).setFailed(e.getMessage());
			done.run(response.build());
		}
	}

	@Override
	public void getGroup(
			com.google.protobuf.RpcController controller,
			com.zhuaiwa.api.Rpc.GetGroupRequest request,
			com.google.protobuf.RpcCallback<com.zhuaiwa.api.Rpc.GetGroupResponse> done)
	{
		done.run(contactService.getGroup((NettyRpcController) controller,
				request));
	}

	@Override
	public void getInviter(
			com.google.protobuf.RpcController controller,
			com.zhuaiwa.api.Rpc.GetInviterRequest request,
			com.google.protobuf.RpcCallback<com.zhuaiwa.api.Rpc.GetInviterResponse> done)
	{
		done.run(userService.getInviter((NettyRpcController) controller,
				request));
	}

	@Override
	public void getInviting(
			com.google.protobuf.RpcController controller,
			com.zhuaiwa.api.Rpc.GetInvitingRequest request,
			com.google.protobuf.RpcCallback<com.zhuaiwa.api.Rpc.GetInvitingResponse> done)
	{
		done.run(userService.getInviting((NettyRpcController) controller,
				request));
	}

	@Override
	public void getLatestRegisterUser(RpcController controller,
			GetLatestRegisterUserRequest request,
			RpcCallback<GetLatestRegisterUserResponse> done)
	{
		done.run(userService.getLatestRegisterUser(
				(NettyRpcController) controller, request));
	}

	@Override
	public void getLatestShare(RpcController controller,
			GetLatestShareRequest request,
			RpcCallback<GetLatestShareResponse> done)
	{
		done.run(messageService.getLatestShare((NettyRpcController) controller,
				request));
	}

	@Override
	public void getMember(
			com.google.protobuf.RpcController controller,
			com.zhuaiwa.api.Rpc.GetMemberRequest request,
			com.google.protobuf.RpcCallback<com.zhuaiwa.api.Rpc.GetMemberResponse> done)
	{
		done.run(contactService.getMember((NettyRpcController) controller,
				request));
	}

	@Override
	public void getMessage(
			com.google.protobuf.RpcController controller,
			com.zhuaiwa.api.Rpc.GetMessageRequest request,
			com.google.protobuf.RpcCallback<com.zhuaiwa.api.Rpc.GetMessageResponse> done)
	{
		done.run(messageService.getMessage((NettyRpcController) controller,
				request));
	}

	@Override
	public void getMessageById(
			com.google.protobuf.RpcController controller,
			com.zhuaiwa.api.Rpc.GetMessageByIdRequest request,
			com.google.protobuf.RpcCallback<com.zhuaiwa.api.Rpc.GetMessageByIdResponse> done)
	{
		done.run(messageService.getMessageById((NettyRpcController) controller,
				request));
	}

	@Override
	public void getMessageByTag(
			com.google.protobuf.RpcController controller,
			com.zhuaiwa.api.SSSessionDomain.GetMessageByTagRequest request,
			com.google.protobuf.RpcCallback<com.zhuaiwa.api.SSSessionDomain.GetMessageByTagResponse> done)
	{

		GetMessageByTagResponse.Builder response = GetMessageByTagResponse
				.newBuilder();

		try
		{

			messageService.getMessageByTag((NettyRpcController) controller,
					request, response);
			done.run(response.build());

		}
		catch (Exception e)
		{
			logger.error("", e);

			((NettyRpcController) controller)
					.setCode(SSResultCode.RC_SERVER_INTERNAL_ERROR.getNumber());
			((NettyRpcController) controller).setFailed(e.getMessage());
			done.run(response.build());
		}
	}

	@Override
	public void getMessageByTimestamp(
			com.google.protobuf.RpcController controller,
			com.zhuaiwa.api.Rpc.GetMessageByTimestampRequest request,
			com.google.protobuf.RpcCallback<com.zhuaiwa.api.Rpc.GetMessageByTimestampResponse> done)
	{
		done.run(messageService.getMessageByTimestamp(
				(NettyRpcController) controller, request));
	}

	/* (non-Javadoc)
	 * @see com.zhuaiwa.api.SSSessionDomain.SSSessionDomainSvc.Interface#getNumberAttribution(com.google.protobuf.RpcController, com.zhuaiwa.api.SSSessionDomain.GetNumberAttributionRequest, com.google.protobuf.RpcCallback)
	 */
	@Override
	public void getNumberAttribution(RpcController controller,
			GetNumberAttributionRequest request,
			RpcCallback<GetNumberAttributionResponse> done)
	{
		done.run(baikuTelService.getNumberAttribution(
				(NettyRpcController) controller, request));
	}

	@Override
	public void getProfile(
			com.google.protobuf.RpcController controller,
			com.zhuaiwa.api.Rpc.GetProfileRequest request,
			com.google.protobuf.RpcCallback<com.zhuaiwa.api.Rpc.GetProfileResponse> done)
	{
		done.run(userService.getProfile((NettyRpcController) controller,
				request));
	}

	@Override
	public void getRecommendUser(
			com.google.protobuf.RpcController controller,
			com.zhuaiwa.api.SSSessionDomain.GetRecommendUserRequest request,
			com.google.protobuf.RpcCallback<com.zhuaiwa.api.SSSessionDomain.GetRecommendUserResponse> done)
	{

		GetRecommendUserResponse.Builder response = GetRecommendUserResponse
				.newBuilder();

		try
		{

			userService.getRecommendUser((NettyRpcController) controller,
					request, response);
			done.run(response.build());

		}
		catch (Exception e)
		{
			logger.error("", e);

			((NettyRpcController) controller)
					.setCode(SSResultCode.RC_SERVER_INTERNAL_ERROR.getNumber());
			((NettyRpcController) controller).setFailed(e.getMessage());
			done.run(response.build());
		}
	}

	/* (non-Javadoc)
	 * @see com.zhuaiwa.api.SSSessionDomain.SSSessionDomainSvc.Interface#getSessionInfo(com.google.protobuf.RpcController, com.zhuaiwa.api.SSSessionDomain.GetSessionInfoRequest, com.google.protobuf.RpcCallback)
	 */
	@Override
	public void getSessionInfo(RpcController controller,
			GetSessionInfoRequest request,
			RpcCallback<GetSessionInfoResponse> done)
	{
		done.run(baikuTelService.getSessionInfo(
				(NettyRpcController) controller, request));
	}

	@Override
	public void getSettings(RpcController controller,
			GetSettingsRequest request, RpcCallback<GetSettingsResponse> done)
	{
		done.run(userService.getSettings((NettyRpcController) controller,
				request));
	}

	/* (non-Javadoc)
	 * @see com.zhuaiwa.api.SSSessionDomain.SSSessionDomainSvc.Interface#getSystemInfo(com.google.protobuf.RpcController, com.zhuaiwa.api.SSSessionDomain.GetSystemInfoRequest, com.google.protobuf.RpcCallback)
	 */
	@Override
	public void getSystemInfo(RpcController controller,
			GetSystemInfoRequest request,
			RpcCallback<GetSystemInfoResponse> done)
	{
		done.run(baikuTelService.getSystemInfo((NettyRpcController) controller,
				request));
	}

	@Override
	public void getTopContent(RpcController controller,
			GetTopContentRequest request,
			RpcCallback<GetTopContentResponse> done)
	{
		done.run(messageService.getTopContent((NettyRpcController) controller,
				request));
	}

	@Override
	public void getTopTag(RpcController controller, GetTopTagRequest request,
			RpcCallback<GetTopTagResponse> done)
	{
		done.run(messageService.getTopTag((NettyRpcController) controller,
				request));
	}

	@Override
	public void getTopUser(RpcController controller, GetTopUserRequest request,
			RpcCallback<GetTopUserResponse> done)
	{
		done.run(userService.getTopUser((NettyRpcController) controller,
				request));
	}

	@Override
	public void getTorrent(RpcController controller, GetTorrentRequest request,
			RpcCallback<GetTorrentResponse> done)
	{
		done.run(messageService.getTorrent((NettyRpcController) controller,
				request));
	}

	@Override
	public void getUserId(
			com.google.protobuf.RpcController controller,
			com.zhuaiwa.api.SSSessionDomain.GetUserIdRequest request,
			com.google.protobuf.RpcCallback<com.zhuaiwa.api.SSSessionDomain.GetUserIdResponse> done)
	{

		GetUserIdResponse.Builder response = GetUserIdResponse.newBuilder();

		try
		{

			userService.getUserId((NettyRpcController) controller, request,
					response);
			done.run(response.build());

		}
		catch (Exception e)
		{
			logger.error("", e);

			((NettyRpcController) controller)
					.setCode(SSResultCode.RC_SERVER_INTERNAL_ERROR.getNumber());
			((NettyRpcController) controller).setFailed(e.getMessage());
			done.run(response.build());
		}
	}

	public void Initialize()
	{
		// clientManager = new ClientManager();
	}

	@Override
	public void invite(
			com.google.protobuf.RpcController controller,
			com.zhuaiwa.api.Rpc.InviteRequest request,
			com.google.protobuf.RpcCallback<com.zhuaiwa.api.Rpc.InviteResponse> done)
	{
		done.run(userService.invite((NettyRpcController) controller, request));
	}

	@Override
	public void isFollower(
			com.google.protobuf.RpcController controller,
			com.zhuaiwa.api.Rpc.IsFollowerRequest request,
			com.google.protobuf.RpcCallback<com.zhuaiwa.api.Rpc.IsFollowerResponse> done)
	{
		done.run(userService.isFollower((NettyRpcController) controller,
				request));
	}

	@Override
	public void login(
			com.google.protobuf.RpcController controller,
			com.zhuaiwa.api.SSSessionDomain.LoginRequest request,
			com.google.protobuf.RpcCallback<com.zhuaiwa.api.SSSessionDomain.LoginResponse> done)
	{

		LoginResponse.Builder response = LoginResponse.newBuilder();

		try
		{

			userService.login((NettyRpcController) controller, request,
					response);
			done.run(response.build());

		}
		catch (Exception e)
		{
			logger.error("", e);

			((NettyRpcController) controller)
					.setCode(SSResultCode.RC_SERVER_INTERNAL_ERROR.getNumber());
			((NettyRpcController) controller).setFailed(e.getMessage());
			done.run(response.build());
		}
	}

	@Override
	public void logout(
			com.google.protobuf.RpcController controller,
			com.zhuaiwa.api.SSSessionDomain.LogoutRequest request,
			com.google.protobuf.RpcCallback<com.zhuaiwa.api.SSSessionDomain.LogoutResponse> done)
	{

		LogoutResponse.Builder response = LogoutResponse.newBuilder();

		try
		{

			userService.logout((NettyRpcController) controller, request,
					response);
			done.run(response.build());

		}
		catch (Exception e)
		{
			logger.error("", e);

			((NettyRpcController) controller)
					.setCode(SSResultCode.RC_SERVER_INTERNAL_ERROR.getNumber());
			((NettyRpcController) controller).setFailed(e.getMessage());
			done.run(response.build());
		}
	}

	@Override
	public void registerAccount(
			com.google.protobuf.RpcController controller,
			com.zhuaiwa.api.SSSessionDomain.RegisterAccountRequest request,
			com.google.protobuf.RpcCallback<com.zhuaiwa.api.SSSessionDomain.RegisterAccountResponse> done)
	{

		RegisterAccountResponse.Builder response = RegisterAccountResponse
				.newBuilder();

		try
		{

			userService.register((NettyRpcController) controller, request,
					response);
			done.run(response.build());

		}
		catch (Exception e)
		{
			logger.error("", e);

			((NettyRpcController) controller)
					.setCode(SSResultCode.RC_SERVER_INTERNAL_ERROR.getNumber());
			((NettyRpcController) controller).setFailed(e.getMessage());
			done.run(response.build());
		}
	}

	@Override
	public void removeContact(
			com.google.protobuf.RpcController controller,
			com.zhuaiwa.api.Rpc.RemoveContactRequest request,
			com.google.protobuf.RpcCallback<com.zhuaiwa.api.Rpc.RemoveContactResponse> done)
	{

		RemoveContactResponse.Builder response = RemoveContactResponse
				.newBuilder();

		try
		{

			done.run(contactService.removeContact(
					(NettyRpcController) controller, request));

		}
		catch (Exception e)
		{
			logger.error("", e);

			((NettyRpcController) controller)
					.setCode(SSResultCode.RC_SERVER_INTERNAL_ERROR.getNumber());
			((NettyRpcController) controller).setFailed(e.getMessage());
			done.run(response.build());
		}
	}

	@Override
	public void removeGroup(
			com.google.protobuf.RpcController controller,
			com.zhuaiwa.api.Rpc.RemoveGroupRequest request,
			com.google.protobuf.RpcCallback<com.zhuaiwa.api.Rpc.RemoveGroupResponse> done)
	{

		RemoveGroupResponse.Builder response = RemoveGroupResponse.newBuilder();

		try
		{

			done.run(contactService.removeGroup(
					(NettyRpcController) controller, request));

		}
		catch (Exception e)
		{
			logger.error("", e);

			((NettyRpcController) controller)
					.setCode(SSResultCode.RC_SERVER_INTERNAL_ERROR.getNumber());
			((NettyRpcController) controller).setFailed(e.getMessage());
			done.run(response.build());
		}
	}

	@Override
	public void removeMember(
			com.google.protobuf.RpcController controller,
			com.zhuaiwa.api.Rpc.RemoveMemberRequest request,
			com.google.protobuf.RpcCallback<com.zhuaiwa.api.Rpc.RemoveMemberResponse> done)
	{

		RemoveMemberResponse.Builder response = RemoveMemberResponse
				.newBuilder();

		try
		{

			done.run(contactService.removeMember(
					(NettyRpcController) controller, request));

		}
		catch (Exception e)
		{
			logger.error("", e);

			((NettyRpcController) controller)
					.setCode(SSResultCode.RC_SERVER_INTERNAL_ERROR.getNumber());
			((NettyRpcController) controller).setFailed(e.getMessage());
			done.run(response.build());
		}
	}

	@Override
	public void removeMessage(
			com.google.protobuf.RpcController controller,
			com.zhuaiwa.api.Rpc.RemoveMessageRequest request,
			com.google.protobuf.RpcCallback<com.zhuaiwa.api.Rpc.RemoveMessageResponse> done)
	{

		RemoveMessageResponse.Builder response = RemoveMessageResponse
				.newBuilder();

		try
		{

			messageService.removeMessage((NettyRpcController) controller,
					request, response);
			done.run(response.build());

		}
		catch (Exception e)
		{
			logger.error("", e);

			((NettyRpcController) controller)
					.setCode(SSResultCode.RC_SERVER_INTERNAL_ERROR.getNumber());
			((NettyRpcController) controller).setFailed(e.getMessage());
			done.run(response.build());
		}
	}

	@Override
	public void resetPassword(
			com.google.protobuf.RpcController controller,
			com.zhuaiwa.api.SSSessionDomain.ResetPasswordRequest request,
			com.google.protobuf.RpcCallback<com.zhuaiwa.api.SSSessionDomain.ResetPasswordResponse> done)
	{

		ResetPasswordResponse.Builder response = ResetPasswordResponse
				.newBuilder();

		try
		{

			userService.resetPassword((NettyRpcController) controller, request,
					response);
			done.run(response.build());

		}
		catch (Exception e)
		{
			logger.error("", e);

			((NettyRpcController) controller)
					.setCode(SSResultCode.RC_SERVER_INTERNAL_ERROR.getNumber());
			((NettyRpcController) controller).setFailed(e.getMessage());
			done.run(response.build());
		}
	}

	@Override
	public void searchMessage(RpcController controller,
			SearchMessageRequest request,
			RpcCallback<SearchMessageResponse> done)
	{
		done.run(messageService.searchMessage((NettyRpcController) controller,
				request));
	}

	@Override
	public void searchUser(RpcController controller, SearchUserRequest request,
			RpcCallback<SearchUserResponse> done)
	{
		done.run(userService.searchUser((NettyRpcController) controller,
				request));
	}

	@Override
	public void sendActivateCode(RpcController controller,
			SendActivateCodeRequest request,
			RpcCallback<SendActivateCodeResponse> done)
	{
		done.run(userService.sendActivateCode((NettyRpcController) controller,
				request));
	}

	@Override
	public void sendMessage(
			com.google.protobuf.RpcController controller,
			com.zhuaiwa.api.Rpc.SendMessageRequest request,
			com.google.protobuf.RpcCallback<com.zhuaiwa.api.Rpc.SendMessageResponse> done)
	{

		SendMessageResponse.Builder response = SendMessageResponse.newBuilder();

		try
		{

			messageService.sendMessage((NettyRpcController) controller,
					request, response);
			done.run(response.build());

		}
		catch (Exception e)
		{
			logger.error("", e);

			((NettyRpcController) controller)
					.setCode(SSResultCode.RC_SERVER_INTERNAL_ERROR.getNumber());
			((NettyRpcController) controller).setFailed(e.getMessage());
			done.run(response.build());
		}
	}

	/**
	 * @param baikuTelService
	 *            the baikuTelService to set
	 */
	public void setBaikuTelService(BaikuTelServiceImpl baikuTelService)
	{
		this.baikuTelService = baikuTelService;
	}

	public void setContactService(ContactService_v_1_0 contactService)
	{
		this.contactService = contactService;
	}

	public void setMessageService(MessageService_v_1_0 messageService)
	{
		this.messageService = messageService;
	}

	@Override
	public void setProfile(
			com.google.protobuf.RpcController controller,
			com.zhuaiwa.api.Rpc.SetProfileRequest request,
			com.google.protobuf.RpcCallback<com.zhuaiwa.api.Rpc.SetProfileResponse> done)
	{

		SetProfileResponse.Builder response = SetProfileResponse.newBuilder();

		try
		{

			userService.setProfile((NettyRpcController) controller, request,
					response);
			done.run(response.build());

		}
		catch (Exception e)
		{
			logger.error("", e);

			((NettyRpcController) controller)
					.setCode(SSResultCode.RC_SERVER_INTERNAL_ERROR.getNumber());
			((NettyRpcController) controller).setFailed(e.getMessage());
			done.run(response.build());
		}
	}

	@Override
	public void setSettings(RpcController controller,
			SetSettingsRequest request, RpcCallback<SetSettingsResponse> done)
	{
		done.run(userService.setSettings((NettyRpcController) controller,
				request));
	}

	@Override
	public void setTorrent(RpcController controller, SetTorrentRequest request,
			RpcCallback<SetTorrentResponse> done)
	{
		done.run(messageService.setTorrent((NettyRpcController) controller,
				request));
	}

	public void setUserService(UserService_v_1_0 userService)
	{
		this.userService = userService;
	}

	@Override
	public void suggest(RpcController controller, SuggestRequest request,
			RpcCallback<SuggestResponse> done)
	{
		StringBuffer sb = new StringBuffer();
		if (request.hasEmail())
		{
			sb.append(request.getEmail() + "\n");
		}
		if (request.hasPhone())
		{
			sb.append(request.getPhone() + "\n");
		}
		if (request.hasSuggest())
		{
			sb.append(request.getSuggest());
		}
		MailUtil.sendMessage("tangjun@channelsoft.com", "用户反馈", sb.toString());
		MailUtil.sendMessage("liangzheng@channelsoft.com", "用户反馈",
				sb.toString());
		MailUtil.sendMessage("wangxz@channelsoft.com", "用户反馈", sb.toString());
		done.run(SuggestResponse.newBuilder().build());
	}

	@Override
	public void unbindAccount(
			com.google.protobuf.RpcController controller,
			com.zhuaiwa.api.Rpc.UnbindAccountRequest request,
			com.google.protobuf.RpcCallback<com.zhuaiwa.api.Rpc.UnbindAccountResponse> done)
	{

		UnbindAccountResponse.Builder response = UnbindAccountResponse
				.newBuilder();

		try
		{

			userService.unbindAccount((NettyRpcController) controller, request,
					response);
			done.run(response.build());

		}
		catch (Exception e)
		{
			logger.error("", e);

			((NettyRpcController) controller)
					.setCode(SSResultCode.RC_SERVER_INTERNAL_ERROR.getNumber());
			((NettyRpcController) controller).setFailed(e.getMessage());
			done.run(response.build());
		}
	}

	@Override
	public void unfollow(
			com.google.protobuf.RpcController controller,
			com.zhuaiwa.api.Rpc.UnfollowRequest request,
			com.google.protobuf.RpcCallback<com.zhuaiwa.api.Rpc.UnfollowResponse> done)
	{

		UnfollowResponse.Builder response = UnfollowResponse.newBuilder();

		try
		{

			userService.unfollow((NettyRpcController) controller, request,
					response);
			done.run(response.build());

		}
		catch (Exception e)
		{
			logger.error("", e);

			((NettyRpcController) controller)
					.setCode(SSResultCode.RC_SERVER_INTERNAL_ERROR.getNumber());
			((NettyRpcController) controller).setFailed(e.getMessage());
			done.run(response.build());
		}
	}

	@Override
	public void unfollowTag(
			com.google.protobuf.RpcController controller,
			com.zhuaiwa.api.SSSessionDomain.UnfollowTagRequest request,
			com.google.protobuf.RpcCallback<com.zhuaiwa.api.SSSessionDomain.UnfollowTagResponse> done)
	{

		UnfollowTagResponse.Builder response = UnfollowTagResponse.newBuilder();

		try
		{

			messageService.unfollowTag((NettyRpcController) controller,
					request, response);
			done.run(response.build());

		}
		catch (Exception e)
		{
			logger.error("", e);

			((NettyRpcController) controller)
					.setCode(SSResultCode.RC_SERVER_INTERNAL_ERROR.getNumber());
			((NettyRpcController) controller).setFailed(e.getMessage());
			done.run(response.build());
		}
	}

}
