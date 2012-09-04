package com.unito.tableplus.client.services;

import java.util.List;
import com.google.gwt.user.client.rpc.RemoteService;
import com.google.gwt.user.client.rpc.RemoteServiceRelativePath;
import com.unito.tableplus.shared.model.Bookmark;
import com.unito.tableplus.shared.model.Comment;

@RemoteServiceRelativePath("bookmark-service")
public interface BookmarkService extends RemoteService {
	
	Bookmark queryBookmark(String key);

	void deleteBookmark(String key);

	boolean addComment(String string, Comment comment);
	
	List<Comment> getComments(String key);

	void deleteComment(String key);

	String editComment(Bookmark b, String key);

	boolean editLegend(String key, String newLegend);

	void storeBookmark(Bookmark bookmark);

}
