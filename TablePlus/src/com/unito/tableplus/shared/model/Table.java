package com.unito.tableplus.shared.model;

import java.io.Serializable;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;

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

	public void setKey(Long key) {
		this.key = key;
	}

	@Persistent
	private String name = null;

	@Persistent
	private List<Long> members = null;

	@NotPersistent
	private List<String> membersEmail = null;

	@Persistent
	private Long creator = null;

	@Persistent
	private Long owner = null;

	@Persistent(mappedBy = "table")
	private List<BlackBoardMessage> blackboard;

	@Persistent(mappedBy = "table")
	private List<SharedResource> resources;

	@Persistent(mappedBy = "table")
	private List<Bookmark> bookmarks;

	public Table(Long creator) {
		this.creator = creator;
		this.owner = creator;
		this.blackboard = new LinkedList<BlackBoardMessage>();
		this.setResources(new LinkedList<SharedResource>());
		this.members = new LinkedList<Long>();
		this.members.add(creator);
		this.bookmarks = new LinkedList<Bookmark>();
	}
	
	//needed by GWT!!!
	public Table() {
	}

	public List<BlackBoardMessage> getBlackBoard() {
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

	@Override
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

	public List<SharedResource> getResources() {
		return resources;
	}

	public void setResources(List<SharedResource> resources) {
		this.resources = resources;
	}

	public void addResource(SharedResource resource) {
		resources.add(resource);
	}

	/**
	 * @return the membersEmail
	 */
	public List<String> getMembersEmail() {
		return membersEmail;
	}

	/**
	 * @param membersEmail
	 *            the membersEmail to set
	 */
	public void setMembersEmail(List<String> membersEmail) {
		this.membersEmail = membersEmail;
	}

	// gestione segnalibri relativi ad un tavolo

	public void setBookmarks(LinkedList<Bookmark> bookmarks) {
		this.bookmarks = bookmarks;
	}

	public List<Bookmark> getBookmarks() {
		return bookmarks;
	}

	public void addBookmark(Bookmark b) {
		getBookmarks().add(b);
	}
}
