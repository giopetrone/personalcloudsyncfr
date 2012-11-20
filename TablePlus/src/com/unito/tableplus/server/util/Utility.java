package com.unito.tableplus.server.util;

import java.util.Enumeration;

import javax.servlet.http.HttpServletRequest;

import com.google.appengine.api.utils.SystemProperty;
import com.google.appengine.api.utils.SystemProperty.Environment.Value;

public class Utility {
	private static final Value environment = SystemProperty.environment.value();
	private static final Value production =  SystemProperty.Environment.Value.Production;

	/**
	 * Returns the appropriate server callback path according to the environment
	 * value
	 * 
	 * @return The server callback path
	 */
	public static String getHomeUrl() {
		if (environment == production) {
			return "http://" + SystemProperty.applicationVersion.get() + "."
					+ SystemProperty.applicationId.get() + ".appspot.com";
		}
		return "http://127.0.0.1:8888/TablePlus.html?gwt.codesvr=127.0.0.1:9997";
	}

	/**
	 * Returns the appropriate server callback path according to the environment
	 * value
	 * 
	 * @return The server path
	 */
	public static String getCallbackUrl() {
		if (environment == production) {
			return "http://" + SystemProperty.applicationVersion.get() + "."
					+ SystemProperty.applicationId.get()
					+ ".appspot.com/tableplus/callback";
		}
		return "http://127.0.0.1:8888/tableplus/callback";
	}

	/**
	 * Returns the appropriate server invitation servlet path according to the
	 * environment value
	 * 
	 * @return The server path
	 */
	public static String getInvitationServletUrl() {
		if (environment == production) {
			return "http://" + SystemProperty.applicationVersion.get() + "."
					+ SystemProperty.applicationId.get()
					+ ".appspot.com/tableplus/invitation";
		}
		return "http://127.0.0.1:8888/tableplus/invitation";
	}

	/**
	 * Returns the request url with parameters and respective values
	 * 
	 * @return Request url
	 */
	public static String getRequestUrl(HttpServletRequest req) {
		StringBuilder request = new StringBuilder();
		StringBuffer address = req.getRequestURL();
		request.append(address + "?");

		Enumeration<?> parameters = req.getParameterNames();
		String attribute;
		String value;
		while (parameters.hasMoreElements()) {
			attribute = (String) parameters.nextElement();
			value = req.getParameter(attribute);
			request.append(attribute + "=" + value + "&");
		}
		return request.toString();
	}

	/**
	 * @return the environment value
	 * 
	 */
	public Value getEnvironment() {
		return environment;
	}
}
