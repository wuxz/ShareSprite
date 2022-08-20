package com.zhuaiwa.dd.dao.impl;

import java.util.ArrayList;
import java.util.Map;

import org.omg.CORBA.BooleanHolder;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.zhuaiwa.dd.cmd.ReadCommand;
import com.zhuaiwa.dd.domain.BaseObject;
import com.zhuaiwa.dd.domain.MessageInfo;
import com.zhuaiwa.dd.exception.DALException;
import com.zhuaiwa.dd.util.ReflectUtils;

public class MessageBoxDaoImpl<T extends BaseObject> extends BaseDaoImpl<T> {
	
	private static Logger logger = LoggerFactory.getLogger(MessageBoxDaoImpl.class);
	
	public String genMessageId(String userid, long number) {
		return String.format("%1$016X%2$s", number, userid);
	}

	public String genMaxMessageId(long number) {
		return genMessageId("zzzzzzzzzzzzzzzzzzzzzzzzzzzzzzzz", number);
	}

	public String genMinMessageId(long number) {
		return genMessageId("00000000000000000000000000000000", number);
	}
	
	public T getMessageByTimestamp(String userid, long starttime, long endtime) throws DALException {
		return getMessageByTimestamp(userid, starttime, endtime, Integer.MAX_VALUE);
	}
	
	public T getMessageByTimestamp(String userid, long starttime, long endtime, int count) throws DALException {
		String cursorId = null;
		String limit = null;
		if (starttime < endtime) {
			cursorId = genMaxMessageId(starttime);
			limit = genMaxMessageId(endtime);
			count = (Integer.MIN_VALUE == count ? Integer.MAX_VALUE : Math.abs(count));
		} else {
			cursorId = genMinMessageId(starttime);
			limit = genMinMessageId(endtime);
			count = (Integer.MAX_VALUE == count ? Integer.MIN_VALUE : -Math.abs(count));
		}
		
		return getMessage(userid, cursorId, limit, count, null);
	}
	
	public T getMessage(String userid, String cursor_id, int count, BooleanHolder eol) throws DALException {
		return getMessage(userid, cursor_id, null, count, eol);
	}
	
	@SuppressWarnings("unchecked")
	public T getMessage(String userid, String cursor_id, String limit, int count, BooleanHolder eol) throws DALException {
		logger.info("userid: " + userid);
		logger.info("cursor_id: " + cursor_id);
		logger.info("limit: " + limit);
		logger.info("count: " + count);
		
		assert(getCassandra() != null);

		ReadCommand command = new ReadCommand(getCassandra());
		command
		.Object(getClazz())
		.Select()
		.Where(userid);
		
		if (eol != null)
			eol.value = false;
		if (count == Integer.MAX_VALUE) {
			if (eol != null)
				eol.value = true;
			command.Limit(cursor_id, limit, Integer.MAX_VALUE);
		} else if (count == Integer.MAX_VALUE-1) {
			if (eol != null)
				eol.value = true;
			command.Limit(cursor_id, limit, Integer.MAX_VALUE);
		} else if (count == Integer.MIN_VALUE) {
			if (eol != null)
				eol.value = true;
			command.Limit(cursor_id, limit, Integer.MIN_VALUE);
		} else if (count == Integer.MIN_VALUE+1) {
			if (eol != null)
				eol.value = true;
			command.Limit(cursor_id, limit, Integer.MIN_VALUE);
		} else {
			command.Limit(cursor_id, limit, (count >= 0 ? (count + 2) : (count - 2)));
		}
//		if (count != Integer.MAX_VALUE && count != Integer.MIN_VALUE && count != 0) {
//			command.Limit(cursor_id, limit, (count > 0 ? (count + 2) : (count - 2)));
//		} else {
//			if (eol != null)
//				eol.value = true;
//			command.Limit(cursor_id, limit, count);
//		}
		
		Map<String, T> r;
		try {
			r = command.execute();
		} catch (DALException e) {
			logger.info("ReadCommand failed to execute.", e);
			throw e;
		}
		if (r == null) {
			logger.info("ReadCommand failed to execute.");
			return null;
		}
		T obj = r.get(userid);
		if (obj == null) {
			logger.info("No object with " + userid);
			return null;
		}
		
		ArrayList<MessageInfo> messageids;
		try {
			messageids = (ArrayList<MessageInfo>)ReflectUtils.getFieldValue(getClazz(), "messages", obj);
		} catch (IllegalArgumentException e) {
			logger.info("", e);
			throw new DALException(e);
		} catch (IllegalAccessException e) {
			logger.info("", e);
			throw new DALException(e);
		}
		if (messageids == null) {
			messageids = new ArrayList<MessageInfo>();
		}
		
		StringBuilder sb = new StringBuilder();
		sb.append("original messageids: " + messageids.size() + ".\n");
		for (MessageInfo msgid : messageids) {
			sb.append("messageid: ").append(msgid.getMsgid()).append("\n");
		}
		logger.info(sb.toString());
		
		if (cursor_id != null && messageids.size() > 0) {
			if (messageids.get(0).getMsgid().equals(cursor_id))
				messageids.remove(0);
//			messageids.remove(cursor_id);
		}
		
		if (count != Integer.MAX_VALUE && count != Integer.MIN_VALUE  && count != 0) {
			if (messageids.size() <= Math.abs(count)) {
				if (eol != null)
					eol.value = true;
			} else {
				do {
					messageids.remove(messageids.size()-1);
				} while (messageids.size() > Math.abs(count));
				
//				int i = 0;
//				Iterator<Entry<String, Long>> iterator = messageids.entrySet().iterator();
//				while(iterator.hasNext()) {
//					i++;
//					iterator.next();
//					if (i > Math.abs(count)) {
//						iterator.remove();
//					}
//				}
			}
		}
		return obj;
	}
}
