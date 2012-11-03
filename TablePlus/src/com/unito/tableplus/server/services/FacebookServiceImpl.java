package com.unito.tableplus.server.services;

import java.util.LinkedList;
import java.util.List;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import org.scribe.builder.ServiceBuilder;
import org.scribe.builder.api.FacebookApi;
import org.scribe.model.OAuthRequest;
import org.scribe.model.Response;
import org.scribe.model.Token;
import org.scribe.model.Verb;
import org.scribe.model.Verifier;
import org.scribe.oauth.OAuthService;

import com.google.appengine.api.users.User;
import com.google.appengine.api.users.UserService;
import com.google.gwt.user.server.rpc.RemoteServiceServlet;
import com.unito.tableplus.client.services.FacebookService;
import com.unito.tableplus.server.ServiceFactory;
import com.unito.tableplus.server.UserQueries;
import com.unito.tableplus.server.Utils;
import com.unito.tableplus.server.WalletQueries;
import com.unito.tableplus.shared.model.FacebookEvent;
import com.unito.tableplus.shared.model.Wallet;

public class FacebookServiceImpl extends RemoteServiceServlet implements
		FacebookService {
	private static final long serialVersionUID = 5057264127495296775L;
	private static final String API_KEY = "393363224057968";
	private static final String APP_SECRET = "bd8c16171ad6acb2f18322bd732b7843";
	private static final String PROVIDER = "?provider=facebook";
	private static final Token EMPTY_TOKEN = null;
	
	private static final UserService userService = ServiceFactory
			.getUserService();

	private static final OAuthService service = new ServiceBuilder()
			.provider(FacebookApi.class)
			.apiKey(API_KEY)
			.apiSecret(APP_SECRET)
			.scope("user_events")
			.callback(Utils.getCallbackUrl() + PROVIDER)
			.build();

	@Override
	public String getAuthUrl() {
		return service.getAuthorizationUrl(EMPTY_TOKEN);
	}

	public static String getApikey() {
		return API_KEY;
	}

	public static String getAppsecret() {
		return APP_SECRET;
	}

	public static void storeAccessToken(String code) {
		Verifier verifier = new Verifier(code);
		Token accessToken = service.getAccessToken(EMPTY_TOKEN, verifier);
		User user = userService.getCurrentUser();
		Long userKey = UserQueries.queryUser("email", user.getEmail())
				.getKey();
		Wallet wallet = WalletQueries.getWallet(userKey);
		wallet.setFacebookToken(accessToken.getToken());
		WalletQueries.storeWallet(wallet);
	}

	protected static List<FacebookEvent> loadEvents(Wallet wallet) {
		String requestLink = "https://graph.facebook.com/me/events?access_token="
				+ wallet.getFacebookToken();
		OAuthRequest request = new OAuthRequest(Verb.GET, requestLink);
		Response response = request.send();
		try {
			JSONObject facebookBody = new JSONObject(response.getBody());
			// TODO: parse paging
			JSONArray facebookEvents = facebookBody.getJSONArray("data");
			int size = facebookEvents.length();
			FacebookEvent event;
			JSONObject jEvent;
			List<FacebookEvent> facebookEventList = new LinkedList<FacebookEvent>();
			for (int i = 0; i < size; i++) {
				jEvent = facebookEvents.getJSONObject(i);
				event = new FacebookEvent();
				event.setName(jEvent.getString("name"));
				event.setId(jEvent.getString("id"));
				String uri = "https://www.facebook.com/events/"+event.getID()+"/";
				event.setURI(uri);
				facebookEventList.add(event);
			}
			return facebookEventList;
		} catch (JSONException e) {
			System.err.println("Error parsing Facebook json response: " + e);
			e.printStackTrace();
		}
		return null;
	}

}
