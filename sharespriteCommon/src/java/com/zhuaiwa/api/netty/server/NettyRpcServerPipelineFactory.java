package com.zhuaiwa.api.netty.server;

import java.util.concurrent.ArrayBlockingQueue;
import java.util.concurrent.LinkedBlockingQueue;
import java.util.concurrent.ThreadPoolExecutor;
import java.util.concurrent.TimeUnit;

import javax.net.ssl.SSLEngine;

import org.jboss.netty.channel.ChannelHandler;
import org.jboss.netty.channel.ChannelPipeline;
import org.jboss.netty.channel.ChannelPipelineFactory;
import org.jboss.netty.channel.ChannelUpstreamHandler;
import org.jboss.netty.channel.Channels;
import org.jboss.netty.handler.codec.frame.LengthFieldBasedFrameDecoder;
import org.jboss.netty.handler.codec.frame.LengthFieldPrepender;
import org.jboss.netty.handler.codec.protobuf.ProtobufDecoder;
import org.jboss.netty.handler.codec.protobuf.ProtobufEncoder;
import org.jboss.netty.handler.execution.ExecutionHandler;
import org.jboss.netty.handler.ssl.SslHandler;

import com.google.protobuf.ExtensionRegistry;
import com.google.protobuf.Message;
import com.zhuaiwa.api.Common.ApiMessage;
import com.zhuaiwa.api.netty.ssl.NettyRpcSslContextFactory;
import com.zhuaiwa.api.util.ApiExtensionHelper;

public class NettyRpcServerPipelineFactory implements ChannelPipelineFactory {

    private static final int MAX_FRAME_BYTES_LENGTH = 1048576;
    private static final ChannelHandler executor = new ExecutionHandler(new JMXEnabledThreadPoolExecutor(
            "RpcExecutor",
            64, 256,
            30, TimeUnit.SECONDS,
            new ArrayBlockingQueue<Runnable>(65535),
            new ThreadPoolExecutor.CallerRunsPolicy()));
    
    private ChannelUpstreamHandler handler;
    private boolean enableSSL = false;
    private Message defaultInstance = ApiMessage.getDefaultInstance();
    private ExtensionRegistry extensionRegistry = ApiExtensionHelper.getExtensionRegistry();
    
    public NettyRpcServerPipelineFactory(ChannelUpstreamHandler handler, boolean enableSSL) {
        super();
        this.handler = handler;
        this.enableSSL = enableSSL;
    }

    @Override
    public ChannelPipeline getPipeline() throws Exception {
        ChannelPipeline p = Channels.pipeline();
        
        if (enableSSL) {
            SSLEngine engine;
            engine = NettyRpcSslContextFactory.getServerContext().createSSLEngine();
            engine.setUseClientMode(false);
            p.addLast("ssl", new SslHandler(engine));
        }
        
        p.addLast("frameDecoder", new LengthFieldBasedFrameDecoder(MAX_FRAME_BYTES_LENGTH, 0, 4, 0, 4));
        p.addLast("frameEncoder", new LengthFieldPrepender(4));
        
        p.addLast("protobufDecoder", new ProtobufDecoder(defaultInstance, extensionRegistry));
        p.addLast("protobufEncoder", new ProtobufEncoder());
        
        p.addLast("executor", executor);
        p.addLast("handler", handler);
        return p;
    }

}
