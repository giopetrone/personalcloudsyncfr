package com.unito.tableplus.client.services;

import com.google.gwt.core.client.GWT;

public class ServiceFactory {
	private static LoginServiceAsync loginServiceInstance;
	private static UserServiceAsync userServiceInstance;
	private static TableServiceAsync tableServiceInstance;
	private static MessagingServiceAsync messagingServiceInstance;
	private static BookmarkServiceAsync bookmarkService;
	
	public static LoginServiceAsync getloginServiceInstance() {
		if (loginServiceInstance == null) {
			loginServiceInstance = GWT.create(LoginService.class);
		}
		return loginServiceInstance;
	}
	
	public static UserServiceAsync getUserServiceInstance() {
		if (userServiceInstance == null) {
			userServiceInstance = GWT.create(UserService.class);
		}
		return userServiceInstance;
	}

	public static TableServiceAsync getTableServiceInstance() {
		if (tableServiceInstance == null) {
			tableServiceInstance = GWT.create(TableService.class);
		}
		return tableServiceInstance;
	}	
	
	public static MessagingServiceAsync getMessagingServiceInstance() {
		if (messagingServiceInstance == null) {
			messagingServiceInstance = GWT.create(MessagingService.class);
		}
		return messagingServiceInstance;
	}
	
	public static BookmarkServiceAsync getBookmarkServiceInstance() {
		if (messagingServiceInstance == null) {
			bookmarkService = GWT.create(BookmarkService.class);
		}
		return bookmarkService;
	}
}
