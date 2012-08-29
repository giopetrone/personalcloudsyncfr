package com.unito.tableplus.server;

import javax.jdo.PersistenceManager;

import com.unito.tableplus.shared.model.Comment;

public class CommentQueries {
	
	public static Comment queryComment(String key) {
		PersistenceManager pm = ServiceFactory.getPmfInstance().getPersistenceManager();
		Comment comment = null;
		try {
			Object object = pm.getObjectById(Comment.class, key);
			comment = (Comment) pm.detachCopy(object);
		} catch (Exception e) {
			System.err.println("There has been an error querying comment: " + e);
		} finally {
			pm.close();
		}
		return comment;
	}
}
