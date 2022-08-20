package com.zhuaiwa.session.search;

import java.net.MalformedURLException;
import java.util.ArrayList;
import java.util.List;

import org.apache.solr.client.solrj.SolrQuery;
import org.apache.solr.client.solrj.SolrQuery.ORDER;
import org.apache.solr.client.solrj.SolrServerException;
import org.apache.solr.client.solrj.impl.CommonsHttpSolrServer;
import org.apache.solr.client.solrj.response.QueryResponse;

import com.zhuaiwa.session.search.domain.Message;
import com.zhuaiwa.session.search.domain.User;
import com.zhuaiwa.util.PropertiesHelper;

public class Searcher {
	private static String solrURL = PropertiesHelper.getValue("solrURL",
			"http://search.baiku.cn:8090/solr");
	static {
		System.out.println("Searcher:: solrURL:" + solrURL);
	}
	static CommonsHttpSolrServer client;

	static {
		try {
			client = new CommonsHttpSolrServer(solrURL);
			// client.setConnectionTimeout(1000);
			client.setDefaultMaxConnectionsPerHost(100);
			client.setMaxTotalConnections(100);
		} catch (MalformedURLException e) {
			e.printStackTrace();
		}
	}

	/**
	 * 搜索内容
	 * 
	 * @param queryString
	 *            field:keyword格式，用AND|OR连接
	 *            field有：from,subject,body,created,tags
	 * @return
	 */
	public SearchResult searchMessage(String queryString) {
		return searchMessage(queryString, 0, 10);
	}

	public SearchResult searchMessage(String queryString, Integer start,
			Integer rows) {
		List<String> result = new ArrayList<String>();
		long total = 0;
		try {
			SolrQuery query = new SolrQuery(queryString);
			query.setStart(start);
			query.setRows(rows);
			query.setSortField("created", ORDER.desc);// 默认按时间倒排
			QueryResponse resp = client.query(query);
			List<Message> beans = resp.getBeans(Message.class);
			total = resp.getResults().getNumFound();
			for (Message message : beans) {
				result.add(message.getId());
			}
		} catch (SolrServerException e) {
			e.printStackTrace();
		}
		return new SearchResult(result, total);
	}

	/**
	 * 搜索用户
	 * 
	 * @param queryString
	 *            field:keyword格式，用AND|OR连接
	 *            field有：nickname,introduction,email,phoneNumber
	 *            ,birthday,gender,
	 *            country,province,city,favorTags,company,school_name
	 * @return
	 */
	public SearchResult searchUser(String queryString) {
		return searchUser(queryString, 0, 10);
	}

	public SearchResult searchUser(String queryString, Integer start,
			Integer rows) {
		List<String> result = new ArrayList<String>();
		long total = 0;
		try {
			SolrQuery query = new SolrQuery(queryString);
			query.setStart(start);
			query.setRows(rows);
			QueryResponse resp = client.query(query);
			List<User> beans = resp.getBeans(User.class);
			total = resp.getResults().getNumFound();
			for (User user : beans) {
				int index = user.getId().indexOf("_");
				if (index > -1) {
					result.add(user.getId().substring(0, index));
				} else {
					result.add(user.getId());
				}
			}
		} catch (SolrServerException e) {
			e.printStackTrace();
		}
		return new SearchResult(result, total);
	}

	public static void main(String[] args) {
		Searcher searcher = new Searcher();
		SearchResult result = null;
//		result = searcher.searchMessage("tags:*");
//		result = searcher.searchMessage("tags:怀旧 AND (from: 3c808c935cf82e6e3cd17bf2ee3ebf25 from: 2aa7e9038a1fc2cac6520440dad155a4)");
		result = searcher.searchMessage("subject:新年 body:新年 -from: 3c808c935cf82e6e3cd17bf2ee3ebf25  -from: 2aa7e9038a1fc2cac6520440dad155a4");
		System.out.println(result.getTotal());
		for (String id : result.getIds()) {
			System.out.println(id);
		}
		
//		if (args.length == 0) {
//			System.out.println("Searcher user|message queryString");
//			return;
//		}
//		Searcher searcher = new Searcher();
//		SearchResult result = null;
//		if (args[0].equals("user")) {
//			result = searcher.searchUser(args[1]);
//		} else if (args[0].equals("message")) {
//			result = searcher.searchMessage(args[1]);
//		}
//		System.out.println(result.getTotal());
//		for (String id : result.getIds()) {
//			System.out.println(id);
//		}

		// System.out.println("========");
		// result = searcher.searchUser(args[1],0,2);
		// for(String id : result.getIds()){
		// System.out.println(id);
		// }
		//
		// System.out.println("========");
		// result = searcher.searchUser(args[1],2,2);
		// for(String id : result.getIds()){
		// System.out.println(id);
		// }

	}
}
