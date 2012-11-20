package com.unito.tableplus.shared.model;

/**
 * <p>
 * This class defines the possible statuses a remote user can be:
 * </p>
 * <ul>
 * <li><b>OFFLINE</b> Whether a user is not connected to the system.</li>
 * <li><b>ONLINE</b> Whether a user is connected to the system and the active
 * table is the same of the current user.</li>
 * <li><b>BUSY</b> Weather a user is connected and has set his status on busy.
 * This status will be the same on every table the user belongs.</li>
 * <li><b>OFFLINE</b> Weather a user is connected and his active table is not
 * the same of the current user.</li>
 * </ul>
 * 
 */
public enum UserStatus {
	ONLINE, OFFLINE, BUSY, AWAY
}
