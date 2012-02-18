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
public class Group implements Serializable{
	
		/**
	 * 
	 */
	private static final long serialVersionUID = 1L;

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
		
		@Persistent(mappedBy = "group")
		private List<Message> blackboard;
		
		@Persistent
		private List<String> docIDs;
		
		@NotPersistent
		private List<User> hiddenMembers;
		
		public Group(Long creator){
			this.creator =  creator;
			this.owner = creator;
			this.blackboard = new LinkedList<Message>();
			this.docIDs = new LinkedList<String>();
			this.members = new LinkedList<Long>();
			this.members.add(creator);			
		}
		
		public Group(){
			
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

		public void addmember(Long user) {
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

		public List<String> getDocIDs() {
			return docIDs;
		}

		public void setDocIDs(List<String> docIDs) {
			this.docIDs = docIDs;
		}

		public List<User> getHiddenMembers() {
			return hiddenMembers;
		}

		public void setHiddenMembers(List<User> hiddenMembers) {
			this.hiddenMembers = hiddenMembers;
		}

		
	}

