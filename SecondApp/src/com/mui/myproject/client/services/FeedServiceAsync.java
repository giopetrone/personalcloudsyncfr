package com.mui.myproject.client.services;

import com.google.gwt.user.client.rpc.AsyncCallback;
import com.mui.myproject.shared.model.Feed;

public interface FeedServiceAsync {
	void createNewFeed(AsyncCallback<Feed> callback);
}
