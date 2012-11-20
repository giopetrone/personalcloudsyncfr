package com.unito.tableplus.server.servlets;

import java.io.IOException;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import com.google.appengine.api.channel.ChannelPresence;
import com.google.appengine.api.channel.ChannelService;
import com.unito.tableplus.server.services.MessagingServiceImpl;
import com.unito.tableplus.server.util.ServiceFactory;

public class ConnectionServlet extends HttpServlet {

	private static final long serialVersionUID = 6138754568427831149L;

	@Override
	protected void doPost(HttpServletRequest req, HttpServletResponse resp)
			throws ServletException, IOException {
		ChannelService channelService = ServiceFactory.getChannelService();
		ChannelPresence presence = channelService.parsePresence(req);
		Long userKey = Long.parseLong(presence.clientId());
		MessagingServiceImpl.userConnection(userKey);
	}
}
