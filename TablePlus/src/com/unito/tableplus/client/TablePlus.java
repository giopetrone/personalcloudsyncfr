package com.unito.tableplus.client;

import java.util.List;

import com.unito.tableplus.client.gui.*;
import com.unito.tableplus.client.services.*;
import com.unito.tableplus.shared.model.*;

import com.extjs.gxt.ui.client.event.ButtonEvent;
import com.extjs.gxt.ui.client.event.SelectionListener;
import com.extjs.gxt.ui.client.widget.Info;
import com.extjs.gxt.ui.client.widget.Window;
import com.extjs.gxt.ui.client.widget.button.Button;
import com.extjs.gxt.ui.client.widget.layout.FlowLayout;
import com.google.gwt.appengine.channel.client.ChannelFactory;
import com.google.gwt.core.client.EntryPoint;
import com.google.gwt.core.client.GWT;
import com.google.gwt.event.logical.shared.CloseEvent;
import com.google.gwt.event.logical.shared.CloseHandler;
import com.google.gwt.user.client.Timer;
import com.google.gwt.user.client.rpc.AsyncCallback;

public class TablePlus implements EntryPoint {

	public static DesktopPlus desktop;
	// crea l'utente corrente
	public static User user = new User();

	public static TableUI personalTable;

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

	// crea il servizio per la chat
	protected final ChatServiceAsync chatService = GWT
			.create(ChatService.class);

	private String loginUrl;

	private String logoutUrl;

	private long clientSeqNumber = -1;

	static boolean legalPageChange = false;

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

//		System.out.println("GWT.getHostPageBaseURL() = "
//				+ GWT.getHostPageBaseURL());
//		System.out
//				.println("GWT.getModuleBaseURL() = " + GWT.getModuleBaseURL());
//		System.out
//				.println("com.google.gwt.user.client.Window.Location.getHref() = "
//						+ com.google.gwt.user.client.Window.Location.getHref());

		com.google.gwt.user.client.Window
				.addCloseHandler(new CloseHandler<com.google.gwt.user.client.Window>() {

					@Override
					public void onClose(
							CloseEvent<com.google.gwt.user.client.Window> event) {
						// Auto-generated method stub
						if (!legalPageChange) {
							System.out.println("ABBIAMO CHIUSO I BATTENTI");
							// redirect(logoutUrl);
							// desktop.logoutUser();
							// notifica di un utente offline
							Notification n = new Notification();
							n.setSenderEmail(user.getEmail());
							n.setSenderKey(user.getKey());
							n.setEventKind("MEMBEROFFLINE");
							n.setOwningGroups(user.getGroups());
							n.setMemberEmail(user.getEmail());
							throwNotification(n);

							user.setOnline(false);
							userService.storeUser(user,
									new AsyncCallback<Void>() {
										@Override
										public void onFailure(Throwable caught) {
										}

										@Override
										public void onSuccess(Void result) {
										}
									});
						}
					}

				});

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
		// if (GWT.getHostPageBaseURL().contains("127.0.0.1"))
		// homepageURL =
		// "http://127.0.0.1:8888/TablePlus.html?gwt.codesvr=127.0.0.1:9997";
		// else
		// homepageURL = GWT.getHostPageBaseURL();

		homepageURL = com.google.gwt.user.client.Window.Location.getHref();

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

	Long tmp;

	public void loadActiveDesktop() {

		// controlla se nell'url c'è un codice di invito, e procede di
		// conseguenza...
		if (!com.google.gwt.user.client.Window.Location.getHref().contains(
				"code="))
			loadActiveDesktop2();
		else {
			// recupera il codice dall'url
			String code = com.google.gwt.user.client.Window.Location
					.getParameter("code");

			notificationService.getInvitedGroupKey(code, user.getEmail(),
					new AsyncCallback<Long>() {

						@Override
						public void onFailure(Throwable caught) {
							// Auto-generated method stub

						}

						@Override
						public void onSuccess(Long result) {

							// se al codice fornito corrisponde un invito
							if (result > 0) {

								tmp = result;
								// Auto-generated method stub
								groupService.addMemberToGroup(user.getKey(),
										result, new AsyncCallback<Boolean>() {

											@Override
											public void onFailure(
													Throwable caught) {
												// Auto-generated method stub

											}

											@Override
											public void onSuccess(Boolean result) {

												// crea una notifica
												Notification n = new Notification();
												n.setSenderEmail(TablePlus.user
														.getEmail());
												n.setSenderKey(TablePlus.user
														.getKey());
												n.setEventKind("MEMBERGROUPADD");
												n.setMemberEmail(user
														.getEmail());
												n.setGroupKey(tmp);
												n.setStatus(user.isOnline() ? "ONLINE"
														: "OFFLINE");

												// la spedisce
												throwNotification(n);

												// (10)aggiorna l'utente
												// corrente
												userService.queryUser(
														user.getKey(),
														new AsyncCallback<User>() {
															@Override
															public void onFailure(
																	Throwable caught) {
															}

															@Override
															public void onSuccess(
																	User result) {
																user=result;
																loadActiveDesktop2();
															}
														});

											}

										});
							} else
								loadActiveDesktop2();
						}

					});
		}

	}

	public void loadActiveDesktop2() {
		timer = new Timer() {
			@Override
			public void run() {
				Notification n = new Notification();
				n.setEventKind("ANSWERNOW");
				n.setSenderEmail(user.getEmail());
				throwNotification(n);
			}
		};

		// crea il desktop standard
		desktop = new DesktopPlus(user, logoutUrl);
		desktop.addFixedShortcuts();

		// crea il personalTable
		personalTable = new TableUI();

		// carica il personal table
		desktop.loadPersonalTable(personalTable);

		caricaListaGruppi();

	}

	public void caricaListaGruppi() {
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

							TableUI gt = new TableUI(g);

							desktop.addGroupTable(gt);
						}
						personalTable.getRightPanel().myGroupsPanel.addData();
						startNotificationListener();

						// vediamo di gestire il canale di comunicazione
						// delle
						// chat...
						// Per il momento, per semplicità, facciamo che ogni
						// utente che logga apre un canale
						// per la chat. Quindi in questo momento io ho un
						// utente
						// collegato e ho creato
						// tutti i suoi tavoli
						startCommunicationChannel();
					}
				});
	}

	public void startCommunicationChannel() {

		// utilizzo come id del mittente l'email dell'utente loggato in questo
		// istante
		// sappiamo che l'email è un identificatore univoco per gli utenti,
		// quindi
		// va bene
		String senderID = user.getEmail();
		System.out.println("USEREMAIL: " + user.getEmail());

		chatService.createChannel(senderID, new AsyncCallback<String>() {
			// senderID deve essere unico per ogni utente (email, userID...)

			@Override
			public void onFailure(Throwable caught) {
				// Fallimento nella creazione del canale sul server
				System.out.println("Failed gainind channel API token");// Failed
																		// creating
																		// channel");
			}

			@Override
			public void onSuccess(String token) {
				// Se il canale viene creato con successo sul server
				// Il token è restituito dal server e deve essere mantenuto
				// segreto

				ChannelFactory.createChannel(token,
						new ChannelCreatedCallbackImpl(messaggioDiProva));
				// obj è l’oggetto in cui verranno visualizzati i messaggi
				// ricevuti
				//

				// chatService
				// .getUsersList(new AsyncCallback<List<String>>() {
				// @Override
				// public void onFailure(
				// Throwable caught) {
				// System.out.println("Could not get users list: "
				// + caught.getMessage());
				// }
				//
				// @Override
				// public void onSuccess(
				// List<String> result) {
				// System.out.println("Users' number is... "+result.size());
				// System.out.println("First user is... " + result.get(0));
				// }
				// });

				/*
				 * System.out.println("ChannelAPI token received");
				 * System.out.println("USEREMAIL: "+user.getEmail());
				 */
				// testMessage();
			}
		});

	}

	String messaggioDiProva = "";

	public void testMessage() {
		System.out.println("USEREMAIL: " + user.getEmail());

		chatService.sendMessage(user.getEmail(), "" + user.getEmail()
				+ " saluta tutti!", MessageType.GENERIC,
				new AsyncCallback<String>() {

					@Override
					public void onFailure(Throwable caught) {
						//  Auto-generated method stub
						System.out
								.println("chatService.sendMessage() failure da "
										+ user.getEmail());
					}

					@Override
					public void onSuccess(String result) {
						//  Auto-generated method stub
						System.out
								.println("chatService.sendMessage() happy ending da "
										+ user.getEmail() + ": " + result);
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

	Timer timer = null;

	public void startNotificationListener() {

		if (timer != null) {
			timer.cancel();
			timer.schedule(40000);
		}

		// fa partire il listener delle notifiche
		// System.out.println(user.getEmail() + " attende news...");
		try {
			notificationService.waitForNotification(user.getGroups(), new Long(
					clientSeqNumber), user.getEmail(),
					new AsyncCallback<List<Notification>>() {

						@Override
						public void onFailure(Throwable caught) {
							// Auto-generated method stub

						}

						@Override
						public void onSuccess(List<Notification> result) {
							if (result != null) {
								clientSeqNumber = result.get(0)
										.getSequenceNumber();
								// if (user.getGroups() != null
								// && user.getGroups().size() > 0)
								// System.out.println("PROVA "
								// + user.getGroups().get(0));

								// il blocco sottostante
								// (1) fa ripartire immediatamente il
								// listener delle modifiche
								// (2) gestisce la nuova notifica ricevuta.
								//
								// L'if serve a invertire le sequenze (1) e (2)
								// perchè,
								// nel caso in cui la notifica riguardi il MIO
								// essere
								// stato aggiunto a un gruppo, prima di far
								// ripartire il
								// listener devo aggiornare le mie
								// sottoscrizioni,
								// aggiungendo questo nuovo gruppo
								if (!(result.get(0).getEventKind()
										.equals("MEMBERGROUPADD") && result
										.get(0).getMemberEmail()
										.equals(user.getEmail())))
									startNotificationListener();

								manageNotification(result);
							}
						}

					});
		} catch (Exception e) {
			System.out.println("CATCH 1");
		}
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

		Info.display("Notification Received",
				n.getEventKind() + " from " + n.getSenderEmail());

		if (// n.getEventKind().equals("MEMBERONLINE")||
		n.getEventKind().equals("MEMBEROFFLINE")) {
			for (TableUI t : desktop.getGroupTables())
				for (Long memberGroup : n.getOwningGroups())
					if (t.groupKey.compareTo(memberGroup) == 0)
						t.getRightPanel().membersPanel.refreshMembersTree(n);
		}

		if (n.getEventKind().equals("MEMBERGROUPADD")) {

			for (TableUI t : desktop.getGroupTables())
				if (t.groupKey.compareTo(n.getGroupKey()) == 0)
					t.getRightPanel().membersPanel.refreshMembersTree(n);

			if (n.getMemberEmail().equals(user.getEmail()))
				invitedToNewGroup(n);
		}

		if (n.getEventKind().equals("MEMBERHIDDEN")
				|| n.getEventKind().equals("MEMBERVISIBLE")) {
			for (TableUI t : desktop.getGroupTables())
				if (t.groupKey.compareTo(n.getGroupKey()) == 0)
					t.getRightPanel().membersPanel.refreshMembersTree(n);
		}

		if (n.getEventKind().equals("SELECTIVEPRESENCEOFF")
				|| n.getEventKind().equals("SELECTIVEPRESENCEON")) {
			for (TableUI t : desktop.getGroupTables())
				if (t.groupKey.compareTo(n.getGroupKey()) == 0)
					t.getRightPanel().membersPanel.refreshMembersTree(n);
		}

		// System.out.println("\n" + user.getEmail()
		// + " riceve una notifica:\n------ " + n.getSequenceNumber()
		// + "\n------ " + n.getEventKind() + "\n------ "
		// + n.getMemberEmail() + "\n");
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
				TableUI gt = new TableUI(result);

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

	public static void redirect(String url) {
		legalPageChange = true;
		redirect_(url);
	}

	public static native void redirect_(String url)
	/*-{
		$wnd.location = url;
	}-*/;

}
