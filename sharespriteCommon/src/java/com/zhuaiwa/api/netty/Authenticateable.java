package com.zhuaiwa.api.netty;

import com.google.protobuf.Descriptors.MethodDescriptor;
import com.google.protobuf.Message;
import com.google.protobuf.RpcCallback;

public interface Authenticateable {
	boolean authenticate(MethodDescriptor methodDescriptor, NettyRpcController controller, Message request, RpcCallback<Message> done);
}
