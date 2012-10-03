package com.unito.tableplus.shared.model;

/**
 * This is the class for Google Drive objects descriptor.
 *
 */
public class DriveFile implements Resource {
	
	private static final long serialVersionUID = 6325703345862427841L;
	private String title;
	private String type;
	private String DocId;
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
	public String getDocId() {
		return DocId;
	}
	public void setDocId(String docId) {
		DocId = docId;
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
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public Provider getProvider() {
		return Provider.DRIVE;
	}

}
