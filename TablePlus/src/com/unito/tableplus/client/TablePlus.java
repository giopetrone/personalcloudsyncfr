package com.unito.tableplus.client;

import java.util.List;

import com.unito.tableplus.client.gui.*;
import com.unito.tableplus.client.services.*;
import com.unito.tableplus.shared.*;

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

	// crea il servizio per il login
	private final LoginServiceAsync loginService = GWT
			.create(LoginService.class);

	// crea il servizio per il token
	private final TokenServiceAsync tokenService = GWT
			.create(TokenService.class);

	// crea l'utente corrente
	private Utente utente = null;

	// PersonalTable
	private PersonalTable personalTable;

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

		// inizializza l'utenteCorrente
		initiateUser();
	}

	// ******************************************************************************
	// ******************************************************************************
	// ******************************************************************************
	// ******
	// ****** onModuleLoad1_5()
	// ******
	// ******************************************************************************
	// ******************************************************************************
	// ******************************************************************************

	public void onModuleLoad1_5() {
		// se sono loggato carica normalmente
		if (utente.isLoggedIn())
			onModuleLoad2();

		// se non sono loggato carica la finestra di login
		else if (!utente.isLoggedIn()) {
			desktop = new DesktopPlus();
			desktop.getTaskBar().disable();

			loginButton
					.addSelectionListener(new SelectionListener<ButtonEvent>() {
						public void componentSelected(ButtonEvent ce) {
							// Azioni da eseguire alla pressione del button
							redirect(utente.getLoginUrl());
						}
					});

			loginWindow.setHeading("Google Login Window");
			loginWindow.setLayout(new FlowLayout());
			loginWindow.add(loginButton);
			loginWindow.setClosable(false);
			desktop.addWindow(loginWindow);
			loginWindow.show();
		}

	}

	// ******************************************************************************
	// ******************************************************************************
	// ******************************************************************************
	// ******
	// ****** onModuleLoad2()
	// ******
	// ******************************************************************************
	// ******************************************************************************
	// ******************************************************************************

	public void onModuleLoad2() {

		// crea il desktop standard
		desktop = new DesktopPlus();

		// crea il personalTable
		personalTable = new PersonalTable(desktop, utente);

		// carica il personal table
		desktop.loadPersonalTable(personalTable);

		// crea il tavolo del gruppo 1 e lo aggiunge al desktop
		Table table1 = new DataMaker().getTable1(desktop, utente);
		desktop.addTable(table1);

		// crea il tavolo del gruppo 2 e lo aggiunge al desktop
		Table table2 = new DataMaker().getTable2(desktop, utente);
		desktop.addTable(table2);

		// gestisce la presenza di un token nell'URL
//		if (com.google.gwt.user.client.Window.Location.getHref().contains(
//				"token="))
//			manageNewToken();

	}

	// ******************************************************************************
	// ******************************************************************************
	// ******************************************************************************
	// ******
	// ****** initiateUser()
	// ******
	// ******************************************************************************
	// ******************************************************************************
	// ******************************************************************************

	public void initiateUser() {

		// URL della home
		final String homepageURL;
		if (GWT.getHostPageBaseURL().contains("127.0.0.1"))
			homepageURL = "http://127.0.0.1:8888/TablePlus.html?gwt.codesvr=127.0.0.1:9997";
		else
			homepageURL = GWT.getHostPageBaseURL();

		// -(1)- inizializziamo l'utente
		AsyncCallback<Utente> callback = new AsyncCallback<Utente>() {
			public void onFailure(Throwable caught) {
			}

			public void onSuccess(Utente result) {
				utente = result;

				// se l'utente ha un token
				if (utente.getWallet().getGoogleDocSessionToken() != null) {
					AsyncCallback<List<Document>> callback = new AsyncCallback<List<Document>>() {
						public void onFailure(Throwable caught) {

						}

						@Override
						public void onSuccess(List<Document> result) {
							utente.setDocuments(result);
							onModuleLoad1_5();
						}
					};
					tokenService.getDocumentList(utente.getWallet()
							.getGoogleDocSessionToken(), callback);
				}
				// se l'utente non ha un token
				else {
					if (com.google.gwt.user.client.Window.Location.getHref().contains(
							"token="))
						manageNewToken();
					//se nell'url c'è un token
					else onModuleLoad1_5();
				}
			}
		};
		loginService.isLogged(homepageURL, callback);
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

		// -(2)- pruomovi il token a "sessionToken" e lo aggiungi
		// alla session dell'utente e al suo wallet
		AsyncCallback<String> callback = new AsyncCallback<String>() {
			public void onFailure(Throwable caught) {
			}

			@Override
			public void onSuccess(String result) {
				// -(2.b)- poi aggiungiamo sessionToken al wallet
				if (utente.getWallet() == null)
					utente.setWallet(new Wallet());
				utente.getWallet().setGoogleDocSessionToken(result);

				AsyncCallback<List<Document>> callback = new AsyncCallback<List<Document>>() {
					public void onFailure(Throwable caught) {

					}

					@Override
					public void onSuccess(List<Document> result) {
						utente.setDocuments(result);

//						// -(2.c)- aggiorniamo i documenti dell'utente
//						PersonalPanel rightPanel = (PersonalPanel) desktop
//								.getPersonalTable().getRightPanel();
//
//						((ContentPanel) rightPanel.getItem(0)).getItem(1)
//								.setEnabled(false);
//						((ContentPanel) rightPanel.getItem(0)).getItem(3)
//								.setEnabled(false);
//						((ContentPanel) rightPanel.getItem(0)).getItem(4)
//								.setEnabled(false);
//						((ContentPanel) rightPanel.getItem(0)).add(new Text(
//								utente.getWallet().getGoogleDocSessionToken()));
//						((ContentPanel) rightPanel.getItem(0)).layout();
//
//						// -(3)- aggiorniamo "my docs"
//						rightPanel.updateMyDocuments(utente.getDocuments(),
//								rightPanel.myResources);
						onModuleLoad1_5();
					}
				};
				tokenService.getDocumentList(result, callback);

				// desktop.getTables().get(0).getRightPanel().updateMyDocuments(result,desktop.getTables().get(0).getRightPanel().getMyResourcesPanel());

			}
		};
		// -(2.a)- il servizio aggiunge il sessionToken alla session
		tokenService.getGdocSessionToken(token, callback);
	}

	// ******************************************************************************
	// ******************************************************************************
	// ******************************************************************************
	// ******
	// ****** redirect(String url)
	// ******
	// ******************************************************************************
	// ******************************************************************************
	// ******************************************************************************

	public static native void redirect(String url)
	/*-{
		$wnd.location = url;
	}-*/;
}
