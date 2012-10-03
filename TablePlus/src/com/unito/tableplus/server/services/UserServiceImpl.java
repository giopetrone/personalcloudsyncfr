package com.unito.tableplus.server.services;

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
		User user = queryUser("email", info.getEmailAddress());
		if (user == null) {
			user = new User();
			user.setEmail(info.getEmailAddress());
			user.setUsername(info.getNickname());
			storeUser(user);
		}
		return user;
	}

	@Override
	public List<Resource> loadResources(User user) {
		Wallet wallet = WalletQueries.getWallet(user.getKey());
		List<Resource> resources = new LinkedList<Resource>();

		List<DriveFile> driveFiles = loadDriveFiles(wallet.getDriveToken());
		List<DropBoxFile> dropboxFiles = loadDropboxFiles(
				wallet.getDropboxToken(), wallet.getDropboxSecret());
		List<FacebookEvent> facebookEvents = loadFacebookEvents(wallet.getFacebookToken());
		
		if (driveFiles != null)
			resources.addAll(driveFiles);
		if (dropboxFiles != null)
			resources.addAll(dropboxFiles);
		if (facebookEvents != null)
			resources.addAll(facebookEvents);

		return resources;
	}

	public List<DriveFile> loadDriveFiles(String token) {
		if (token != null)
			return DriveServiceImpl.getDriveFileList(token);
		return null;
	}

	public List<DropBoxFile> loadDropboxFiles(String token, String secret) {
		if (token != null && secret != null)
			return DropBoxServiceImpl.loadFiles(token, secret);
		return null;
	}
	
	public List<FacebookEvent> loadFacebookEvents(String token){
		if (token != null)
			return FacebookServiceImpl.loadEvents(token);
		return null;
	}

}
