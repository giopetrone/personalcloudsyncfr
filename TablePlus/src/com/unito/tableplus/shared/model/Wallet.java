package com.unito.tableplus.shared.model;

import javax.jdo.annotations.IdGeneratorStrategy;
import javax.jdo.annotations.PersistenceCapable;
import javax.jdo.annotations.Persistent;
import javax.jdo.annotations.PrimaryKey;

@PersistenceCapable(detachable = "true")
public class Wallet {
	
	@PrimaryKey
	@Persistent(valueStrategy = IdGeneratorStrategy.IDENTITY)
	private Long userKey;
	
	@Persistent
	private String driveToken;
	
	@Persistent
	private String dropboxToken;

	@Persistent
	private String dropboxSecret;

	public String getDriveToken() {
		return driveToken;
	}

	public void setDriveToken(String driveToken) {
		this.driveToken = driveToken;
	}

	public String getDropboxToken() {
		return dropboxToken;
	}

	public void setDropboxToken(String dropboxToken) {
		this.dropboxToken = dropboxToken;
	}

	public String getDropboxSecret() {
		return dropboxSecret;
	}

	public void setDropboxSecret(String dropboxSecret) {
		this.dropboxSecret = dropboxSecret;
	}


	/**
	 * @return the userKey
	 */
	public Long getUserKey() {
		return userKey;
	}

	/**
	 * @param userKey the userKey to set
	 */
	public void setUserKey(Long userKey) {
		this.userKey = userKey;
	}
}
