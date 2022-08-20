package com.zhuaiwa.dd.domain;

import com.zhuaiwa.dd.annotation.Column;
import com.zhuaiwa.dd.annotation.Key;

public class InviteInfo extends BaseObject {

	public static final String FN_SSID = "ssid";
	
	@Key
	@Column(name=FN_SSID)
	private String ssid;

	public String getSsid() {
		return ssid;
	}

	public void setSsid(String ssid) {
		this.ssid = ssid;
	}
}
