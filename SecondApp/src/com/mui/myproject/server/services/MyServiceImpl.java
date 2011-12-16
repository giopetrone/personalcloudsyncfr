package com.mui.myproject.server.services;

import com.google.gwt.user.server.rpc.RemoteServiceServlet;
import com.mui.myproject.client.services.MyService;

public class MyServiceImpl extends RemoteServiceServlet implements MyService {
	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;

	public String myMethod(String s) {
		// Do something interesting with 's' here on the server.
		return s+" + "+s;
	}

}
