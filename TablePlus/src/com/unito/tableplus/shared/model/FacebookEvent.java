package com.unito.tableplus.shared.model;

public class FacebookEvent implements Resource {

	private static final long serialVersionUID = -6007594027290204640L;
	private String id;
	private Owner owner;
	private String name;
	private String description;
	private String start_time;
	private String end_time;
	private String location;
	private Venue venue;
	private String privacy;
	private String updated_time;
	private String picture;

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
	public String getURI() {
		// TODO Auto-generated method stub
		return null;
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

	public Owner getOwner() {
		return owner;
	}

	public void setOwner(Owner owner) {
		this.owner = owner;
	}
	
	public void setOwner(String id, String name) {
		this.owner.setId(id);
		this.owner.setName(name);
	}

	public Venue getVenue() {
		return venue;
	}

	public void setVenue(Venue venue) {
		this.venue = venue;
	}

	public void setVenue(String id, String street, String city, String state,
			String zip, String country, String latitude, String longitude) {
		this.venue.setId(id);
		this.venue.setStreet(street);
		this.venue.setCity(city);
		this.venue.setState(state);
		this.venue.setZip(zip);
		this.venue.setCountry(country);
		this.venue.setCountry(country);
		this.venue.setLatitude(latitude);
		this.venue.setLongitude(longitude);
	}
	
	@SuppressWarnings("unused")
	private class Owner {
		private String id;
		private String name;

		
		public String getId() {
			return id;
		}

		public void setId(String id) {
			this.id = id;
		}
		public String getName() {
			return name;
		}

		public void setName(String name) {
			this.name = name;
		}
	}

	@SuppressWarnings("unused")
	private class Venue {
		private String id;
		private String street;
		private String city;
		private String state;
		private String zip;
		private String country;
		private String latitude;
		private String longitude;

		public String getId() {
			return id;
		}

		public void setId(String id) {
			this.id = id;
		}

		public String getStreet() {
			return street;
		}

		public void setStreet(String street) {
			this.street = street;
		}

		public String getCity() {
			return city;
		}

		public void setCity(String city) {
			this.city = city;
		}

		public String getState() {
			return state;
		}

		public void setState(String state) {
			this.state = state;
		}

		public String getZip() {
			return zip;
		}

		public void setZip(String zip) {
			this.zip = zip;
		}

		public String getCountry() {
			return country;
		}

		public void setCountry(String country) {
			this.country = country;
		}

		public String getLatitude() {
			return latitude;
		}

		public void setLatitude(String latitude) {
			this.latitude = latitude;
		}

		public String getLongitude() {
			return longitude;
		}

		public void setLongitude(String longitude) {
			this.longitude = longitude;
		}
	}

}
