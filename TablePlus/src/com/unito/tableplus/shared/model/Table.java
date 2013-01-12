package com.unito.tableplus.shared.model;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
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
	private List<TableObject> tableObjects;

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
		this.blackboard = new ArrayList<BlackBoardMessage>();
		this.tableObjects = new ArrayList<TableObject>();
		this.members = new ArrayList<Long>();
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

	public List<TableObject> getTableObjects() {
		return tableObjects;
	}

	public void setTableObjects(List<TableObject> tableObjects) {
		this.tableObjects = tableObjects;
	}

	public void addTableObject(TableObject tableObject) {
		tableObjects.add(tableObject);
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

	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result + ((creator == null) ? 0 : creator.hashCode());
		result = prime * result + ((key == null) ? 0 : key.hashCode());
		result = prime * result + ((members == null) ? 0 : members.hashCode());
		result = prime * result + ((name == null) ? 0 : name.hashCode());
		result = prime * result + ((owner == null) ? 0 : owner.hashCode());
		return result;
	}

	@Override
	public boolean equals(Object obj) {
		if (this == obj)
			return true;
		if (obj == null)
			return false;
		if (getClass() != obj.getClass())
			return false;
		Table other = (Table) obj;
		if (creator == null) {
			if (other.creator != null)
				return false;
		} else if (!creator.equals(other.creator))
			return false;
		if (key == null) {
			if (other.key != null)
				return false;
		} else if (!key.equals(other.key))
			return false;
		if (members == null) {
			if (other.members != null)
				return false;
		} else if (!members.equals(other.members))
			return false;
		if (name == null) {
			if (other.name != null)
				return false;
		} else if (!name.equals(other.name))
			return false;
		if (owner == null) {
			if (other.owner != null)
				return false;
		} else if (!owner.equals(other.owner))
			return false;
		return true;
	}
	
}
