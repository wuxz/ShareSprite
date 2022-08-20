package com.zhuaiwa.dd.dao;

import java.util.Iterator;
import java.util.List;
import java.util.Map;

import com.zhuaiwa.dd.domain.BaseObject;
import com.zhuaiwa.dd.exception.DALException;

public interface BaseDao<T extends BaseObject> {
	void insert(T t) throws DALException;
	void delete(String id) throws DALException;
	T get(String id) throws DALException;
	
	void insert(List<T> objlist) throws DALException;
	void delete(List<String> idlist) throws DALException;
	Map<String, T> get(List<String> idlist) throws DALException;
	
	Iterator<T> getAll() throws DALException;
	Iterator<T> getAll(int pageCount) throws DALException;
}
