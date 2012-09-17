package com.unito.tableplus.server.services;

import java.util.List;
import com.google.gwt.user.server.rpc.RemoteServiceServlet;
import com.unito.tableplus.client.services.BookmarkService;
import com.unito.tableplus.server.BookmarkQueries;
import com.unito.tableplus.shared.model.Bookmark;
import com.unito.tableplus.shared.model.Comment;

public class BookmarkServiceImpl extends RemoteServiceServlet implements BookmarkService {

	private static final long serialVersionUID = 2345237647330858842L;
	
	@Override
	public void storeBookmark(Bookmark bookmark) {
		BookmarkQueries.storeBookmark(bookmark);
	}
	
	@Override
	public Bookmark queryBookmark(String key) {
		return BookmarkQueries.queryBookmark(key);
	}

	@Override
	public boolean deleteBookmark(String key) {
		return BookmarkQueries.deleteBookmark(key);
	}

	public boolean addComment(String key, Comment comment) {
		return BookmarkQueries.addComment(key, comment);
	}

	public List<Comment> getComments(String key) {
		return BookmarkQueries.getComments(key);
	}

	@Override
	public boolean deleteComment(String key) {
		return BookmarkQueries.deleteComment(key);
	}

	@Override
	public String editComment(Bookmark b, String key) {
		return BookmarkQueries.editComment(b, key);
		
	}

	@Override
	public boolean editLegend(String key, String newLegend) {
		return BookmarkQueries.editLegend(key, newLegend);
	}

	@Override
	public boolean addTag(String key, String tag) {
		return BookmarkQueries.addTag(key, tag);
	}

	@Override
	public boolean removeTag(String key, int tag) {
		return BookmarkQueries.removeTag(key, tag);
	}


}
