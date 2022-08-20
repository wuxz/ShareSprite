/**
 * 
 */
package com.zhuaiwa.session.util;

import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

import org.junit.Before;
import org.junit.Test;

/**
 * @author wuxz
 */
public class ValidatorUtilTest
{

	/**
	 * @throws java.lang.Exception
	 */
	@Before
	public void setUp() throws Exception
	{
	}

	/**
	 * Test method for
	 * {@link com.zhuaiwa.session.util.ValidatorUtil#isValidEmail(java.lang.String)}
	 * .
	 */
	@Test
	public void testIsValidEmail()
	{
		assertTrue(ValidatorUtil.isValidEmail("1@2.com"));
		assertTrue(ValidatorUtil.isValidEmail("1@2.com.cn"));
		assertFalse(ValidatorUtil.isValidEmail(null));
		assertFalse(ValidatorUtil.isValidEmail("1@2"));
		assertFalse(ValidatorUtil.isValidEmail("@2.com.cn"));
		assertFalse(ValidatorUtil.isValidEmail("@1@2.com.cn"));
	}

	/**
	 * Test method for
	 * {@link com.zhuaiwa.session.util.ValidatorUtil#isValidNickname(java.lang.String)}
	 * .
	 */
	@Test
	public void testIsValidNickname()
	{
		assertFalse(ValidatorUtil.isValidNickname(null));
		assertFalse(ValidatorUtil.isValidNickname(""));
		assertFalse(ValidatorUtil.isValidNickname("1"));
		assertTrue(ValidatorUtil.isValidNickname("1吴相铮"));
		assertTrue(ValidatorUtil.isValidNickname("12"));
		assertTrue(ValidatorUtil.isValidNickname("123"));
		assertFalse(ValidatorUtil.isValidNickname("12吴相铮"));
	}

	/**
	 * Test method for
	 * {@link com.zhuaiwa.session.util.ValidatorUtil#isValidPassword(java.lang.String)}
	 * .
	 */
	@Test
	public void testIsValidPassword()
	{
		assertFalse(ValidatorUtil.isValidPassword(null));
		assertFalse(ValidatorUtil.isValidPassword(""));
		assertFalse(ValidatorUtil.isValidPassword("12345"));
		assertTrue(ValidatorUtil.isValidPassword("abc123"));
		assertTrue(ValidatorUtil.isValidPassword("123456"));
		assertFalse(ValidatorUtil.isValidPassword("_12345"));
		assertTrue(ValidatorUtil.isValidPassword("123456789012345678"));
		assertFalse(ValidatorUtil.isValidPassword("1234567890123456789"));
	}

	/**
	 * Test method for
	 * {@link com.zhuaiwa.session.util.ValidatorUtil#isValidPhoneNumber(java.lang.String)}
	 * .
	 */
	@Test
	public void testIsValidPhoneNumber()
	{
		assertTrue(ValidatorUtil.isValidPhoneNumber("13901234567"));
		assertFalse(ValidatorUtil.isValidPhoneNumber(null));
		assertTrue(ValidatorUtil.isValidPhoneNumber("13001234567"));
		assertTrue(ValidatorUtil.isValidPhoneNumber("15001234567"));
		assertTrue(ValidatorUtil.isValidPhoneNumber("15901234567"));
		assertTrue(ValidatorUtil.isValidPhoneNumber("18901234567"));
		assertTrue(ValidatorUtil.isValidPhoneNumber("18601234567"));
		assertFalse(ValidatorUtil.isValidPhoneNumber("1390123456"));
	}

}
