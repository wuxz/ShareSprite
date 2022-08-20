package com.zhuaiwa.session.search.json;

import java.io.IOException;
import java.io.Writer;

import org.json.simple.JSONObject;
import org.json.simple.JSONValue;

public class ContentSelf extends Content {

	/**
	 * 
	 */
	private static final long serialVersionUID = -4402460846903012508L;

	public ContentSelf(String body) {
		super(Content.ContentType.self.name());
		this.body = body;
	}

	public ContentSelf() {
		super(Content.ContentType.self.name());
	}

	private String body = "";

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
		obj.put("body", body);
		JSONValue.writeJSONString(obj, out);
	}

	@Override
	public String toString() {
		return "ContentSelf [body=" + body + "]";
	}

}
