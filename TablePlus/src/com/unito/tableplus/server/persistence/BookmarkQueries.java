package com.unito.tableplus.server.persistence;

import java.util.List;

import javax.jdo.PersistenceManager;
import javax.jdo.Transaction;

import com.unito.tableplus.server.util.ServiceFactory;
import com.unito.tableplus.shared.model.Bookmark;
import com.unito.tableplus.shared.model.Comment;
import com.unito.tableplus.shared.model.TableObject;

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

	public static boolean deleteBookmark(String key) {
		PersistenceManager pm = ServiceFactory.getPmfInstance().getPersistenceManager();
		boolean result=false;
		try {
			Object object = pm.getObjectById(Bookmark.class, key);
			pm.deletePersistent(object);
			result=true;
		} catch (Exception e) {
			System.err.println("Something gone wrong deleting the bookmark: " + e);
		} finally {
			pm.close();
		}
		return result;
	}

	public static boolean addComment(String key, Comment comment) {
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
		TableObject detached = null;
		try {
			TableObject bookmark = pm.getObjectById(TableObject.class, key);
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

	public static boolean deleteComment(String key) {
		boolean result=false;
		PersistenceManager pm = ServiceFactory.getPmfInstance().getPersistenceManager();
		try {
			Comment c = pm.getObjectById(Comment.class, key);
			pm.deletePersistentAll(c);
			result=true;
		} catch (Exception e) {
			System.err.println("There has been an error deleting comment: " + e);
		} finally {
			pm.close();
		}	
		return result;
		
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
		PersistenceManager pm = ServiceFactory.getPmfInstance().getPersistenceManager();
		Bookmark bookmark = null;
		Boolean result=false;
		try {
			Object object = pm.getObjectById(Bookmark.class, key);
			bookmark = (Bookmark) pm.detachCopy(object);
			bookmark.setLegend(newLegend);
			pm.makePersistent(bookmark);
			result=true;
		} catch (Exception e) {
			System.err.println("There has been an error editing bookmark's legend: " + e);
			
		} finally {
			pm.close();
		}
		return result;
	}

	public static boolean addTag(String key, String tag) {
		PersistenceManager pm = ServiceFactory.getPmfInstance().getPersistenceManager();
		Bookmark bookmark = null;
		Boolean result=false;
		try {
			Object object = pm.getObjectById(Bookmark.class, key);
			bookmark = (Bookmark) pm.detachCopy(object);
			bookmark.addTag(tag);
			pm.makePersistent(bookmark);
			result=true;
		} catch (Exception e) {
			System.err.println("There has been an error adding tag: " + e);
			
		} finally {
			pm.close();
		}
		return result;
	}

	public static boolean removeTag(String key, int tag) {
		PersistenceManager pm = ServiceFactory.getPmfInstance().getPersistenceManager();
		Bookmark bookmark = null;
		Boolean result=false;
		try {
			Object object = pm.getObjectById(Bookmark.class, key);
			bookmark = (Bookmark) pm.detachCopy(object);
			bookmark.removeTag(tag);
			pm.makePersistent(bookmark);
			result=true;
		} catch (Exception e) {
			System.err.println("There has been an error deleting tag: " + e);
			
		} finally {
			pm.close();
		}
		return result;
	}

	public static boolean addAnnotation(String key, String annotation) {
		PersistenceManager pm = ServiceFactory.getPmfInstance().getPersistenceManager();
		Bookmark bookmark = null;
		Boolean result=false;
		try {
			Object object = pm.getObjectById(Bookmark.class, key);
			bookmark = (Bookmark) pm.detachCopy(object);
			bookmark.addAnnotation(annotation);
			pm.makePersistent(bookmark);
			result=true;
		} catch (Exception e) {
			System.err.println("There has been an error adding annotation: " + e);
			
		} finally {
			pm.close();
		}
		return result;
	}

	public static boolean removeAnnotation(String key, int annotation) {
		PersistenceManager pm = ServiceFactory.getPmfInstance().getPersistenceManager();
		Bookmark bookmark = null;
		Boolean result=false;
		try {
			Object object = pm.getObjectById(Bookmark.class, key);
			bookmark = (Bookmark) pm.detachCopy(object);
			bookmark.removeAnnotation(annotation);
			pm.makePersistent(bookmark);
			result=true;
		} catch (Exception e) {
			System.err.println("There has been an error deleting annotation: " + e);
			
		} finally {
			pm.close();
		}
		return result;
	}

}
