package com.channelsoft.zhuaiwa.dal.domain;

import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

import com.channelsoft.zhuaiwa.dal.annotation.ColumnFamily;
import com.channelsoft.zhuaiwa.dal.annotation.Columns;
import com.channelsoft.zhuaiwa.dal.annotation.Key;
import com.channelsoft.zhuaiwa.dal.annotation.SuperColumns;

@ColumnFamily("TagMessages")
public class TagMessages extends BaseObject {
	public static final String CN_TAG_MESSAGES = "TagMessages";

	@Key
	private String userid;

	@SuperColumns(valueType = TagMessagesInfo.class)
	private List<TagMessagesInfo> messages = new ArrayList<TagMessagesInfo>();

	public String getUserid() {
		return userid;
	}

	public void setUserid(String userid) {
		this.userid = userid;
	}

	public List<TagMessagesInfo> getMessages() {
		return messages;
	}

	public void setMessages(List<TagMessagesInfo> messages) {
		this.messages = messages;
	}

	public static class TagMessagesInfo {
		@Key
		private String tagid;

		@Columns(nameType = String.class, valueType = Long.class)
		private Map<String, Long> messages = new LinkedHashMap<String, Long>();

		public String getTagid() {
			return tagid;
		}

		public void setTagid(String tagid) {
			this.tagid = tagid;
		}

		public Map<String, Long> getMessages() {
			return messages;
		}

		public void setMessages(Map<String, Long> messages) {
			this.messages = messages;
		}

	}
}
