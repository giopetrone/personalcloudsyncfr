package com.unito.tableplus.server.services;

import com.google.gwt.user.server.rpc.RemoteServiceServlet;
import com.unito.tableplus.client.services.CommentService;
import com.unito.tableplus.server.persistence.CommentQueries;
import com.unito.tableplus.shared.model.Comment;

public class CommentServiceImpl extends RemoteServiceServlet implements CommentService {

	private static final long serialVersionUID = 1L;

	@Override
	public Comment queryComment(String key) {
		return CommentQueries.queryComment(key);
	}

}
