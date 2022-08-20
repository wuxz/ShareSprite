package com.zhuaiwa.dd.tool;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Map;

import me.prettyprint.hector.api.Keyspace;

import com.zhuaiwa.dd.cmd.IterateCommand;
import com.zhuaiwa.dd.cmd.ReadCommand;
import com.zhuaiwa.dd.domain.Account;
import com.zhuaiwa.dd.domain.Profile;
import com.zhuaiwa.dd.hector.HectorFactory;

public class PrintUser {
	
	private static Keyspace cassandra;

	/**
	 * @param args
	 */
	public static void main(String[] args) throws Exception {
		cassandra = HectorFactory.getKeyspace();
		IterateCommand command = new IterateCommand(cassandra);
		command.Object(Account.class);
		command.Select();
		command.Where("", 50);
		
		SimpleDateFormat sdf = new SimpleDateFormat("yyyy/MM/dd HH:mm:ss");
		
		int count = 0;
		for (Object o : command) {
			Account account = (Account) o;
			if (account.getEmail() != null && account.getEmail().endsWith("@zhuaiwa.com"))
				continue;
			count++;
			
			System.out.println("user id: " + account.getUserid());
			System.out.println("email: " + (account.getEmail() == null ? "" : account.getEmail()));
			System.out.println("phone: " + (account.getPhoneNumber() == null ? "" : account.getPhoneNumber()));
			System.out.println("register time: " + (account.getRegisterTime() == null ? "" : sdf.format(new Date(account.getRegisterTime()))));
			
			Profile profile = getProfile(account.getUserid());
			if (profile != null) {
			System.out.println("nickname: " + profile.getNickname());
			System.out.println("following count: " + (profile.getFollowingCount() == null ? 0 : profile.getFollowingCount()));
			System.out.println("follower count: " + (profile.getFollowerCount() == null ? 0 : profile.getFollowerCount()));
			}
			
			System.out.println("--------");
		}
		System.out.println("total user: " + count);
	}

	private static Profile getProfile(String userid) throws Exception {
		ReadCommand command = new ReadCommand(cassandra)
		.Object(Profile.class)
		.Select()
		.Where(userid);
		Map<String,Profile> resultmap = command.<Profile>execute();
		if (resultmap == null)
			return null;
		return resultmap.get(userid);
	}
}
