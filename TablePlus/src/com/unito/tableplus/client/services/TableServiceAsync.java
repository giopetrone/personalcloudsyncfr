package com.unito.tableplus.client.services;

import java.util.List;

import com.google.gwt.user.client.rpc.AsyncCallback;
import com.unito.tableplus.shared.model.DriveFile;
import com.unito.tableplus.shared.model.Table;
import com.unito.tableplus.shared.model.BlackBoardMessage;
import com.unito.tableplus.shared.model.User;

public interface TableServiceAsync {

	void queryTables(List<Long> keys, AsyncCallback<List<Table>> callback);

	void storeTable(Table table, AsyncCallback<Long> callback);

	void queryTable(Long key, AsyncCallback<Table> callback);

	void deleteTable(Long key, AsyncCallback<Void> callback);

	void addBlackBoardMessage(Long key, BlackBoardMessage bbMessage, AsyncCallback<Boolean> callback);

	void clearMessages(Long key, AsyncCallback<Boolean> callback);

	void addDocumentToTable(String DocId, User user, Long tableKey,
			AsyncCallback<Boolean> callback);

	void docAccessToNewMember(User newMember, Table table,
			AsyncCallback<Void> callback);

	void getTableDriveFiles(Table table, AsyncCallback<List<DriveFile>> callback);

	void addMember(Long userKey, Long tableKey, AsyncCallback<Void> callback);

	void removeMessage(String messageKey, AsyncCallback<Void> callback);

	void getTableMessages(Long tableKey,
			AsyncCallback<List<BlackBoardMessage>> callback);

}
