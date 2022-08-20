package com.channelsoft.zhuaiwa.dal.domain;

import java.util.ArrayList;
import java.util.List;

import com.channelsoft.zhuaiwa.dal.annotation.Column;
import com.channelsoft.zhuaiwa.dal.annotation.ColumnFamily;
import com.channelsoft.zhuaiwa.dal.annotation.Key;
import com.channelsoft.zhuaiwa.dal.annotation.SuperColumns;

@ColumnFamily("Follower")
public class Follower extends BaseObject {
	public static final String CN_FOLLOWER = "Follower";
	
	@Key
	private String userid;
	
//	@Columns(nameType=String.class, valueType=Long.class)
//	private Map<String, Long> followers = new LinkedHashMap<String, Long>();
	@SuperColumns(valueType=FollowerInfo.class)
	private List<FollowerInfo> followers = new ArrayList<FollowerInfo>();

	public String getUserid() {
		return userid;
	}

	public void setUserid(String userid) {
		this.userid = userid;
	}

	public List<FollowerInfo> getFollowers() {
		return followers;
	}

	public void setFollowers(List<FollowerInfo> followers) {
		this.followers = followers;
	}
	
	public static class FollowerInfo {
		public static final String FN_FOLLOWERID = "followerid";
		
		@Key
		@Column(name=FN_FOLLOWERID)
		private String followerid;

		public void setFollowerid(String followerid) {
			this.followerid = followerid;
		}

		public String getFollowerid() {
			return followerid;
		}
	}
}
