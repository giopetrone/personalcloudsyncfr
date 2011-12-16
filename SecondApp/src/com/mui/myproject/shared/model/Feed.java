package com.mui.myproject.shared.model;

import java.io.Serializable;

@SuppressWarnings("serial")
public class Feed implements Serializable {
	private String description;
	private String link;
	private String title;
	private String uuid;

	public Feed() {

	}

	public Feed(String uuid) {
		this.uuid = uuid;
	}

	public String getDescription() {
		return description;
	}

	public String getLink() {
		return link;
	}

	public String getTitle() {
		return title;
	}

	public String getUuid() {
		return uuid;
	}

	public void setDescription(String description) {
		this.description = description;
	}

	public void setLink(String link) {
		this.link = link;
	}

	public void setTitle(String title) {
		this.title = title;
	}
}