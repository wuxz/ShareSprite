package com.zhuaiwa.session;

public interface PubSubManagerMBean {
	public int getTopics();

	public int getSubscriber(String topic);
}
