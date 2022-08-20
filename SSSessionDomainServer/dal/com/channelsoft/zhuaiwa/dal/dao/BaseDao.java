package com.channelsoft.zhuaiwa.dal.dao;

import java.util.List;
import java.util.Map;

import com.channelsoft.zhuaiwa.dal.domain.BaseObject;
import com.channelsoft.zhuaiwa.dal.exception.DALException;

public interface BaseDao<T extends BaseObject> {
	public void persist(T t) throws DALException;
	String insert(T t) throws DALException;
	void delete(String id) throws DALException;
	T get(String id) throws DALException;
	
//	Map<String, String> insert(List<T> objlist) throws CommandException;
	void delete(List<String> idlist) throws DALException;
	Map<String, T> get(List<String> idlist) throws DALException;
}
