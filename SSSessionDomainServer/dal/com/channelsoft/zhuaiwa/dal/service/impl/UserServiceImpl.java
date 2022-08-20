package com.channelsoft.zhuaiwa.dal.service.impl;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Map;

import org.apache.log4j.Logger;
import org.omg.CORBA.BooleanHolder;

import com.channelsoft.zhuaiwa.dal.dao.impl.AccountDaoImpl;
import com.channelsoft.zhuaiwa.dal.dao.impl.EmailAccountDaoImpl;
import com.channelsoft.zhuaiwa.dal.dao.impl.FollowerDaoImpl;
import com.channelsoft.zhuaiwa.dal.dao.impl.FollowingDaoImpl;
import com.channelsoft.zhuaiwa.dal.dao.impl.PhoneAccountDaoImpl;
import com.channelsoft.zhuaiwa.dal.dao.impl.ProfileDaoImpl;
import com.channelsoft.zhuaiwa.dal.domain.Account;
import com.channelsoft.zhuaiwa.dal.domain.EmailAccount;
import com.channelsoft.zhuaiwa.dal.domain.Follower;
import com.channelsoft.zhuaiwa.dal.domain.Following;
import com.channelsoft.zhuaiwa.dal.domain.PhoneAccount;
import com.channelsoft.zhuaiwa.dal.domain.Profile;
import com.channelsoft.zhuaiwa.dal.domain.Follower.FollowerInfo;
import com.channelsoft.zhuaiwa.dal.domain.Following.FollowingInfo;
import com.channelsoft.zhuaiwa.dal.domain.Inviter.InviterInfo;
import com.channelsoft.zhuaiwa.dal.domain.Inviting.InvitingInfo;
import com.channelsoft.zhuaiwa.dal.exception.DALException;
import com.channelsoft.zhuaiwa.dal.exception.NoSuchObjectException;
import com.channelsoft.zhuaiwa.dal.service.UserService;

public class UserServiceImpl implements UserService {
	private static Logger logger = Logger.getLogger(UserServiceImpl.class);

	/*
	 * dao
	 */
	
	private AccountDaoImpl accountDao;
	private EmailAccountDaoImpl emailAccountDao;
	private PhoneAccountDaoImpl phoneAccountDao;
	private FollowingDaoImpl followingDao;
	private FollowerDaoImpl followerDao;
	private ProfileDaoImpl profileDao;
	
	public AccountDaoImpl getAccountDao() {
		return accountDao;
	}

	public void setAccountDao(AccountDaoImpl accountDao) {
		this.accountDao = accountDao;
	}

	public EmailAccountDaoImpl getEmailAccountDao() {
		return emailAccountDao;
	}

	public void setEmailAccountDao(EmailAccountDaoImpl emailAccountDao) {
		this.emailAccountDao = emailAccountDao;
	}

	public PhoneAccountDaoImpl getPhoneAccountDao() {
		return phoneAccountDao;
	}

	public void setPhoneAccountDao(PhoneAccountDaoImpl phoneAccountDao) {
		this.phoneAccountDao = phoneAccountDao;
	}
	
	public FollowingDaoImpl getFollowingDao() {
		return followingDao;
	}

	public void setFollowingDao(FollowingDaoImpl followingDao) {
		this.followingDao = followingDao;
	}

	public FollowerDaoImpl getFollowerDao() {
		return followerDao;
	}

	public void setFollowerDao(FollowerDaoImpl followerDao) {
		this.followerDao = followerDao;
	}

	public ProfileDaoImpl getProfileDao() {
		return profileDao;
	}

	public void setProfileDao(ProfileDaoImpl profileDao) {
		this.profileDao = profileDao;
	}
	
	/*
	 * function
	 */

	public String getUserIdByEmail(String email) throws DALException {
		EmailAccount ea = emailAccountDao.get(email);
		if (ea == null)
			return null;
		return ea.getUserid();
	}
	
	public String getUserIdByPhone(String phoneNumber) throws DALException {
		PhoneAccount pa = phoneAccountDao.get(phoneNumber);
		if (pa == null)
			return null;
		return pa.getUserid();
	}
	
	public Account getAccount(String userid) throws DALException {
		return accountDao.get(userid);
	}

	public Map<String, Account> getAccount(List<String> userids) throws DALException {
		return accountDao.get(userids);
	}

	public boolean isExistAccount(String userid) throws DALException {
		return accountDao.get(userid) != null;
	}
	
	public boolean isExistAccountByEmail(String email) throws DALException {
		return getUserIdByEmail(email) != null;
	}
	
	public boolean isExistAccountByPhone(String phoneNumber) throws DALException {
		return getUserIdByPhone(phoneNumber) != null;
	}
	
	public Map<String, Account> getAccountByEmail(List<String> emails) throws DALException {
		Map<String, EmailAccount> emailAccountMap = emailAccountDao.get(emails);
		if (emailAccountMap == null)
			return null;
		List<String> userids = new ArrayList<String>();
		for (EmailAccount emailAccount : emailAccountMap.values()) {
			userids.add(emailAccount.getUserid());
		}
		return getAccount(userids);
	}

	public Map<String, Account> getAccountByPhone(List<String> phoneNumbers) throws DALException {
		Map<String, PhoneAccount> phoneAccounts = phoneAccountDao.get(phoneNumbers);
		if (phoneAccounts == null)
			return null;
		List<String> userids = new ArrayList<String>();
		for (PhoneAccount phoneAccount : phoneAccounts.values()) {
			userids.add(phoneAccount.getUserid());
		}
		return getAccount(userids);
	}
	
	public Account getAccountByEmail(String email) throws DALException {
		Map<String, Account> r = getAccountByEmail(Arrays.asList(email));
		if (r == null)
			return null;
		return r.get(email);
	}
	
	public Account getAccountByPhone(String phoneNumber) throws DALException {
		Map<String, Account> r = getAccountByPhone(Arrays.asList(phoneNumber));
		if (r == null)
			return null;
		return r.get(phoneNumber);
	}
	
	public String CreateAccount(Account account) throws DALException {
		String userid = accountDao.insert(account);
		if (userid == null)
			return null;
		
		if (account.getEmail() != null) {
			EmailAccount ea = new EmailAccount();
			ea.setEmail(account.getEmail());
			ea.setUserid(userid);
			ea.setPassword(account.getPassword());
			emailAccountDao.insert(ea);
		}
		
		if (account.getPhoneNumber() != null) {
			PhoneAccount pa = new PhoneAccount();
			pa.setPhoneNumber(account.getPhoneNumber());
			pa.setUserid(userid);
			pa.setPassword(account.getPassword());
			phoneAccountDao.insert(pa);
		}
		
		return userid;
	}
	
	public void ChangePassword(String userid, String newPassword) throws DALException {
		
		// 更新Account
		Account account = getAccount(userid);
		if (account == null) {
			throw new NullPointerException();
		}
		account.setPassword(newPassword);
		accountDao.insert(account);
		
		// 更新EmailAccount
		if (account.getEmail() != null) {
			EmailAccount ea = emailAccountDao.get(account.getEmail());
			if (ea != null) {
				ea.setPassword(newPassword);
				emailAccountDao.insert(ea);
			}
		}
		
		// 更新PhoneAccount
		if (account.getPhoneNumber() != null) {
			PhoneAccount pa = phoneAccountDao.get(account.getPhoneNumber());
			if (pa != null) {
				pa.setPassword(newPassword);
				phoneAccountDao.insert(pa);
			}
		}
	}
	
	public void BindAccountToPhone(String userid, String newPhoneNumber) throws DALException {
		Account a = accountDao.get(userid);
		if (a == null) {
			throw new NoSuchObjectException("帐号不存在。");
		}
		
		PhoneAccount pa = null;
		
		// 删除对旧号码的绑定
		if (a.getPhoneNumber() != null) {
			pa = phoneAccountDao.get(a.getPhoneNumber());
			if (pa != null) {
				phoneAccountDao.delete(a.getPhoneNumber());
			}
		}
		
		// 更新Account表
		a.setPhoneNumber(newPhoneNumber);
		accountDao.insert(a);
		
		// 绑定新号码
		if (pa == null) {
			pa = new PhoneAccount();
			pa.setUserid(userid);
			pa.setPassword(a.getPassword());
		}
		pa.setPhoneNumber(newPhoneNumber);
		
		phoneAccountDao.insert(pa);
	}
	
	public void BindAccountToEmail(String userid, String newEmail) throws DALException {
		Account a = accountDao.get(userid);
		if (a == null) {
			throw new NoSuchObjectException("帐号不存在。");
		}
		
		EmailAccount ea = null;
		
		// 删除对旧邮件的绑定
		if (a.getEmail() != null) {
			ea = emailAccountDao.get(a.getEmail());
			if (ea != null) {
				emailAccountDao.delete(a.getEmail());
			}
		}
		
		// 更新Account表
		a.setEmail(newEmail);
		accountDao.insert(a);
		
		// 绑定新邮件
		if (ea == null) {
			ea = new EmailAccount();
			ea.setUserid(userid);
			ea.setPassword(a.getPassword());
		}
		ea.setEmail(newEmail);
		
		emailAccountDao.insert(ea);
	}
	
	public void setProfile(Profile profile) throws DALException {
		profileDao.insert(profile);
	}
	
	public Profile getProfile(String userid) throws DALException {
		return profileDao.get(userid);
	}
	
	public Map<String, Profile> getProfile(List<String> userids) throws DALException {
		return profileDao.get(userids);
	}
	
	public void follow(String userid, List<String> followings) throws DALException {
		
		// 更新Following
		Following following = new Following();
		following.setUserid(userid);
		for (String followingid : followings) {
			FollowingInfo fi = new FollowingInfo();
			fi.setFollowingid(followingid);
			following.getFollowings().add(fi);
		}
		
		followingDao.insert(following);
		
		// 分别更新Follower
		for (String followingid : followings) {
			Follower follower = new Follower();
			follower.setUserid(followingid);
			FollowerInfo fi = new FollowerInfo();
			fi.setFollowerid(userid);
			follower.getFollowers().add(fi);
			
			followerDao.insert(follower);
		}
	}
	
	public void unfollow(String userid, List<String> followings) throws DALException {
		
	}
	
	public List<FollowingInfo> getFollowing(String userid) throws DALException {
		return null;
	}
	
	public List<FollowerInfo> getFollower(String userid, String lastFollowerId, int count, BooleanHolder eol) throws DALException {
		return null;
	}
	
	public List<FollowingInfo> getFollowing(String userid, List<String> followingids) throws DALException {
		return null;
	}
	
	public List<FollowerInfo> getFollower(String userid, List<String> followerids) throws DALException {
		return null;
	}
	
	public void invite(String userid, List<String> inviting_users) throws DALException {
		
	}
	
	public List<InvitingInfo> getInviting(String userid) throws DALException {
		return null;
	}
	
	public List<InviterInfo> getInviter(String userid) throws DALException {
		return null;
	}
	
	public List<InvitingInfo> getInviting(String userid, List<String> inviting_users) throws DALException {
		return null;
	}
}
