package com.unito.tableplus.client.services;

import com.google.gwt.user.client.rpc.RemoteService;
import com.google.gwt.user.client.rpc.RemoteServiceRelativePath;
import com.unito.tableplus.shared.model.ChannelMessageType;

@RemoteServiceRelativePath("chatservice")
public interface MessagingService extends RemoteService {

	/**
	 * Creates a new channel for a client. Only one channel per client is
	 * allowed at the same time.
	 * 
	 * @param user
	 *            the user-id the channel will be created for. It must be a
	 *            unique id
	 * @return The token to use for the channel.
	 */
	String createChannel(String user);

	/**
	 * @param sender
	 * @param content
	 * @param type
	 * @param tableKey
	 * @return
	 */
	String sendMessage(Long sender, String content, ChannelMessageType type, Long tableKey);

	boolean sendInvitationEmail(String sender, String recipient, Long tableKey);

}
