package com.unito.tableplus.shared.model;

import java.io.Serializable;

public class Document implements Serializable{

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	
	private String title;
	private String type;
	private String DocId;
	
	public String getTitle() {
		return title;
	}
	public void setTitle(String title) {
		this.title = title;
	}
	public String getType() {
		return type;
	}
	public void setType(String type) {
		this.type = type;
	}
	public String getDocId() {
		return DocId;
	}
	public void setDocId(String docId) {
		DocId = docId;
	}

}
