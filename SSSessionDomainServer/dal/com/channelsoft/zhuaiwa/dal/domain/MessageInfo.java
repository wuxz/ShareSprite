package com.channelsoft.zhuaiwa.dal.domain;

import com.channelsoft.zhuaiwa.dal.annotation.Column;
import com.channelsoft.zhuaiwa.dal.annotation.Key;

public class MessageInfo {
	public static final String FN_MSGID = "msgid";
//	public static final String FN_ROOTID = "rootid";
//	public static final String FN_PARENTID = "parentid";
	public static final String FN_TIMESTAMP = "timestamp";
//	public static final String FN_SENDER = "sender";
//	public static final String FN_RECEIVERS = "receivers";
	
	@Key
	@Column(name=FN_MSGID)
	private String msgid;
	
//	@Column(name=FN_ROOTID)
//	private String rootid;
//	
//	@Column(name=FN_PARENTID)
//	private String parentid;
	
	public Long getTimestamp() {
		return timestamp;
	}

	public void setTimestamp(Long timestamp) {
		this.timestamp = timestamp;
	}

	@Column(name=FN_TIMESTAMP)
	private Long timestamp;

//	public String getRootid() {
//		return rootid;
//	}
//
//	public void setRootid(String rootid) {
//		this.rootid = rootid;
//	}
//
//	public String getParentid() {
//		return parentid;
//	}
//
//	public void setParentid(String parentid) {
//		this.parentid = parentid;
//	}

	public void setMsgid(String msgid) {
		this.msgid = msgid;
	}

	public String getMsgid() {
		return msgid;
	}
}
