package com.zhuaiwa.dd.domain;

import com.zhuaiwa.dd.annotation.Column;
import com.zhuaiwa.dd.annotation.Key;

public class FollowInfo extends BaseObject {

	public static final String FN_USERID = "userid";
	public static final String FN_TIMESTAMP = "timestamp";
	
	@Key
	@Column(name=FN_USERID)
	private String userid;
	
	@Column(name=FN_TIMESTAMP)
	private Long timestamp;

	public String getUserid() {
		return userid;
	}

	public void setUserid(String userid) {
		this.userid = userid;
	}

	public void setTimestamp(Long timestamp) {
		this.timestamp = timestamp;
	}

	public Long getTimestamp() {
		return timestamp;
	}

}
