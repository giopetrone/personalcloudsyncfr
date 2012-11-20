package com.unito.tableplus.server.servlets;

import java.io.IOException;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import com.google.appengine.api.users.User;
import com.google.appengine.api.users.UserService;
import com.google.appengine.api.users.UserServiceFactory;
import com.unito.tableplus.server.persistence.InvitationQueries;
import com.unito.tableplus.server.persistence.TableQueries;
import com.unito.tableplus.server.persistence.UserQueries;
import com.unito.tableplus.server.util.Utility;
import com.unito.tableplus.shared.model.Invitation;
import com.unito.tableplus.shared.model.LoginInfo;

public class InvitationServlet extends HttpServlet {

	private static final long serialVersionUID = -2761251611193483348L;
	private static final UserService userService = UserServiceFactory
			.getUserService();

	@Override
	protected void doGet(HttpServletRequest req, HttpServletResponse resp)
			throws ServletException, IOException {
		serveRequest(req, resp);
	}

	@Override
	protected void doPost(HttpServletRequest req, HttpServletResponse resp)
			throws ServletException, IOException {
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
			String code = req.getParameter("code");
			if (code != null) {
				Invitation i = InvitationQueries.queryInvitation(code);
				if (i != null) {
					com.unito.tableplus.shared.model.User invitedUser = UserQueries
							.queryUser("email", i.getInvitedUser());
					if (invitedUser == null) {
						invitedUser = new com.unito.tableplus.shared.model.User();
						invitedUser.setEmail(user.getEmail());
						invitedUser.setUsername(user.getNickname());
						UserQueries.storeUser(invitedUser);
					}
					TableQueries.addMember(invitedUser.getKey(), i.getTableKey());
					// TODO: send notification to table members
					InvitationQueries.deleteInvitation(i.getKey());
				}
			}
		}
		resp.sendRedirect(Utility.getHomeUrl());
	}
}
