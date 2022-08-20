package com.zhuaiwa.dd.domain;

import java.util.Date;

import com.zhuaiwa.dd.annotation.Column;
import com.zhuaiwa.dd.annotation.ColumnFamily;
import com.zhuaiwa.dd.annotation.Key;

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
	public static final String FN_ROLE = "role";
	public static final String FN_IS_ACTIVATED = "is_activated";
	public static final String FN_FIRST_LOGIN = "first_login";
	public static final String FN_IS_EMAIL_HIDDEN = "is_email_hidden";
	public static final String FN_IS_PHONE_HIDDEN = "is_phone_hidden";
	public static final String FN_IS_EDUCATION_HIDDEN = "is_education_hidden";
	public static final String FN_IS_WORK_HIDDEN = "is_work_hidden";
	public static final String FN_IS_NICKNAME_HIDDEN = "is_nickname_hidden";
	public static final String FN_MESSAGE_FILTER = "message_filter";
	
	public static final String FN_SHARE_FORBIDDEN_TIME = "share_forbidden_time"; // 禁言时间
	
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
	private Integer isActivated; // 帐号是否已经激活
	
	@Column(name=FN_FIRST_LOGIN)
	private Integer firstLogin;
	
	@Column(name=FN_ROLE)
	private Integer role; // 用户角色
	
	@Column(name=FN_IS_EMAIL_HIDDEN)
	private Integer isEmailHidden;
	
	@Column(name=FN_IS_PHONE_HIDDEN)
	private Integer isPhoneHidden;
	
	@Column(name=FN_IS_EDUCATION_HIDDEN)
	private Integer isEducationHidden;
	
	@Column(name=FN_IS_WORK_HIDDEN)
	private Integer isWorkHidden;
	
	@Column(name=FN_IS_NICKNAME_HIDDEN)
	private Integer isNicknameHidden;
	
	@Column(name=FN_MESSAGE_FILTER)
	private Integer messageFilter;
	
    @Column(name=FN_SHARE_FORBIDDEN_TIME)
    private Long shareForbiddenTime;

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
	
	public Date getSecurityCodeTimeDate() {
		if (securityCodeTime == null)
			return null;
		return new Date(securityCodeTime);
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
	
	public Date getRegisterTimeDate() {
		if (registerTime == null)
			return null;
		return new Date(registerTime);
	}
	
	public void setIsActivated(Integer isActivated) {
		this.isActivated = isActivated;
	}

	public Integer getIsActivated() {
		return isActivated;
	}

	public void setRole(Integer role) {
		this.role = role;
	}

	public Integer getRole() {
		return role;
	}

	public Integer getFirstLogin() {
		return firstLogin;
	}

	public void setFirstLogin(Integer firstLogin) {
		this.firstLogin = firstLogin;
	}

	public Integer getIsEmailHidden() {
		return isEmailHidden;
	}

	public void setIsEmailHidden(Integer isEmailHidden) {
		this.isEmailHidden = isEmailHidden;
	}

	public Integer getIsPhoneHidden() {
		return isPhoneHidden;
	}

	public void setIsPhoneHidden(Integer isPhoneHidden) {
		this.isPhoneHidden = isPhoneHidden;
	}

	public Integer getIsEducationHidden() {
		return isEducationHidden;
	}

	public void setIsEducationHidden(Integer isEducationHidden) {
		this.isEducationHidden = isEducationHidden;
	}

	public Integer getIsWorkHidden() {
		return isWorkHidden;
	}

	public void setIsWorkHidden(Integer isWorkHidden) {
		this.isWorkHidden = isWorkHidden;
	}

	public void setIsNicknameHidden(Integer isNicknameHidden) {
		this.isNicknameHidden = isNicknameHidden;
	}

	public Integer getIsNicknameHidden() {
		return isNicknameHidden;
	}

	public void setMessageFilter(Integer messageFilter) {
		this.messageFilter = messageFilter;
	}

	public Integer getMessageFilter() {
		return messageFilter;
	}

    public void setShareForbiddenTime(Long shareForbiddenTime) {
        this.shareForbiddenTime = shareForbiddenTime;
    }

    public Long getShareForbiddenTime() {
        return shareForbiddenTime;
    }
    
    public Date getShareForbiddenTimeDate() {
        if (shareForbiddenTime == null)
            return null;
        return new Date(shareForbiddenTime);
    }
}
