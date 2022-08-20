package com.channelsoft.zhuaiwa.dal.domain;

import java.util.ArrayList;
import java.util.List;

import com.channelsoft.zhuaiwa.dal.annotation.Column;
import com.channelsoft.zhuaiwa.dal.annotation.ColumnFamily;
import com.channelsoft.zhuaiwa.dal.annotation.Key;
import com.channelsoft.zhuaiwa.dal.annotation.SuperColumns;

@ColumnFamily("Inviter")
public class Inviter extends BaseObject {
	public static final String CN_INVITER = "Inviter";
	
	@Key
	private String userid;
	
//	@Columns(nameType=String.class, valueType=Long.class)
//	private Map<String, Long> inviters = new LinkedHashMap<String, Long>();
	@SuperColumns(valueType=InviterInfo.class)
	private List<InviterInfo> inviters = new ArrayList<InviterInfo>();

	public String getUserid() {
		return userid;
	}

	public void setUserid(String userid) {
		this.userid = userid;
	}

	public List<InviterInfo> getInviters() {
		return inviters;
	}

	public void setInviters(List<InviterInfo> inviters) {
		this.inviters = inviters;
	}
	
	public static class InviterInfo {
		public static final String FN_INVITERID = "inviterid";
		
		@Key
		@Column(name=FN_INVITERID)
		private String inviterid;

		public void setInviterid(String inviterid) {
			this.inviterid = inviterid;
		}

		public String getInviterid() {
			return inviterid;
		}
	}
}
