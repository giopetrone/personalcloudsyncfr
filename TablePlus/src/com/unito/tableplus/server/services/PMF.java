package com.unito.tableplus.server.services;

import javax.jdo.JDOHelper;
import javax.jdo.PersistenceManagerFactory;

public final class PMF {
	private static final PersistenceManagerFactory pmfInstance = JDOHelper
			.getPersistenceManagerFactory("transactions-optional");

	protected static PersistenceManagerFactory get() {
		return pmfInstance;
	}
}