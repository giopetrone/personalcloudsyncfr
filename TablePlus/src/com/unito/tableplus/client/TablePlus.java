package com.unito.tableplus.client;

import java.util.List;

import com.unito.tableplus.client.gui.*;
import com.unito.tableplus.client.services.*;
import com.unito.tableplus.shared.model.*;

import com.extjs.gxt.ui.client.event.ButtonEvent;
import com.extjs.gxt.ui.client.event.SelectionListener;
import com.extjs.gxt.ui.client.widget.Window;
import com.extjs.gxt.ui.client.widget.button.Button;
import com.extjs.gxt.ui.client.widget.layout.FlowLayout;
import com.google.gwt.core.client.EntryPoint;
import com.google.gwt.core.client.GWT;
import com.google.gwt.user.client.rpc.AsyncCallback;

public class TablePlus implements EntryPoint {

	public static DesktopPlus desktop;
	// crea l'utente corrente
	public static User user = new User();

	public static Table personalTable;

	// finestra che compare se accedi al sistema non loggato
	private Window loginWindow = new Window();
	Button loginButton = new Button("Login Google");

	// crea il servizio per il token
	private final TokenServiceAsync tokenService = GWT
			.create(TokenService.class);

	// crea il servizio per l'utente
	private final UserServiceAsync userService = GWT.create(UserService.class);

	// crea il servizio per il group
	private final GroupServiceAsync groupService = GWT
			.create(GroupService.class);

	// crea il servizio per il notification
	protected final NotificationServiceAsync notificationService = GWT
			.create(NotificationService.class);

	private String loginUrl;

	private String logoutUrl;

	private long clientSeqNumber = -1;

	// ******************************************************************************
	// ******************************************************************************
	// ******************************************************************************
	// ******
	// ****** onModuleLoad()
	// ******
	// ******************************************************************************
	// ******************************************************************************
	// ******************************************************************************

	public void onModuleLoad() {

		// verifico se è loggato con google
		// se è loggato carico il desktop vuoto
		// se non è loggato carico la finestra di login
		verifyLoginStatus();

	}

	// ******************************************************************************
	// ******************************************************************************
	// ******************************************************************************
	// ******
	// ****** verifyLoginStatus()
	// ******
	// ******************************************************************************
	// ******************************************************************************
	// ******************************************************************************

	public void verifyLoginStatus() {
		// URL della home
		final String homepageURL;
		if (GWT.getHostPageBaseURL().contains("127.0.0.1"))
			homepageURL = "http://127.0.0.1:8888/TablePlus.html?gwt.codesvr=127.0.0.1:9997";
		else
			homepageURL = GWT.getHostPageBaseURL();

		userService.isLoggedIn(homepageURL, new AsyncCallback<String>() {
			@Override
			public void onFailure(Throwable caught) {
			}

			@Override
			public void onSuccess(String result) {
				if (result.startsWith("y")) {
					logoutUrl = result.substring(1);
					initUser();
				} else if (result.startsWith("n")) {
					loginUrl = result.substring(1);
					loadLoginWindow();
				}
			}
		});
	}

	// ******************************************************************************
	// ******************************************************************************
	// ******************************************************************************
	// ******
	// ****** initUser()
	// ******
	// ******************************************************************************
	// ******************************************************************************
	// ******************************************************************************F

	public void initUser() {
		userService.getCurrentUser(new AsyncCallback<User>() {
			@Override
			public void onFailure(Throwable caught) {
			}

			@Override
			public void onSuccess(User result) {
				// Auto-generated method stub
				user = result;

				// Notifica di utente online
				Notification n = new Notification();
				n.setSenderEmail(user.getEmail());
				n.setSenderKey(user.getKey());
				n.setEventKind("MEMBERONLINE");
				n.setOwningGroups(user.getGroups());
				n.setMemberEmail(user.getEmail());
				throwNotification(n);

				if (user.getToken() != null)
					tokenService.getDocumentList(user.getToken(),
							new AsyncCallback<List<Document>>() {
								public void onFailure(Throwable caught) {

								}

								@Override
								public void onSuccess(List<Document> result) {
									user.setDocuments(result);
									loadActiveDesktop();
								}
							});

				else
				// se nell'url c'è un token
				if (com.google.gwt.user.client.Window.Location.getHref()
						.contains("token="))
					manageNewToken();

				else
					loadActiveDesktop();

			}
		});

	}

	public void throwNotification(Notification notification) {
		notificationService.sendNotification(notification,
				new AsyncCallback<Boolean>() {
					@Override
					public void onFailure(Throwable caught) {
					}

					@Override
					public void onSuccess(Boolean result) {
					}
				});
	}

	// ******************************************************************************
	// ******************************************************************************
	// ******************************************************************************
	// ******
	// ****** manageNewToken()
	// ******
	// ******************************************************************************
	// ******************************************************************************
	// ******************************************************************************

	public void manageNewToken() {
		// -(1)- estrai il token
		String token = com.google.gwt.user.client.Window.Location
				.getParameter("token");

		// -(2)- pruomovi il token a "sessionToken" e lo aggiungi all'utente

		// -(2.a)- il servizio aggiunge il sessionToken alla session
		tokenService.getGdocSessionToken(token, new AsyncCallback<String>() {
			public void onFailure(Throwable caught) {
			}

			@Override
			public void onSuccess(String result) {
				// -(2.b)- poi aggiungiamo sessionToken al wallet
				user.setToken(result);

				tokenService.getDocumentList(result,
						new AsyncCallback<List<Document>>() {
							public void onFailure(Throwable caught) {
							}

							@Override
							public void onSuccess(List<Document> result) {
								userService.storeUser(user,
										new AsyncCallback<Void>() {
											@Override
											public void onFailure(
													Throwable caught) {
											}

											@Override
											public void onSuccess(Void result) {
											}
										});

								user.setDocuments(result);
								loadActiveDesktop();
							}
						});
			}
		});

	}

	// ******************************************************************************
	// ******************************************************************************
	// ******************************************************************************
	// ******
	// ****** loadActiveDesktop()
	// ******
	// ******************************************************************************
	// ******************************************************************************
	// ******************************************************************************

	public void loadActiveDesktop() {

		// crea il desktop standard
		desktop = new DesktopPlus(user, logoutUrl);
		desktop.addFixedShortcuts();

		// crea il personalTable
		personalTable = new Table();

		// carica il personal table
		desktop.loadPersonalTable(personalTable);

		// carica la lista di gruppi dell'utente corrente
		groupService.queryGroups(user.getGroups(),
				new AsyncCallback<List<Group>>() {
					@Override
					public void onFailure(Throwable caught) {
						// Auto-generated method stub
					}

					@Override
					public void onSuccess(List<Group> result) {
						// Auto-generated method stub
						for (Group g : result) {

							Table gt = new Table(g);

							desktop.addGroupTable(gt);
						}
						personalTable.getRightPanel().myGroupsPanel.addData();
						startNotificationListener();
					}
				});

	}

	// ******************************************************************************
	// ******************************************************************************
	// ******************************************************************************
	// ******
	// ****** startNotificationListener()
	// ******
	// ******************************************************************************
	// ******************************************************************************
	// ******************************************************************************

	public void startNotificationListener() {
		// fa partire il listener delle notifiche
		// System.out.println(user.getEmail() + " attende news...");
		notificationService.waitForNotification(user.getGroups(), new Long(
				clientSeqNumber), user.getEmail(),
				new AsyncCallback<List<Notification>>() {

					@Override
					public void onFailure(Throwable caught) {
						// Auto-generated method stub

					}

					@Override
					public void onSuccess(List<Notification> result) {
						clientSeqNumber = result.get(0).getSequenceNumber();
						// if (user.getGroups() != null
						// && user.getGroups().size() > 0)
						// System.out.println("PROVA "
						// + user.getGroups().get(0));

						// il blocco sottostante
						// (1) fa ripartire immediatamente il
						// listener delle modifiche
						// (2) gestisce la nuova notifica ricevuta.
						//
						// L'if serve a invertire le sequenze (1) e (2) perchè,
						// nel caso in cui la notifica riguardi il MIO essere
						// stato aggiunto a un gruppo, prima di far ripartire il
						// listener devo aggiornare le mie sottoscrizioni,
						// aggiungendo questo nuovo gruppo
						if (!(result.get(0).getEventKind()
								.equals("MEMBERGROUPADD") && result.get(0)
								.getMemberEmail().equals(user.getEmail())))
							startNotificationListener();

						manageNotification(result);
					}

				});
	}

	// ******************************************************************************
	// ******************************************************************************
	// ******************************************************************************
	// ******
	// ****** manageNotification()
	// ******
	// ******************************************************************************
	// ******************************************************************************
	// ******************************************************************************

	public void manageNotification(List<Notification> nList) {
		Notification n = nList.get(0);

		if (n.getEventKind().equals("MEMBERONLINE")
				|| n.getEventKind().equals("MEMBEROFFLINE"))
			for (Table t : desktop.getGroupTables())
				for (Long memberGroup : n.getOwningGroups())
					if (t.groupKey.compareTo(memberGroup) == 0)
						t.getRightPanel().membersPanel.refreshMembersTree(n);

		if (n.getEventKind().equals("MEMBERGROUPADD")) {

			for (Table t : desktop.getGroupTables())
				if (t.groupKey.compareTo(n.getGroupKey()) == 0)
					t.getRightPanel().membersPanel.refreshMembersTree(n);

			if (n.getMemberEmail().equals(user.getEmail()))
				invitedToNewGroup(n);
		}

		if (n.getEventKind().equals("MEMBERHIDDEN")
				|| n.getEventKind().equals("MEMBERVISIBLE")) {
			for (Table t : desktop.getGroupTables())
				if (t.groupKey.compareTo(n.getGroupKey()) == 0)
						t.getRightPanel().membersPanel.refreshMembersTree(n);
		}
		
		if (n.getEventKind().equals("SELECTIVEPRESENCEOFF")
				|| n.getEventKind().equals("SELECTIVEPRESENCEON")) {
			for (Table t : desktop.getGroupTables())
				if (t.groupKey.compareTo(n.getGroupKey()) == 0)
						t.getRightPanel().membersPanel.refreshMembersTree(n);
		}

//		System.out.println("\n" + user.getEmail()
//				+ " riceve una notifica:\n------ " + n.getSequenceNumber()
//				+ "\n------ " + n.getEventKind() + "\n------ "
//				+ n.getMemberEmail() + "\n");
	}

	public void invitedToNewGroup(Notification n) {

		// (10)aggiorna l'utente corrente
		userService.queryUser(user.getKey(), new AsyncCallback<User>() {
			@Override
			public void onFailure(Throwable caught) {
			}

			@Override
			public void onSuccess(User result) {
				user = result;
				startNotificationListener();
			}
		});

		// (13)recupera il gruppo
		groupService.queryGroup(n.getGroupKey(), new AsyncCallback<Group>() {
			@Override
			public void onFailure(Throwable caught) {
			}

			@Override
			public void onSuccess(Group result) {
				// (15)crea un grouptable sulla base del gruppo
				Table gt = new Table(result);

				// (20)aggiungi il nuovo table al desktop
				desktop.addGroupTable(gt);

				// (30)aggiorna l'elenco gruppi in personalpanel
				personalTable.getRightPanel().myGroupsPanel
						.addNewGroupToTree(gt);
			}
		});

	}

	// ******************************************************************************
	// ******************************************************************************
	// ******************************************************************************
	// ******
	// ****** loadLoginWindow()
	// ******
	// ******************************************************************************
	// ******************************************************************************
	// ******************************************************************************

	public void loadLoginWindow() {
		desktop = new DesktopPlus(user, logoutUrl);
		desktop.getTaskBar().disable();

		loginButton.addSelectionListener(new SelectionListener<ButtonEvent>() {
			public void componentSelected(ButtonEvent ce) {
				// Azioni da eseguire alla pressione del button
				redirect(loginUrl);
			}
		});

		loginWindow.setHeading("Google Login Window");
		loginWindow.setLayout(new FlowLayout());
		loginWindow.add(loginButton);
		loginWindow.setClosable(false);
		desktop.addWindow(loginWindow);
		loginWindow.show();

	}

	public static native void redirect(String url)
	/*-{
		$wnd.location = url;
	}-*/;
}
