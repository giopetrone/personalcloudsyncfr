package com.unito.tableplus.server.services;

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
import com.google.gwt.user.server.rpc.RemoteServiceServlet;
import com.unito.tableplus.client.services.TableService;
import com.unito.tableplus.shared.model.Document;
import com.unito.tableplus.shared.model.Table;
import com.unito.tableplus.shared.model.Message;
import com.unito.tableplus.shared.model.User;

public class TableServiceImpl extends RemoteServiceServlet implements
		TableService {

	private static final long serialVersionUID = 1L;

	@Override
	public List<Table> queryTables(List<Long> keys) {
		PersistenceManager pm = PMF.get().getPersistenceManager();
		List<Table> tables = new LinkedList<Table>();
		try {
			for (Long key : keys) {
				Table t = pm.getObjectById(Table.class, key);
				// because of lazy behaviour the BlackBoard must be "touched"
				// for being detached
				t.getBlackBoard();
				tables.add(pm.detachCopy(t));
			}
		} catch (Exception e) {
			System.err.println("There has been an error querying tables: " + e);
		} finally {
			pm.close();
		}
		return tables;
	}

	@Override
	public Long storeTable(Table table) {
		Long key = null;
		PersistenceManager pm = PMF.get().getPersistenceManager();
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

	@Override
	public Table queryTable(Long key) {
		PersistenceManager pm = PMF.get().getPersistenceManager();
		Table detached = null;
		try {
			Table table = pm.getObjectById(Table.class, key);
			if (table == null)
				return table;
			// because of lazy behaviour the BlackBoard must be "touched"
			// for being detached
			table.getBlackBoard();
			detached = pm.detachCopy(table);
		} catch (Exception e) {
			System.err.println("There has been an error querying tables: " + e);
		} finally {
			pm.close();
		}
		return detached;
	}

	@Override
	public void deleteTable(Long key) {
		PersistenceManager pm = PMF.get().getPersistenceManager();
		try {
			Table table = pm.getObjectById(Table.class, key);
			pm.deletePersistentAll(table);
		} catch (Exception e) {
			System.err.println("Something gone wrong deleting the Table: " + e);
		} finally {
			pm.close();
		}
	}

	@Override
	public boolean addMessage(Long key, Message message) {
		PersistenceManager pm = PMF.get().getPersistenceManager();
		Transaction tx = pm.currentTransaction();
		try {
			Table t = pm.getObjectById(Table.class, key);
			if (t == null)
				return false;
			tx.begin();
			t.getBlackBoard().add(message);
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
	
	@Override
	public void removeMessage(String messageKey) {
		PersistenceManager pm = PMF.get().getPersistenceManager();
		try {
			Message message = pm.getObjectById(Message.class, messageKey);
			pm.deletePersistentAll(message);
		} catch (Exception e) {
			System.err
					.println("There has been an error removing message: " + e);
		} finally {
			pm.close();
		}
	}

	@Override
	public boolean clearMessages(Long key) {
		PersistenceManager pm = PMF.get().getPersistenceManager();
		List<Message> blackboard = null;
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

	@Override
	public boolean addDocumentToTable(String DocId, User user, Long tableKey) {

		Table table = this.queryTable(tableKey);

		DocsService client = new DocsService("yourCo-yourAppName-v1");
		client.setAuthSubToken(user.getToken());
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
			List<User> usersToShare_ = (new UserServiceImpl()).queryUsers(table
					.getMembers());

			URL url = new URL("https://docs.google.com/feeds/acl/private/full/"
					+ docToAdd.getResourceId());

			AclFeed aclFeed = client.getFeed(new URL(docToAdd.getAclFeedLink()
					.getHref()), AclFeed.class);

			boolean skip;
			for (User u : usersToShare_) {
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

	@Override
	public boolean docAccessToNewMember(User newMember, Table table) {

		// User tableOwner=new UserServiceImpl().queryUser(table.getOwner());
		//
		// DocsService client = new DocsService("yourCo-yourAppName-v1");
		// client.setAuthSubToken(tableOwner.getToken());
		// client.setProtocolVersion(DocsService.Versions.V2);
		//
		// URL feedUri;
		//
		// try {
		// feedUri = new URL(
		// "https://docs.google.com/feeds/documents/private/full/");
		// DocumentListFeed feed = client.getFeed(feedUri,
		// DocumentListFeed.class);
		//
		// //per ogni documento del tableOwner
		// for (DocumentListEntry entry : feed.getEntries()) {
		//
		// //se questo documento appartiene al gruppo
		// for(String docId:table.getDocuments()){
		// if(entry.getDocId().equals(docId)){
		//
		// URL url = new URL("https://docs.google.com/feeds/acl/private/full/"
		// + entry.getResourceId());
		//
		// AclFeed aclFeed = client.getFeed(new URL(entry.getAclFeedLink()
		// .getHref()), AclFeed.class);
		//
		// //se l'utente non ha già accesso al doc
		// boolean skip;
		// skip = false;
		// for (AclEntry entry_ : aclFeed.getEntries())
		// if (entry_.getScope().getValue().equals(newMember.getEmail()))
		// if (entry_.getRole().getValue().equals("owner")
		// || entry_.getRole().getValue().equals("writer"))
		// skip = true;
		//
		// if (!skip) {
		// AclRole role = new AclRole("writer");
		//
		// AclScope scope = new AclScope(AclScope.Type.USER,
		// newMember.getEmail());
		//
		// AclEntry entry_ = new AclEntry();
		// entry_.setRole(role);
		// entry_.setScope(scope);
		//
		// client.insert(url, entry);
		//
		// System.out.println("L'utente " + newMember.getEmail()
		// + " può adesso scrivere nel doc");
		// }
		// }
		// }
		// }
		// } catch (MalformedURLException e) {
		// e.printStackTrace();
		// System.out.println("uno");
		// } catch (IOException e) {
		// e.printStackTrace();
		// System.out.println("due");
		// } catch (ServiceException e) {
		// e.printStackTrace();
		// System.out.println("tre");
		// }

		storeTable(table);
		new UserServiceImpl().storeUser(newMember);

		return false;
	}

	@Override
	public List<Document> getTableDocuments(Table table) {
		// Auto-generated method stub
		List<Document> tableDocuments = new ArrayList<Document>();

		User tableOwner = new UserServiceImpl().queryUser(table.getOwner());

		// e se il gruppo è appena stato creato e il proprietario non ha
		// token???
		if (tableOwner.getToken() != null)
			for (Document doc : new TokenServiceImpl()
					.getDocumentList(tableOwner.getToken()))
				for (String docId : table.getDocuments())
					if (doc.getDocId().equals(docId))
						tableDocuments.add(doc);

		if (tableDocuments.size() == 0)
			return null;
		else
			return tableDocuments;
	}

	@Override
	public boolean addMemberToTable(Long userKey, Long tableKey) {

		// System.out.println("Aggiunto utente"+userKey +" a gruppo"+ tableKey);

		// recupera gli oggetti gruppo e utente
		Table t = this.queryTable(tableKey);
		User u = new UserServiceImpl().queryUser(userKey);

		// aggiunge il membro al gruppo e lo salva
		t.addMember(u.getKey());
		this.storeTable(t);

		// aggiunge il gruppo all'utente e lo salva
		u.addTable(t.getKey());
		new UserServiceImpl().storeUser(u);

		// fornisco all'utente gli accessi in scrittura a tutti i doc

		return false;
	}

	@Override
	public boolean addHiddenMemberToTable(Long userKey, Long tableKey) {
		// recupera l'oggetto gruppo
		Table t = this.queryTable(tableKey);

		// aggiunge il membro al gruppo e lo salva
		t.addHiddenMember(userKey);
		this.storeTable(t);

		return false;
	}

	@Override
	public boolean removeHiddenMemberFromTable(Long userKey, Long tableKey) {
		// recupera l'oggetto gruppo
		Table t = this.queryTable(tableKey);

		// rimuove l'utente dal gruppo e lo salva
		t.removeHiddenMember(userKey);
		this.storeTable(t);

		return false;
	}

	@Override
	public boolean addSelectivePresenceMemberToTable(Long userKey, Long tableKey) {
		// recupera l'oggetto gruppo
		Table t = this.queryTable(tableKey);

		// aggiunge il membro al gruppo e lo salva
		if (!t.getSelectivePresenceMembers().contains(userKey)) {
			t.addSelectivePresenceMember(userKey);
			this.storeTable(t);
			return true;
		} else
			return false;
	}

	@Override
	public boolean removeSelectivePresenceMemberFromTable(Long userKey,
			Long tableKey) {
		// recupera l'oggetto gruppo
		Table t = this.queryTable(tableKey);

		// System.out
		// .println("removeSelectivePresenceMemberFromTable:\n------ userKey = "
		// + userKey
		// + "\n------ tableKey = "
		// + tableKey
		// + "\n------ table = " + (g!=null?g.getName():"null"));

		// rimuove l'utente dal gruppo e lo salva
		t.removeSelectivePresenceMember(userKey);
		this.storeTable(t);

		return false;
	}

}
