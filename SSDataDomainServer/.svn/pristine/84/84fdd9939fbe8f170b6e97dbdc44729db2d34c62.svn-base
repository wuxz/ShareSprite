package com.zhuaiwa.session.search.json;

import java.io.Serializable;

import org.json.simple.JSONStreamAware;

public abstract class Content implements JSONStreamAware, Serializable{

	/**
	 * 
	 */
	private static final long serialVersionUID = 4487654670044290750L;

	public Content(String contentType) {
		super();
		content_type = contentType;
	}

	public enum ContentType{
		self, file, external,reshare,html,media,pageContent
	}
	
	private String content_type;

	public String getContent_type() {
		return content_type;
	}

	public void setContent_type(String contentType) {
		content_type = contentType;
	}
}
