package com.unito.tableplus.client.services;

import java.util.List;

import com.google.gwt.user.client.rpc.RemoteService;
import com.google.gwt.user.client.rpc.RemoteServiceRelativePath;
import com.unito.tableplus.shared.model.DriveFile;
import com.unito.tableplus.shared.model.Table;
import com.unito.tableplus.shared.model.BlackBoardMessage;
import com.unito.tableplus.shared.model.User;

@RemoteServiceRelativePath("table-service")
public interface TableService extends RemoteService {

	List<Table> queryTables(List<Long> keys);

	Long storeTable(Table table);

	Table queryTable(Long key);

	void deleteTable(Long key);

	boolean addBlackBoardMessage(Long tableKey, BlackBoardMessage bbMessage);
	
	List<BlackBoardMessage> getTableMessages(Long tableKey);
	
	boolean clearMessages(Long key);

	boolean addDocumentToTable(String DocId, User user, Long tableKey);

	void docAccessToNewMember(User newMember, Table table);

	List<DriveFile> getTableDriveFiles(Table table);

	void addMember(Long userKey, Long tableKey);

	void removeMessage(String messageKey);
}