package com.unito.tableplus.server.services;

import java.io.IOException;
import java.util.HashMap;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import com.google.gwt.user.server.rpc.RemoteServiceServlet;
import com.unito.tableplus.client.services.UserService;
import com.unito.tableplus.server.persistence.UserQueries;
import com.unito.tableplus.server.persistence.WalletQueries;
import com.unito.tableplus.shared.model.Bookmark;
import com.unito.tableplus.shared.model.DriveFile;
import com.unito.tableplus.shared.model.DropBoxFile;
import com.unito.tableplus.shared.model.FacebookEvent;
import com.unito.tableplus.shared.model.LoginInfo;
import com.unito.tableplus.shared.model.Resource;
import com.unito.tableplus.shared.model.TableObject;
import com.unito.tableplus.shared.model.User;
import com.unito.tableplus.shared.model.Wallet;

public class UserServiceImpl extends RemoteServiceServlet implements
		UserService {

	private static final long serialVersionUID = 2345237647330858842L;

	@Override
	public void storeUser(User user) {
		UserQueries.storeUser(user);
	}

	@Override
	public User queryUser(String fieldName, String fieldValue) {
		return UserQueries.queryUser(fieldName, fieldValue);
	}

	@Override
	public User queryUser(Long key) {
		return UserQueries.queryUser(key);
	}

	@Override
	public void deleteUser(Long key) {
		UserQueries.deleteUser(key);
	}

	@Override
	public Map<Long,User> queryUsers(List<Long> keys) {
		Map<Long,User> usersMap = null;
		List<User> usersList = UserQueries.queryUsers(keys); 
		if(usersList != null && !usersList.isEmpty()){
			usersMap =  new HashMap<Long,User>();
			for(User u : usersList)
				usersMap.put(u.getKey(), u);
		}
		return usersMap;
		
	}

	@Override
	public User loadUser(LoginInfo info) {
		User user = UserQueries.queryUser("email", info.getEmailAddress());
		if (user == null) {
			user = new User();
			user.setEmail(info.getEmailAddress());
			user.setUsername(info.getNickname());
			Long key = UserQueries.storeUser(user);
			User queried = UserQueries.queryUser(key);
			if (queried != null)
				user = queried;
		}
		return user;
	}

	@Override
	public List<Resource> loadUserObjects(User user) {
		Wallet wallet = WalletQueries.getWallet(user.getKey());
		List<Resource> resources = new ArrayList<Resource>();
		try {
			if (wallet.getDriveAccessToken() != null) {
				List<DriveFile> driveFiles = DriveServiceImpl.loadFiles(wallet);
				if (driveFiles != null)
					resources.addAll(driveFiles);
			}
		} catch (IOException e) {
			System.err.println("IO Error while loading Drive Files: " + e);
		}

		if (wallet.getDropboxToken() != null) {
			List<DropBoxFile> dropboxFiles = DropBoxServiceImpl
					.loadFiles(wallet);
			if (dropboxFiles != null)
				resources.addAll(dropboxFiles);
		}

		if (wallet.getFacebookToken() != null) {
			List<FacebookEvent> facebookEvents = FacebookServiceImpl
					.loadEvents(wallet);
			if (facebookEvents != null)
				resources.addAll(facebookEvents);
		}
		resources.addAll(UserQueries.getBookmark(user.getKey()));
		return resources;
	}
	
	@Override
	public boolean addBookmark(Long key, Bookmark bookmark) {
		return UserQueries.addBookmark(key, bookmark);
	}

	@Override
	public void removeBookmark(String key) {
		UserQueries.removeBookmark(key);
		
	}

	@Override
	public boolean addObject(Long key, TableObject o) {
		// TODO Auto-generated method stub
		return false;
	}
}
