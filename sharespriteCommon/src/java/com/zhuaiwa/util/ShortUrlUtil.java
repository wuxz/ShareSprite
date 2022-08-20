package com.zhuaiwa.util;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.UnsupportedEncodingException;
import java.net.MalformedURLException;
import java.net.URL;
import java.net.URLEncoder;

/**
 * 短链接口
 * @author Administrator
 *
 */
public class ShortUrlUtil {

	private static String ShortApi = PropertiesHelper.getValue("short.api.url", "http://59.151.117.230:8680/");
	static{
		if(!ShortApi.endsWith("/")){
			ShortApi += "/";
		}
	}
	public static String shortUrl(String url){
		try {
			url = URLEncoder.encode(url, "UTF-8");
			
			URL smsURL = new URL(ShortApi + "?url=" + url);
			BufferedReader in = new BufferedReader(new InputStreamReader(smsURL
					.openStream()));
			String inputLine = in.readLine();
			in.close();
			return inputLine;
		} catch (UnsupportedEncodingException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (MalformedURLException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		return null;
	}
	
	public static void main(String[] args) {
		String url = "http://wap.baiku.cn";
		String shortUrl = ShortUrlUtil.shortUrl(url);
		System.out.println(shortUrl);
	}
}
