package com.unito.tableplus.client.services;

import java.util.List;

import com.google.gwt.user.client.rpc.RemoteService;
import com.google.gwt.user.client.rpc.RemoteServiceRelativePath;
import com.unito.tableplus.shared.model.Document;
import com.unito.tableplus.shared.model.Group;
import com.unito.tableplus.shared.model.Message;
import com.unito.tableplus.shared.model.User;

@RemoteServiceRelativePath("group-service")
public interface GroupService extends RemoteService {

	List<Group> queryGroups(List<Long> keys);

	Long storeGroup(Group group);

	Long storeGroupProva(Group group);

	Group queryGroup(Long key);

	void deleteGroup(Long key);

	boolean addMessage(Long key, Message message);

	boolean clearMessages(Long key);

	boolean addDocumentToGroup(String DocId, User user, Long groupKey);

	boolean docAccessToNewMember(User newMember, Group group);

	List<Document> getGroupDocuments(Group group);

	boolean addMemberToGroup(Long userKey, Long groupKey);

	boolean addHiddenMemberToGroup(Long userKey, Long groupKey);

	boolean removeHiddenMemberFromGroup(Long userKey, Long groupKey);

	boolean addSelectivePresenceMemberToGroup(Long userKey, Long groupKey);

	boolean removeSelectivePresenceMemberFromGroup(Long userKey, Long groupKey);
}