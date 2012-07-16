package com.unito.tableplus.client.services;


import com.google.gwt.user.client.rpc.AsyncCallback;

public interface DropBoxServiceAsync {

	void getAuthUrl(AsyncCallback<String> callback);
}
