package com.channelsoft.zhuaiwa.dal.util;

import java.util.ArrayList;
import java.util.List;

import com.zhuaiwa.api.Api.SSId;

public class SSIdUtils {
	public static String toString(SSId ssid) {
		assert(ssid != null);
		assert(!ssid.getId().isEmpty());
		if (!ssid.hasDomain()) {
			return "userid:" + ssid.getId();
		}
		return ssid.getDomain() + ":" + ssid.getId();
	}
	public static String toString(List<SSId> ssid_list) {
		assert(ssid_list != null);
		assert(!ssid_list.isEmpty());
		StringBuilder sb = new StringBuilder();
		
		boolean semicolon = false;
		for (SSId ssid : ssid_list) {
			if (semicolon) {
				sb.append(";");
			} else {
				semicolon = true;
			}
			sb.append(toString(ssid));
		}
		return sb.toString();
	}
	public static List<SSId> parse(String s) {
		assert(s != null);
		assert(!s.isEmpty());
		String[] ssid_array = s.split(";");
		
		List<SSId> ssid_list = new ArrayList<SSId>();
		for (String ssid_str : ssid_array) {
			if (ssid_str.isEmpty())
				continue;
			ssid_list.add(parseSingle(ssid_str));
		}
		
		return ssid_list;
	}
	public static SSId parseSingle(String s) {
		String[] parts = s.split(":");
		assert(parts.length > 0);
		if (parts.length == 1) {
			return SSId.newBuilder().setDomain("userid").setId(parts[0]).build();
		}
		return SSId.newBuilder().setDomain(parts[0]).setId(parts[1]).build();
	}
}
