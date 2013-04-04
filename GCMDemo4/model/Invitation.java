package com.unito.tableplus.shared.model;

import java.io.Serializable;

import javax.jdo.annotations.IdGeneratorStrategy;
import javax.jdo.annotations.PersistenceCapable;
import javax.jdo.annotations.Persistent;
import javax.jdo.annotations.PrimaryKey;

@PersistenceCapable
public class Invitation implements Serializable{
	
	private static final long serialVersionUID = -7604076012205785560L;

	@PrimaryKey
	@Persistent(valueStrategy = IdGeneratorStrategy.IDENTITY)
	private Long key;
	
	@Persistent
	private String code;
	
	@Persistent
	private Long tableKey;
	
	@Persistent
	private Long author;

	@Persistent
	private String invitedUser;

	public Long getKey() {
		return key;
	}

	public void setKey(Long key) {
		this.key = key;
	}

	public String getCode() {
		return code;
	}

	public void setCode(String code) {
		this.code = code;
	}

	public Long getTableKey() {
		return tableKey;
	}

	public void setTableKey(Long tableKey) {
		this.tableKey = tableKey;
	}
	
	public Long getAuthor() {
		return author;
	}

	public void setAuthor(Long author) {
		this.author = author;
	}
	
	public String getInvitedUser() {
		return invitedUser;
	}

	public void setInvitedUser(String invitedUser) {
		this.invitedUser = invitedUser;
	}
	
}