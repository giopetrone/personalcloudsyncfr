package com.unito.tableplus.client.services;

import com.google.gwt.user.client.rpc.AsyncCallback;
import com.unito.tableplus.shared.model.Comment;

public interface CommentServiceAsync {
	
	void queryComment(String key, AsyncCallback<Comment> callback);
	
}
