package com.unito.tableplus.client.services;

import com.google.gwt.user.client.rpc.AsyncCallback;
import com.unito.tableplus.shared.model.User;

public interface UserServiceAsync {

	void storeUser(User user, AsyncCallback<Void> callback);

	void queryUser(String fieldName, String fieldValue,
			AsyncCallback<User> callback);

	void queryUser(Long key, AsyncCallback<User> callback);

	void deleteUser(Long key, AsyncCallback<Void> callback);

}
