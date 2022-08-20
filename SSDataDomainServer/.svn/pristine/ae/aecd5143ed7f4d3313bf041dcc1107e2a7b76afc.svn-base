package com.zhuaiwa.session.search.domain;

import java.io.Serializable;
import java.util.List;
import java.util.UUID;

import org.apache.solr.client.solrj.beans.Field;
import org.json.simple.JSONArray;
import org.json.simple.JSONObject;

public class WorkExpirence extends SolrObj implements Serializable {

	public WorkExpirence() {
		super(SolrObjType.WORK.ordinal());
	}

	public WorkExpirence(String userId) {
		super(SolrObjType.WORK.ordinal());
		this.userId = userId;
	}

	public WorkExpirence(String userId, JSONObject jo) {
		super(SolrObjType.WORK.ordinal());
		this.userId = userId;
		if (jo.containsKey("company")) {
			this.company = (String) jo.get("company");
		}
		if (jo.containsKey("start_time")) {
			if (jo.get("start_time") instanceof Double) {
				this.start_time = ((Double) jo.get("start_time")).longValue();
			} else {
				this.start_time = (Long) jo.get("start_time");
			}
		}
		if (jo.containsKey("end_time")) {
			if (jo.get("end_time") instanceof Double) {
				this.end_time = ((Double) jo.get("end_time")).longValue();
			} else {
				this.end_time = (Long) jo.get("end_time");
			}
		}
		if (jo.containsKey("vocation")) {
			this.vocation = (String) jo.get("vocation");
		}
		if (jo.containsKey("major")) {
			this.major = (String) jo.get("major");
		}

		if (jo.containsKey("position")) {
			this.position = (String) jo.get("position");
		}

		if (jo.containsKey("department")) {
			this.department = (String) jo.get("department");
		}
		if (jo.containsKey("province")) {
			this.province = (String) jo.get("province");
		}
		if (jo.containsKey("city")) {
			this.city = (String) jo.get("city");
		}
		super.setId(userId + "_" + UUID.randomUUID().toString());
	}

	public JSONObject toJson() {
		JSONObject jo = new JSONObject();
		jo.put("company", company);
		jo.put("start_time", start_time);
		jo.put("end_time", end_time);
		jo.put("vocation", vocation);

		jo.put("major", major);
		jo.put("position", position);
		jo.put("department", department);
		jo.put("province", province);
		jo.put("city", city);
		return jo;
	}

	@Override
	public String toString() {
		return "WorkExpirence [userId=" + userId + ", company=" + company
				+ ", start_time=" + start_time + ", end_time=" + end_time
				+ ", vocation=" + vocation + ", major=" + major + ", position="
				+ position + ", department=" + department + ", province="
				+ province + ", city=" + city + "]";
	}

	/**
	 * 
	 */
	private static final long serialVersionUID = -4364952369104816757L;

	@Field
	private String userId;
	@Field
	private String company;
	@Field
	private long start_time;
	@Field
	private long end_time;
	@Field
	private String vocation;
	@Field
	private String major;
	@Field
	private String position;
	@Field
	private String department;
	@Field
	private String province;
	@Field
	private String city;

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

	public String getCompany() {
		return company;
	}

	public void setCompany(String company) {
		this.company = company;
	}

	public long getStart_time() {
		return start_time;
	}

	public void setStart_time(long start_time) {
		this.start_time = start_time;
	}

	public long getEnd_time() {
		return end_time;
	}

	public void setEnd_time(long end_time) {
		this.end_time = end_time;
	}

	public String getVocation() {
		return vocation;
	}

	public void setVocation(String vocation) {
		this.vocation = vocation;
	}

	public String getMajor() {
		return major;
	}

	public void setMajor(String major) {
		this.major = major;
	}

	public String getPosition() {
		return position;
	}

	public void setPosition(String position) {
		this.position = position;
	}

	public String getProvince() {
		return province;
	}

	public void setProvince(String province) {
		this.province = province;
	}

	public String getCity() {
		return city;
	}

	public void setCity(String city) {
		this.city = city;
	}

	public static void main(String[] args) {
		JSONArray list = new JSONArray();
		WorkExpirence we = new WorkExpirence();
		we.setCity("北京");
		we.setCompany("青牛（北京）技术有限公司");
		we.setDepartment("移动互联网");
		we.setMajor("计算机");
		we.setPosition("软件工程师");
		we.setStart_time(1143881974608L);
		we.setEnd_time(1175418013166L);
		we.setProvince("北京");
		we.setVocation("IT");
		list.add(we.toJson());

		WorkExpirence we1 = new WorkExpirence();
		we1.setCity("北京");
		we1.setCompany("北京青牛科技有限公司");
		we1.setDepartment("移动互联网");
		we1.setMajor("计算机");
		we1.setPosition("软件工程师");
		we1.setStart_time(1207040642358L);
		we1.setEnd_time(1238576661903L);
		we1.setProvince("北京");
		we1.setVocation("IT");
		list.add(we1.toJson());

		String jsonText = list.toJSONString();
		User user = new User();
		user.setWorkExpirences(jsonText);
		List<WorkExpirence> tmp = user.getWorkExpirences();
		for (WorkExpirence t : tmp) {
			System.out.println(t.toString());
		}
	}
}
