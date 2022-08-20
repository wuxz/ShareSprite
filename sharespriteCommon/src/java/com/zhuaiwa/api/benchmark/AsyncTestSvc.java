package com.zhuaiwa.api.benchmark;

import java.util.concurrent.atomic.AtomicInteger;

import com.google.protobuf.ByteString;
import com.google.protobuf.RpcCallback;
import com.google.protobuf.RpcController;
import com.zhuaiwa.api.Common.TestRequest;
import com.zhuaiwa.api.Common.TestResponse;
import com.zhuaiwa.api.Common.TestSvc;

public class AsyncTestSvc implements TestSvc.Interface
{
	AtomicInteger count = new AtomicInteger(0);

	final ByteString payload = ByteString.copyFrom(new byte[0]);

	long startTime = 0;

	@Override
	public void test(RpcController controller, TestRequest request,
			RpcCallback<TestResponse> done)
	{
		if (startTime == 0)
		{
			startTime = System.currentTimeMillis();
		}
		done.run(TestResponse.newBuilder().setPayload(payload).build());
		if ((count.incrementAndGet() % 10000) == 0)
		{
			System.out
					.println(""
							+ count.get()
							+ "/"
							+ ((count.intValue() * 1000l) / (System
									.currentTimeMillis() - startTime))
							+ " opt/s");
		}
	}

}
