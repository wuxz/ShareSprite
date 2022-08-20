package com.zhuaiwa.dd.service.impl;

import java.util.List;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.zhuaiwa.dd.dao.impl.ContactDaoImpl;
import com.zhuaiwa.dd.dao.impl.GroupDaoImpl;
import com.zhuaiwa.dd.dao.impl.MemberDaoImpl;
import com.zhuaiwa.dd.domain.Contact.ContactInfo;
import com.zhuaiwa.dd.domain.Group.GroupInfo;
import com.zhuaiwa.dd.domain.Member.MemberInfo;
import com.zhuaiwa.dd.exception.DALException;
import com.zhuaiwa.dd.service.ContactService;

public class ContactServiceImpl implements ContactService {
	private static Logger logger = LoggerFactory.getLogger(ContactServiceImpl.class);
	
	private ContactDaoImpl contactDao;
	private GroupDaoImpl groupDao;
	private MemberDaoImpl memberDao;

	private DataServiceImpl dataService;

	public DataServiceImpl getDataService() {
		return dataService;
	}

	public void setDataService(DataServiceImpl dataService) {
		this.dataService = dataService;
	}

	public ContactDaoImpl getContactDao() {
		return contactDao;
	}

	public void setContactDao(ContactDaoImpl contactDao) {
		this.contactDao = contactDao;
	}

	public GroupDaoImpl getGroupDao() {
		return groupDao;
	}

	public void setGroupDao(GroupDaoImpl groupDao) {
		this.groupDao = groupDao;
	}

	public MemberDaoImpl getMemberDao() {
		return memberDao;
	}

	public void setMemberDao(MemberDaoImpl memberDao) {
		this.memberDao = memberDao;
	}

	public String addContact(String userid, ContactInfo contact) throws DALException {
		return null;
	}
	
	public void removeContact(String userid, String contactid) throws DALException {
		
	}
	
	public String addGroup(String userid, GroupInfo group) throws DALException {
		return null;
	}
	
	public void removeGroup(String userid, String groupid) throws DALException {
		
	}
	
	public List<GroupInfo> getGroup(String userid) throws DALException {
		return null;
	}
	
	public void addMember(String userid, String groupid, List<String> contactid) throws DALException {
		
	}
	
	public void removeMember(String userid, String groupid, List<String> contactid) throws DALException {
		
	}
	
	public List<MemberInfo> getMember(String userid, String groupid) throws DALException {
		return null;
	}
}
