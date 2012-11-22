package com.unito.tableplus.shared.model;

import java.io.Serializable;
import java.sql.Timestamp;
import java.util.Date;

import javax.jdo.annotations.Extension;
import javax.jdo.annotations.IdGeneratorStrategy;
import javax.jdo.annotations.PersistenceCapable;
import javax.jdo.annotations.Persistent;
import javax.jdo.annotations.PrimaryKey;

@PersistenceCapable(detachable = "true")
public class Comment implements Serializable {

	private static final long serialVersionUID = 1L;

	@PrimaryKey
	@Persistent(valueStrategy = IdGeneratorStrategy.IDENTITY)
	@Extension(vendorName = "datanucleus", key = "gae.encoded-pk", value = "true")
	private String key;

	@Persistent
	private Bookmark bookmark;

	@Persistent
	private String author;

	@Persistent
	private String date;

	@Persistent
	private String comment;

	@Persistent
	private VisibilityType visibility;

	public Comment() {
	}

	// default visibilty = PUBLIC
	public Comment(String comment, String author) {
		this.author = author;
		this.comment = comment;
		this.date = new Timestamp(new Date().getTime()).toString();
		this.visibility = VisibilityType.PUBLIC;
	}

	// getters

	public Comment(String comment, String author, VisibilityType visibility) {
		this.author = author;
		this.comment = comment;
		this.date = new Timestamp(new Date().getTime()).toString();
		this.visibility = visibility;
	}

	public String getKey() {
		return key;
	}

	public String getAuthor() {
		return author;
	}

	public String getDate() {
		return date;
	}

	public String getComment() {
		return comment;
	}

	public VisibilityType getVisibilty() {
		return visibility;
	}

	public void getAuthor(String author) {
		this.author = author;
	}

	public void setComment(String c) {
		this.comment = c;
	}

	public void setVisibilty(VisibilityType visibility) {
		this.visibility = visibility;
	}

	// others

	public String toString() {
		return "<" + author + "> " + comment + " Writed on: " + getDate()
				+ " [" + visibility + "]";
	}

	public boolean isPrivate() {
		if (visibility == VisibilityType.PRIVATE)
			return true;
		else
			return false;
	}
}
