package com.zhuaiwa.dd.domain;

import java.util.Date;

import com.zhuaiwa.dd.annotation.Column;
import com.zhuaiwa.dd.annotation.ColumnFamily;
import com.zhuaiwa.dd.annotation.Key;
import com.zhuaiwa.dd.cmd.Command;

@ColumnFamily("Message")
public class Message extends BaseObject {
	public static final String CN_MESSAGE = "Message";
	public static final String FN_MSGID = "msgid";
	public static final String FN_AGENT = "agent";
	public static final String FN_TIMESTAMP = "timestamp";
	public static final String FN_SHARE_TYPE = "share_type";
	public static final String FN_MSG_TYPE = "msg_type";
	public static final String FN_SENDER = "sender";
	public static final String FN_RECEIVERS = "receivers";
	public static final String FN_CONTENT = "content";
	public static final String FN_PARENT_ID = "parent_id";
	public static final String FN_ROOT_ID = "root_id";
	public static final String FN_TAGS = "tags";
	
	public static final String FN_MSG_STATE = "msg_state"; // 消息状态：0-正常（均可见），1-隐藏（本人可见），2-删除（都不可见）

	@Key
	@Column(name=FN_MSGID)
	private String msgid;
	
	@Column(name=FN_AGENT)
	private String agent;
	
	@Column(name=FN_TIMESTAMP)
	private Long timestamp;
	
	@Column(name=FN_MSG_TYPE)
	private Integer msgType;
	
	@Column(name=FN_SHARE_TYPE)
	private Integer shareType;
	
	@Column(name=FN_SENDER)
	private String sender;
	
	@Column(name=FN_RECEIVERS)
	private String receivers;
	
	@Column(name=FN_CONTENT)
	private byte[] content;
	
	@Column(name=FN_PARENT_ID)
	private String parentId;
	
	@Column(name=FN_ROOT_ID)
	private String rootId;
	
	@Column(name=FN_TAGS)
	private String tags;
	
    @Column(name=FN_MSG_STATE)
    private Integer msgState;
	
	public String getMsgid() {
		return msgid;
	}

	public void setMsgid(String msgid) {
		this.msgid = msgid;
	}

	public Long getTimestamp() {
		return timestamp;
	}
	
	public Date getTimestampDate() {
		if (timestamp == null)
			return null;
		return new Date(timestamp);
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

	public String getParentId() {
		return parentId;
	}

	public void setParentId(String parentId) {
		this.parentId = parentId;
	}

	public String getRootId() {
		return rootId;
	}

	public void setRootId(String rootId) {
		this.rootId = rootId;
	}

	public void setTags(String tags) {
		this.tags = tags;
	}

	public String getTags() {
		return tags;
	}

	public String getAgent() {
		return agent;
	}

	public void setAgent(String agent) {
		this.agent = agent;
	}

	public Integer getShareType() {
		return shareType;
	}

	public void setShareType(Integer shareType) {
		this.shareType = shareType;
	}
	
	public String getContentString() {
		if (content == null)
			return null;
		try {
			return new String(content, Command.encoding);
		} catch (Exception e) {
			e.printStackTrace();
			return null;
		}
	}

    public void setMsgState(Integer msgState) {
        this.msgState = msgState;
    }

    public Integer getMsgState() {
        return msgState;
    }
}
