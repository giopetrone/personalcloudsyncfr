package com.unito.tableplus.client.services;

import java.util.List;

import com.google.gwt.user.client.rpc.RemoteService;
import com.google.gwt.user.client.rpc.RemoteServiceRelativePath;
import com.unito.tableplus.shared.model.Group;
import com.unito.tableplus.shared.model.Message;



@RemoteServiceRelativePath("group-service")
public interface GroupService extends RemoteService {

	List<Group> queryGroups(List<Long> keys);
	
	Long storeGroup(Group group);
	
	Group queryGroup(Long key);
	
	void deleteGroup(Long key);
	
	boolean addMessage(Long key, Message message);

	boolean clearMessages(Long key);
	
}