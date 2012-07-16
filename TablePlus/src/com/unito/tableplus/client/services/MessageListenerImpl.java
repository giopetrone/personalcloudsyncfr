package com.unito.tableplus.client.services;

import com.extjs.gxt.ui.client.widget.Info;
import com.google.gwt.appengine.channel.client.SocketError;
import com.google.gwt.appengine.channel.client.SocketListener;
import com.google.gwt.core.client.GWT;
import com.google.gwt.json.client.JSONArray;
import com.google.gwt.json.client.JSONNumber;
import com.google.gwt.json.client.JSONObject;
import com.google.gwt.json.client.JSONParser;
import com.google.gwt.json.client.JSONString;
import com.google.gwt.json.client.JSONValue;
import com.google.gwt.user.client.rpc.AsyncCallback;
import com.unito.tableplus.client.TablePlus;
import com.unito.tableplus.client.gui.TableUI;
import com.unito.tableplus.client.gui.windows.ChatWindow;
import com.unito.tableplus.shared.model.ChannelMessageType;
import com.unito.tableplus.shared.model.Table;

public class MessageListenerImpl implements SocketListener {

	public static final UserServiceAsync userService = ServiceFactory
			.getUserServiceInstance();

	private final static TableServiceAsync tableService = ServiceFactory
			.getTableServiceInstance();

	private final MessagingServiceAsync messagingService = ServiceFactory
			.getChatServiceInstance();


	@Override
	public void onOpen() {
		GWT.log("Channel opened for " +  TablePlus.getUser().getUsername());
	}

	@Override
	public void onMessage(String message) {
		JSONValue jv = JSONParser.parseStrict(message);
		JSONObject jo = jv.isObject();
		GWT.log("Message received: " + message);
		ChannelMessageType type = ChannelMessageType.valueOf(getJsonValue(jo,
				"type"));
		Long senderId = Long.valueOf(getJsonValue(jo, "id"));
		String senderEmail = getJsonValue(jo, "email");
		String content = getJsonValue(jo, "content");
		Long tableKey = Long.valueOf(getJsonValue(jo, "tableKey"));
		GWT.log(message);

		if (type.equals(ChannelMessageType.CHAT)) {
			for (TableUI t : TablePlus.getDesktop().getTables())
				if (t.getTableKey().equals(tableKey)) {
					((ChatWindow) t.getTableChatWindow()).manageNewMessage(
							senderEmail, content);
				}
		}

		else if (type.equals(ChannelMessageType.NEWCONNECTION)) {
			Info.display("New connection", "User " + senderEmail
					+ " just connected.");
			for (TableUI t : TablePlus.getDesktop().getTables())
				if (t.getTableKey().equals(tableKey))
					t.getRightPanel().getMembersPanel()
							.refreshMembersTree(type, senderId, senderEmail);
		}

		else if (type.equals(ChannelMessageType.DISCONNECTION)) {
			Info.display("New connection", "User " + senderEmail
					+ " disconnected.");
			for (TableUI t : TablePlus.getDesktop().getTables())
				if (t.getTableKey().equals(tableKey))
					t.getRightPanel().getMembersPanel()
							.refreshMembersTree(type, senderId, senderEmail);
		}

		else if (type.equals(ChannelMessageType.USERONLINE)) {
			for (TableUI t : TablePlus.getDesktop().getTables())
				if (t.getTableKey().equals(tableKey))
					t.getRightPanel().getMembersPanel()
							.refreshMembersTree(type, senderId, senderEmail);
		}

		else if (type.equals(ChannelMessageType.USERAWAY)) {
			for (TableUI t : TablePlus.getDesktop().getTables())
				if (t.getTableKey().equals(tableKey))
					t.getRightPanel().getMembersPanel()
							.refreshMembersTree(type, senderId, senderEmail);
		}

		else if (type.equals(ChannelMessageType.USERSTATUS)) {
			for (TableUI t : TablePlus.getDesktop().getTables())
				if (t.getTableKey().equals(tableKey))
					t.getRightPanel()
							.getMembersPanel()
							.refreshMembersTree(ChannelMessageType.USERONLINE,
									senderId, senderEmail);
				else
					t.getRightPanel()
							.getMembersPanel()
							.refreshMembersTree(ChannelMessageType.USERAWAY,
									senderId, senderEmail);
		}

		else if (type.equals(ChannelMessageType.NEWTABLEMEMBER)) {

			if (content.equals(TablePlus.getUser().getEmail())) {
				Info.display("New shared table",
						"You have been invited to share a new table from "
								+ senderEmail);
				// updates user querying database
				TablePlus.updateUser();

				// loads invitation table
				tableService.queryTable(tableKey, new AsyncCallback<Table>() {
					@Override
					public void onFailure(Throwable caught) {
						GWT.log("Failure querying table in MessageListener",
								caught);
					}

					@Override
					public void onSuccess(Table result) {
						// crea un table sulla base del tavolo
						TableUI table = new TableUI(result);

						// aggiungi il nuovo table al desktop
						TablePlus.getDesktop().addTable(table);

						// aggiorna l'elenco gruppi in personalpanel
						TablePlus.getPersonalTable().getRightPanel()
								.getMyTablesPanel().addNewTableToTree(table);
						// send my status to the table members
						messagingService.sendMessage(TablePlus.getUser()
								.getKey(), "", ChannelMessageType.USERSTATUS,
								result.getKey(), TablePlus.getDesktop()
										.getActiveTableKey(),
								new AsyncCallback<String>() {
									@Override
									public void onFailure(Throwable caught) {
										GWT.log("Failed to send status", caught);
									}

									@Override
									public void onSuccess(String result) {
									}

								});
					}
				});
			} else {
				for (TableUI t : TablePlus.getDesktop().getTables())
					if (t.getTableKey().equals(tableKey)) {
						t.getRightPanel().getMembersPanel()
								.refreshMembersTree(type, senderId, content);
					}

			}
		}
	}

	@Override
	public void onError(SocketError error) {
		GWT.log("Error: " + error.getDescription());
	}

	@Override
	public void onClose() {
		GWT.log("Channel closed!");
	}

	private String getJsonValue(JSONObject jo, String key) {
		JSONValue jvContent = null;
		JSONArray jaContent = null;
		JSONString jsContent = null;
		JSONNumber jsNumber = null;
		String value = null;

		jvContent = jo.get(key);
		if (jvContent != null)
			jaContent = jvContent.isArray();
		else
			GWT.log("I could not parse jvcontent: " + jvContent);
		if (jaContent != null)
			jvContent = jaContent.get(0);
		else
			GWT.log("I could not parse jacontent: " + jaContent);
		if (jvContent != null)
			jsContent = jvContent.isString();
		else
			GWT.log("I could not parse jvcontent: " + jvContent);

		if (jsContent != null)
			value = jsContent.stringValue();
		else {
			jsNumber = jvContent.isNumber();
			if (jsNumber != null){
				value = jsNumber.toString();
			}
			else
				GWT.log("I could not parse jsnumber or jscontent: " + jsNumber);
		}
		return value;
	}

}
