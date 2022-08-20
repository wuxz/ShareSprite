package com.zhuaiwa.api.proto;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import com.zhuaiwa.api.Common.SSPerson;
import com.zhuaiwa.api.util.ApiExtensionHelper;
import com.zhuaiwa.util.JsonFormat;

public class JsonFormatTest {
	public static void main(String[] args) throws Exception {
		SSPerson p1 = SSPerson.newBuilder()
						.setEmail("p1@zhuaiwa.com")
						.setUserid("p1")
						.setPhone("00001")
						.build();
		SSPerson p2 = SSPerson.newBuilder()
						.setEmail("p2@zhuaiwa.com")
						.setUserid("p2")
						.setPhone("00002")
						.build();
		SSPerson p3 = SSPerson.newBuilder()
						.setEmail("p3@zhuaiwa.com")
						.setUserid("p3")
						.setPhone("00003")
						.build();
		String json = JsonFormat.printToString((List)Arrays.asList(p1, p2, p3));
		System.out.println(json);
		
		List<SSPerson.Builder> persons = new ArrayList<SSPerson.Builder>();
		JsonFormat.merge(json, ApiExtensionHelper.getExtensionRegistry(), SSPerson.newBuilder(), (List)persons);
		for (SSPerson.Builder p : persons) {
			System.out.println(JsonFormat.printToString(p.build()));
		}
	}
}
