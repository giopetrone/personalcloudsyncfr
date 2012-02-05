package com.unito.tableplus.server.services;

import java.util.List;

import javax.jdo.PersistenceManager;
import javax.jdo.Query;
import com.google.gwt.user.server.rpc.RemoteServiceServlet;
import com.unito.tableplus.client.services.UserService;
import com.unito.tableplus.shared.model.GoogleUser;
import com.unito.tableplus.shared.model.User;

public class UserServiceImpl extends RemoteServiceServlet implements
		UserService {
	
	private static final long serialVersionUID = 2345237647330858842L;

	@Override
	public User queryUserByUsername(String username) {
		PersistenceManager pm = PMF.get().getPersistenceManager();
		Query query = pm.newQuery(GoogleUser.class);
		query.setFilter("username == usernameParam");
		query.declareParameters("String usernameParam");
		User user = null;
		try {
			@SuppressWarnings("unchecked")
			List<User> results = (List<User>) query.execute(username);
			if (results.isEmpty())
				return user;
			user = pm.detachCopy(results.get(0));
		} catch (Exception e) {
			System.err.println("There has been an error querying user: " + e);
			e.printStackTrace();
		} finally {
			query.closeAll();
			pm.close();
		}
		return user;
	}

	@Override
	public User queryUserByEmail(String email) {
		PersistenceManager pm = PMF.get().getPersistenceManager();
		Query query = pm.newQuery(User.class);
		query.setFilter("email == emailParam");
		query.declareParameters("String emailParam");
		User user = null;
		try {
			@SuppressWarnings("unchecked")
			List<User> results = (List<User>) query.execute(email);
			if (results.isEmpty())
				return user;
			user = pm.detachCopy(results.get(0));
		} catch (Exception e) {
			System.err.println("There has been an error querying user: " + e);
		} finally {
			query.closeAll();
			pm.close();
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
	
	protected static boolean deleteUser(Long key) {
		PersistenceManager pm = PMF.get().getPersistenceManager();
		try {
			Object object = pm.getObjectById(User.class, key);
			pm.deletePersistent(object);
		} catch (Exception e) {
			System.err.println("Something gone wrong deleting the user: " + e);
		} finally {
			pm.close();
		}
		return true;
	}

}
