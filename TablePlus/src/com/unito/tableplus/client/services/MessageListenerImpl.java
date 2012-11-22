package com.unito.tableplus.client.services;

import com.extjs.gxt.ui.client.widget.Info;
import com.google.gwt.appengine.channel.client.SocketError;
import com.google.gwt.appengine.channel.client.SocketListener;
import com.google.gwt.core.client.GWT;
import com.google.gwt.json.client.JSONNumber;
import com.google.gwt.json.client.JSONObject;
import com.google.gwt.json.client.JSONParser;
import com.google.gwt.json.client.JSONString;
import com.google.gwt.json.client.JSONValue;
import com.unito.tableplus.client.TablePlus;
import com.unito.tableplus.shared.model.ChannelMessageType;
import com.unito.tableplus.shared.model.Table;
import com.unito.tableplus.shared.model.UserStatus;

public class MessageListenerImpl implements SocketListener {

	@Override
	public void onOpen() {
		GWT.log("Channel opened for " + TablePlus.getUser().getUsername());
	}

	@Override
	public void onMessage(String message) {
		JSONValue jv = JSONParser.parseStrict(message);
		JSONObject jo = jv.isObject();
		GWT.log("Message received: " + message);
		ChannelMessageType type = ChannelMessageType.valueOf(getJsonValue(jo,
				"type"));
		Long senderId = Long.valueOf(getJsonValue(jo, "senderId"));
		String content = getJsonValue(jo, "content");
		Long tableKey = Long.valueOf(getJsonValue(jo, "tableKey"));
		
		Table t = TablePlus.getDesktop().getTables().get(tableKey);
		String senderName = t.getUsersMap().get(senderId).getUsername();

		if (type.equals(ChannelMessageType.CHAT)) {
			
			if (senderId.equals(TablePlus.getUser().getKey()))
				t.appendChat("<font color=\"blue\"><b>" + senderName
						+ ":</b> " + content + "</font><br />");
			else
				t.appendChat("<b>" + senderName + ":</b> " + content
						+ "<br />");
			TablePlus.getDesktop().setChatStatus(t.getKey(), true);
			if (t.isActive())
				TablePlus.getDesktop().getChatWindow().updateContent();
		}

		else if (type.equals(ChannelMessageType.NEWCONNECTION)) {
			t.setUserStatus(senderId, UserStatus.AWAY);
			if (t.isActive()) {
				TablePlus.getDesktop().getRightPanel().getMembersPanel()
						.updateUserStatus(senderId, UserStatus.AWAY);
				Info.display("New connection", "User " + senderName
						+ " just connected.");
			}
		}

		else if (type.equals(ChannelMessageType.DISCONNECTION)) {
			t.setUserStatus(senderId, UserStatus.OFFLINE);
			if (t.isActive()) {
				TablePlus.getDesktop().getRightPanel().getMembersPanel()
						.updateUserStatus(senderId, UserStatus.OFFLINE);
				Info.display("Disconnection", "User " + senderName
						+ " disconnected.");
			}
		}

		else if (type.equals(ChannelMessageType.USERONLINE)) {
			t.setUserStatus(senderId, UserStatus.ONLINE);
			if (t.isActive()) {
				TablePlus.getDesktop().getRightPanel().getMembersPanel()
						.updateUserStatus(senderId, UserStatus.ONLINE);
				Info.display("User status", "User " + senderName
						+ " is now online");
			}
		}

		else if (type.equals(ChannelMessageType.USERAWAY)) {
			t.setUserStatus(senderId, UserStatus.AWAY);
			if (t.isActive()) {
				TablePlus.getDesktop().getRightPanel().getMembersPanel()
						.updateUserStatus(senderId, UserStatus.AWAY);
				Info.display("User status", "User " + senderName
						+ " went away.");
			}
		}

		else if (type.equals(ChannelMessageType.NEWTABLEMEMBER)) {
			Long userKey = Long.parseLong(content);
			if (userKey.equals(TablePlus.getUser().getKey()))
				TablePlus.updateUser();
			else {
				TablePlus.loadTableMembers(t);
			}

		} else
			GWT.log("Message type unknown!");
	}

	@Override
	public void onError(SocketError error) {
		GWT.log("Error in message listener: " + error.getDescription());
	}

	@Override
	public void onClose() {
		// TODO: manage the channel expiration after 2hs it has been opened.
		GWT.log("Channel closed!");
	}

	private String getJsonValue(JSONObject jo, String key) {
		JSONValue jvContent = null;
		JSONString jsContent = null;
		JSONNumber jsNumber = null;
		String value = null;

		jvContent = jo.get(key);
		if (jvContent != null) {
			jsNumber = jvContent.isNumber();
			if(jsNumber != null)
				value = jsNumber.toString();
			else{
				jsContent = jvContent.isString();
				if(jsContent != null){
					value = jsContent.stringValue();
				}
				else
					GWT.log("Failed to parse key " + key +":"+ jsContent);
			}
		} else
			GWT.log("I could not get key " + key + " from jvContent");

		return value;
	}

}
