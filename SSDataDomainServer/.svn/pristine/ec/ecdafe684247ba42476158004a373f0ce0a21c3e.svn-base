package com.zhuaiwa.dd.dao;

import java.util.Iterator;
import java.util.List;
import java.util.Map;

import com.zhuaiwa.dd.domain.BaseObject;
import com.zhuaiwa.dd.exception.DALException;

public interface BaseDao<T extends BaseObject> {
	void insert(T obj);
	void delete(String id);
	T get(String id) throws DALException;
	
	void insert(List<T> objlist);
	void delete(List<String> idlist);
	Map<String, T> get(List<String> idlist);
	
    Iterator<T> getAll();
	Iterator<T> getAll(int pageSize);
}
