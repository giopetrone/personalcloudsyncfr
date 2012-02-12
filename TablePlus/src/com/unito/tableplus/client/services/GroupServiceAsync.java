package com.unito.tableplus.client.services;

import com.google.gwt.user.client.rpc.AsyncCallback;
import com.unito.tableplus.shared.model.Group;
import com.unito.tableplus.shared.model.Message;

public interface GroupServiceAsync {

	void storeGroup(Group group, AsyncCallback<Long> callback);
	
	void queryGroup(Long key, AsyncCallback<Group> callback);

	void deleteGroup(Long key, AsyncCallback<Void> callback);
	
	void addMessage(Long key, Message message, AsyncCallback<Boolean> callback);

	void clearMessages(Long key, AsyncCallback<Boolean> callback);	

}
