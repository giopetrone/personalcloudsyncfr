package com.unito.tableplus.client.services;

import com.google.gwt.user.client.rpc.AsyncCallback;

public interface FacebookServiceAsync {

	void getAuthUrl(AsyncCallback<String> callback);

}
