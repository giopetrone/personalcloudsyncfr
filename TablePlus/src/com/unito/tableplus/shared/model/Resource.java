package com.unito.tableplus.shared.model;

import java.io.Serializable;

public interface Resource extends Serializable {
	public String getName();

	public String getIcon();

	public String getURI();
	
	public Provider getProvider();
	
	//public void comment();
	//public void addTag();
	
	//public void share();
	//public void unshare();
}
