package com.unito.tableplus.shared.model;

/**
 * <p>
 * This class defines the possible message types that can be sent/received on
 * the communication channel:
 * </p>
 * <ul>
 * <li><b>CHAT: </b> It is a chat message.</li>
 * <li><b>NEWCONNECTION: </b> Sent when a user who has just connected to the
 * system.</li>
 * <li><b>DISCONNECTION: </b> Sent when a user disconnects from system.</li>
 * <li><b>USERONLINE: </b> Sent by a user who goes on the same table of the
 * current user.</li>
 * <li><b>USERBUSY: </b> Sent by a user who puts his status on BUSY.</li>
 * <li><b>USERAWAY: </b> Sent by a user when he leaves the table where the
 * current user is connected to.</li>
 * <li><b>NEWTABLERESOURCE: </b> Sent when a new resource is added to a table.</li>
 * <li><b>NEWTABLEMEMBER: </b> Sent when a new user is added to a table.</li>
 * </ul>
 * <p>
 * <b>NOTE: </b> NEWCONNECTION and DISCONNECTION messages are automatically sent
 * on connection/disconnection action detected by omonimous servlets:
 * @see ConnectionServlet.java
 * @see DisconnectionServlet.java
 * @see MessagingServiceImpl.java
 * </p>
 */
public enum ChannelMessageType {
	CHAT, NEWCONNECTION, DISCONNECTION, USERONLINE, USERBUSY, USERAWAY, NEWTABLERESOURCE, NEWTABLEMEMBER
}
