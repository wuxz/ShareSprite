package com.zhuaiwa.dd.dao.impl;

import com.zhuaiwa.dd.domain.Group;


public class GroupDaoImpl extends BaseDaoImpl<Group> {
	public String genGroupId() {
		return genGuid();
	}
}
