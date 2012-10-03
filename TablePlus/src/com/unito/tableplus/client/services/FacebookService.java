package com.unito.tableplus.client.services;

import com.google.gwt.user.client.rpc.RemoteService;
import com.google.gwt.user.client.rpc.RemoteServiceRelativePath;

@RemoteServiceRelativePath("facebook-service")
public interface FacebookService extends RemoteService{
	String getAuthUrl();
}
