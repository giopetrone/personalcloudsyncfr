package it.unito.model;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;



public class Group implements Serializable {

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;

	
	private Long key;


	private String name = null;

	
	private List<Long> members = null;

	
	private List<Long> hiddenMembers;

	
	private List<Long> selectivePresenceMembers;


	private Long creator = null;

	
	private Long owner = null;


	private List<Message> blackboard;


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
	
	public void setKey(Long key){
		this.key=key;
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
