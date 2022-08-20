package com.zhuaiwa.session.search.domain;

import java.io.Serializable;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.StringTokenizer;

import org.apache.solr.client.solrj.beans.Field;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.zhuaiwa.api.Common.SSMessage;
import com.zhuaiwa.session.search.json.Content;
import com.zhuaiwa.session.search.json.Content.ContentType;
import com.zhuaiwa.session.search.json.ContentExternal;
import com.zhuaiwa.session.search.json.ContentFile;
import com.zhuaiwa.session.search.json.ContentHtml;
import com.zhuaiwa.session.search.json.ContentPageMessage;
import com.zhuaiwa.session.search.json.Contents;

public class Message extends SolrObj implements Serializable {
	private Logger LOG = LoggerFactory.getLogger(this.getClass());
	private static final HashMap<String, String> transferredSigns = new HashMap<String, String>();

	static {
		transferredSigns.put("&#160;", " ");
		transferredSigns.put("&#60;", "&lt;");
		transferredSigns.put("&#62;", "&gt;");
		transferredSigns.put("&#38;", "&amp;");
		transferredSigns.put("&#34;", "&quot;");
		transferredSigns.put("&#174;", "&reg;");
		transferredSigns.put("&#169;", "&copy;");
		transferredSigns.put("&#8482;", "&trade;");
		transferredSigns.put("&#8194;", "&ensp;");
		transferredSigns.put("&#8195;", "&emsp;");
	}

	/**
	 * 去处html标记
	 * 
	 * @param s
	 * @return
	 */
	public static String removeHTML(String s) {
		String s1 = "";
		try {
			StringTokenizer stringtokenizer = new StringTokenizer(s, "<");
			while (stringtokenizer.hasMoreTokens()) {
				String s3 = stringtokenizer.nextToken();
				int i = s3.indexOf(">");
				if (i != -1) {
					s1 = s1 + s3.substring(i + 1, s3.length());
				} else {
					s1 = s1 + s3;
				}
			}
			return s1;
		} catch (Exception e) {
			return s;
		}
	}

	/**
	 * 去掉首尾空格
	 * 
	 * @param s
	 * @return
	 */
	public static String trimBeginEnd(String s) {
		if (s == null || s.trim().length() == 0) {
			return "";
		}
		int start = 0;
		int end = s.length();
		for (int i = 0; i < s.length(); i++) {
			if (!(s.charAt(i) == ' ')) {
				start = i;
				break;
			}
		}
		for (int i = (s.length() - 1); i >= 0; i--) {
			if (!(s.charAt(i) == ' ')) {
				end = i;
				break;
			}
		}
		return s.substring(start, end + 1);
	}

	public static String removeTransferredSigns(String s) {
		for (String key : transferredSigns.keySet()) {
			s = s.replace(key, transferredSigns.get(key));
		}
		return s;
	}

	private static final long serialVersionUID = 3023140366312604158L;

	@Field
	private String from;
	// @Field
	// private String to;
	// @Field
	// private String type;
	@Field
	private String subject;
	@Field
	private String body;
	@Field
	private Date created;
	@Field
	private List<String> tags;

	public Message() {
		super(SolrObjType.MESSAGE.ordinal());
	}

	public Message(String msgid) {
		super(SolrObjType.MESSAGE.ordinal());
		super.setId(msgid);
	}

	public Message(SSMessage msg) {
		this(msg.getMsgid(), msg);
	}

	public Message(String msgid, SSMessage msg) {
		super(SolrObjType.MESSAGE.ordinal());
		super.setId(msgid);
		this.setFrom(msg.getSender().getId());
		this.setCreated(new Date(msg.getTimestamp()));
		this.setTags(msg.getTagsList());
		String body_json = msg.getBody();
		if (body_json != null && !body_json.isEmpty()) {
			Contents contents = new Contents(body_json);
			if (contents != null) {
				String subject = contents.getSelfContent();
				subject = removeHTML(subject);
				this.setSubject(subject);
				List<Content> contentList = contents.getContents();
				if (contentList != null) {
					StringBuffer body = new StringBuffer();
					for (Content c : contentList) {
						if (c.getContent_type().equals(ContentType.html.name())) {
							ContentHtml ch = (ContentHtml) c;
							if (ch.getTitle() != null) {
								body.append(ch.getTitle());
							}
							if (ch.getBody() != null) {
								String tmp = removeHTML(ch.getBody());
								tmp = trimBeginEnd(tmp);
								tmp = removeTransferredSigns(tmp);
								body.append(tmp);
							}

						} else if (c.getContent_type().equals(
								ContentType.file.name())) {
							ContentFile cf = (ContentFile) c;
							if (cf != null)
								body.append(cf.getSummary());
						} else if (c.getContent_type().equals(
								ContentType.external.name())) {
							ContentExternal ce = (ContentExternal) c;
							if (ce.getTitle() != null)
								body.append(ce.getTitle());
							if (ce.getBody() != null) {
								String tmp = removeHTML(ce.getBody());
								tmp = trimBeginEnd(tmp);
								tmp = removeTransferredSigns(tmp);
								body.append(tmp);
							}
						} else if (c.getContent_type().equals(
								ContentType.pageContent.name())) {
							ContentPageMessage cpm = (ContentPageMessage) c;
							if (cpm.getTitle() != null)
								body.append(cpm.getTitle());
							if (cpm.getBody() != null) {
								String tmp = removeHTML(cpm.getBody());
								tmp = trimBeginEnd(tmp);
								tmp = removeTransferredSigns(tmp);
								body.append(tmp);
							}
						}
					}
					this.setBody(body.toString());
				}
			}
		} else {
			LOG.debug("ssmessage's body is null");
		}
	}

	public Message(String id, String from, String subject, String body,
			Date created, List<String> tags) {
		super(SolrObjType.MESSAGE.ordinal());
		super.setId(id);
		this.from = from;
		this.subject = subject;
		body = removeHTML(body);
		body = trimBeginEnd(body);
		body = removeTransferredSigns(body);
		this.body = body;
		this.created = created;
		this.tags = tags;
	}

	public String getFrom() {
		return from;
	}

	public void setFrom(String from) {
		this.from = from;
	}

	public String getSubject() {
		return subject;
	}

	public void setSubject(String subject) {
		this.subject = subject;
	}

	public String getBody() {
		return body;
	}

	public void setBody(String body) {
		this.body = body;
	}

	public Date getCreated() {
		return created;
	}

	public void setCreated(Date created) {
		this.created = created;
	}

	public List<String> getTags() {
		return tags;
	}

	public void setTags(List<String> tags) {
		this.tags = tags;
	}

	@Override
	public String toString() {
		return "Content [body=" + body + ", created=" + created + ", from="
				+ from + ", subject=" + subject + "]";
	}

	public static void main(String[] args) {
		try {
			// URL url = new URL(new
			// URL("http://www.cccc.com/aa/bb/cc/dd/ee.htm"),"../../../df/gov.htm");
			URL url = new URL("http://www.cccc.com/aa/bb/cc/dd/ee.htm?a=&gt;");
			System.out.println(url);
		} catch (MalformedURLException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}

}
