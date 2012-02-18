package com.unito.tableplus.server.services;

import java.io.IOException;
import java.net.MalformedURLException;
import java.net.URL;
import java.security.GeneralSecurityException;
import java.util.ArrayList;
import java.util.List;

import com.google.gdata.client.docs.DocsService;
import com.google.gdata.client.http.AuthSubUtil;
import com.google.gdata.data.docs.DocumentListEntry;
import com.google.gdata.data.docs.DocumentListFeed;
import com.google.gdata.util.AuthenticationException;
import com.google.gdata.util.ServiceException;
import com.google.gwt.user.server.rpc.RemoteServiceServlet;
import com.unito.tableplus.client.services.TokenService;
import com.unito.tableplus.shared.model.Document;

public class TokenServiceImpl extends RemoteServiceServlet implements
		TokenService {

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;

	@Override
	public String getRequestTokenURL(String backURL) {

		return AuthSubUtil.getRequestUrl(backURL,
				"https://docs.google.com/feeds/", false, true);
	}

	@Override
	public List<Document> getDocumentList(String gdocSessionToken) {

		List<Document> myDocumentList = new ArrayList<Document>();

		DocsService client = new DocsService("yourCo-yourAppName-v1");
		client.setAuthSubToken(gdocSessionToken);
		client.setProtocolVersion(DocsService.Versions.V2);

		URL feedUri;
		try {
			feedUri = new URL(
					"https://docs.google.com/feeds/documents/private/full/");
			DocumentListFeed feed = client.getFeed(feedUri,
					DocumentListFeed.class);

			Document d;

			// Scorro i miei documenti
			for (DocumentListEntry entry : feed.getEntries()) {
				d = new Document();
				d.setTitle(entry.getTitle().getPlainText());
				d.setDocId(entry.getDocId());
				d.setLink(entry.getDocumentLink().getHref());
				//System.out.println("L = "+entry.getDocumentLink().getHref());
				myDocumentList.add(d);
				// System.out.println(entry.getTitle().getPlainText());
			}

		} catch (MalformedURLException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (ServiceException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}

		return myDocumentList;
	}

	@Override
	public String getGdocSessionToken(String gdocToken) {
		String gdocSessionToken = null;

		//
		// Promuovo il token a "sessionToken"
		//

		try {
			gdocSessionToken = AuthSubUtil.exchangeForSessionToken(gdocToken,
					null);
		} catch (AuthenticationException e1) {
			// TODO Auto-generated catch block
			e1.printStackTrace();
		} catch (IOException e1) {
			// TODO Auto-generated catch block
			e1.printStackTrace();
		} catch (GeneralSecurityException e1) {
			// TODO Auto-generated catch block
			e1.printStackTrace();
		}

		return gdocSessionToken;
	}

}
