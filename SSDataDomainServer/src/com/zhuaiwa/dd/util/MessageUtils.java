package com.zhuaiwa.dd.util;

public class MessageUtils {
	public static long getMessageTimestamp(String msgid) {
		String ts = msgid.substring(0, 16);
		return Long.parseLong(ts, 16);
	}
	public static String getMessageSender(String msgid) {
		return msgid.substring(16);
	}
	public static String normalizeMessageId(String msgid) {
	    return msgid.substring(0, 16).toUpperCase() + msgid.substring(16).toLowerCase();
	}
}
