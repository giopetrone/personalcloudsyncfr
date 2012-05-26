package com.unito.tableplus.server.services;

import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;

import org.json.JSONException;
import org.json.JSONObject;

import com.google.appengine.api.channel.ChannelFailureException;
import com.google.appengine.api.channel.ChannelMessage;
import com.google.appengine.api.channel.ChannelService;
import com.google.appengine.api.channel.ChannelServiceFactory;
import com.google.gwt.user.server.rpc.RemoteServiceServlet;
import com.unito.tableplus.client.services.ChatService;
import com.unito.tableplus.shared.model.MessageType;

public class ChatServiceImpl extends RemoteServiceServlet implements
		ChatService {
	private static final long serialVersionUID = 1093735951433111434L;
	private static ChannelService channelService = ChannelServiceFactory
			.getChannelService();

	/**
	 * A list of connected users. Users ids are stored as strings and must be
	 * uniques.
	 */
	private static List<String> users = new LinkedList<String>();

	@Override
	public String createChannel(String userId) {
		try {
			String token = channelService.createChannel(userId);
			return token;
		} catch (ChannelFailureException channelFailureException) {
			System.err.println("Error creating the channel: "
					+ channelFailureException);
			return null;
		} catch (Exception otherException) {
			System.err.println("Unknown exception while creating channel: "
					+ otherException);
			return null;
		}
	}
	
	@Override
	public String sendMessage(String sender, String content, MessageType type) {
		System.out.println("Dentro ChatServiceImpl.sendMessage()");
		try {
			JSONObject jsonMessage =  JSONMessageBuilder(sender, type.toString(), content);
			for (String user : users){
				System.out.println(" --> "+user);
				//if(!user.equals(sender))
					channelService.sendMessage(new ChannelMessage(user,
							jsonMessage.toString()));
			}
			return "Message sent";
		} catch (JSONException e) {
			return "Error creating JSON";
		}
	}
	
	@Override
	public String sendMessage(String sender, String content, MessageType type,
			List<String> recipients) {
		try {
			JSONObject jsonMessage =  JSONMessageBuilder(sender, type.toString(), content);
			for (String user : recipients)
				//if(!user.equals(sender))
					channelService.sendMessage(new ChannelMessage(user,
							jsonMessage.toString()));
			return "Message sent";
		} catch (JSONException e) {
			return "Error creating JSON";
		}
	}

	@Override
	public String sendMessage(String sender, String content, MessageType type,
			List<String> recipients, Long tableKey) {
		try {
			JSONObject jsonMessage =  JSONMessageBuilder(sender, type.toString(), content,tableKey.toString());
			for (String user : recipients)
				//if(!user.equals(sender))
					channelService.sendMessage(new ChannelMessage(user,
							jsonMessage.toString()));
			return "Message sent";
		} catch (JSONException e) {
			return "Error creating JSON";
		}
	}
	
	@Override
	public List<String> getUsersList() {
		return users;
	}

	/**
	 * Adds a user to the list of connected users
	 * 
	 * @param user
	 *            The user id to add to the list.
	 */
	public static void addUser(String user) {
		users.add(user);
		//TODO: Send a message to notify the connection to other users
	}

	/**
	 * Removes the user passed as parameter from the list of connected users if
	 * it exists.
	 * 
	 * @param user
	 *            The user id to remove.
	 */
	public static void removeUser(String user) {
		Iterator<String> i = users.iterator();
		Boolean found = false;
		while (i.hasNext() && !found) {
			if (i.next().equals(user)) {
				i.remove();
				found = true;
			}
		}
		//TODO: Send a message to notify the disconnection to other users
	}

	/**
	 * Checks if a user is connected to the Chat Service by the user identity
	 * 
	 * @param user
	 *            The user identity to check status of.
	 * @return True if the user is connected, false if not.
	 */
	public static boolean isConnected(String user) {
		return users.contains(user);
	}

	/**
	 * Returns the number of users connected to the chat service.
	 * 
	 * @return The integer representing the number of connected users.
	 */
	public int usersCount() {
		return users.size();
	}

	private static JSONObject JSONMessageBuilder(String sender, String type,
			String content) throws JSONException {
		JSONObject jsonMessage = new JSONObject();
		jsonMessage.append("sender", sender);
		jsonMessage.append("type", type);
		jsonMessage.append("content", content);
		return jsonMessage;
	}
	
	private static JSONObject JSONMessageBuilder(String sender, String type,
			String content,String tableKey) throws JSONException {
		JSONObject jsonMessage = new JSONObject();
		jsonMessage.append("sender", sender);
		jsonMessage.append("type", type);
		jsonMessage.append("content", content);
		jsonMessage.append("tableKey", tableKey);
		return jsonMessage;
	}
}
