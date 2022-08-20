package com.zhuaiwa.session.search.domain;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.Date;
import java.util.Iterator;
import java.util.List;

import org.apache.solr.client.solrj.beans.Field;
import org.json.simple.JSONArray;
import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;
import org.json.simple.parser.ParseException;

import com.zhuaiwa.api.Common.SSAccount;
import com.zhuaiwa.api.Common.SSId;
import com.zhuaiwa.api.Common.SSProfile;
import com.zhuaiwa.api.util.SSIdUtils;
import com.zhuaiwa.api.util.SSIdUtils.SSIdDomain;

public class User extends SolrObj implements Serializable {

	private static final long serialVersionUID = 2360086250851413462L;

	public static void main(String[] args) {
		String jsonText = "[{\"school_type\":\"大学\",\"school_name\":\"北京理工大学\",\"department\":null,\"enter_time\":0},{\"school_type\":\"大学\",\"school_name\":\"南京气象学院\",\"department\":null,\"enter_time\":0}]";
		User u = new User();
		u.setEducations(jsonText);
		List<Education> list = u.getEducations();
		for (Education edu : list) {
			System.out.println(edu.toString());
		}
	}

	@Field
	private long birthday;

	@Field
	private String city;

	@Field
	private String country;

	private List<Education> educations;// 单独索引

	@Field
	private String email;

	private String fav_tags;// json格式

	@Field
	private List<String> favorTags;

	@Field
	private int gender;

	@Field
	private String introduction;

	@Field
	private String nickname;

	@Field
	private String phoneNumber;

	@Field
	private String province;

	private List<WorkExpirence> workExpirences;// 单独索引
	
	@Field
	private Date created;

	public User() {
		super(SolrObjType.USER.ordinal());
	}

	public User(String userid) {
		super(SolrObjType.USER.ordinal());
		super.setId(userid);
	}

	public void from(SSAccount ssa, SSProfile ssp) {
		if (ssa == null) {
			return;
		}
		super.setId(ssa.getUserid());
		if (ssp != null) {
			if (!ssa.hasIsNicknameHidden() || (ssa.getIsNicknameHidden() != 1)) {
				this.setNickname(ssp.getNickname());
			}
			this.setIntroduction(ssp.getIntroduction());
			this.setBirthday(ssp.getBirthday());
			this.setCity(ssp.getCity());
			this.setCountry(ssp.getCountry());
			this.setFavorTags(ssp.getFavTagsList());
			this.setGender(ssp.getGender().ordinal());
			this.setProvince(ssp.getProvince());
			this.setCreated(new Date(ssa.getRegisterTime()));
			if (!ssa.hasIsEducationHidden()
					|| (ssa.getIsEducationHidden() != 1)) {
				this.setEducations(ssp.getEducation());
			}
			if (!ssa.hasIsWorkHidden() || (ssa.getIsWorkHidden() != 1)) {
				this.setWorkExpirences(ssp.getWorkExperience());
			}
		}
		List<SSId> ssid_list = ssa.getAliasIdListList();
		if (!ssa.hasIsEmailHidden() || (ssa.getIsEmailHidden() != 1)) {
			String email = SSIdUtils.getId(ssid_list, SSIdDomain.email);
			if (email != null) {
				this.setEmail(email);
			}
		}
		if (!ssa.hasIsPhoneHidden() || (ssa.getIsPhoneHidden() != 1)) {
			String phoneNumber = SSIdUtils.getId(ssid_list, SSIdDomain.phone);
			if (phoneNumber != null) {
				this.setPhoneNumber(phoneNumber);
			}
		}
	}

	public long getBirthday() {
		return birthday;
	}

	public String getCity() {
		return city;
	}

	public String getCountry() {
		return country;
	}

	public List<Education> getEducations() {
		return educations;
	}

	public String getEmail() {
		return email;
	}

	public String getFav_tags() {
		return fav_tags;
	}

	public List<String> getFavorTags() {
		return favorTags;
	}

	public int getGender() {
		return gender;
	}

	public String getIntroduction() {
		return introduction;
	}

	public String getNickname() {
		return nickname;
	}

	public String getPhoneNumber() {
		return phoneNumber;
	}

	public String getProvince() {
		return province;
	}

	public List<WorkExpirence> getWorkExpirences() {
		return workExpirences;
	}

	public void setBirthday(long birthday) {
		this.birthday = birthday;
	}

	public void setCity(String city) {
		this.city = city;
	}

	public void setCountry(String country) {
		this.country = country;
	}

	public void setEducations(List<Education> educations) {
		this.educations = educations;
	}

	public void setEducations(String jsonText) {
		if ((jsonText == null) || jsonText.isEmpty()) {
			return;
		}
		if (this.educations == null) {
			educations = new ArrayList<Education>();
		}
		JSONParser parser = new JSONParser();
		try {
			Object obj = parser.parse(jsonText);
			JSONArray array = (JSONArray) obj;
			if (array != null) {
				Iterator<JSONObject> iter = array.iterator();
				while (iter.hasNext()) {
					JSONObject jsonObj = iter.next();
					educations.add(new Education(this.getId(), jsonObj));
				}
			}
		} catch (Exception e) {
			e.printStackTrace();
			System.out.println("json::" + jsonText);
		}

	}

	public void setEmail(String email) {
		this.email = email;
	}

	public void setFav_tags(String fav_tags) {
		this.fav_tags = fav_tags;
		if ((fav_tags != null) && (fav_tags.length() > 0)) {
			JSONParser parser = new JSONParser();
			try {
				Object obj = parser.parse(fav_tags);
				JSONArray array = (JSONArray) obj;
				if (array != null) {
					favorTags = array.subList(0, array.size());
				}
			} catch (ParseException e) {
				e.printStackTrace();
			}
		}
	}

	public void setFavorTags(List<String> favorTags) {
		this.favorTags = favorTags;
	}

	public void setGender(int gender) {
		this.gender = gender;
	}

	public void setIntroduction(String introduction) {
		this.introduction = introduction;
	}

	public void setNickname(String nickname) {
		this.nickname = nickname;
	}

	public void setPhoneNumber(String phoneNumber) {
		this.phoneNumber = phoneNumber;
	}

	public void setProvince(String province) {
		this.province = province;
	}

	public void setWorkExpirences(List<WorkExpirence> workExpirences) {
		this.workExpirences = workExpirences;
	}

	public void setWorkExpirences(String jsonText) {
		// TODO parse json text
		if ((jsonText == null) || jsonText.isEmpty()) {
			return;
		}
		if (this.workExpirences == null) {
			workExpirences = new ArrayList<WorkExpirence>();
		}
		JSONParser parser = new JSONParser();
		try {
			Object obj = parser.parse(jsonText);
			JSONArray array = (JSONArray) obj;
			if (array != null) {
				Iterator<JSONObject> iter = array.iterator();
				while (iter.hasNext()) {
					JSONObject jsonObj = iter.next();
					workExpirences
							.add(new WorkExpirence(this.getId(), jsonObj));
				}
			}
		} catch (ParseException e) {
			e.printStackTrace();
		}
	}

	public Date getCreated() {
		return created;
	}

	public void setCreated(Date created) {
		this.created = created;
	}

}
