package com.zhuaiwa.dd.domain;

import java.util.ArrayList;
import java.util.List;

import com.zhuaiwa.dd.annotation.ColumnFamily;
import com.zhuaiwa.dd.annotation.Key;
import com.zhuaiwa.dd.annotation.SuperColumns;

@ColumnFamily(value="Inviter", isSuper=true)
public class Inviter extends BaseObject {
	public static final String CN_INVITER = "Inviter";
	
	@Key
	private String userid;
	
//	@Columns(nameType=String.class, valueType=Long.class)
//	private Map<String, Long> inviters = new LinkedHashMap<String, Long>();
	@SuperColumns(valueType=InviteInfo.class)
	private List<InviteInfo> inviters = new ArrayList<InviteInfo>();
	
	public List<InviteInfo> getInviters() {
		return inviters;
	}

	public void setInviters(List<InviteInfo> inviters) {
		this.inviters = inviters;
	}

	public String getUserid() {
		return userid;
	}

	public void setUserid(String userid) {
		this.userid = userid;
	}

//	public Map<String, Long> getInviters() {
//		return inviters;
//	}
//
//	public void setInviters(Map<String, Long> inviters) {
//		this.inviters = inviters;
//	}
}
