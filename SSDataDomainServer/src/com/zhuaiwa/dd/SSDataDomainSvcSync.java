package com.zhuaiwa.dd;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

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
import com.zhuaiwa.api.SSDataDomain.SSDataDomainSvc.BlockingInterface;
import com.zhuaiwa.api.SSDataDomain.SetAccountRequest;
import com.zhuaiwa.api.SSDataDomain.SetAccountResponse;
import com.zhuaiwa.api.SSDataDomain.SetMessageRequest;
import com.zhuaiwa.api.SSDataDomain.SetMessageResponse;
import com.zhuaiwa.api.netty.NettyRpcController;
import com.zhuaiwa.dd.api.DataDomainApi;

public class SSDataDomainSvcSync implements BlockingInterface {
    private static Logger logger = LoggerFactory.getLogger(SSDataDomainSvcSync.class);

    private DataDomainApi impl;
	
	public DataDomainApi getImpl() {
		return impl;
	}
	
	public void setImpl(DataDomainApi impl) {
		this.impl = impl;
	}
	
	@Override
	public CreateAccountResponse createAccount(RpcController controller, CreateAccountRequest request) {
		CreateAccountResponse.Builder response = CreateAccountResponse.newBuilder();
		try {
			impl.createAccount((NettyRpcController)controller, request, response);
		} catch (Exception e) {
			logger.error(e.getMessage(), e);
			((NettyRpcController)controller).setCode(SSResultCode.RC_SERVER_INTERNAL_ERROR.getNumber());
			((NettyRpcController)controller).setFailed(e.getMessage());
		}
		return response.build();
	}

	@Override
	public GetAccountResponse getAccount(RpcController controller, GetAccountRequest request) {
		GetAccountResponse.Builder response = GetAccountResponse.newBuilder();
		try {
			impl.getAccount((NettyRpcController)controller, request, response);
		} catch (Exception e) {
			logger.error(e.getMessage(), e);
			((NettyRpcController)controller).setCode(SSResultCode.RC_SERVER_INTERNAL_ERROR.getNumber());
			((NettyRpcController)controller).setFailed(e.getMessage());
		}
		return response.build();
	}
	
	@Override
	public SetAccountResponse setAccount(RpcController controller, SetAccountRequest request) {
		SetAccountResponse.Builder response = SetAccountResponse.newBuilder();
		try {
			impl.setAccount((NettyRpcController)controller, request, response);
		} catch (Exception e) {
			logger.error(e.getMessage(), e);
			((NettyRpcController)controller).setCode(SSResultCode.RC_SERVER_INTERNAL_ERROR.getNumber());
			((NettyRpcController)controller).setFailed(e.getMessage());
		}
		return response.build();
	}
	
	@Override
	public BindAccountResponse bindAccount(RpcController controller,
			BindAccountRequest request) {
		BindAccountResponse.Builder response = BindAccountResponse.newBuilder();
		try {
			impl.bindAccount((NettyRpcController)controller, request, response);
		} catch (Exception e) {
			logger.error(e.getMessage(), e);
			((NettyRpcController)controller).setCode(SSResultCode.RC_SERVER_INTERNAL_ERROR.getNumber());
			((NettyRpcController)controller).setFailed(e.getMessage());
		}
		return response.build();
	}
	@Override
	public UnbindAccountResponse unbindAccount(RpcController controller, UnbindAccountRequest request) {
		UnbindAccountResponse.Builder response = UnbindAccountResponse.newBuilder();
		try {
			impl.unbindAccount((NettyRpcController)controller, request, response);
		} catch (Exception e) {
			logger.error(e.getMessage(), e);
			((NettyRpcController)controller).setCode(SSResultCode.RC_SERVER_INTERNAL_ERROR.getNumber());
			((NettyRpcController)controller).setFailed(e.getMessage());
		}
		return response.build();
	}
	

	@Override
	public UnfollowResponse unfollow(RpcController controller, UnfollowRequest request) {
		UnfollowResponse.Builder response = UnfollowResponse.newBuilder();
		try {
			impl.unfollow((NettyRpcController)controller, request, response);
		} catch (Exception e) {
			logger.error(e.getMessage(), e);
			((NettyRpcController)controller).setCode(SSResultCode.RC_SERVER_INTERNAL_ERROR.getNumber());
			((NettyRpcController)controller).setFailed(e.getMessage());
		}
		return response.build();
	}
	
	@Override
	public FollowResponse follow(RpcController controller, FollowRequest request) {
		FollowResponse.Builder response = FollowResponse.newBuilder();
		try {
			impl.follow((NettyRpcController)controller, request, response);
		} catch (Exception e) {
			logger.error(e.getMessage(), e);
			((NettyRpcController)controller).setCode(SSResultCode.RC_SERVER_INTERNAL_ERROR.getNumber());
			((NettyRpcController)controller).setFailed(e.getMessage());
		}
		return response.build();
	}
	
	@Override
	public GetFollowerResponse getFollower(RpcController controller, GetFollowerRequest request) {
		GetFollowerResponse.Builder response = GetFollowerResponse.newBuilder();
		try {
			impl.getFollower((NettyRpcController)controller, request, response);
		} catch (Exception e) {
			logger.error(e.getMessage(), e);
			((NettyRpcController)controller).setCode(SSResultCode.RC_SERVER_INTERNAL_ERROR.getNumber());
			((NettyRpcController)controller).setFailed(e.getMessage());
		}
		return response.build();
	}
	

	@Override
	public GetFollowingResponse getFollowing(RpcController controller, GetFollowingRequest request) {
		GetFollowingResponse.Builder response = GetFollowingResponse.newBuilder();
		try {
			impl.getFollowing((NettyRpcController)controller, request, response);
		} catch (Exception e) {
			logger.error(e.getMessage(), e);
			((NettyRpcController)controller).setCode(SSResultCode.RC_SERVER_INTERNAL_ERROR.getNumber());
			((NettyRpcController)controller).setFailed(e.getMessage());
		}
		return response.build();
	}
	
	@Override
	public GetInviterResponse getInviter(RpcController controller, GetInviterRequest request) {
		GetInviterResponse.Builder response = GetInviterResponse.newBuilder();
		try {
			impl.getInviter((NettyRpcController)controller, request, response);
		} catch (Exception e) {
			logger.error(e.getMessage(), e);
			((NettyRpcController)controller).setCode(SSResultCode.RC_SERVER_INTERNAL_ERROR.getNumber());
			((NettyRpcController)controller).setFailed(e.getMessage());
		}
		return response.build();
	}
	
	@Override
	public GetInvitingResponse getInviting(RpcController controller, GetInvitingRequest request) {
		GetInvitingResponse.Builder response = GetInvitingResponse.newBuilder();
		try {
			impl.getInviting((NettyRpcController)controller, request, response);
		} catch (Exception e) {
			logger.error(e.getMessage(), e);
			((NettyRpcController)controller).setCode(SSResultCode.RC_SERVER_INTERNAL_ERROR.getNumber());
			((NettyRpcController)controller).setFailed(e.getMessage());
		}
		return response.build();
	}
	
	@Override
	public GetProfileResponse getProfile(RpcController controller, GetProfileRequest request) {
		GetProfileResponse.Builder response = GetProfileResponse.newBuilder();
		try {
			impl.getProfile((NettyRpcController)controller, request, response);
		} catch (Exception e) {
			logger.error(e.getMessage(), e);
			((NettyRpcController)controller).setCode(SSResultCode.RC_SERVER_INTERNAL_ERROR.getNumber());
			((NettyRpcController)controller).setFailed(e.getMessage());
		}
		return response.build();
	}
	
	@Override
	public SetProfileResponse setProfile(RpcController controller, SetProfileRequest request) {
		SetProfileResponse.Builder response = SetProfileResponse.newBuilder();
		try {
			impl.setProfile((NettyRpcController)controller, request, response);
		} catch (Exception e) {
			logger.error(e.getMessage(), e);
			((NettyRpcController)controller).setCode(SSResultCode.RC_SERVER_INTERNAL_ERROR.getNumber());
			((NettyRpcController)controller).setFailed(e.getMessage());
		}
		return response.build();
	}
	@Override
	public IsFollowerResponse isFollower(RpcController controller, IsFollowerRequest request) {
		IsFollowerResponse.Builder response = IsFollowerResponse.newBuilder();
		try {
			impl.isFollower((NettyRpcController)controller, request, response);
		} catch (Exception e) {
			logger.error(e.getMessage(), e);
			((NettyRpcController)controller).setCode(SSResultCode.RC_SERVER_INTERNAL_ERROR.getNumber());
			((NettyRpcController)controller).setFailed(e.getMessage());
		}
		return response.build();
	}
	@Override
	public InviteResponse invite(RpcController controller, InviteRequest request) {
		InviteResponse.Builder response = InviteResponse.newBuilder();
		try {
			impl.invite((NettyRpcController)controller, request, response);
		} catch (Exception e) {
			logger.error(e.getMessage(), e);
			((NettyRpcController)controller).setCode(SSResultCode.RC_SERVER_INTERNAL_ERROR.getNumber());
			((NettyRpcController)controller).setFailed(e.getMessage());
		}
		return response.build();
	}
	@Override
	public SendMessageResponse sendMessage(RpcController controller, SendMessageRequest request) {
		SendMessageResponse.Builder response = SendMessageResponse.newBuilder();
		try {
			impl.sendMessage((NettyRpcController)controller, request, response);
		} catch (Exception e) {
			logger.error(e.getMessage(), e);
			((NettyRpcController)controller).setCode(SSResultCode.RC_SERVER_INTERNAL_ERROR.getNumber());
			((NettyRpcController)controller).setFailed(e.getMessage());
		}
		return response.build();
	}
	@Override
	public GetMessageResponse getMessage(RpcController controller, GetMessageRequest request) {
		GetMessageResponse.Builder response = GetMessageResponse.newBuilder();
		try {
			impl.getMessage((NettyRpcController)controller, request, response);
		} catch (Exception e) {
			logger.error(e.getMessage(), e);
			((NettyRpcController)controller).setCode(SSResultCode.RC_SERVER_INTERNAL_ERROR.getNumber());
			((NettyRpcController)controller).setFailed(e.getMessage());
		}
		return response.build();
	}
	
	@Override
	public GetMessageByTimestampResponse getMessageByTimestamp(RpcController controller, GetMessageByTimestampRequest request) {
		GetMessageByTimestampResponse.Builder response = GetMessageByTimestampResponse.newBuilder();
		try {
			impl.getMessageByTimestamp((NettyRpcController)controller, request, response);
		} catch (Exception e) {
			logger.error(e.getMessage(), e);
			((NettyRpcController)controller).setCode(SSResultCode.RC_SERVER_INTERNAL_ERROR.getNumber());
			((NettyRpcController)controller).setFailed(e.getMessage());
		}
		return response.build();
	}
	@Override
	public RemoveMessageResponse removeMessage(RpcController controller, RemoveMessageRequest request) {
		RemoveMessageResponse.Builder response = RemoveMessageResponse.newBuilder();
		try {
			impl.removeMessage((NettyRpcController)controller, request, response);
		} catch (Exception e) {
			logger.error(e.getMessage(), e);
			((NettyRpcController)controller).setCode(SSResultCode.RC_SERVER_INTERNAL_ERROR.getNumber());
			((NettyRpcController)controller).setFailed(e.getMessage());
		}
		return response.build();
	}
	@Override
	public GetMessageByIdResponse getMessageById(RpcController controller, GetMessageByIdRequest request) {
		GetMessageByIdResponse.Builder response = GetMessageByIdResponse.newBuilder();
		try {
			impl.getMessageById((NettyRpcController)controller, request, response);
		} catch (Exception e) {
			logger.error(e.getMessage(), e);
			((NettyRpcController)controller).setCode(SSResultCode.RC_SERVER_INTERNAL_ERROR.getNumber());
			((NettyRpcController)controller).setFailed(e.getMessage());
		}
		return response.build();
	}

	@Override
	public AddContactResponse addContact(RpcController controller, AddContactRequest request) {
		AddContactResponse.Builder response = AddContactResponse.newBuilder();
		try {
			impl.addContact((NettyRpcController)controller, request, response);
		} catch (Exception e) {
			logger.error(e.getMessage(), e);
			((NettyRpcController)controller).setCode(SSResultCode.RC_SERVER_INTERNAL_ERROR.getNumber());
			((NettyRpcController)controller).setFailed(e.getMessage());
		}
		return response.build();
	}
	@Override
	public RemoveContactResponse removeContact(RpcController controller, RemoveContactRequest request) {
		RemoveContactResponse.Builder response = RemoveContactResponse.newBuilder();
		try {
			impl.removeContact((NettyRpcController)controller, request, response);
		} catch (Exception e) {
			logger.error(e.getMessage(), e);
			((NettyRpcController)controller).setCode(SSResultCode.RC_SERVER_INTERNAL_ERROR.getNumber());
			((NettyRpcController)controller).setFailed(e.getMessage());
		}
		return response.build();
	}
	@Override
	public GetContactResponse getContact(RpcController controller, GetContactRequest request) {
		GetContactResponse.Builder response = GetContactResponse.newBuilder();
		try {
			impl.getContact((NettyRpcController)controller, request, response);
		} catch (Exception e) {
			logger.error(e.getMessage(), e);
			((NettyRpcController)controller).setCode(SSResultCode.RC_SERVER_INTERNAL_ERROR.getNumber());
			((NettyRpcController)controller).setFailed(e.getMessage());
		}
		return response.build();
	}
	@Override
	public AddGroupResponse addGroup(RpcController controller, AddGroupRequest request) {
		AddGroupResponse.Builder response = AddGroupResponse.newBuilder();
		try {
			impl.addGroup((NettyRpcController)controller, request, response);
		} catch (Exception e) {
			logger.error(e.getMessage(), e);
			((NettyRpcController)controller).setCode(SSResultCode.RC_SERVER_INTERNAL_ERROR.getNumber());
			((NettyRpcController)controller).setFailed(e.getMessage());
		}
		return response.build();
	}
	@Override
	public RemoveGroupResponse removeGroup(RpcController controller, RemoveGroupRequest request) {
		RemoveGroupResponse.Builder response = RemoveGroupResponse.newBuilder();
		try {
			impl.removeGroup((NettyRpcController)controller, request, response);
		} catch (Exception e) {
			logger.error(e.getMessage(), e);
			((NettyRpcController)controller).setCode(SSResultCode.RC_SERVER_INTERNAL_ERROR.getNumber());
			((NettyRpcController)controller).setFailed(e.getMessage());
		}
		return response.build();
	}
	
	@Override
	public GetGroupResponse getGroup(RpcController controller, GetGroupRequest request) {
		GetGroupResponse.Builder response = GetGroupResponse.newBuilder();
		try {
			impl.getGroup((NettyRpcController)controller, request, response);
		} catch (Exception e) {
			logger.error(e.getMessage(), e);
			((NettyRpcController)controller).setCode(SSResultCode.RC_SERVER_INTERNAL_ERROR.getNumber());
			((NettyRpcController)controller).setFailed(e.getMessage());
		}
		return response.build();
	}
	@Override
	public AddMemberResponse addMember(RpcController controller, AddMemberRequest request) {
		AddMemberResponse.Builder response = AddMemberResponse.newBuilder();
		try {
			impl.addMember((NettyRpcController)controller, request, response);
		} catch (Exception e) {
			logger.error(e.getMessage(), e);
			((NettyRpcController)controller).setCode(SSResultCode.RC_SERVER_INTERNAL_ERROR.getNumber());
			((NettyRpcController)controller).setFailed(e.getMessage());
		}
		return response.build();
	}
	
	@Override
	public RemoveMemberResponse removeMember(RpcController controller, RemoveMemberRequest request) {
		RemoveMemberResponse.Builder response = RemoveMemberResponse.newBuilder();
		try {
			impl.removeMember((NettyRpcController)controller, request, response);
		} catch (Exception e) {
			logger.error(e.getMessage(), e);
			((NettyRpcController)controller).setCode(SSResultCode.RC_SERVER_INTERNAL_ERROR.getNumber());
			((NettyRpcController)controller).setFailed(e.getMessage());
		}
		return response.build();
	}
	
	@Override
	public GetMemberResponse getMember(RpcController controller, GetMemberRequest request) {
		GetMemberResponse.Builder response = GetMemberResponse.newBuilder();
		try {
			impl.getMember((NettyRpcController)controller, request, response);
		} catch (Exception e) {
			logger.error(e.getMessage(), e);
			((NettyRpcController)controller).setCode(SSResultCode.RC_SERVER_INTERNAL_ERROR.getNumber());
			((NettyRpcController)controller).setFailed(e.getMessage());
		}
		return response.build();
	}
		@Override
	public GetTorrentResponse getTorrent(RpcController controller, GetTorrentRequest request) {
			GetTorrentResponse.Builder response = GetTorrentResponse.newBuilder();
			try {
				impl.getTorrent((NettyRpcController)controller, request, response);
			} catch (Exception e) {
				logger.error(e.getMessage(), e);
				((NettyRpcController)controller).setCode(SSResultCode.RC_SERVER_INTERNAL_ERROR.getNumber());
				((NettyRpcController)controller).setFailed(e.getMessage());
			}
			return response.build();
		}

		@Override
		public SetTorrentResponse setTorrent(RpcController controller, SetTorrentRequest request) {
			SetTorrentResponse.Builder response = SetTorrentResponse.newBuilder();
			try {
				impl.setTorrent((NettyRpcController)controller, request, response);
			} catch (Exception e) {
				logger.error(e.getMessage(), e);
				((NettyRpcController)controller).setCode(SSResultCode.RC_SERVER_INTERNAL_ERROR.getNumber());
				((NettyRpcController)controller).setFailed(e.getMessage());
			}
			return response.build();
		}

        @Override
        public SetMessageResponse setMessage(RpcController controller, SetMessageRequest request) {
            SetMessageResponse.Builder response = SetMessageResponse.newBuilder();
            try {
                impl.setMessage((NettyRpcController)controller, request, response);
            } catch (Exception e) {
                logger.error(e.getMessage(), e);
                ((NettyRpcController)controller).setCode(SSResultCode.RC_SERVER_INTERNAL_ERROR.getNumber());
                ((NettyRpcController)controller).setFailed(e.getMessage());
            }
            return response.build();
        }

        @Override
        public GetCommentResponse getComment(RpcController controller, GetCommentRequest request) {
            GetCommentResponse.Builder response = GetCommentResponse.newBuilder();
            try {
                impl.getComment((NettyRpcController)controller, request, response);
            } catch (Exception e) {
                logger.error(e.getMessage(), e);
                ((NettyRpcController)controller).setCode(SSResultCode.RC_SERVER_INTERNAL_ERROR.getNumber());
                ((NettyRpcController)controller).setFailed(e.getMessage());
            }
            return response.build();
        }
}
