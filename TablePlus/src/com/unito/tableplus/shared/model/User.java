package com.unito.tableplus.shared.model;

import java.io.Serializable;
import java.util.Iterator;
import java.util.List;
import javax.jdo.annotations.IdGeneratorStrategy;
import javax.jdo.annotations.Inheritance;
import javax.jdo.annotations.InheritanceStrategy;
import javax.jdo.annotations.PersistenceCapable;
import javax.jdo.annotations.Persistent;
import javax.jdo.annotations.PrimaryKey;

@PersistenceCapable
@Inheritance(strategy = InheritanceStrategy.SUBCLASS_TABLE)
public abstract class User implements Serializable {
	
	private static final long serialVersionUID = -7305579743183016832L;
	
	@PrimaryKey
	@Persistent(valueStrategy = IdGeneratorStrategy.IDENTITY)
	private Long key;
	
	@Persistent
	private String username;

	@Persistent
	private String firstName;

	@Persistent
	private String lastName;

	@Persistent
	private String email;

	@Persistent
	private List<Long> tables;
	
	@Persistent
	private List<Long> documents;
	
	private boolean online;


	public Long getKey() {
		return key;
	}


	public String getUsername() {
		return username;
	}


	public void setUsername(String username) {
		this.username = username;
	}


	public String getFirstName() {
		return firstName;
	}


	public void setFirstName(String firstName) {
		this.firstName = firstName;
	}


	public String getLastName() {
		return lastName;
	}


	public void setLastName(String lastName) {
		this.lastName = lastName;
	}


	public String getEmail() {
		return email;
	}


	public void setEmail(String email) {
		this.email = email;
	}


	public List<Long> getTables() {
		return tables;
	}


	public void setTables(List<Long> tables) {
		this.tables = tables;
	}


	public List<Long> getDocuments() {
		return documents;
	}


	public void setDocuments(List<Long> documents) {
		this.documents = documents;
	}


	public boolean isOnline() {
		return online;
	}


	public void setOnline(boolean online) {
		this.online = online;
	}
	
	public void addDocument(Long document){
		this.documents.add(document);
	}
	
	public boolean removeDocument(Long document) {
		Iterator<Long> d = this.documents.iterator();
		Boolean found = false;
		while (d.hasNext() && !found) {
			if (d.equals(document)) {
				tables.remove(d);
				found = true;
			}
			d.next();
		}
		return found;
	}

}
