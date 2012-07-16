package com.unito.tableplus.client.services;

import java.util.List;

import com.google.gwt.user.client.rpc.AsyncCallback;
import com.unito.tableplus.shared.model.LoginInfo;
import com.unito.tableplus.shared.model.Resource;
import com.unito.tableplus.shared.model.User;

public interface UserServiceAsync {

	void storeUser(User user, AsyncCallback<Void> callback);

	void queryUser(String fieldName, String fieldValue,
			AsyncCallback<User> callback);

	void queryUser(Long key, AsyncCallback<User> callback);

	void deleteUser(Long key, AsyncCallback<Void> callback);

	void queryUsers(List<Long> keys, AsyncCallback<List<User>> callback);

	void loadResources(User user, AsyncCallback<List<Resource>> callback);

	void loadUser(LoginInfo info, AsyncCallback<User> callback);

}
