package com.unito.tableplus.client.services;

import java.util.List;

import com.google.gwt.user.client.rpc.AsyncCallback;
import com.unito.tableplus.shared.model.Document;

public interface TokenServiceAsync {

	void getRequestTokenURL(String backURL, AsyncCallback<String> callback);

	void getDocumentList(String gdocSessionToken, AsyncCallback<List<Document>> callback);

	void getGdocSessionToken(String gdocToken, AsyncCallback<String> callback);

	
}
