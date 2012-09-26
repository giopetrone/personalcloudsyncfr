package aa.vv.cc.server;

import java.util.HashMap;

import aa.vv.cc.client.GreetingService;
import aa.vv.cc.client.Info;
import aa.vv.cc.client.Util;
import aa.vv.cc.server.ps.Test;
import aa.vv.cc.shared.FieldVerifier;

import com.google.gwt.user.server.rpc.RemoteServiceServlet;

/**
 * The server side implementation of the RPC service.
 */
@SuppressWarnings("serial")
public class GreetingServiceImpl extends RemoteServiceServlet implements
		GreetingService {

	public String storeAtom(HashMap<String, String> args)
			throws IllegalArgumentException {
		String ret = "success";
		try {
			try {

				Info.createInfo(args);
			} catch (Exception e) {
				String msg = Util.getErrorMessage(e);
				ret = msg;
			}
		} catch (Exception exx) {
		}

		return ret;
	}

	public String feedAsString(String feedName) throws IllegalArgumentException {
		String ret = new Util().fromDataStoreToFeed(feedName).toString();
		return escapeHtml(ret);
	}

	public String subscribe(String feedName) throws IllegalArgumentException {
		String result = new Test().testSubscriber(
				"http://www.piemonte.di.unito.it/Atomi/marinofeed.xml",
				"http://aioeoio:8080/PubSubLib/callback", "");
		return escapeHtml(result);
	}
	
	public String publish(String feedName) throws IllegalArgumentException {
		String result = "failure";
		try {
			result = new Test().testPublisher(
					"http://pubsubhubbub.appspot.com", "http://sgnmrnsubgwt.appspot.com/feed");
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		return escapeHtml(result);
	}
	
	public String greetServer(String input) throws IllegalArgumentException {
		// Verify that the input is valid.
		if (!FieldVerifier.isValidName(input)) {
			// If the input is not valid, throw an IllegalArgumentException back
			// to
			// the client.
			throw new IllegalArgumentException(
					"Name must be at least 4 characters long");
		}

		String serverInfo = getServletContext().getServerInfo();
		String userAgent = getThreadLocalRequest().getHeader("User-Agent");

		// Escape data from the client to avoid cross-site script
		// vulnerabilities.
		input = escapeHtml(input);
		userAgent = escapeHtml(userAgent);

		return "Hello, " + input + "!<br><br>I am running " + serverInfo
				+ ".<br><br>It looks like you are using:<br>" + userAgent;
	}

	/**
	 * Escape an html string. Escaping data received from the client helps to
	 * prevent cross-site script vulnerabilities.
	 * 
	 * @param html
	 *            the html string to escape
	 * @return the escaped string
	 */
	private String escapeHtml(String html) {
		if (html == null) {
			return null;
		}
		return html.replaceAll("&", "&amp;").replaceAll("<", "&lt;")
				.replaceAll(">", "&gt;");
	}
}
