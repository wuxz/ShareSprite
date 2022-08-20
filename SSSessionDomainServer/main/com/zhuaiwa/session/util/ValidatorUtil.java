/**
 * 
 */
package com.zhuaiwa.session.util;

import java.util.regex.Pattern;

/**
 * @author wuxz
 */
public class ValidatorUtil
{
	static Pattern emailPattern = Pattern
			.compile("[a-z0-9!#$%&'*+\\/=?^_`{|}~-]+(?:\\.[a-z0-9!#$%&'*+\\/=?^_`{|}~-]+)*@(?:[a-z0-9](?:[a-z0-9-]*[a-z0-9])?\\.)+[a-z0-9](?:[a-z0-9-]*[a-z0-9])?");

	static Pattern nicknamePattern = Pattern.compile(".{2,4}");

	static Pattern passwordPattern = Pattern.compile("[a-zA-Z0-9]{6,32}");

	static Pattern phoneNumberPattern = Pattern
			.compile("(13[0-9]|15[0-9]|18[0-9])[0-9]{8}");

	public static boolean isValidEmail(String email)
	{
		return (email != null) && (emailPattern.matcher(email).matches());
	}

	public static boolean isValidNickname(String name)
	{
		return (name != null) && (nicknamePattern.matcher(name).matches());
	}

	public static boolean isValidPassword(String password)
	{
		return (password != null)
				&& (passwordPattern.matcher(password).matches());
	}

	public static boolean isValidPhoneNumber(String phoneNumber)
	{
		return (phoneNumber != null)
				&& (phoneNumberPattern.matcher(phoneNumber).matches());
	}

}
