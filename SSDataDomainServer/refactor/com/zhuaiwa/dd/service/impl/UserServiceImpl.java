package com.zhuaiwa.dd.service.impl;

import java.lang.management.ManagementFactory;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.management.MBeanServer;
import javax.management.ObjectName;

import org.apache.commons.lang.StringUtils;
import org.omg.CORBA.BooleanHolder;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.zhuaiwa.dd.dao.impl.AccountDaoImpl;
import com.zhuaiwa.dd.dao.impl.EmailAccountDaoImpl;
import com.zhuaiwa.dd.dao.impl.FollowerDaoImpl;
import com.zhuaiwa.dd.dao.impl.FollowingDaoImpl;
import com.zhuaiwa.dd.dao.impl.PhoneAccountDaoImpl;
import com.zhuaiwa.dd.dao.impl.ProfileDaoImpl;
import com.zhuaiwa.dd.domain.Account;
import com.zhuaiwa.dd.domain.EmailAccount;
import com.zhuaiwa.dd.domain.FollowInfo;
import com.zhuaiwa.dd.domain.Follower;
import com.zhuaiwa.dd.domain.Following;
import com.zhuaiwa.dd.domain.PhoneAccount;
import com.zhuaiwa.dd.domain.Profile;
import com.zhuaiwa.dd.exception.DALException;
import com.zhuaiwa.dd.manage.UserManageMXBean;
import com.zhuaiwa.dd.service.UserService;

public class UserServiceImpl implements UserService, UserManageMXBean {
	private static Logger logger = LoggerFactory.getLogger(UserServiceImpl.class);

	/*
	 * dao
	 */
	
	private AccountDaoImpl accountDao;
	private EmailAccountDaoImpl emailAccountDao;
	private PhoneAccountDaoImpl phoneAccountDao;
	private FollowingDaoImpl followingDao;
	private FollowerDaoImpl followerDao;
	private ProfileDaoImpl profileDao;
	
	private DataServiceImpl dataService;
	
	public DataServiceImpl getDataService() {
		return dataService;
	}

	public void setDataService(DataServiceImpl dataService) {
		this.dataService = dataService;
	}

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
	
	public UserServiceImpl() {
		MBeanServer mbs = ManagementFactory.getPlatformMBeanServer();
        String mbeanName = "com.channelsoft.ss.manage:type=UserManage";
        try
        {
            mbs.registerMBean(this, new ObjectName(mbeanName));
        }
        catch (Exception e)
        {
            throw new RuntimeException(e);
        }
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
		Map<String, EmailAccount> indexmap = emailAccountDao.get(emails);
		if (indexmap == null)
			return null;
		List<String> userids = new ArrayList<String>();
		for (EmailAccount emailAccount : indexmap.values()) {
			userids.add(emailAccount.getUserid());
		}
		Map<String, Account> userAccountMap = getAccount(userids);
		if (userAccountMap == null)
			return null;
		Map<String, Account> emailAccountMap = new HashMap<String, Account>();
		for (String email : emails) {
			EmailAccount ea = indexmap.get(email);
			if (ea == null)
				continue;
			Account a = userAccountMap.get(ea.getUserid());
			if (a == null)
				continue;
			emailAccountMap.put(email, a);
		}
		return emailAccountMap;
	}

	public Map<String, Account> getAccountByPhone(List<String> phoneNumbers) throws DALException {
		Map<String, PhoneAccount> indexmap = phoneAccountDao.get(phoneNumbers);
		if (indexmap == null)
			return null;
		List<String> userids = new ArrayList<String>();
		for (PhoneAccount phoneAccount : indexmap.values()) {
			userids.add(phoneAccount.getUserid());
		}
		Map<String, Account> userAccountMap = getAccount(userids);
		if (userAccountMap == null)
			return null;
		Map<String, Account> phoneAccountMap = new HashMap<String, Account>();
		for (String phone : phoneNumbers) {
			PhoneAccount pa = indexmap.get(phone);
			if (pa == null)
				continue;
			Account a = userAccountMap.get(pa.getUserid());
			if (a == null)
				continue;
			phoneAccountMap.put(phone, a);
		}
		return phoneAccountMap;
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
		if (account.getUserid() == null) {
			account.setUserid(dataService.genUserId());
		}
		
		accountDao.insert(account);
		
		if (account.getEmail() != null) {
			EmailAccount ea = new EmailAccount();
			ea.setEmail(account.getEmail());
			ea.setUserid(account.getUserid());
			emailAccountDao.insert(ea);
		}
		
		if (account.getPhoneNumber() != null) {
			PhoneAccount pa = new PhoneAccount();
			pa.setPhoneNumber(account.getPhoneNumber());
			pa.setUserid(account.getUserid());
			phoneAccountDao.insert(pa);
		}
		
		return account.getUserid();
	}
	
	public void BindAccountToPhone(String userid, String newPhoneNumber) throws DALException {
		Account a = accountDao.get(userid);
		if (a == null) {
			throw new DALException("帐号不存在。");
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
		}
		pa.setPhoneNumber(newPhoneNumber);
		
		phoneAccountDao.insert(pa);
	}
	
	public void BindAccountToEmail(String userid, String newEmail) throws DALException {
		Account a = accountDao.get(userid);
		if (a == null) {
			throw new DALException("帐号不存在。");
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
			FollowInfo fi = new FollowInfo();
			fi.setUserid(followingid);
			following.getFollowings().add(fi);
//			following.getFollowings().put(followingid, System.currentTimeMillis());
		}
		
		followingDao.insert(following);
		
		// 分别更新Follower
		for (String followingid : followings) {
			Follower follower = new Follower();
			follower.setUserid(followingid);
			FollowInfo fi = new FollowInfo();
			fi.setUserid(userid);
			follower.getFollowers().add(fi);
//			follower.getFollowers().put(userid, System.currentTimeMillis());
			
			followerDao.insert(follower);
		}
	}
	
	public void unfollow(String userid, List<String> followings) throws DALException {
		
	}
	
	public List<String> getFollowing(String userid) throws DALException {
		return null;
	}
	
	public List<String> getFollower(String userid, String lastFollowerId, int count, BooleanHolder eol) throws DALException {
		return null;
	}
	
	public List<String> getFollowing(String userid, List<String> followingids) throws DALException {
		return null;
	}
	
	public List<String> getFollower(String userid, List<String> followerids) throws DALException {
		return null;
	}
	
	public void invite(String userid, List<String> inviting_users) throws DALException {
		
	}
	
	public List<String> getInviting(String userid) throws DALException {
		return null;
	}
	
	public List<String> getInviter(String userid) throws DALException {
		return null;
	}
	
	public List<String> getInviting(String userid, List<String> inviting_users) throws DALException {
		return null;
	}

	@Override
	public Profile getProfileByEmail(String email) throws DALException {
		String userid = this.getUserIdByEmail(email);
		if (userid == null)
			return null;
		return profileDao.get(userid);
	}

	@Override
	public Profile getProfileByPhone(String phoneNumber) throws DALException {
		String userid = this.getUserIdByPhone(phoneNumber);
		if (userid == null)
			return null;
		return profileDao.get(userid);
	}

	@Override
	public void ResetPassword(String userid) throws DALException {
		Account a = accountDao.get(userid);
		if (a == null)
			throw new DALException("此用户不存在");
		a.setPassword("e10adc3949ba59abbe56e057f20f883e"); //123456
		accountDao.insert(a);
	}

	@Override
	public void ResetPasswordByEmail(String email) throws DALException {
		Account a = getAccountByEmail(email);
		if (a == null)
			throw new DALException("此用户不存在");
		a.setPassword("e10adc3949ba59abbe56e057f20f883e"); //123456
		accountDao.insert(a);
	}

	@Override
	public void ResetPasswordByPhone(String phoneNumber) throws DALException {
		Account a = getAccountByPhone(phoneNumber);
		if (a == null)
			throw new DALException("此用户不存在");
		a.setPassword("e10adc3949ba59abbe56e057f20f883e"); //123456
		accountDao.insert(a);
	}
	
	@Override
	public void deleteAccount(String userid) throws DALException {
		try {
			Account a = accountDao.get(userid);
			if (StringUtils.isNotEmpty(a.getEmail())) {
				emailAccountDao.delete(a.getEmail());
			}
			if (StringUtils.isNotEmpty(a.getPhoneNumber())) {
				phoneAccountDao.delete(a.getPhoneNumber());
			}
		} catch (Exception e) {
			// ignore
		}
		accountDao.delete(userid);
	}
	
	@Override
	public void deleteAccountByEmail(String email) throws DALException {
		String userid = this.getUserIdByEmail(email);
		if (userid == null)
			throw new DALException("此用户不存在");
		deleteAccount(userid);
		
		try {
			emailAccountDao.delete(email);
		} catch (Exception e) {
			// ignore
		}
	}
	
	@Override
	public void deleteAccountByPhone(String phoneNumber) throws DALException {
		String userid = this.getUserIdByPhone(phoneNumber);
		if (userid == null)
			throw new DALException("此用户不存在");
		deleteAccount(userid);

		try {
			phoneAccountDao.delete(phoneNumber);
		} catch (Exception e) {
			// ignore
		}
	}
}
