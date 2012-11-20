package com.unito.tableplus.shared.model;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

import javax.jdo.annotations.IdGeneratorStrategy;
import javax.jdo.annotations.NotPersistent;
import javax.jdo.annotations.PersistenceCapable;
import javax.jdo.annotations.Persistent;
import javax.jdo.annotations.PrimaryKey;

@PersistenceCapable(detachable = "true")
public class User implements Serializable {

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

	@NotPersistent
	private UserStatus status;
	
	public User(){
		status = UserStatus.OFFLINE;
		this.tables = new ArrayList<Long>();
	}

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

	public void addTable(Long table) {
		this.tables.add(table);
	}

	/**
	 * @return the status
	 */
	public UserStatus getStatus() {
		return status;
	}

	/**
	 * @param status the status to set
	 */
	public void setStatus(UserStatus status) {
		this.status = status;
	}
}
