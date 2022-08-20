package com.channelsoft.zhuaiwa.dal.dao.impl;

import java.nio.ByteBuffer;

import com.channelsoft.zhuaiwa.dal.cmd.CreateCommand;
import com.channelsoft.zhuaiwa.dal.domain.Message;
import com.channelsoft.zhuaiwa.dal.exception.DALException;
import com.channelsoft.zhuaiwa.dal.util.KeyUtils;

public class MessageDaoImpl extends BaseDaoImpl<Message> {

//	@Override
//	public String insert(Message message) throws CommandException {
//		CreateCommand command =
//			new CreateCommand(getCassandra())
//			.Object(this.getColumnFamilyName());
//		
//		ByteBuffer msgid = null;
//		if (message.getMsgid() == null) {
//			msgid = message.generateId();
//			message.setMsgid(KeyUtils.toString(msgid));
//		} else {
//			msgid = KeyUtils.toByteBuffer(message.getMsgid());
//		}
//		
//		command.InsertWithKey(msgid, KeyUtils.toByteBuffer(Message.FN_MSGID), msgid);
//		if (message.getTimestamp() != null) {
//			command.InsertWithKey(msgid, KeyUtils.toByteBuffer(Message.FN_TIMESTAMP), message.getTimestamp());
//		}
//		if (message.getSender() != null) {
//			command.InsertWithKey(msgid, KeyUtils.toByteBuffer(Message.FN_SENDER), message.getSender());
//		}
//		if (message.getReceivers() != null) {
//			command.InsertWithKey(msgid, KeyUtils.toByteBuffer(Message.FN_RECEIVERS), message.getReceivers());
//		}
//		if (message.getMsgType() != null) {
//			command.InsertWithKey(msgid, KeyUtils.toByteBuffer(Message.FN_MSG_TYPE), message.getMsgType());
//		}
//		if (message.getContent() != null) {
//			command.InsertWithKey(msgid, KeyUtils.toByteBuffer(Message.FN_CONTENT), message.getContent());
//		}
//		
//		command.execute();
//		
//		return message.getMsgid();
//	}
}
