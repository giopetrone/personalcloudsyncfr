package com.unito.tableplus.client.services;

import java.util.List;
import java.util.Map;

import com.google.gwt.user.client.rpc.RemoteService;
import com.google.gwt.user.client.rpc.RemoteServiceRelativePath;
import com.unito.tableplus.shared.model.Bookmark;
import com.unito.tableplus.shared.model.LoginInfo;
import com.unito.tableplus.shared.model.Resource;
import com.unito.tableplus.shared.model.TableObject;
import com.unito.tableplus.shared.model.User;

@RemoteServiceRelativePath("user-service")
public interface UserService extends RemoteService {

	Map<Long,User> queryUsers(List<Long> keys);
	
	void storeUser(User user);

	User queryUser(String fieldName, String fieldValue);

	User queryUser(Long key);

	void deleteUser(Long key);

	List<Resource> loadUserObjects(User user);

	User loadUser(LoginInfo info);
	
	/**
	 * Adds a bookmark on the table bookmarks list
	 * 
	 * @param tableKey
	 *            The key of the table where the bookmark should be added.
	 * @param bookmark
	 *            The bookmark to add
	 * @return True if the bookmark has been added, false instead.
	 */
	boolean addBookmark(Long Key, Bookmark bookmark);	
	
	//List<Resource> loadBookmarks(Long userKey);	
	
	void removeBookmark(String key);

	boolean addObject(Long key, TableObject o);
}
