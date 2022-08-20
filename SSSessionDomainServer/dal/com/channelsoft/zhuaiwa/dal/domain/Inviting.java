package com.channelsoft.zhuaiwa.dal.domain;

import java.util.ArrayList;
import java.util.List;

import com.channelsoft.zhuaiwa.dal.annotation.Column;
import com.channelsoft.zhuaiwa.dal.annotation.ColumnFamily;
import com.channelsoft.zhuaiwa.dal.annotation.Key;
import com.channelsoft.zhuaiwa.dal.annotation.SuperColumns;

@ColumnFamily("Inviting")
public class Inviting extends BaseObject {
	public static final String CN_INVITING = "Inviting";
	
	@Key
	private String userid;
	
//	@Columns(nameType=String.class, valueType=Long.class)
//	private Map<String, Long> invitings = new LinkedHashMap<String, Long>();
	@SuperColumns(valueType=InvitingInfo.class)
	private List<InvitingInfo> invitings = new ArrayList<InvitingInfo>();

	public String getUserid() {
		return userid;
	}

	public void setUserid(String userid) {
		this.userid = userid;
	}

	public List<InvitingInfo> getInvitings() {
		return invitings;
	}

	public void setInvitings(List<InvitingInfo> invitings) {
		this.invitings = invitings;
	}

	public static class InvitingInfo {
		public static final String FN_INVITING = "inviting";
		
		@Key
		@Column(name=FN_INVITING)
		private String inviting;

		public void setInviting(String inviting) {
			this.inviting = inviting;
		}

		public String getInviting() {
			return inviting;
		}
	}
}
