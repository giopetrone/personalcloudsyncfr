package it.unito.model;

import java.io.Serializable;
import java.sql.Timestamp;
import java.util.Date;


public class Message implements Serializable {

	private static final long serialVersionUID = 4594842116272050252L;


	private String key;

	private Long author = null;

	private MessageType type = null;
 
	private String content = null;
 
	private String date;
 
	private int hashcode;
 
	private Group group;

	public Message(Long author, MessageType type, String content) {
		date = new Timestamp(new Date().getTime()).toString();
		this.author = author;
		this.type = type;
		this.content = content;
		this.hashcode = this.hashCode();
	}
	
	public Message(){
		date = new Timestamp(new Date().getTime()).toString();
		this.hashcode = this.hashCode();
	}

	public Long getAuthor() {
		return author;
	}

	public void setAuthor(Long author) {
		this.author = author;
	}

	public MessageType getType() {
		return type;
	}

	public void setType(MessageType type) {
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

	public int getHashcode() {
		return hashcode;
	}

	public boolean equals(Message message) {
		return (this.hashcode == message.getHashcode());
	}

	public int hashCode() {
		StringBuilder builder = new StringBuilder();
		builder.append(this.date);
		builder.append(this.author);
		builder.append(this.type);
		builder.append(this.content);
		return builder.hashCode();
	}

	public String toString() {
		return this.getDate() + "--" + this.getAuthor() + "--"
				+ this.getContent() + "--" + this.getType();
	}

	public String getKey() {
		return key;
	}
	
	public void setKey(String key) {
		this.key=key;
	}

	public Group getTable() {
		return group;
	}
}
