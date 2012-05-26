package com.unito.tableplus.client.services;

import java.util.List;

import com.google.gwt.user.client.rpc.RemoteService;
import com.google.gwt.user.client.rpc.RemoteServiceRelativePath;
import com.unito.tableplus.shared.model.Document;
import com.unito.tableplus.shared.model.Table;
import com.unito.tableplus.shared.model.Message;
import com.unito.tableplus.shared.model.User;

@RemoteServiceRelativePath("table-service")
public interface TableService extends RemoteService {

	List<Table> queryTables(List<Long> keys);

	Long storeTable(Table table);

	Table queryTable(Long key);

	void deleteTable(Long key);

	boolean addMessage(Long key, Message message);

	boolean clearMessages(Long key);

	boolean addDocumentToTable(String DocId, User user, Long tableKey);

	boolean docAccessToNewMember(User newMember, Table table);

	List<Document> getTableDocuments(Table table);

	boolean addMemberToTable(Long userKey, Long tableKey);

	boolean addHiddenMemberToTable(Long userKey, Long tableKey);

	boolean removeHiddenMemberFromTable(Long userKey, Long tableKey);

	boolean addSelectivePresenceMemberToTable(Long userKey, Long tableKey);

	boolean removeSelectivePresenceMemberFromTable(Long userKey, Long tableKey);

	void removeMessage(String messageKey);
}