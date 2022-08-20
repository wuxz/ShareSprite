package com.zhuaiwa.dd.domain;

import java.util.ArrayList;
import java.util.List;

import com.zhuaiwa.dd.annotation.ColumnFamily;
import com.zhuaiwa.dd.annotation.Key;
import com.zhuaiwa.dd.annotation.SuperColumns;

@ColumnFamily(value="PubBox", isSuper=true)
public class PubBox extends BaseObject {
	public static final String CN_PUBBOX = "PubBox";
	
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
}
