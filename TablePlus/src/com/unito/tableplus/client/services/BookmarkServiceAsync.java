package com.unito.tableplus.client.services;

import java.util.List;

import com.google.gwt.user.client.rpc.AsyncCallback;
import com.unito.tableplus.shared.model.Bookmark;
import com.unito.tableplus.shared.model.Comment;

public interface BookmarkServiceAsync {

	void storeBookmark(Bookmark bookmark, AsyncCallback<Void> asyncCallback);

	void queryBookmark(String key, AsyncCallback<Bookmark> callback);

	void deleteBookmark(String key, AsyncCallback<Void> callback);

	void addComment(String string, Comment comment, AsyncCallback<Boolean> asyncCallback);
	
	void getComments(String key, AsyncCallback<List<Comment>> callback);

	void deleteComment(String key, AsyncCallback<Void> asyncCallback);

	void editComment(Bookmark b, String key, AsyncCallback<String> asyncCallback);

}
