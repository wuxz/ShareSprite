package com.zhuaiwa.api.netty.server;

import java.net.SocketAddress;

import org.jboss.netty.bootstrap.ServerBootstrap;
import org.jboss.netty.channel.ChannelFactory;
import org.jboss.netty.channel.ChannelPipelineFactory;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.google.protobuf.Service;
import com.zhuaiwa.api.netty.Authenticateable;

public class NettyRpcServer {

	private static final Logger logger = LoggerFactory.getLogger(NettyRpcServer.class);
	
	private final ServerBootstrap bootstrap;
	private final NettyRpcServerChannelUpstreamHandler handler = new NettyRpcServerChannelUpstreamHandler();
	private final ChannelPipelineFactory pipelineFactory;
	
	public NettyRpcServer(ChannelFactory channelFactory) {
		this(channelFactory, false);
	}
	public NettyRpcServer(ChannelFactory channelFactory, boolean enableSSL) {
		this.pipelineFactory = new NettyRpcServerPipelineFactory(handler, enableSSL);
		bootstrap = new ServerBootstrap(channelFactory);
		bootstrap.setPipelineFactory(pipelineFactory);
        bootstrap.setOption("child.tcpNoDelay", true);
        bootstrap.setOption("child.keepAlive", true);
	}
	
	public void registerDefaultService(Service service) {
		handler.registerDefaultService(service, null);
	}
	
	public void registerDefaultService(Service service, Authenticateable auth) {
		handler.registerDefaultService(service, auth);
	}
	
	public void unregisterDefaultService(Service service) {
		handler.unregisterDefaultService(service);
	}
	
	public void registerVersionService(Service service, Integer version) {
		handler.registerVersionService(service, version, null);
	}

	public void registerVersionService(Service service, Integer version, Authenticateable auth) {
		handler.registerVersionService(service, version, auth);
	}
	
	public void unregisterVersionService(Service service, Integer version) {
		handler.unregisterVersionService(service, version);
	}
	
	public void serve() {
		logger.info("Serving...");
		bootstrap.bind();
	}
	
	public void serve(SocketAddress sa) {
		logger.info("Serving on: " + sa);
		bootstrap.bind(sa);
	}
	
}
