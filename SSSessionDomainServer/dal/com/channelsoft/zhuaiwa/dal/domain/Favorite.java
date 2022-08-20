package com.channelsoft.zhuaiwa.dal.domain;

import java.util.ArrayList;
import java.util.List;

import com.channelsoft.zhuaiwa.dal.annotation.ColumnFamily;
import com.channelsoft.zhuaiwa.dal.annotation.Key;
import com.channelsoft.zhuaiwa.dal.annotation.SuperColumns;

@ColumnFamily("Favorite")
public class Favorite extends BaseObject {
	public static final String CN_FAVORITE = "Favorite";
	
	@Key
	private String userid;
	
//	@Columns(nameType=String.class, valueType=Long.class,
//			maxName={00,00,00,00,
//					 00,00,00,00,
//					 00,00,00,00,
//					 00,00,00,00,
//					 00,00,00,00,
//					 00,00,00,00},
//			minName={(byte)0xFF,(byte)0xFF,(byte)0xFF,(byte)0xFF,
//					 (byte)0xFF,(byte)0xFF,(byte)0xFF,(byte)0xFF,
//					 (byte)0xFF,(byte)0xFF,(byte)0xFF,(byte)0xFF,
//					 (byte)0xFF,(byte)0xFF,(byte)0xFF,(byte)0xFF,
//					 (byte)0xFF,(byte)0xFF,(byte)0xFF,(byte)0xFF,
//					 (byte)0xFF,(byte)0xFF,(byte)0xFF,(byte)0xFF})
//	private Map<String, Long> messages = new LinkedHashMap<String, Long>();
	@SuperColumns(valueType=MessageInfo.class)
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
