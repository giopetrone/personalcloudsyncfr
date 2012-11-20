package com.unito.tableplus.server.servlets;

import java.io.IOException;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import com.google.appengine.api.memcache.MemcacheService;
import com.google.appengine.api.users.User;
import com.google.appengine.api.users.UserService;
import com.unito.tableplus.server.services.DriveServiceImpl;
import com.unito.tableplus.server.services.DropBoxServiceImpl;
import com.unito.tableplus.server.services.FacebookServiceImpl;
import com.unito.tableplus.server.util.ServiceFactory;
import com.unito.tableplus.server.util.Utility;
import com.unito.tableplus.shared.model.LoginInfo;

public class CallbackServlet extends HttpServlet {

	private static final long serialVersionUID = 4460569977782921882L;
	UserService userService = ServiceFactory.getUserService();
	MemcacheService syncCache = ServiceFactory.getSyncCache();

	@Override
	protected void doGet(HttpServletRequest req, HttpServletResponse resp)
			throws ServletException, IOException {
		if (req.getParameter("provider") == null)
			resp.sendRedirect("http://www.google.com/");
		else
			serveRequest(req, resp);
	}

	@Override
	protected void doPost(HttpServletRequest req, HttpServletResponse resp)
			throws ServletException, IOException {
		if (req.getParameter("provider") == null)
			resp.sendRedirect("http://www.google.com/");
		else
			serveRequest(req, resp);

	}

	private void serveRequest(HttpServletRequest req, HttpServletResponse resp)
			throws IOException {
		User user = userService.getCurrentUser();
		if (user == null) {
			LoginInfo loginInfo = new LoginInfo();
			loginInfo.setLoggedIn(false);
			loginInfo.setLoginUrl(userService.createLoginURL(Utility
					.getRequestUrl(req)));
			resp.sendRedirect(loginInfo.getLoginUrl());
		} else {
			if (req.getParameter("provider").equals("drive")) {
				String code = req.getParameter("code");
				if (code != null)
					DriveServiceImpl.storeCredentials(code);
				
			} else if (req.getParameter("provider").equals("dropbox")) {
					String oauthToken = req.getParameter("oauth_token");
					if(oauthToken != null)
					DropBoxServiceImpl.storeAccessToken(oauthToken);
					
			} else if (req.getParameter("provider").equals("facebook")) {
				String code = req.getParameter("code");
				if(code != null)
					FacebookServiceImpl.storeAccessToken(code);
			}
		}
		resp.sendRedirect(Utility.getHomeUrl());
	}
}
