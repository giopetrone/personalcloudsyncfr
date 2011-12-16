package com.mui.myproject.client.services;

import com.google.gwt.user.client.rpc.RemoteService;
import com.google.gwt.user.client.rpc.RemoteServiceRelativePath;
import com.mui.myproject.shared.model.Feed;

@RemoteServiceRelativePath("feed-service")
public interface FeedService extends RemoteService{
	  Feed createNewFeed();
}
