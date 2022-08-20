package com.channelsoft.zhuaiwa.dal.util;

import com.zhuaiwa.api.Api.SSMember;
import com.zhuaiwa.api.Api.SSMessage;

public class DomainUtils {
	public static String toString(SSMessage ssmessage) {
		StringBuilder sb = new StringBuilder();
		sb.append("{\n");
		sb.append("\t").append("\"msgid\" : ").append(ssmessage.getMsgid()).append(",\n");
		sb.append("\t").append("\"timestamp\" : ").append(ssmessage.getTimestamp()).append(",\n");
		sb.append("\t").append("\"sender\" : ").append(ssmessage.getSender()).append(",\n");
//		sb.append("\t").append("\"receivers\" : ").append(Command.join(ssmessage.getReceiversList())).append(",\n");
		sb.append("}");
		return sb.toString();
	}

//	public static String toString(SSSessionInfo sssessioninfo) {
//		StringBuilder sb = new StringBuilder();
//		sb.append("{\n");
//		if (sssessioninfo.hasClientState()) {
//			SSClientState cs = sssessioninfo.getClientState();
//			sb.append("\t").append("\"client_state\" : ").append("{\n");
//			sb.append("\t\t").append("\"last_send_time\" : ").append(cs.getLastSendTime()).append(",\n");
//			sb.append("\t\t").append("\"last_fetch_time\" : ").append(cs.getLastFetchTime()).append(",\n");
//			sb.append("\t\t").append("\"last_logout_time\" : ").append(cs.getLastLogoutTime()).append(",\n");
//			sb.append("\t\t").append("\"last_seq_number\" : ").append(cs.getLastSeqNumber()).append(",\n");
//			sb.append("\t\t").append("\"last_client_identifier\" : ").append(cs.getLastClientIdentifier()).append(",\n");
//			sb.append("\t").append("},\n");
//		}
//		if (sssessioninfo.getMessageStateCount() > 0) {
//			sb.append("\t").append("\"message_state\" : ").append("[\n");
//			for (SSMessageState ms : sssessioninfo.getMessageStateList()) {
//				sb.append("\t\t{").append("\"userid\" : ").append(ms.getUserid()).append(", ").append("\"seq_number\" : ").append(ms.getSeqNumber()).append("},\n");
//			}
//			sb.append("\t],\n");
//		}
//		sb.append("}");
//		return sb.toString();
//	}
	
	public static String toString(SSMember ssmember) {
		StringBuilder sb = new StringBuilder();
		sb.append("{\n");
		sb.append("\t").append(ssmember.getParentid()).append(" : [\n");
		for (String id : ssmember.getMembersList()) {
			sb.append("\t\t").append(id).append(",\n");
		}
		sb.append("\t").append("]\n");
		sb.append("}");
		return sb.toString();
	}
}
