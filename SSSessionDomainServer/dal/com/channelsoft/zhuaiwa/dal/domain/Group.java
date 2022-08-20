package com.channelsoft.zhuaiwa.dal.domain;

import java.util.ArrayList;
import java.util.List;

import com.channelsoft.zhuaiwa.dal.annotation.Column;
import com.channelsoft.zhuaiwa.dal.annotation.ColumnFamily;
import com.channelsoft.zhuaiwa.dal.annotation.Key;
import com.channelsoft.zhuaiwa.dal.annotation.SuperColumns;

@ColumnFamily("Group")
public class Group extends BaseObject {
	public static final String CN_GROUP = "Group";
	
	@Key
	private String userid;
	
	@SuperColumns(valueType=GroupInfo.class)
	private List<GroupInfo> groups = new ArrayList<GroupInfo>();
	
	public String getUserid() {
		return userid;
	}

	public void setUserid(String userid) {
		this.userid = userid;
	}

	public List<GroupInfo> getGroups() {
		return groups;
	}

	public void setGroups(List<GroupInfo> groups) {
		this.groups = groups;
	}

	public static class GroupInfo {
		public static final String FN_GROUPID = "groupid";
		public static final String FN_NAME = "name";
		public static final String FN_TYPE = "type";

		@Key
		@Column(name=FN_GROUPID)
		private String groupid;
		
		@Column(name=FN_NAME)
		private String name;
		
		@Column(name=FN_TYPE)
		private Integer type;
		
		public String getName() {
			return name;
		}

		public void setName(String name) {
			this.name = name;
		}

		public void setGroupid(String groupid) {
			this.groupid = groupid;
		}

		public String getGroupid() {
			return groupid;
		}

		public void setType(Integer type) {
			this.type = type;
		}

		public Integer getType() {
			return type;
		}
	}
}
