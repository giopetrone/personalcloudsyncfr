package com.unito.tableplus.server.services;

import java.io.IOException;
import java.util.Arrays;
import java.util.LinkedList;
import java.util.List;

import com.google.api.client.auth.oauth2.Credential;
import com.google.api.client.googleapis.auth.oauth2.GoogleAuthorizationCodeFlow;
import com.google.api.client.googleapis.auth.oauth2.GoogleCredential;
import com.google.api.client.googleapis.auth.oauth2.GoogleTokenResponse;
import com.google.api.client.http.HttpTransport;
import com.google.api.client.http.javanet.NetHttpTransport;
import com.google.api.client.json.JsonFactory;
import com.google.api.client.json.jackson2.JacksonFactory;
import com.google.api.services.drive.Drive;
import com.google.api.services.drive.Drive.Files;
import com.google.api.services.drive.DriveScopes;
import com.google.api.services.drive.model.File;
import com.google.api.services.drive.model.FileList;
import com.google.api.services.drive.model.Permission;
import com.google.appengine.api.users.UserService;
import com.google.appengine.api.users.User;
import com.google.gwt.user.server.rpc.RemoteServiceServlet;
import com.unito.tableplus.client.services.DriveService;
import com.unito.tableplus.server.persistence.UserQueries;
import com.unito.tableplus.server.persistence.WalletQueries;
import com.unito.tableplus.server.util.ServiceFactory;
import com.unito.tableplus.server.util.Utility;
import com.unito.tableplus.shared.model.DriveFile;
import com.unito.tableplus.shared.model.Wallet;

public class DriveServiceImpl extends RemoteServiceServlet implements
		DriveService {

	private static final long serialVersionUID = 2603943737426137505L;

	private static final String CLIENT_ID = "843761346041.apps.googleusercontent.com";
	private static final String CLIENT_SECRET = "GEO0G7BcohMenRnhJix1pfeA";
	private static final String PROVIDER = "?provider=drive";
	private static final String REDIRECT_URI = Utility.getCallbackUrl()
			+ PROVIDER;
	private static GoogleAuthorizationCodeFlow flow;

	private static final UserService userService = ServiceFactory
			.getUserService();

	@Override
	public String getAuthorizationURL() {
		HttpTransport httpTransport = new NetHttpTransport();
		JsonFactory jsonFactory = new JacksonFactory();

		flow = new GoogleAuthorizationCodeFlow.Builder(httpTransport,
				jsonFactory, CLIENT_ID, CLIENT_SECRET,
				Arrays.asList(DriveScopes.DRIVE)).setAccessType("offline")
				.setApprovalPrompt("force").build();
		String url = flow.newAuthorizationUrl().setRedirectUri(REDIRECT_URI)
				.build();
		return url;
	}

	public static void storeCredentials(String authorizationCode)
			throws IOException {
		User user = userService.getCurrentUser();
		HttpTransport transport = new NetHttpTransport();
		JsonFactory jsonFactory = new JacksonFactory();
		GoogleTokenResponse response = flow.newTokenRequest(authorizationCode)
				.setRedirectUri(REDIRECT_URI).execute();
		Credential credential = new GoogleCredential.Builder()
				.setTransport(transport).setJsonFactory(jsonFactory)
				.setClientSecrets(CLIENT_ID, CLIENT_SECRET).build()
				.setFromTokenResponse(response);
		Long userKey = UserQueries.queryUser("email", user.getEmail()).getKey();
		if (userKey != null && credential != null) {
			Wallet wallet = WalletQueries.getWallet(userKey);
			wallet.setDriveAccessToken(credential.getAccessToken());
			wallet.setDriveRefreshToken(credential.getRefreshToken());
			WalletQueries.storeWallet(wallet);
		}
	}

	public static List<DriveFile> loadFiles(Wallet wallet) throws IOException {
		List<DriveFile> driveFiles = new LinkedList<DriveFile>();
		Drive service = getService(wallet);

		List<File> result = new LinkedList<File>();
		Files.List request = service.files().list();
		do {
			FileList files = request.execute();
			result.addAll(files.getItems());
			request.setPageToken(files.getNextPageToken());
		} while (request.getPageToken() != null
				&& request.getPageToken().length() > 0);
		DriveFile df;
		for (File f : result) {
			df = new DriveFile();
			df.setTitle(f.getTitle());
			df.setLink(f.getSelfLink());
			df.setID(f.getId());
			df.setURI(f.getSelfLink());
			driveFiles.add(df);
		}

		return driveFiles;
	}

	public static Drive getService(Wallet wallet) {
		HttpTransport transport = new NetHttpTransport();
		JsonFactory jsonFactory = new JacksonFactory();

		GoogleCredential credential = new GoogleCredential.Builder()
				.setJsonFactory(jsonFactory).setTransport(transport)
				.setClientSecrets(CLIENT_ID, CLIENT_SECRET).build();
		credential.setAccessToken(wallet.getDriveAccessToken());
		credential.setRefreshToken(wallet.getDriveRefreshToken());
		return new Drive.Builder(transport, jsonFactory, credential).build();
	}

	/**
	 * https://developers.google.com/drive/v2/reference/permissions/insert
	 * Insert a new permission.
	 * 
	 * @param service
	 *            Drive API service instance.
	 * @param fileId
	 *            ID of the file to insert permission for.
	 * @param value
	 *            User or group e-mail address, domain name or {@code null}
	 *            "default" type.
	 * @param type
	 *            The value "user", "group", "domain" or "default".
	 * @param role
	 *            The value "owner", "writer" or "reader".
	 * @return The inserted permission if successful, {@code null} otherwise.
	 */
	private static Permission insertPermission(Drive service, String fileId,
			String value, String type, String role) {
		Permission newPermission = new Permission();

		newPermission.setValue(value);
		newPermission.setType(type);
		newPermission.setRole(role);
		try {
			return service.permissions().insert(fileId, newPermission)
					.execute();
		} catch (IOException e) {
			System.err.println("An error occurred: " + e);
		}
		return null;
	}

	/**
	 * https://developers.google.com/drive/v2/reference/permissions/update
	 * Update a permission's role.
	 * 
	 * @param service
	 *            Drive API service instance.
	 * @param fileId
	 *            ID of the file to update permission for.
	 * @param permissionId
	 *            ID of the permission to update.
	 * @param newRole
	 *            The value "owner", "writer" or "reader".
	 * @return The updated permission if successful, {@code null} otherwise.
	 */
	@SuppressWarnings("unused")
	private static Permission updatePermission(Drive service, String fileId,
			String permissionId, String newRole) {
		try {
			// First retrieve the permission from the API.
			Permission permission = service.permissions()
					.get(fileId, permissionId).execute();
			permission.setRole(newRole);
			return service.permissions()
					.update(fileId, permissionId, permission).execute();
		} catch (IOException e) {
			System.out.println("An error occurred: " + e);
		}
		return null;
	}

	/**
	 * Share a file with a list of users.
	 * 
	 * @param file
	 *            The id of the file to share
	 * @param wallet
	 *            File owner's wallet
	 * @param users
	 *            Users' email addresses list to share the file with
	 */
	public static void shareFile(String fileId, Wallet wallet,
			List<String> users) {
		Drive service = getService(wallet);
		for (String u : users)
			insertPermission(service, fileId, u, "user", "owner");
	}

	/**
	 * Share a file with a user.
	 * 
	 * @param fileID
	 *            The id of the file to share
	 * @param wallet
	 *            File owner's wallet
	 * @param user
	 *            User's email address to share the file with
	 */
	public static void shareFile(String fileID, Wallet wallet, String user) {
		Drive service = getService(wallet);
		insertPermission(service, fileID, user, "user", "owner");
	}

	/**
	 * Share a list of file with a user.
	 * 
	 * @param fileIDs
	 *            The ids of the files to share
	 * @param wallet
	 *            File owner's wallet
	 * @param user
	 *            User's email address to share the file with
	 */
	public static void shareFiles(List<String> fileIDs, Wallet wallet,
			String user) {
		Drive service = getService(wallet);
		for (String fileID : fileIDs)
			insertPermission(service, fileID, user, "user", "owner");
	}

}
