package com.channelsoft.zhuaiwa.dal.dao.impl;

import java.nio.ByteBuffer;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import org.omg.CORBA.BooleanHolder;

import com.channelsoft.zhuaiwa.dal.cmd.ReadCommand;
import com.channelsoft.zhuaiwa.dal.domain.BaseObject;
import com.channelsoft.zhuaiwa.dal.domain.MessageInfo;
import com.channelsoft.zhuaiwa.dal.exception.DALException;
import com.channelsoft.zhuaiwa.dal.util.KeyUtils;
import com.channelsoft.zhuaiwa.dal.util.ReflectUtils;

public class MessageBoxDaoImpl<T extends BaseObject> extends BaseDaoImpl<T> {
	
	public List<MessageInfo> getMessage(String userid, String cursor_id, String limit, int count, BooleanHolder eol) throws DALException {
		ByteBuffer userid_key = KeyUtils.toByteBuffer(userid);
		
		ReadCommand command = new ReadCommand(getCassandra());
		command
		.Object(getClazz())
		.Select()
		.Where(userid_key);
		
		if (eol != null)
			eol.value = false;
		if (count != Integer.MAX_VALUE && count != Integer.MIN_VALUE) {
			int xcount = 0;
			if (count > 0) {
				xcount = count + 2;
			}
			if (count < 0) {
				xcount = count - 1;
			}
			if (count == 0) {
				if (eol != null)
					eol.value = true;
			}
			command.Limit(KeyUtils.toByteBuffer(cursor_id), KeyUtils.toByteBuffer(limit), xcount);
		} else {
			if (eol != null)
				eol.value = true;
			command.Limit(KeyUtils.toByteBuffer(cursor_id), KeyUtils.toByteBuffer(limit), count);
		}
		
		Map<String, Object> r = KeyUtils.toStringMap(command.execute());
		if (r == null) {
			return null;
		}
		Object obj = r.get(userid_key);
		if (obj == null) {
			return null;
		}
		
		List<MessageInfo> message_list = new ArrayList<MessageInfo>();
		try {
			message_list.addAll((List<MessageInfo>)ReflectUtils.getFieldValue(getClazz(), "messages", obj));
		} catch (IllegalArgumentException e) {
			throw new DALException(e);
		} catch (IllegalAccessException e) {
			throw new DALException(e);
		}
		if (count > 0) {
			if (cursor_id != null && message_list.size() > 0) {
				if (message_list.get(0).getMsgid().equals(cursor_id)) {
					message_list.remove(0);
				}
			}
		} else {
			if (cursor_id != null && message_list.size() > 0) {
				if (message_list.get(message_list.size() - 1).getMsgid().equals(cursor_id)) {
					message_list.remove(message_list.size() - 1);
				}
			}
		}
		
		if (count != Integer.MAX_VALUE && count != Integer.MIN_VALUE) {
			if (count > 0) {
				if (message_list.size() <= count) {
					if (eol != null)
						eol.value = true;
				}
				while (message_list.size() > count) {
					message_list.remove(message_list.size()-1);
				}
			}
			if (count < 0) {
				if (message_list.size() <= (-1*count)) {
					if (eol != null)
						eol.value = true;
				}
				while (message_list.size() > (-1*count)){
					message_list.remove(0);
				}
			}
		}
		
		return message_list;
	}
}
