package com.unito.tableplus.server;

import java.io.IOException;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;

import javax.jdo.PersistenceManager;
import javax.jdo.Transaction;

import com.google.gdata.client.docs.DocsService;
import com.google.gdata.data.acl.AclEntry;
import com.google.gdata.data.acl.AclFeed;
import com.google.gdata.data.acl.AclRole;
import com.google.gdata.data.acl.AclScope;
import com.google.gdata.data.docs.DocumentListEntry;
import com.google.gdata.data.docs.DocumentListFeed;
import com.google.gdata.util.ServiceException;
import com.unito.tableplus.server.services.DriveServiceImpl;
import com.unito.tableplus.shared.model.DriveFile;
import com.unito.tableplus.shared.model.BlackBoardMessage;
import com.unito.tableplus.shared.model.Table;
import com.unito.tableplus.shared.model.User;
import com.unito.tableplus.shared.model.Wallet;

public class TableQueries {

	public static List<Table> queryTables(List<Long> keys) {
		PersistenceManager pm = ServiceFactory.getPmfInstance()
				.getPersistenceManager();
		List<Table> tables = new LinkedList<Table>();
		try {
			for (Long key : keys) {
				Table t = pm.getObjectById(Table.class, key);
				tables.add(pm.detachCopy(t));
			}
		} catch (Exception e) {
			System.err.println("There has been an error querying tables: " + e);
		} finally {
			pm.close();
		}
		return tables;
	}

	public static Long storeTable(Table table) {
		Long key = null;
		PersistenceManager pm = ServiceFactory.getPmfInstance()
				.getPersistenceManager();
		try {
			pm.makePersistent(table);
			key = table.getKey();
		} catch (Exception e) {
			System.err.println("There has been an error storing the table: "
					+ e);
		} finally {
			pm.close();
		}
		return key;
	}

	public static Table queryTable(Long key) {
		PersistenceManager pm = ServiceFactory.getPmfInstance()
				.getPersistenceManager();
		Table detached = null;
		try {
			Table table = pm.getObjectById(Table.class, key);
			if (table == null)
				return table;
			detached = pm.detachCopy(table);
		} catch (Exception e) {
			System.err.println("There has been an error querying tables: " + e);
		} finally {
			pm.close();
		}
		return detached;
	}

	public static void deleteTable(Long key) {
		PersistenceManager pm = ServiceFactory.getPmfInstance()
				.getPersistenceManager();
		try {
			Table table = pm.getObjectById(Table.class, key);
			pm.deletePersistentAll(table);
		} catch (Exception e) {
			System.err.println("Something gone wrong deleting the Table: " + e);
		} finally {
			pm.close();
		}
	}

	public static boolean addBlackBoardMessage(Long tableKey,
			BlackBoardMessage bbMessage) {
		PersistenceManager pm = ServiceFactory.getPmfInstance()
				.getPersistenceManager();
		Transaction tx = pm.currentTransaction();
		try {
			Table t = pm.getObjectById(Table.class, tableKey);
			if (t == null)
				return false;
			tx.begin();
			t.getBlackBoard().add(bbMessage);
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

	public static List<BlackBoardMessage> getBlackBoardMessages(Long tableKey) {
		PersistenceManager pm = ServiceFactory.getPmfInstance()
				.getPersistenceManager();
		Table detached = null;
		try {
			Table table = pm.getObjectById(Table.class, tableKey);
			if (table == null)
				return null;
			table.getBlackBoard(); // needed because of lazy behaviour
			detached = pm.detachCopy(table);
		} catch (Exception e) {
			System.err.println("Error querying blackboard messages");
		} finally {
			pm.close();
		}
		return detached.getBlackBoard();
	}

	public static void removeMessage(String messageKey) {
		PersistenceManager pm = ServiceFactory.getPmfInstance()
				.getPersistenceManager();
		try {
			BlackBoardMessage bbMessage = pm.getObjectById(
					BlackBoardMessage.class, messageKey);
			pm.deletePersistentAll(bbMessage);
		} catch (Exception e) {
			System.err
					.println("There has been an error removing message: " + e);
		} finally {
			pm.close();
		}
	}

	public static boolean clearMessages(Long key) {
		PersistenceManager pm = ServiceFactory.getPmfInstance()
				.getPersistenceManager();
		List<BlackBoardMessage> blackboard = null;
		try {
			Table table = pm.getObjectById(Table.class, key);
			if (table == null)
				return false;
			blackboard = table.getBlackBoard();
			blackboard.clear();
		} catch (Exception e) {
			System.err.println("There has been an error clearing messages: "
					+ e);
		} finally {
			pm.close();
		}
		return true;
	}

	public static boolean addResource(String DocId, User user, Long tableKey) {

		Table table = queryTable(tableKey);

		DocsService client = new DocsService("TablePlus");
		Wallet wallet = WalletQueries.getWallet(user.getKey());
		client.setAuthSubToken(wallet.getDriveToken());
		client.setProtocolVersion(DocsService.Versions.V2);

		URL feedUri;

		try {
			feedUri = new URL(
					"https://docs.google.com/feeds/documents/private/full/");
			DocumentListFeed feed = client.getFeed(feedUri,
					DocumentListFeed.class);

			DocumentListEntry docToAdd = null;
			// System.out.println("DocId = " + DocId);
			for (DocumentListEntry entry : feed.getEntries()) {
				// System.out.println("entry.getDocId() = " + entry.getDocId());
				if (entry.getDocId().equals(DocId))
					docToAdd = entry;
			}

			if (docToAdd == null)
				return false;

			// Ho recuperato il doc da condividere, devo recuperare gli utenti
			// del gruppo con cui condividere il doc
			List<User> usersToShare = UserQueries
					.queryUsers(table.getMembers());

			URL url = new URL("https://docs.google.com/feeds/acl/private/full/"
					+ docToAdd.getResourceId());

			AclFeed aclFeed = client.getFeed(new URL(docToAdd.getAclFeedLink()
					.getHref()), AclFeed.class);

			boolean skip;
			for (User u : usersToShare) {
				skip = false;

				for (AclEntry entry : aclFeed.getEntries())
					if (entry.getScope().getValue().equals(u.getEmail()))
						if (entry.getRole().getValue().equals("owner")
								|| entry.getRole().getValue().equals("writer"))
							skip = true;

				if (!skip) {
					AclRole role = new AclRole("writer");

					AclScope scope = new AclScope(AclScope.Type.USER,
							u.getEmail());

					AclEntry entry = new AclEntry();
					entry.setRole(role);
					entry.setScope(scope);

					client.insert(url, entry);

				}
			}

		} catch (MalformedURLException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		} catch (ServiceException e) {
			e.printStackTrace();
		}

		table.addDocument(DocId);

		storeTable(table);

		return true;
	}

	public static void docAccessToNewMember(User newMember, Table table) {
		storeTable(table);
		UserQueries.storeUser(newMember);
	}

	public static List<DriveFile> getTableDriveFiles(Table table) {
		List<DriveFile> tableDriveFiles = new ArrayList<DriveFile>();

		//For Drive it's fine wathever table member
		Wallet wallet = WalletQueries.getWallet(table.getOwner());
		if (wallet.getDriveToken() != null)
			for (DriveFile doc : DriveServiceImpl.getDriveFileList(wallet
					.getDriveToken()))
				for (String docId : table.getDocuments())
					if (doc.getDocId().equals(docId))
						tableDriveFiles.add(doc);

		if (tableDriveFiles.size() == 0)
			return null;
		else
			return tableDriveFiles;
	}

	public static void addMember(Long userKey, Long tableKey) {

		// recupera gli oggetti gruppo e utente
		Table t = queryTable(tableKey);
		User u = UserQueries.queryUser(userKey);

		// aggiunge il membro al gruppo e lo salva
		t.addMember(u.getKey());
		storeTable(t);

		// aggiunge il gruppo all'utente e lo salva
		u.addTable(t.getKey());
		UserQueries.storeUser(u);

		// fornisco all'utente gli accessi in scrittura a tutti i doc
	}
}
