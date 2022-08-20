package com.channelsoft.zhuaiwa.dal.domain;

import java.nio.ByteBuffer;

import com.channelsoft.zhuaiwa.dal.annotation.Column;
import com.channelsoft.zhuaiwa.dal.annotation.ColumnFamily;
import com.channelsoft.zhuaiwa.dal.annotation.Key;
import com.channelsoft.zhuaiwa.dal.util.KeyUtils;

@ColumnFamily("Message")
public class Message extends BaseObject {
	public static final String CN_MESSAGE = "Message";
	public static final String FN_MSGID = "msgid";
	public static final String FN_TIMESTAMP = "timestamp";
	public static final String FN_MSG_TYPE = "msg_type";
	public static final String FN_SENDER = "sender";
	public static final String FN_RECEIVERS = "receivers";
	public static final String FN_CONTENT = "content";
//	public static final String FN_PARENT_ID = "parent_id";
//	public static final String FN_ROOT_ID = "root_id";
	
	public ByteBuffer generateId() {
		return KeyUtils.toByteBuffer(String.format("%1$016X%2$s", timestamp, sender));
	}
	
	public static String genMessageId(String userid, long number) {
		return String.format("%1$016X%2$s", number, userid);
	}
	
	public static String genMaxMessageId(long number) {
		return genMessageId("zzzzzzzzzzzzzzzzzzzzzzzzzzzzzzzz", number);
	}
	
	public static String genMinMessageId(long number) {
		return genMessageId("00000000000000000000000000000000", number);
	}
	
	@Key
	@Column(name=FN_MSGID)
	private String msgid;
	
	@Column(name=FN_TIMESTAMP)
	private Long timestamp;
	
	@Column(name=FN_MSG_TYPE)
	private Integer msgType;
	
	@Column(name=FN_SENDER)
	private String sender;
	
	@Column(name=FN_RECEIVERS)
	private String receivers;
	
	@Column(name=FN_CONTENT)
	private byte[] content;
	
//	@Column(name=FN_PARENT_ID)
//	private String parentId;
//	
//	@Column(name=FN_ROOT_ID)
//	private String rootId;
	
	public String getMsgid() {
		return msgid;
	}

	public void setMsgid(String msgid) {
		this.msgid = msgid;
	}

	public Long getTimestamp() {
		return timestamp;
	}

	public void setTimestamp(Long timestamp) {
		this.timestamp = timestamp;
	}

	public String getSender() {
		return sender;
	}

	public void setSender(String sender) {
		this.sender = sender;
	}

	public String getReceivers() {
		return receivers;
	}

	public void setReceivers(String receivers) {
		this.receivers = receivers;
	}

	public Integer getMsgType() {
		return msgType;
	}

	public void setMsgType(Integer msgType) {
		this.msgType = msgType;
	}

	public byte[] getContent() {
		return content;
	}

	public void setContent(byte[] content) {
		this.content = content;
	}

//	public String getParentId() {
//		return parentId;
//	}
//
//	public void setParentId(String parentId) {
//		this.parentId = parentId;
//	}
//
//	public String getRootId() {
//		return rootId;
//	}
//
//	public void setRootId(String rootId) {
//		this.rootId = rootId;
//	}
}
