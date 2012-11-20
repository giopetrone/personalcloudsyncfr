package com.unito.tableplus.server.services;

import com.google.appengine.api.users.User;
import com.google.appengine.api.users.UserService;
import com.google.gwt.user.server.rpc.RemoteServiceServlet;
import com.unito.tableplus.client.services.LoginService;
import com.unito.tableplus.server.util.ServiceFactory;
import com.unito.tableplus.shared.model.LoginInfo;

public class LoginServiceImpl extends RemoteServiceServlet implements
		LoginService {
	
	private static final long serialVersionUID = 656712748090184127L;

	@Override
	public LoginInfo login(String requestUri) {
		UserService userService = ServiceFactory.getUserService();
	    User user = userService.getCurrentUser();
	    LoginInfo loginInfo = new LoginInfo();

	    if (user != null) {
	      loginInfo.setLoggedIn(true);
	      loginInfo.setEmailAddress(user.getEmail());
	      loginInfo.setNickname(user.getNickname());
	      loginInfo.setLogoutUrl(userService.createLogoutURL(requestUri));
	    } else {
	      loginInfo.setLoggedIn(false);
	      loginInfo.setLoginUrl(userService.createLoginURL(requestUri));
	    }
	    return loginInfo;
	  }

}
