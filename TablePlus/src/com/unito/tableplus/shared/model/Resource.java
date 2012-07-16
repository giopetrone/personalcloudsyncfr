package com.unito.tableplus.shared.model;

import java.io.Serializable;

public abstract class Resource implements Serializable {
	
	private static final long serialVersionUID = -2157821573998748181L;
	private Provider provider;
	
	public Resource(Provider provider){
		this.setProvider(provider);
	}

	/**
	 * @return the provider
	 */
	public Provider getProvider() {
		return provider;
	}

	/**
	 * @param provider the provider to set
	 */
	public void setProvider(Provider provider) {
		this.provider = provider;
	}

}
