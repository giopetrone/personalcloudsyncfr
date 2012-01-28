package com.unito.tableplus.client.services;

import com.google.gwt.user.client.rpc.AsyncCallback;
import com.unito.tableplus.shared.Utente;

public interface LoginServiceAsync {
	public void isLogged(String requestUri, AsyncCallback<Utente> callback);
}
