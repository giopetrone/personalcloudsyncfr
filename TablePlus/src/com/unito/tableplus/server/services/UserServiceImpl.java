package com.unito.tableplus.server.services;

import java.io.IOException;
import java.util.LinkedList;
import java.util.List;

import com.google.gwt.user.server.rpc.RemoteServiceServlet;
import com.unito.tableplus.client.services.UserService;
import com.unito.tableplus.server.UserQueries;
import com.unito.tableplus.server.WalletQueries;
import com.unito.tableplus.shared.model.DriveFile;
import com.unito.tableplus.shared.model.DropBoxFile;
import com.unito.tableplus.shared.model.FacebookEvent;
import com.unito.tableplus.shared.model.LoginInfo;
import com.unito.tableplus.shared.model.Resource;
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
	public List<User> queryUsers(List<Long> keys) {
		return UserQueries.queryUsers(keys);
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
	public List<Resource> loadResources(User user) {
		Wallet wallet = WalletQueries.getWallet(user.getKey());
		List<Resource> resources = new LinkedList<Resource>();
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

		return resources;
	}

}
