package com.channelsoft.zhuaiwa.dal.domain;

import com.channelsoft.zhuaiwa.dal.annotation.Column;
import com.channelsoft.zhuaiwa.dal.annotation.ColumnFamily;
import com.channelsoft.zhuaiwa.dal.annotation.Key;

@ColumnFamily("EmailAccount")
public class EmailAccount extends BaseObject {
	public static final String CN_EMAIL_ACCOUNT = "EmailAccount";
	public static final String FN_USERID = "userid";
	public static final String FN_EMAIL = "email";
	public static final String FN_PASSWORD = "password";

	@Column(name=FN_USERID)
	private String userid;

	@Key
	@Column(name=FN_EMAIL)
	private String email;
	
	@Column(name=FN_PASSWORD)
	private String password;

	public String getUserid() {
		return userid;
	}

	public void setUserid(String userid) {
		this.userid = userid;
	}

	public String getEmail() {
		return email;
	}

	public void setEmail(String email) {
		this.email = email;
	}

	public String getPassword() {
		return password;
	}

	public void setPassword(String password) {
		this.password = password;
	}
}
