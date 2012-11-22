package com.unito.tableplus.server.services;

import java.util.HashMap;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import com.google.gwt.user.server.rpc.RemoteServiceServlet;
import com.unito.tableplus.client.services.TableService;
import com.unito.tableplus.server.persistence.TableQueries;
import com.unito.tableplus.server.persistence.UserQueries;
import com.unito.tableplus.server.persistence.WalletQueries;
import com.unito.tableplus.shared.model.BlackBoardMessage;
import com.unito.tableplus.shared.model.Provider;
import com.unito.tableplus.shared.model.Resource;
import com.unito.tableplus.shared.model.SharedResource;
import com.unito.tableplus.shared.model.Table;
import com.unito.tableplus.shared.model.User;
import com.unito.tableplus.shared.model.UserStatus;
import com.unito.tableplus.shared.model.Wallet;

public class TableServiceImpl extends RemoteServiceServlet implements TableService {

	private static final long serialVersionUID = -3403034728905706407L;
	
	@Override
	public Table storeNewTable(Table table, User creator){
		return TableQueries.storeNewTable(table, creator);
	}

	@Override
	public Map<Long,Table> queryTables(List<Long> keys) {
		Map<Long,Table> tablesMap = null;
		List<Table> tablesList = TableQueries.queryTables(keys);
		if(tablesList != null){
			tablesMap = new HashMap<Long,Table>();
			for(Table t : tablesList)
				tablesMap.put(t.getKey(), t);
			}
		return tablesMap;
	}

	@Override
	public boolean storeTable(Table table) {
		return TableQueries.storeTable(table);
	}

	@Override
	public Table queryTable(Long key) {
		return TableQueries.queryTable(key);
	}

	@Override
	public void deleteTable(Long key) {
		TableQueries.deleteTable(key);
	}
	
	@Override
	public Map<Long,UserStatus> getUsersStatus(Long tableKey){
		return MessagingServiceImpl.getTableStatus(tableKey);
	}

	@Override
	public boolean addBlackBoardMessage(Long key, BlackBoardMessage bbMessage) {
		return TableQueries.addMessage(key, bbMessage);
	}

	@Override
	public void removeMessage(String messageKey) {
		TableQueries.removeMessage(messageKey);
	}

	@Override
	public boolean clearMessages(Long key) {
		return TableQueries.clearMessages(key);
	}

	@Override
	public List<SharedResource> loadResources(Long tableKey) {
			return TableQueries.queryResources(tableKey);
	}
	
	@Override
	public boolean addResource(Resource resource, User user, Long tableKey) {
		Provider resourceProvider = resource.getProvider();
		//TODO check if resource has already been added
		if(resourceProvider.equals(Provider.DRIVE)){
			Wallet wallet = WalletQueries.getWallet(user.getKey());
			List<Long> userKeys = TableQueries.queryTable(tableKey).getMembers();
			List<User> users = UserQueries.queryUsers(userKeys);
			List<String> userEmails = new ArrayList<String>();
			for(User u : users)
				userEmails.add(u.getEmail());
			userEmails.remove(user.getEmail());//owner removed from list
			if(!userEmails.isEmpty())
			DriveServiceImpl.shareFile(resource.getID(), wallet, userEmails);
			
		} else if(resourceProvider.equals(Provider.DROPBOX)){
			Wallet wallet = WalletQueries.getWallet(user.getKey());
			String shareLink = DropBoxServiceImpl.shareFile(wallet, resource.getID());
			resource.setURI(shareLink);
		}
			
		return TableQueries.addResource(resource, tableKey);
	}

	@Override
	public void addMember(Long currentUserKey, Long newUserKey, Long tableKey) {
		User user = UserQueries.queryUser(newUserKey);
		Wallet wallet = WalletQueries.getWallet(currentUserKey);
		TableQueries.addMember(newUserKey, tableKey);
		List<SharedResource> resources = TableQueries.queryResources(tableKey);
		
		if(wallet.getDriveAccessToken() != null)
		for(Resource r : resources){
			List<String> toShare = new ArrayList<String>();
			if(r.getProvider().equals(Provider.DRIVE))
				toShare.add(r.getID());
			if(!toShare.isEmpty())
			DriveServiceImpl.shareFiles(toShare, wallet, user.getEmail());
		}
	}

	@Override
	public List<BlackBoardMessage> loadBlackBoardMessages(Long tableKey) {
		return TableQueries.queryMessages(tableKey);
	}



}
