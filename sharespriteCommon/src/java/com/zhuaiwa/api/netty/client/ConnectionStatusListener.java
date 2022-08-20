/**
 * 
 */
package com.zhuaiwa.api.netty.client;

/**
 * 监听连接状态变化的接口。
 * 
 * @author wuxz
 */
public interface ConnectionStatusListener
{
	/**
	 * 连接状态变化。
	 * 
	 * @param isConnected
	 *            当前状态是否为已连接。
	 * @param channel
	 *            发生状态改变的当前通道。
	 */
	public void onStatusChanged(boolean isConnected, Object channel);
}
