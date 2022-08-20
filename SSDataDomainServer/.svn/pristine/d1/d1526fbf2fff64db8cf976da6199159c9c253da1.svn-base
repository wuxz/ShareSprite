package com.zhuaiwa.dd.manage;

import java.util.List;

import com.zhuaiwa.api.Common.SSBOX;
import com.zhuaiwa.dd.domain.Message;
import com.zhuaiwa.dd.domain.MessageInfo;
import com.zhuaiwa.dd.exception.DALException;

public interface MessageManageMXBean {
	Message getMessageById(String msgid) throws DALException;
	List<MessageInfo> getMessage(String userid, SSBOX ssbox, String cursor_id, int count) throws DALException;
}
