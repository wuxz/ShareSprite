package com.zhuaiwa.session.search.json;

import java.io.IOException;
import java.io.Writer;

import org.json.simple.JSONObject;
import org.json.simple.JSONValue;

public class ContentExternal extends Content {

	/**
	 * 
	 */
	private static final long serialVersionUID = -654667169050223654L;
	private String title;
	private String body;

	public ContentExternal(String title, String body) {
		super(ContentType.external.name());
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

	@Override
	public void writeJSONString(Writer out) throws IOException {
		JSONObject obj = new JSONObject();
		obj.put("content_type", this.getContent_type());
		obj.put("title", this.title);
		obj.put("body", this.body);
		JSONValue.writeJSONString(obj, out);
	}

}
