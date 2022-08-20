package com.channelsoft.zhuaiwa.dal.domain;

import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

import com.channelsoft.zhuaiwa.dal.annotation.ColumnFamily;
import com.channelsoft.zhuaiwa.dal.annotation.Columns;
import com.channelsoft.zhuaiwa.dal.annotation.Key;
import com.channelsoft.zhuaiwa.dal.annotation.SuperColumns;

@ColumnFamily("Member")
public class Member extends BaseObject {
	public static final String CN_MEMBER = "Member";
	
	@Key
	private String userid;
	
	@SuperColumns(valueType=MemberInfo.class)
	private List<MemberInfo> members = new ArrayList<MemberInfo>();
	
	public String getUserid() {
		return userid;
	}

	public void setUserid(String userid) {
		this.userid = userid;
	}

	public List<MemberInfo> getMembers() {
		return members;
	}

	public void setMembers(List<MemberInfo> members) {
		this.members = members;
	}

	public static class MemberInfo {
		@Key
		private String groupid;
		
		@Columns(nameType=String.class, valueType=Long.class)
		private Map<String, Long> contacts = new LinkedHashMap<String, Long>();

		public Map<String, Long> getContacts() {
			return contacts;
		}

		public void setContacts(Map<String, Long> contacts) {
			this.contacts = contacts;
		}

		public void setGroupid(String groupid) {
			this.groupid = groupid;
		}

		public String getGroupid() {
			return groupid;
		}
	}
}
