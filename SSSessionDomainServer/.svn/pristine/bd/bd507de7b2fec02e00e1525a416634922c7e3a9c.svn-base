package com.channelsoft.zhuaiwa.dal.domain;

import java.nio.ByteBuffer;

import org.apache.cassandra.utils.GuidGenerator;

public class BaseObject {

	static public ByteBuffer genGuid() {
		ByteBuffer array = GuidGenerator.guidAsBytes();
        
//        StringBuilder sb = new StringBuilder();
//        for (int j = 0; j < array.length; ++j) {
//            int b = array[j] & 0xFF;
//            if (b < 0x10) sb.append('0');
//            sb.append(Integer.toHexString(b));
//        }
        return array;
	}
	
	public ByteBuffer generateId() {
		return genGuid();
	}
	
//	public String getId();
}
