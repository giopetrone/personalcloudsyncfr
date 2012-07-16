package com.unito.tableplus.client.services;

import com.google.gwt.core.client.GWT;

public class ServiceFactory {
	private static UserServiceAsync userServiceInstance;
	private static TableServiceAsync tableServiceInstance;
	private static MessagingServiceAsync chatServiceInstance;
	private static LoginServiceAsync loginServiceInstance;

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
	
	public static MessagingServiceAsync getChatServiceInstance() {
		if (chatServiceInstance == null) {
			chatServiceInstance = GWT.create(MessagingService.class);
		}
		return chatServiceInstance;
	}
	
	public static LoginServiceAsync getloginServiceInstance() {
		if (loginServiceInstance == null) {
			loginServiceInstance = GWT.create(LoginService.class);
		}
		return loginServiceInstance;
	}
}
