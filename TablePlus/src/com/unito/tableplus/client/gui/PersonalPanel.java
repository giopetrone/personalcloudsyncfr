package com.unito.tableplus.client.gui;

import com.extjs.gxt.ui.client.Style.Scroll;
import com.extjs.gxt.ui.client.event.ButtonEvent;
import com.extjs.gxt.ui.client.event.SelectionListener;
import com.extjs.gxt.ui.client.widget.ContentPanel;
import com.extjs.gxt.ui.client.widget.Text;
import com.extjs.gxt.ui.client.widget.button.Button;
import com.extjs.gxt.ui.client.widget.form.TextField;
import com.google.gwt.core.client.GWT;
import com.google.gwt.user.client.rpc.AsyncCallback;
import com.unito.tableplus.client.services.UserService;
import com.unito.tableplus.client.services.UserServiceAsync;
import com.unito.tableplus.shared.model.User;

public class PersonalPanel extends RightPanel {
	
	// crea il servizio per l'utente
		private final UserServiceAsync userService = GWT.create(UserService.class);

	public ContentPanel wallet = new ContentPanel();
	public ContentPanel myResources = new ContentPanel();
	public ContentPanel groups = new ContentPanel();
	TextField<String> manualSessionToken = null;

	public PersonalPanel(DesktopPlus desktop, User user) {

		super(desktop, user);

		addWalletPanel();

		addMyResourcesPanel();

		addGroupsPanel();

	}

	public void addGroupsPanel() {
		groups = new ContentPanel();
		groups.setHeading("Groups");
		groups.add(new Text("Third Panel Text"));
		groups.setCollapsible(true);
		groups.setTitleCollapse(true);
		groups.setBodyStyle("backgroundColor: white;");
		groups.setScrollMode(Scroll.AUTO);

		add(groups);
	}

	public void addMyResourcesPanel() {
		myResources = new ContentPanel();
		myResources.setHeading("My Resources");
		myResources.setCollapsible(true);
		myResources.setTitleCollapse(true);
		myResources.setBodyStyle("backgroundColor: white;");
		myResources.setScrollMode(Scroll.AUTO);

		if (user.getToken() != null)
			updateMyDocuments(user.getToken(), myResources);

		add(myResources);
	}

	public void addWalletPanel() {
		wallet.setHeading("Wallet");
		wallet.add(new Text(
				"Click here to allow docs access: (new token request)"));

		Button toGdocTokenRequestButton = new Button("GDocs");

		final String homepageURL;
		if (GWT.getHostPageBaseURL().contains("127.0.0.1"))
			homepageURL = "http://127.0.0.1:8888/TablePlus.html?gwt.codesvr=127.0.0.1:9997";
		else
			homepageURL = GWT.getHostPageBaseURL();

		toGdocTokenRequestButton
				.addSelectionListener(new SelectionListener<ButtonEvent>() {
					public void componentSelected(ButtonEvent ce) {
						// Azioni da eseguire alla pressione del button
						AsyncCallback<String> callback = new AsyncCallback<String>() {
							public void onFailure(Throwable caught) {
							}

							public void onSuccess(String result) {
								redirect(result);
							}
						};
						tokenService.getRequestTokenURL(homepageURL, callback);
					}
				});
		// item (1)
		wallet.add(toGdocTokenRequestButton);

		// richiesta manuale
		// item (2)
		wallet.add(new Text(
				"or manually copy here an old token (not managing wrong tokens right now)"));
		manualSessionToken = new TextField<String>();
		manualSessionToken.setFieldLabel("Session Token");
		manualSessionToken.setAllowBlank(false);
		Button go = new Button("GO");
		go.addSelectionListener(new SelectionListener<ButtonEvent>() {
			public void componentSelected(ButtonEvent ce) {
				
				user.setToken(manualSessionToken.getValue());
				
				AsyncCallback<Void> callback = new AsyncCallback<Void>() {
					public void onFailure(Throwable caught) {
					}

					@Override
					public void onSuccess(Void result) {
						// Auto-generated method stub
						wallet.getItem(1).setEnabled(false);
						wallet.getItem(3).setEnabled(false);
						wallet.getItem(4).setEnabled(false);
						wallet.add(new Text(manualSessionToken.getValue()));
						wallet.layout();
						updateMyDocuments(manualSessionToken.getValue(),
								myResources);
					}
				};
				

				userService.storeUser(user, callback);
			}
		});
		// item (3)
		wallet.add(manualSessionToken);
		// item (4)
		wallet.add(go);

		// se abbiamo già il token, il button sarà inattivo
		if (user.getToken() != null) {
			toGdocTokenRequestButton.setEnabled(false);
			manualSessionToken.setEnabled(false);
			go.setEnabled(false);
			wallet.add(new Text(user.getToken()));
		}

		System.out.println("da RPP: " + homepageURL);

		wallet.setCollapsible(true);
		wallet.setTitleCollapse(true);
		wallet.setBodyStyle("backgroundColor: white;");
		wallet.setScrollMode(Scroll.AUTO);

		add(wallet);
	}

	public static native void redirect(String url)
	/*-{
		$wnd.location = url;

	}-*/;
}
