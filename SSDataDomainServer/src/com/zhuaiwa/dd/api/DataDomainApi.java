package com.zhuaiwa.dd.api;

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
import com.zhuaiwa.api.SSDataDomain.SetAccountRequest;
import com.zhuaiwa.api.SSDataDomain.SetAccountResponse;
import com.zhuaiwa.api.SSDataDomain.SetMessageRequest;
import com.zhuaiwa.api.SSDataDomain.SetMessageResponse;
import com.zhuaiwa.api.netty.NettyRpcController;
import com.zhuaiwa.dd.exception.DALException;

public interface DataDomainApi {

    public void createAccount(NettyRpcController controller, CreateAccountRequest request, CreateAccountResponse.Builder response) throws DALException;

    public void getAccount(NettyRpcController controller, GetAccountRequest request, GetAccountResponse.Builder response) throws DALException;

    public void setAccount(NettyRpcController controller, SetAccountRequest request, SetAccountResponse.Builder response) throws DALException;

    public void bindAccount(NettyRpcController controller, BindAccountRequest request, BindAccountResponse.Builder response) throws DALException;

    public void unbindAccount(NettyRpcController controller, UnbindAccountRequest request, UnbindAccountResponse.Builder response) throws DALException;

    public void follow(NettyRpcController controller, FollowRequest request, FollowResponse.Builder response) throws DALException;

    public void unfollow(NettyRpcController controller, UnfollowRequest request, UnfollowResponse.Builder response) throws DALException;

    public void getFollowing(NettyRpcController controller, GetFollowingRequest request, GetFollowingResponse.Builder response) throws DALException;

    public void getFollower(NettyRpcController controller, GetFollowerRequest request, GetFollowerResponse.Builder response) throws DALException;

    public void getInviting(NettyRpcController controller, GetInvitingRequest request, GetInvitingResponse.Builder response) throws DALException;

    public void getInviter(NettyRpcController controller, GetInviterRequest request, GetInviterResponse.Builder response) throws DALException;

    public void getProfile(NettyRpcController controller, GetProfileRequest request, GetProfileResponse.Builder response) throws DALException;

    public void setProfile(NettyRpcController controller, SetProfileRequest request, SetProfileResponse.Builder response) throws DALException;

    public void isFollower(NettyRpcController controller, IsFollowerRequest request, IsFollowerResponse.Builder response) throws DALException;

    public void invite(NettyRpcController controller, InviteRequest request, InviteResponse.Builder response) throws DALException;

    public void getMessage(NettyRpcController controller, GetMessageRequest request, GetMessageResponse.Builder response) throws DALException;

    public void sendMessage(NettyRpcController controller, SendMessageRequest request, SendMessageResponse.Builder response) throws DALException;

    public void getMessageByTimestamp(NettyRpcController controller, GetMessageByTimestampRequest request, GetMessageByTimestampResponse.Builder response)
            throws DALException;

    public void getMessageById(NettyRpcController controller, GetMessageByIdRequest request, GetMessageByIdResponse.Builder response) throws DALException;

    public void removeMessage(NettyRpcController controller, RemoveMessageRequest request, RemoveMessageResponse.Builder response) throws DALException;

    public void addContact(NettyRpcController controller, AddContactRequest request, AddContactResponse.Builder response) throws DALException;

    public void getContact(NettyRpcController controller, GetContactRequest request, GetContactResponse.Builder response) throws DALException;

    public void removeContact(NettyRpcController controller, RemoveContactRequest request, RemoveContactResponse.Builder response) throws DALException;

    public void addGroup(NettyRpcController controller, AddGroupRequest request, AddGroupResponse.Builder response) throws DALException;

    public void removeGroup(NettyRpcController controller, RemoveGroupRequest request, RemoveGroupResponse.Builder response) throws DALException;

    public void getGroup(NettyRpcController controller, GetGroupRequest request, GetGroupResponse.Builder response) throws DALException;

    public void addMember(NettyRpcController controller, AddMemberRequest request, AddMemberResponse.Builder response) throws DALException;

    public void removeMember(NettyRpcController controller, RemoveMemberRequest request, RemoveMemberResponse.Builder response) throws DALException;

    public void getMember(NettyRpcController controller, GetMemberRequest request, GetMemberResponse.Builder response) throws DALException;

    public void getTorrent(NettyRpcController controller, GetTorrentRequest request, GetTorrentResponse.Builder response) throws DALException;

    public void setTorrent(NettyRpcController controller, SetTorrentRequest request, SetTorrentResponse.Builder response) throws DALException;

    public void setMessage(NettyRpcController controller, SetMessageRequest request, SetMessageResponse.Builder response) throws DALException;

    public void getComment(NettyRpcController controller, GetCommentRequest request, GetCommentResponse.Builder response) throws DALException;

}