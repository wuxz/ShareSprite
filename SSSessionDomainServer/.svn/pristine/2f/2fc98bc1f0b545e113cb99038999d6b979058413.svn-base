package com.zhuaiwa.session.search.domain;

import java.io.Serializable;

import org.apache.solr.client.solrj.beans.Field;

public abstract class SolrObj implements Serializable {

	/**
	 * 
	 */
	private static final long serialVersionUID = -2589397587344993103L;

	public enum SolrObjType {
		MESSAGE, USER, EDUCATION, WORK
	}

	@Field
	private int objType;

	@Field
	private String id;

	public SolrObj() {
		super();
	}

	public SolrObj(int objType) {
		super();
		this.objType = objType;
	}

	public int getObjType() {
		return objType;
	}

	public void setObjType(int objType) {
		this.objType = objType;
	}

	public String getId() {
		return id;
	}

	public void setId(String id) {
		this.id = id;
	}

}
