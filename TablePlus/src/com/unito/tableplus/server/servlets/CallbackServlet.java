package com.unito.tableplus.server.servlets;

import java.io.IOException;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.scribe.model.Token;

import com.google.appengine.api.memcache.MemcacheService;
import com.google.appengine.api.users.User;
import com.google.appengine.api.users.UserService;
import com.unito.tableplus.server.ServiceFactory;
import com.unito.tableplus.server.UserQueries;
import com.unito.tableplus.server.Utils;
import com.unito.tableplus.server.WalletQueries;
import com.unito.tableplus.server.services.DriveServiceImpl;
import com.unito.tableplus.server.services.DropBoxServiceImpl;
import com.unito.tableplus.shared.model.LoginInfo;
import com.unito.tableplus.shared.model.Wallet;

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
		    loginInfo.setLoginUrl(userService.createLoginURL(Utils.getRequestUrl(req)));
			resp.sendRedirect(loginInfo.getLoginUrl());
		} else {
			com.unito.tableplus.shared.model.User u = null;
			Wallet wallet = null;

			if (req.getParameter("provider").equals("google")) {
				String token = req.getParameter("token");
				if (token != null) {
					String sessionToken = DriveServiceImpl
							.getDriveSessionToken(token);
					u = UserQueries.queryUser("email", user.getEmail());
					if (u != null) {
						wallet = WalletQueries.getWallet(u.getKey());
						wallet.setDriveToken(sessionToken);
						WalletQueries.storeWallet(wallet);
					}
				}
			} else if (req.getParameter("provider").equals("dropbox")) {
				Token requestToken = (Token) syncCache.get(user.getEmail());
				if (requestToken != null) {
					syncCache.delete(user.getEmail());
					String oauthToken = req.getParameter("oauth_token");
					Token accessToken = DropBoxServiceImpl.getAccessToken(
							oauthToken, requestToken);
					u = UserQueries.queryUser("email", user.getEmail());
					if (accessToken != null & u != null) {
						u = UserQueries.queryUser("email", user.getEmail());
						wallet = WalletQueries.getWallet(u.getKey());
						wallet.setDropboxToken(accessToken.getToken());
						wallet.setDropboxSecret(accessToken.getSecret());
						WalletQueries.storeWallet(wallet);
					}

				}
			}
		}
		resp.sendRedirect(Utils.getHomeUrl());
	}
}
