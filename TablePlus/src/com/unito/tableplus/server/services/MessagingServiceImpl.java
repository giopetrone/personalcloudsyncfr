package com.unito.tableplus.server.services;

import java.io.IOException;
import java.util.ArrayList;
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
import com.unito.tableplus.server.persistence.InvitationQueries;
import com.unito.tableplus.server.persistence.TableQueries;
import com.unito.tableplus.server.persistence.UserQueries;
import com.unito.tableplus.server.util.ServiceFactory;
import com.unito.tableplus.server.util.Utility;
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
	 * The following map contains the connected users lists, 
	 * where the key is the table key and the ArrayList is the list
	 * of connected users, meant as users using the Channel Service.
	 */
	private final static Map<Long, ArrayList<Long>> connectedUsers = new HashMap<Long, ArrayList<Long>>();

	@Override
	public String createChannel(String userId) {
		String token = null;
		try {
			token = channelService.createChannel(userId);
		} catch (ChannelFailureException channelFailureException) {
			System.err.println("Error creating the channel: "
					+ channelFailureException);
		} catch (Exception otherException) {
			System.err.println("Unknown exception while creating channel: "
					+ otherException);
		}
		return token;
	}

	@Override
	public String sendMessage(Long senderId, String content,
			ChannelMessageType type, Long table) {
		try {
			JSONObject jsonMessage = new JSONObject();
			jsonMessage.put("senderId", senderId);
			jsonMessage.put("type", type.toString());
			jsonMessage.put("content", content);
			jsonMessage.put("tableKey", table);
			String message = jsonMessage.toString();

			ArrayList<Long> recipients = connectedUsers.get(table);
			if (recipients != null) {
				for (Long r : recipients)
					channelService.sendMessage(new ChannelMessage(r.toString(),
							message));
			}
			//TODO avoid this if
			if (type.equals(ChannelMessageType.NEWTABLEMEMBER))
				channelService
						.sendMessage(new ChannelMessage(content, message));

			return "Message sent: " + jsonMessage;
		} catch (JSONException e) {
			return "Error creating JSON on server: " + e;
		}
	}

	/**
	 * Adds a user to the list of connected users
	 * 
	 * @param user
	 *            The user id to add to the list.
	 */
	public static void userConnection(Long userKey) {
		User u = UserQueries.queryUser(userKey);
		List<Table> tables = TableQueries.queryTables(u.getTables());
		try {
			JSONObject jsonMessage = new JSONObject();
			jsonMessage.put("senderId", userKey);
			jsonMessage
					.put("type", ChannelMessageType.NEWCONNECTION.toString());
			jsonMessage.put("content", "");

			for (Table t : tables) {
				List<Long> recipients = connectedUsers.get(t.getKey());
				if (recipients != null) { 
					jsonMessage.put("tableKey", t.getKey());
					for (Long r : recipients)
						channelService.sendMessage(new ChannelMessage(r
								.toString(), jsonMessage.toString()));
					recipients.add(userKey); 
				} else {
					ArrayList<Long> list = new ArrayList<Long>();
					list.add(userKey);
					connectedUsers.put(t.getKey(), list);
				}
			}
		} catch (JSONException e) {
			System.err.println("Error creating JSON on server: " + e);
		}

	}

	/**
	 * Removes the user passed as parameter from the list of connected users if
	 * it exists. Then sends a message to notify other users.
	 * 
	 * @param user
	 *            The user id to remove.
	 */
	public static void userDisconnection(Long userKey) {
		User u = UserQueries.queryUser(userKey);
		List<Table> tables = TableQueries.queryTables(u.getTables());
		try {
			JSONObject jsonMessage = new JSONObject();
			jsonMessage.put("senderId", userKey);
			jsonMessage
					.put("type", ChannelMessageType.DISCONNECTION.toString());
			jsonMessage.put("content", "");

			for (Table t : tables) {
				ArrayList<Long> recipients = connectedUsers.get(t.getKey());
				if (recipients != null) {
					jsonMessage.put("tableKey", t.getKey());
					recipients.remove(userKey);
					for (Long r : recipients)
						channelService.sendMessage(new ChannelMessage(r
								.toString(), jsonMessage.toString()));
				}
			}
			
		} catch (JSONException e) {
			System.err.println("Error creating JSON on server: " + e);
		}
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
			String invitationUrl = Utility.getInvitationServletUrl() + "?code="
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
