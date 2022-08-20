package com.zhuaiwa.session.search;

import java.util.List;

public class SearchResult {
	private List<String> ids;
	private long total;

	public SearchResult(List<String> ids, long total) {
		super();
		this.ids = ids;
		this.total = total;
	}

	public SearchResult() {
		super();
	}

	public List<String> getIds() {
		return ids;
	}

	public void setIds(List<String> ids) {
		this.ids = ids;
	}

	public long getTotal() {
		return total;
	}

	public void setTotal(long total) {
		this.total = total;
	}

}
