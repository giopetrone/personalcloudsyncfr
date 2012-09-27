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
	private List<String> membersEmail =  null;

	@Persistent
	private Long creator = null;

	@Persistent
	private Long owner = null;

	@Persistent(mappedBy = "table")
	private List<BlackBoardMessage> blackboard;

	@Persistent
	private List<String> documents;

	@Persistent(mappedBy = "table")
	private List<Bookmark> bookmark=new LinkedList<Bookmark>();

	public Table(Long creator) {
		this.creator = creator;
		this.owner = creator;
		this.blackboard = new LinkedList<BlackBoardMessage>();
		this.setDocuments(new LinkedList<String>());
		this.members = new LinkedList<Long>();
		this.members.add(creator);
		this.setBookmarks(new LinkedList<Bookmark>());
	}

	public Table() {}

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


	public List<String> getDocuments() {
		return documents;
	}

	public void setDocuments(List<String> documents) {
		this.documents = documents;
	}

	public void addDocument(String document) {
		documents.add(document);
	}

	/**
	 * @return the membersEmail
	 */
	public List<String> getMembersEmail() {
		return membersEmail;
	}

	/**
	 * @param membersEmail the membersEmail to set
	 */
	public void setMembersEmail(List<String> membersEmail) {
		this.membersEmail = membersEmail;
	}

	//gestione segnalibri relativi ad un tavolo

	private void setBookmarks(LinkedList<Bookmark> bookmark) {
		this.bookmark=bookmark;
	}

	public List<Bookmark> getBookmark() {
		return bookmark;
	}

	public void addBookmark(Bookmark b) {
		getBookmark().add(b);
	}
}
