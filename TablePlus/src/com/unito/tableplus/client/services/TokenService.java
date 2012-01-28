package com.unito.tableplus.client.services;

import java.util.List;

import com.google.gwt.user.client.rpc.RemoteService;
import com.google.gwt.user.client.rpc.RemoteServiceRelativePath;
import com.unito.tableplus.shared.Document;

@RemoteServiceRelativePath("token-service")
public interface TokenService extends RemoteService{
	
	public String getRequestTokenURL(String backURL);
	
	public void manualToken(String SessionToken);
	
	public List<Document> getDocumentList(String gdocSessionToken);
	
	public String getGdocSessionToken(String gdocToken);

}
