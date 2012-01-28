package com.unito.tableplus.client.services;

import com.google.gwt.user.client.rpc.RemoteService;
import com.google.gwt.user.client.rpc.RemoteServiceRelativePath;
import com.unito.tableplus.shared.Utente;


@RemoteServiceRelativePath("login-service")
public interface LoginService extends RemoteService {
	public Utente isLogged(String requestUri);
}