package com.zhuaiwa.util;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.UnsupportedEncodingException;
import java.util.Properties;

import javax.mail.BodyPart;
import javax.mail.Message;
import javax.mail.MessagingException;
import javax.mail.NoSuchProviderException;
import javax.mail.Session;
import javax.mail.Transport;
import javax.mail.internet.AddressException;
import javax.mail.internet.InternetAddress;
import javax.mail.internet.MimeBodyPart;
import javax.mail.internet.MimeMessage;
import javax.mail.internet.MimeMultipart;
import javax.mail.internet.MimeUtility;

public class MailUtil
{

	public static class Mail implements java.io.Serializable
	{

		private static final long serialVersionUID = -5220383900308600L;

		private String body = null;

		private String cc = null;

		private String from = null;

		private String fromName = null;

		private boolean isTxtFormat = false;

		private String subject = null;

		private String to = null;

		public Mail()
		{
			super();
		}

		public Mail(String from, String to, String subject, String body,
				boolean isTxtFormat, String cc)
		{
			super();
			this.from = from;
			this.to = to;
			this.subject = subject;
			this.body = body;
			this.isTxtFormat = isTxtFormat;
			this.cc = cc;
		}

		public String getBody()
		{
			return body;
		}

		public String getCc()
		{
			return cc;
		}

		public String getFrom()
		{
			return from;
		}

		public String getFromName()
		{
			return fromName;
		}

		public String getSubject()
		{
			return subject;
		}

		public String getTo()
		{
			return to;
		}

		public boolean isTxtFormat()
		{
			return isTxtFormat;
		}

		public void setBody(String body)
		{
			this.body = body;
		}

		public void setCc(String cc)
		{
			this.cc = cc;
		}

		public void setFrom(String from)
		{
			this.from = from;
		}

		public void setFromName(String fromName)
		{
			this.fromName = fromName;
		}

		public void setSubject(String subject)
		{
			this.subject = subject;
		}

		public void setTo(String to)
		{
			this.to = to;
		}

		public void setTxtFormat(boolean isTxtFormat)
		{
			this.isTxtFormat = isTxtFormat;
		}

	}

	public static class MailException extends Exception
	{

		private static final long serialVersionUID = 8907069088881883542L;

		public MailException()
		{
		}

		public MailException(String message)
		{
			super(message);
		}

		public MailException(String message, Throwable cause)
		{
			super(message, cause);
		}

		public MailException(Throwable cause)
		{
			super(cause);
		}

	}

	public static class SentMailThread extends Thread
	{
		Mail mail;

		public SentMailThread(Mail mail)
		{
			this.mail = mail;
		}

		@Override
		public void run()
		{
			try
			{
				new MailUtil().sendMail(mail);
			}
			catch (Exception e)
			{
				e.printStackTrace();
			}
		}
	}

	/**
	 * @param args
	 */
	public static void main(String[] args)
	{
		Mail mail = new Mail();
		mail.setBody("body");
		// mail.setFrom("test@feedss.com");
		mail.setFrom("jira-qn@channelsoft.com");
		mail.setSubject("subject");
		mail.setTo("wuxz@channelsoft.com");

		MailUtil util = new MailUtil();
		try
		{
			util.sendMail(mail);
		}
		catch (MailException e)
		{
			e.printStackTrace();
		}

	}

	public static String readFile(File file)
	{
		StringBuffer sb = new StringBuffer();
		try
		{
			InputStreamReader isr = new FileReader(file);
			BufferedReader br = new BufferedReader(isr);
			String line = null;
			while (null != (line = br.readLine()))
			{
				sb.append(line);
			}
			br.close();
			isr.close();
		}
		catch (FileNotFoundException e)
		{
			e.printStackTrace();
		}
		catch (IOException e)
		{
			e.printStackTrace();
		}
		return sb.toString();
	}

	public static void sendMessage(String to, String subject, String body)
	{
		if (body == null)
		{
			return;
		}
		Mail mail = new Mail();
		mail.setTxtFormat(false);
		mail.setBody(body);
		mail.setFrom(PropertiesHelper.getValue("smtp.user",
				"jira-qn@channelsoft.com"));
		mail.setFromName("百库分享");
		mail.setSubject(subject);
		mail.setTo(to);

		Thread smt = new Thread(new SentMailThread(mail));
		smt.setDaemon(true);
		smt.start();
	}

	public MailUtil()
	{
	}

	public boolean sendMail(Mail mail) throws MailException
	{
		String smtpHost = PropertiesHelper.getValue("smtp.host",
				"mail.channelsoft.com");
		String smtpUser = PropertiesHelper.getValue("smtp.user", "jira-qn");
		String smtpPassword = PropertiesHelper.getValue("smtp.password",
				"qctest");
		String smtpAuth = PropertiesHelper.getValue("smtp.auth", "true");
		Properties properties = new Properties();
		properties.put("mail.smtp.host", smtpHost);
		properties.put("mail.smtp.auth", smtpAuth);
		Session session = Session.getInstance(properties, null);
		MimeMessage mimeMessage = new MimeMessage(session);

		try
		{
			InternetAddress fromAddress = new InternetAddress(mail.getFrom());
			if (mail.getFromName() != null)
			{
				fromAddress.setPersonal(mail.getFromName(), "utf-8");
			}
			mimeMessage.setFrom(fromAddress);
			InternetAddress toAddress = new InternetAddress(mail.getTo());
			mimeMessage.addRecipient(Message.RecipientType.TO, toAddress);
			if (null != mail.getCc())
			{
				InternetAddress ccAddress = new InternetAddress(mail.getCc());
				// mimeMessage.addRecipient(Message.RecipientType.CC,
				// ccAddress);
				mimeMessage.addRecipient(Message.RecipientType.TO, ccAddress);
			}
			mimeMessage.setSubject(MimeUtility.encodeText(mail.getSubject(),
					"utf-8", "B"));
			if (mail.isTxtFormat())
			{
				mimeMessage.setText(mail.getBody());
			}
			else
			{
				MimeMultipart mimeMultipart = new MimeMultipart();
				BodyPart bodyPart = new MimeBodyPart();
				bodyPart.setContent(mail.getBody(), "text/html;charset=utf-8");
				mimeMultipart.setSubType("related");
				mimeMultipart.addBodyPart(bodyPart);
				mimeMessage.setContent(mimeMultipart);
			}
			Transport transport = session.getTransport("smtp");
			transport.connect(smtpHost, smtpUser, smtpPassword);
			transport.sendMessage(mimeMessage,
					mimeMessage.getRecipients(Message.RecipientType.TO));
			transport.close();
			return true;
		}
		catch (AddressException e)
		{
			throw new MailException(e.getMessage(), e);
		}
		catch (UnsupportedEncodingException e)
		{
			throw new MailException(e.getMessage(), e);
		}
		catch (NoSuchProviderException e)
		{
			throw new MailException(e.getMessage(), e);
		}
		catch (MessagingException e)
		{
			throw new MailException(e.getMessage(), e);
		}
	}

}
