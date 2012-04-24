package com.unito.tableplus.client.services;

import java.util.List;

import com.google.gwt.user.client.rpc.RemoteService;
import com.google.gwt.user.client.rpc.RemoteServiceRelativePath;
import com.unito.tableplus.shared.model.MessageType;

@RemoteServiceRelativePath("chatservice")
public interface ChatService extends RemoteService {

	/**
	 * Creates a new channel for a tableplus.spike.chat.client.
	 * Only one channel per client is allowed at the same time.
	 * 
	 * @param user
	 *            the user-id the channel will be created for. It must be a
	 *            unique id
	 * @return The token to use for the channel.
	 */
	String createChannel(String user);

	/**
	 * This method can be used to retrieve the list of user connected to the
	 * Chat Service
	 * 
	 * @return The user-id list of connected users
	 */
	List<String> getUsersList();

	/**
	 * Sends a message to all the connected users
	 * @param sender The sender of the message
	 * @param content The message content
	 * @param type The type of the message
	 * @return Some string returned by the callback
	 */
	String sendMessage(String sender, String content, MessageType type);

	/**
	 * Sends a message to the connected users specified in the recipient list
	 * @param sender The sender of the message
	 * @param content The message content
	 * @param type The type of the message
	 * @param recipients
	 * @return Some String returned by the callback
	 */
	String sendMessage(String sender, String content, MessageType type,
			List<String> recipients);

}
