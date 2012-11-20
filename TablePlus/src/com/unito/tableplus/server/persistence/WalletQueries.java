package com.unito.tableplus.server.persistence;

import java.util.List;

import javax.jdo.PersistenceManager;
import javax.jdo.Query;

import com.unito.tableplus.server.util.ServiceFactory;
import com.unito.tableplus.shared.model.Wallet;

public class WalletQueries {

	public static Wallet getWallet(Long userKey) {
		PersistenceManager pm = ServiceFactory.getPmfInstance().getPersistenceManager();
		Query query = pm.newQuery(Wallet.class);
		query.setFilter("userKey == param");
		query.declareParameters("String param");
		Wallet wallet = null;
		try {
			@SuppressWarnings("unchecked")
			List<Wallet> results = (List<Wallet>) query.execute(userKey);
			if (results.isEmpty()){
				wallet =  new Wallet();
				wallet.setUserKey(userKey);
			}	
			else
				wallet = pm.detachCopy(results.get(0));
		} catch (Exception e) {
			System.err.println("Something gone wrong querying the wallet: " + e);
		} finally {
			query.closeAll();
			pm.close();
		}
		return wallet;
	}

	public static void storeWallet(Wallet wallet) {
		PersistenceManager pm = ServiceFactory.getPmfInstance().getPersistenceManager();
		try {
			pm.makePersistent(wallet);
		} catch (Exception e) {
			System.err.println("Something gone wrong storing the wallet: " + e);
			e.printStackTrace();
		} finally {
			pm.close();
		}
	}

}
