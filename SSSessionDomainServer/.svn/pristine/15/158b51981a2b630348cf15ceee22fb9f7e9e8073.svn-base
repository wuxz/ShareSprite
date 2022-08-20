package com.channelsoft.zhuaiwa.dal.service;


import junit.framework.Assert;

import org.apache.cassandra.thrift.Cassandra;
import org.apache.thrift.protocol.TBinaryProtocol;
import org.apache.thrift.transport.TSocket;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;

import com.channelsoft.zhuaiwa.dal.dao.impl.AccountDaoImpl;
import com.channelsoft.zhuaiwa.dal.dao.impl.EmailAccountDaoImpl;
import com.channelsoft.zhuaiwa.dal.dao.impl.FollowerDaoImpl;
import com.channelsoft.zhuaiwa.dal.dao.impl.FollowingDaoImpl;
import com.channelsoft.zhuaiwa.dal.dao.impl.PhoneAccountDaoImpl;
import com.channelsoft.zhuaiwa.dal.dao.impl.ProfileDaoImpl;
import com.channelsoft.zhuaiwa.dal.domain.Account;
import com.channelsoft.zhuaiwa.dal.service.impl.UserServiceImpl;

public class UserServiceTester {
	
	private TSocket socket;
	private Cassandra.Client client;
	private UserServiceImpl usersvc;

	@Before
	public void setUp() throws Exception {
		socket = new TSocket("127.0.0.1", 9160);
        TBinaryProtocol binaryProtocol = new TBinaryProtocol(socket, false, false);
        client = new Cassandra.Client(binaryProtocol);

        try {
        	socket.open();
        } catch (Exception e) {
        	e.printStackTrace();
            return;
        }
        
        AccountDaoImpl accountDao = new AccountDaoImpl();
        accountDao.setCassandra(client);
        
        EmailAccountDaoImpl emailAccountDao = new EmailAccountDaoImpl();
        emailAccountDao.setCassandra(client);
        
        PhoneAccountDaoImpl phoneAccountDao = new PhoneAccountDaoImpl();
        phoneAccountDao.setCassandra(client);
        
        ProfileDaoImpl profileDao = new ProfileDaoImpl();
        profileDao.setCassandra(client);
        
        FollowingDaoImpl followingDao = new FollowingDaoImpl();
        followingDao.setCassandra(client);
        
        FollowerDaoImpl followerDao = new FollowerDaoImpl();
        followerDao.setCassandra(client);
        
        usersvc = new UserServiceImpl();
        usersvc.setAccountDao(accountDao);
        usersvc.setEmailAccountDao(emailAccountDao);
        usersvc.setPhoneAccountDao(phoneAccountDao);
        usersvc.setFollowingDao(followingDao);
        usersvc.setFollowerDao(followerDao);
        usersvc.setProfileDao(profileDao);
	}

	@After
	public void tearDown() throws Exception {
        socket.close();
	}

	@Test
	public void testCreateAccount() throws Exception {
		Account a = new Account();
		a.setEmail("yaosw@channelsoft.com");
		a.setPassword("yaosw");
		a.setRegisterTime(System.currentTimeMillis());
		a.setRole(0L);
		String userid = usersvc.CreateAccount(a);
		
		Account a1 = usersvc.getAccount(userid);
		Assert.assertEquals("yaosw@channelsoft.com", a1.getEmail());
		Account a2 = usersvc.getAccountByEmail("yaosw@channelsoft.com");
		Assert.assertEquals(userid, a2.getUserid());
	}
}
