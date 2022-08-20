package com.zhuaiwa.dd.tool;

import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

import me.prettyprint.hector.api.Keyspace;

import com.zhuaiwa.dd.cmd.DeleteCommand;
import com.zhuaiwa.dd.cmd.IterateCommand;
import com.zhuaiwa.dd.cmd.ReadCommand;
import com.zhuaiwa.dd.domain.Account;
import com.zhuaiwa.dd.domain.Contact;
import com.zhuaiwa.dd.domain.Contact.ContactInfo;
import com.zhuaiwa.dd.hector.HectorFactory;

public class RepairContact {

	private static Keyspace cassandra;

	/**
	 * @param args
	 */
	public static void main(String[] args) throws Exception {
	    HectorFactory.getKeyspace();
		IterateCommand command = new IterateCommand(cassandra);
		command.Object(Account.class);
		command.Select();
		command.Where("", 50);
		
		for (Object o : command) {
			Account account = (Account) o;
			List<ContactInfo>  contacts = getContacts(account.getUserid());
			if (contacts == null)
				continue;
			
			Set<String> emails = new HashSet<String>();
			for (ContactInfo ci : contacts) {
				String email = ci.getAliasEmail();
				if (email != null) {
					if (!emails.contains(email)) {
						emails.add(email);
					} else {
						System.out.println(account.getUserid() + ", " + ci.getAliasEmail());
//						removeContact(account.getUserid(), ci.getContactId());
					}
				}
			}
			
			System.out.println("--------");
		}
	}
	
	private static List<ContactInfo> getContacts(String userid) throws Exception {
		ReadCommand command = new ReadCommand(cassandra)
		.Object(Contact.class)
		.Select()
		.Where(userid);
		Map<String,Contact> resultmap = command.<Contact>execute();
		if (resultmap == null)
			return null;
		Contact c = resultmap.get(userid);
		if (c == null)
			return null;
		return c.getContacts();
	}
	
	private static void removeContact(String userid, String contactid) throws Exception {
		DeleteCommand command =
			new DeleteCommand(cassandra)
			.Object(Contact.CN_CONTACT)
			.Where(userid)
			.DeleteColumn(contactid);
		command.execute();
	}
}
