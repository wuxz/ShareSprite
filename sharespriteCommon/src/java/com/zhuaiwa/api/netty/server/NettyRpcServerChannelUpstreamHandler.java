/*
 * Copyright (c) 2009 Stephen Tu <stephen_tu@berkeley.edu>
 *
 * Permission is hereby granted, free of charge, to any person obtaining a copy
 * of this software and associated documentation files (the "Software"), to deal
 * in the Software without restriction, including without limitation the rights
 * to use, copy, modify, merge, publish, distribute, sublicense, and/or sell
 * copies of the Software, and to permit persons to whom the Software is
 * furnished to do so, subject to the following conditions:
 * 
 * The above copyright notice and this permission notice shall be included in
 * all copies or substantial portions of the Software.
 * 
 * THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR
 * IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY,
 * FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE
 * AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER
 * LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM,
 * OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN
 * THE SOFTWARE.
 */
package com.zhuaiwa.api.netty.server;

import java.io.IOException;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

import org.jboss.netty.channel.Channel;
import org.jboss.netty.channel.ChannelEvent;
import org.jboss.netty.channel.ChannelFuture;
import org.jboss.netty.channel.ChannelFutureListener;
import org.jboss.netty.channel.ChannelHandler.Sharable;
import org.jboss.netty.channel.ChannelHandlerContext;
import org.jboss.netty.channel.ChannelStateEvent;
import org.jboss.netty.channel.ExceptionEvent;
import org.jboss.netty.channel.MessageEvent;
import org.jboss.netty.channel.SimpleChannelUpstreamHandler;
import org.jboss.netty.handler.ssl.SslHandler;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.google.protobuf.ByteString;
import com.google.protobuf.Descriptors.MethodDescriptor;
import com.google.protobuf.Descriptors.ServiceDescriptor;
import com.google.protobuf.InvalidProtocolBufferException;
import com.google.protobuf.Message;
import com.google.protobuf.RpcCallback;
import com.google.protobuf.Service;
import com.google.protobuf.TextFormat;
import com.zhuaiwa.api.Common;
import com.zhuaiwa.api.Common.ApiHeader;
import com.zhuaiwa.api.Common.ApiHeartbeat;
import com.zhuaiwa.api.Common.ApiMessage;
import com.zhuaiwa.api.Common.ApiRequest;
import com.zhuaiwa.api.Common.ApiResponse;
import com.zhuaiwa.api.Common.ApiType;
import com.zhuaiwa.api.Common.SSResultCode;
import com.zhuaiwa.api.netty.Authenticateable;
import com.zhuaiwa.api.netty.NettyRpcController;
import com.zhuaiwa.api.netty.exception.InvalidRpcRequestException;
import com.zhuaiwa.api.netty.exception.NoSuchServiceException;
import com.zhuaiwa.api.netty.exception.NoSuchServiceMethodException;
import com.zhuaiwa.api.netty.exception.RpcException;
import com.zhuaiwa.api.netty.exception.RpcServiceException;
import com.zhuaiwa.api.statistic.ApiStatistic;
import com.zhuaiwa.api.statistic.LatencyStats;
import com.zhuaiwa.api.statistic.LatencyTracker;
import com.zhuaiwa.api.util.ApiExtensionHelper;

@Sharable()
public class NettyRpcServerChannelUpstreamHandler extends SimpleChannelUpstreamHandler {

	private static final Logger logger = LoggerFactory.getLogger(NettyRpcServerChannelUpstreamHandler.class);
	private static final Logger logger4performance = LoggerFactory.getLogger("performancelogger");

	protected final Map<String, Service> defaultServiceMap = new ConcurrentHashMap<String, Service>();
	protected final Map<String, Map<Integer, Service>> serviceMap = new ConcurrentHashMap<String, Map<Integer,Service>>();
	protected final Map<Service, Authenticateable> authenticateMap = new ConcurrentHashMap<Service, Authenticateable>();
	protected final Map<Service, ApiStatistic> serviceStatisticMap = new ConcurrentHashMap<Service, ApiStatistic>();
	
	protected Service getService(String serviceName, Integer version) {
//		logger.info("serviceName: " + serviceName + ", version: " + version);
		if (version == null || version == 0) {
			return defaultServiceMap.get(serviceName);
		}
		Map<Integer, Service> service_versions = serviceMap.get(serviceName);
		if (service_versions != null) {
			return service_versions.get(version);
		}
		return null;
	}
	
	protected ApiStatistic getStatistic(Service service) {
		ApiStatistic s = serviceStatisticMap.get(service);
		if (s == null) {
			synchronized (serviceStatisticMap) {
				s = serviceStatisticMap.get(service);
				if (s == null) {
					ServiceDescriptor serviceDescriptor = service.getDescriptorForType();
					s = new ApiStatistic(serviceDescriptor.getName());
					List<MethodDescriptor> methodDescriptors = serviceDescriptor.getMethods();
					int size = methodDescriptors.size();
					for (int i = 0; i < size; i++) {
						s.add(new LatencyTracker(new LatencyStats()));
					}
					for (MethodDescriptor methodDescriptor : methodDescriptors) {
						logger.info("add tracker: " + methodDescriptor.getIndex() + ", " + methodDescriptor.getName());
						LatencyTracker tracker = s.get(methodDescriptor.getIndex());
						tracker.getLatencyStats().setName(methodDescriptor.getName());
						s.set(methodDescriptor.getIndex(), tracker);
					}
					serviceStatisticMap.put(service, s);
				}
			}
		}
		return s;
	}
	
	@Override
    public void channelConnected(
            ChannelHandlerContext ctx, ChannelStateEvent e) throws Exception {

        // Get the SslHandler in the current pipeline.
        // We added it in SecureChatPipelineFactory.
        final SslHandler sslHandler = ctx.getPipeline().get(SslHandler.class);
        if (sslHandler == null)
        	return;

        // Get notified when SSL handshake is done.
        ChannelFuture handshakeFuture = sslHandler.handshake();
        handshakeFuture.addListener(new ChannelFutureListener() {
			@Override
			public void operationComplete(ChannelFuture future) throws Exception {
				if (!future.isSuccess()) {
					future.getChannel().close();
				}
			}
		});
    }
	
	@Override
	public void messageReceived(ChannelHandlerContext ctx, MessageEvent e) throws Exception {
		final ApiMessage message = (ApiMessage) e.getMessage();
		if (logger.isDebugEnabled())
		    logger.debug(TextFormat.printToString(message));
		
		if (message.getHeader().getType() == ApiType.API_TYPE_REQUEST.getNumber()) {
			OnReceiveRequest(ctx, e, message.getHeader(), message.getExtension(Common.request));
		} else if (message.getHeader().getType() == ApiType.API_TYPE_RESPONSE.getNumber()) {
			OnReceiveResponse(ctx, e, message.getHeader(), message.getExtension(Common.response));
		} else if (message.getHeader().getType() == ApiType.API_TYPE_HEARTBEAT.getNumber()){
			OnHeartbeatRequest(ctx, e, message.getHeader());
		} else {
			logger.warn("Can't handle the received message type: " + message.getHeader().getType());
		}
	}
	
	private void messageSend(Channel channel, ApiMessage message) {
	    if (logger.isDebugEnabled())
	        logger.debug(TextFormat.printToString(message));
		channel.write(message);
	}
	
	private void OnHeartbeatRequest(ChannelHandlerContext ctx, MessageEvent e, ApiHeader header) {
		final Channel channel = e.getChannel();
		final NettyRpcController controller = new NettyRpcController();
		controller.setChannelHandlerContext(ctx);
		controller.setApiHeader(header);
		controller.setApiResponse(ApiResponse.newBuilder());
		
		ApiHeader.Builder responseApiHeader = ApiHeader.newBuilder();
		responseApiHeader.setSeq(controller.getApiHeader().getSeq());
		if (controller.getApiHeader().hasVersion()) {
			responseApiHeader.setVersion(controller.getApiHeader().getVersion());
		}
		if (controller.getApiHeader().hasSid()) {
			responseApiHeader.setSid(controller.getApiHeader().getSid());
		}
		
		responseApiHeader.setType(ApiType.API_TYPE_HEARTBEAT.getNumber());// 包类型
		
		ApiHeartbeat.Builder responseApiHearbeat = ApiHeartbeat.newBuilder();
		
		// api message
		ApiMessage.Builder responseApiMessage = ApiMessage.newBuilder();
		responseApiMessage.setHeader(responseApiHeader);
		
		// send
		messageSend(channel, responseApiMessage.build());
	}
	
//	final ByteString payload = ByteString.copyFrom(new byte[84]);
//    private void OnReceiveRequest2(ChannelHandlerContext ctx, MessageEvent e, ApiHeader header, ApiRequest request) throws Exception {
//        // header
//        ApiHeader.Builder responseApiHeader = ApiHeader.newBuilder();
//        responseApiHeader.setSeq(header.getSeq());
//        if (header.hasVersion()) {
//            responseApiHeader.setVersion(header.getVersion());
//        }
//        if (header.hasSid()) {
//            responseApiHeader.setSid(header.getSid());
//        }
//        responseApiHeader.setType(ApiType.API_TYPE_RESPONSE.getNumber());// 包类型，取值为：请求为0、响应为1
//
//        // body
//        ApiResponse.Builder responseApiResponse = ApiResponse.newBuilder();
//        responseApiResponse.setCode(SSResultCode.RC_OK.getNumber());
//        responseApiResponse.setExtension(Common.testResponse, TestResponse.newBuilder().setPayload(payload).build());
//
//        // api message
//        ApiMessage.Builder responseApiMessage = ApiMessage.newBuilder();
//        responseApiMessage.setHeader(responseApiHeader);
//        responseApiMessage.setExtension(Common.response, responseApiResponse.build());
//        
//        // send
//        messageSend(e.getChannel(), responseApiMessage.build());
//    }

	private void OnReceiveRequest(ChannelHandlerContext ctx, MessageEvent e, ApiHeader header, ApiRequest request) throws Exception {
		final String serviceName = request.getService();
		final String methodName = request.getMethod();
		Integer version = header.hasVersion() ? header.getVersion() : null;
		
//		logger.info("Received request: " + header.getSeq() + ", service: " + serviceName + ", method: " + methodName + ", version: " + version);
		
		Service service = getService(serviceName, version);
		if (service == null) {
			throw new NoSuchServiceException(header, request, serviceName);
		} else if (service.getDescriptorForType().findMethodByName(methodName) == null) {
			throw new NoSuchServiceMethodException(header, request, methodName);
		}
		
		try {
			MethodDescriptor methodDescriptor = service.getDescriptorForType().findMethodByName(methodName);
			final Message methodRequest = 
					request.getExtension(ApiExtensionHelper.<Message>getRequestByMethodName(request.getMethod()));
			if (methodRequest == null)
				throw new InvalidRpcRequestException(header, request, methodName);
			
			final Channel channel = e.getChannel();
			final NettyRpcController controller = new NettyRpcController();
			final long startTime = System.nanoTime();
			
			ApiStatistic statistic = getStatistic(service);
			final LatencyTracker tracker = statistic.get(methodDescriptor.getIndex());
			final LatencyTracker stats = statistic.getStatistic();
			
			controller.setChannelHandlerContext(ctx);
			controller.setApiHeader(header);
			controller.setApiRequest(request);
			controller.setApiResponse(ApiResponse.newBuilder());
			
			RpcCallback<Message> callback = !header.hasSeq() ? null : new RpcCallback<Message>() {
				public void run(Message methodResponse) {
					
//					logger.info("Send response: seq=" + controller.getApiHeader().getSeq() + ", code=" + controller.getCode() + ", reason=" + controller.errorText());

					// header
					ApiHeader.Builder responseApiHeader = ApiHeader.newBuilder();
					responseApiHeader.setSeq(controller.getApiHeader().getSeq());
					if (controller.getApiHeader().hasVersion()) {
						responseApiHeader.setVersion(controller.getApiHeader().getVersion());
					}
					if (controller.getApiHeader().hasSid()) {
						responseApiHeader.setSid(controller.getApiHeader().getSid());
					}
					responseApiHeader.setType(ApiType.API_TYPE_RESPONSE.getNumber());// 包类型，取值为：请求为0、响应为1

					// body
					ApiResponse.Builder responseApiResponse = controller.getApiResponse();
					if (methodResponse == null) {
						responseApiResponse.setCode(controller.getCode());
						if (controller.errorText() != null) {
							responseApiResponse.setReason(controller.errorText());
						} else {
							
						}
					} else {
						
//						logger.info(TextFormat.printToString(methodResponse));
						
						responseApiResponse.setCode(controller.getCode());
						if (controller.errorText()!= null)
							responseApiResponse.setReason(controller.errorText());
						responseApiResponse.setExtension(ApiExtensionHelper.getResponseByMethodName(methodName), methodResponse);
					}

					// api message
					ApiMessage.Builder responseApiMessage = ApiMessage.newBuilder();
					responseApiMessage.setHeader(responseApiHeader);
					responseApiMessage.setExtension(Common.response, responseApiResponse.build());
					
					// send
					messageSend(channel, responseApiMessage.build());
					
					long endTime = System.nanoTime();
					long spendTime = endTime - startTime;
					tracker.getLatencyStats().addNano(spendTime);
					stats.getLatencyStats().addNano(spendTime);
					if (spendTime > 1000000000) { //延迟超过1秒
						logger4performance.info(controller.getApiHeader().getSeq() + ": " + spendTime);
						logger4performance.info(methodRequest.getDescriptorForType().getName());
						logger4performance.info(TextFormat.printToString(methodRequest));
						if (methodResponse != null) {
							logger4performance.info(methodResponse.getDescriptorForType().getName());
							logger4performance.info(TextFormat.printToString(methodResponse));
						}
					}
					return;
				}
			};
			
//			logger.info(TextFormat.printToString(methodRequest));
			
			Authenticateable auth = this.authenticateMap.get(service);
			if (auth != null) {
				if (!auth.authenticate(methodDescriptor, controller, methodRequest, callback)) {
					return;
				}
			}
			service.callMethod(methodDescriptor, controller, methodRequest, callback);
		} catch (InvalidRpcRequestException ex) {
			throw ex;
		} catch (Exception ex) {
			throw new RpcException(ex, header, request, "Service threw unexpected exception");
		}
	}
	
	private void OnReceiveResponse(ChannelHandlerContext ctx, MessageEvent e, ApiHeader header, ApiResponse response) throws Exception {
		
	}
	
	@Override
	public void exceptionCaught(ChannelHandlerContext ctx, ExceptionEvent e) {
		if (e.getCause() != null 
				&& e.getCause() instanceof IOException 
				&& e.getCause().getMessage() != null
				&& e.getCause().getMessage().equalsIgnoreCase("Connection reset by peer")) {
			logger.warn(e.getCause().getMessage());
			return;
		}
		if (!(e.getCause() instanceof RpcException)) {
			/* Cannot respond to this exception, because it is not tied
			 * to a request */
			logger.info("Cannot respond to handler exception" + ctx.getChannel().toString(), e.getCause());
			return;
		}
		
		RpcException ex = (RpcException) e.getCause();
		if (ex.getHeader() != null && ex.getHeader().hasSeq()) {
			// header
			ApiHeader.Builder responseApiHeader = ApiHeader.newBuilder();
			responseApiHeader.setSeq(ex.getHeader().getSeq());
			if (ex.getHeader().hasVersion()) {
				responseApiHeader.setVersion(ex.getHeader().getVersion());
			}
			if (ex.getHeader().hasSid()) {
				responseApiHeader.setSid(ex.getHeader().getSid());
			}
			responseApiHeader.setType(ApiType.API_TYPE_RESPONSE.getNumber());// 包类型，取值为：请求为0、响应为1
			
			// body
			ApiResponse.Builder responseApiResponse = ApiResponse.newBuilder();
			if (ex instanceof NoSuchServiceException) {
				responseApiResponse.setCode(SSResultCode.RC_NO_SUCH_SERVICE.getNumber());
				if (ex.getMessage() != null) {
					responseApiResponse.setReason(ex.getMessage());
				}
			} else if (ex instanceof NoSuchServiceMethodException) {
				responseApiResponse.setCode(SSResultCode.RC_NO_SUCH_METHOD.getNumber());
				if (ex.getMessage() != null) {
					responseApiResponse.setReason(ex.getMessage());
				}
			} else if (ex instanceof InvalidRpcRequestException) {
				responseApiResponse.setCode(SSResultCode.RC_REQUEST_INVALID.getNumber());
				if (ex.getMessage() != null) {
					responseApiResponse.setReason(ex.getMessage());
				}
			} else if (ex instanceof RpcServiceException) {
				responseApiResponse.setCode(SSResultCode.RC_SERVER_INTERNAL_ERROR.getNumber());
				if (ex.getMessage() != null) {
					responseApiResponse.setReason(ex.getMessage());
				}
			} else if (ex instanceof RpcException) {
				responseApiResponse.setCode(SSResultCode.RC_SERVER_INTERNAL_ERROR.getNumber());
				if (ex.getCause() != null && ex.getCause().getMessage() != null) {
					responseApiResponse.setReason(ex.getCause().getMessage());
				}
			}
			
//			logger.info("Send response: seq=" + responseApiHeader.getSeq() + ", code=" + responseApiResponse.getCode() + ", reason=" + responseApiResponse.getReason());

			// api message
			ApiMessage.Builder responseApiMessage = ApiMessage.newBuilder();
			responseApiMessage.setHeader(responseApiHeader);
			responseApiMessage.setExtension(Common.response, responseApiResponse.build());
			
			// send
			messageSend(e.getChannel(), responseApiMessage.build());
		} else {
			logger.info("Cannot respond to handler exception", ex);
		}
	}
	
	protected Message buildMessageFromPrototype(Message prototype, ByteString messageToBuild) throws InvalidProtocolBufferException {
		return prototype.newBuilderForType().mergeFrom(messageToBuild).build();
	}
	
	public synchronized void registerDefaultService(Service service, Authenticateable authenticate) {
		String serviceName = service.getDescriptorForType().getName();
		if(defaultServiceMap.containsKey(serviceName)) {
			throw new IllegalArgumentException("Service already registered");
		}
		defaultServiceMap.put(serviceName, service);
		if (authenticate != null)
			authenticateMap.put(service, authenticate);
		
		// 初始化统计
		getStatistic(service);
	}
	
	public synchronized void unregisterDefaultService(Service service) {
		String serviceName = service.getDescriptorForType().getName();
		if(!defaultServiceMap.containsKey(serviceName)) {
			throw new IllegalArgumentException("Service not already registered");
		}
		defaultServiceMap.remove(serviceName);
	}
	
	public synchronized void registerVersionService(Service service, Integer version, Authenticateable authenticate) {
		String serviceName = service.getDescriptorForType().getName();
		Map<Integer, Service> versionServices = serviceMap.get(serviceName);
		if (versionServices == null) {
			versionServices = new ConcurrentHashMap<Integer, Service>();
			serviceMap.put(serviceName, versionServices);
		}
		if (versionServices.containsKey(version)) {
			throw new IllegalArgumentException("Service already registered");
		}
		versionServices.put(version, service);
		if (authenticate != null)
			authenticateMap.put(service, authenticate);

        // 初始化统计
        getStatistic(service);
	}
	
	public synchronized void unregisterVersionService(Service service, Integer version) {
		String serviceName = service.getDescriptorForType().getName();
		Map<Integer, Service> versionServices = serviceMap.get(serviceName);
		if(versionServices == null || !versionServices.containsKey(version)) {
			throw new IllegalArgumentException("Service not already registered");
		}
		versionServices.remove(version);
	}

    @Override
    public void handleUpstream(ChannelHandlerContext ctx, ChannelEvent e) throws Exception {

        if (logger.isDebugEnabled())
            logger.debug("upstream channel event:{}", e);

        super.handleUpstream(ctx, e);
    }

}