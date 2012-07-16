package com.unito.tableplus.server.servlets;

import java.io.IOException;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import com.google.appengine.api.channel.ChannelPresence;
import com.google.appengine.api.channel.ChannelService;
import com.unito.tableplus.server.ServiceFactory;
import com.unito.tableplus.server.services.MessagingServiceImpl;

public class DisconnectionServlet extends HttpServlet {
	
	private static final long serialVersionUID = -2940772294465739242L;

	@Override
	protected void doPost(HttpServletRequest req, HttpServletResponse resp)
			throws ServletException, IOException {
		ChannelService channelService = ServiceFactory.getChannelService();
		ChannelPresence presence = channelService.parsePresence(req);
		MessagingServiceImpl.removeUser(presence.clientId());
	}
}
