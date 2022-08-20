package com.zhuaiwa.session.search.json;

import java.io.IOException;
import java.io.Writer;
import java.util.ArrayList;
import java.util.List;


public class ContentFile extends Content{

	
	public ContentFile() {
		super(ContentType.file.name());
	}

	/**
	 * 
	 */
	private static final long serialVersionUID = -7992879040212130722L;
	List<FileObj> files;

	public List<FileObj> getFiles() {
		return files;
	}

	public void setFiles(List<FileObj> files) {
		this.files = files;
	}
	
	public void addFile(FileObj fileObj){
		if(files==null) files = new ArrayList<FileObj>();
		files.add(fileObj);
	}
	
	public String getSummary(){
		StringBuffer sb = new StringBuffer();
		if(files!=null && !files.isEmpty()){
			for(FileObj file:files){
				sb.append(file.getName());
			}
		}
		return sb.toString();
	}
	
	@Override
	public void writeJSONString(Writer out) throws IOException {
		//TODO
	}
	
	
}
