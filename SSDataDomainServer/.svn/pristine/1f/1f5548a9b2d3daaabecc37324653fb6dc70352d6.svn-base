package com.zhuaiwa.dd.domain;

import java.util.ArrayList;
import java.util.List;

import com.zhuaiwa.dd.annotation.Column;
import com.zhuaiwa.dd.annotation.ColumnFamily;
import com.zhuaiwa.dd.annotation.Key;
import com.zhuaiwa.dd.annotation.SuperColumns;

@ColumnFamily(value="Contact", isSuper=true)
public class Contact extends BaseObject {
	public static final String CN_CONTACT = "Contact";
	
	@Key
	private String userid;
	
	@SuperColumns(valueType=ContactInfo.class)
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
	
	@Override
	public String toString() {
		return toString(0);
	}
	
	public static class ContactInfo extends BaseObject {
		public static final String FN_CONTACTID = "contactid";
		public static final String FN_USERID = "userid";
		public static final String FN_ALIAS_EMAIL = "email";
		public static final String FN_ALIAS_PHONE_NUMBER = "phone_number";
		public static final String FN_ALIAS_NICKNAME = "nickname";
		public static final String FN_BINARY = "binary";
		
		@Key
		@Column(name=FN_CONTACTID)
		private String contactId;
		
		@Column(name=FN_USERID)
		private String userId;
		
		@Column(name=FN_ALIAS_EMAIL)
		private String email;
		
		@Column(name=FN_ALIAS_PHONE_NUMBER)
		private String phoneNumber;
		
		@Column(name=FN_ALIAS_NICKNAME)
		private String nickname;
		
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
			return email;
		}

		public void setAliasEmail(String email) {
			this.email = email;
		}

		public void setAliasNickname(String aliasNickname) {
			this.nickname = aliasNickname;
		}

		public String getAliasNickname() {
			return nickname;
		}

		public String getAliasPhoneNumber() {
			return phoneNumber;
		}

		public void setAliasPhoneNumber(String aliasPhoneNumber) {
			this.phoneNumber = aliasPhoneNumber;
		}
	}
}
