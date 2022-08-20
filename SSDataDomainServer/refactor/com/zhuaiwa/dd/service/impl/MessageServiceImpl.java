package com.zhuaiwa.dd.service.impl;

import java.lang.management.ManagementFactory;
import java.util.List;
import java.util.Map;

import javax.management.MBeanServer;
import javax.management.ObjectName;

import org.omg.CORBA.BooleanHolder;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.zhuaiwa.api.Common.SSBOX;
import com.zhuaiwa.dd.dao.impl.FavoriteDaoImpl;
import com.zhuaiwa.dd.dao.impl.InBoxDaoImpl;
import com.zhuaiwa.dd.dao.impl.MessageDaoImpl;
import com.zhuaiwa.dd.dao.impl.OutBoxDaoImpl;
import com.zhuaiwa.dd.dao.impl.PubBoxDaoImpl;
import com.zhuaiwa.dd.domain.Message;
import com.zhuaiwa.dd.domain.MessageInfo;
import com.zhuaiwa.dd.domain.Tag.TagInfo;
import com.zhuaiwa.dd.exception.DALException;
import com.zhuaiwa.dd.manage.MessageManageMXBean;
import com.zhuaiwa.dd.service.MessageService;

public class MessageServiceImpl implements MessageService, MessageManageMXBean {
	private static Logger logger = LoggerFactory.getLogger(MessageServiceImpl.class);
	
	private PubBoxDaoImpl pubBoxDao;
	private InBoxDaoImpl inBoxDao;
	private OutBoxDaoImpl outBoxDao;
	private FavoriteDaoImpl favBoxDao;
	private MessageDaoImpl messageDao;
	
	private DataServiceImpl dataService;

	public DataServiceImpl getDataService() {
		return dataService;
	}

	public void setDataService(DataServiceImpl dataService) {
		this.dataService = dataService;
	}

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
	
	public MessageServiceImpl() {
		MBeanServer mbs = ManagementFactory.getPlatformMBeanServer();
        String mbeanName = "com.channelsoft.ss.manage:type=MessageManage";
        try
        {
            mbs.registerMBean(this, new ObjectName(mbeanName));
        }
        catch (Exception e)
        {
            throw new RuntimeException(e);
        }
	}

	public List<MessageInfo> getMessage(String userid, SSBOX ssbox, String cursor_id, int count, BooleanHolder eol) throws DALException {
		if (ssbox == SSBOX.PUBBOX) {
			return pubBoxDao.getMessage(userid, cursor_id, count, eol).getMessages();
		} else if (ssbox == SSBOX.OUTBOX) {
			return outBoxDao.getMessage(userid, cursor_id, count, eol).getMessages();
		} else if (ssbox == SSBOX.INBOX) {
			return inBoxDao.getMessage(userid, cursor_id, count, eol).getMessages();
		} else if (ssbox == SSBOX.FAVBOX) {
			return favBoxDao.getMessage(userid, cursor_id, count, eol).getMessages();
		}
		return null;
	}
	
	public List<MessageInfo> getMessageByTimestamp(String userid, SSBOX ssbox, long starttime, long endtime, int count) throws DALException {
		if (ssbox == SSBOX.PUBBOX) {
			return pubBoxDao.getMessageByTimestamp(userid, starttime, endtime, count).getMessages();
		} else if (ssbox == SSBOX.OUTBOX) {
			return outBoxDao.getMessageByTimestamp(userid, starttime, endtime, count).getMessages();
		} else if (ssbox == SSBOX.INBOX) {
			return inBoxDao.getMessageByTimestamp(userid, starttime, endtime, count).getMessages();
		} else if (ssbox == SSBOX.FAVBOX) {
			return favBoxDao.getMessageByTimestamp(userid, starttime, endtime, count).getMessages();
		}
		return null;
	}
	
	public String AddMessage(Message message) throws DALException {
		return null;
	}
	
	public Map<String, Message> getMessage(List<String> messageids) throws DALException {
		return messageDao.get(messageids);
	}
	
	public void InsertMessage(String userid, SSBOX ssbox, String messageid) throws DALException {
	}
	
	public void RemoveMessage(String userid, SSBOX ssbox, String messageid) throws DALException {
	}
	
	public String AddTag(String userid, TagInfo tag) throws DALException {
		return null;
	}
	
	public List<String> getMessageByTag(String userid, String tagid) throws DALException {
		return null;
	}
	
	public void tagMessage(String userid, String messageid, List<String> tagids) throws DALException {
		
	}

	@Override
	public Message getMessageById(String msgid) throws DALException {
		return messageDao.get(msgid);
	}
	
	@Override
	public List<MessageInfo> getMessage(String userid, SSBOX ssbox, String cursor_id, int count) throws DALException {
		return getMessage(userid, ssbox, cursor_id, count, null);
	}
}
