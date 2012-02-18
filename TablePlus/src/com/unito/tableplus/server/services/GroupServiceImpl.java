package com.unito.tableplus.server.services;

import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;

import javax.jdo.PersistenceManager;
import javax.jdo.Transaction;
import com.google.gwt.user.server.rpc.RemoteServiceServlet;
import com.unito.tableplus.client.services.GroupService;
import com.unito.tableplus.shared.model.Group;
import com.unito.tableplus.shared.model.Message;

public class GroupServiceImpl extends RemoteServiceServlet implements
		GroupService {

	private static final long serialVersionUID = 1L;

	@Override
	public List<Group> queryGroups(List<Long> keys) {
		PersistenceManager pm = PMF.get().getPersistenceManager();
		List<Group> groups = new LinkedList<Group>();
		Object object = null;
		try {
			for (Long key : keys) {
				object = pm.getObjectById(Group.class, key);
				groups.add((Group) pm.detachCopy(object));
			}
		} catch (Exception e) {
			System.err.println("There has been an error querying groups: " + e);
		} finally {
			pm.close();
		}
		return groups;
	}

	@Override
	public Long storeGroup(Group group) {
		Long key = null;
		PersistenceManager pm = PMF.get().getPersistenceManager();
		try {
			pm.makePersistent(group);
			key = group.getKey();
		} catch (Exception e) {
			System.err.println("There has been an error storing the group: "
					+ e);
		} finally {
			pm.close();
		}
		return key;
	}

	@Override
	public Group queryGroup(Long key) {
		PersistenceManager pm = PMF.get().getPersistenceManager();
		Group detached = null;
		try {
			Group group = pm.getObjectById(Group.class, key);
			if (group == null)
				return group;
			detached = pm.detachCopy(group);
		} catch (Exception e) {
			System.err.println("There has been an error querying groups: " + e);
		} finally {
			pm.close();
		}
		return detached;
	}

	@Override
	public void deleteGroup(Long key) {
		PersistenceManager pm = PMF.get().getPersistenceManager();

		try {
			Group group = pm.getObjectById(Group.class, key);
			pm.deletePersistentAll(group);
		} catch (Exception e) {
			System.err.println("Something gone wrong deleting the Group: " + e);
		} finally {
			pm.close();
		}
	}

	@Override
	public boolean addMessage(Long key, Message message) {
		PersistenceManager pm = PMF.get().getPersistenceManager();
		Transaction tx = pm.currentTransaction();
		try {
			Group g = pm.getObjectById(Group.class, key);
			if (g == null)
				return false;
			tx.begin();
			g.getBlackBoard().add(message);
			tx.commit();
		} catch (Exception e) {
			System.err.println("There has been an error adding message: " + e);
		} finally {
			if (tx.isActive())
				tx.rollback();
			pm.close();
		}
		return true;
	}

	public boolean removeMessage(Long groupKey, Long message) {
		PersistenceManager pm = PMF.get().getPersistenceManager();
		Transaction tx = pm.currentTransaction();
		Boolean found = false;
		try {
			Group group = pm.getObjectById(Group.class, groupKey);
			if (group == null)
				return false;
			List<Message> blackboard = group.getBlackBoard();
			Iterator<Message> i = blackboard.iterator();
			while (i.hasNext() && !found) {
				if (i.next().equals(message)) {
					tx.begin();
					i.remove();
					found = true;
					tx.commit();
				}
			}
		} catch (Exception e) {
			System.err
					.println("There has been an error removing message: " + e);
		} finally {
			if (tx.isActive())
				tx.rollback();
			pm.close();
		}
		return found;
	}

	@Override
	public boolean clearMessages(Long key) {
		PersistenceManager pm = PMF.get().getPersistenceManager();
		List<Message> blackboard = null;
		try {
			Group group = pm.getObjectById(Group.class, key);
			if (group == null)
				return false;
			blackboard = group.getBlackBoard();
			blackboard.clear();
		} catch (Exception e) {
			System.err.println("There has been an error clearing messages: "
					+ e);
		} finally {
			pm.close();
		}
		return true;
	}

}
