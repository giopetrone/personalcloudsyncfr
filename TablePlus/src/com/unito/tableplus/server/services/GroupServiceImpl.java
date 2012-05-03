package com.unito.tableplus.server.services;

import java.io.IOException;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.ArrayList;
import java.util.Iterator;
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
import com.unito.tableplus.client.services.GroupService;
import com.unito.tableplus.shared.model.Document;
import com.unito.tableplus.shared.model.Group;
import com.unito.tableplus.shared.model.Message;
import com.unito.tableplus.shared.model.User;

public class GroupServiceImpl extends RemoteServiceServlet implements
		GroupService {

	private static final long serialVersionUID = 1L;

	@Override
	public List<Group> queryGroups(List<Long> keys) {
		PersistenceManager pm = PMF.get().getPersistenceManager();
		List<Group> groups = new LinkedList<Group>();
		try {
			for (Long key : keys) {
				Group g = pm.getObjectById(Group.class, key);
				//because of lazy behaviour the BlackBoard must be "touched"
				//for being detached
				g.getBlackBoard();
				groups.add(pm.detachCopy(g));
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
	public Long storeGroupProva(Group group) {
		group.setName(group.getName() + "++");
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
			//because of lazy behaviour the BlackBoard must be "touched"
			//for being detached
			group.getBlackBoard();			
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

	@Override
	public boolean addDocumentToGroup(String DocId, User user, Long groupKey) {

		Group group = this.queryGroup(groupKey);

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
			List<User> usersToShare_ = (new UserServiceImpl()).queryUsers(group
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

		group.addDocument(DocId);

		storeGroup(group);

		return true;
	}

	@Override
	public boolean docAccessToNewMember(User newMember, Group group) {

		// User groupOwner=new UserServiceImpl().queryUser(group.getOwner());
		//
		// DocsService client = new DocsService("yourCo-yourAppName-v1");
		// client.setAuthSubToken(groupOwner.getToken());
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
		// //per ogni documento del groupOwner
		// for (DocumentListEntry entry : feed.getEntries()) {
		//
		// //se questo documento appartiene al gruppo
		// for(String docId:group.getDocuments()){
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

		storeGroup(group);
		new UserServiceImpl().storeUser(newMember);

		return false;
	}

	@Override
	public List<Document> getGroupDocuments(Group group) {
		// Auto-generated method stub
		List<Document> groupDocuments = new ArrayList<Document>();

		User groupOwner = new UserServiceImpl().queryUser(group.getOwner());

		// e se il gruppo è appena stato creato e il proprietario non ha
		// token???
		if (groupOwner.getToken() != null)
			for (Document doc : new TokenServiceImpl()
					.getDocumentList(groupOwner.getToken()))
				for (String docId : group.getDocuments())
					if (doc.getDocId().equals(docId))
						groupDocuments.add(doc);

		if (groupDocuments.size() == 0)
			return null;
		else
			return groupDocuments;
	}

	@Override
	public boolean addMemberToGroup(Long userKey, Long groupKey) {

		// System.out.println("Aggiunto utente"+userKey +" a gruppo"+ groupKey);

		// recupera gli oggetti gruppo e utente
		Group g = this.queryGroup(groupKey);
		User u = new UserServiceImpl().queryUser(userKey);

		// aggiunge il membro al gruppo e lo salva
		g.addMember(u.getKey());
		this.storeGroup(g);

		// aggiunge il gruppo all'utente e lo salva
		u.addGroup(g.getKey());
		new UserServiceImpl().storeUser(u);

		// fornisco all'utente gli accessi in scrittura a tutti i doc

		return false;
	}

	@Override
	public boolean addHiddenMemberToGroup(Long userKey, Long groupKey) {
		// recupera l'oggetto gruppo
		Group g = this.queryGroup(groupKey);

		// aggiunge il membro al gruppo e lo salva
		g.addHiddenMember(userKey);
		this.storeGroup(g);

		return false;
	}

	@Override
	public boolean removeHiddenMemberFromGroup(Long userKey, Long groupKey) {
		// recupera l'oggetto gruppo
		Group g = this.queryGroup(groupKey);

		// rimuove l'utente dal gruppo e lo salva
		g.removeHiddenMember(userKey);
		this.storeGroup(g);

		return false;
	}

	@Override
	public boolean addSelectivePresenceMemberToGroup(Long userKey, Long groupKey) {
		// recupera l'oggetto gruppo
		Group g = this.queryGroup(groupKey);

		// aggiunge il membro al gruppo e lo salva
		if (!g.getSelectivePresenceMembers().contains(userKey)) {
			g.addSelectivePresenceMember(userKey);
			this.storeGroup(g);
			return true;
		} else
			return false;
	}

	@Override
	public boolean removeSelectivePresenceMemberFromGroup(Long userKey,
			Long groupKey) {
		// recupera l'oggetto gruppo
		Group g = this.queryGroup(groupKey);

//		System.out
//				.println("removeSelectivePresenceMemberFromGroup:\n------ userKey = "
//						+ userKey
//						+ "\n------ groupKey = "
//						+ groupKey
//						+ "\n------ group = " + (g!=null?g.getName():"null"));

		// rimuove l'utente dal gruppo e lo salva
		g.removeSelectivePresenceMember(userKey);
		this.storeGroup(g);

		return false;
	}

}
