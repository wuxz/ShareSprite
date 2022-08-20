package com.zhuaiwa.dd.domain;

import java.util.ArrayList;
import java.util.List;

import com.zhuaiwa.dd.annotation.ColumnFamily;
import com.zhuaiwa.dd.annotation.Key;
import com.zhuaiwa.dd.annotation.SuperColumns;

@ColumnFamily(value="Comment", isSuper=true)
public class Comment extends BaseObject {
    public static final String CN_COMMENT = "Comment";

    @Key
    private String msgid;

    @SuperColumns(valueType=MessageInfo.class, descending=true)
    private List<MessageInfo> messages = new ArrayList<MessageInfo>();

    public String getMsgid() {
        return msgid;
    }

    public void setMsgid(String msgid) {
        this.msgid = msgid;
    }

    public List<MessageInfo> getMessages() {
        return messages;
    }

    public void setMessages(List<MessageInfo> messages) {
        this.messages = messages;
    }
}
