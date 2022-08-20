package com.channelsoft.zhuaiwa.dal.domain;

import java.util.ArrayList;
import java.util.List;

import com.channelsoft.zhuaiwa.dal.annotation.Column;
import com.channelsoft.zhuaiwa.dal.annotation.ColumnFamily;
import com.channelsoft.zhuaiwa.dal.annotation.Key;
import com.channelsoft.zhuaiwa.dal.annotation.SuperColumns;

@ColumnFamily("Following")
public class Following extends BaseObject {
	public static final String CN_FOLLOWING = "Following";
	
	@Key
	private String userid;
	
//	@Columns(nameType=String.class, valueType=Long.class)
//	private Map<String, Long> followings = new LinkedHashMap<String, Long>();
	@SuperColumns(valueType=FollowingInfo.class)
	private List<FollowingInfo> followings = new ArrayList<FollowingInfo>();

	public String getUserid() {
		return userid;
	}

	public void setUserid(String userid) {
		this.userid = userid;
	}

	public List<FollowingInfo> getFollowings() {
		return followings;
	}

	public void setFollowings(List<FollowingInfo> followings) {
		this.followings = followings;
	}
	
	public static class FollowingInfo {
		public static final String FN_FOLLOWINGID = "followingid";
		
		@Key
		@Column(name=FN_FOLLOWINGID)
		private String followingid;

		public void setFollowingid(String followingid) {
			this.followingid = followingid;
		}

		public String getFollowingid() {
			return followingid;
		}
	}
}
