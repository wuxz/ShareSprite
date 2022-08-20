package com.channelsoft.zhuaiwa.dal.domain;

import com.channelsoft.zhuaiwa.dal.annotation.Column;
import com.channelsoft.zhuaiwa.dal.annotation.ColumnFamily;
import com.channelsoft.zhuaiwa.dal.annotation.Key;

@ColumnFamily("Account")
public class Account extends BaseObject {
	public static final String CN_ACCOUNT = "Account";
	public static final String FN_USERID = "userid";
	public static final String FN_PASSWORD = "password";
	public static final String FN_EMAIL = "email";
	public static final String FN_PHONE_NUMBER = "phone_number";
	public static final String FN_SECURITY_CODE = "security_code";
	public static final String FN_SECURITY_CODE_TIME = "security_code_time";
	public static final String FN_REGISTER_TIME = "register_time";
	public static final String FN_IS_ACTIVATED = "is_activated";
	public static final String FN_ROLE = "role";
	
	@Key
	@Column(name=FN_USERID)
	private String userid;
	
	@Column(name=FN_PASSWORD)
	private String password;
	
	@Column(name=FN_EMAIL)
	private String email;
	
	@Column(name=FN_PHONE_NUMBER)
	private String phoneNumber;
	
	@Column(name=FN_SECURITY_CODE)
	private String securityCode;
	
	@Column(name=FN_SECURITY_CODE_TIME)
	private Long securityCodeTime;
	
	@Column(name=FN_REGISTER_TIME)
	private Long registerTime;
	
	@Column(name=FN_IS_ACTIVATED)
	private Long isActivated; // 帐号是否已经激活
	
	@Column(name=FN_ROLE)
	private Long role; // 用户角色
	
	public String getUserid() {
		return userid;
	}

	public void setUserid(String userid) {
		this.userid = userid;
	}

	public String getPassword() {
		return password;
	}

	public void setPassword(String password) {
		this.password = password;
	}

	public String getEmail() {
		return email;
	}

	public void setEmail(String email) {
		this.email = email;
	}

	public String getSecurityCode() {
		return securityCode;
	}

	public void setSecurityCode(String securityCode) {
		this.securityCode = securityCode;
	}

	public Long getSecurityCodeTime() {
		return securityCodeTime;
	}

	public void setSecurityCodeTime(Long securityCodeTime) {
		this.securityCodeTime = securityCodeTime;
	}

	public String getPhoneNumber() {
		return phoneNumber;
	}

	public void setPhoneNumber(String phoneNumber) {
		this.phoneNumber = phoneNumber;
	}

	public void setRegisterTime(Long registerTime) {
		this.registerTime = registerTime;
	}

	public Long getRegisterTime() {
		return registerTime;
	}

	public void setIsActivated(Long isActivated) {
		this.isActivated = isActivated;
	}

	public Long getIsActivated() {
		return isActivated;
	}

	public void setRole(Long role) {
		this.role = role;
	}

	public Long getRole() {
		return role;
	}
}
