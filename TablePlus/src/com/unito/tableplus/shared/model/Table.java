package com.unito.tableplus.shared.model;

import java.io.Serializable;
import java.util.HashMap;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;

import javax.jdo.annotations.IdGeneratorStrategy;
import javax.jdo.annotations.NotPersistent;
import javax.jdo.annotations.PersistenceCapable;
import javax.jdo.annotations.Persistent;
import javax.jdo.annotations.PrimaryKey;

@PersistenceCapable(detachable = "true")
public class Table implements Serializable {

	private static final long serialVersionUID = -4489608881786573961L;

	@PrimaryKey
	@Persistent(valueStrategy = IdGeneratorStrategy.IDENTITY)
	private Long key;

	@Persistent
	private String name = null;

	@Persistent
	private List<Long> members = null;

	@Persistent
	private Long creator = null;

	@Persistent
	private Long owner = null;

	@Persistent(mappedBy = "table")
	private List<BlackBoardMessage> blackboard;

	@Persistent(mappedBy = "table")
	private List<SharedResource> resources;



	@NotPersistent
	private Map<Long, User> usersMap;

	@NotPersistent
	private String chatHistory;

	@NotPersistent
	private boolean isActive;

	/* Needed by GWT */
	public Table() {
	}

	public Table(Long creator) {
		this.creator = creator;
		this.owner = creator;
		this.blackboard = new LinkedList<BlackBoardMessage>();
		this.resources = new LinkedList<SharedResource>();
		this.members = new LinkedList<Long>();
		this.members.add(creator);

		this.usersMap = new HashMap<Long, User>();
		this.chatHistory = "";
		this.isActive = false;
	}

	public void setKey(Long key) {
		this.key = key;
	}

	public Long getKey() {
		return key;
	}

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public Long getCreator() {
		return creator;
	}

	public void setCreator(Long creator) {
		this.creator = creator;
	}

	public Long getOwner() {
		return owner;
	}

	public void setOwner(Long owner) {
		this.owner = owner;
	}

	public void addMember(Long user) {
		members.add(user);
	}

	public boolean removeMember(Long member) {
		Iterator<Long> i = this.members.iterator();
		Boolean found = false;
		while (i.hasNext() && !found) {
			if (i.next().equals(member)) {
				i.remove();
				found = true;
			}
		}
		return found;
	}

	public void removeAllMembers() {
		members.clear();
		members.add(owner);
	}

	public List<Long> getMembers() {
		return members;
	}

	public void setMembers(List<Long> members) {
		this.members = members;
	}

	public List<SharedResource> getResources() {
		return resources;
	}

	public void setResources(List<SharedResource> resources) {
		this.resources = resources;
	}

	public void addResource(SharedResource resource) {
		resources.add(resource);
	}

	public List<BlackBoardMessage> getBlackboard() {
		return blackboard;
	}

	public void setBlackboard(List<BlackBoardMessage> blackboard) {
		this.blackboard = blackboard;
	}


	public void setUserStatus(Long userKey, UserStatus status) {
		if (usersMap != null && !usersMap.isEmpty())
			usersMap.get(userKey).setStatus(status);
	}

	public Map<Long, User> getUsersMap() {
		return usersMap;
	}

	public void setUsersMap(Map<Long, User> users) {
		this.usersMap = new HashMap<Long, User>(users);
	}
	
	public void setUsersMap(User user) {
		this.usersMap = new HashMap<Long,User>();
		this.usersMap.put(user.getKey(), user);
	}

	public String getChatHistory() {
		return chatHistory;
	}

	public void setChatHistory(String chatHistory) {
		this.chatHistory = chatHistory;
	}

	public void appendChat(String message) {
		if (this.chatHistory == null)
		this.chatHistory = message;
		else this.chatHistory += message;
	}

	public void clearChatHistory() {
		this.chatHistory = "";
	}

	public boolean isActive() {
		return this.isActive;
	}

	public void setActive(boolean value) {
		this.isActive = value;
	}
	
	@Override
	public String toString() {
		return "Table Name: " + this.name + " - Creator ID: " + this.creator
				+ " - Owner ID: " + this.owner + " - There are "
				+ this.members.size() + " member(s).";
	}
}
