package com.zhuaiwa.session.util;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.alisoft.xplatform.asf.cache.ICacheManager;
import com.alisoft.xplatform.asf.cache.IMemcachedCache;
import com.alisoft.xplatform.asf.cache.memcached.CacheUtil;
import com.alisoft.xplatform.asf.cache.memcached.MemcachedCacheManager;

public class MemcachedService
{
	private static final Logger logger = LoggerFactory
			.getLogger(MemcachedService.class);

	static ICacheManager<IMemcachedCache> manager;

	static boolean useQ = true;

	static
	{
		try
		{
			manager = CacheUtil.getCacheManager(IMemcachedCache.class,
					MemcachedCacheManager.class.getName());
			manager.setConfigFile("memcached.xml");// 可以指定配置文件名
			manager.start();
		}
		catch (Throwable e)
		{
			logger.error("启动Memcached客户端失败", e);

			useQ = false;
		}
	}

	public static IMemcachedCache getClient()
	{
		return useQ ? manager.getCache("mclient0") : null;
	}

	public static IMemcachedCache getClientQ()
	{
		return useQ ? manager.getCache("mclientq") : null;
	}

	public static String getMsg(String key)
	{
		return useQ ? (String) getClientQ().get(key) : null;
	}

	public static void main(String args[])
	{
		/*
		  MemcachedService mcc = new MemcachedService();
		  boolean success1 = mcc.sendMsg("SLEE","1");
		  boolean success2 = mcc.sendMsg("SLEE","2");
		  boolean success3 = (new MemcachedService()).sendMsg("SLEE","3");

		  System.out.println((new MemcachedService()).getMsg("SLEE"));
		  System.out.println((new MemcachedService()).getMsg("SLEE"));
		  System.out.println((new MemcachedService()).getMsg("SLEE"));
		  */

		// String str=test.getMsg("SLEE");
		// System.out.println(str);
		// str=test.getMsg("SLEE");
		// System.out.println(str);

		// MemcachedService.shutdown();
	}

	public static boolean sendMsg(String strKey, String message)
	{
		if (useQ)
		{
			getClientQ().put(strKey, message);

			return true;
		}
		else
		{
			return false;
		}
	}

	public static void shutdown()
	{
		if (!useQ)
		{
			return;
		}

		manager.stop();
	}

	String port;

	String strServer;

}
