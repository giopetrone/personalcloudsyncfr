package com.unito.tableplus.shared.model;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;
import javax.jdo.annotations.IdGeneratorStrategy;
import javax.jdo.annotations.PersistenceCapable;
import javax.jdo.annotations.Persistent;
import javax.jdo.annotations.PrimaryKey;

@PersistenceCapable(detachable = "true")
public class Group implements Serializable {
	
	private static final long serialVersionUID = -4489608881786573961L;

	@PrimaryKey
	@Persistent(valueStrategy = IdGeneratorStrategy.IDENTITY)
	private Long key;

	@Persistent
	private String name = null;

	@Persistent
	private List<Long> members = null;

	@Persistent
	private List<Long> hiddenMembers;

	@Persistent
	private List<Long> selectivePresenceMembers;

	@Persistent
	private Long creator = null;

	@Persistent
	private Long owner = null;

	@Persistent(mappedBy = "group")
	private List<Message> blackboard;

	@Persistent
	private List<String> documents;

	public Group(Long creator) {
		this.creator = creator;
		this.owner = creator;
		this.blackboard = new LinkedList<Message>();
		this.setDocuments(new LinkedList<String>());
		this.members = new LinkedList<Long>();
		this.setHiddenMembers(new ArrayList<Long>());
		this.setSelectivePresenceMembers(new ArrayList<Long>());
		this.members.add(creator);
	}

	public Group() {

	}

	public List<Message> getBlackBoard() {
		return this.blackboard;
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

	public String toString() {
		return "Table Name:" + this.name + " - Creator: " + this.creator
				+ " - Owner: " + this.owner + " - " + this.members.size()
				+ "members.";
	}

	public List<Long> getMembers() {
		return members;
	}

	public void setMembers(List<Long> members) {
		this.members = members;
	}

	public void addHiddenMember(Long hmKey) {
		hiddenMembers.add(hmKey);
	}

	public boolean removeHiddenMember(Long hmKey) {
		Long toRemove = null;
		for (Long l : hiddenMembers) {
			if (l.compareTo(hmKey) == 0) {
				toRemove = l;
			}
		}
		if (toRemove != null) {
			hiddenMembers.remove(toRemove);
			return true;
		} else {
			return false;
		}

	}

	public void addSelectivePresenceMember(Long hmKey) {
		selectivePresenceMembers.add(hmKey);
	}

	public boolean removeSelectivePresenceMember(Long hmKey) {
		Long toRemove = null;
		for (Long l : selectivePresenceMembers) {
			if (l.compareTo(hmKey) == 0) {
				toRemove = l;
			}
		}
		if (toRemove != null) {
			selectivePresenceMembers.remove(toRemove);
			return true;
		} else {
			return false;
		}

	}

	public List<String> getDocuments() {
		return documents;
	}

	public void setDocuments(List<String> documents) {
		this.documents = documents;
	}

	public void addDocument(String document) {
		documents.add(document);
	}

	public List<Long> getHiddenMembers() {
		return hiddenMembers;
	}

	public void setHiddenMembers(List<Long> hiddenMembers) {
		this.hiddenMembers = hiddenMembers;
	}

	public List<Long> getSelectivePresenceMembers() {
		return selectivePresenceMembers;
	}

	public void setSelectivePresenceMembers(List<Long> selectivePresenceMembers) {
		this.selectivePresenceMembers = selectivePresenceMembers;
	}

}
