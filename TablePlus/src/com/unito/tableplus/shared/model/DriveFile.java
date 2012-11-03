/**
 * NOTE:
 * The class com.google.api.services.drive.model.File already describes
 * the DriveFile object, but its modifier is set on final, so it could not be extended.
 */
package com.unito.tableplus.shared.model;

/**
 * This is the class for Google Drive objects descriptor.
 * 
 */
public class DriveFile implements Resource {

	private static final long serialVersionUID = 6325703345862427841L;
	private String title;
	private String type;
	private String id;
	private String link;

	public String getTitle() {
		return title;
	}

	public void setTitle(String title) {
		this.title = title;
	}

	public String getType() {
		return type;
	}

	public void setType(String type) {
		this.type = type;
	}

	public String getLink() {
		return link;
	}

	public void setLink(String link) {
		this.link = link;
	}

	@Override
	public String getName() {
		return getTitle();
	}

	@Override
	public String getIcon() {
		return "drive-file.png";
	}

	@Override
	public String getURI() {
		return this.link;
	}

	@Override
	public Provider getProvider() {
		return Provider.DRIVE;
	}

	@Override
	public String getID() {
		return id;
	}
	
	public void setID(String id){
		this.id = id;
	}

	@Override
	public void setURI(String uri) {
		this.link = uri;
	}

	
}
