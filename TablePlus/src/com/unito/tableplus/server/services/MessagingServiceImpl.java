package com.unito.tableplus.server.services;

import java.io.IOException;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.UUID;

import org.json.JSONException;
import org.json.JSONObject;

import com.google.appengine.api.channel.ChannelFailureException;
import com.google.appengine.api.channel.ChannelMessage;
import com.google.appengine.api.channel.ChannelService;
import com.google.appengine.api.mail.MailService;
import com.google.appengine.api.mail.MailService.Message;
import com.google.appengine.api.mail.MailServiceFactory;
import com.google.gwt.user.server.rpc.RemoteServiceServlet;
import com.unito.tableplus.client.services.MessagingService;
import com.unito.tableplus.server.InvitationQueries;
import com.unito.tableplus.server.ServiceFactory;
import com.unito.tableplus.server.TableQueries;
import com.unito.tableplus.server.UserQueries;
import com.unito.tableplus.server.Utils;
import com.unito.tableplus.shared.model.ChannelMessageType;
import com.unito.tableplus.shared.model.Invitation;
import com.unito.tableplus.shared.model.Table;
import com.unito.tableplus.shared.model.User;

public class MessagingServiceImpl extends RemoteServiceServlet implements
		MessagingService {
	private static final long serialVersionUID = 1093735951433111434L;
	private static final ChannelService channelService = ServiceFactory
			.getChannelService();

	/**
	 * A map of connected users. Users ids are stored as strings and must be
	 * uniques.
	 */
	private final static Map<Long, String> users = new HashMap<Long, String>();

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
	public String sendMessage(Long senderId, String content,
			ChannelMessageType type, List<Long> recipients) {
		try {
			JSONObject jsonMessage = JSONMessageBuilder(senderId,
					users.get(senderId), type.toString(), content);
			for (Long user : recipients)
				if (users.containsKey(user))
					channelService.sendMessage(new ChannelMessage(user
							.toString(), jsonMessage.toString()));
			return "Message sent";
		} catch (JSONException e) {
			return "Error creating JSON";
		}
	}

	@Override
	public String sendMessage(Long senderId, String content,
			ChannelMessageType type, List<Long> recipients, Long tableKey) {
		try {
			JSONObject jsonMessage = JSONMessageBuilder(senderId,
					users.get(senderId), type.toString(), content,
					tableKey.toString());
			for (Long user : recipients)
				if (users.containsKey(user))
					channelService.sendMessage(new ChannelMessage(user
							.toString(), jsonMessage.toString()));
			return "Message sent";
		} catch (JSONException e) {
			return "Error creating JSON";
		}
	}

	@Override
	public String sendMessage(Long senderId, String content,
			ChannelMessageType type, Long recipient, Long tableKey) {
		try {
			JSONObject jsonMessage = JSONMessageBuilder(senderId,
					users.get(senderId), type.toString(), content,
					tableKey.toString());
			if (users.containsKey(recipient))
				channelService.sendMessage(new ChannelMessage(recipient
						.toString(), jsonMessage.toString()));
			return "Message sent";
		} catch (JSONException e) {
			return "Error creating JSON";
		}
	}

	/**
	 * Adds a user to the list of connected users
	 * 
	 * @param user
	 *            The user id to add to the list.
	 */
	public static void addUser(String userId) {
		Long userKey = Long.parseLong(userId);
		User u = UserQueries.queryUser(userKey);
		users.put(userKey, u.getEmail());
		List<Table> tables = TableQueries.queryTables(u.getTables());
		JSONObject jsonMessage = null;
		for (Table t : tables) {
			try {
				jsonMessage = JSONMessageBuilder(u.getKey(), u.getEmail(),
						ChannelMessageType.NEWCONNECTION.toString(), "", t
								.getKey().toString());
			} catch (JSONException e) {
				System.err.println("Error creating JSON: " + e);
			}
			for (Long user : t.getMembers())
				if (users.containsKey(user))
					channelService.sendMessage(new ChannelMessage(user
							.toString(), jsonMessage.toString()));
		}
	}

	/**
	 * Removes the user passed as parameter from the list of connected users if
	 * it exists. Then sends a message to notify other users.
	 * 
	 * @param user
	 *            The user id to remove.
	 */
	public static void removeUser(String userId) {
		Long userKey = Long.parseLong(userId);
		User u = UserQueries.queryUser(userKey);
		users.remove(u.getKey());
		List<Table> tables = TableQueries.queryTables(u.getTables());
		JSONObject jsonMessage = null;
		for (Table t : tables) {
			try {
				jsonMessage = JSONMessageBuilder(u.getKey(), u.getEmail(),
						ChannelMessageType.DISCONNECTION.toString(), "", t
								.getKey().toString());
			} catch (JSONException e) {
				System.err.println("Error creating JSON: " + e);
			}
			for (Long user : t.getMembers())
				if (users.containsKey(user))
					channelService.sendMessage(new ChannelMessage(user
							.toString(), jsonMessage.toString()));
		}
	}

	/**
	 * Returns the number of users connected to the chat service.
	 * 
	 * @return The integer representing the number of connected users.
	 */
	public int usersCount() {
		return users.size();
	}

	private static JSONObject JSONMessageBuilder(Long senderId,
			String senderEmail, String type, String content)
			throws JSONException {
		JSONObject jsonMessage = new JSONObject();
		jsonMessage.append("id", senderId);
		jsonMessage.append("email", senderEmail);
		jsonMessage.append("type", type);
		jsonMessage.append("content", content);
		return jsonMessage;
	}

	private static JSONObject JSONMessageBuilder(Long senderId,
			String senderEmail, String type, String content, String tableKey)
			throws JSONException {
		JSONObject jsonMessage = new JSONObject();
		jsonMessage.append("id", senderId);
		jsonMessage.append("email", senderEmail);
		jsonMessage.append("type", type);
		jsonMessage.append("content", content);
		jsonMessage.append("tableKey", tableKey);
		return jsonMessage;
	}

	@Override
	public boolean sendInvitationEmail(String sender, String recipient,
			Long tableKey) {

		String code = UUID.randomUUID().toString();

		Long author = UserQueries.queryUser("email", sender).getKey();
		Invitation i = new Invitation();
		i.setAuthor(author);
		i.setCode(code);
		i.setInvitedUser(recipient);
		i.setTableKey(tableKey);

		try {
			MailService mailService = MailServiceFactory.getMailService();
			String invitationUrl = Utils.getInvitationServletUrl() + "?code="
					+ code;
			String body = "You have been invited by "
					+ sender
					+ " to join his/her table in TablePlusPlus environment. \n"
					+ "Don't lose the chance, join now TablePlusPlus and start interacting with "
					+ sender
					+ " and many others, from all over the world! \n"
					+ "Copy-paste in your addresses bar the following address: "
					+ invitationUrl;
			String subject = "TablePlusPlus - New Invitation";

			Message mail = new Message(sender, recipient, subject, body);
			mailService.send(mail);
			InvitationQueries.storeInvitation(i);

		} catch (IOException e) {
			e.printStackTrace();
		}
		return true;
	}
}
