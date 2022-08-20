package com.zhuaiwa.session.service;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.google.protobuf.ServiceException;
import com.zhuaiwa.api.Common.SSResultCode;
import com.zhuaiwa.api.Rpc.AddContactRequest;
import com.zhuaiwa.api.Rpc.AddContactResponse;
import com.zhuaiwa.api.Rpc.AddGroupRequest;
import com.zhuaiwa.api.Rpc.AddGroupResponse;
import com.zhuaiwa.api.Rpc.AddMemberRequest;
import com.zhuaiwa.api.Rpc.AddMemberResponse;
import com.zhuaiwa.api.Rpc.GetContactRequest;
import com.zhuaiwa.api.Rpc.GetContactResponse;
import com.zhuaiwa.api.Rpc.GetGroupRequest;
import com.zhuaiwa.api.Rpc.GetGroupResponse;
import com.zhuaiwa.api.Rpc.GetMemberRequest;
import com.zhuaiwa.api.Rpc.GetMemberResponse;
import com.zhuaiwa.api.Rpc.RemoveContactRequest;
import com.zhuaiwa.api.Rpc.RemoveContactResponse;
import com.zhuaiwa.api.Rpc.RemoveGroupRequest;
import com.zhuaiwa.api.Rpc.RemoveGroupResponse;
import com.zhuaiwa.api.Rpc.RemoveMemberRequest;
import com.zhuaiwa.api.Rpc.RemoveMemberResponse;
import com.zhuaiwa.api.SSDataDomain.SSDataDomainSvc.BlockingInterface;
import com.zhuaiwa.api.netty.NettyRpcController;
import com.zhuaiwa.session.SessionManager;

public class ContactService_v_1_0 extends BaseService {
	private Logger LOG = LoggerFactory.getLogger(this.getClass());
	private BlockingInterface dataDomainClientSvc;

	public void setDataDomainClientSvc(BlockingInterface dataDomainClientSvc) {
		this.dataDomainClientSvc = dataDomainClientSvc;
	}

	public AddContactResponse addContact(NettyRpcController controller, AddContactRequest request) {
		String userid = SessionManager.getInstance().getSession(controller.getApiHeader().getSid()).userid;
		com.zhuaiwa.api.Rpc.AddContactRequest req = com.zhuaiwa.api.Rpc.AddContactRequest.newBuilder()
				.setUserid(userid).mergeFrom(request).build();
		try {
			return dataDomainClientSvc.addContact(getRpcController(), req);
		} catch (ServiceException e) {
			failed(controller, SSResultCode.RC_SERVICE_EXCEPTION.getNumber(), "添加联系人失败.", e);
			return null;
		}
	}

	public RemoveContactResponse removeContact(NettyRpcController controller, RemoveContactRequest request) {
		String userid = SessionManager.getInstance().getSession(controller.getApiHeader().getSid()).userid;
		com.zhuaiwa.api.Rpc.RemoveContactRequest req = com.zhuaiwa.api.Rpc.RemoveContactRequest.newBuilder()
				.setUserid(userid).mergeFrom(request).build();
		try {
			return dataDomainClientSvc.removeContact(getRpcController(), req);
		} catch (ServiceException e) {
			failed(controller, SSResultCode.RC_SERVICE_EXCEPTION.getNumber(), "删除联系人失败.", e);
			return null;
		}
	}

	public GetContactResponse getContact(NettyRpcController controller, GetContactRequest request) {
		String userid = SessionManager.getInstance().getSession(controller.getApiHeader().getSid()).userid;
		com.zhuaiwa.api.Rpc.GetContactRequest req = com.zhuaiwa.api.Rpc.GetContactRequest.newBuilder()
				.setUserid(userid).build();
		try {
			return dataDomainClientSvc.getContact(getRpcController(), req);
		} catch (ServiceException e) {
			failed(controller, SSResultCode.RC_SERVICE_EXCEPTION.getNumber(), "查询所有联系人失败.", e);
			return null;
		}
	}

	public AddGroupResponse addGroup(NettyRpcController controller, AddGroupRequest request) {
		String userid = SessionManager.getInstance().getSession(controller.getApiHeader().getSid()).userid;
		com.zhuaiwa.api.Rpc.AddGroupRequest req = com.zhuaiwa.api.Rpc.AddGroupRequest.newBuilder().setUserid(userid)
				.mergeFrom(request).build();
		try {
			return dataDomainClientSvc.addGroup(getRpcController(), req);
		} catch (ServiceException e) {
			failed(controller, SSResultCode.RC_SERVICE_EXCEPTION.getNumber(), "添加联系人分组失败.", e);
			return null;
		}
	}

	public RemoveGroupResponse removeGroup(NettyRpcController controller, RemoveGroupRequest request) {
		String userid = SessionManager.getInstance().getSession(controller.getApiHeader().getSid()).userid;
		com.zhuaiwa.api.Rpc.RemoveGroupRequest req = com.zhuaiwa.api.Rpc.RemoveGroupRequest.newBuilder()
				.setUserid(userid).mergeFrom(request).build();
		try {
			return dataDomainClientSvc.removeGroup(getRpcController(), req);
		} catch (ServiceException e) {
			failed(controller, SSResultCode.RC_SERVICE_EXCEPTION.getNumber(), "删除联系人分组失败.", e);
			return null;
		}
	}

	public GetGroupResponse getGroup(NettyRpcController controller, GetGroupRequest request) {
		String userid = SessionManager.getInstance().getSession(controller.getApiHeader().getSid()).userid;
		com.zhuaiwa.api.Rpc.GetGroupRequest req = com.zhuaiwa.api.Rpc.GetGroupRequest.newBuilder().setUserid(userid)
				.build();
		try {
			return dataDomainClientSvc.getGroup(getRpcController(), req);
		} catch (ServiceException e) {
			failed(controller, SSResultCode.RC_SERVICE_EXCEPTION.getNumber(), "查询所有联系人分组失败.", e);
			return null;
		}
	}

	public AddMemberResponse addMember(NettyRpcController controller, AddMemberRequest request) {
		String userid = SessionManager.getInstance().getSession(controller.getApiHeader().getSid()).userid;
		com.zhuaiwa.api.Rpc.AddMemberRequest req = com.zhuaiwa.api.Rpc.AddMemberRequest.newBuilder().setUserid(userid)
				.mergeFrom(request).build();
		try {
			return dataDomainClientSvc.addMember(getRpcController(), req);
		} catch (ServiceException e) {
			failed(controller, SSResultCode.RC_SERVICE_EXCEPTION.getNumber(), "添加联系人分组成员失败.", e);
			return null;
		}
	}

	public RemoveMemberResponse removeMember(NettyRpcController controller, RemoveMemberRequest request) {
		String userid = SessionManager.getInstance().getSession(controller.getApiHeader().getSid()).userid;
		com.zhuaiwa.api.Rpc.RemoveMemberRequest req = com.zhuaiwa.api.Rpc.RemoveMemberRequest.newBuilder()
				.setUserid(userid).mergeFrom(request).build();
		try {
			return dataDomainClientSvc.removeMember(getRpcController(), req);
		} catch (ServiceException e) {
			failed(controller, SSResultCode.RC_SERVICE_EXCEPTION.getNumber(), "删除联系人分组成员失败.", e);
			return null;
		}
	}

	public GetMemberResponse getMember(NettyRpcController controller, GetMemberRequest request) {
		String userid = SessionManager.getInstance().getSession(controller.getApiHeader().getSid()).userid;
		com.zhuaiwa.api.Rpc.GetMemberRequest req = com.zhuaiwa.api.Rpc.GetMemberRequest.newBuilder().setUserid(userid)
				.build();
		try {
			return dataDomainClientSvc.getMember(getRpcController(), req);
		} catch (ServiceException e) {
			failed(controller, SSResultCode.RC_SERVICE_EXCEPTION.getNumber(), "查询所有联系人分组成员失败.", e);
			return null;
		}
	}
}
