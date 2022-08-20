package com.zhuaiwa.session.search;

import java.io.BufferedReader;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStreamReader;

import javax.jms.Connection;
import javax.jms.Destination;
import javax.jms.JMSException;
import javax.jms.MessageProducer;
import javax.jms.ObjectMessage;
import javax.jms.Session;

import org.apache.activemq.ActiveMQConnectionFactory;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.zhuaiwa.session.search.domain.Operate;
import com.zhuaiwa.session.search.domain.SolrObj;
import com.zhuaiwa.session.search.domain.User;
import com.zhuaiwa.util.PropertiesHelper;

// import partake.core.util.UuidUtil;

/**
 * 发送JMS消息
 */
public class MessageSender
{
	static class MessageSenderHolder
	{
		static MessageSender instance = new MessageSender();
	}

	private static String brokerURL = PropertiesHelper.getValue("brokerURL",
			"tcp://search.baiku.cn:61616");

	static Logger LOG = LoggerFactory.getLogger(MessageSender.class);

	private static String password = "";

	private static String queueName = PropertiesHelper.getValue("queueName",
			"zhuaiwa");

	private static String userName = "";
	static
	{
		System.out.println("MessageSender:: brokerURL:" + brokerURL
				+ ", queueName:" + queueName);
	}

	public static synchronized MessageSender getInstance(boolean reconnect)
	{
		if (reconnect)
		{
			MessageSenderHolder.instance.init();
		}
		return MessageSenderHolder.instance;
	}

	public static void main(String[] args) throws IOException
	{
		MessageSender sender;
		try
		{
			sender = MessageSender.getInstance(false);
			String file = "d:/1.txt";
			InputStreamReader read = new InputStreamReader(new FileInputStream(
					file));

			BufferedReader bufferedReader = new BufferedReader(read);
			String lineTXT = null;
			while ((lineTXT = bufferedReader.readLine()) != null)
			{
				System.out.println(lineTXT);
				if ((lineTXT == null) || lineTXT.isEmpty())
				{
					continue;
				}
				String[] values = lineTXT.split(",");
				String nickname = values[1];
				String id = values[0];
				User user = new User();
				user.setId(id);
				user.setNickname(nickname);

				long now = System.currentTimeMillis();
				for (int i = 0; i < 1000; i++)
				{
					sender.send(Operate.Modify, user);
				}
				System.out.println(System.currentTimeMillis() - now);
			}
			read.close();
			sender.destroy();
			System.out.println("sended.");
		}
		catch (JMSException e)
		{
			e.printStackTrace();
		}
	}

	private Connection connection = null;

	private MessageProducer producer = null;

	private Session session = null;

	private MessageSender()
	{
		init();
	}

	public void destroy()
	{
		try
		{
			if (null != producer)
			{
				producer.close();
			}
		}
		catch (JMSException e)
		{
			e.printStackTrace();
		}
		try
		{
			if (null != session)
			{
				session.close();
			}
		}
		catch (JMSException e)
		{
			e.printStackTrace();
		}
		try
		{
			if (null != connection)
			{
				connection.close();
			}
		}
		catch (JMSException e)
		{
			e.printStackTrace();
		}
	}

	protected synchronized void init()
	{
		try
		{
			destroy();

			ActiveMQConnectionFactory connectionFactory = new ActiveMQConnectionFactory(
					userName, password, brokerURL);
			connection = connectionFactory.createConnection();
			connection.start();
			session = connection.createSession(false, Session.AUTO_ACKNOWLEDGE);
			Destination destination = session.createQueue(queueName);
			producer = session.createProducer(destination);
			// producer.setDeliveryMode(DeliveryMode.NON_PERSISTENT);
		}
		catch (JMSException e)
		{
			LOG.error("init error", e);
		}
	}

	public void send(Operate operate, SolrObj obj) throws JMSException
	{
		ObjectMessage message = session.createObjectMessage(obj);
		message.setJMSType(operate.ordinal() + "");
		producer.send(message);
	}
}
