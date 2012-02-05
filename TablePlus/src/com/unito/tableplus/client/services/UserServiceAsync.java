package com.unito.tableplus.client.services;

import com.google.gwt.user.client.rpc.AsyncCallback;
import com.unito.tableplus.shared.model.User;

public interface UserServiceAsync {

	void queryUserByUsername(String username, AsyncCallback<User> callback);

	void queryUserByEmail(String email, AsyncCallback<User> callback);

	void storeUser(User user, AsyncCallback<Void> callback);

}
