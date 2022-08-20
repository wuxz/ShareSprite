package com.zhuaiwa.dd.domain;

import com.zhuaiwa.dd.annotation.Column;
import com.zhuaiwa.dd.annotation.ColumnFamily;
import com.zhuaiwa.dd.annotation.Key;

@ColumnFamily("NameAccount")
public class NameAccount extends BaseObject {
	public static final String CN_NAME_ACCOUNT = "NameAccount";
	public static final String FN_USERID = "userid";
	public static final String FN_NAME = "name";

	@Column(name=FN_USERID)
	private String userid;

	@Key
	@Column(name=FN_NAME)
	private String name;

	public String getUserid() {
		return userid;
	}

	public void setUserid(String userid) {
		this.userid = userid;
	}

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}
}
