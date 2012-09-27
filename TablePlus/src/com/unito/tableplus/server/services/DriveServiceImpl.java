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
import com.unito.tableplus.client.services.DriveService;
import com.unito.tableplus.server.Utils;
import com.unito.tableplus.shared.model.DriveFile;

public class DriveServiceImpl extends RemoteServiceServlet implements
		DriveService {

	private static final long serialVersionUID = 2603943737426137505L;

	@Override
	public String getRequestTokenURL() {
		final String PROVIDER = "?provider=google";
		final String callbackURL = Utils.getCallbackUrl() + PROVIDER;
		return AuthSubUtil.getRequestUrl(callbackURL,
				"https://docs.google.com/feeds/", false, true);
	}

	public static String getDriveSessionToken(String token) {
		String sessionToken = null;
		try {
			sessionToken = AuthSubUtil.exchangeForSessionToken(token, null);
		} catch (AuthenticationException e1) {
			e1.printStackTrace();
		} catch (IOException e1) {
			e1.printStackTrace();
		} catch (GeneralSecurityException e1) {
			e1.printStackTrace();
		}

		return sessionToken;
	}

	public static List<DriveFile> getDriveFileList(String sessionToken) {

		List<DriveFile> myDriveFileList = new ArrayList<DriveFile>();

		DocsService client = new DocsService("TablePlus");
		client.setAuthSubToken(sessionToken);
		client.setProtocolVersion(DocsService.Versions.V2);
		client.setConnectTimeout(0);

		URL feedUri;
		try {
			feedUri = new URL(
					"https://docs.google.com/feeds/documents/private/full/");
			DocumentListFeed feed = client.getFeed(feedUri,
					DocumentListFeed.class);

			DriveFile d;

			for (DocumentListEntry entry : feed.getEntries()) {
				d = new DriveFile();
				d.setTitle(entry.getTitle().getPlainText());
				d.setDocId(entry.getDocId());
				d.setLink(entry.getDocumentLink().getHref());
				myDriveFileList.add(d);
			}

		} catch (MalformedURLException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		} catch (ServiceException e) {
			e.printStackTrace();
		}

		return myDriveFileList;
	}
}
