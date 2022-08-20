package com.zhuaiwa.api.util;

import java.util.ArrayList;
import java.util.List;

import org.apache.commons.lang.StringUtils;

import com.zhuaiwa.api.Common.SSId;

public class SSIdUtils {
	public static enum SSIdDomain {
		userid, phone, email
	}
	
	public static boolean isEmpty(SSId ssid) {
		if (!ssid.hasId())
			return true;
		if (ssid.getId() == null || ssid.getId().isEmpty())
			return true;
		return false;
	}

	public static SSId fromUserId(String userid) {
		assert(userid != null);
		assert(!userid.isEmpty());
		return SSId.newBuilder().setDomain(SSIdDomain.userid.name()).setId(userid).build();
	}

	public static SSId fromEmail(String email) {
		assert(email != null);
		assert(!email.isEmpty());
		return SSId.newBuilder().setDomain(SSIdDomain.email.name()).setId(email).build();
	}

	public static SSId fromPhone(String phone) {
		assert(phone != null);
		assert(!phone.isEmpty());
		return SSId.newBuilder().setDomain(SSIdDomain.phone.name()).setId(phone).build();
	}

	public static boolean isUserId(SSId ssid) {
		if (!ssid.hasDomain())
			return true;
		return ssid.getDomain().equalsIgnoreCase(SSIdDomain.userid.name());
	}

	public static boolean isEmailId(SSId ssid) {
		if (!ssid.hasDomain())
			return false;
		return ssid.getDomain().equalsIgnoreCase(SSIdDomain.email.name());
	}

	public static boolean isPhoneId(SSId ssid) {
		if (!ssid.hasDomain())
			return false;
		return ssid.getDomain().equalsIgnoreCase(SSIdDomain.phone.name());
	}

	public static String toString(SSId ssid) {
		assert (ssid != null);
		assert (!ssid.getId().isEmpty());
		if (!ssid.hasDomain()) {
			return SSIdDomain.userid.name() + ":" + ssid.getId();
		}
		return ssid.getDomain() + ":" + ssid.getId();
	}

	public static String toString(List<SSId> ssid_list) {
		assert (ssid_list != null);
		assert (!ssid_list.isEmpty());
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

	public static String getId(List<SSId> ssid_list, SSIdDomain domain) {
		for (SSId ssid : ssid_list) {
			if (domain.name().equals(ssid.getDomain())) {
				return ssid.getId();
			}
		}
		return null;
	}

	public static SSIdDomain getDomain(String domain) {
		if(StringUtils.isEmpty(domain)) return SSIdDomain.userid;
		for (SSIdDomain ssidDomain : SSIdDomain.values()) {
			if (domain.equals(ssidDomain.name())) {
				return ssidDomain;
			}
		}
		return null;
	}

	public static List<SSId> parse(String s) {
		assert (s != null);
		assert (!s.isEmpty());
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
		assert (parts.length > 0);
		if (parts.length == 1) {
			return SSId.newBuilder().setDomain(SSIdDomain.userid.name()).setId(parts[0]).build();
		}
		return SSId.newBuilder().setDomain(parts[0]).setId(parts[1]).build();
	}
}
