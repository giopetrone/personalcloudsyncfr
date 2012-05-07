package com.unito.tableplus.client.services;

import java.util.List;

import com.google.gwt.user.client.rpc.AsyncCallback;
import com.unito.tableplus.shared.model.Document;
import com.unito.tableplus.shared.model.Group;
import com.unito.tableplus.shared.model.Message;
import com.unito.tableplus.shared.model.User;

public interface GroupServiceAsync {

	void queryGroups(List<Long> keys, AsyncCallback<List<Group>> callback);

	void storeGroup(Group group, AsyncCallback<Long> callback);

	void queryGroup(Long key, AsyncCallback<Group> callback);

	void deleteGroup(Long key, AsyncCallback<Void> callback);

	void addMessage(Long key, Message message, AsyncCallback<Boolean> callback);

	void clearMessages(Long key, AsyncCallback<Boolean> callback);

	void addDocumentToGroup(String DocId, User user, Long groupKey,
			AsyncCallback<Boolean> callback);

	void storeGroupProva(Group group, AsyncCallback<Long> callback);

	void docAccessToNewMember(User newMember, Group group,
			AsyncCallback<Boolean> callback);

	void getGroupDocuments(Group group, AsyncCallback<List<Document>> callback);

	void addMemberToGroup(Long userKey, Long groupKey,
			AsyncCallback<Boolean> callback);

	void addHiddenMemberToGroup(Long userKey, Long groupKey,
			AsyncCallback<Boolean> callback);

	void removeHiddenMemberFromGroup(Long userK, Long groupKey,
			AsyncCallback<Boolean> callback);

	void addSelectivePresenceMemberToGroup(Long userKey, Long groupKey,
			AsyncCallback<Boolean> callback);

	void removeSelectivePresenceMemberFromGroup(Long userKey, Long groupKey,
			AsyncCallback<Boolean> callback);

	void removeMessage(String messageKey, AsyncCallback<Void> callback);

}
