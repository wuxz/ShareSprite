package com.zhuaiwa.session;

import java.net.InetSocketAddress;
import java.util.Iterator;
import java.util.List;
import java.util.concurrent.CyclicBarrier;
import java.util.concurrent.Executors;
import java.util.concurrent.atomic.AtomicInteger;

import org.apache.commons.codec.digest.DigestUtils;
import org.jboss.netty.channel.socket.nio.NioClientSocketChannelFactory;
import org.junit.After;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;

import com.google.protobuf.ServiceException;
import com.zhuaiwa.api.Common.SSAccount;
import com.zhuaiwa.api.Common.SSBOX;
import com.zhuaiwa.api.Common.SSId;
import com.zhuaiwa.api.Common.SSMessage;
import com.zhuaiwa.api.Common.SSMessageType;
import com.zhuaiwa.api.Common.SSPerson;
import com.zhuaiwa.api.Common.SSResultCode;
import com.zhuaiwa.api.Common.SSShareType;
import com.zhuaiwa.api.Rpc.GetMessageByIdRequest;
import com.zhuaiwa.api.Rpc.GetMessageByIdResponse;
import com.zhuaiwa.api.Rpc.GetMessageByTimestampRequest;
import com.zhuaiwa.api.Rpc.GetMessageByTimestampResponse;
import com.zhuaiwa.api.Rpc.SendMessageRequest;
import com.zhuaiwa.api.SSDataDomain.GetAccountRequest;
import com.zhuaiwa.api.SSDataDomain.GetAccountResponse;
import com.zhuaiwa.api.SSDataDomain.SetAccountRequest;
import com.zhuaiwa.api.SSSessionDomain.LoginRequest;
import com.zhuaiwa.api.SSSessionDomain.LoginResponse;
import com.zhuaiwa.api.SSSessionDomain.LogoutRequest;
import com.zhuaiwa.api.SSSessionDomain.RegisterAccountRequest;
import com.zhuaiwa.api.SSSessionDomain.SSSessionDomainSvc;
import com.zhuaiwa.api.netty.NettyRpcController;
import com.zhuaiwa.api.netty.client.NettyRpcChannel;
import com.zhuaiwa.api.netty.client.NettyRpcClient;
import com.zhuaiwa.api.util.SSIdUtils;

public class PressureTest
{
	class TestThread extends Thread
	{
		private NettyRpcChannel channel;

		int id = 0;

		int millisUsed[] = new int[MESSAGE_COUNT_PER_USER];
		private com.zhuaiwa.api.SSSessionDomain.SSSessionDomainSvc.BlockingInterface sessionDomainBlockingSvc;
		private com.zhuaiwa.api.SSSessionDomain.SSSessionDomainSvc.Interface sessionDomainSvc;

		String userId;

		TestThread(int id)
		{
			this.id = id;
		}

		void activate() throws Exception
		{
			SSAccount a = SSAccount.newBuilder().setIsActive(1)
			.build();
			SetAccountRequest req = SetAccountRequest.newBuilder()
			.setUserid(getAccount().getUserid()).setAccount(a).build();

			NettyRpcController controller = new NettyRpcController();
			DataDomainSvcFactory.getBlockingService().setAccount(controller, req);
		}

		protected SSAccount getAccount() throws ServiceException
		{
			GetAccountRequest.Builder builder = GetAccountRequest.newBuilder();
			builder.addEmail("pressure" + id + "@feedss.com");

			NettyRpcController controller = new NettyRpcController();
			GetAccountResponse resp = DataDomainSvcFactory.getBlockingService().getAccount(
					controller, builder.build());
			if (resp.getAccountCount() > 0)
			{
				return resp.getAccount(0);
			}
			else
			{
				return null;
			}
		}

		int getMessageById(String id) throws Exception
		{
			NettyRpcController controller = new NettyRpcController();
			GetMessageByIdRequest getMessageByIdRequest = GetMessageByIdRequest.newBuilder().addMsgidList(id).build();
			GetMessageByIdResponse response = sessionDomainBlockingSvc.getMessageById(controller, getMessageByIdRequest);
			Assert.assertEquals(controller.getReason(), SSResultCode.RC_OK.getNumber(), controller.getCode());
			return response.getMsgListCount();
		}

		int getMessageCountByTimestamp() throws Exception
		{
			NettyRpcController controller = new NettyRpcController();
			GetMessageByTimestampRequest getMessageByTimestampRequest = GetMessageByTimestampRequest.newBuilder().setSourceBox(SSBOX.PUBBOX).setUserid(getAccount().getUserid()).setStartTimestamp(0).build();
			GetMessageByTimestampResponse response = sessionDomainBlockingSvc.getMessageByTimestamp(controller, getMessageByTimestampRequest);
			Assert.assertEquals(controller.getReason(), SSResultCode.RC_OK.getNumber(), controller.getCode());
			return response.getMsgidListCount();
		}

		int getMessagesByIds(List<String> ids) throws Exception
		{
			NettyRpcController controller = new NettyRpcController();
			GetMessageByIdRequest getMessageByIdRequest = GetMessageByIdRequest.newBuilder().addAllMsgidList(ids).build();
			GetMessageByIdResponse response = sessionDomainBlockingSvc.getMessageById(controller, getMessageByIdRequest);
			Assert.assertEquals(controller.getReason(), SSResultCode.RC_OK.getNumber(), controller.getCode());
			return response.getMsgListCount();
		}

		List<String> getMessagesByTimestamp() throws Exception
		{
			NettyRpcController controller = new NettyRpcController();
			GetMessageByTimestampRequest getMessageByTimestampRequest = GetMessageByTimestampRequest.newBuilder().setSourceBox(SSBOX.PUBBOX).setUserid(getAccount().getUserid()).setStartTimestamp(0).build();
			GetMessageByTimestampResponse response = sessionDomainBlockingSvc.getMessageByTimestamp(controller, getMessageByTimestampRequest);
			Assert.assertEquals(controller.getReason(), SSResultCode.RC_OK.getNumber(), controller.getCode());
			return response.getMsgidListList();
		}

		public void init(NettyRpcClient client) throws Exception
		{
			channel = client.blockingConnect(new InetSocketAddress("127.0.0.1", 8000));
			sessionDomainSvc = SSSessionDomainSvc.newStub(channel);
			sessionDomainBlockingSvc = SSSessionDomainSvc.newBlockingStub(channel);
		}

		void login() throws Exception
		{
			SSId ssid = SSId.newBuilder().setDomain(SSIdUtils.SSIdDomain.email.name())
			.setId("pressure" + id + "@feedss.com").build();
			LoginRequest loginRequest = LoginRequest.newBuilder().setUser(ssid)
			.setPassword(DigestUtils.md5Hex("000000")).build();

			NettyRpcController controller = new NettyRpcController();
			LoginResponse resp = sessionDomainBlockingSvc.login(
					controller, loginRequest);
			Assert.assertEquals(controller.getReason(), SSResultCode.RC_OK.getNumber(), controller.getCode());

			userId = resp.getIdList(0).getId();

//			channel.setSid(resp.getSid());
		}

		void logout() throws Exception
		{
			LogoutRequest logoutRequest = LogoutRequest.newBuilder().build();

			NettyRpcController controller = new NettyRpcController();
			sessionDomainBlockingSvc.logout(
					controller, logoutRequest);
			Assert.assertEquals(controller.getReason(), SSResultCode.RC_OK.getNumber(), controller.getCode());
		}

		void register() throws Exception
		{
			SSId ssid = SSId.newBuilder().setDomain(SSIdUtils.SSIdDomain.email.name())
			.setId("pressure" + id + "@feedss.com").build();
			RegisterAccountRequest registerRequest = RegisterAccountRequest.newBuilder().setUser(ssid).setNickname("昵称")
			.setPassword("000000").build();

			NettyRpcController controller = new NettyRpcController();
			sessionDomainBlockingSvc.registerAccount(
					controller, registerRequest);
			Assert.assertEquals(controller.getReason(), SSResultCode.RC_OK.getNumber(), controller.getCode());
		}

		@Override
		public void run()
		{
			try
			{
				Thread.sleep((long)(15 * 1000 * Math.random()));
				//register(i);
				//activate(i);
				login();

				try
				{
					waiter.await();
				}
				catch (Exception e)
				{
				}
				try
				{
					waiter.reset();
				}
				catch (Exception e)
				{
				}

				startMillis = System.currentTimeMillis();

				//getMessageByTimestamp(i, 0);
				//			sendMessage(i);
				//			Thread.sleep(40000);

				try
				{
					for (int i = 0; i < 1/*MESSAGE_COUNT_PER_USER*/; i ++)
					{
						Thread.sleep((long)(0.01 * 1000 * Math.random()));

						//						sendMessage();
						//						getMessageCountByTimestamp();
						List<String> ids = getMessagesByTimestamp();
						for (Iterator<String> it = ids.iterator(); it.hasNext();)
						{
							long callStartMillis = System.currentTimeMillis();
							int count = getMessageById(it.next());

							millisUsed[i] = (int)(System.currentTimeMillis() - callStartMillis);
							if (millisUsed[i] > 300)
							{
								longCallCount.incrementAndGet();
							}

							msgCount.addAndGet(count);
						}
					}
				}
				finally
				{

					try
					{
						waiter.await();
					}
					catch (Exception e)
					{
					}
					try
					{
						waiter.reset();
					}
					catch (Exception e)
					{
					}
				}

				endMillis = System.currentTimeMillis();

				Thread.sleep((long)(15 * 1000 * Math.random()));
				logout();

				//				msgCount.incrementAndGet();
			}
			catch (Exception e)
			{
				e.printStackTrace();
			}
		}

		void sendMessage() throws Exception
		{
			SSMessage.Builder msgBuilder = SSMessage.newBuilder();
			msgBuilder.setMsgType(SSMessageType.MESSAGE_TYPE_CONTENT.getNumber()).
			setSender(SSId.newBuilder().setId(getAccount().getUserid()).build()).
			setShareType(SSShareType.SHARE_TYPE_PUBLIC.getNumber()).setBody(
			"{\"contents\":[{\"content_type\":\"command\", \"command_id\":\"befollowed\", \"command_desc\":{\"desc\":\"\", \"sender\":\"\"}}]}")
			;
			msgBuilder.addReceiver(SSPerson.newBuilder().setUserid(getAccount().getUserid()));

			NettyRpcController controller = new NettyRpcController();
			sessionDomainBlockingSvc.sendMessage(controller, SendMessageRequest.newBuilder().setMsg(msgBuilder.build()).setUserid("system_user").build());
			Assert.assertEquals(controller.getReason(), SSResultCode.RC_OK.getNumber(), controller.getCode());
		}
	}

	static final int MESSAGE_COUNT_PER_USER = 100;
	static final int USER_COUNT = 500;
	static CyclicBarrier waiter = new CyclicBarrier(USER_COUNT);

	public static void main(String args[]) throws Exception
	{
		PressureTest pt = new PressureTest();
		pt.setUp();
		pt.test();
		pt.tearDown();

		System.exit(0);
	}

	long endMillis = System.currentTimeMillis();

	AtomicInteger longCallCount = new AtomicInteger(0);
	AtomicInteger msgCount = new AtomicInteger(0);
	long startMillis = System.currentTimeMillis();

	TestThread [] testers;

	@Before
	public void setUp() throws Exception {
		SSSessionDomainServer.registerSessionDomainApiProtobufExtension();

		NettyRpcClient client = new NettyRpcClient(new NioClientSocketChannelFactory(Executors.newCachedThreadPool(),
				Executors.newCachedThreadPool()));

		testers = new TestThread[USER_COUNT];

		for (int i = 0; i < USER_COUNT; i ++)
		{
			testers[i] = new TestThread(i);
			testers[i].init(client);

			testers[i].start();
		}

		//		NettyRpcClient client = new NettyRpcClient(new NioClientSocketChannelFactory(Executors.newCachedThreadPool(),
		//				Executors.newCachedThreadPool()),true);
		//		channel = client.blockingConnect(new InetSocketAddress("59.151.117.236", 8001));
		//		sessionDomainSvc = SSSessionDomainSvc.newStub(channel);
		//		sessionDomainBlockingSvc = SSSessionDomainSvc.newBlockingStub(channel);
	}

	@After
	public void tearDown() throws Exception {

	}

	@Test
	public void test() throws Exception
	{
		for (int i = 0; i < USER_COUNT; i ++)
		{
			testers[i].join();
		}

		if (msgCount.intValue() != USER_COUNT * MESSAGE_COUNT_PER_USER)
		{
			System.out.println("Error!!!!");
			endMillis = System.currentTimeMillis();
		}

		System.out.println("time(s):" + (endMillis - startMillis) / 1000);
		System.out.println(msgCount.intValue());
		System.out.println(longCallCount.intValue());
	}
}
