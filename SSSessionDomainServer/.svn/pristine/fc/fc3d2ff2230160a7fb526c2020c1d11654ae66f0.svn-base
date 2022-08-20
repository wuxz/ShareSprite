package com.zhuaiwa.session.util;

import java.io.File;
import java.io.IOException;
import java.io.StringWriter;
import java.util.HashMap;
import java.util.Map;

import com.zhuaiwa.util.ShortUrlUtil;

import freemarker.template.Configuration;
import freemarker.template.Template;

public class FreemarkerUtil
{
	static Template activateAccountSuccessTemplate = null;

	static Template activeCodeMailTemplate = null;

	static Configuration cfg = new Configuration();

	static Template failedTemplate = null;

	static Template forgetPasswordFormTemplate = null;

	static Template forgetPasswordMailTemplate = null;

	static Template inviteMailTemplate = null;

	static Template shareMessageMailTemplate = null;

	static Template successTemplate = null;
	static
	{
		try
		{
			cfg.setDirectoryForTemplateLoading(new File("./template"));
			activeCodeMailTemplate = cfg.getTemplate("activeCodeMail.ftl");
			inviteMailTemplate = cfg.getTemplate("inviteMail.ftl");
			successTemplate = cfg.getTemplate("success.ftl");
			activateAccountSuccessTemplate = cfg
			.getTemplate("activateAccountSuccess.ftl");
			failedTemplate = cfg.getTemplate("failure.ftl");
			forgetPasswordMailTemplate = cfg
			.getTemplate("forgetPasswordMail.ftl");
			forgetPasswordFormTemplate = cfg
			.getTemplate("forgetPasswordForm.ftl");
			shareMessageMailTemplate = cfg
			.getTemplate("shareMessageMail.ftl");
		}
		catch (IOException e)
		{
		}
	}

	public static String getActivateAccountSuccessOutput(String email)
	{
		try
		{
			StringWriter sw = new StringWriter();
			Map<String, Object> root = new HashMap<String, Object>();
			root.put("email", email);
			activateAccountSuccessTemplate.process(root, sw);
			return (sw.toString());
		}
		catch (Exception e)
		{
			return null;
		}
	}

	public static String getActiveCodeMailOutput(String name,String userId,
			String securityCode)
	{
		try
		{
			StringWriter sw = new StringWriter();
			Map<String, Object> root = new HashMap<String, Object>();
			root.put("name", name);
			root.put("userid", userId);
			root.put("code", securityCode);
			activeCodeMailTemplate.process(root, sw);
			return (sw.toString());
		}
		catch (Exception e)
		{
			return null;
		}
	}

	public static String getFailedOutput(String name,String operation, int code,
			String reason)
	{
		try
		{
			StringWriter sw = new StringWriter();
			Map<String, Object> root = new HashMap<String, Object>();
			root.put("name", name);
			root.put("operation", operation);
			root.put("code", code);
			root.put("reason", reason);
			failedTemplate.process(root, sw);
			return (sw.toString());
		}
		catch (Exception e)
		{
			return null;
		}
	}

	public static String getForgetPasswordFormOutput(String userid,
			String securityCode)
	{
		try
		{
			StringWriter sw = new StringWriter();
			Map<String, Object> root = new HashMap<String, Object>();
			root.put("code", securityCode);
			root.put("userid", userid);
			forgetPasswordFormTemplate.process(root, sw);
			return (sw.toString());
		}
		catch (Exception e)
		{
			return null;
		}
	}

	public static String getForgetPasswordMailOutput(String name,String userid,
			String securityCode)
	{
		try
		{
			StringWriter sw = new StringWriter();
			Map<String, Object> root = new HashMap<String, Object>();
			root.put("name", name);
			root.put("code", securityCode);
			root.put("userid", userid);
			forgetPasswordMailTemplate.process(root, sw);
			return (sw.toString());
		}
		catch (Exception e)
		{
			return null;
		}
	}

	public static String getInviteMailOutput(String inviter)
	{
		try
		{
			StringWriter sw = new StringWriter();
			Map<String, Object> root = new HashMap<String, Object>();
			root.put("inviter", inviter);
			inviteMailTemplate.process(root, sw);
			return (sw.toString());
		}
		catch (Exception e)
		{
			return null;
		}
	}

	public static String getShareMsgMailOutput(String msg, String email, String nickname)
	{
		try
		{
			StringWriter sw = new StringWriter();
			Map<String, Object> root = new HashMap<String, Object>();
			root.put("msg", msg);
			root.put("email", email);
			root.put("nickname", nickname);
			shareMessageMailTemplate.process(root, sw);
			return (sw.toString());
		}
		catch (Exception e)
		{
			return null;
		}
	}

	public static String getShareMsgSmsOutput(String title, String body, String nickname, String msgId)
	{
		if ((title != null) && !title.isEmpty())
		{
			body = title;
		}
		else if ((body == null) || body.isEmpty())
		{
			body = "快快去看我的大作";
		}

		String url = ShortUrlUtil
		.shortUrl("http://wap.baiku.cn/message.action?messageId=" + msgId);
		int lenUsed = nickname.length() + 2 + 4 + 4 + url.length();
		int lenCanUse = 67 - lenUsed;
		if (body.length() > lenCanUse)
		{
			body = body.substring(0, lenCanUse);
		}

		return nickname + "：*" + body + "*...详见" + url + "【百库】";
	}

	public static String getSuccessOutput()
	{
		try
		{
			StringWriter sw = new StringWriter();
			Map<String, Object> root = new HashMap<String, Object>();
			successTemplate.process(root, sw);
			return (sw.toString());
		}
		catch (Exception e)
		{
			return null;
		}
	}
}
