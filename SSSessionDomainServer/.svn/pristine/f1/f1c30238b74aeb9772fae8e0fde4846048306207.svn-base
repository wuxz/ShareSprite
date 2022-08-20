/**
 * 
 */
package com.zhuaiwa.session.service;

import org.apache.commons.lang.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.zhuaiwa.api.Common.ApiHeader;
import com.zhuaiwa.api.Common.SSAccount;
import com.zhuaiwa.api.Common.SSId;
import com.zhuaiwa.api.Common.SSResultCode;
import com.zhuaiwa.api.SSDataDomain.SSDataDomainSvc.BlockingInterface;
import com.zhuaiwa.api.SSSessionDomain.GetNumberAttributionRequest;
import com.zhuaiwa.api.SSSessionDomain.GetNumberAttributionResponse;
import com.zhuaiwa.api.SSSessionDomain.GetSessionInfoRequest;
import com.zhuaiwa.api.SSSessionDomain.GetSessionInfoResponse;
import com.zhuaiwa.api.SSSessionDomain.GetSystemInfoRequest;
import com.zhuaiwa.api.SSSessionDomain.GetSystemInfoResponse;
import com.zhuaiwa.api.SSSessionDomain.NumberAttribution;
import com.zhuaiwa.api.netty.NettyRpcController;
import com.zhuaiwa.api.util.SSIdUtils;
import com.zhuaiwa.api.util.SSIdUtils.SSIdDomain;
import com.zhuaiwa.session.SessionManager;
import com.zhuaiwa.session.SessionManager.Session;
import com.zhuaiwa.session.util.PhoneNumberSeeker;
import com.zhuaiwa.session.util.PhoneNumberSeeker.PhoneNumberInfo;
import com.zhuaiwa.util.PropertiesHelper;

/**
 * @author wuxz
 */
public class BaikuTelServiceImpl extends BaseService
{
	static PhoneNumberSeeker pns = null;

	public static void main(String[] args) throws Exception
	{
		NettyRpcController controller = new NettyRpcController();
		BaikuTelServiceImpl baikuTelService = new BaikuTelServiceImpl();

		GetSystemInfoResponse resp = baikuTelService.getSystemInfo(controller,
				GetSystemInfoRequest.newBuilder().setPhoneNumber("13901234567")
						.build());
		System.out.println(resp.getAreaCode());

		GetNumberAttributionResponse resp1 = baikuTelService
				.getNumberAttribution(controller, GetNumberAttributionRequest
						.newBuilder().addPhoneNumber("13912234567").build());
		System.out.println(resp1.getNumberAttribution(0).getPostCode());

		controller.setApiHeader(ApiHeader.getDefaultInstance());
		baikuTelService.getSessionInfo(controller, GetSessionInfoRequest
				.newBuilder().build());
		System.out.println(controller.errorText());

		System.exit(0);
	}

	private BlockingInterface dataDomainClientSvc;

	private Logger LOG = LoggerFactory.getLogger(this.getClass());

	private UserService_v_1_0 userService;

	{
		String filePath = PropertiesHelper.getValue("phonenumber.file");
		pns = new PhoneNumberSeeker(
				StringUtils.isEmpty(filePath) ? new String[] {}
						: filePath.split(" "));
	}

	public GetNumberAttributionResponse getNumberAttribution(
			NettyRpcController controller, GetNumberAttributionRequest request)
	{
		if (pns == null)
		{
			return null;
		}

		GetNumberAttributionResponse.Builder builder = GetNumberAttributionResponse
				.newBuilder();

		for (String phoneNumber : request.getPhoneNumberList())
		{
			if (phoneNumber.length() < 7)
			{
				failed(controller,
						SSResultCode.RC_SERVICE_EXCEPTION.getNumber(),
						phoneNumber + "不是有效的电话号码.");

				return null;
			}

			String prefix = phoneNumber.substring(0, 7);
			PhoneNumberInfo pni = pns.seek(prefix);
			if (pni == null)
			{
				failed(controller,
						SSResultCode.RC_SERVICE_EXCEPTION.getNumber(),
						phoneNumber + "没找到归属信息.");

				return null;
			}

			builder.addNumberAttribution(NumberAttribution
					.newBuilder()
					.setCarrier(
							pni.carrier.equals("移动") ? 1 : pni.carrier
									.equals("联通") ? 2 : pni.carrier
									.equals("电信") ? 3 : 4)
					.setPostCode(pni.postCode).setAreaCode(pni.areaCode)
					.setPrefix(prefix).build());
		}

		return builder.build();
	}

	public GetSessionInfoResponse getSessionInfo(NettyRpcController controller,
			GetSessionInfoRequest request)
	{
		Session session = SessionManager.getInstance().getSession(
				controller.getApiHeader().getSid());

		try
		{
			SSId ssid = SSIdUtils.fromUserId(session.userid);
			SSAccount account = userService.getAccount(ssid);
			for (SSId id : account.getAliasIdListList())
			{
				if (SSIdUtils.getDomain(id.getDomain()) == SSIdDomain.phone)
				{
					GetSessionInfoResponse.Builder builder = GetSessionInfoResponse
							.newBuilder();
					builder.setPhoneNumber(id.getId());

					return builder.build();
				}
			}

			return null;
		}
		catch (Throwable e)
		{
			failed(controller, SSResultCode.RC_SERVICE_EXCEPTION.getNumber(),
					"查询会话发生异常.", e);
			return null;
		}
	}

	public GetSystemInfoResponse getSystemInfo(NettyRpcController controller,
			GetSystemInfoRequest request)
	{
		return GetSystemInfoResponse.newBuilder()
				.setAreaCode(PropertiesHelper.getValue("area_code"))
				.setBaikuServer(PropertiesHelper.getValue("baiku_server"))
				.setIvrNumber(PropertiesHelper.getValue("ivr_number"))
				.setIvrServer(PropertiesHelper.getValue("ivr_server"))
				.setSmsNumber(PropertiesHelper.getValue("sms_number"))
				.setUpdateServer(PropertiesHelper.getValue("update_server"))
				.build();
	}

	/**
	 * @return the userService
	 */
	public UserService_v_1_0 getUserService()
	{
		return userService;
	}

	public void setDataDomainClientSvc(BlockingInterface dataDomainClientSvc)
	{
		this.dataDomainClientSvc = dataDomainClientSvc;
	}

	/**
	 * @param userService
	 *            the userService to set
	 */
	public void setUserService(UserService_v_1_0 userService)
	{
		this.userService = userService;
	}
}
