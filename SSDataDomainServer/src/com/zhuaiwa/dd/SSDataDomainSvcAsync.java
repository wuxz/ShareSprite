package com.zhuaiwa.dd;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.google.protobuf.RpcCallback;
import com.google.protobuf.RpcController;
import com.zhuaiwa.api.Common.SSResultCode;
import com.zhuaiwa.api.Rpc.AddContactRequest;
import com.zhuaiwa.api.Rpc.AddContactResponse;
import com.zhuaiwa.api.Rpc.AddGroupRequest;
import com.zhuaiwa.api.Rpc.AddGroupResponse;
import com.zhuaiwa.api.Rpc.AddMemberRequest;
import com.zhuaiwa.api.Rpc.AddMemberResponse;
import com.zhuaiwa.api.Rpc.BindAccountRequest;
import com.zhuaiwa.api.Rpc.BindAccountResponse;
import com.zhuaiwa.api.Rpc.FollowRequest;
import com.zhuaiwa.api.Rpc.FollowResponse;
import com.zhuaiwa.api.Rpc.GetCommentRequest;
import com.zhuaiwa.api.Rpc.GetCommentResponse;
import com.zhuaiwa.api.Rpc.GetContactRequest;
import com.zhuaiwa.api.Rpc.GetContactResponse;
import com.zhuaiwa.api.Rpc.GetFollowerRequest;
import com.zhuaiwa.api.Rpc.GetFollowerResponse;
import com.zhuaiwa.api.Rpc.GetFollowingRequest;
import com.zhuaiwa.api.Rpc.GetFollowingResponse;
import com.zhuaiwa.api.Rpc.GetGroupRequest;
import com.zhuaiwa.api.Rpc.GetGroupResponse;
import com.zhuaiwa.api.Rpc.GetInviterRequest;
import com.zhuaiwa.api.Rpc.GetInviterResponse;
import com.zhuaiwa.api.Rpc.GetInvitingRequest;
import com.zhuaiwa.api.Rpc.GetInvitingResponse;
import com.zhuaiwa.api.Rpc.GetMemberRequest;
import com.zhuaiwa.api.Rpc.GetMemberResponse;
import com.zhuaiwa.api.Rpc.GetMessageByIdRequest;
import com.zhuaiwa.api.Rpc.GetMessageByIdResponse;
import com.zhuaiwa.api.Rpc.GetMessageByTimestampRequest;
import com.zhuaiwa.api.Rpc.GetMessageByTimestampResponse;
import com.zhuaiwa.api.Rpc.GetMessageRequest;
import com.zhuaiwa.api.Rpc.GetMessageResponse;
import com.zhuaiwa.api.Rpc.GetProfileRequest;
import com.zhuaiwa.api.Rpc.GetProfileResponse;
import com.zhuaiwa.api.Rpc.GetTorrentRequest;
import com.zhuaiwa.api.Rpc.GetTorrentResponse;
import com.zhuaiwa.api.Rpc.InviteRequest;
import com.zhuaiwa.api.Rpc.InviteResponse;
import com.zhuaiwa.api.Rpc.IsFollowerRequest;
import com.zhuaiwa.api.Rpc.IsFollowerResponse;
import com.zhuaiwa.api.Rpc.RemoveContactRequest;
import com.zhuaiwa.api.Rpc.RemoveContactResponse;
import com.zhuaiwa.api.Rpc.RemoveGroupRequest;
import com.zhuaiwa.api.Rpc.RemoveGroupResponse;
import com.zhuaiwa.api.Rpc.RemoveMemberRequest;
import com.zhuaiwa.api.Rpc.RemoveMemberResponse;
import com.zhuaiwa.api.Rpc.RemoveMessageRequest;
import com.zhuaiwa.api.Rpc.RemoveMessageResponse;
import com.zhuaiwa.api.Rpc.SendMessageRequest;
import com.zhuaiwa.api.Rpc.SendMessageResponse;
import com.zhuaiwa.api.Rpc.SetProfileRequest;
import com.zhuaiwa.api.Rpc.SetProfileResponse;
import com.zhuaiwa.api.Rpc.SetTorrentRequest;
import com.zhuaiwa.api.Rpc.SetTorrentResponse;
import com.zhuaiwa.api.Rpc.UnbindAccountRequest;
import com.zhuaiwa.api.Rpc.UnbindAccountResponse;
import com.zhuaiwa.api.Rpc.UnfollowRequest;
import com.zhuaiwa.api.Rpc.UnfollowResponse;
import com.zhuaiwa.api.SSDataDomain.CreateAccountRequest;
import com.zhuaiwa.api.SSDataDomain.CreateAccountResponse;
import com.zhuaiwa.api.SSDataDomain.GetAccountRequest;
import com.zhuaiwa.api.SSDataDomain.GetAccountResponse;
import com.zhuaiwa.api.SSDataDomain.SSDataDomainSvc.Interface;
import com.zhuaiwa.api.SSDataDomain.SetAccountRequest;
import com.zhuaiwa.api.SSDataDomain.SetAccountResponse;
import com.zhuaiwa.api.SSDataDomain.SetMessageRequest;
import com.zhuaiwa.api.SSDataDomain.SetMessageResponse;
import com.zhuaiwa.api.netty.NettyRpcController;
import com.zhuaiwa.dd.api.DataDomainApi;

public class SSDataDomainSvcAsync implements Interface {
    private static Logger logger = LoggerFactory.getLogger(SSDataDomainSvcAsync.class);

    private DataDomainApi impl;
	
	public DataDomainApi getImpl() {
		return impl;
	}
	
	public void setImpl(DataDomainApi impl) {
		this.impl = impl;
	}
	
	@Override
	public void createAccount(RpcController controller,
			CreateAccountRequest request,
			RpcCallback<CreateAccountResponse> done) {
		CreateAccountResponse.Builder response = CreateAccountResponse.newBuilder();
		try {
			impl.createAccount((NettyRpcController)controller, request, response);
		} catch (Exception e) {
			logger.error(e.getMessage(), e);
			((NettyRpcController)controller).setCode(SSResultCode.RC_SERVER_INTERNAL_ERROR.getNumber());
			((NettyRpcController)controller).setFailed(e.getMessage());
		}
		done.run(response.build());
	}

	@Override
	public void getAccount(RpcController controller, GetAccountRequest request,
			RpcCallback<GetAccountResponse> done) {
		GetAccountResponse.Builder response = GetAccountResponse.newBuilder();
		try {
			impl.getAccount((NettyRpcController)controller, request, response);
		} catch (Exception e) {
			logger.error(e.getMessage(), e);
			((NettyRpcController)controller).setCode(SSResultCode.RC_SERVER_INTERNAL_ERROR.getNumber());
			((NettyRpcController)controller).setFailed(e.getMessage());
		}
		done.run(response.build());
	}
	
	@Override
	public void setAccount(RpcController controller, SetAccountRequest request,
			RpcCallback<SetAccountResponse> done) {
		SetAccountResponse.Builder response = SetAccountResponse.newBuilder();
		try {
			impl.setAccount((NettyRpcController)controller, request, response);
		} catch (Exception e) {
			logger.error(e.getMessage(), e);
			((NettyRpcController)controller).setCode(SSResultCode.RC_SERVER_INTERNAL_ERROR.getNumber());
			((NettyRpcController)controller).setFailed(e.getMessage());
		}
		done.run(response.build());
	}
	
	@Override
	public void bindAccount(RpcController controller,
			BindAccountRequest request, RpcCallback<BindAccountResponse> done) {
		BindAccountResponse.Builder response = BindAccountResponse.newBuilder();
		try {
			impl.bindAccount((NettyRpcController)controller, request, response);
		} catch (Exception e) {
			logger.error(e.getMessage(), e);
			((NettyRpcController)controller).setCode(SSResultCode.RC_SERVER_INTERNAL_ERROR.getNumber());
			((NettyRpcController)controller).setFailed(e.getMessage());
		}
		done.run(response.build());
	}
	@Override
	public void unbindAccount(RpcController controller,
			UnbindAccountRequest request,
			RpcCallback<UnbindAccountResponse> done) {
		UnbindAccountResponse.Builder response = UnbindAccountResponse.newBuilder();
		try {
			impl.unbindAccount((NettyRpcController)controller, request, response);
		} catch (Exception e) {
			logger.error(e.getMessage(), e);
			((NettyRpcController)controller).setCode(SSResultCode.RC_SERVER_INTERNAL_ERROR.getNumber());
			((NettyRpcController)controller).setFailed(e.getMessage());
		}
		done.run(response.build());
	}
	

	@Override
	public void unfollow(RpcController controller,
			UnfollowRequest request, RpcCallback<UnfollowResponse> done) {
		UnfollowResponse.Builder response = UnfollowResponse.newBuilder();
		try {
			impl.unfollow((NettyRpcController)controller, request, response);
		} catch (Exception e) {
			logger.error(e.getMessage(), e);
			((NettyRpcController)controller).setCode(SSResultCode.RC_SERVER_INTERNAL_ERROR.getNumber());
			((NettyRpcController)controller).setFailed(e.getMessage());
		}
		done.run(response.build());
	}
	
	@Override
	public void follow(RpcController controller, FollowRequest request,
			RpcCallback<FollowResponse> done) {
		FollowResponse.Builder response = FollowResponse.newBuilder();
		try {
			impl.follow((NettyRpcController)controller, request, response);
		} catch (Exception e) {
			logger.error(e.getMessage(), e);
			((NettyRpcController)controller).setCode(SSResultCode.RC_SERVER_INTERNAL_ERROR.getNumber());
			((NettyRpcController)controller).setFailed(e.getMessage());
		}
		done.run(response.build());
	}
	
	@Override
	public void getFollower(RpcController controller,
			GetFollowerRequest request, RpcCallback<GetFollowerResponse> done) {
		GetFollowerResponse.Builder response = GetFollowerResponse.newBuilder();
		try {
			impl.getFollower((NettyRpcController)controller, request, response);
		} catch (Exception e) {
			logger.error(e.getMessage(), e);
			((NettyRpcController)controller).setCode(SSResultCode.RC_SERVER_INTERNAL_ERROR.getNumber());
			((NettyRpcController)controller).setFailed(e.getMessage());
		}
		done.run(response.build());
	}
	

	@Override
	public void getFollowing(RpcController controller,
			GetFollowingRequest request, RpcCallback<GetFollowingResponse> done) {
		GetFollowingResponse.Builder response = GetFollowingResponse.newBuilder();
		try {
			impl.getFollowing((NettyRpcController)controller, request, response);
		} catch (Exception e) {
			logger.error(e.getMessage(), e);
			((NettyRpcController)controller).setCode(SSResultCode.RC_SERVER_INTERNAL_ERROR.getNumber());
			((NettyRpcController)controller).setFailed(e.getMessage());
		}
		done.run(response.build());
	}
	
	@Override
	public void getInviter(RpcController controller, GetInviterRequest request,
			RpcCallback<GetInviterResponse> done) {
		GetInviterResponse.Builder response = GetInviterResponse.newBuilder();
		try {
			impl.getInviter((NettyRpcController)controller, request, response);
		} catch (Exception e) {
			logger.error(e.getMessage(), e);
			((NettyRpcController)controller).setCode(SSResultCode.RC_SERVER_INTERNAL_ERROR.getNumber());
			((NettyRpcController)controller).setFailed(e.getMessage());
		}
		done.run(response.build());
	}
	
	@Override
	public void getInviting(RpcController controller,
			GetInvitingRequest request, RpcCallback<GetInvitingResponse> done) {
		GetInvitingResponse.Builder response = GetInvitingResponse.newBuilder();
		try {
			impl.getInviting((NettyRpcController)controller, request, response);
		} catch (Exception e) {
			logger.error(e.getMessage(), e);
			((NettyRpcController)controller).setCode(SSResultCode.RC_SERVER_INTERNAL_ERROR.getNumber());
			((NettyRpcController)controller).setFailed(e.getMessage());
		}
		done.run(response.build());
	}
	
	@Override
	public void getProfile(RpcController controller, GetProfileRequest request,
			RpcCallback<GetProfileResponse> done) {
		GetProfileResponse.Builder response = GetProfileResponse.newBuilder();
		try {
			impl.getProfile((NettyRpcController)controller, request, response);
		} catch (Exception e) {
			logger.error(e.getMessage(), e);
			((NettyRpcController)controller).setCode(SSResultCode.RC_SERVER_INTERNAL_ERROR.getNumber());
			((NettyRpcController)controller).setFailed(e.getMessage());
		}
		done.run(response.build());
	}
	
	@Override
	public void setProfile(RpcController controller, SetProfileRequest request,
			RpcCallback<SetProfileResponse> done) {
		SetProfileResponse.Builder response = SetProfileResponse.newBuilder();
		try {
			impl.setProfile((NettyRpcController)controller, request, response);
		} catch (Exception e) {
			logger.error(e.getMessage(), e);
			((NettyRpcController)controller).setCode(SSResultCode.RC_SERVER_INTERNAL_ERROR.getNumber());
			((NettyRpcController)controller).setFailed(e.getMessage());
		}
		done.run(response.build());
	}
	@Override
	public void isFollower(RpcController controller, IsFollowerRequest request,
			RpcCallback<IsFollowerResponse> done) {
		IsFollowerResponse.Builder response = IsFollowerResponse.newBuilder();
		try {
			impl.isFollower((NettyRpcController)controller, request, response);
		} catch (Exception e) {
			logger.error(e.getMessage(), e);
			((NettyRpcController)controller).setCode(SSResultCode.RC_SERVER_INTERNAL_ERROR.getNumber());
			((NettyRpcController)controller).setFailed(e.getMessage());
		}
		done.run(response.build());
	}
	@Override
	public void invite(RpcController controller, InviteRequest request,
			RpcCallback<InviteResponse> done) {
		InviteResponse.Builder response = InviteResponse.newBuilder();
		try {
			impl.invite((NettyRpcController)controller, request, response);
		} catch (Exception e) {
			logger.error(e.getMessage(), e);
			((NettyRpcController)controller).setCode(SSResultCode.RC_SERVER_INTERNAL_ERROR.getNumber());
			((NettyRpcController)controller).setFailed(e.getMessage());
		}
		done.run(response.build());
	}
	@Override
	public void sendMessage(RpcController controller,
			SendMessageRequest request, RpcCallback<SendMessageResponse> done) {
		SendMessageResponse.Builder response = SendMessageResponse.newBuilder();
		try {
			impl.sendMessage((NettyRpcController)controller, request, response);
		} catch (Exception e) {
			logger.error(e.getMessage(), e);
			((NettyRpcController)controller).setCode(SSResultCode.RC_SERVER_INTERNAL_ERROR.getNumber());
			((NettyRpcController)controller).setFailed(e.getMessage());
		}
		done.run(response.build());
	}
	@Override
	public void getMessage(RpcController controller, GetMessageRequest request,
			RpcCallback<GetMessageResponse> done) {
		GetMessageResponse.Builder response = GetMessageResponse.newBuilder();
		try {
			impl.getMessage((NettyRpcController)controller, request, response);
		} catch (Exception e) {
			logger.error(e.getMessage(), e);
			((NettyRpcController)controller).setCode(SSResultCode.RC_SERVER_INTERNAL_ERROR.getNumber());
			((NettyRpcController)controller).setFailed(e.getMessage());
		}
		done.run(response.build());
	}
	
//	@Override
//	public void sendPublicMessage(RpcController controller,
//			SendPublicMessageRequest request,
//			RpcCallback<SendPublicMessageResponse> done) {
//		SendPublicMessageResponse.Builder response = SendPublicMessageResponse.newBuilder();
//		try {
//			impl.sendPublicMessage((NettyRpcController)controller, request, response);
//		} catch (Exception e) {
//			logger.error(e.getMessage(), e);
//			((NettyRpcController)controller).setCode(SSResultCode.RC_SERVER_INTERNAL_ERROR.getNumber());
//			((NettyRpcController)controller).setFailed(e.getMessage());
//		}
//		done.run(response.build());
//	}
//	@Override
//	public void addFavoriteMessage(RpcController controller,
//			AddFavoriteMessageRequest request,
//			RpcCallback<AddFavoriteMessageResponse> done) {
//		AddFavoriteMessageResponse.Builder response = AddFavoriteMessageResponse.newBuilder();
//		try {
//			impl.addFavoriteMessage((NettyRpcController)controller, request, response);
//		} catch (Exception e) {
//			logger.error(e.getMessage(), e);
//			((NettyRpcController)controller).setCode(SSResultCode.RC_SERVER_INTERNAL_ERROR.getNumber());
//			((NettyRpcController)controller).setFailed(e.getMessage());
//		}
//		done.run(response.build());
//	}
	
	@Override
	public void getMessageByTimestamp(RpcController controller,
			GetMessageByTimestampRequest request,
			RpcCallback<GetMessageByTimestampResponse> done) {
		GetMessageByTimestampResponse.Builder response = GetMessageByTimestampResponse.newBuilder();
		try {
			impl.getMessageByTimestamp((NettyRpcController)controller, request, response);
		} catch (Exception e) {
			logger.error(e.getMessage(), e);
			((NettyRpcController)controller).setCode(SSResultCode.RC_SERVER_INTERNAL_ERROR.getNumber());
			((NettyRpcController)controller).setFailed(e.getMessage());
		}
		done.run(response.build());
	}
	@Override
	public void removeMessage(RpcController controller,
			RemoveMessageRequest request,
			RpcCallback<RemoveMessageResponse> done) {
		RemoveMessageResponse.Builder response = RemoveMessageResponse.newBuilder();
		try {
			impl.removeMessage((NettyRpcController)controller, request, response);
		} catch (Exception e) {
			logger.error(e.getMessage(), e);
			((NettyRpcController)controller).setCode(SSResultCode.RC_SERVER_INTERNAL_ERROR.getNumber());
			((NettyRpcController)controller).setFailed(e.getMessage());
		}
		done.run(response.build());
	}
	@Override
	public void getMessageById(RpcController controller,
			GetMessageByIdRequest request,
			RpcCallback<GetMessageByIdResponse> done) {
		GetMessageByIdResponse.Builder response = GetMessageByIdResponse.newBuilder();
		try {
			impl.getMessageById((NettyRpcController)controller, request, response);
		} catch (Exception e) {
			logger.error(e.getMessage(), e);
			((NettyRpcController)controller).setCode(SSResultCode.RC_SERVER_INTERNAL_ERROR.getNumber());
			((NettyRpcController)controller).setFailed(e.getMessage());
		}
		done.run(response.build());
	}

	@Override
	public void addContact(RpcController controller, AddContactRequest request,
			RpcCallback<AddContactResponse> done) {
		AddContactResponse.Builder response = AddContactResponse.newBuilder();
		try {
			impl.addContact((NettyRpcController)controller, request, response);
		} catch (Exception e) {
			logger.error(e.getMessage(), e);
			((NettyRpcController)controller).setCode(SSResultCode.RC_SERVER_INTERNAL_ERROR.getNumber());
			((NettyRpcController)controller).setFailed(e.getMessage());
		}
		done.run(response.build());
	}
	@Override
	public void removeContact(RpcController controller,
			RemoveContactRequest request,
			RpcCallback<RemoveContactResponse> done) {
		RemoveContactResponse.Builder response = RemoveContactResponse.newBuilder();
		try {
			impl.removeContact((NettyRpcController)controller, request, response);
		} catch (Exception e) {
			logger.error(e.getMessage(), e);
			((NettyRpcController)controller).setCode(SSResultCode.RC_SERVER_INTERNAL_ERROR.getNumber());
			((NettyRpcController)controller).setFailed(e.getMessage());
		}
		done.run(response.build());
	}
	@Override
	public void getContact(RpcController controller, GetContactRequest request,
			RpcCallback<GetContactResponse> done) {
		GetContactResponse.Builder response = GetContactResponse.newBuilder();
		try {
			impl.getContact((NettyRpcController)controller, request, response);
		} catch (Exception e) {
			logger.error(e.getMessage(), e);
			((NettyRpcController)controller).setCode(SSResultCode.RC_SERVER_INTERNAL_ERROR.getNumber());
			((NettyRpcController)controller).setFailed(e.getMessage());
		}
		done.run(response.build());
	}
	@Override
	public void addGroup(RpcController controller, AddGroupRequest request,
			RpcCallback<AddGroupResponse> done) {
		AddGroupResponse.Builder response = AddGroupResponse.newBuilder();
		try {
			impl.addGroup((NettyRpcController)controller, request, response);
		} catch (Exception e) {
			logger.error(e.getMessage(), e);
			((NettyRpcController)controller).setCode(SSResultCode.RC_SERVER_INTERNAL_ERROR.getNumber());
			((NettyRpcController)controller).setFailed(e.getMessage());
		}
		done.run(response.build());
	}
	@Override
	public void removeGroup(RpcController controller,
			RemoveGroupRequest request, RpcCallback<RemoveGroupResponse> done) {
		RemoveGroupResponse.Builder response = RemoveGroupResponse.newBuilder();
		try {
			impl.removeGroup((NettyRpcController)controller, request, response);
		} catch (Exception e) {
			logger.error(e.getMessage(), e);
			((NettyRpcController)controller).setCode(SSResultCode.RC_SERVER_INTERNAL_ERROR.getNumber());
			((NettyRpcController)controller).setFailed(e.getMessage());
		}
		done.run(response.build());
	}
	
	@Override
	public void getGroup(RpcController controller, GetGroupRequest request,
			RpcCallback<GetGroupResponse> done) {
		GetGroupResponse.Builder response = GetGroupResponse.newBuilder();
		try {
			impl.getGroup((NettyRpcController)controller, request, response);
		} catch (Exception e) {
			logger.error(e.getMessage(), e);
			((NettyRpcController)controller).setCode(SSResultCode.RC_SERVER_INTERNAL_ERROR.getNumber());
			((NettyRpcController)controller).setFailed(e.getMessage());
		}
		done.run(response.build());
	}
	@Override
	public void addMember(RpcController controller, AddMemberRequest request,
			RpcCallback<AddMemberResponse> done) {
		AddMemberResponse.Builder response = AddMemberResponse.newBuilder();
		try {
			impl.addMember((NettyRpcController)controller, request, response);
		} catch (Exception e) {
			logger.error(e.getMessage(), e);
			((NettyRpcController)controller).setCode(SSResultCode.RC_SERVER_INTERNAL_ERROR.getNumber());
			((NettyRpcController)controller).setFailed(e.getMessage());
		}
		done.run(response.build());
	}
	
	@Override
	public void removeMember(RpcController controller,
			RemoveMemberRequest request, RpcCallback<RemoveMemberResponse> done) {
		RemoveMemberResponse.Builder response = RemoveMemberResponse.newBuilder();
		try {
			impl.removeMember((NettyRpcController)controller, request, response);
		} catch (Exception e) {
			logger.error(e.getMessage(), e);
			((NettyRpcController)controller).setCode(SSResultCode.RC_SERVER_INTERNAL_ERROR.getNumber());
			((NettyRpcController)controller).setFailed(e.getMessage());
		}
		done.run(response.build());
	}
	
	@Override
	public void getMember(RpcController controller, GetMemberRequest request,
			RpcCallback<GetMemberResponse> done) {
		GetMemberResponse.Builder response = GetMemberResponse.newBuilder();
		try {
			impl.getMember((NettyRpcController)controller, request, response);
		} catch (Exception e) {
			logger.error(e.getMessage(), e);
			((NettyRpcController)controller).setCode(SSResultCode.RC_SERVER_INTERNAL_ERROR.getNumber());
			((NettyRpcController)controller).setFailed(e.getMessage());
		}
		done.run(response.build());
	}
		@Override
	public void getTorrent(RpcController controller, GetTorrentRequest request,
			RpcCallback<GetTorrentResponse> done) {
			GetTorrentResponse.Builder response = GetTorrentResponse.newBuilder();
			try {
				impl.getTorrent((NettyRpcController)controller, request, response);
			} catch (Exception e) {
				logger.error(e.getMessage(), e);
				((NettyRpcController)controller).setCode(SSResultCode.RC_SERVER_INTERNAL_ERROR.getNumber());
				((NettyRpcController)controller).setFailed(e.getMessage());
			}
			done.run(response.build());
		}

		@Override
		public void setTorrent(RpcController controller, SetTorrentRequest request,
				RpcCallback<SetTorrentResponse> done) {
			SetTorrentResponse.Builder response = SetTorrentResponse.newBuilder();
			try {
				impl.setTorrent((NettyRpcController)controller, request, response);
			} catch (Exception e) {
				logger.error(e.getMessage(), e);
				((NettyRpcController)controller).setCode(SSResultCode.RC_SERVER_INTERNAL_ERROR.getNumber());
				((NettyRpcController)controller).setFailed(e.getMessage());
			}
			done.run(response.build());
		}

        @Override
        public void setMessage(RpcController controller, SetMessageRequest request, RpcCallback<SetMessageResponse> done) {
            SetMessageResponse.Builder response = SetMessageResponse.newBuilder();
            try {
                impl.setMessage((NettyRpcController)controller, request, response);
            } catch (Exception e) {
                logger.error(e.getMessage(), e);
                ((NettyRpcController)controller).setCode(SSResultCode.RC_SERVER_INTERNAL_ERROR.getNumber());
                ((NettyRpcController)controller).setFailed(e.getMessage());
            }
            done.run(response.build());
        }

        @Override
        public void getComment(RpcController controller, GetCommentRequest request, RpcCallback<GetCommentResponse> done) {
            GetCommentResponse.Builder response = GetCommentResponse.newBuilder();
            try {
                impl.getComment((NettyRpcController)controller, request, response);
            } catch (Exception e) {
                logger.error(e.getMessage(), e);
                ((NettyRpcController)controller).setCode(SSResultCode.RC_SERVER_INTERNAL_ERROR.getNumber());
                ((NettyRpcController)controller).setFailed(e.getMessage());
            }
            done.run(response.build());
        }
}
