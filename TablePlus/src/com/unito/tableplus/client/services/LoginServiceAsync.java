package com.unito.tableplus.client.services;

import com.google.gwt.user.client.rpc.AsyncCallback;
import com.unito.tableplus.shared.model.LoginInfo;

public interface LoginServiceAsync {
	void login(String requestUri, AsyncCallback<LoginInfo> callback);

}
