package com.unito.tableplus.client.services;

import java.util.List;
import java.util.Map;

import com.google.gwt.user.client.rpc.RemoteService;
import com.google.gwt.user.client.rpc.RemoteServiceRelativePath;
import com.unito.tableplus.shared.model.LoginInfo;
import com.unito.tableplus.shared.model.Resource;
import com.unito.tableplus.shared.model.User;

@RemoteServiceRelativePath("user-service")
public interface UserService extends RemoteService {

	Map<Long,User> queryUsers(List<Long> keys);
	
	void storeUser(User user);

	User queryUser(String fieldName, String fieldValue);

	User queryUser(Long key);

	void deleteUser(Long key);

	List<Resource> loadResources(User user);

	User loadUser(LoginInfo info);
}
