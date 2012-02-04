package com.unito.tableplus.shared.model;

import javax.jdo.annotations.Persistent;

public class GoogleUser extends User {

	private static final long serialVersionUID = -147740847060485538L;
	
	@Persistent
	private String token;
	
	@Persistent
	private String loginUrl;
	
	@Persistent
	private String logoutUrl;	
	
	public String getToken(){
		return this.token;
	}

	public String getLoginUrl() {
		return loginUrl;
	}

	public void setLoginUrl(String loginUrl) {
		this.loginUrl = loginUrl;
	}

	public String getLogoutUrl() {
		return logoutUrl;
	}

	public void setLogoutUrl(String logoutUrl) {
		this.logoutUrl = logoutUrl;
	}

}
