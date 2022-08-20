package com.zhuaiwa.dd.manage;

import com.zhuaiwa.dd.domain.Account;
import com.zhuaiwa.dd.domain.Profile;
import com.zhuaiwa.dd.exception.DALException;

public interface UserManageMXBean {
	Account getAccount(String userid) throws DALException;
	Account getAccountByEmail(String email) throws DALException;
	Account getAccountByPhone(String phoneNumber) throws DALException;
	Profile getProfile(String userid) throws DALException;
	Profile getProfileByPhone(String phoneNumber) throws DALException;
	Profile getProfileByEmail(String email) throws DALException;
	void ResetPassword(String userid) throws DALException;
	void ResetPasswordByEmail(String email) throws DALException;
	void ResetPasswordByPhone(String phoneNumber) throws DALException;
	
	void deleteAccount(String userid) throws DALException;
	void deleteAccountByEmail(String email) throws DALException;
	void deleteAccountByPhone(String phoneNumber) throws DALException;
}
