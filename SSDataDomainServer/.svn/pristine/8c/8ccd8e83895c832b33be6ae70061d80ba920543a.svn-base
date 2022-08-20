package com.zhuaiwa.session.search.json;

import java.io.IOException;
import java.io.Writer;

import org.json.simple.JSONObject;
import org.json.simple.JSONValue;

public class ContentHtml extends Content {

	/**
	 * 
	 */
	private static final long serialVersionUID = 556585319901558227L;
	private String title;
	private String body;
	private String url;

	public ContentHtml(String title, String body, String url) {
		super(ContentType.html.name());
		this.title = title;
		this.url = url;
		this.body = body;
	}

	public String getTitle() {
		return title;
	}

	public String getBody() {
		return body;
	}

	public String getUrl() {
		return url;
	}

	@Override
	public void writeJSONString(Writer out) throws IOException {
		JSONObject obj = new JSONObject();
		obj.put("content_type", this.getContent_type());
		obj.put("title", this.title);
		obj.put("body", this.body);
		obj.put("url", this.url);
		JSONValue.writeJSONString(obj, out);
	}

	@Override
	public String toString() {
		return "ContentHtml [body=" + body + ", title=" + title + ", url="
				+ url + "]";
	}

}
