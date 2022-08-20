package com.zhuaiwa.session.util;

import com.google.protobuf.Message;
import com.google.protobuf.Descriptors.FieldDescriptor;
import com.zhuaiwa.api.netty.NettyRpcController;
import com.zhuaiwa.session.SessionManager;
import com.zhuaiwa.session.SessionManager.Session;

public class RpcUtils {
	// 优先获取request里的userid
	public static String getUserId(Message request, Session session) {
		String userid = null;
		if (userid == null && request != null) {
			FieldDescriptor fd = request.getDescriptorForType().findFieldByName("userid");
			if (fd != null && request.hasField(fd)) {
				userid = (String)request.getField(fd);
			}
		}
		if (userid == null && session != null) {
			userid = session.userid;
		}
		return userid;
	}
	// 优先获取request里的userid
	public static String getUserId(NettyRpcController controller, Message request) {
		Session session = SessionManager.getInstance().getSession(controller.getApiHeader().getSid());
		return getUserId(request, session);
	}
}
