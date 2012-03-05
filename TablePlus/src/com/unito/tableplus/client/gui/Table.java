package com.unito.tableplus.client.gui;

import java.util.ArrayList;
import java.util.List;

import com.extjs.gxt.desktop.client.Shortcut;
import com.google.gwt.core.client.GWT;
import com.google.gwt.user.client.Timer;
import com.google.gwt.user.client.rpc.AsyncCallback;
import com.unito.tableplus.client.TablePlus;
import com.unito.tableplus.client.gui.windows.GroupResourcesWindow;
import com.unito.tableplus.client.gui.windows.WindowPlus;
import com.unito.tableplus.client.services.GroupService;
import com.unito.tableplus.client.services.GroupServiceAsync;
import com.unito.tableplus.client.services.NotificationService;
import com.unito.tableplus.client.services.NotificationServiceAsync;
import com.unito.tableplus.client.services.UserService;
import com.unito.tableplus.client.services.UserServiceAsync;
import com.unito.tableplus.shared.model.Document;
import com.unito.tableplus.shared.model.Group;
import com.unito.tableplus.shared.model.Notification;
import com.unito.tableplus.shared.model.User;

public class Table {

	// componenti
	public RightPanel rightPanel;
	private List<WindowPlus> windows = new ArrayList<WindowPlus>();
	private List<Shortcut> shortcuts = new ArrayList<Shortcut>();

	// informazioni
	public String groupName;
	public Long groupKey;
	public List<String> onlineMembersEmail = new ArrayList<String>();
	public List<String> offlineMembersEmail = new ArrayList<String>();
	public List<String> hiddenMembersEmail = new ArrayList<String>();
	public List<String> selectivePresenceMembers = new ArrayList<String>();
	public List<Document> googleDocuments = new ArrayList<Document>();

	// servizi
	public final UserServiceAsync userService = GWT.create(UserService.class);
	public final GroupServiceAsync groupService = GWT
			.create(GroupService.class);
	public final NotificationServiceAsync notificationService = GWT
			.create(NotificationService.class);

	// altro
	public Timer timer = null;
	public boolean selectivePresence;
	public boolean hiddenUser = false;

	/**
	 * Costruttore per personal table
	 * 
	 * @return void
	 */

	public Table() {
		this.rightPanel = new RightPanel(this, false);
	}

	/**
	 * Costruttore per group table
	 * 
	 * @return void
	 */

	public Table(Group group) {
		this.groupName = group.getName();
		this.groupKey = group.getKey();
		this.rightPanel = new RightPanel(this, true);

		// -(1)- crea delle finestre; credo che di default, una volta
		// aggiunte al desktop, siano inizialmente invisibili
		WindowPlus groupResourcesWindow = new GroupResourcesWindow();// createGridWindow();
		addWindow(groupResourcesWindow);

		// group resources
		Shortcut s = new Shortcut();
		s.setText("Group Resources");
		s.setId("groupresources-win-shortcut");
		s.setData("window", groupResourcesWindow);
		this.addShortcut(s);

		createMembersList(group);
		createGoogleDocsList(group);

	}

	/**
	 * Crea la lista dei membri online/offline
	 * 
	 * @return void
	 */

	public void createMembersList(Group group_) {
		final Group group = group_;
		userService.queryUsers(group.getMembers(),
				new AsyncCallback<List<User>>() {
					@Override
					public void onFailure(Throwable caught) {
					}

					@Override
					public void onSuccess(List<User> result) {
						if (result != null)
							// controllo ogni utente per decidere se inserirlo
							// nella lista di onlineMembers o offlineMembers
							for (User u : result) {

								// (10) nella lista degli ONLINE ci vanno:
								// --- membri online
								// --- && non nascosti
								// --- && con presenceSelective

								if (u.isOnline()
										&& !(group.getHiddenMembers()
												.contains(u.getKey()))
										&& group.getSelectivePresenceMembers()
												.contains(u.getKey()))
									onlineMembersEmail.add(u.getEmail());

								// (20) altrimenti vanno nella lista degli
								// OFFLINE

								else
									offlineMembersEmail.add(u.getEmail());

								// (30) aggiungo i membri invisibili a
								// hiddenMembers
								if (group.getHiddenMembers().contains(
										u.getKey()))
									hiddenMembersEmail.add(u.getEmail());

								// (40) aggiungo i membri con presenceSelective
								// a
								// selectivePresenceMembers
								if (group.getSelectivePresenceMembers()
										.contains(u.getKey()))
									selectivePresenceMembers.add(u.getEmail());
							}
						// setto il togglebutton pressato o meno
						if (hiddenMembersEmail.contains(TablePlus.user
								.getEmail()))
							hiddenUser = true;

						rightPanel.membersPanel.setHidden.toggle(hiddenUser);
						rightPanel.membersPanel.addData();
					}
				});
	}

	/**
	 * Crea la lista dei google documents del gruppo
	 * 
	 * @return void
	 */

	public void createGoogleDocsList(Group group) {
		groupService.getGroupDocuments(group,
				new AsyncCallback<List<Document>>() {
					@Override
					public void onFailure(Throwable caught) {
					}

					@Override
					public void onSuccess(List<Document> result) {
						googleDocuments = result;

						rightPanel.groupResourcesPanel.addData();
					}
				});
	}

	/**
	 * Gestisce il passaggio offline per la presence selettiva
	 * 
	 * @return void
	 */

	public void selectivePresenceOff() {
		// (10) aggiorno il flag
		selectivePresence = false;

		// (20) aggiorno l'oggetto Group nel DB
		groupService.removeSelectivePresenceMemberFromGroup(
				TablePlus.user.getKey(), groupKey,
				new AsyncCallback<Boolean>() {
					@Override
					public void onFailure(Throwable caught) {
					}

					@Override
					public void onSuccess(Boolean result) {
					}
				});

		// (30) lancio la notifica
		Notification n = new Notification();
		n.setEventKind("SELECTIVEPRESENCEOFF");
		n.setSenderEmail(TablePlus.user.getEmail());
		n.setMemberEmail(TablePlus.user.getEmail());
		n.setSenderKey(TablePlus.user.getKey());
		n.setGroupKey(groupKey);
		throwNotification(n);
	}

	/**
	 * Gestisce il passaggio online per la presence selettiva
	 * 
	 * @return void
	 */

	public void selectivePresenceOn() {
		// System.out.println("Selective presence on");

		// (10) aggiorno il flag
		selectivePresence = true;

		// (20) aggiorno l'oggetto Group nel DB
		groupService.addSelectivePresenceMemberToGroup(TablePlus.user.getKey(),
				groupKey, new AsyncCallback<Boolean>() {
					@Override
					public void onFailure(Throwable caught) {
					}

					@Override
					public void onSuccess(Boolean result) {
					}
				});

		// (30) lancio la notifica
		Notification n = new Notification();
		n.setEventKind("SELECTIVEPRESENCEON");
		n.setSenderEmail(TablePlus.user.getEmail());
		n.setMemberEmail(TablePlus.user.getEmail());
		n.setSenderKey(TablePlus.user.getKey());
		n.setGroupKey(groupKey);
		throwNotification(n);
	}

	/**
	 * Lancia una notifica
	 * 
	 * @return void
	 */

	public void throwNotification(Notification notification) {
		notificationService.sendNotification(notification,
				new AsyncCallback<Boolean>() {

					@Override
					public void onFailure(Throwable caught) {
						// Auto-generated method stub

					}

					@Override
					public void onSuccess(Boolean result) {
						// Auto-generated method stub

					}

				});
	}

	/**
	 * Vari setters e getters
	 * 
	 * @return
	 */

	public RightPanel getRightPanel() {
		return rightPanel;
	}

	public void setRightPanel(RightPanel rightPanel) {
		this.rightPanel = rightPanel;
	}

	public List<WindowPlus> getWindows() {
		return windows;
	}

	public void setWindows(List<WindowPlus> windows) {
		this.windows = windows;
	}

	public void addWindow(WindowPlus wp) {
		this.windows.add(wp);
	}

	public List<Shortcut> getShortcuts() {
		return shortcuts;
	}

	public void setShortcuts(List<Shortcut> shortcuts) {
		this.shortcuts = shortcuts;
	}

	public void addShortcut(Shortcut s) {
		this.shortcuts.add(s);
	}

}
