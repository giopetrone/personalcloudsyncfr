package com.unito.tableplus.shared.model;

/**
 * This is the class for DropBox objects descriptor.
 * 
 */
public class DropBoxFile implements Resource{

	private static final long serialVersionUID = -1506954532942923572L;
	private String size;
	private String rev;
	private String thumb_exists;
	private String bytes;
	private String modified;
	private String client_mtime;
	private String path;
	private Boolean isDir;
	private String icon;
	private String mimeTipe;
	private String revision;

	public String getSize() {
		return size;
	}

	public void setSize(String size) {
		this.size = size;
	}

	public String getRev() {
		return rev;
	}

	public void setRev(String rev) {
		this.rev = rev;
	}

	public String getThumb_exists() {
		return thumb_exists;
	}

	public void setThumb_exists(String thumb_exists) {
		this.thumb_exists = thumb_exists;
	}

	public String getBytes() {
		return bytes;
	}

	public void setBytes(String bytes) {
		this.bytes = bytes;
	}

	public String getModified() {
		return modified;
	}

	public void setModified(String modified) {
		this.modified = modified;
	}

	public String getClient_mtime() {
		return client_mtime;
	}

	public void setClient_mtime(String client_mtime) {
		this.client_mtime = client_mtime;
	}

	public String getPath() {
		return path;
	}

	public void setPath(String path) {
		this.path = path;
	}

	public Boolean getIsDir() {
		return isDir;
	}

	public void setIsDir(Boolean isDir) {
		this.isDir = isDir;
	}

	public String getMimeTipe() {
		return mimeTipe;
	}

	public void setMimeTipe(String mimeTipe) {
		this.mimeTipe = mimeTipe;
	}

	public String getRevision() {
		return revision;
	}

	public void setRevision(String revision) {
		this.revision = revision;
	}
	
	@Override
	public String getIcon() {
		return icon + ".gif";
	}

	public void setIcon(String icon) {
		this.icon = icon;
	}

	@Override
	public String getName() {
		return getPath().substring(1);
	}

	@Override
	public String getURI() {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public Provider getProvider() {
		return Provider.DROPBOX;
	}
	
	@Override
	public String toString() {
		return "Path: " + this.getPath() + " Icon: " + this.getIcon()
				+ " Is Dir?: " + this.getIsDir();
	}
}
