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
package com.zhuaiwa.api.netty.client;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.concurrent.atomic.AtomicLong;

import org.jboss.netty.channel.Channel;
import org.jboss.netty.channel.ChannelFuture;
import org.jboss.netty.channel.ChannelFutureListener;
import org.jboss.netty.channel.ChannelHandlerContext;
import org.jboss.netty.channel.ChannelStateEvent;
import org.jboss.netty.channel.ExceptionEvent;
import org.jboss.netty.channel.MessageEvent;
import org.jboss.netty.channel.SimpleChannelUpstreamHandler;
import org.jboss.netty.handler.ssl.SslHandler;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.google.protobuf.Descriptors.MethodDescriptor;
import com.google.protobuf.Message;
import com.google.protobuf.RpcCallback;
import com.google.protobuf.Service;
import com.google.protobuf.TextFormat;
import com.zhuaiwa.api.Common;
import com.zhuaiwa.api.Common.ApiHeader;
import com.zhuaiwa.api.Common.ApiMessage;
import com.zhuaiwa.api.Common.ApiNotification;
import com.zhuaiwa.api.Common.ApiRequest;
import com.zhuaiwa.api.Common.ApiResponse;
import com.zhuaiwa.api.Common.ApiType;
import com.zhuaiwa.api.netty.Authenticateable;
import com.zhuaiwa.api.netty.NettyRpcController;
import com.zhuaiwa.api.netty.exception.InvalidRpcRequestException;
import com.zhuaiwa.api.netty.exception.NoSuchServiceException;
import com.zhuaiwa.api.netty.exception.NoSuchServiceMethodException;
import com.zhuaiwa.api.netty.exception.RpcException;
import com.zhuaiwa.api.statistic.ApiStatistic;
import com.zhuaiwa.api.statistic.LatencyTracker;
import com.zhuaiwa.api.util.ApiExtensionHelper;

public class NettyRpcClientChannelUpstreamHandler extends SimpleChannelUpstreamHandler {

	private static final Logger logger = LoggerFactory.getLogger(NettyRpcClientChannelUpstreamHandler.class);
	
	private final AtomicInteger seqNum = new AtomicInteger(0);
	
	private final ConcurrentHashMap<Integer, ResponsePrototypeRpcCallback> callbackMap = new ConcurrentHashMap<Integer, ResponsePrototypeRpcCallback>();
	private final AtomicLong rpcCount = new AtomicLong();
	private final AtomicLong rpcCompletedCount = new AtomicLong();
	
	private String sid = null;
	private volatile long lastReceiveTime = 0;
	
    protected final Map<String, Service> serviceMap = new ConcurrentHashMap<String, Service>();
    protected Service getService(String serviceName) {
        return serviceMap.get(serviceName);
    }
	
	public int getNextSeqId() {
		return seqNum.getAndIncrement();
	}
	
	public int getSeqId() {
	    return seqNum.get();
	}
	
	public long getLastReceiveTime() {
        return lastReceiveTime;
    }
	
	public void setLastReceiveTime(long timestamp) {
	    this.lastReceiveTime = timestamp;
	}
	
	public String getSid() {
	    return sid;
	}
	
	public long getPendingCount() {
	    return rpcCount.get() - rpcCompletedCount.get();
	}
	
	public void registerCallback(int seqId, ResponsePrototypeRpcCallback callback) {
        if (callbackMap.putIfAbsent(seqId, callback) != null)
            throw new IllegalArgumentException("Callback already registered");
        rpcCount.incrementAndGet();
    }
	
	public ResponsePrototypeRpcCallback unregisterCallback(int seqId) {
	    ResponsePrototypeRpcCallback callback = callbackMap.remove(seqId);
	    if (callback != null)
	        rpcCompletedCount.incrementAndGet();
		return callback;
	}
	
	@Override
    public void channelConnected(
            ChannelHandlerContext ctx, ChannelStateEvent e) throws Exception {
	    lastReceiveTime = System.currentTimeMillis();

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
	    lastReceiveTime = System.currentTimeMillis();
	    
		ApiMessage message = (ApiMessage) e.getMessage();
		
		ApiHeader header = message.getHeader();
		if (header.hasSid() && !header.getSid().equals(sid)) {
		    sid = header.getSid();
		}
		
		if (header.getType() == ApiType.API_TYPE_REQUEST.getNumber()) {
			OnReceiveRequest(ctx, e, header, message.getExtension(Common.request));
		} else if (header.getType() == ApiType.API_TYPE_RESPONSE.getNumber()) {
			OnReceiveResponse(ctx, e, header, message.getExtension(Common.response));
		} else if (header.getType() == ApiType.API_TYPE_HEARTBEAT.getNumber()) {
			
		} else if (header.getType() == ApiType.API_TYPE_NOTIFICATION.getNumber()) {
			OnReceiveNotification(ctx, e, header, message.getExtension(Common.notification));
		} else {
		    
		}
	}
	
	private void OnReceiveRequest(ChannelHandlerContext ctx, MessageEvent e, ApiHeader header, ApiRequest request) throws Exception {
		if (logger.isDebugEnabled()) {
		    logger.debug(header.toString());
	        logger.debug(request.toString());
		}
	}
	
	private void OnReceiveResponse(ChannelHandlerContext ctx, MessageEvent e, ApiHeader header, ApiResponse response) throws Exception {

		if (!header.hasSeq()) {
			logger.warn("Should never receive response without seq");
			return;
		}
		
		int seqId = header.getSeq();
		ResponsePrototypeRpcCallback callback = unregisterCallback(seqId);
		
		if (callback == null) {
			logger.warn("Received response with no callback registered");
		} else {
			logger.debug("Invoking callback with response");
			callback.run(response);
		}
		
	}
	
	private void OnReceiveNotification(ChannelHandlerContext ctx, MessageEvent e, ApiHeader header, ApiNotification notification) throws Exception {
	    final String serviceName = notification.getService();
        final String methodName = notification.getMethod();
        
//      logger.info("Received notification: " + header.getSeq() + ", service: " + serviceName + ", method: " + methodName);
        
        Service service = getService(serviceName);
        if (service == null) {
            throw new NoSuchServiceException(header, notification, serviceName);
        } else if (service.getDescriptorForType().findMethodByName(methodName) == null) {
            throw new NoSuchServiceMethodException(header, notification, methodName);
        }
        
        try {
            MethodDescriptor methodDescriptor = service.getDescriptorForType().findMethodByName(methodName);
            final Message methodRequest = 
                    notification.getExtension(ApiExtensionHelper.<Message>getNotificationByMethodName(methodName));
            if (methodRequest == null)
                throw new InvalidRpcRequestException(header, notification, methodName);
            
            final NettyRpcController controller = new NettyRpcController();
            
            controller.setChannelHandlerContext(ctx);
            controller.setApiHeader(header);
            service.callMethod(methodDescriptor, controller, methodRequest, null);
        } catch (InvalidRpcRequestException ex) {
            throw ex;
        } catch (Exception ex) {
            throw new RpcException(ex, header, notification, "Service threw unexpected exception");
        }
	}
	
	@Override
	public void exceptionCaught(ChannelHandlerContext ctx, ExceptionEvent e) throws Exception {
		Throwable t = e.getCause();
		if (t instanceof java.net.ConnectException) {
			logger.error("Connection exception: " + t.getMessage());
			e.getChannel().close();
			return;
		}
		logger.error("Unhandled exception in handler", e.getCause());
		e.getChannel().close();
		throw new Exception(e.getCause());
	}
	
    public synchronized void registerService(Service service) {
        String serviceName = service.getDescriptorForType().getName();
        if(serviceMap.containsKey(serviceName)) {
            throw new IllegalArgumentException("Service already registered");
        }
        serviceMap.put(serviceName, service);
    }
    
    public synchronized void unregisterService(Service service) {
        String serviceName = service.getDescriptorForType().getName();
        if(!serviceMap.containsKey(serviceName)) {
            throw new IllegalArgumentException("Service not already registered");
        }
        serviceMap.remove(serviceName);
    }
}
