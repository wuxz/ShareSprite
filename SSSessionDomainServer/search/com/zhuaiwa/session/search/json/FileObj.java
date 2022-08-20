package com.zhuaiwa.session.search.json;

public class FileObj {

	public FileObj(String name, String linker, String size) {
		super();
		this.name = name;
		this.linker = linker;
		this.size = size;
	}
	public FileObj(String name, String linker, Long size) {
		super();
		this.name = name;
		this.linker = linker;
		this.size = "" + size;
	}	

	private String name;
	
	private String linker;
	
	private String size;//带单位的大小描述

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public String getLinker() {
		return linker;
	}

	public void setLinker(String linker) {
		this.linker = linker;
	}

	public String getSize() {
		return size;
	}

	public void setSize(String size) {
		this.size = size;
	}

}
