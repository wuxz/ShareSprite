package com.zhuaiwa.session.search.json;

import java.io.IOException;
import java.io.Serializable;
import java.io.StringWriter;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

import org.json.simple.JSONArray;
import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;

import com.zhuaiwa.session.search.json.Content.ContentType;

public class Contents implements Serializable
{

	/**
	 * 
	 */
	private static final long serialVersionUID = -8574950112963745615L;

	public static void main(String[] args)
	{
		String jsonText = "{\"contents\":[{\"body\":\"测试\",\"content_type\":\"self\"}]}";
		Contents cs = new Contents();
		cs.fromJson(jsonText);
		System.out.println(cs.getSelfContent().toString());
		System.out.println(cs.toJson());
		System.out.println("done!");
	}

	List<Content> contents;

	public Contents()
	{
		super();
	}

	public Contents(String contentsJson)
	{
		super();
		fromJson(contentsJson);
	}

	public void addContent(Content c)
	{
		if (contents == null)
		{
			contents = new ArrayList<Content>();
		}
		contents.add(c);
	}

	public void fromJson(String jsonText)
	{
		JSONParser parser = new JSONParser();
		try
		{
			JSONObject contentsObj = (JSONObject) parser.parse(jsonText);
			if ((contentsObj != null) && contentsObj.containsKey("contents"))
			{
				JSONArray array = (JSONArray) contentsObj.get("contents");
				// Object obj = parser.parse(contentsJsonText);
				// JSONArray array = (JSONArray) obj;
				Iterator<JSONObject> iter = array.iterator();
				while (iter.hasNext())
				{
					JSONObject jsonObj = iter.next();
					if (jsonObj.containsKey("content_type"))
					{
						String contentType = (String) jsonObj
								.get("content_type");
						if (contentType.equals(ContentType.self.name()))
						{
							if (jsonObj.containsKey("body"))
							{
								this.addContent(new ContentSelf(
										(String) jsonObj.get("body")));
							}
						}
						else if (contentType.equals(ContentType.reshare.name()))
						{
							this.addContent(new ContentReshare());
						}
						else if (contentType.equals(ContentType.html.name()))
						{
							this.addContent(new ContentHtml((String) jsonObj
									.get("title"),
									(String) jsonObj.get("body"),
									(String) jsonObj.get("url")));
						}
						else if (contentType.equals(ContentType.file.name()))
						{
							ContentFile cf = new ContentFile();
							if (jsonObj.containsKey("files"))
							{
								JSONArray arr = (JSONArray) jsonObj
										.get("files");
								Iterator<JSONObject> iter_tmp = arr.iterator();
								while (iter_tmp.hasNext())
								{
									JSONObject fileObj = iter_tmp.next();
									cf.addFile(new FileObj((String) fileObj
											.get("name"), (String) fileObj
											.get("linker"), (Long) fileObj
											.get("size")));
								}
								this.addContent(cf);
							}

						}
						else if (contentType
								.equals(ContentType.external.name()))
						{
							this.addContent(new ContentExternal(
									(String) jsonObj.get("title"),
									(String) jsonObj.get("body")));
						}
						else if (contentType.equals(ContentType.media.name()))
						{
							this.addContent(new ContentMedia(ContentType.media
									.name(),
									(String) jsonObj.get("media_type"),
									(String) jsonObj.get("url"),
									(String) jsonObj.get("name")));
						}
						else if (contentType.equals(ContentType.pageContent
								.name()))
						{
							this.addContent(new ContentPageMessage(
									(String) jsonObj.get("title"),
									(String) jsonObj.get("body")));
						}
					}
				}
			}

		}
		catch (Exception e)
		{
			throw new RuntimeException(e);
		}

	}

	public List<Content> getContents()
	{
		return contents;
	}

	public String getFileContent()
	{
		if ((contents == null) || contents.isEmpty())
		{
			return null;
		}
		for (Content c : contents)
		{
			if (c.getContent_type().equalsIgnoreCase(
					Content.ContentType.file.name()))
			{
				ContentFile f = (ContentFile) c;
				if (f.files != null)
				{
					StringBuffer sb = new StringBuffer();
					sb.append("文件：");
					for (FileObj file : f.files)
					{
						sb.append(file.getName() + ",");
					}
					return sb.toString();
				}
			}
		}
		return null;
	}

	public String getHtmlContent()
	{
		if ((contents == null) || contents.isEmpty())
		{
			return null;
		}
		for (Content c : contents)
		{
			if (c.getContent_type().equalsIgnoreCase(
					Content.ContentType.html.name()))
			{
				ContentHtml h = (ContentHtml) c;
				StringBuffer sb = new StringBuffer();
				sb.append("网页：");
				if ((h.getBody() != null) && !h.getBody().isEmpty())
				{
					sb.append(h.getBody());
				}
				sb.append("来源：");
				sb.append("<a href=\"");
				sb.append(h.getUrl() == null ? "#" : h.getUrl());
				sb.append("\">");
				sb.append(h.getTitle() == null ? "无标题" : h.getTitle());
				sb.append("</a>");
				return sb.toString();
			}
		}
		return null;
	}

	public String getSelfContent()
	{
		if ((contents == null) || contents.isEmpty())
		{
			return null;
		}
		for (Content c : contents)
		{
			if (c.getContent_type().equalsIgnoreCase(
					Content.ContentType.self.name()))
			{
				return ((ContentSelf) c).getBody();
			}
		}
		return null;
	}

	public void setContents(List<Content> contents)
	{
		this.contents = contents;
	}

	public String toJson()
	{
		if ((this.contents != null) && !contents.isEmpty())
		{
			JSONArray jsonContents = new JSONArray();
			for (Content c : contents)
			{
				jsonContents.add(c);
			}
			// StringWriter tmp = new StringWriter();
			// try {
			// jsonContents.writeJSONString(tmp);
			// } catch (IOException e1) {
			// e1.printStackTrace();
			// }
			JSONObject obj = new JSONObject();
			obj.put("contents", jsonContents);
			StringWriter out = new StringWriter();
			try
			{
				obj.writeJSONString(out);
			}
			catch (IOException e)
			{
	            throw new RuntimeException(e);
			}
			return out.toString();
		}
		return null;
	}
}
