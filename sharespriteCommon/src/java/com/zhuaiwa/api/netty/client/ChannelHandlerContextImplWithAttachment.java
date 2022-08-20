/**
 * 
 */
package com.zhuaiwa.api.netty.client;

import org.apache.commons.lang.NotImplementedException;
import org.jboss.netty.channel.Channel;
import org.jboss.netty.channel.ChannelEvent;
import org.jboss.netty.channel.ChannelHandler;
import org.jboss.netty.channel.ChannelHandlerContext;
import org.jboss.netty.channel.ChannelPipeline;

/**
 * 支持带附件的ChannelHandlerContext的实现。
 * 
 * @author wuxz
 */
public class ChannelHandlerContextImplWithAttachment implements
		ChannelHandlerContext
{

	private Object attachment;

	/* (non-Javadoc)
	 * @see org.jboss.netty.channel.ChannelHandlerContext#canHandleDownstream()
	 */
	@Override
	public boolean canHandleDownstream()
	{
		throw new NotImplementedException();
	}

	/* (non-Javadoc)
	 * @see org.jboss.netty.channel.ChannelHandlerContext#canHandleUpstream()
	 */
	@Override
	public boolean canHandleUpstream()
	{
		throw new NotImplementedException();
	}

	/* (non-Javadoc)
	 * @see org.jboss.netty.channel.ChannelHandlerContext#getAttachment()
	 */
	@Override
	public Object getAttachment()
	{
		return attachment;
	}

	/* (non-Javadoc)
	 * @see org.jboss.netty.channel.ChannelHandlerContext#getChannel()
	 */
	@Override
	public Channel getChannel()
	{
		throw new NotImplementedException();
	}

	/* (non-Javadoc)
	 * @see org.jboss.netty.channel.ChannelHandlerContext#getHandler()
	 */
	@Override
	public ChannelHandler getHandler()
	{
		throw new NotImplementedException();
	}

	/* (non-Javadoc)
	 * @see org.jboss.netty.channel.ChannelHandlerContext#getName()
	 */
	@Override
	public String getName()
	{
		throw new NotImplementedException();
	}

	/* (non-Javadoc)
	 * @see org.jboss.netty.channel.ChannelHandlerContext#getPipeline()
	 */
	@Override
	public ChannelPipeline getPipeline()
	{
		throw new NotImplementedException();
	}

	/* (non-Javadoc)
	 * @see org.jboss.netty.channel.ChannelHandlerContext#sendDownstream(org.jboss.netty.channel.ChannelEvent)
	 */
	@Override
	public void sendDownstream(ChannelEvent e)
	{
		throw new NotImplementedException();
	}

	/* (non-Javadoc)
	 * @see org.jboss.netty.channel.ChannelHandlerContext#sendUpstream(org.jboss.netty.channel.ChannelEvent)
	 */
	@Override
	public void sendUpstream(ChannelEvent e)
	{
		throw new NotImplementedException();
	}

	/* (non-Javadoc)
	 * @see org.jboss.netty.channel.ChannelHandlerContext#setAttachment(java.lang.Object)
	 */
	@Override
	public void setAttachment(Object attachment)
	{
		this.attachment = attachment;
	}

}
