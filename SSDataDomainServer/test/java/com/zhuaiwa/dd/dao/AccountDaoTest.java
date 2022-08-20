package com.zhuaiwa.dd.dao;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;

import com.zhuaiwa.dd.config.DDProperties;
import com.zhuaiwa.dd.domain.Account;
import com.zhuaiwa.dd.hector.HectorFactory;

public class AccountDaoTest {
    AccountDao accountDao;

    @Before
    public void setUp() throws Exception {
        DDProperties.setProperty("dd.cassandra.addresses", "10.130.29.240");
        DDProperties.setProperty("dd.cassandra.port", "9160");
        accountDao = new AccountDao();
        accountDao.setKeyspace(HectorFactory.getKeyspace());
    }

    @After
    public void tearDown() throws Exception {
    }

    @Test
    public void testGetString() {
        Account a = accountDao.get("7a5de7abdf1c00dc5cef6e1484d7fd5b");
        System.out.println(a.toString());
    }

}
