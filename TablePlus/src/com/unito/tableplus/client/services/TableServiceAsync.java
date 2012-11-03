package com.unito.tableplus.client.services;

import java.util.List;

import com.google.gwt.user.client.rpc.AsyncCallback;
import com.unito.tableplus.shared.model.BlackBoardMessage;
import com.unito.tableplus.shared.model.Bookmark;
import com.unito.tableplus.shared.model.Resource;
import com.unito.tableplus.shared.model.SharedResource;
import com.unito.tableplus.shared.model.Table;
import com.unito.tableplus.shared.model.User;

public interface TableServiceAsync {

	void queryTables(List<Long> keys, AsyncCallback<List<Table>> callback);

	void storeTable(Table table, AsyncCallback<Long> callback);

	void queryTable(Long key, AsyncCallback<Table> callback);

	void deleteTable(Long key, AsyncCallback<Void> callback);

	void addBlackBoardMessage(Long key, BlackBoardMessage bbMessage, AsyncCallback<Boolean> callback);

	void clearMessages(Long key, AsyncCallback<Boolean> callback);

	void addResource(Resource resource, User user, Long tableKey,AsyncCallback<Boolean> callback);

	void loadResources(Long tableKey, AsyncCallback<List<SharedResource>> callback);

	void addMember(Long currentUser, Long newUserKey, Long tableKey, AsyncCallback<Void> callback);

	void removeMessage(String messageKey, AsyncCallback<Void> callback);

	void loadBlackBoardMessages(Long tableKey,
			AsyncCallback<List<BlackBoardMessage>> callback);

	void loadBookmarks(Long tableKey, AsyncCallback<List<Bookmark>> callback);

	void addBookmark(Long key, Bookmark bookmark, AsyncCallback<Boolean> asyncCallback);

	void removeBookmark(String key, AsyncCallback<Void> asyncCallback);


}
