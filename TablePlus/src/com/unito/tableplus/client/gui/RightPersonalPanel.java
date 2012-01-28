package com.unito.tableplus.client.gui;

import com.extjs.gxt.desktop.client.Desktop;
import com.extjs.gxt.ui.client.Style.Orientation;
import com.extjs.gxt.ui.client.Style.Scroll;
import com.extjs.gxt.ui.client.event.ButtonEvent;
import com.extjs.gxt.ui.client.event.MenuEvent;
import com.extjs.gxt.ui.client.event.SelectionListener;
import com.extjs.gxt.ui.client.widget.ContentPanel;
import com.extjs.gxt.ui.client.widget.Text;
import com.extjs.gxt.ui.client.widget.button.Button;
import com.extjs.gxt.ui.client.widget.form.TextField;
import com.extjs.gxt.ui.client.widget.layout.AccordionLayout;
import com.extjs.gxt.ui.client.widget.layout.FillLayout;
import com.extjs.gxt.ui.client.widget.menu.Menu;
import com.extjs.gxt.ui.client.widget.menu.MenuItem;
import com.extjs.gxt.ui.client.widget.toolbar.ToolBar;
import com.google.gwt.core.client.GWT;
import com.google.gwt.user.client.rpc.AsyncCallback;
import com.unito.tableplus.client.TablePlus;
import com.unito.tableplus.client.services.TokenService;
import com.unito.tableplus.client.services.TokenServiceAsync;
import com.unito.tableplus.shared.Utente;

public class RightPersonalPanel extends ContentPanel {

	private Desktop desktop;
	private ToolBar toolBar = null;
	private ContentPanel walletPanel = null;
	private ContentPanel resourcePanel = null;
	private ContentPanel thirdPanel = null;
	private Utente utente = null;
	private TablePlus tablePlus = null;
	// crea il servizio per il token
	private final TokenServiceAsync tokenService = GWT
			.create(TokenService.class);

	TextField<String> manualSessionToken = null;

	public RightPersonalPanel(Desktop desktop, Utente utente,
			TablePlus tablePlus) {
		this.desktop = desktop;
		this.utente = utente;
		this.tablePlus = tablePlus;

		setLayout(new FillLayout(Orientation.VERTICAL));
		setHeading("One Glance View");
		setCollapsible(true);
		setTitleCollapse(true);
		setBodyStyle("backgroundColor: lightgray;");
		setFrame(true);

		addToolbar();

		addWalletPanel();

		addResourcePanel();

		addThirdPanel();

	}

	// ******************************************************************************
	// ******************************************************************************
	// ******************************************************************************
	// ******
	// ****** addToolbar()
	// ******
	// ******************************************************************************
	// ******************************************************************************
	// ******************************************************************************

	public void addToolbar() {
		ToolBar toolBar = new ToolBar();
		Button styleButton = new Button("Style");

		MenuItem accordionItem = new MenuItem("Accordion Layout",
				new SelectionListener<MenuEvent>() {
					@Override
					public void componentSelected(MenuEvent ce) {
						setLayout(new AccordionLayout());
						layout();
					}
				});

		MenuItem fillItem = new MenuItem("Fill Layout",
				new SelectionListener<MenuEvent>() {
					@Override
					public void componentSelected(MenuEvent ce) {
						setLayout(new FillLayout(Orientation.VERTICAL));
						layout();
					}
				});

		Menu menu = new Menu();
		menu.add(accordionItem);
		menu.add(fillItem);

		styleButton.setMenu(menu);

		Button button2 = new Button("Menu2");
		toolBar.add(styleButton);
		toolBar.add(button2);
		setTopComponent(toolBar);
	}

	// ******************************************************************************
	// ******************************************************************************
	// ******************************************************************************
	// ******
	// ****** addWalletPanel()
	// ******
	// ******************************************************************************
	// ******************************************************************************
	// ******************************************************************************

	public void addWalletPanel() {
		walletPanel = new ContentPanel();
		walletPanel.setHeading("Wallet");
		walletPanel.add(new Text(
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
		walletPanel.add(toGdocTokenRequestButton);

		// richiesta manuale
		// item (2)
		walletPanel
				.add(new Text(
						"or manually copy here an old token (not managing wrong tokens right now)"));
		manualSessionToken = new TextField<String>();
		manualSessionToken.setFieldLabel("Session Token");
		manualSessionToken.setAllowBlank(false);
		Button go = new Button("GO");
		go.addSelectionListener(new SelectionListener<ButtonEvent>() {
			public void componentSelected(ButtonEvent ce) {
				AsyncCallback<Void> callback = new AsyncCallback<Void>() {
					public void onFailure(Throwable caught) {
					}

					@Override
					public void onSuccess(Void result) {
						// TODO Auto-generated method stub
						walletPanel.getItem(1).setEnabled(false);
						walletPanel.getItem(3).setEnabled(false);
						walletPanel.getItem(4).setEnabled(false);
						walletPanel.add(new Text(manualSessionToken.getValue()));
						walletPanel.layout();
						tablePlus.updateMyGdocsFolder(manualSessionToken
								.getValue());
					}
				};
				utente.getWallet().setGoogleDocSessionToken(
						manualSessionToken.getValue());
				tokenService.manualToken(manualSessionToken.getValue(),
						callback);
			}
		});
		// item (3)
		walletPanel.add(manualSessionToken);
		// item (4)
		walletPanel.add(go);

		// se abbiamo già il token, il button sarà inattivo
		if (utente.getWallet() != null)
			if (utente.getWallet().getGoogleDocSessionToken() != null) {
				toGdocTokenRequestButton.setEnabled(false);
				manualSessionToken.setEnabled(false);
				go.setEnabled(false);
				walletPanel.add(new Text(utente.getWallet()
						.getGoogleDocSessionToken()));
			}

		System.out.println("da RPP: " + homepageURL);

		walletPanel.setCollapsible(true);
		walletPanel.setTitleCollapse(true);
		walletPanel.setBodyStyle("backgroundColor: white;");
		walletPanel.setScrollMode(Scroll.AUTO);
		add(walletPanel);
	}

	// ******************************************************************************
	// ******************************************************************************
	// ******************************************************************************
	// ******
	// ****** addResourcePanel()
	// ******
	// ******************************************************************************
	// ******************************************************************************
	// ******************************************************************************

	public void addResourcePanel() {
		resourcePanel = new ContentPanel();
		resourcePanel.setHeading("My Resources");
		resourcePanel.setCollapsible(true);
		resourcePanel.setTitleCollapse(true);
		resourcePanel.setBodyStyle("backgroundColor: white;");
		resourcePanel.setScrollMode(Scroll.AUTO);
		add(resourcePanel);

		if (utente.getWallet() != null)
			if (utente.getWallet().getGoogleDocSessionToken() != null)
				tablePlus.updateMyGdocsFolder(utente.getWallet()
						.getGoogleDocSessionToken());

	}

	// ******************************************************************************
	// ******************************************************************************
	// ******************************************************************************
	// ******
	// ****** addThirdPanel()
	// ******
	// ******************************************************************************
	// ******************************************************************************
	// ******************************************************************************

	public void addThirdPanel() {
		thirdPanel = new ContentPanel();
		thirdPanel.setHeading("Third Panel");
		thirdPanel.add(new Text("Third Panel Text"));
		thirdPanel.setCollapsible(true);
		thirdPanel.setTitleCollapse(true);
		thirdPanel.setBodyStyle("backgroundColor: white;");
		thirdPanel.setScrollMode(Scroll.AUTO);
		add(thirdPanel);
	}

	public static native void redirect(String url)
	/*-{
		$wnd.location = url;

	}-*/;

	public Desktop getDesktop() {
		return desktop;
	}

	public void setDesktop(Desktop desktop) {
		this.desktop = desktop;
	}

	public ToolBar getToolBar() {
		return toolBar;
	}

	public void setToolBar(ToolBar toolBar) {
		this.toolBar = toolBar;
	}

	public ContentPanel getWalletPanel() {
		return walletPanel;
	}

	public void setWalletPanel(ContentPanel walletPanel) {
		this.walletPanel = walletPanel;
	}

	public ContentPanel getThirdPanel() {
		return thirdPanel;
	}

	public void setThirdPanel(ContentPanel thirdPanel) {
		this.thirdPanel = thirdPanel;
	}

	public ContentPanel getMyResourcePanel() {
		return resourcePanel;
	}

	public void setMyResourcePanel(ContentPanel resourcePanel) {
		this.resourcePanel = resourcePanel;
	}

	public Utente getUtente() {
		return utente;
	}

	public void setUtente(Utente utente) {
		this.utente = utente;
	}

}
