package com.zhuaiwa.dd.domain;

import java.util.ArrayList;
import java.util.List;

import com.zhuaiwa.dd.annotation.ColumnFamily;
import com.zhuaiwa.dd.annotation.Key;
import com.zhuaiwa.dd.annotation.SuperColumns;

@ColumnFamily(value="Follower", isSuper=true)
public class Follower extends BaseObject {
	public static final String CN_FOLLOWER = "Follower";
	
	@Key
	private String userid;
	
//	@Columns(nameType=String.class, valueType=Long.class)
//	private Map<String, Long> followers = new LinkedHashMap<String, Long>();
	
	@SuperColumns(valueType=FollowInfo.class)
	private List<FollowInfo> followers = new ArrayList<FollowInfo>();

	public String getUserid() {
		return userid;
	}

	public void setUserid(String userid) {
		this.userid = userid;
	}

	public List<FollowInfo> getFollowers() {
		return followers;
	}

	public void setFollowers(List<FollowInfo> followers) {
		this.followers = followers;
	}

//	public Map<String, Long> getFollowers() {
//		return followers;
//	}
//
//	public void setFollowers(Map<String, Long> followers) {
//		this.followers = followers;
//	}
}
