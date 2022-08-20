package com.zhuaiwa.dd.domain;

import java.util.Date;

import com.zhuaiwa.dd.annotation.Column;
import com.zhuaiwa.dd.annotation.ColumnFamily;
import com.zhuaiwa.dd.annotation.Key;

@ColumnFamily("Profile")
public class Profile extends BaseObject
{
	public static final String CN_PROFILE = "Profile";

	public static final String FN_BIRTHDAY = "birthday";

	public static final String FN_CITY = "city";

	public static final String FN_COUNTRY = "country";

	public static final String FN_EDUCATION = "education";

	public static final String FN_FAV_TAGS = "fav_tags";

	public static final String FN_FOLLOWER_COUNT = "follower_count";

	public static final String FN_FOLLOWING_COUNT = "following_count";

	public static final String FN_GENDER = "gender";

	public static final String FN_ICON = "icon";

	public static final String FN_ICON_ADDRESS = "icon_address";

	public static final String FN_INTRODUCTION = "introduction";

	// public static final String FN_EMAIL = "email";
	// public static final String FN_PHONE_NUMBER = "phone_number";

	public static final String FN_NICKNAME = "nickname";

	public static final String FN_PROVINCE = "province";

	public static final String FN_WORK_EXPERIENCE = "work_experience";

	@Column(name = FN_BIRTHDAY)
	private Long birthday;

	@Column(name = FN_CITY)
	private String city;

	@Column(name = FN_COUNTRY)
	private String country;

	@Column(name = FN_EDUCATION)
	private String education;

	@Column(name = FN_FAV_TAGS)
	private String favTags;

	@Column(name = FN_FOLLOWER_COUNT)
	private Integer followerCount;

	@Column(name = FN_FOLLOWING_COUNT)
	private Integer followingCount;

	@Column(name = FN_GENDER)
	private Integer gender;

	@Column(name = FN_ICON)
	private byte[] icon;

	// @Column(name=FN_EMAIL)
	// private String email;
	//
	// @Column(name=FN_PHONE_NUMBER)
	// private String phoneNumber;

	@Column(name = FN_ICON_ADDRESS)
	private String iconAddress;

	@Column(name = FN_INTRODUCTION)
	private String introduction;

	@Column(name = FN_NICKNAME)
	private String nickname;

	@Column(name = FN_PROVINCE)
	private String province;

	@Key
	private String userid;

	@Column(name = FN_WORK_EXPERIENCE)
	private String work_experience;

	public Long getBirthday()
	{
		return birthday;
	}

	public Date getBirthdayDate()
	{
		if (birthday == null)
		{
			return null;
		}
		return new Date(birthday);
	}

	public String getCity()
	{
		return city;
	}

	public String getCountry()
	{
		return country;
	}

	public String getEducation()
	{
		return education;
	}

	public String getFavTags()
	{
		return favTags;
	}

	public Integer getFollowerCount()
	{
		return followerCount;
	}

	public Integer getFollowingCount()
	{
		return followingCount;
	}

	public Integer getGender()
	{
		return gender;
	}

	public byte[] getIcon()
	{
		return icon;
	}

	/**
	 * @return the iconAddress
	 */
	public String getIconAddress()
	{
		return iconAddress;
	}

	public String getIntroduction()
	{
		return introduction;
	}

	public String getNickname()
	{
		return nickname;
	}

	public String getProvince()
	{
		return province;
	}

	public String getUserid()
	{
		return userid;
	}

	public String getWork_experience()
	{
		return work_experience;
	}

	public void setBirthday(Long birthday)
	{
		this.birthday = birthday;
	}

	public void setCity(String city)
	{
		this.city = city;
	}

	public void setCountry(String country)
	{
		this.country = country;
	}

	public void setEducation(String education)
	{
		this.education = education;
	}

	public void setFavTags(String favTags)
	{
		this.favTags = favTags;
	}

	public void setFollowerCount(Integer followerCount)
	{
		this.followerCount = followerCount;
	}

	public void setFollowingCount(Integer followingCount)
	{
		this.followingCount = followingCount;
	}

	public void setGender(Integer gender)
	{
		this.gender = gender;
	}

	public void setIcon(byte[] icon)
	{
		this.icon = icon;
	}

	/**
	 * @param iconAddress
	 *            the iconAddress to set
	 */
	public void setIconAddress(String iconAddress)
	{
		this.iconAddress = iconAddress;
	}

	// public String getEmail() {
	// return email;
	// }
	//
	// public void setEmail(String email) {
	// this.email = email;
	// }
	//
	// public String getPhoneNumber() {
	// return phoneNumber;
	// }
	//
	// public void setPhoneNumber(String phoneNumber) {
	// this.phoneNumber = phoneNumber;
	// }

	public void setIntroduction(String introduction)
	{
		this.introduction = introduction;
	}

	public void setNickname(String nickname)
	{
		this.nickname = nickname;
	}

	public void setProvince(String province)
	{
		this.province = province;
	}

	public void setUserid(String userid)
	{
		this.userid = userid;
	}

	public void setWork_experience(String workExperience)
	{
		work_experience = workExperience;
	}
}
