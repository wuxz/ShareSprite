package com.channelsoft.zhuaiwa.dal.service.impl;

import java.util.List;
import java.util.Map;

import org.apache.log4j.Logger;
import org.omg.CORBA.BooleanHolder;

import com.channelsoft.zhuaiwa.dal.dao.impl.FavoriteDaoImpl;
import com.channelsoft.zhuaiwa.dal.dao.impl.InBoxDaoImpl;
import com.channelsoft.zhuaiwa.dal.dao.impl.MessageDaoImpl;
import com.channelsoft.zhuaiwa.dal.dao.impl.OutBoxDaoImpl;
import com.channelsoft.zhuaiwa.dal.dao.impl.PubBoxDaoImpl;
import com.channelsoft.zhuaiwa.dal.domain.Message;
import com.channelsoft.zhuaiwa.dal.domain.MessageInfo;
import com.channelsoft.zhuaiwa.dal.domain.Tag.TagInfo;
import com.channelsoft.zhuaiwa.dal.exception.DALException;
import com.channelsoft.zhuaiwa.dal.service.MessageService;
import com.zhuaiwa.api.Api.SSBOX;

public class MessageServiceImpl implements MessageService {
	private static Logger logger = Logger.getLogger(MessageServiceImpl.class);
	
	private PubBoxDaoImpl pubBoxDao;
	private InBoxDaoImpl inBoxDao;
	private OutBoxDaoImpl outBoxDao;
	private FavoriteDaoImpl favBoxDao;
	private MessageDaoImpl messageDao;
	
	public PubBoxDaoImpl getPubBoxDao() {
		return pubBoxDao;
	}

	public void setPubBoxDao(PubBoxDaoImpl pubBoxDao) {
		this.pubBoxDao = pubBoxDao;
	}

	public InBoxDaoImpl getInBoxDao() {
		return inBoxDao;
	}

	public void setInBoxDao(InBoxDaoImpl inBoxDao) {
		this.inBoxDao = inBoxDao;
	}

	public OutBoxDaoImpl getOutBoxDao() {
		return outBoxDao;
	}

	public void setOutBoxDao(OutBoxDaoImpl outBoxDao) {
		this.outBoxDao = outBoxDao;
	}

	public FavoriteDaoImpl getFavBoxDao() {
		return favBoxDao;
	}

	public void setFavBoxDao(FavoriteDaoImpl favBoxDao) {
		this.favBoxDao = favBoxDao;
	}

	public void setMessageDao(MessageDaoImpl messageDao) {
		this.messageDao = messageDao;
	}

	public MessageDaoImpl getMessageDao() {
		return messageDao;
	}

	public List<MessageInfo> getMessage(String userid, SSBOX ssbox, String cursor_id, String limit, int count, BooleanHolder eol) throws DALException {
		logger.info("userid: " + userid);
		logger.info("ssbox: " + ssbox.name());
		logger.info("cursor_id: " + cursor_id);
		logger.info("limit: " + limit);
		logger.info("count: " + count);
		
		if (ssbox == SSBOX.PUBBOX) {
			return pubBoxDao.getMessage(userid, cursor_id, limit, count, eol);
		} else if (ssbox == SSBOX.OUTBOX) {
			return outBoxDao.getMessage(userid, cursor_id, limit, count, eol);
		} else if (ssbox == SSBOX.INBOX) {
			return inBoxDao.getMessage(userid, cursor_id, limit, count, eol);
		} else if (ssbox == SSBOX.FAVBOX) {
			return favBoxDao.getMessage(userid, cursor_id, limit, count, eol);
		}
		return null;
	}
	
	public List<MessageInfo> getMessageByTimestamp(String userid, SSBOX ssbox, long starttime, long endtime, int count, BooleanHolder eol) throws DALException {
		
		String cursorId = null;
		String limit = null;
		if (starttime > endtime) {
			cursorId = Message.genMinMessageId(starttime);
			limit = Message.genMinMessageId(endtime);
		} else {
			cursorId = Message.genMaxMessageId(endtime);
			limit = Message.genMaxMessageId(starttime);
		}
		
		return getMessage(userid, ssbox, cursorId, limit, count, eol);
	}
	
	public String AddMessage(Message message) throws DALException {
		return null;
	}
	
	public void InsertMessage(String userid, SSBOX ssbox, String messageid) throws DALException {
	}
	
	public void RemoveMessage(String userid, SSBOX ssbox, String messageid) throws DALException {
	}
	
	public Map<String, Message> getMessage(List<String> messageids) throws DALException {
		return null;
	}
	
	public String AddTag(String userid, TagInfo tag) throws DALException {
		return null;
	}
	
	public List<String> getMessageByTag(String userid, String tagid) throws DALException {
		return null;
	}
	
	public void tagMessage(String userid, String messageid, List<String> tagids) throws DALException {
		
	}
}
