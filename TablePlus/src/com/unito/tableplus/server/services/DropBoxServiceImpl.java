package com.unito.tableplus.server.services;

import java.util.LinkedList;
import java.util.List;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import org.scribe.builder.ServiceBuilder;
import org.scribe.builder.api.DropBoxApi;
import org.scribe.model.OAuthRequest;
import org.scribe.model.Response;
import org.scribe.model.Token;
import org.scribe.model.Verb;
import org.scribe.model.Verifier;
import org.scribe.oauth.OAuthService;

import com.google.appengine.api.memcache.MemcacheService;
import com.google.appengine.api.users.User;
import com.google.appengine.api.users.UserService;
import com.google.gwt.user.server.rpc.RemoteServiceServlet;
import com.unito.tableplus.client.services.DropBoxService;
import com.unito.tableplus.server.ServiceFactory;
import com.unito.tableplus.server.Utils;
import com.unito.tableplus.shared.model.DropBoxFile;

public class DropBoxServiceImpl extends RemoteServiceServlet implements
		DropBoxService {

	private static final long serialVersionUID = -8545654983535244092L;

	private static final String APP_KEY = "3chhborglx41kdv";
	private static final String APP_SECRET = "d52qtybp0f8n8nv";
	private static final String CALLBACK_PARAMETER = "&oauth_callback=";
	private static final String PROVIDER = "?provider=dropbox";

	private static final OAuthService service = new ServiceBuilder().provider(DropBoxApi.class)
			.apiKey(APP_KEY).apiSecret(APP_SECRET).build();

	private static final MemcacheService syncCache = ServiceFactory.getSyncCache();
	private static final UserService userService = ServiceFactory.getUserService();

	@Override
	public String getAuthUrl() {
		User user = userService.getCurrentUser();
		Token requestToken = service.getRequestToken();
		syncCache.put(user.getEmail(), requestToken);
		return service.getAuthorizationUrl(requestToken) + CALLBACK_PARAMETER
				+ Utils.getCallbackUrl() + PROVIDER;
	}

	public static Token getAccessToken(String oauthToken, Token requestToken) {
		Verifier verifier = new Verifier(oauthToken);
		Token accessToken = service.getAccessToken(requestToken, verifier); 
		return accessToken; 
	}


	
	
	protected static List<DropBoxFile> loadFiles(String token, String secret){
		String requestLink = "https://api.dropbox.com/1/metadata/sandbox/";
		OAuthRequest request = new OAuthRequest(Verb.GET, requestLink);
		Token accessToken = new Token(token,secret);
		service.signRequest(accessToken, request);
		Response response = request.send();
		try {
			JSONObject dropbox = new JSONObject(response.getBody());
			return mapJSON(dropbox);
		} catch (JSONException e) {
			System.err.println("Error parsing dropbox json response: " + e);
			e.printStackTrace();
		} 
		return null;
	}
	
	/**
	 * Maps json Dropbox metadata to DropboxFile model as list of DropboxFiles objects
	 * @return The mapped object list
	 */
	private static List<DropBoxFile> mapJSON(JSONObject dropbox) throws JSONException{
		if(dropbox.getBoolean("is_dir")){
			JSONArray ja = dropbox.getJSONArray("contents");
			List<DropBoxFile> filesList = new LinkedList<DropBoxFile>();
			int size = ja.length();
			for(int i=0; i<size; i++){
				JSONObject jFile = ja.getJSONObject(i);
				DropBoxFile dbFile = new DropBoxFile();
				dbFile.setPath(jFile.getString("path"));
				dbFile.setIcon(jFile.getString("icon"));
				dbFile.setIsDir(jFile.getBoolean("is_dir"));
				filesList.add(dbFile);
			}
			return filesList;
		}
		return null;
	}

}