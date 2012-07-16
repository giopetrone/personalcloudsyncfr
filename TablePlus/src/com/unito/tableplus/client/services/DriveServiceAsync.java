package com.unito.tableplus.client.services;

import com.google.gwt.user.client.rpc.AsyncCallback;

public interface DriveServiceAsync {

	void getRequestTokenURL(AsyncCallback<String> callback);
	
}
