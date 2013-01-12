package com.unito.tableplus.shared.model;


public class FacebookEvent implements Resource {

	private static final long serialVersionUID = -6007594027290204640L;
	private String id;
	//private Owner owner;
	private String name;
	private String description;
	private String start_time;
	private String end_time;
	private String location;
	//private Venue venue;
	private String privacy;
	private String updated_time;
	private String picture;
	private String uri;

	public String getId() {
		return id;
	}

	public void setId(String id) {
		this.id = id;
	}

	public String getDescription() {
		return description;
	}

	public void setDescription(String description) {
		this.description = description;
	}

	public String getStart_time() {
		return start_time;
	}

	public void setStart_time(String start_time) {
		this.start_time = start_time;
	}

	public String getEnd_time() {
		return end_time;
	}

	public void setEnd_time(String end_time) {
		this.end_time = end_time;
	}

	public String getLocation() {
		return location;
	}

	public void setLocation(String location) {
		this.location = location;
	}

	public String getPrivacy() {
		return privacy;
	}

	public void setPrivacy(String privacy) {
		this.privacy = privacy;
	}

	public String getUpdated_time() {
		return updated_time;
	}

	public void setUpdated_time(String updated_time) {
		this.updated_time = updated_time;
	}

	public String getPicture() {
		return picture;
	}

	public void setPicture(String picture) {
		this.picture = picture;
	}

	public static long getSerialversionuid() {
		return serialVersionUID;
	}

	@Override
	public String getIcon() {
		return "calendar-icon.png";
	}

	@Override
	public Provider getProvider() {
		return Provider.FACEBOOK;
	}

	@Override
	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	@Override
	public String getID() {
		return id;
	}
	
	@Override
	public String getURI() {
		return this.uri;
	}

	@Override
	public void setURI(String uri) {
		this.uri = uri;
	}


}
