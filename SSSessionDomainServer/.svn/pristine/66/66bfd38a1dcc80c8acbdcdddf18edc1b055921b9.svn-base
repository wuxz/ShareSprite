package com.zhuaiwa.session.service;

import java.lang.reflect.Field;
import java.lang.reflect.Modifier;
import java.util.List;

import org.junit.Test;

import com.google.protobuf.Descriptors.Descriptor;
import com.google.protobuf.Descriptors.FieldDescriptor;
import com.google.protobuf.Descriptors.FileDescriptor;
import com.google.protobuf.Descriptors.FieldDescriptor.Type;
import com.google.protobuf.GeneratedMessage.GeneratedExtension;
import com.zhuaiwa.api.SSSessionDomain;
import com.zhuaiwa.api.Common.ApiNotification;
import com.zhuaiwa.api.Common.ApiRequest;
import com.zhuaiwa.api.Common.ApiResponse;
import com.zhuaiwa.api.Common.SSProfile;
import com.zhuaiwa.api.Rpc.GetFollowingResponse;
import com.zhuaiwa.api.Rpc.GetProfileResponse;
import com.zhuaiwa.api.Rpc.SetProfileRequest;
import com.zhuaiwa.api.util.ApiExtensionHelper;

public class ProtobufTest {
	@Test
	public void testSimpleField() {
		SSProfile.Builder b = SSProfile.newBuilder();
		b.setUserid("0001");
		b.setNickname("yaosw");
		
		FieldDescriptor fd_nickname = SSProfile.getDescriptor().findFieldByName("nickname");
		b = b.build().toBuilder();
		System.out.println("in new builder: " + b.getUserid());
		System.out.println("in new builder: " + b.getNickname());
		
		b.setField(fd_nickname, "aaa");

		SSProfile p = b.build();
		System.out.println(p.getUserid());
		System.out.println(p.getNickname());
	}

	@Test
	public void testComplexField() {
		SetProfileRequest.Builder b = SetProfileRequest.newBuilder();
		b.setProfile(SSProfile.newBuilder().setUserid("0001").setNickname("yaosw").build());
		
		FieldDescriptor fd_profile = SetProfileRequest.getDescriptor().findFieldByName("profile");
		FieldDescriptor fd_nickname = SSProfile.getDescriptor().findFieldByName("nickname");
		b = b.build().toBuilder();
		System.out.println("in new builder: " + b.getProfile().getUserid());
		System.out.println("in new builder: " + b.getProfile().getNickname());
		
		b.setField(fd_profile, ((SSProfile)b.getField(fd_profile)).toBuilder().setField(fd_nickname, "aaa").build());
		
		SSProfile p = b.build().getProfile();
		System.out.println(p.getUserid());
		System.out.println(p.getNickname());
	}

	@Test
	public void testRepeatedField() {
		GetProfileResponse.Builder b = GetProfileResponse.newBuilder();
		b.addProfileList(SSProfile.newBuilder().setUserid("0001").setNickname("yaosw").build());
		b.addProfileList(SSProfile.newBuilder().setUserid("0002").setNickname("xyz").build());

		FieldDescriptor fd_profile_list = GetProfileResponse.getDescriptor().findFieldByName("profile_list");
		FieldDescriptor fd_nickname = SSProfile.getDescriptor().findFieldByName("nickname");
		b = b.build().toBuilder();
		
		System.out.println("profile_list is repeated: " + fd_profile_list.isRepeated());
		System.out.println("in new builder: " + b.getProfileList(0).getUserid());
		System.out.println("in new builder: " + b.getProfileList(0).getNickname());
		System.out.println("in new builder: " + b.getProfileList(1).getUserid());
		System.out.println("in new builder: " + b.getProfileList(1).getNickname());
		
		b.setRepeatedField(fd_profile_list, 0, ((SSProfile)b.getRepeatedField(fd_profile_list, 0)).toBuilder().setField(fd_nickname, "aaa").build());
		b.setRepeatedField(fd_profile_list, 1, ((SSProfile)b.getRepeatedField(fd_profile_list, 1)).toBuilder().setField(fd_nickname, "bbb").build());
		List<SSProfile> p_list = b.build().getProfileListList();
		System.out.println(p_list.get(0).getUserid());
		System.out.println(p_list.get(0).getNickname());
		System.out.println(p_list.get(1).getUserid());
		System.out.println(p_list.get(1).getNickname());
	}

	@Test
	public void testFindFieldByType() {
		GetFollowingResponse response = GetFollowingResponse.getDefaultInstance();
		
		System.out.println(SSProfile.getDescriptor().getFullName());
		
		Descriptor d = response.getDescriptorForType();
		for (FieldDescriptor fd : d.getFields()) {
			if (fd.getType() == Type.MESSAGE) {
				System.out.println(fd.getMessageType().getFullName());
				if (fd.getMessageType().equals(SSProfile.getDescriptor())) {
					System.out.println("found it");
				}
			}
		}
	}
	
	@Test
	public void testProto() throws Exception {
		FileDescriptor fileDescriptor = SSSessionDomain.getDescriptor();
		for (FieldDescriptor fieldDescriptor : fileDescriptor.getExtensions()) {
//			if (fieldDescriptor.isExtension())
//				System.out.println(fieldDescriptor.getContainingType().getFullName());
//			System.out.println(fieldDescriptor.getName());
			if (fieldDescriptor.getContainingType().equals(ApiRequest.getDescriptor())) {
				System.out.println(fieldDescriptor.getName());
//			if (fieldDescriptor.getName().endsWith("Request")) {
				Field field = SSSessionDomain.class.getDeclaredField(fieldDescriptor.getName());
				if ((field.getModifiers() & Modifier.STATIC) == Modifier.STATIC) {
					ApiExtensionHelper.registerRequest((GeneratedExtension<ApiRequest, ?>)field.get(null));
				}
			}
			if (fieldDescriptor.getName().endsWith("Response")) {
				Field field = SSSessionDomain.class.getDeclaredField(fieldDescriptor.getName());
				if ((field.getModifiers() & Modifier.STATIC) == Modifier.STATIC) {
					ApiExtensionHelper.registerResponse((GeneratedExtension<ApiResponse, ?>)field.get(null));
				}
			}
			if (fieldDescriptor.getName().endsWith("Notification")) {
				Field field = SSSessionDomain.class.getDeclaredField(fieldDescriptor.getName());
				if ((field.getModifiers() & Modifier.STATIC) == Modifier.STATIC) {
					ApiExtensionHelper.registerNotification((GeneratedExtension<ApiNotification, ?>)field.get(null));
				}
			}
		}
	}
}
