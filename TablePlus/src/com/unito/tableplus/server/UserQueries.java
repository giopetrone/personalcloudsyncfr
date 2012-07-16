package com.unito.tableplus.server;

import java.util.LinkedList;
import java.util.List;

import javax.jdo.PersistenceManager;
import javax.jdo.Query;

import com.unito.tableplus.shared.model.User;

public class UserQueries {

	public static Long storeUser(User user) {
		PersistenceManager pm = ServiceFactory.getPmfInstance().getPersistenceManager();
		Long key = null;
		try {
			pm.makePersistent(user);
			key = user.getKey();
		} catch (Exception e) {
			System.err.println("Something gone wrong storing the user: " + e);
			e.printStackTrace();
		} finally {
			pm.close();
		}
		return key;
	}

	public static User queryUser(String fieldName, String fieldValue) {
		PersistenceManager pm = ServiceFactory.getPmfInstance().getPersistenceManager();
		Query query = pm.newQuery(User.class);
		query.setFilter(fieldName + " == param");
		query.declareParameters("String param");
		User detachedUser = null;
		try {
			@SuppressWarnings("unchecked")
			List<User> results = (List<User>) query.execute(fieldValue);
			if (!results.isEmpty())
				detachedUser = pm.detachCopy(results.get(0));
		} catch (Exception e) {
			System.err.println("Something gone wrong querying the user: " + e);
		} finally {
			query.closeAll();
			pm.close();
		}
		return detachedUser;
	}

	public static User queryUser(Long key) {
		PersistenceManager pm = ServiceFactory.getPmfInstance().getPersistenceManager();
		User user = null;
		try {
			Object object = pm.getObjectById(User.class, key);
			user = (User) pm.detachCopy(object);
		} catch (Exception e) {
			System.err.println("There has been an error querying users: " + e);
		} finally {
			pm.close();
		}
		return user;
	}

	public static void deleteUser(Long key) {
		PersistenceManager pm = ServiceFactory.getPmfInstance().getPersistenceManager();
		try {
			Object object = pm.getObjectById(User.class, key);
			pm.deletePersistent(object);
		} catch (Exception e) {
			System.err.println("Something gone wrong deleting the user: " + e);
		} finally {
			pm.close();
		}
	}

	public static List<User> queryUsers(List<Long> keys) {
		PersistenceManager pm = ServiceFactory.getPmfInstance().getPersistenceManager();
		List<User> users = new LinkedList<User>();
		Object object = null;
		try {
			for (Long key : keys) {
				object = pm.getObjectById(User.class, key);
				users.add((User) pm.detachCopy(object));
			}
		} catch (Exception e) {
			e.printStackTrace();
		} finally {
			pm.close();
		}
		return users;
	}

}
