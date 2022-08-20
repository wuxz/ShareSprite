package com.zhuaiwa.session;

import java.util.List;
import java.util.Set;

import com.zhuaiwa.api.Common.SSAccount;
import com.zhuaiwa.session.SessionManager.Session;

public interface SessionManagerMBean {
	public int getChannels();

	public int getSessions();

	public String getLoginUsers();
	
	public SSAccount getAccountBySid(String sid);
	
	public Set<Session> getLoginSessions();

	public void setMaxSession(int max);
}
