package com.zhuaiwa.session.search.json;

import java.io.IOException;
import java.io.Writer;

import org.json.simple.JSONObject;
import org.json.simple.JSONValue;

public class ContentMedia extends Content {

	private static final long serialVersionUID = -748455328868726518L;
	private String media_type;
	private String url;
	private String name;

	public ContentMedia(String contentType) {
		super(contentType);
	}
	
	public ContentMedia(String contentType,String media_type,String url,String name){
		super(contentType);
		this.media_type = media_type;
		this.url = url;
		this.name = name;
	}

	@Override
	public void writeJSONString(Writer out) throws IOException {
		JSONObject obj = new JSONObject();
		obj.put("content_type", this.getContent_type());
		obj.put("media_type", this.media_type);
		obj.put("url", this.url);
		obj.put("name", this.name);
		JSONValue.writeJSONString(obj, out);
	}

}
