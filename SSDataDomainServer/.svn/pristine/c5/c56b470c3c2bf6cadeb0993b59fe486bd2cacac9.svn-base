package com.zhuaiwa.dd.domain;

import java.util.ArrayList;
import java.util.List;

import com.zhuaiwa.dd.annotation.ColumnFamily;
import com.zhuaiwa.dd.annotation.Key;
import com.zhuaiwa.dd.annotation.SuperColumns;

@ColumnFamily(value="Inviting", isSuper=true)
public class Inviting extends BaseObject {
	public static final String CN_INVITING = "Inviting";
	
	@Key
	private String userid;
	
//	@Columns(nameType=String.class, valueType=Long.class)
//	private Map<String, Long> invitings = new LinkedHashMap<String, Long>();
	
	@SuperColumns(valueType=InviteInfo.class)
	private List<InviteInfo> invitings = new ArrayList<InviteInfo>();

	public String getUserid() {
		return userid;
	}

	public void setUserid(String userid) {
		this.userid = userid;
	}

	public List<InviteInfo> getInvitings() {
		return invitings;
	}

	public void setInvitings(List<InviteInfo> invitings) {
		this.invitings = invitings;
	}

//	public Map<String, Long> getInvitings() {
//		return invitings;
//	}
//
//	public void setInvitings(Map<String, Long> invitings) {
//		this.invitings = invitings;
//	}
}
