package com.unito.tableplus.client.services;

import java.util.List;

import com.google.gwt.user.client.rpc.RemoteService;
import com.google.gwt.user.client.rpc.RemoteServiceRelativePath;
import com.unito.tableplus.shared.model.ChannelMessageType;

@RemoteServiceRelativePath("chatservice")
public interface MessagingService extends RemoteService {

	/**
	 * Creates a new channel for a tableplus.spike.chat.client. Only one channel
	 * per client is allowed at the same time.
	 * 
	 * @param user
	 *            the user-id the channel will be created for. It must be a
	 *            unique id
	 * @return The token to use for the channel.
	 */
	String createChannel(String user);

	/**
	 * Sends a message to the connected users specified in the recipient list
	 * 
	 * @param sender
	 *            The sender of the message
	 * @param content
	 *            The message content
	 * @param type
	 *            The type of the message
	 * @param recipients
	 * @return Some String returned by the callback
	 */
	String sendMessage(Long sender, String content, ChannelMessageType type,
			List<Long> recipients);

	/**
	 * Sends a message to the connected users specified in the recipient list
	 * belonging to the specified table
	 * 
	 * @param sender
	 *            The sender of the message
	 * @param content
	 *            The message content
	 * @param type
	 *            The type of the message
	 * @param recipients
	 * @return Some String returned by the callback
	 */
	String sendMessage(Long sender, String content, ChannelMessageType type,
			List<Long> recipients, Long tableKey);

	String sendMessage(Long sender, String content, ChannelMessageType type,
			Long recipient, Long tableKey);

	boolean sendInvitationEmail(String sender, String recipient, Long tableKey);

}
