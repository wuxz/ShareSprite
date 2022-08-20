package com.zhuaiwa.dd.api;

import static org.junit.Assert.assertEquals;
import me.prettyprint.cassandra.serializers.StringSerializer;
import me.prettyprint.hector.api.factory.HFactory;
import me.prettyprint.hector.api.mutation.Mutator;

import org.junit.After;
import org.junit.AfterClass;
import org.junit.Assert;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;

import com.zhuaiwa.api.Common.SSAccount;
import com.zhuaiwa.api.Common.SSResultCode;
import com.zhuaiwa.api.Rpc.BindAccountRequest;
import com.zhuaiwa.api.Rpc.BindAccountResponse;
import com.zhuaiwa.api.Rpc.UnbindAccountRequest;
import com.zhuaiwa.api.Rpc.UnbindAccountResponse;
import com.zhuaiwa.api.SSDataDomain.CreateAccountRequest;
import com.zhuaiwa.api.SSDataDomain.CreateAccountResponse;
import com.zhuaiwa.api.SSDataDomain.GetAccountRequest;
import com.zhuaiwa.api.SSDataDomain.GetAccountResponse;
import com.zhuaiwa.api.SSDataDomain.SSDataDomainSvc.BlockingInterface;
import com.zhuaiwa.api.SSDataDomain.SetAccountRequest;
import com.zhuaiwa.api.SSDataDomain.SetAccountResponse;
import com.zhuaiwa.api.netty.NettyRpcController;
import com.zhuaiwa.api.util.SSIdUtils;
import com.zhuaiwa.api.util.SSIdUtils.SSIdDomain;
import com.zhuaiwa.dd.domain.Account;
import com.zhuaiwa.dd.domain.EmailAccount;
import com.zhuaiwa.dd.domain.PhoneAccount;
import com.zhuaiwa.dd.hector.HectorFactory;
import com.zhuaiwa.dd.utils.TestInitializer;

public class AccountTest {
	private static BlockingInterface api;

    @BeforeClass
    public static void init() throws Exception {
		api = TestInitializer.getSync();
    }
    
    @AfterClass
    public static void shutdown() throws Exception {
    }
    
    @Before
    public void setUp() throws Exception {
        TestInitializer.initKeyspace();
		testCreateAccount();
	}

	@After
	public void tearDown() throws Exception {
	    TestInitializer.dropKeyspace();
	}
	
	private String getUserId() throws Exception {
		return "test";
	}

	public void testCreateAccount() throws Exception {
        NettyRpcController controller = new NettyRpcController();
        CreateAccountRequest request = CreateAccountRequest.newBuilder()
            .setEmail("test@baiku.cn")
            .setPhoneNumber("12345678901")
            .setUserid(getUserId())
            .build();
        CreateAccountResponse response = api.createAccount(controller, request);
        assertEquals(SSResultCode.RC_OK.getNumber(), controller.getCode());
        assertEquals(getUserId(), response.getUserid());
	}
    
    public void clearAccount() throws Exception {
        Mutator<String> mutator = HFactory.createMutator(HectorFactory.getKeyspace(), StringSerializer.get());
        mutator.addDeletion(getUserId(), Account.CN_ACCOUNT);
        mutator.addDeletion("test@baiku.cn", EmailAccount.CN_EMAIL_ACCOUNT);
        mutator.addDeletion("aaa@bbb.ccc", EmailAccount.CN_EMAIL_ACCOUNT);
        mutator.addDeletion("12345678901", PhoneAccount.CN_PHONE_ACCOUNT);
        mutator.addDeletion("11111111111", PhoneAccount.CN_PHONE_ACCOUNT);
        mutator.execute();
    }
	
    @Test
    public void testGetAccountByUserid() throws Exception {
        NettyRpcController controller = new NettyRpcController();
        GetAccountRequest request = GetAccountRequest.newBuilder()
            .addUserid(getUserId())
            .build();
        GetAccountResponse response = api.getAccount(controller, request);
        assertEquals(SSResultCode.RC_OK.getNumber(), controller.getCode());
        assertEquals(getUserId(), response.getAccount(0).getUserid());
    }

    @Test
    public void testGetAccountByEmail() throws Exception {
        NettyRpcController controller = new NettyRpcController();
        GetAccountRequest request = GetAccountRequest.newBuilder()
            .addEmail("test@baiku.cn")
            .build();
        GetAccountResponse response = api.getAccount(controller, request);
        assertEquals(SSResultCode.RC_OK.getNumber(), controller.getCode());
        assertEquals(getUserId(), response.getAccount(0).getUserid());
        assertEquals("test@baiku.cn", SSIdUtils.getId(response.getAccount(0).getAliasIdListList(), SSIdDomain.email));
    }

    @Test
    public void testGetAccountByPhone() throws Exception {
        NettyRpcController controller = new NettyRpcController();
        GetAccountRequest request = GetAccountRequest.newBuilder()
            .addPhoneNumber("12345678901")
            .build();
        GetAccountResponse response = api.getAccount(controller, request);
        assertEquals(SSResultCode.RC_OK.getNumber(), controller.getCode());
        assertEquals(getUserId(), response.getAccount(0).getUserid());
        assertEquals("12345678901", SSIdUtils.getId(response.getAccount(0).getAliasIdListList(), SSIdDomain.phone));
    }

	@Test
	public void testSetAccount() throws Exception {
		String userid = getUserId();
		String password = "123456";
		String sc = "aaa";
		long registerTime = System.currentTimeMillis();
		long scTime = System.currentTimeMillis();
		int isActive = 1;
		int role = 1;
		{
		NettyRpcController controller = new NettyRpcController();
		SetAccountRequest request = SetAccountRequest.newBuilder()
			.setUserid(userid)
			.setAccount(SSAccount.newBuilder()
					.setUserid(userid)
					.setPassword(password)
					.setRegisterTime(registerTime)
					.setIsActive(isActive)
					.setRole(role)
					.setSecurityCode(sc)
					.setSecurityCodeTime(scTime)
					.build()
					)
			.build();
		SetAccountResponse response = api.setAccount(controller, request);
		Assert.assertEquals(SSResultCode.RC_OK.getNumber(), controller.getCode());
		}
		
		{
        NettyRpcController controller = new NettyRpcController();
        GetAccountRequest request = GetAccountRequest.newBuilder()
            .addUserid(getUserId())
            .build();
        GetAccountResponse response = api.getAccount(controller, request);
        assertEquals(SSResultCode.RC_OK.getNumber(), controller.getCode());
        SSAccount account = response.getAccount(0);
        assertEquals(userid, account.getUserid());
        assertEquals(password, account.getPassword());
        assertEquals(registerTime, account.getRegisterTime());
        assertEquals(isActive, account.getIsActive());
        assertEquals(role, account.getRole());
        assertEquals(sc, account.getSecurityCode());
        assertEquals(scTime, account.getSecurityCodeTime());
		}
	}

	/**
	 * 测试不允许修改email和phone
	 */
	@Test
	public void testSetAccount2() throws Exception {
        {
        NettyRpcController controller = new NettyRpcController();
        SetAccountRequest request = SetAccountRequest.newBuilder()
            .setUserid(getUserId())
            .setAccount(SSAccount.newBuilder()
                    .addAliasIdList(SSIdUtils.fromEmail("aaa@bbb.ccc"))
                    .build()
                    )
            .build();
        SetAccountResponse response = api.setAccount(controller, request);
        Assert.assertEquals(SSResultCode.RC_PARAMETER_INVALID.getNumber(), controller.getCode());
        }
        {
        NettyRpcController controller = new NettyRpcController();
        SetAccountRequest request = SetAccountRequest.newBuilder()
            .setUserid(getUserId())
            .setAccount(SSAccount.newBuilder()
                    .addAliasIdList(SSIdUtils.fromPhone("11111111111"))
                    .build()
                    )
            .build();
        SetAccountResponse response = api.setAccount(controller, request);
        Assert.assertEquals(SSResultCode.RC_PARAMETER_INVALID.getNumber(), controller.getCode());
        }
	}

	@Test
	public void testBindPhoneAccount() throws Exception {
	    String phone = "11111111111";
	    {
            NettyRpcController controller = new NettyRpcController();
            BindAccountRequest request = BindAccountRequest.newBuilder()
                .setUserid(getUserId())
                .setNewId(SSIdUtils.fromPhone(phone))
                .build();
            BindAccountResponse response = api.bindAccount(controller, request);
            Assert.assertEquals(SSResultCode.RC_OK.getNumber(), controller.getCode());
	    }
        {
            NettyRpcController controller = new NettyRpcController();
            GetAccountRequest request = GetAccountRequest.newBuilder()
                .addPhoneNumber(phone)
                .build();
            GetAccountResponse response = api.getAccount(controller, request);
            assertEquals(SSResultCode.RC_OK.getNumber(), controller.getCode());
            assertEquals(getUserId(), response.getAccount(0).getUserid());
            assertEquals(phone, SSIdUtils.getId(response.getAccount(0).getAliasIdListList(), SSIdDomain.phone));
        }
	}

    @Test
    public void testBindEmailAccount() throws Exception {
        String email = "aaa@bbb.ccc";
        {
            NettyRpcController controller = new NettyRpcController();
            BindAccountRequest request = BindAccountRequest.newBuilder()
                .setUserid(getUserId())
                .setNewId(SSIdUtils.fromEmail(email))
                .build();
            BindAccountResponse response = api.bindAccount(controller, request);
            Assert.assertEquals(SSResultCode.RC_OK.getNumber(), controller.getCode());
        }
        {
            NettyRpcController controller = new NettyRpcController();
            GetAccountRequest request = GetAccountRequest.newBuilder()
                .addEmail(email)
                .build();
            GetAccountResponse response = api.getAccount(controller, request);
            assertEquals(SSResultCode.RC_OK.getNumber(), controller.getCode());
            assertEquals(getUserId(), response.getAccount(0).getUserid());
            assertEquals(email, SSIdUtils.getId(response.getAccount(0).getAliasIdListList(), SSIdDomain.email));
        }
    }

	@Test
	public void testUnbindPhoneAccount() throws Exception {
	    String phone = "12345678901";
	    {
            NettyRpcController controller = new NettyRpcController();
            UnbindAccountRequest request = UnbindAccountRequest.newBuilder()
                .setOldId(SSIdUtils.fromPhone(phone))
                .setUserid(getUserId())
                .build();
            UnbindAccountResponse response = api.unbindAccount(controller, request);
            Assert.assertEquals(SSResultCode.RC_OK.getNumber(), controller.getCode());
	    }
        {
            NettyRpcController controller = new NettyRpcController();
            GetAccountRequest request = GetAccountRequest.newBuilder()
                .addPhoneNumber(phone)
                .build();
            GetAccountResponse response = api.getAccount(controller, request);
            assertEquals(SSResultCode.RC_OK.getNumber(), controller.getCode());
            assertEquals(0, response.getAccountCount());
        }
        {
            NettyRpcController controller = new NettyRpcController();
            GetAccountRequest request = GetAccountRequest.newBuilder()
                .addUserid(getUserId())
                .build();
            GetAccountResponse response = api.getAccount(controller, request);
            assertEquals(SSResultCode.RC_OK.getNumber(), controller.getCode());
            assertEquals(getUserId(), response.getAccount(0).getUserid());
            assertEquals(null, SSIdUtils.getId(response.getAccount(0).getAliasIdListList(), SSIdDomain.phone));
        }
	}


    @Test
    public void testUnbindEmailAccount() throws Exception {
        String email = "test@baiku.cn";
        {
            NettyRpcController controller = new NettyRpcController();
            UnbindAccountRequest request = UnbindAccountRequest.newBuilder()
                .setOldId(SSIdUtils.fromEmail(email))
                .setUserid(getUserId())
                .build();
            UnbindAccountResponse response = api.unbindAccount(controller, request);
            Assert.assertEquals(SSResultCode.RC_OK.getNumber(), controller.getCode());
        }
        {
            NettyRpcController controller = new NettyRpcController();
            GetAccountRequest request = GetAccountRequest.newBuilder()
                .addEmail(email)
                .build();
            GetAccountResponse response = api.getAccount(controller, request);
            assertEquals(SSResultCode.RC_OK.getNumber(), controller.getCode());
            assertEquals(0, response.getAccountCount());
        }
        {
            NettyRpcController controller = new NettyRpcController();
            GetAccountRequest request = GetAccountRequest.newBuilder()
                .addUserid(getUserId())
                .build();
            GetAccountResponse response = api.getAccount(controller, request);
            assertEquals(SSResultCode.RC_OK.getNumber(), controller.getCode());
            assertEquals(getUserId(), response.getAccount(0).getUserid());
            assertEquals(null, SSIdUtils.getId(response.getAccount(0).getAliasIdListList(), SSIdDomain.email));
        }
    }
}
