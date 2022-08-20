package com.zhuaiwa.dd.protobuf;

import com.zhuaiwa.api.SSDataDomain;
import com.zhuaiwa.api.util.ApiExtensionHelper;

public class DataDomainApiProtobufExtension {
    private static class RegisterOnce {
        static {
            ApiExtensionHelper.registerProto(SSDataDomain.getDescriptor(), SSDataDomain.class);
        }
    }
    public static void register() {
        new RegisterOnce();
    }
}
