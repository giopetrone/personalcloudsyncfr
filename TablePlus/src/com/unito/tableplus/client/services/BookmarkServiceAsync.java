package com.unito.tableplus.client.services;

import java.util.LinkedList;
import java.util.List;

import com.google.gwt.user.client.rpc.AsyncCallback;
import com.unito.tableplus.shared.model.Bookmark;
import com.unito.tableplus.shared.model.Comment;

public interface BookmarkServiceAsync {

	void queryBookmark(String key, AsyncCallback<Bookmark> callback);

	void deleteBookmark(String key, AsyncCallback<Void> callback);

	void addComment(String string, Comment comment, AsyncCallback<Boolean> asyncCallback);
	
	void getComments(String key, AsyncCallback<List<Comment>> callback);

	void deleteComment(String key, AsyncCallback<Void> asyncCallback);

	void editComment(Bookmark b, String key, AsyncCallback<String> asyncCallback);

	void editLegend(String key, String newLegend, AsyncCallback<Boolean> asyncCallback);

	void storeBookmark(Bookmark bookmark, AsyncCallback<Void> callback);

	void addTag(String key, String tag, AsyncCallback<Boolean> asyncCallback);

	void removeTag(String key, String tag, AsyncCallback<Boolean> asyncCallback);


}

