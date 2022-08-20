package com.zhuaiwa.session;

import com.zhuaiwa.session.PubSubManager.TopicType;

public class PubSubUtil {

	public static String getTopicId(TopicType topicType, String id) {
		if (topicType.equals(TopicType.PS_)) {
			return TopicType.PS_.name() + id;
		} else if (topicType.equals(TopicType.RS_)) {
			return TopicType.RS_.name() + id;
		} else if (topicType.equals(TopicType.PP_)) {
			return TopicType.PP_.name() + id;
		} else {
			return TopicType.UN_.name() + id;
		}
	}

	public static String getPublicShareTopic(String userid) {
		return TopicType.PS_.name() + userid;
	}

	public static String getPublicPrecenceTopic(String userid) {
		return TopicType.PP_.name() + userid;
	}

	public static String getProtecedShareTopic(String userid) {
		return TopicType.RS_.name() + userid;
	}

	public static TopicType getTopicType(String topic) {
		if (topic.startsWith(TopicType.PS_.name())) {
			return TopicType.PS_;
		} else if (topic.startsWith(TopicType.RS_.name())) {
			return TopicType.RS_;
		} else if (topic.startsWith(TopicType.PP_.name())) {
			return TopicType.PP_;
		} else {
			return TopicType.UN_;
		}
	}
}
