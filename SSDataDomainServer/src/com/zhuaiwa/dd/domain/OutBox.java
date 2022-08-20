package com.zhuaiwa.dd.domain;

import java.util.ArrayList;
import java.util.List;

import com.zhuaiwa.dd.annotation.ColumnFamily;
import com.zhuaiwa.dd.annotation.Key;
import com.zhuaiwa.dd.annotation.SuperColumns;

@ColumnFamily(value="OutBox", isSuper=true)
public class OutBox extends BaseObject {
	public static final String CN_OUTBOX = "OutBox";
	
	@Key
	private String userid;
	
	@SuperColumns(valueType=MessageInfo.class, descending=true)
	private List<MessageInfo> messages = new ArrayList<MessageInfo>();
	
	public String getUserid() {
		return userid;
	}

	public void setUserid(String userid) {
		this.userid = userid;
	}

	public List<MessageInfo> getMessages() {
		return messages;
	}

	public void setMessages(List<MessageInfo> messages) {
		this.messages = messages;
	}

//	public Map<String, Long> getMessages() {
//		return messages;
//	}
//
//	public void setMessages(Map<String, Long> messages) {
//		this.messages = messages;
//	}
}
