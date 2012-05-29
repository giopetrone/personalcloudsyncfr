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

	// crea il servizio per il table
	private final TableServiceAsync tableService = GWT
			.create(TableService.class);

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

	/**
	 * E' il primo metodo che viene eseguito. 
	 * Aggiunge alla pagina un "CloseHandler" che gestisce la chiusura del browser (o di un tab) 
	 * da parte dell'utente: se si verifica uno dei due eventi, setta l'utente corrente OFFLINE 
	 * nel DB.
	 * 
	 */

	public void onModuleLoad() {

		com.google.gwt.user.client.Window
				.addCloseHandler(new CloseHandler<com.google.gwt.user.client.Window>() {

					//sequenza di azioni da eseguire alla chiusura della pagina
					@Override
					public void onClose(
							CloseEvent<com.google.gwt.user.client.Window> event) {

						if (!legalPageChange) {
							
							// invio notifica di utente offline
							Notification n = new Notification();
							n.setSenderEmail(user.getEmail());
							n.setSenderKey(user.getKey());
							n.setEventKind("MEMBEROFFLINE");
							n.setOwningTables(user.getTables());
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

		// verifico lo stato di login dell'utente
		verifyLoginStatus();

	}

	/**
	 * Verifica se l'utente che ha aperto la pagina è loggato con google: 
     * - se è loggato viene caricato tutto l'ambiente e visualizzato il personal table 
     * - se non è loggato carico la finestra di login
	 */

	public void verifyLoginStatus() {
		// URL della home
		final String homepageURL;
		
		homepageURL = com.google.gwt.user.client.Window.Location.getHref();

		userService.isLoggedIn(homepageURL, new AsyncCallback<String>() {
			@Override
			public void onFailure(Throwable caught) {
			}

			@Override
			public void onSuccess(String result) {
				// se l'utente risulta loggato, viene restituito l'url di logout, 
				// con un carattere "y" aggiunto come prefisso
				if (result.startsWith("y")) {
					logoutUrl = result.substring(1);
					initUser();
				}
				// se l'utente risulta non loggato, viene restituito l'url di login, 
				// con un carattere "n" aggiunto come prefisso
				else if (result.startsWith("n")) {
					loginUrl = result.substring(1);
					loadLoginWindow();
				}
			}
		});
	}

	/**
	 * Questo metodo viene eseguito se l'utente risulta loggato (nel nostro sistema) con google.
	 * Si occupa di istanziare l'oggetto "user", che rappresenta l'utente corrente:
	 *  - viene inviata la notifica di utente online
	 *  - se del DB abbiamo già un suo token, recuperioamo i suoi Google Docs
	 *  - se nell'URL corrente c'è un token, lo recupera e lo gestisce tramite manageNewToken();
	 * 
	 */
	
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
				n.setOwningTables(user.getTables());
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
									manageInvitation();
								}
							});

				else
				// se nell'url c'è un token
				if (com.google.gwt.user.client.Window.Location.getHref()
						.contains("token="))
					manageNewToken();

				else
					manageInvitation();

			}
		});

	}
	
	/**
	 * Invia una notifica
	 * 
	 * @param notification
	 */

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

	/**
	 * Nel caso in cui venga rilevata la presenza del parametro "token" nell'url,
	 * lo recupera e lo memorizza nel DB
	 */

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
								manageInvitation();
							}
						});
			}
		});

	}

	/**
	 * Controlla che l'URL corrente contenga il parametro "code", che identifica un invito 
	 * ad un table (tramite link ricevuto via email). Se è presente, aggiunge il table 
	 * all'utente corrente ed elimina la notifica dal DB.
	 * 
	 */

	Long tmp;

	public void manageInvitation() {

		// controlla se nell'url c'è un codice di invito, e procede di
		// conseguenza...
		if (!com.google.gwt.user.client.Window.Location.getHref().contains(
				"code="))
			loadActiveDesktop();
		else {
			// recupera il codice dall'url
			String code = com.google.gwt.user.client.Window.Location
					.getParameter("code");

			notificationService.getInvitedTableKey(code, user.getEmail(),
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
								tableService.addMemberToTable(user.getKey(),
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
												n.setEventKind("MEMBERTABLEADD");
												n.setMemberEmail(user
														.getEmail());
												n.setTableKey(tmp);
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
																loadActiveDesktop();
															}
														});

											}

										});
							} else
								loadActiveDesktop();
						}

					});
		}

	}
	
	/**
	 * Inizia a preparare l'ambiente. Istanzia il timer delle notifiche: 
	 * eviterà che una richiesta client -> server impieghi più di 60 secondi a ricevere risposta.
	 * 
	 */

	public void loadActiveDesktop() {
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

		loadTablesList();

	}
	
	/**
	 * Carica i tavoli degli utenti: per ognuno crea un oggetto tableUI. 
	 */

	public void loadTablesList() {
		// carica la lista di gruppi dell'utente corrente
		tableService.queryTables(user.getTables(),
				new AsyncCallback<List<Table>>() {
					@Override
					public void onFailure(Throwable caught) {
						// Auto-generated method stub
					}

					@Override
					public void onSuccess(List<Table> result) {
						// Auto-generated method stub
						for (Table t : result) {

							TableUI gt = new TableUI(t);

							desktop.addTable(gt);
						}
						personalTable.getRightPanel().myTablesPanel.addData();
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
	
	/**
	 * avvia il canale di comunicazione per la chat
	 */

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

			}
		});

	}

	String messaggioDiProva = "";

	

	Timer timer = null;
	
	/**
	 * Avvia il listener delle notifiche
	 * 
	 */

	public void startNotificationListener() {

		if (timer != null) {
			timer.cancel();
			timer.schedule(40000);
		}

		// fa partire il listener delle notifiche
		try {
			notificationService.waitForNotification(user.getTables(), new Long(
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

								// il blocco sottostante
								// (1) fa ripartire immediatamente il
								// listener delle modifiche
								// (2) gestisce la nuova notifica ricevuta.
								//
								// L'if serve a invertire le sequenze (1) e (2)
								// perchè,
								// nel caso in cui la notifica riguardi il MIO
								// essere
								// stato aggiunto a un tavolo, prima di far
								// ripartire il
								// listener devo aggiornare le mie
								// sottoscrizioni,
								// aggiungendo questo nuovo tavolo
								if (!(result.get(0).getEventKind()
										.equals("MEMBERTABLEADD") && result
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

	/**
	 * Gestisce le notifiche sulla base del tipo notification.eventKind
	 * 
	 * @param nList
	 */

	public void manageNotification(List<Notification> nList) {
		Notification n = nList.get(0);

		Info.display("Notification Received",
				n.getEventKind() + " from " + n.getSenderEmail());

		if (// n.getEventKind().equals("MEMBERONLINE")||
		n.getEventKind().equals("MEMBEROFFLINE")) {
			for (TableUI t : desktop.getTables())
				for (Long memberTable : n.getOwningTables())
					if (t.tableKey.compareTo(memberTable) == 0)
						t.getRightPanel().membersPanel.refreshMembersTree(n);
		}

		if (n.getEventKind().equals("MEMBERTABLEADD")) {

			for (TableUI t : desktop.getTables())
				if (t.tableKey.compareTo(n.getTableKey()) == 0)
					t.getRightPanel().membersPanel.refreshMembersTree(n);

			if (n.getMemberEmail().equals(user.getEmail()))
				invitedToNewTable(n);
		}

		if (n.getEventKind().equals("MEMBERHIDDEN")
				|| n.getEventKind().equals("MEMBERVISIBLE")) {
			for (TableUI t : desktop.getTables())
				if (t.tableKey.compareTo(n.getTableKey()) == 0)
					t.getRightPanel().membersPanel.refreshMembersTree(n);
		}

		if (n.getEventKind().equals("SELECTIVEPRESENCEOFF")
				|| n.getEventKind().equals("SELECTIVEPRESENCEON")) {
			for (TableUI t : desktop.getTables())
				if (t.tableKey.compareTo(n.getTableKey()) == 0)
					t.getRightPanel().membersPanel.refreshMembersTree(n);
		}

	}
	
	/**
	 *
	 */

	public void invitedToNewTable(Notification n) {

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

		// (13)recupera il tavolo
		tableService.queryTable(n.getTableKey(), new AsyncCallback<Table>() {
			@Override
			public void onFailure(Throwable caught) {
			}

			@Override
			public void onSuccess(Table result) {
				// (15)crea un table sulla base del tavolo
				TableUI gt = new TableUI(result);

				// (20)aggiungi il nuovo table al desktop
				desktop.addTable(gt);

				// (30)aggiorna l'elenco gruppi in personalpanel
				personalTable.getRightPanel().myTablesPanel
						.addNewTableToTree(gt);
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
