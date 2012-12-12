package com.unito.tableplus.client.services;

import java.util.List;
import java.util.Map;

import com.google.gwt.user.client.rpc.AsyncCallback;
import com.unito.tableplus.shared.model.BlackBoardMessage;
import com.unito.tableplus.shared.model.Resource;
import com.unito.tableplus.shared.model.TableObject;
import com.unito.tableplus.shared.model.Table;
import com.unito.tableplus.shared.model.User;
import com.unito.tableplus.shared.model.UserStatus;

public interface TableServiceAsync {
	void storeNewTable(Table newTable, User user,
			AsyncCallback<Table> callback);

	void queryTable(Long key, AsyncCallback<Table> callback);
	
	void queryTables(List<Long> keys, AsyncCallback<Map<Long,Table>> callback);

	void storeTable(Table table, AsyncCallback<Boolean> callback);

	void deleteTable(Long key, AsyncCallback<Void> callback);

	void addBlackBoardMessage(Long key, BlackBoardMessage bbMessage, AsyncCallback<Boolean> callback);

	void clearMessages(Long key, AsyncCallback<Boolean> callback);

	void addResource(Resource resource, User user, Long tableKey,AsyncCallback<Boolean> callback);

	void loadResources(Long tableKey, AsyncCallback<List<TableObject>> callback);

	void addMember(Long currentUser, Long newUserKey, Long tableKey, AsyncCallback<Void> callback);

	void removeMessage(String messageKey, AsyncCallback<Void> callback);

	void loadBlackBoardMessages(Long tableKey,
			AsyncCallback<List<BlackBoardMessage>> callback);



	void getUsersStatus(Long tableKey,
			AsyncCallback<Map<Long, UserStatus>> callback);

	void queryObject(String key, AsyncCallback<TableObject> asyncCallback);

	void editComment(TableObject b, String key,
			AsyncCallback<String> asyncCallback);
}
