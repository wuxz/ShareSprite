package com.zhuaiwa.dd.dao.impl;

import com.zhuaiwa.dd.domain.Account;

public class AccountDaoImpl extends BaseDaoImpl<Account> {
	public String genUserId() {
		return genGuid();
	}
}
