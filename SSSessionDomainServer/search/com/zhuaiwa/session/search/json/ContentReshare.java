package com.zhuaiwa.session.search.json;

import java.io.IOException;
import java.io.Writer;

import org.json.simple.JSONObject;
import org.json.simple.JSONValue;

public class ContentReshare extends Content{

	public ContentReshare() {
		super(ContentType.reshare.name());
		// TODO Auto-generated constructor stub
	}

	/**
	 * 
	 */
	private static final long serialVersionUID = 7692774762168385938L;

	@Override
	public void writeJSONString(Writer out) throws IOException {
		JSONObject obj = new JSONObject();
		obj.put("content_type", this.getContent_type());
		JSONValue.writeJSONString(obj, out);
	}

}
