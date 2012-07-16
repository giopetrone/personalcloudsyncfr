package com.unito.tableplus.client.services;

import java.util.List;
import com.google.gwt.user.client.rpc.AsyncCallback;
import com.unito.tableplus.shared.model.ChannelMessageType;

public interface MessagingServiceAsync {

	void createChannel(String string, AsyncCallback<String> callback);

	void sendMessage(Long sender, String content, ChannelMessageType type,
			List<Long> recipients, AsyncCallback<String> callback);

	void sendMessage(Long sender, String content, ChannelMessageType type,
			List<Long> recipients, Long tableKey, AsyncCallback<String> callback);

	void sendMessage(Long sender, String content, ChannelMessageType type,
			Long recipient, Long tableKey, AsyncCallback<String> callback);

	void sendInvitationEmail(String sender, String recipient, Long tableKey,
			AsyncCallback<Boolean> callback);
}
