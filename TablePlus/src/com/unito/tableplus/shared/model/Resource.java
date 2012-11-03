package com.unito.tableplus.shared.model;

import java.io.Serializable;

public interface Resource extends Serializable {
	public String getID();
	
	public String getName();

	public String getIcon();

	public String getURI();
	
	public void setURI(String uri);
	
	public Provider getProvider();
}
