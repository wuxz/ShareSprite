package com.zhuaiwa.util;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.UnsupportedEncodingException;
import java.net.MalformedURLException;
import java.net.URL;
import java.net.URLEncoder;

/**
 * 短信下发
 * 
 * @author Administrator
 */
public class SmsUtil
{

	private static String ip = PropertiesHelper.getValue("sms.url.ip");

	private static String loginPWD = PropertiesHelper
			.getValue("sms.url.loginPWD");

	private static String port = PropertiesHelper.getValue("sms.url.port");

	private static String spNum = PropertiesHelper.getValue("sms.url.spNum");

	private static String UserId = PropertiesHelper.getValue("sms.url.UserId");

	public static void main(String[] args) throws UnsupportedEncodingException
	{
		String s = "测试";
		int code = SmsUtil.sendSms(s, "18610107055");
		System.out.println(code);
	}

	// 返回1为成功，0为失败
	public static int sendSms(String smsText, String receiverNumber)
	{
		int resultCode = 0;
		if (ip == null)
		{
			return 0;
		}

		try
		{
			String smsContent = Base64Code.byteArrayToBase64(smsText
					.getBytes("GBK"));
			smsContent = URLEncoder.encode(smsContent, "gb2312");

			URL smsURL = new URL("http://" + ip + ":" + port
					+ "/smsginterface/SendMsgLong?Id=" + UserId + "&Pwd="
					+ loginPWD + "&SpNum=" + spNum + "&CalledId="
					+ receiverNumber + "&LinkId=" + "&Content=" + smsContent);
			// System.out.println(smsURL);
			BufferedReader in = new BufferedReader(new InputStreamReader(
					smsURL.openStream()));

			String inputLine;
			while ((inputLine = in.readLine()) != null)
			{
				if (inputLine.contains("IsSend"))
				{
					int start = inputLine.indexOf("<IsSend>");
					int end = inputLine.indexOf("</IsSend>");
					String result = inputLine.substring(
							start + "<IsSend>".length(), end);
					resultCode = Integer.parseInt(result);
				}
			}
			in.close();
			return resultCode;
		}
		catch (UnsupportedEncodingException e)
		{
			e.printStackTrace();
		}
		catch (MalformedURLException e)
		{
			e.printStackTrace();
		}
		catch (IOException e)
		{
			e.printStackTrace();
		}

		return resultCode;
	}
}
