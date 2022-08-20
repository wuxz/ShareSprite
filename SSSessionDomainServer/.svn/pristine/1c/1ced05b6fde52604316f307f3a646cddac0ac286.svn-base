package com.zhuaiwa.session.search.domain;

import java.io.Serializable;
import java.util.UUID;

import org.apache.solr.client.solrj.beans.Field;
import org.json.simple.JSONArray;
import org.json.simple.JSONObject;

public class Education extends SolrObj implements Serializable {

	public Education() {
		super(SolrObjType.EDUCATION.ordinal());
	}

	public Education(String userId) {
		super(SolrObjType.EDUCATION.ordinal());
		this.userId = userId;
	}

	public Education(String userId, JSONObject jo) {
		super(SolrObjType.EDUCATION.ordinal());// TODO userId复合
		this.userId = userId;
		if (jo.containsKey("school_name")) {
			this.school_name = (String) jo.get("school_name");
		}
		if (jo.containsKey("school_type")) {
			this.school_type = (String) jo.get("school_type");
		}
		if (jo.containsKey("enter_time")) {
			if (jo.get("enter_time") instanceof Double) {
				this.enter_time = ((Double) jo.get("enter_time")).longValue();
			} else {
				this.enter_time = (Long) jo.get("enter_time");
			}
		}
		if (jo.containsKey("department")) {
			this.department = (String) jo.get("department");
		}
		super.setId(userId + "_" + UUID.randomUUID().toString());
	}

	/**
	 * 
	 */
	private static final long serialVersionUID = -4364952369104816757L;

	@Field
	private String userId;
	@Field
	private String school_type;
	@Field
	private String school_name;
	@Field
	private long enter_time;
	@Field
	private String department;

	public String getSchool_type() {
		return school_type;
	}

	public void setSchool_type(String school_type) {
		this.school_type = school_type;
	}

	public String getSchool_name() {
		return school_name;
	}

	public void setSchool_name(String school_name) {
		this.school_name = school_name;
	}

	public long getEnter_time() {
		return enter_time;
	}

	public void setEnter_time(long enter_time) {
		this.enter_time = enter_time;
	}

	public String getDepartment() {
		return department;
	}

	public void setDepartment(String department) {
		this.department = department;
	}

	public String getUserId() {
		return userId;
	}

	public void setUserId(String userId) {
		this.userId = userId;
	}

	public JSONObject toJson() {
		JSONObject jo = new JSONObject();
		jo.put("school_type", school_type);
		jo.put("school_name", school_name);
		jo.put("enter_time", enter_time);
		jo.put("department", department);
		return jo;
	}

	@Override
	public String toString() {
		return "Education [userId=" + userId + ", school_type=" + school_type
				+ ", school_name=" + school_name + ", enter_time=" + enter_time
				+ ", department=" + department + "]";
	}

	public static void main(String[] args) {
		JSONArray list = new JSONArray();

		Education e1 = new Education("111");
		e1.setSchool_name("北京理工大学");
		e1.setSchool_type("大学");
		list.add(e1.toJson());
		Education e2 = new Education("111");
		e2.setSchool_name("南京气象学院");
		e2.setSchool_type("大学");
		list.add(e2.toJson());
		System.out.print(list.toJSONString());
		// String s = list.toJSONString();
		// User u = new User("123");
		// u.setFav_tags(s);
		// List<String> list1 = u.getFavorTags();
		// for(String l:list1){
		// System.out.println(l);
		// }
	}
}
