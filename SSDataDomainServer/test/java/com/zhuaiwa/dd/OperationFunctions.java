package com.zhuaiwa.dd;

import java.io.BufferedReader;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileReader;
import java.net.InetSocketAddress;
import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.Executors;

import org.jboss.netty.channel.socket.nio.NioClientSocketChannelFactory;

import com.google.protobuf.ByteString;
import com.zhuaiwa.api.Common.SSAccount;
import com.zhuaiwa.api.Common.SSGender;
import com.zhuaiwa.api.Common.SSProfile;
import com.zhuaiwa.api.Rpc.FollowRequest;
import com.zhuaiwa.api.Rpc.GetFollowerRequest;
import com.zhuaiwa.api.Rpc.GetFollowerResponse;
import com.zhuaiwa.api.Rpc.SetProfileRequest;
import com.zhuaiwa.api.Rpc.UnfollowRequest;
import com.zhuaiwa.api.SSDataDomain.CreateAccountRequest;
import com.zhuaiwa.api.SSDataDomain.SSDataDomainSvc;
import com.zhuaiwa.api.SSDataDomain.SSDataDomainSvc.BlockingInterface;
import com.zhuaiwa.api.netty.NettyRpcController;
import com.zhuaiwa.api.netty.client.NettyRpcChannel;
import com.zhuaiwa.api.netty.client.NettyRpcClient;

public class OperationFunctions {
	NettyRpcClient client = null;
	NettyRpcChannel channel = null;
	BlockingInterface api = null;

	public OperationFunctions() {
		client = new NettyRpcClient(new NioClientSocketChannelFactory(Executors.newCachedThreadPool(),
				Executors.newCachedThreadPool()));
		 channel = client.blockingConnect(new
		 InetSocketAddress("59.151.117.234", 49161));//生产环境
//		channel = client.blockingConnect(new InetSocketAddress("59.151.117.233", 29163));// 测试环境
		api = SSDataDomainSvc.newBlockingStub(channel);
	}

	private byte[] getRandomProfile(boolean isMan) throws Exception {
		StringBuffer iconFilePath = new StringBuffer();
		iconFilePath.append("D:\\tmp\\头像\\");
		if (isMan) {
			iconFilePath.append("boy");
		} else {
			iconFilePath.append("girl");
		}
		DecimalFormat df = new DecimalFormat("00");
		iconFilePath.append(df.format(new java.util.Random().nextInt(10) + 1));
		
		iconFilePath.append(".jpg");

		FileInputStream fis = new FileInputStream(new File(iconFilePath.toString()));
		ByteArrayOutputStream baos = new ByteArrayOutputStream();
		byte[] buffer = new byte[1024];
		int size = 0;
		while ((size = fis.read(buffer)) != -1) {
			baos.write(buffer, 0, size);
		}
		return baos.toByteArray();
	}

	public void createUser(String email, String password, String nickname, boolean isMan) throws Exception {
		System.out.println("add " + email + " " + nickname);
		CreateAccountRequest request = CreateAccountRequest.newBuilder().setEmail(email)
		// .setUserid(getUserId())
				.build();
		com.zhuaiwa.api.SSDataDomain.CreateAccountResponse resp = api.createAccount(new NettyRpcController(), request);
		resp.getUserid();
		
		SSAccount account = SSAccount.newBuilder().setIsActive(1).setUserid(resp.getUserid()).setPassword(password).build();
		api.setAccount(new NettyRpcController(),com.zhuaiwa.api.SSDataDomain.SetAccountRequest.newBuilder().setAccount(account).setUserid(resp.getUserid()).build());
		SSGender g;
		ByteString icon;
		if (isMan) {
			g = SSGender.GENDER_MALE;
		} else {
			g = SSGender.GENDER_FEMAIL;
		}
		icon = ByteString.copyFrom(getRandomProfile(isMan));
		SetProfileRequest req = SetProfileRequest
				.newBuilder().setUserid(resp.getUserid())
				.setProfile(SSProfile.newBuilder().setGender(g).setIcon(icon).setNickname(nickname).build()).build();
		api.setProfile(new NettyRpcController(), req);
		System.out.println("add ok " + email + " " + nickname);
	}

	public void createRelation(String userid,List<String> userids) throws Exception{
//		api.follow(new NettyRpcController(), com.zhuaiwa.api.SSDataDomain.FollowRequest.newBuilder().setFollowerUserid(userid).addAllFollowingUseridList(userids).build());
		for(String u : userids){
			api.follow(new NettyRpcController(), FollowRequest.newBuilder().setFollowerUserid(u).addFollowingUseridList(userid).build());
			System.out.println(u + " follow " + userid);
		}
	}
	
	public void unFollow(String userid,List<String> userids) throws Exception{
		api.unfollow(new NettyRpcController(), UnfollowRequest.newBuilder().setFollowerUserid(userid).addAllFollowingUseridList(userids).build());
	}
	
	public String getUserId(String email) throws Exception{
		com.zhuaiwa.api.SSDataDomain.GetAccountResponse resp = api.getAccount(new NettyRpcController(), com.zhuaiwa.api.SSDataDomain.GetAccountRequest.newBuilder().addEmail(email).build());
		return resp.getAccount(0).getUserid();
	}
	
	public void queryRelation(String userid) throws Exception{
		GetFollowerResponse resp = api.getFollower(new NettyRpcController(), GetFollowerRequest.newBuilder().setUserid(userid).setCount(10000).build());
		System.out.println(resp.getFollowerUseridListCount());
	}
	
	/**
	 * @param args
	 */
	public static void main(String[] args) throws Exception {
		OperationFunctions funcs = new OperationFunctions();
		FileReader fr = new FileReader("D:\\tmp\\ID名字.csv");
		BufferedReader br = new BufferedReader(fr);
		String line = null;
		List<String> userids = new ArrayList<String>();
		
		while ((line = br.readLine()) != null) {
			String[] userinfo = line.split(",");
			if (userinfo.length == 3) {
//				boolean isMan = false;
//				if (userinfo[1].contains("男")) {
//					isMan = true;
//				}
//				funcs.createUser(userinfo[2], org.apache.commons.codec.digest.DigestUtils.md5Hex("123456"), userinfo[0], isMan);
				
				userids.add(funcs.getUserId(userinfo[2]));
			}
		}
		
//		for(String userid : userids){
//			System.out.println(userid);
//		}
		
		funcs.createRelation(funcs.getUserId("baiku2018@126.com"), userids.subList(208, 300));
//		funcs.unFollow(funcs.getUserId("fangxia@channelsoft.com"), userids);
//		funcs.queryRelation(funcs.getUserId("fangxia@channelsoft.com"));
		
		
//		System.out.println(org.apache.commons.codec.digest.DigestUtils.md5Hex("tangjun"));
	}

}
