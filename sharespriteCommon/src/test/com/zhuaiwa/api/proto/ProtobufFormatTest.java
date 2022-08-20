package com.zhuaiwa.api.proto;

import com.google.protobuf.TextFormat;
import com.zhuaiwa.api.Common;
import com.zhuaiwa.api.Common.ApiHeader;
import com.zhuaiwa.api.Common.ApiMessage;
import com.zhuaiwa.api.Common.ApiRequest;
import com.zhuaiwa.api.Common.ApiType;
import com.zhuaiwa.api.Common.SSProfile;
import com.zhuaiwa.api.Rpc;
import com.zhuaiwa.api.Rpc.SetProfileRequest;
import com.zhuaiwa.api.util.ApiExtensionHelper;
import com.zhuaiwa.util.JsonFormat;

public class ProtobufFormatTest
{

	/**
	 * @param args
	 */
	public static void main(String[] args) throws Exception
	{

		ApiHeader apiHeader = ApiHeader.newBuilder().setSeq(1)
				.setSid("11111111111111111111")
				.setType(ApiType.API_TYPE_REQUEST.getNumber())
				.setVersion(0x00010000).build();
		SetProfileRequest setProfileRequest = SetProfileRequest
				.newBuilder()
				.setUserid("userid1")
				.setProfile(
						SSProfile.newBuilder().setUserid("userid1")
								.setEducation("education1")
								.setWorkExperience("workexperience1")
								.addFavTags("fav1").addFavTags("fav2")).build();
		// BindAccountRequest bindAccountRequest =
		// BindAccountRequest.newBuilder()
		// .setNewId(SSIdUtils.fromEmail("aaa@bbb.ccc"))
		// .setUserid("ddd")
		// .build();
		ApiRequest apiRequest = ApiRequest.newBuilder()
				.setService("ServiceName").setMethod("MethodName")
				// .setExtension(Rpc.bindAccountRequest, bindAccountRequest)
				.setExtension(Rpc.setProfileRequest, setProfileRequest).build();
		ApiMessage apiMessage = ApiMessage.newBuilder().setHeader(apiHeader)
				.setExtension(Common.request, apiRequest).build();

		System.out.println("TextFormat:");
		System.out.println(TextFormat.printToString(apiMessage));

		String json = JsonFormat.printToString(apiMessage);
		System.out.println("JsonFormat:");
		System.out.println(json);
		ApiMessage.Builder apiMessageBuilder = ApiMessage.newBuilder();
		JsonFormat.merge(json, ApiExtensionHelper.getExtensionRegistry(),
				apiMessageBuilder);
		System.out.println("TextFormat:");
		System.out.println(TextFormat.printToString(apiMessageBuilder.build()));
	}

}
