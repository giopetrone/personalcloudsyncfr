package com.unito.tableplus.client.services;

import java.util.List;
import com.google.gwt.user.client.rpc.AsyncCallback;
import com.unito.tableplus.shared.model.Bookmark;
import com.unito.tableplus.shared.model.Comment;

public interface BookmarkServiceAsync {

	void queryBookmark(String key, AsyncCallback<Bookmark> callback);

	void deleteBookmark(String key, AsyncCallback<Boolean> callback);

	void addComment(String string, Comment comment, AsyncCallback<Boolean> asyncCallback);
	
	void getComments(String key, AsyncCallback<List<Comment>> callback);

	void deleteComment(String key, AsyncCallback<Boolean> asyncCallback);

	void editComment(Bookmark b, String key, AsyncCallback<String> asyncCallback);

	void editLegend(String key, String newLegend, AsyncCallback<Boolean> asyncCallback);

	void storeBookmark(Bookmark bookmark, AsyncCallback<Void> callback);

	void addTag(String key, String tag, AsyncCallback<Boolean> asyncCallback);

	void removeTag(String key, int tag, AsyncCallback<Boolean> asyncCallback);

	void addAnnotation(String key, String annotation,AsyncCallback<Boolean> asyncCallback);

	void removeAnnotation(String key, int annotation, AsyncCallback<Boolean> asyncCallback);

}

