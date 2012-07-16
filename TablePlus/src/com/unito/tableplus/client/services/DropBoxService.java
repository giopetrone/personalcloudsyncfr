package com.unito.tableplus.client.services;


import com.google.gwt.user.client.rpc.RemoteService;
import com.google.gwt.user.client.rpc.RemoteServiceRelativePath;

@RemoteServiceRelativePath("dropbox-service")
public interface DropBoxService extends RemoteService{

	String getAuthUrl();
}
