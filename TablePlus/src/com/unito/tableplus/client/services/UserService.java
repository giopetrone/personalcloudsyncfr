package com.unito.tableplus.client.services;

import com.google.gwt.user.client.rpc.RemoteService;
import com.unito.tableplus.shared.model.User;

public interface UserService extends RemoteService {
	User queryUserByName(String username);
	User queryUserByEmail(String email);
	void storeUser(User user);
	
}
