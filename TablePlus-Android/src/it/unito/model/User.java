package it.unito.model;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;


public class User implements Serializable {

	private static final long serialVersionUID = -7305579743183016832L;


	private Long key;


	private String username;


	private String firstName;

	
	private String lastName;


	private String email;

	
	private List<Long> groups=new ArrayList<Long>();


	private String token;

	
	private String loginProvider;


	private List<Document> documents;


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

	public boolean isOnline() {
		return online;
	}

	public void setOnline(boolean online) {
		this.online = online;
	}

	public List<Document> getDocuments() {
		return documents;
	}

	public void setDocuments(List<Document> documents) {
		this.documents = documents;
	}

	public String getToken() {
		return token;
	}

	public void setToken(String token) {
		this.token = token;
	}

	public String getLoginProvider() {
		return loginProvider;
	}

	public void setLoginProvider(String loginProvider) {
		this.loginProvider = loginProvider;
	}

	public List<Long> getGroups() {
		return groups;
	}

	public void setGroups(List<Long> groups) {
		this.groups = groups;
	}
	
	public void addGroup(Long group){
		this.groups.add(group);
	}

}
