package com.unito.tableplus.shared;

import java.io.Serializable;

public class Wallet implements Serializable{

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	
	private String GoogleDocSessionToken;

	public String getGoogleDocSessionToken() {
		return GoogleDocSessionToken;
	}

	public void setGoogleDocSessionToken(String googleDocSessionToken) {
		GoogleDocSessionToken = googleDocSessionToken;
	}

}
