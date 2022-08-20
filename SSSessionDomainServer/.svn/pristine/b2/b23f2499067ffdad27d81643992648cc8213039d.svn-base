package com.channelsoft.zhuaiwa.dal.domain;

import com.channelsoft.zhuaiwa.dal.annotation.Column;
import com.channelsoft.zhuaiwa.dal.annotation.ColumnFamily;
import com.channelsoft.zhuaiwa.dal.annotation.Key;

@ColumnFamily("Profile")
public class Profile extends BaseObject {
	public static final String CN_PROFILE = "Profile";
	public static final String FN_USERID = "userid";
	public static final String FN_NICKNAME = "nickname";
	public static final String FN_BIRTHDAY = "birthday";
	public static final String FN_GENDER = "gender";
	public static final String FN_ICON = "icon";
//	public static final String FN_FOLLOWER_COUNT = "follower_count";
//	public static final String FN_FOLLOWING_COUNT = "following_count";
	public static final String FN_INTRODUCTION = "introduction";
//	public static final String FN_EMAIL = "email";
//	public static final String FN_PHONE_NUMBER = "phone_number";
	public static final String FN_INTERESTS = "interests";
	
	@Key
	@Column(name=FN_USERID)
	private String userid;
	
	@Column(name=FN_NICKNAME)
	private String nickname;
	
	@Column(name=FN_BIRTHDAY)
	private Long birthday;
	
	@Column(name=FN_GENDER)
	private Integer gender;
	
	@Column(name=FN_ICON)
	private byte[] icon;
	
//	@Column(name=FN_FOLLOWER_COUNT)
//	private Integer followercount;
//	
//	@Column(name=FN_FOLLOWING_COUNT)
//	private Integer followingcount;
	
	@Column(name=FN_INTRODUCTION)
	private String introduction;
	
//	@Column(name=FN_EMAIL)
//	private String email;
//	
//	@Column(name=FN_PHONE_NUMBER)
//	private String phoneNumber;
	
	@Column(name=FN_INTERESTS)
	private String interests;

	public String getUserid() {
		return userid;
	}

	public void setUserid(String userid) {
		this.userid = userid;
	}

	public String getNickname() {
		return nickname;
	}

	public void setNickname(String nickname) {
		this.nickname = nickname;
	}

	public Integer getGender() {
		return gender;
	}

	public void setGender(Integer gender) {
		this.gender = gender;
	}

	public byte[] getIcon() {
		return icon;
	}

	public void setIcon(byte[] icon) {
		this.icon = icon;
	}

//	public void setFollowercount(Integer followercount) {
//		this.followercount = followercount;
//	}
//
//	public Integer getFollowercount() {
//		return followercount;
//	}

	public void setIntroduction(String introduction) {
		this.introduction = introduction;
	}

	public String getIntroduction() {
		return introduction;
	}

//	public void setFollowingcount(Integer followingcount) {
//		this.followingcount = followingcount;
//	}
//
//	public Integer getFollowingcount() {
//		return followingcount;
//	}

	public void setBirthday(Long birthday) {
		this.birthday = birthday;
	}

	public Long getBirthday() {
		return birthday;
	}

	public void setInterests(String interests) {
		this.interests = interests;
	}

	public String getInterests() {
		return interests;
	}

//	public String getEmail() {
//		return email;
//	}
//
//	public void setEmail(String email) {
//		this.email = email;
//	}
//
//	public String getPhoneNumber() {
//		return phoneNumber;
//	}
//
//	public void setPhoneNumber(String phoneNumber) {
//		this.phoneNumber = phoneNumber;
//	}
}
