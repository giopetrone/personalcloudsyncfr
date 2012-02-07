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

	private DesktopPlus desktop;

	// finestra che compare se accedi al sistema non loggato
	private Window loginWindow = new Window();
	Button loginButton = new Button("Login Google");

	// crea il servizio per il token
	private final TokenServiceAsync tokenService = GWT
			.create(TokenService.class);

	// crea il servizio per l'utente
	private final UserServiceAsync userService = GWT.create(UserService.class);

	// crea l'utente corrente
	// private Utente utente = null;

	// crea l'utente corrente
	private User user = new User();

	// PersonalTable
	private PersonalTable personalTable;

	private String loginUrl;

	private String logoutUrl;

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

		AsyncCallback<String> callback = new AsyncCallback<String>() {
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
		};
		userService.isLoggedIn(homepageURL, callback);
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
		AsyncCallback<User> callback = new AsyncCallback<User>() {
			@Override
			public void onFailure(Throwable caught) {
			}

			@Override
			public void onSuccess(User result) {
				// Auto-generated method stub
				user = result;

				if (user.getToken() != null) {
					AsyncCallback<List<Document>> callback = new AsyncCallback<List<Document>>() {
						public void onFailure(Throwable caught) {

						}

						@Override
						public void onSuccess(List<Document> result) {
							user.setDocuments(result);
							loadActiveDesktop();
						}
					};
					tokenService.getDocumentList(user.getToken(), callback);

				} else {
					if (com.google.gwt.user.client.Window.Location.getHref()
							.contains("token="))
						manageNewToken();
					// se nell'url c'è un token
					else
						loadActiveDesktop();
				}
			}
		};
		userService.getCurrentUser(callback);
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
		AsyncCallback<String> callback = new AsyncCallback<String>() {
			public void onFailure(Throwable caught) {
			}

			@Override
			public void onSuccess(String result) {
				// -(2.b)- poi aggiungiamo sessionToken al wallet
				user.setToken(result);

				AsyncCallback<List<Document>> callback2 = new AsyncCallback<List<Document>>() {
					public void onFailure(Throwable caught) {
					}

					@Override
					public void onSuccess(List<Document> result) {
						AsyncCallback<Void> callback3 = new AsyncCallback<Void>() {
							@Override
							public void onFailure(Throwable caught) {
							}

							@Override
							public void onSuccess(Void result) {
							}
						};
						userService.storeUser(user, callback3);
						user.setDocuments(result);

						loadActiveDesktop();
					}
				};
				tokenService.getDocumentList(result, callback2);
			}
		};
		// -(2.a)- il servizio aggiunge il sessionToken alla session
		tokenService.getGdocSessionToken(token, callback);
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
		desktop = new DesktopPlus();

		// crea il personalTable
		personalTable = new PersonalTable(desktop, user, logoutUrl);

		// carica il personal table
		desktop.loadPersonalTable(personalTable);

		// crea il tavolo del gruppo 1 e lo aggiunge al desktop
		Table table1 = new DataMaker().getTable1(desktop, user);
		desktop.addTable(table1);

		// crea il tavolo del gruppo 2 e lo aggiunge al desktop
		Table table2 = new DataMaker().getTable2(desktop, user);
		desktop.addTable(table2);
		
		//dovrei avere una funzione che restituisce una lista dei tavoli dell'utente

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
		desktop = new DesktopPlus();
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
