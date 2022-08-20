package com.zhuaiwa.session.search;

import java.util.Iterator;

import org.json.simple.JSONArray;
import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;

public class SearchTest {

	public static void main(String[] args) throws Exception {
		//		String query = "favorTags:iphone";
		//		URL solrURL = new URL("http://is0:8090/solr/select/?q=" + query);
		//		BufferedReader in = new BufferedReader(new InputStreamReader(solrURL
		//				.openStream()));
		//
		//		String inputLine;
		//		while ((inputLine = in.readLine()) != null)
		//			System.out.println(inputLine);
		//		in.close();
		JSONParser parser = new JSONParser();
		String body = "{\"contents\":[{\"body\":\"asdfsadf\",\"content_type\":\"self\"}]}";
		JSONObject obj = (JSONObject)parser.parse(body);
		JSONArray contents = (JSONArray)obj.get("contents");
		for (Iterator<JSONObject> it = contents.iterator(); it.hasNext();)
		{
			JSONObject content = it.next();
			System.out.println(content.get("body") + "/" + content.get("content_type"));
		}
	}
}
