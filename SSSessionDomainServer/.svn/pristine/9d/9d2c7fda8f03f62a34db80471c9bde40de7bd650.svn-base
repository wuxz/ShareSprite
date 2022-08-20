package com.channelsoft.zhuaiwa.dal.domain;

import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;

import com.channelsoft.zhuaiwa.dal.annotation.Column;
import com.channelsoft.zhuaiwa.dal.annotation.ColumnFamily;
import com.channelsoft.zhuaiwa.dal.annotation.Key;
import com.channelsoft.zhuaiwa.dal.annotation.SuperColumns;

@ColumnFamily("Contact")
public class Contact extends BaseObject {
	public static final String CN_CONTACT = "Contact";
	
	@Key
	private String userid;
	
	@SuperColumns(valueType=ContactInfo.class)
//	private LinkedHashMap<String, ContactInfo> contacts = new LinkedHashMap<String, ContactInfo>();
	private List<ContactInfo> contacts = new ArrayList<ContactInfo>();
	
	public String getUserid() {
		return userid;
	}

	public void setUserid(String userid) {
		this.userid = userid;
	}

	public List<ContactInfo> getContacts() {
		return contacts;
	}

	public void setContacts(List<ContactInfo> contacts) {
		this.contacts = contacts;
	}

	public static class ContactInfo {
		public static final String FN_CONTACTID = "contactid";
		public static final String FN_USERID = "userid";
		public static final String FN_ALIAS_EMAIL = "alias_email";
		public static final String FN_ALIAS_PHONE_NUMBER = "alias_phone_number";
		public static final String FN_ALIAS_NICKNAME = "alias_nickname";
		
		@Key
		@Column(name=FN_CONTACTID)
		private String contactId;
		
		@Column(name=FN_USERID)
		private String userId;
		
		@Column(name=FN_ALIAS_EMAIL)
		private String aliasEmail;
		
		@Column(name=FN_ALIAS_PHONE_NUMBER)
		private String aliasPhoneNumber;
		
		@Column(name=FN_ALIAS_NICKNAME)
		private String aliasNickname;

		public String getContactId() {
			return contactId;
		}

		public void setContactId(String contactId) {
			this.contactId = contactId;
		}

		public String getUserId() {
			return userId;
		}

		public void setUserId(String userId) {
			this.userId = userId;
		}

		public String getAliasEmail() {
			return aliasEmail;
		}

		public void setAliasEmail(String email) {
			this.aliasEmail = email;
		}

		public void setAliasNickname(String aliasNickname) {
			this.aliasNickname = aliasNickname;
		}

		public String getAliasNickname() {
			return aliasNickname;
		}

		public String getAliasPhoneNumber() {
			return aliasPhoneNumber;
		}

		public void setAliasPhoneNumber(String aliasPhoneNumber) {
			this.aliasPhoneNumber = aliasPhoneNumber;
		}
	}
}
