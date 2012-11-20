package com.unito.tableplus.server.persistence;

import java.util.List;

import javax.jdo.PersistenceManager;
import javax.jdo.Query;

import com.unito.tableplus.server.util.ServiceFactory;
import com.unito.tableplus.shared.model.Invitation;

public class InvitationQueries {
	
	public static Long storeInvitation(Invitation invitation) {
		Long key = null;
		PersistenceManager pm = ServiceFactory.getPmfInstance()
				.getPersistenceManager();
		try {
			pm.makePersistent(invitation);
			key = invitation.getKey();
		} catch (Exception e) {
			System.err
					.println("There has been an error storing the invitation: "
							+ e);
		} finally {
			pm.close();
		}
		return key;
	}
	
	public static Invitation queryInvitation(String code) {
		PersistenceManager pm = ServiceFactory.getPmfInstance()
				.getPersistenceManager();
		Query query = pm.newQuery(Invitation.class);
		query.setFilter("code == codeParam");
		query.declareParameters("String codeParam");
		Invitation invitation = null;
		try {
			@SuppressWarnings("unchecked")
			List<Invitation> results = (List<Invitation>) query.execute(code);
			if (!results.isEmpty())
				invitation = results.get(0);
		} catch (Exception e) {
			System.err.println("Something gone wrong querying the invitation: "
					+ e);
		} finally {
			query.closeAll();
			pm.close();
		}
		return invitation;
	}

	public static void deleteInvitation(Invitation i) {
		PersistenceManager pm = ServiceFactory.getPmfInstance()
				.getPersistenceManager();
		try {
			pm.deletePersistentAll(i);
		} catch (Exception e) {
			System.err.println("Something gone wrong deleting the Invitation: "
					+ e);
		} finally {
			pm.close();
		}
	}
	
	public static void deleteInvitation(Long key) {
		PersistenceManager pm = ServiceFactory.getPmfInstance()
				.getPersistenceManager();
		try {
			Invitation i = pm.getObjectById(Invitation.class, key);
			pm.deletePersistentAll(i);
		} catch (Exception e) {
			System.err.println("Something gone wrong deleting the Invitation: "
					+ e);
		} finally {
			pm.close();
		}
	}

}
