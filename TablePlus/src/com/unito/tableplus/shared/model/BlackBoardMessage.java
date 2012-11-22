package com.unito.tableplus.shared.model;
   
import java.io.Serializable;
import java.sql.Timestamp;
import java.util.Date;

import javax.jdo.annotations.Extension;
import javax.jdo.annotations.IdGeneratorStrategy;
import javax.jdo.annotations.PersistenceCapable;
import javax.jdo.annotations.Persistent;
import javax.jdo.annotations.PrimaryKey;

@PersistenceCapable(detachable = "true")
public class BlackBoardMessage implements Serializable {

	private static final long serialVersionUID = 4594842116272050252L;

	@PrimaryKey
	@Persistent(valueStrategy = IdGeneratorStrategy.IDENTITY)
	@Extension(vendorName="datanucleus", key="gae.encoded-pk", value="true")
	private String key;

	@Persistent
	private String author;
	@Persistent
	private BlackBoardMessageType type;
	@Persistent
	private String content;
	@Persistent
	private String date;
	@Persistent
	private Table table;

	public BlackBoardMessage(){
	}
	
	public BlackBoardMessage(String author, BlackBoardMessageType type, String content) {
		date = new Timestamp(new Date().getTime()).toString();
		this.author = author;
		this.type = type;
		this.content = content;
	}

	public String getAuthor() {
		return author;
	}

	public void setAuthor(String author) {
		this.author = author;
	}

	public BlackBoardMessageType getType() {
		return type;
	}

	public void setType(BlackBoardMessageType type) {
		this.type = type;
	}

	public String getContent() {
		return content;
	}

	public void setContent(String content) {
		this.content = content;
	}

	public String getDate() {
		return date;
	}

	public String getKey() {
		return key;
	}

	public Table getTable() {
		return table;
	}
	
	@Override
	public String toString() {
		return "BlackBoardMessage [key=" + key + ", author=" + author
				+ ", type=" + type + ", content=" + content + ", date=" + date
				+ ", table=" + table + "]";
	}
	
}
