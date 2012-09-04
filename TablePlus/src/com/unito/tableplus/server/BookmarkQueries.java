package com.unito.tableplus.server;

import java.util.List;
import javax.jdo.PersistenceManager;
import javax.jdo.Transaction;
import com.unito.tableplus.shared.model.Bookmark;
import com.unito.tableplus.shared.model.Comment;

public class BookmarkQueries {
	
	public static void storeBookmark(Bookmark bookmark) {

		PersistenceManager pm = ServiceFactory.getPmfInstance().getPersistenceManager();
		try {
			pm.makePersistent(bookmark);

		} catch (Exception e) {
			System.err.println("Something gone wrong storing the bookmark: " + e);
			e.printStackTrace();
		} finally {
			pm.close();
		}
	}
	
	public static Bookmark queryBookmark(String key) {
		PersistenceManager pm = ServiceFactory.getPmfInstance().getPersistenceManager();
		Bookmark bookmark = null;
		try {
			Object object = pm.getObjectById(Bookmark.class, key);
			bookmark = (Bookmark) pm.detachCopy(object);
		} catch (Exception e) {
			System.err.println("There has been an error querying bookmarks: " + e);
		} finally {
			pm.close();
		}
		return bookmark;
	}

	public static void deleteBookmark(String key) {
		PersistenceManager pm = ServiceFactory.getPmfInstance().getPersistenceManager();
		try {
			Object object = pm.getObjectById(Bookmark.class, key);
			pm.deletePersistent(object);
		} catch (Exception e) {
			System.err.println("Something gone wrong deleting the bookmark: " + e);
		} finally {
			pm.close();
		}
	}

	public static boolean addComment(String key, Comment comment) {
		System.out.println("Bookmark queries add comment" );
		PersistenceManager pm = ServiceFactory.getPmfInstance().getPersistenceManager();
		Transaction tx = pm.currentTransaction();
		try {
			Bookmark b = pm.getObjectById(Bookmark.class, key);
			if (b == null) return false;
			tx.begin();
			b.getComments().add(comment);
			tx.commit();
		} catch (Exception e) {
			System.err.println("There has been an error adding comment: " + e);
		} finally {
			if (tx.isActive()) tx.rollback();
			pm.close();
		}
		return true;
	}

	public static List<Comment> getComments(String key) {
		PersistenceManager pm = ServiceFactory.getPmfInstance().getPersistenceManager();
		Bookmark detached = null;
		try {
			Bookmark bookmark = pm.getObjectById(Bookmark.class, key);
			if (bookmark == null) return null;
			bookmark.getComments();
			detached = pm.detachCopy(bookmark);
		} catch (Exception e) {
			System.err.println("Error querying comments");
		} finally {
			pm.close();
		}
		return detached.getComments();
	}

	public static void deleteComment(String key) {
		System.out.println("Bookmark queries delete comment" );

		PersistenceManager pm = ServiceFactory.getPmfInstance().getPersistenceManager();
		try {
			Comment c = pm.getObjectById(Comment.class, key);
			pm.deletePersistentAll(c);
		} catch (Exception e) {
			System.err.println("There has been an error deleting comment: " + e);
		} finally {
			pm.close();
		}	
		
	}

	public static String editComment(final Bookmark b, String key) {
		String editable=null;
		PersistenceManager pm = ServiceFactory.getPmfInstance().getPersistenceManager();
		try {
			final Comment c = pm.getObjectById(Comment.class, key);
			editable= c.getComment();
		} catch (Exception e) {
			System.err.println("There has been an error editing comment: " + e);
		} finally {
			pm.close();
		}	
		return editable;
		
	}

	public static boolean editLegend(String key, String newLegend) {
		System.out.println("*********BookmarkQueries editLegend(String key, String newLegend):"+ key+", "+ newLegend+ "**********");

		PersistenceManager pm = ServiceFactory.getPmfInstance().getPersistenceManager();
		Bookmark bookmark = null;
		Boolean result=false;
		try {
			Object object = pm.getObjectById(Bookmark.class, key);
			bookmark = (Bookmark) pm.detachCopy(object);
			bookmark.setLegend(newLegend);
			result=true;
		} catch (Exception e) {
			System.err.println("There has been editing bookmark's legend: " + e);
			
		} finally {
			pm.close();
		}
		return result;
	}

}
