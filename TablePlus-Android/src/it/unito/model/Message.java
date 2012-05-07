package it.unito.model;

import java.io.Serializable;
import java.sql.Timestamp;
import java.util.Date;



public class Message implements Serializable {

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;


	private String key;


	private String author = null;

	private MessageType type = null;

	private String content = null;
	
	private Timestamp date;
	
	private int hashcode;
	
	private Group group;

	public Message(String author, MessageType type, String content) {
		date = new Timestamp(new Date().getTime());
		this.author = author;
		this.type = type;
		this.content = content;
		this.hashcode = this.hashCode();
	}
	
	public Message(){
		date = new Timestamp(new Date().getTime());
		this.hashcode = 1;
	}

	protected String getAuthor() {
		return author;
	}

	protected void setAuthor(String author) {
		this.author = author;
	}

	protected MessageType getType() {
		return type;
	}

	protected void setType(MessageType type) {
		this.type = type;
	}

	protected String getContent() {
		return content;
	}

	protected void setContent(String content) {
		this.content = content;
	}

	protected Timestamp getDate() {
		return date;
	}

	protected int getHashcode() {
		return hashcode;
	}

	protected boolean equals(Message message) {
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

	protected String getKey() {
		return key;
	}

	public Group getTable() {
		return group;
	}
}
