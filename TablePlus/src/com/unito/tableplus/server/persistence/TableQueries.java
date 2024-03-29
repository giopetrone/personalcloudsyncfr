package com.unito.tableplus.server.persistence;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import javax.jdo.PersistenceManager;
import javax.jdo.Transaction;

import com.unito.tableplus.server.util.ServiceFactory;
import com.unito.tableplus.shared.model.BlackBoardMessage;
import com.unito.tableplus.shared.model.Comment;
import com.unito.tableplus.shared.model.Resource;
import com.unito.tableplus.shared.model.TableObject;
import com.unito.tableplus.shared.model.Table;
import com.unito.tableplus.shared.model.User;
import com.unito.tableplus.shared.model.UserStatus;

public class TableQueries {
	
	public static Table storeNewTable(Table table, User creator) {
		PersistenceManager pm = ServiceFactory.getPmfInstance().getPersistenceManager();
		Transaction tx = pm.currentTransaction();
		try {
			tx.begin();
			table = pm.makePersistent(table);
			table = pm.detachCopy(table);
			creator.addTable(table.getKey());
			pm.makePersistent(creator);
			tx.commit();
		} catch (Exception e) {
			System.err.println("There has been an error storing new Table: " + e);
			return null;
		} finally {
			if (tx.isActive()) tx.rollback();
			pm.close();
		}
		return table;
	}
	
	public static Table queryTable(Long key) {
		PersistenceManager pm = ServiceFactory.getPmfInstance().getPersistenceManager();
		Table detached = null;
		try {
			Table table = pm.getObjectById(Table.class, key);
			if (table == null) return table;
			table.getTableObjects();
			table.getBlackboard();
			table.getMembersStatus();
			detached = pm.detachCopy(table);
		} catch (Exception e) {
			System.err.println("There has been an error querying table: " + e);
		} finally {
			pm.close();
		}
		return detached;
	}

	public static List<Table> queryTables(List<Long> keys) {
		PersistenceManager pm = ServiceFactory.getPmfInstance().getPersistenceManager();
		List<Table> tables = new ArrayList<Table>();
		try {
			for (Long key : keys) {
				Table t = pm.getObjectById(Table.class, key);
				t.getBlackboard(); //needed because of lazy behavior
				t.getTableObjects(); // as above
				tables.add(pm.detachCopy(t));
			}
		} catch (Exception e) {
			System.err.println("There has been an error querying tables: " + e);
		} finally {
			pm.close();
		}
		return tables;
	}

	public static boolean storeTable(Table table) {
		PersistenceManager pm = ServiceFactory.getPmfInstance().getPersistenceManager();
		try {
			pm.makePersistent(table);
		} catch (Exception e) {
			System.err.println("There has been an error storing the table: "
					+ e);
			return false;
		} finally {
			pm.close();
		}
		return true;
	}
	
	public static void deleteTable(Long key) {
		PersistenceManager pm = ServiceFactory.getPmfInstance().getPersistenceManager();
		try {
			Table table = pm.getObjectById(Table.class, key);
			pm.deletePersistentAll(table);
		} catch (Exception e) {
			System.err.println("Something gone wrong deleting the table: " + e);
		} finally {
			pm.close();
		}
	}

	public static boolean addMessage(Long tableKey,BlackBoardMessage bbMessage) {
		PersistenceManager pm = ServiceFactory.getPmfInstance().getPersistenceManager();
		Transaction tx = pm.currentTransaction();
		try {
			Table t = pm.getObjectById(Table.class, tableKey);
			if (t == null || t.getBlackboard() == null)
				return false;
			tx.begin();
			t.getBlackboard().add(bbMessage);
			tx.commit();
		} catch (Exception e) {
			System.err.println("There has been an error adding message: " + e);
			return false;
		} finally {
			if (tx.isActive()) tx.rollback();
			pm.close();
		}
		return true;
	}

	public static ArrayList<BlackBoardMessage> queryMessages(Long tableKey) {
		PersistenceManager pm = ServiceFactory.getPmfInstance().getPersistenceManager();
		Table detached = null;
		try {
			Table table = pm.getObjectById(Table.class, tableKey);
			if (table == null) return null;
			table.getBlackboard(); // needed because of lazy behaviour
			detached = pm.detachCopy(table);
		} catch (Exception e) {
			System.err.println("Error querying blackboard messages");
		} finally {
			pm.close();
		}
		return detached.getBlackboard();
	}

	public static void removeMessage(String messageKey) {
		PersistenceManager pm = ServiceFactory.getPmfInstance().getPersistenceManager();
		try {
			BlackBoardMessage bbMessage = pm.getObjectById(BlackBoardMessage.class, messageKey);
			pm.deletePersistentAll(bbMessage);
		} catch (Exception e) {
			System.err.println("There has been an error removing message: " + e);
		} finally {
			pm.close();
		}
	}

	public static boolean clearMessages(Long key) {
		PersistenceManager pm = ServiceFactory.getPmfInstance().getPersistenceManager();
		List<BlackBoardMessage> blackboard = null;
		try {
			Table table = pm.getObjectById(Table.class, key);
			if (table == null) return false;
			blackboard = table.getBlackboard();
			blackboard.clear();
		} catch (Exception e) {
			System.err.println("There has been an error clearing messages: "+ e);
		} finally {
			pm.close();
		}
		return true;
	}

	public static boolean addResource(Resource resource, Long tableKey) {
		PersistenceManager pm = ServiceFactory.getPmfInstance().getPersistenceManager();
		Transaction tx = pm.currentTransaction();
		try {
			Table table = pm.getObjectById(Table.class, tableKey);
			if (table == null)
				return false;
			TableObject sr = new TableObject(resource,table);
			tx.begin();
			table.getTableObjects().add(sr);
			tx.commit();
		} catch (Exception e) {
			System.err.println("There has been an error adding resource: " + e);
			return false;
		} finally {
			if (tx.isActive()) tx.rollback();
			pm.close();
		}
		return true;
		
	}
	
	public static ArrayList<TableObject> queryObjects(Long tableKey){
		PersistenceManager pm = ServiceFactory.getPmfInstance().getPersistenceManager();
		Table detachedTable = null;
		try {
			Table table = pm.getObjectById(Table.class, tableKey);
			if (table == null) return null;
			table.getTableObjects(); // needed because of lazy behaviour
			detachedTable = pm.detachCopy(table);
		} catch (Exception e) {
			System.err.println("Error querying table objects");
		} finally {
			pm.close();
		}
		return detachedTable.getTableObjects();
	}

	public static void addMember(Long userKey, Long tableKey) {
		Table t = queryTable(tableKey);
		User u = UserQueries.queryUser(userKey);
		
		t.addMember(u.getKey());
		storeTable(t);
		u.addTable(t.getKey());
		UserQueries.storeUser(u);
	}

	public static String editComment(TableObject b, String key) {
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

	public static Map<Long, UserStatus> queryMembersStatus(Long tableKey) {
		Table t = queryTable(tableKey);
		return t.getMembersStatus();
	}

	public static String setUserStatus(Long tableKey, Long userKey, UserStatus status) {
		PersistenceManager pm = ServiceFactory.getPmfInstance().getPersistenceManager();
		try {
			Table table = pm.getObjectById(Table.class, tableKey);
			if (table == null) 
				return "Unable to find table";
			table.setMemberStatus(userKey, status);
		} catch (Exception e) {
			System.err.println("There has been an error setting user status: " + e);
			System.err.println("TableKey: " + tableKey);
			System.err.println("UserKey: " + userKey);
			System.err.println("Status: " + status);
			return "Error querying table";
		} finally {
			pm.close();
		}
		return "Status has been successfully set";
	}
}
