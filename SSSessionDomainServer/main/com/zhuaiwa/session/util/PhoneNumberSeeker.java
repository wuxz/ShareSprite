package com.zhuaiwa.session.util;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.InputStreamReader;
import java.nio.charset.Charset;
import java.util.Collections;
import java.util.HashMap;
import java.util.Map;

import org.apache.commons.lang.StringUtils;

import com.zhuaiwa.util.PropertiesHelper;

public class PhoneNumberSeeker
{
	public static class PhoneNumberInfo
	{
		public String areaCode;

		public String carrier;

		public String city;

		public String network;

		public String postCode;

		public String prefix;

		public String province;

		public PhoneNumberInfo(String prefix)
		{
			this.prefix = prefix;
			this.province = "未知";
			this.city = "未知";
			this.areaCode = "未知";
			this.postCode = "未知";
			this.carrier = "未知";
			this.network = "未知";
		}

		public PhoneNumberInfo(String[] data)
		{
			if ((data == null) || (data.length != 7))
			{
				return;
			}
			this.prefix = data[0];
			this.province = data[1];
			if (this.province.trim().length() == 0)
			{
				this.province = "未知";
			}
			this.city = data[2];
			this.areaCode = data[3];
			this.postCode = data[4];
			this.carrier = data[5];
			if (this.carrier.trim().length() == 0)
			{
				this.carrier = "未知";
			}
			this.network = data[6];
		}

		public String getName()
		{
			StringBuffer sb = new StringBuffer();
			sb.append(this.province);
			sb.append("_");
			if (this.carrier.indexOf("移动") > -1)
			{
				sb.append("移动");
			}
			else if (this.carrier.indexOf("联通") > -1)
			{
				sb.append("联通");
			}
			else if (this.carrier.indexOf("电信") > -1)
			{
				sb.append("电信");
			}
			else
			{
				sb.append("未知");
			}
			return sb.toString();
		}

		@Override
		public String toString()
		{
			return "PhoneNumberInfo [prefix=" + prefix + ", provence="
					+ province + ", city=" + city + ", areaCode=" + areaCode
					+ ", postCode=" + postCode + ", carrier=" + carrier
					+ ", network=" + network + "]";
		}

	}

	/**
	 * @param args
	 */
	public static void main(String[] args)
	{
		String filePath = PropertiesHelper.getValue("phonenumber.file");
		PhoneNumberSeeker pns = new PhoneNumberSeeker(
				StringUtils.isEmpty(filePath) ? new String[] {}
						: filePath.split(" "));
		System.out.println(pns.map.size());
	}

	private Map<String, PhoneNumberInfo> map = Collections
			.synchronizedMap(new HashMap<String, PhoneNumberInfo>());

	public PhoneNumberSeeker(String[] fileList)
	{
		try
		{
			for (String file : fileList)
			{
				loadDb(new File(file));
			}
		}
		catch (Exception e)
		{
			e.printStackTrace();
		}
	}

	private void loadDb(File file) throws Exception
	{
		BufferedReader br = new BufferedReader(new InputStreamReader(
				new FileInputStream(file), Charset.forName("utf-8")));
		String line = null;
		while ((line = br.readLine()) != null)
		{
			String[] fragments = line.split("\\|");
			if (fragments.length == 7)
			{
				map.put(fragments[0], new PhoneNumberInfo(fragments));
			}
			else
			{
				System.err.println(line + " is invalid." + fragments.length);
			}
		}
	}

	public PhoneNumberInfo seek(String prefix)
	{
		PhoneNumberInfo info = map.get(prefix);
		if (info == null)
		{
			info = new PhoneNumberInfo(prefix);
		}
		return info;
	}

}
