package com.zhuaiwa.session.search;

import java.io.IOException;
import java.net.MalformedURLException;
import java.util.List;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.LinkedBlockingQueue;
import java.util.concurrent.ScheduledThreadPoolExecutor;
import java.util.concurrent.ThreadPoolExecutor;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicInteger;

import javax.jms.Connection;
import javax.jms.Destination;
import javax.jms.JMSException;
import javax.jms.MessageConsumer;
import javax.jms.ObjectMessage;
import javax.jms.Session;

import org.apache.activemq.ActiveMQConnectionFactory;
import org.apache.solr.client.solrj.SolrServerException;
import org.apache.solr.client.solrj.impl.CommonsHttpSolrServer;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.zhuaiwa.session.search.domain.Education;
import com.zhuaiwa.session.search.domain.Message;
import com.zhuaiwa.session.search.domain.Operate;
import com.zhuaiwa.session.search.domain.SolrObj;
import com.zhuaiwa.session.search.domain.SolrObj.SolrObjType;
import com.zhuaiwa.session.search.domain.User;
import com.zhuaiwa.session.search.domain.WorkExpirence;
import com.zhuaiwa.util.PropertiesHelper;

/**
 * 接收JMS消息，调用Solr进行索引操作
 * 
 * @author tangjun
 * 
 */
public class MessageReceiver {
	private static final Logger LOG = LoggerFactory.getLogger(MessageReceiver.class);

	// activeMQ setting
	private static String userName = "";
	private static String password = "";
	private static String brokerURL = PropertiesHelper.getValue("brokerURL", "tcp://search.baiku.cn:61616");
	private static String queueName = PropertiesHelper.getValue("queueName", "zhuaiwa");

	// solr setting
	private static String solrURL = PropertiesHelper.getValue("solrURL", "http://search.baiku.cn:8090/solr");
	private static CommonsHttpSolrServer client;

	// stats
	private static ConcurrentHashMap<String, AtomicInteger> stats = new ConcurrentHashMap<String, AtomicInteger>();

	static {
		System.out.println("MessageReceiver:: brokerURL:" + brokerURL + ", queueName:" + queueName + ", solrURL:"
				+ solrURL);
		try {
			client = new CommonsHttpSolrServer(solrURL);
			client.setConnectionTimeout(1000);
			client.setDefaultMaxConnectionsPerHost(10);
			client.setMaxTotalConnections(10);
		} catch (MalformedURLException e) {
			LOG.error("", e);
		}
	}

	private static void count(String key) {
		AtomicInteger c = stats.get(key);
		if (c == null) {
			AtomicInteger alt = new AtomicInteger(0);
			c = stats.putIfAbsent(key, alt);
			if (c == null) {
				c = alt;
			}
		}
		c.addAndGet(1);
	}

	public static class Indexer implements Runnable {
		private Operate operate;
		private SolrObj obj;
		private String tip;
		private String key;

		public Indexer(Operate operate, SolrObj obj) {
			this.operate = operate;
			this.obj = obj;
			tip = operate.name() + " " + SolrObjType.values()[obj.getObjType()].name() + ":" + obj.getId();
			key = SolrObjType.values()[obj.getObjType()].name();
		}

		private void add() {
			try {
				if (obj.getObjType() == SolrObjType.MESSAGE.ordinal()) {
					Message c = (Message) obj;
					client.addBean(c);
				} else if (obj.getObjType() == SolrObjType.USER.ordinal()) {
					User user = (User) obj;
					client.addBean(user);
					List<Education> eduList = user.getEducations();
					if (eduList != null) {
						client.addBeans(eduList);
					}

					List<WorkExpirence> workList = user.getWorkExpirences();
					if (workList != null) {
						client.addBeans(workList);
					}
				}
				count(key + "_add_success");
				LOG.debug(tip + " added。");
			} catch (Exception e) {
				count(key + "_add_error");
				LOG.error(tip + " add failed。", e);
			}
		}

		private void delete() {
			try {
				client.deleteById(obj.getId());
				if (obj.getObjType() == SolrObjType.USER.ordinal()) {
					String query = "<delete><query>";
					query += "(userId:" + obj.getId() + ")AND((objType:" + SolrObj.SolrObjType.EDUCATION.ordinal()
							+ ")OR(objType:" + SolrObjType.WORK.ordinal() + "))";
					query += "</query></delete> ";//
					client.deleteByQuery(query);
				}
				count(key + "_delete_success");
				LOG.debug(tip + " deleted。");
			} catch (Exception e) {
				count(key + "_delete_error");
				LOG.error(tip + " delete failed。", e);
			}
		}

		@Override
		public void run() {
			LOG.debug(tip);
			count(operate.name());
			if (operate.equals(Operate.Add)) {
				add();
			} else if (operate.equals(Operate.Delete)) {
				delete();
			} else if (operate.equals(Operate.Modify)) {
				delete();
				add();
			}
		}

	}

	public void receive() {
		LOG.info("MessageReceiver started.");
		final ThreadPoolExecutor threadPoolExecutor = new ThreadPoolExecutor(5, 10, 5, TimeUnit.MINUTES,
				new LinkedBlockingQueue<Runnable>());

		ScheduledThreadPoolExecutor scheduledThreadPoolExecutor = new ScheduledThreadPoolExecutor(1);
		scheduledThreadPoolExecutor.scheduleAtFixedRate(new Runnable() {

			@Override
			public void run() {
				LOG.debug("start to commit index ...");
				try {
					client.commit();
				} catch (SolrServerException e) {
					e.printStackTrace();
				} catch (IOException e) {
					e.printStackTrace();
				}
				LOG.debug("index commit.");

				// print stats
				StringBuffer sb = new StringBuffer();
				for (String key : stats.keySet()) {
					sb.append(key + ":" + stats.get(key) + ", ");
				}
				LOG.info("stats:" + sb.toString());

				// print threadPoolExecutor status
				LOG.info("threadPoolExecutor Active:" + threadPoolExecutor.getActiveCount() + ", Completed:"
						+ threadPoolExecutor.getCompletedTaskCount() + ", Task:" + threadPoolExecutor.getTaskCount()
						+ ", Queue size:" + threadPoolExecutor.getQueue().size());
			}
		}, 1, 1, TimeUnit.MINUTES);

		while (true) {
			Connection connection = null;
			Session session = null;
			MessageConsumer consumer = null;
			try {
				ActiveMQConnectionFactory connectionFactory = new ActiveMQConnectionFactory(userName, password,
						brokerURL);
				connection = connectionFactory.createConnection();
				connection.start();
				session = connection.createSession(false, Session.AUTO_ACKNOWLEDGE);
				Destination destination = session.createQueue(queueName);
				consumer = session.createConsumer(destination);
				while (true) {
					javax.jms.Message message = consumer.receive(1000);
					if (null != message) {
						count("receive_message");
						LOG.debug("receive a message.msg type:" + message.getJMSType() + ", msg class:"
								+ ((ObjectMessage) message).getObject().getClass().getName());
						try {
							int type = Integer.parseInt(message.getJMSType());
							if (type > -1 && type < Operate.values().length) {
								Operate operate = Operate.values()[type];
								if (message instanceof ObjectMessage) {
									ObjectMessage om = (ObjectMessage) message;
									if (om.getObject() instanceof SolrObj) {
										SolrObj solrObj = (SolrObj) om.getObject();
										threadPoolExecutor.execute(new Indexer(operate, solrObj));
										count("start_indexer");
									} else {
										LOG.error("ObjectMessage is not instance of SolrObj.");
									}
								} else {
									LOG.error("Message is not instance of ObjectMessage.");
								}
							} else {
								LOG.debug("type:" + type);
							}
						} catch (Exception e) {
							LOG.error("invaild message", e);
						}
					} else {
						try {
							Thread.sleep(10);
						} catch (Exception e) {
							LOG.error("", e);
						}
					}
				}
			} catch (Exception e) {
				LOG.error("", e);
			} finally {
				try {
					if (null != consumer)
						consumer.close();
				} catch (JMSException e) {
					LOG.error("", e);
				}
				try {
					if (null != session)
						session.close();
				} catch (JMSException e) {
					LOG.error("", e);
				}
				try {
					if (null != connection)
						connection.close();
				} catch (JMSException e) {
					LOG.error("", e);
				}
			}
		}
	}

	public static void deleteMessage() throws Exception {
		// String query = "<delete><query>";
		// query += "created:[* TO 2011-09-25\\T00:00:00\\Z]";
		// query += "</query></delete>";
		String query = "created:[* TO 2012-09-25\\T00:00:00\\Z]";
		// created:[1995-12-31\T23:59:59.999\Z TO 2011-09-25\T00:00:00\Z]
		client.deleteByQuery(query);
	}

	public static void main(String[] args) {
		LOG.info("MessageReceiver starting......");
		MessageReceiver receiver = new MessageReceiver();
		receiver.receive();
	}
}
