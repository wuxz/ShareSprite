package com.zhuaiwa.session;

import static org.junit.Assert.fail;

import java.net.InetSocketAddress;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.Executors;

import org.jboss.netty.channel.socket.nio.NioClientSocketChannelFactory;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;

import com.google.protobuf.ServiceException;
import com.zhuaiwa.api.Common.SSBOX;
import com.zhuaiwa.api.Common.SSId;
import com.zhuaiwa.api.Common.SSMessage;
import com.zhuaiwa.api.Common.SSMessageType;
import com.zhuaiwa.api.Common.SSPerson;
import com.zhuaiwa.api.Common.SSResultCode;
import com.zhuaiwa.api.Common.SSShareType;
import com.zhuaiwa.api.Rpc.UnbindAccountRequest;
import com.zhuaiwa.api.SSSessionDomain.LoginRequest;
import com.zhuaiwa.api.SSSessionDomain.RegisterAccountRequest;
import com.zhuaiwa.api.SSSessionDomain.RegisterAccountResponse;
import com.zhuaiwa.api.SSSessionDomain.SSSessionDomainSvc;
import com.zhuaiwa.api.netty.NettyRpcController;
import com.zhuaiwa.api.netty.client.NettyRpcChannel;
import com.zhuaiwa.api.netty.client.NettyRpcClient;
import com.zhuaiwa.api.util.FutureResponse;
import com.zhuaiwa.api.util.SSIdUtils;

public class SSSessionDomainSvc_v_1_0Test
{
	private static NettyRpcChannel channel;

	private static SSId ssid = SSId.newBuilder()
			.setDomain(SSIdUtils.SSIdDomain.email.name())
			.setId("tangjun@channelsoft.com").build();

	private static SSId userid = SSId.newBuilder()
			.setDomain(SSIdUtils.SSIdDomain.userid.name())
			.setId("e871ca996626f239637e0fc2e7019e9c").build();

	private com.zhuaiwa.api.SSSessionDomain.SSSessionDomainSvc.BlockingInterface sessionDomainBlockingSvc;

	private com.zhuaiwa.api.SSSessionDomain.SSSessionDomainSvc.Interface sessionDomainSvc;

	@Before
	public void setUp() throws Exception
	{
		SSSessionDomainServer.registerSessionDomainApiProtobufExtension();

		NettyRpcClient client = new NettyRpcClient(
				new NioClientSocketChannelFactory(
						Executors.newCachedThreadPool(),
						Executors.newCachedThreadPool()));
		channel = client.blockingConnect(new InetSocketAddress("10.130.29.237",
				8000));
//		channel = client.blockingConnect(new InetSocketAddress("localhost",
//				8000));
		sessionDomainSvc = SSSessionDomainSvc.newStub(channel);
		sessionDomainBlockingSvc = SSSessionDomainSvc.newBlockingStub(channel);

		// NettyRpcClient client = new NettyRpcClient(new
		// NioClientSocketChannelFactory(Executors.newCachedThreadPool(),
		// Executors.newCachedThreadPool()),true);
		// channel = client.blockingConnect(new
		// InetSocketAddress("59.151.117.236", 8001));
		// sessionDomainSvc = SSSessionDomainSvc.newStub(channel);
		// sessionDomainBlockingSvc =
		// SSSessionDomainSvc.newBlockingStub(channel);
	}

	@After
	public void tearDown() throws Exception
	{

	}

	@Test
	public void testActivateAccount()
	{
		fail("Not yet implemented");
	}

	@Test
	public void testAddContact()
	{
		fail("Not yet implemented");
	}

	@Test
	public void testAddGroup()
	{
		fail("Not yet implemented");
	}

	@Test
	public void testAddMember()
	{
		fail("Not yet implemented");
	}

	@Test
	public void testAuthenticate()
	{
		fail("Not yet implemented");
	}

	@Test
	public void testBindAccount()
	{
		fail("Not yet implemented");
	}

	@Test
	public void testFollow()
	{
		try
		{
			com.zhuaiwa.api.Rpc.FollowRequest req = com.zhuaiwa.api.Rpc.FollowRequest
					.newBuilder().setFollowerUserid("")
					.addFollowingUseridList("").addFollowingUseridList("")
					.build();
			sessionDomainBlockingSvc.follow(new NettyRpcController(), req);

		}
		catch (ServiceException e)
		{
			e.printStackTrace();
		}
	}

	@Test
	public void testFollowTag()
	{
		fail("Not yet implemented");
	}

	@Test
	public void testForgetPassword()
	{
		fail("Not yet implemented");
	}

	@Test
	public void testGetContact()
	{
		fail("Not yet implemented");
	}

	@Test
	public void testGetFollower()
	{
		fail("Not yet implemented");
	}

	@Test
	public void testGetFollowing()
	{
		fail("Not yet implemented");
	}

	@Test
	public void testGetFollowingTag()
	{
		fail("Not yet implemented");
	}

	@Test
	public void testGetGroup()
	{
		fail("Not yet implemented");
	}

	@Test
	public void testGetInviter()
	{
		fail("Not yet implemented");
	}

	@Test
	public void testGetInviting()
	{
		fail("Not yet implemented");
	}

	@Test
	public void testGetMember()
	{
		fail("Not yet implemented");
	}

	@Test
	public void testGetMessage()
	{
		try
		{
			LoginRequest.Builder req = LoginRequest.newBuilder().setUser(ssid)
					.setPassword("7CC0F8222C26B474B6A525C92F92D35C");
			sessionDomainBlockingSvc.login(new NettyRpcController(),
					req.build());

			com.zhuaiwa.api.Rpc.GetMessageRequest req1 = com.zhuaiwa.api.Rpc.GetMessageRequest
					.newBuilder().setUserid(userid.getId()).setCount(2)
					.setMode(1).setSourceBox(SSBOX.OUTBOX).build();
			sessionDomainBlockingSvc.getMessage(new NettyRpcController(), req1);
		}
		catch (ServiceException e)
		{
			e.printStackTrace();
		}
	}

	@Test
	public void testGetMessageById()
	{
		fail("Not yet implemented");
	}

	@Test
	public void testGetMessageByTag()
	{
		fail("Not yet implemented");
	}

	@Test
	public void testGetMessageByTimestamp()
	{
		fail("Not yet implemented");
	}

	@Test
	public void testGetProfile()
	{
		try
		{
			com.zhuaiwa.api.Rpc.GetProfileRequest req = com.zhuaiwa.api.Rpc.GetProfileRequest
					.newBuilder().setFlag(1)
					.addUseridList("e871ca996626f239637e0fc2e7019e9c").build();
			sessionDomainBlockingSvc.getProfile(new NettyRpcController(), req);
		}
		catch (ServiceException e)
		{
			e.printStackTrace();
		}
	}

	@Test
	public void testGetRecommendUser()
	{
		fail("Not yet implemented");
	}

	@Test
	public void testGetUserId()
	{
		try
		{
			LoginRequest.Builder req = LoginRequest.newBuilder().setUser(ssid)
					.setPassword("7CC0F8222C26B474B6A525C92F92D35C");
			sessionDomainBlockingSvc.login(new NettyRpcController(),
					req.build());

			com.zhuaiwa.api.SSSessionDomain.GetUserIdResponse resp1 = sessionDomainBlockingSvc
					.getUserId(new NettyRpcController(),
							com.zhuaiwa.api.SSSessionDomain.GetUserIdRequest
									.newBuilder().setUser(ssid).build());
			System.out.println(resp1.getUserid());

			sessionDomainBlockingSvc.setSettings(new NettyRpcController(),
					com.zhuaiwa.api.SSSessionDomain.SetSettingsRequest
							.newBuilder().setUserid(resp1.getUserid())
							.setIsEmailHidden(1).build());

			com.zhuaiwa.api.SSSessionDomain.GetUserIdResponse resp2 = sessionDomainBlockingSvc
					.getUserId(new NettyRpcController(),
							com.zhuaiwa.api.SSSessionDomain.GetUserIdRequest
									.newBuilder().setUser(ssid).build());
			System.out.println(resp2.getUserid());

		}
		catch (ServiceException e)
		{
			System.out.println(e.getMessage());
			e.printStackTrace();
		}
	}

	@Test
	public void testInvite()
	{
		fail("Not yet implemented");
	}

	@Test
	public void testIsFollower()
	{
		fail("Not yet implemented");
	}

	@Test
	public void testLogin()
	{
		try
		{
			LoginRequest.Builder req = LoginRequest.newBuilder().setUser(ssid)
					.setPassword("7CC0F8222C26B474B6A525C92F92D35C");
			com.zhuaiwa.api.SSSessionDomain.LoginResponse resp = sessionDomainBlockingSvc
					.login(new NettyRpcController(), req.build());
			System.out.println("sid : " + resp.getSid());
			System.out.println(resp.hasFirstLogin());
		}
		catch (ServiceException e)
		{
			e.printStackTrace();
		}
	}

	@Test
	public void testLogout()
	{
		fail("Not yet implemented");
	}

	@Test
	public void testRegister()
	{
		try
		{
			RegisterAccountRequest registerRequest = RegisterAccountRequest
					.newBuilder().setUser(ssid).setNickname("昵称")
					.setPassword("tangjun").build();
			NettyRpcController controller = new NettyRpcController();
			FutureResponse<RegisterAccountResponse> done = new FutureResponse<RegisterAccountResponse>();
			sessionDomainSvc.registerAccount(controller, registerRequest, done);
			RegisterAccountResponse resp = done.get();
			if (controller.getCode() == SSResultCode.RC_OK.getNumber())
			{
				System.out.println("UserId:" + resp.getUserid());
			}
			else
			{
				System.out.println(controller.getCode() + " : "
						+ controller.getReason());
			}
			try
			{
				sessionDomainBlockingSvc.registerAccount(
						new NettyRpcController(), registerRequest);

			}
			catch (ServiceException e)
			{
				e.printStackTrace();
			}

		}
		catch (InterruptedException e)
		{
			e.printStackTrace();
		}
		catch (ExecutionException e)
		{
			e.printStackTrace();
		}
	}

	@Test
	public void testRemoveContact()
	{
		fail("Not yet implemented");
	}

	@Test
	public void testRemoveGroup()
	{
		fail("Not yet implemented");
	}

	@Test
	public void testRemoveMember()
	{
		fail("Not yet implemented");
	}

	@Test
	public void testRemoveMessage()
	{
		fail("Not yet implemented");
	}

	@Test
	public void testResetPassword()
	{
		fail("Not yet implemented");
	}

	@Test
	public void testSendMessage()
	{
		try
		{
			LoginRequest.Builder req = LoginRequest.newBuilder().setUser(ssid)
					.setPassword("7CC0F8222C26B474B6A525C92F92D35C");
			com.zhuaiwa.api.SSSessionDomain.LoginResponse r = sessionDomainBlockingSvc
					.login(new NettyRpcController(), req.build());
			System.out.println("sid : " + r.getSid());
			// SSId sender =
			// SSId.newBuilder().setDomain("userid").setId(r.getSid()).build();
			SSPerson receiver = SSPerson.newBuilder()
					.setUserid("5172caf958403e7ca854f0d360486fce").build();
			SSMessage msg = SSMessage.newBuilder().setAgent("agent")
					.setBody("body")
					.setMsgType(SSMessageType.MESSAGE_TYPE_CONTENT.getNumber())
					.addReceiver(receiver)
					.setShareType(SSShareType.SHARE_TYPE_PROTECTED.getNumber())
					.build();
			com.zhuaiwa.api.Rpc.SendMessageRequest request = com.zhuaiwa.api.Rpc.SendMessageRequest
					.newBuilder().setUserid(ssid.getId()).setMsg(msg).build();
			com.zhuaiwa.api.Rpc.SendMessageResponse resp = sessionDomainBlockingSvc
					.sendMessage(new NettyRpcController(), request);
			System.out.println(resp.getMsgid());
		}
		catch (Exception e)
		{
			e.printStackTrace();
		}
	}

	@Test
	public void testSetProfile()
	{
		fail("Not yet implemented");
	}

	@Test
	public void testUnbindAccount()
	{
		try {
			SSId old = SSId.newBuilder().setDomain(SSIdUtils.SSIdDomain.phone.name()).setId("18610299757").build();
			UnbindAccountRequest.Builder req = UnbindAccountRequest.newBuilder().setOldId(old);
			sessionDomainBlockingSvc.unbindAccount(new NettyRpcController(), req.build());
		} catch (ServiceException e) {
			e.printStackTrace();
		}
	}

	@Test
	public void testUnfollow()
	{
		fail("Not yet implemented");
	}

	@Test
	public void testUnfollowTag()
	{
		fail("Not yet implemented");
	}

}
