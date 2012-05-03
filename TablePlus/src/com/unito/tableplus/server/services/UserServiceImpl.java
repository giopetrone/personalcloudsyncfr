package com.unito.tableplus.server.services;

import java.util.LinkedList;
import java.util.List;
import javax.jdo.PersistenceManager;
import javax.jdo.Query;

import com.google.appengine.api.users.UserServiceFactory;
import com.google.gwt.user.server.rpc.RemoteServiceServlet;
import com.unito.tableplus.client.services.UserService;
import com.unito.tableplus.shared.model.User;

public class UserServiceImpl extends RemoteServiceServlet implements
		UserService {

	private static final long serialVersionUID = 2345237647330858842L;

	@Override
	public String isLoggedIn(String requestUri) {
		com.google.appengine.api.users.UserService appEngineUserService = UserServiceFactory
				.getUserService();
		com.google.appengine.api.users.User appEngineUser = appEngineUserService
				.getCurrentUser();

		return (appEngineUser != null) ? "y"
				+ appEngineUserService.createLogoutURL(requestUri) : "n"
				+ appEngineUserService.createLoginURL(requestUri);
	}

	@Override
	public User getCurrentUser() {
		com.google.appengine.api.users.UserService appEngineUserService = UserServiceFactory
				.getUserService();
		com.google.appengine.api.users.User appEngineUser = appEngineUserService
				.getCurrentUser();

		String email = appEngineUser.getEmail();


		User user = queryUser("email", email);

		if (user == null) {
			user = new User();
			user.setEmail(email);
			user.setOnline(true);
			storeUser(user);
		} else {
			user.setOnline(true);
			storeUser(user);
		}

		return user;
	}

	@Override
	public void storeUser(User user) {
		PersistenceManager pm = PMF.get().getPersistenceManager();
		try {
			pm.makePersistent(user);
		} catch (Exception e) {
			System.err.println("Something gone wrong storing the user: " + e);
			e.printStackTrace();
		} finally {
			pm.close();
		}
	}

	@Override
	public User queryUser(String fieldName, String fieldValue) {
		PersistenceManager pm = PMF.get().getPersistenceManager();
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

	@Override
	public User queryUser(Long key) {
		PersistenceManager pm = PMF.get().getPersistenceManager();
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

	@Override
	public void deleteUser(Long key) {
		PersistenceManager pm = PMF.get().getPersistenceManager();
		try {
			Object object = pm.getObjectById(User.class, key);
			pm.deletePersistent(object);
		} catch (Exception e) {
			System.err.println("Something gone wrong deleting the user: " + e);
		} finally {
			pm.close();
		}
	}

	@Override
	public List<User> queryUsers(List<Long> keys) {
		PersistenceManager pm = PMF.get().getPersistenceManager();
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
