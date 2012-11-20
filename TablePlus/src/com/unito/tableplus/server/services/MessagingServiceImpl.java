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
import com.unito.tableplus.server.persistence.InvitationQueries;
import com.unito.tableplus.server.persistence.TableQueries;
import com.unito.tableplus.server.persistence.UserQueries;
import com.unito.tableplus.server.util.ServiceFactory;
import com.unito.tableplus.server.util.Utility;
import com.unito.tableplus.shared.model.ChannelMessageType;
import com.unito.tableplus.shared.model.Invitation;
import com.unito.tableplus.shared.model.Table;
import com.unito.tableplus.shared.model.User;
import com.unito.tableplus.shared.model.UserStatus;

public class MessagingServiceImpl extends RemoteServiceServlet implements
		MessagingService {
	private static final long serialVersionUID = 1093735951433111434L;
	private static final ChannelService channelService = ServiceFactory
			.getChannelService();

	private final static Map<Long, HashMap<Long, UserStatus>> usersStatus = new HashMap<Long, HashMap<Long, UserStatus>>();

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
		updateUserStatus(table, senderId, type);
		try {
			JSONObject jsonMessage = new JSONObject();
			jsonMessage.append("id", senderId);
			jsonMessage.append("type", type.toString());
			jsonMessage.append("content", content);
			jsonMessage.append("tableKey", table);

			Map<Long, UserStatus> recipients = usersStatus.get(table);
			if (recipients != null) { // if there are online users
				for (Long r : recipients.keySet())
					// send a message to each user
					channelService.sendMessage(new ChannelMessage(r.toString(),
							jsonMessage.toString()));
			}
			return "Message sent: " + jsonMessage;
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
	public static void userConnection(Long userKey) {
		User u = UserQueries.queryUser(userKey);
		List<Table> tables = TableQueries.queryTables(u.getTables());
		try {
			JSONObject jsonMessage = new JSONObject();
			jsonMessage.append("id", userKey);
			jsonMessage.append("type",
					ChannelMessageType.NEWCONNECTION.toString());
			jsonMessage.append("content", "");

			for (Table t : tables) { // for each user table
				Map<Long, UserStatus> recipients = usersStatus.get(t.getKey());
				if (recipients != null) { // if there are online users
					jsonMessage.append("tableKey", t.getKey());
					for (Long r : recipients.keySet())
						// send a message to each user
						channelService.sendMessage(new ChannelMessage(r
								.toString(), jsonMessage.toString()));
					recipients.put(userKey, UserStatus.AWAY); // add current
																// user to table
				} else { // if there are no users online
					HashMap<Long, UserStatus> hm = new HashMap<Long, UserStatus>();
					hm.put(userKey, UserStatus.AWAY);
					usersStatus.put(t.getKey(), hm);
				}
			}
			printMap();
		} catch (JSONException e) {
			System.err.println("Error creating JSON: " + e);
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
			jsonMessage.append("id", userKey);
			jsonMessage.append("type",
					ChannelMessageType.DISCONNECTION.toString());
			jsonMessage.append("content", "");

			for (Table t : tables) { // for each user table
				Map<Long, UserStatus> recipients = usersStatus.get(t.getKey());
				if (recipients != null) { // if there are online users
					jsonMessage.append("tableKey", t.getKey());
					recipients.remove(userKey);
					for (Long r : recipients.keySet())
						// send a message to each user
						channelService.sendMessage(new ChannelMessage(r
								.toString(), jsonMessage.toString()));
				}
			}
			printMap();
		} catch (JSONException e) {
			System.err.println("Error creating JSON: " + e);
		}
	}

	public static Map<Long, UserStatus> getTableStatus(Long tableKey) {
		return usersStatus.get(tableKey);
	}

	private static void updateUserStatus(Long tableKey, Long userKey,
			ChannelMessageType messageType) {
		UserStatus status = null;
		if (messageType.equals(ChannelMessageType.USERAWAY))
			status = UserStatus.AWAY;
		else if (messageType.equals(ChannelMessageType.USERONLINE))
			status = UserStatus.ONLINE;
		else if (messageType.equals(ChannelMessageType.USERBUSY))
			status = UserStatus.BUSY;
		if (status != null) {
			Map<Long, UserStatus> tableStatus = usersStatus.get(tableKey);
			if (tableStatus != null)
				tableStatus.put(userKey, status);
			else {
				HashMap<Long, UserStatus> s = new HashMap<Long, UserStatus>();
				s.put(userKey, status);
				usersStatus.put(tableKey, s);
			}
			printMap();
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

	public static void printMap() {
		for (Long t : usersStatus.keySet()) {
			System.out.println("Tavolo " + t);
			Map<Long, UserStatus> m = usersStatus.get(t);
			for (Long s : m.keySet())
				System.out.println("\t User " + s + " is " + m.get(s));
		}
	}
}
