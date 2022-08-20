package com.zhuaiwa.session.search;

import java.util.Map;

import javax.jms.JMSException;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.zhuaiwa.api.Common.SSAccount;
import com.zhuaiwa.api.Common.SSMessage;
import com.zhuaiwa.api.Common.SSProfile;
import com.zhuaiwa.dd.cmd.IterateCommand;
import com.zhuaiwa.dd.cmd.ReadCommand;
import com.zhuaiwa.dd.domain.Account;
import com.zhuaiwa.dd.domain.Profile;
import com.zhuaiwa.dd.hector.HectorFactory;
import com.zhuaiwa.dd.service.impl.DataServiceImpl;
import com.zhuaiwa.session.search.domain.Message;
import com.zhuaiwa.session.search.domain.Operate;
import com.zhuaiwa.session.search.domain.SolrObj;
import com.zhuaiwa.session.search.domain.User;

/**
 * 索引接口
 * 
 * @author tangjun
 */
public class Indexer {
	static class IndexHolder {
		static Indexer instance = new Indexer();
	}

	private static Logger LOG = LoggerFactory.getLogger(Indexer.class);

	public static Indexer getInstance() {
		return IndexHolder.instance;
	}
	
	private static Profile getProfile(String userid)
			throws Exception {
		ReadCommand command = new ReadCommand(HectorFactory.getKeyspace()).Object(Profile.class)
				.Select().Where(userid);
		Map<String, Profile> resultmap = command.<Profile> execute();
		if (resultmap == null) {
			return null;
		}
		return resultmap.get(userid);
	}

	public static void main(String[] args) throws Exception {
		if (args.length == 0) {
			System.out.println("Usage:Indexer updateUser [userId]|updateMessage [msgId]");
			return;
		} else {
			if (args[0].equals("updateUser")) {
				if(args.length > 1){
					String userId = args[1];
					updateUser(userId);
				}else{
					System.out.println("start to update user...");
					updateUser();
					System.out.println("user updated.");
				}
			} else if (args[0].equals("updateMessage")) {
				if(args.length > 1){
					String msgId = args[1];
					updateMessage(msgId);
				}else{
					System.out.println("start to update message...");
					updateMessage();
					System.out.println("message updated.");
				}
			}
		}
	}
	
	private static void updateMessage(String msgId) throws Exception{
		ReadCommand command = new ReadCommand(HectorFactory.getKeyspace()).Object(Message.class).Select().Where(msgId);
		com.zhuaiwa.dd.domain.Message m = command.<com.zhuaiwa.dd.domain.Message>execute().get(msgId);
		if ((m.getShareType() == 1) && (m.getMsgType() == 1)) {// 公开分享的内容消息
			System.out.println(m.getMsgid());
			SSMessage.Builder mb = SSMessage.newBuilder();
			DataServiceImpl.build(mb, m);
			Indexer.getInstance().updateMessage(mb.build());
		}
	}

	private static void updateMessage() throws Exception {
		IterateCommand command = new IterateCommand(HectorFactory.getKeyspace());
		command.Object(com.zhuaiwa.dd.domain.Message.class);
		command.Select();
		command.Where("", 300);
		
		Speed speed = new Speed();
		Thread s = new Thread(new Speed());
		s.setDaemon(true);
		s.start();
		
		int pubCount = 0;
		for (Object o : command) {
			com.zhuaiwa.dd.domain.Message m = (com.zhuaiwa.dd.domain.Message) o;
			if ((m.getShareType() == 1) && (m.getMsgType() == 1)) {// 公开分享的内容消息
//				System.out.println(m.getMsgid());
				SSMessage.Builder mb = SSMessage.newBuilder();
				DataServiceImpl.build(mb, m);
//				Indexer.getInstance().addMessage(mb.build());
				Indexer.getInstance().updateMessage(mb.build());
				pubCount++;
			}
			speed.count(1);
		}
		System.out.println("total message:" + speed.getCount() + ",pub message:"
				+ pubCount);
	}
	
	private static void updateUser(String userId) throws Exception{
		ReadCommand command = new ReadCommand(HectorFactory.getKeyspace()).Object(Account.class).Select().Where(userId);
		Account account = command.<Account>execute().get(userId);
		if ((account.getEmail() != null)
				&& account.getEmail().endsWith("@zhuaiwa.com")) {
			return;
		}

		System.out.println("user id: " + account.getUserid());
		if (account.getUserid() == null) {
			return;
		}
		Profile profile = getProfile(account.getUserid());
		if (profile != null) {
			System.out.println("nickname: " + profile.getNickname());
			SSAccount.Builder ab = SSAccount.newBuilder();
			DataServiceImpl.build(ab, account);
			SSProfile.Builder pb = SSProfile.newBuilder();
			DataServiceImpl.build(pb, profile);
			Indexer.getInstance().updateUser(ab.build(), pb.build());
		}
	}

	private static void updateUser() throws Exception {
		/**
		Cassandra.Client cassandra;
		TSocket socket = new TSocket(DataDomainHost, DataDomainPort);
		socket.open();
		cassandra = new Cassandra.Client(new TBinaryProtocol(socket));
		IterateCommand command = new IterateCommand(cassandra);
		command.Object(Account.class);
		command.Select();
		command.Where("", 300);
		**/
		
		IterateCommand command = new IterateCommand(HectorFactory.getKeyspace());
		command.Object(Account.class);
		command.Select();
		command.Where("", 100);

		Speed speed = new Speed();
		Thread s = new Thread(new Speed());
		s.setDaemon(true);
		s.start();
		
		for (Object o : command) {
			Account account = (Account) o;
			if ((account.getEmail() != null)
					&& account.getEmail().endsWith("@zhuaiwa.com")) {
				continue;
			}
			speed.count(1);

			//System.out.println("user id: " + account.getUserid());
			// System.out.println("email: " + (account.getEmail() == null ? "" :
			// account.getEmail()));
			// System.out.println("phone: " + (account.getPhoneNumber() == null
			// ? "" : account.getPhoneNumber()));
			// System.out.println("register time: " + (account.getRegisterTime()
			// == null ? "" : sdf.format(new Date(account.getRegisterTime()))));
			if (account.getUserid() == null) {
				continue;
			}
			Profile profile = getProfile(account.getUserid());
			if (profile != null) {
				//System.out.println("nickname: " + profile.getNickname());
				// System.out.println("following count: " +
				// (profile.getFollowingCount() == null ? 0 :
				// profile.getFollowingCount()));
				// System.out.println("follower count: " +
				// (profile.getFollowerCount() == null ? 0 :
				// profile.getFollowerCount()));
				SSAccount.Builder ab = SSAccount.newBuilder();
				DataServiceImpl.build(ab, account);
				SSProfile.Builder pb = SSProfile.newBuilder();
				DataServiceImpl.build(pb, profile);
				// Indexer.getInstance().addUser(ab.build(), pb.build());
				Indexer.getInstance().updateUser(ab.build(), pb.build());
			}

			//System.out.println("--------");
		}
		System.out.println("total user: " + speed.getCount());
	}

	private Indexer() {

	}

	private void addMessage(SSMessage ssmessage) {
		index(Operate.Add, new Message(ssmessage.getMsgid(), ssmessage));
	}

	public void addMessage(String msgid, SSMessage ssmessage) {
		index(Operate.Add, new Message(msgid, ssmessage));
	}

	/**
	 * 激活用户
	 * 
	 * @param ssa
	 * @param ssp
	 * @throws Exception
	 */
	public void addUser(SSAccount ssa, SSProfile ssp) {
		if (ssa == null) {
			LOG.error("account is null");
			return;
		}
		if (ssp == null) {
			LOG.error("profile is null");
			return;
		}
		User user = new User();
		user.from(ssa, ssp);
		index(Operate.Add, user);
	}

	public void index(Operate operate, SolrObj obj) {
		try {
			MessageSender.getInstance(false).send(operate, obj);
		} catch (JMSException e) {
			LOG.error("index exception", e);
		}
	}

	public void removeMessage(String msgid) {
		index(Operate.Delete, new Message(msgid));
	}

	public void removeUser(String userid) {
		index(Operate.Delete, new User(userid));
	}

	public void updateMessage(SSMessage ssmessage) {
		index(Operate.Modify, new Message(ssmessage));
	}

	/**
	 * 修改账号(含绑定或者取消绑定)或者个人资料
	 * 
	 * @param ssa
	 * @param ssp
	 * @throws Exception
	 */
	public void updateUser(SSAccount ssa, SSProfile ssp) {
		if (ssa == null) {
			LOG.error("account is null");
			return;
		}
		if (ssp == null) {
			LOG.error("profile is null");
			return;
		}
		User user = new User();
		user.from(ssa, ssp);
		index(Operate.Modify, user);
	}

}
