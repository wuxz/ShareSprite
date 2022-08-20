package com.zhuaiwa.dd.domain;

import java.util.ArrayList;
import java.util.List;

import com.zhuaiwa.dd.annotation.ColumnFamily;
import com.zhuaiwa.dd.annotation.Key;
import com.zhuaiwa.dd.annotation.SuperColumns;

@ColumnFamily(value="Following", isSuper=true)
public class Following extends BaseObject {
	public static final String CN_FOLLOWING = "Following";
	
	@Key
	private String userid;
	
	@SuperColumns(valueType=FollowInfo.class)
	private List<FollowInfo> followings = new ArrayList<FollowInfo>();

	public String getUserid() {
		return userid;
	}

	public void setUserid(String userid) {
		this.userid = userid;
	}

	public List<FollowInfo> getFollowings() {
		return followings;
	}

	public void setFollowings(List<FollowInfo> followings) {
		this.followings = followings;
	}
}
