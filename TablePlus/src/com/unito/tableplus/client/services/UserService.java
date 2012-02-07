package com.unito.tableplus.client.services;

import com.google.gwt.user.client.rpc.RemoteService;
import com.google.gwt.user.client.rpc.RemoteServiceRelativePath;
import com.unito.tableplus.shared.model.User;

@RemoteServiceRelativePath("user-service")
public interface UserService extends RemoteService {

	User getCurrentUser();
	
	void storeUser(User user);

	User queryUser(String fieldName, String fieldValue);

	User queryUser(Long key);

	void deleteUser(Long key);

	public String isLoggedIn(String requestUri);
}
