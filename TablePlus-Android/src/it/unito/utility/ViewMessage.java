package it.unito.utility;

import it.unito.model.MessageType;

public class ViewMessage {

	public ViewMessage(String key,String author,String content,MessageType type){
		this.key=key;
		this.author=author;
		this.content=content;
		this.type= type;
	}
	public ViewMessage(){
		this.key="";
		this.author="";
		this.content="";
		this.type= MessageType.GENERIC;
	}
	
	public String getKey() {
		return key;
	}
	public void setKey(String key) {
		this.key = key;
	}
	public String getAuthor() {
		return author;
	}
	public void setAuthor(String author) {
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
	
	private String key;
	private String author;
	private MessageType type;
	private String content;
}
