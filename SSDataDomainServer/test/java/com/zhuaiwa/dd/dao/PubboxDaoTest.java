package com.zhuaiwa.dd.dao;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;

import com.zhuaiwa.dd.config.DDProperties;
import com.zhuaiwa.dd.domain.PubBox;
import com.zhuaiwa.dd.hector.HectorFactory;

public class PubboxDaoTest {
    PubBoxDao pubboxDao;

    @Before
    public void setUp() throws Exception {
        DDProperties.setProperty("dd.cassandra.addresses", "10.130.29.240");
        DDProperties.setProperty("dd.cassandra.port", "9160");
        pubboxDao = new PubBoxDao();
        pubboxDao.setKeyspace(HectorFactory.getKeyspace());
    }

    @After
    public void tearDown() throws Exception {
    }

    @Test
    public void testGetMessage() {
        PubBox pubbox = pubboxDao.getMessage("e52c044534a2d5eb5f374c6c97033883", "000001333D9061EAe52c044534a2d5eb5f374c6c97033883", "0000013328B64290e52c044534a2d5eb5f374c6c97033883", -10, null);
        System.out.println(pubbox.toString());
    }

}
