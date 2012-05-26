package com.unito.tableplus.client.services;

import java.util.List;
import com.google.gwt.user.client.rpc.AsyncCallback;
import com.unito.tableplus.shared.model.MessageType;

public interface ChatServiceAsync {

	void createChannel(String string, AsyncCallback<String> callback);

	void getUsersList(AsyncCallback<List<String>> callback);

	void sendMessage(String sender, String content, MessageType type,
			AsyncCallback<String> callback);

	void sendMessage(String sender, String content, MessageType type,
			List<String> recipients, AsyncCallback<String> callback);

	void sendMessage(String sender, String content, MessageType type,
			List<String> recipients, Long tableKey, AsyncCallback<String> callback);
}
