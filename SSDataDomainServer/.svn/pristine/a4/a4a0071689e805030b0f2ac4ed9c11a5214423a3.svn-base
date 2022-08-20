package com.zhuaiwa.session.search.json;

import java.io.IOException;
import java.io.Writer;

import org.json.simple.JSONObject;
import org.json.simple.JSONValue;

public class ContentPageMessage extends Content{

	/**
	 * 
	 */
	private static final long serialVersionUID = -188915592093885666L;
	private String title;
	private String body;
	public ContentPageMessage(String title, String body) {
		super(ContentType.pageContent.name());
		this.title = title;
		this.body = body;
	}
	public String getTitle() {
		return title;
	}

	public void setTitle(String title) {
		this.title = title;
	}

	public String getBody() {
		return body;
	}

	public void setBody(String body) {
		this.body = body;
	}

	public void writeJSONString(Writer out) throws IOException {
		JSONObject obj = new JSONObject();
		obj.put("content_type", this.getContent_type());
		obj.put("title", this.title);
		obj.put("body", this.body);
		JSONValue.writeJSONString(obj, out);
	}
}
