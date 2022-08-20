package com.zhuaiwa.session.search;

import static org.junit.Assert.*;

import java.util.ArrayList;
import java.util.List;

import javax.jms.JMSException;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;

import com.zhuaiwa.session.search.domain.Operate;
import com.zhuaiwa.session.search.domain.User;

public class IndexerTest {
	String email = "zhouyj@channelsoft.com";
	
	Indexer indexer;
	
	String userId;
	String nickname;
	long birthday;
	int gender;
	String country;
	String province;
	String city;
	List<String> fav_tags;

	User user;
	
	
	@Before
	public void setUp() throws Exception {
		indexer = Indexer.getInstance();
		userId = "ab342f2bf3db14b15058fa613373ca84";
		nickname = "周裕娟";
		birthday = 1l;
		gender = 1;
		country = "中国";
		city = "北京";
		fav_tags = new ArrayList<String>();
		fav_tags.add("育儿");
		fav_tags.add("IT");
		fav_tags.add("iphone");
		user = new User();
		user.setId(userId);
		user.setNickname(nickname);
		user.setBirthday(birthday);
		user.setCity(city);
		user.setCountry(country);
		user.setEmail(email);
		user.setFavorTags(fav_tags);
		user.setGender(gender);
	}

	@After
	public void tearDown() throws Exception {
	}

	@Test
	public void testActive() throws Exception {
		
		indexer.index(Operate.Add, user);
	}

	@Test
	public void testBindOrUnbind() {
		fail("Not yet implemented");
	}

	@Test
	public void testModifyAccountOrProfile() throws JMSException {
		indexer.index(Operate.Modify, user);
	}

}
