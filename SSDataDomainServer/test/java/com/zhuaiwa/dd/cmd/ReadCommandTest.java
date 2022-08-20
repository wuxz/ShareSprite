package com.zhuaiwa.dd.cmd;


import java.util.Map;

import me.prettyprint.hector.api.Keyspace;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;

import com.zhuaiwa.dd.config.DDProperties;
import com.zhuaiwa.dd.domain.Account;
import com.zhuaiwa.dd.domain.Contact;
import com.zhuaiwa.dd.domain.EmailAccount;
import com.zhuaiwa.dd.domain.Message;
import com.zhuaiwa.dd.domain.PhoneAccount;
import com.zhuaiwa.dd.hector.HectorFactory;

public class ReadCommandTest {
	ReadCommand command;

	@Before
	public void setUp() throws Exception {


	    DDProperties.setProperty("dd.cassandra.addresses", "p0");
	    DDProperties.setProperty("dd.cassandra.port", "9160");
        Keyspace cassandra = HectorFactory.getKeyspace();
		command = new ReadCommand(cassandra);
	}

	@After
	public void tearDown() throws Exception {
	}

	@Test
	public void test() throws Exception {
		command.Object(PhoneAccount.class);
		command.Where("13301202666");
		command.Select();
		System.out.println(command.<PhoneAccount>execute().get("13301202666").getUserid());
	}
	
	@Test
	public void test3() throws Exception {
		command.Object(Account.class);
		command.Where("e9f28e443fd567581c8194ee0403683d");
		command.Select();
		System.out.println(command.<Account>execute().get("e9f28e443fd567581c8194ee0403683d").getPassword());
	}
	
	@Test
	public void test2() throws Exception {
		command.Object(Contact.class);
		command.Where("yaosw");
		command.Limit("", 0);
		Map<String, Contact> columnsMap = (Map<String, Contact>)(Map)command.execute();
		for (String key : columnsMap.keySet()) {
			Contact contact = columnsMap.get(key);
		}
	}
	
	@Test
	public void test4() throws Exception {
		String email = "wdkong@channelsoft.com";
		command.Object(EmailAccount.class);
		command.Where(email);
		command.Select();
		System.out.println(command.<Account>execute().get(email));
	}
	
	@Test
	public void test5() throws Exception {
		String msgid = "0000000000000009test";
		command.Object(Message.class);
		command.Where(msgid);
		command.Select();
		Message message = command.<Message>execute().get(msgid);
		System.out.println(message.getContentString());
		System.out.println(message);
	}
	
	@Test
	public void testPrimitive() throws Exception {
		System.out.println(Byte.class.isAssignableFrom(byte.class));
		System.out.println(byte.class.isAssignableFrom(Byte.class));
		System.out.println(Byte.class.isAssignableFrom(Byte.class));
		System.out.println(byte.class.isAssignableFrom(byte.class));
		
		Class<?> type = byte[].class;
		System.out.println(type.isArray() && byte.class.isAssignableFrom(type.getComponentType()));
	}
}
