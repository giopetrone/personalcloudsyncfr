package com.unito.tableplus.shared.model;

import java.io.Serializable;

import com.extjs.gxt.ui.client.data.BaseModel;

public interface Resource extends Serializable {
	public String getName();

	public String getIcon();

	public String getURI();

	public BaseModel getModel();
	
	public Provider getProvider();
}
