package com.mui.myproject.server.services;

import java.util.UUID;

import com.google.gwt.user.server.rpc.RemoteServiceServlet;
import com.mui.myproject.client.services.FeedService;
import com.mui.myproject.shared.model.Feed;

public class FeedServiceImpl extends RemoteServiceServlet implements
		FeedService {
	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;

	@Override
	public Feed createNewFeed() {
		UUID uuid = UUID.randomUUID();
		return new Feed(uuid.toString());
	}
}
