package com.unito.tableplus.client.services;

import java.util.List;

import com.google.gwt.user.client.rpc.AsyncCallback;
import com.unito.tableplus.shared.model.Document;
import com.unito.tableplus.shared.model.Table;
import com.unito.tableplus.shared.model.Message;
import com.unito.tableplus.shared.model.User;

public interface TableServiceAsync {

	void queryTables(List<Long> keys, AsyncCallback<List<Table>> callback);

	void storeTable(Table table, AsyncCallback<Long> callback);

	void queryTable(Long key, AsyncCallback<Table> callback);

	void deleteTable(Long key, AsyncCallback<Void> callback);

	void addMessage(Long key, Message message, AsyncCallback<Boolean> callback);

	void clearMessages(Long key, AsyncCallback<Boolean> callback);

	void addDocumentToTable(String DocId, User user, Long tableKey,
			AsyncCallback<Boolean> callback);

	void docAccessToNewMember(User newMember, Table table,
			AsyncCallback<Boolean> callback);

	void getTableDocuments(Table table, AsyncCallback<List<Document>> callback);

	void addMemberToTable(Long userKey, Long tableKey,
			AsyncCallback<Boolean> callback);

	void addHiddenMemberToTable(Long userKey, Long tableKey,
			AsyncCallback<Boolean> callback);

	void removeHiddenMemberFromTable(Long userK, Long tableKey,
			AsyncCallback<Boolean> callback);

	void addSelectivePresenceMemberToTable(Long userKey, Long tableKey,
			AsyncCallback<Boolean> callback);

	void removeSelectivePresenceMemberFromTable(Long userKey, Long tableKey,
			AsyncCallback<Boolean> callback);

	void removeMessage(String messageKey, AsyncCallback<Void> callback);

}
