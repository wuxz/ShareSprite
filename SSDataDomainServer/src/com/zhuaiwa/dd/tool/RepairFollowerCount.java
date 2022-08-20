package com.zhuaiwa.dd.tool;

import java.util.Map;

import me.prettyprint.hector.api.Keyspace;

import org.apache.cassandra.thrift.ConsistencyLevel;

import com.zhuaiwa.dd.cmd.CreateCommand;
import com.zhuaiwa.dd.cmd.IterateCommand;
import com.zhuaiwa.dd.cmd.ReadCommand;
import com.zhuaiwa.dd.domain.Follower;
import com.zhuaiwa.dd.domain.Following;
import com.zhuaiwa.dd.domain.Profile;
import com.zhuaiwa.dd.hector.HectorFactory;

public class RepairFollowerCount {

	/**
	 * @param args
	 * @throws Exception 
	 */
	public static void main(String[] args) throws Exception {
		Keyspace cassandra = HectorFactory.getKeyspace();
		IterateCommand command_profile = new IterateCommand(cassandra);
		command_profile.Object(Profile.class);
		command_profile.Select();
		command_profile.Where("", 50);
		
		int count = 0;
		for (Object o : command_profile) {
			Profile profile = (Profile) o;
			System.out.println(profile.getUserid());
			System.out.println(profile.getNickname());
			
			int leak_following_count = profile.getFollowingCount() == null ? 0 : profile.getFollowingCount();
			int leak_follower_count = profile.getFollowerCount() == null ? 0 : profile.getFollowerCount();
			int fine_following_count = 0;
			int fine_follower_count = 0;
			
			System.out.println(leak_following_count);
			System.out.println(leak_follower_count);
			
			ReadCommand command_following =
				new ReadCommand(cassandra, ConsistencyLevel.QUORUM)
				.Object(Following.class)
				.Select()
				.Where(profile.getUserid());
			Map<String, Following> following_map = command_following.<Following>execute();
			Following following = (following_map != null ? following_map.get(profile.getUserid()) : null);
			fine_following_count = (following != null ? following.getFollowings().size() : 0);
			System.out.println("following count: " + fine_following_count);
			
			ReadCommand command_follower =
				new ReadCommand(cassandra, ConsistencyLevel.QUORUM)
				.Object(Follower.class)
				.Select()
				.Where(profile.getUserid());
			Map<String, Follower> follower_map = command_follower.<Follower>execute();
			
			Follower follower = (follower_map != null ? follower_map.get(profile.getUserid()) : null);
			fine_follower_count = (follower != null ? follower.getFollowers().size() : 0);
			System.out.println("follower count: " + fine_follower_count);
			
			if (leak_following_count != fine_following_count) {
				CreateCommand command = 
				new CreateCommand(cassandra)
				.Object(Profile.CN_PROFILE)
				.Where(profile.getUserid())
				.Insert(Profile.FN_FOLLOWING_COUNT, fine_following_count);
				command.execute();
				System.out.println("##################################################");
			}
			if (leak_follower_count != fine_follower_count) {
				CreateCommand command = 
				new CreateCommand(cassandra)
				.Object(Profile.CN_PROFILE)
				.Where(profile.getUserid())
				.Insert(Profile.FN_FOLLOWER_COUNT, fine_follower_count);
				command.execute();
				System.out.println("@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@");
			}
			
			System.out.println("----");
			count++;
		}
		System.out.println("total:" + count);
	}

}
