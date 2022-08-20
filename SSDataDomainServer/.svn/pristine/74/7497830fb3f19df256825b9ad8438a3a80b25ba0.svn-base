package com.zhuaiwa.dd.domain;

import com.zhuaiwa.dd.annotation.Column;
import com.zhuaiwa.dd.annotation.ColumnFamily;
import com.zhuaiwa.dd.annotation.Key;

@ColumnFamily("PhoneAccount")
public class PhoneAccount extends BaseObject {
	public static final String CN_PHONE_ACCOUNT = "PhoneAccount";
	public static final String FN_USERID = "userid";
	public static final String FN_PHONE_NUMBER = "phone_number";
//	public static final String FN_PASSWORD = "password";

	@Column(name=FN_USERID)
	private String userid;

	@Key
	@Column(name=FN_PHONE_NUMBER)
	private String phoneNumber;
	
//	@Column(name=FN_PASSWORD)
//	private String password;

	public String getUserid() {
		return userid;
	}

	public void setUserid(String userid) {
		this.userid = userid;
	}

	public String getPhoneNumber() {
		return phoneNumber;
	}

	public void setPhoneNumber(String phoneNumber) {
		this.phoneNumber = phoneNumber;
	}

//	public String getPassword() {
//		return password;
//	}
//
//	public void setPassword(String password) {
//		this.password = password;
//	}

}
