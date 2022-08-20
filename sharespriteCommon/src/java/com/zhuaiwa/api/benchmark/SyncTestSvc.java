package com.zhuaiwa.api.benchmark;

import com.google.protobuf.ByteString;
import com.google.protobuf.RpcController;
import com.google.protobuf.ServiceException;
import com.zhuaiwa.api.Common.TestRequest;
import com.zhuaiwa.api.Common.TestResponse;
import com.zhuaiwa.api.Common.TestSvc;

public class SyncTestSvc implements TestSvc.BlockingInterface {
    final ByteString payload = ByteString.copyFrom(new byte[100]);
    @Override
    public TestResponse test(RpcController controller, TestRequest request) throws ServiceException {
        return TestResponse.newBuilder().setPayload(payload).build();
    }

}
