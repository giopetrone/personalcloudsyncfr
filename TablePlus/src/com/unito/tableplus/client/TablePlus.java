package com.unito.tableplus.client;

import java.util.List;

import com.unito.tableplus.client.gui.*;
import com.unito.tableplus.client.services.*;
import com.unito.tableplus.shared.*;

import com.extjs.gxt.desktop.client.Desktop;
import com.extjs.gxt.desktop.client.Shortcut;
import com.extjs.gxt.desktop.client.StartMenu;
import com.extjs.gxt.desktop.client.TaskBar;
import com.extjs.gxt.ui.client.Style.Orientation;
import com.extjs.gxt.ui.client.data.BaseModelData;
import com.extjs.gxt.ui.client.data.ModelData;
import com.extjs.gxt.ui.client.event.ButtonEvent;
import com.extjs.gxt.ui.client.event.ComponentEvent;
import com.extjs.gxt.ui.client.event.MenuEvent;
import com.extjs.gxt.ui.client.event.SelectionListener;
import com.extjs.gxt.ui.client.store.TreeStore;
import com.extjs.gxt.ui.client.util.IconHelper;
import com.extjs.gxt.ui.client.util.Margins;
import com.extjs.gxt.ui.client.widget.ContentPanel;
import com.extjs.gxt.ui.client.widget.Info;
import com.extjs.gxt.ui.client.widget.TabItem;
import com.extjs.gxt.ui.client.widget.TabPanel;
import com.extjs.gxt.ui.client.widget.Text;
import com.extjs.gxt.ui.client.widget.Window;
import com.extjs.gxt.ui.client.widget.button.Button;
import com.extjs.gxt.ui.client.widget.layout.FitLayout;
import com.extjs.gxt.ui.client.widget.layout.FlowLayout;
import com.extjs.gxt.ui.client.widget.layout.RowData;
import com.extjs.gxt.ui.client.widget.layout.RowLayout;
import com.extjs.gxt.ui.client.widget.menu.Menu;
import com.extjs.gxt.ui.client.widget.menu.MenuItem;
import com.extjs.gxt.ui.client.widget.treepanel.TreePanel;
import com.google.gwt.core.client.EntryPoint;
import com.google.gwt.core.client.GWT;
import com.google.gwt.user.client.rpc.AsyncCallback;
import com.google.gwt.user.client.ui.AbstractImagePrototype;

//import com.google.appengine.api.users.User;

public class TablePlus implements EntryPoint {

	private Desktop desktop;

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

	// finestra fissa (utenti, propri docs...)
	private Window fixedWindow = null;

	// panel fisso
	ContentPanel rightPanel;
	RightPersonalPanel rightPersonalPanel;

	// listener dedicato al menu
	SelectionListener<MenuEvent> menuListener;

	// listener dedicato agli shortcut
	SelectionListener<ComponentEvent> shortcutListener;

	private void itemSelected(ComponentEvent ce) {
		Window w;
		if (ce instanceof MenuEvent) {
			MenuEvent me = (MenuEvent) ce;
			w = me.getItem().getData("window");
		} else {
			w = ce.getComponent().getData("window");
		}
		if (!desktop.getWindows().contains(w)) {
			desktop.addWindow(w);
		}
		if (w != null && !w.isVisible()) {
			w.show();
		} else {
			w.toFront();
		}
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
				onModuleLoad1_5();
			}
		};
		loginService.isLogged(homepageURL, callback);
	}

	// ******************************************************************************
	// ******************************************************************************
	// ******************************************************************************
	// ******
	// ****** createRightPanel()
	// ******
	// ******************************************************************************
	// ******************************************************************************
	// ******************************************************************************

	public void createRightPanel() {
		desktop.getDesktop().setLayout(new RowLayout(Orientation.HORIZONTAL));

		// creo due ContentPanel
		ContentPanel leftCP = new ContentPanel();
		rightPersonalPanel = new RightPersonalPanel(desktop, utente, this);

		// definisco il primo
		leftCP.setVisible(false);

		desktop.getDesktop().add(leftCP, new RowData(1, 100));
		desktop.getDesktop().add(rightPersonalPanel,
				new RowData(350, 1, new Margins(8)));
		desktop.getDesktop().layout();
	}

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
			desktop = new Desktop();
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
	// ****** getDocsInfo
	// ******
	// ******************************************************************************
	// ******************************************************************************
	// ******************************************************************************

	public void updateMyGdocsFolder(String gdocSessionToken) {

		AsyncCallback<List<Document>> callback = new AsyncCallback<List<Document>>() {
			public void onFailure(Throwable caught) {

			}

			@Override
			public void onSuccess(List<Document> result) {
				addDocsToMyFolder(result);
			}
		};
		tokenService.getDocumentList(gdocSessionToken, callback);
	}

	public void addDocsToMyFolder(List<Document> result) {
		// crea un tree per il secondo contentpanel
		TreeStore<ModelData> store = new TreeStore<ModelData>();
		TreePanel<ModelData> tree = new TreePanel<ModelData>(store);

		tree.setDisplayProperty("name");

		ModelData m = new BaseModelData();
		m.set("name", "MyGoogleDocs");
		store.add(m, false);
		ModelData m_son;
		for (Document document : result) {
			m_son = new BaseModelData();
			m_son.set("name", document.getTitle());
			store.add(m, m_son, false);
		}
		// aggiunge l'albero al secondo contentPanel
		rightPersonalPanel.getMyResourcePanel().add(tree);
		rightPersonalPanel.getMyResourcePanel().layout();
	}

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

				// -(2.b)- aggiorniamo "myWallet"
				// rightPersonalPanel.setUtente(utente);
				rightPersonalPanel.getWalletPanel().getItem(1)
						.setEnabled(false);
				rightPersonalPanel.getWalletPanel().getItem(3)
						.setEnabled(false);
				rightPersonalPanel.getWalletPanel().getItem(4)
						.setEnabled(false);
				rightPersonalPanel.getWalletPanel().add(new Text(result));
				rightPersonalPanel.getWalletPanel().layout();

				// -(3)- aggiorniamo "my docs"
				updateMyGdocsFolder(result);
			}
		};
		// -(2.a)- il servizio aggiunge il sessionToken alla session
		tokenService.getGdocSessionToken(token, callback);
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
		// LayoutContainer innerDesktop2;
		desktop = new Desktop();

		// inizializza il pannello sulla destra
		createRightPanel();

		// se nell'url corrente ho la stringa "token=", significa che
		// sto passando un token
		// alla mia applicazione

		if (com.google.gwt.user.client.Window.Location.getHref().contains(
				"token="))
			manageNewToken();

		// -(1)- crea un listener per il menu
		menuListener = new SelectionListener<MenuEvent>() {
			@Override
			public void componentSelected(MenuEvent me) {
				itemSelected(me);
			}
		};

		// -(2)- crea un listener per gli shortcut (icone sul desktop)
		shortcutListener = new SelectionListener<ComponentEvent>() {
			@Override
			public void componentSelected(ComponentEvent ce) {
				itemSelected(ce);
			}
		};
		
		// fino adesso sono state eseguite azioni essenziali e identiche
		// per ogni desktop/tavolo/gruppo. Da qui in poi cominciano le 
		// personalizzazioni.

		// Da adesso gli elementi che cambiano di tavolo in tavolo
		// sono
		//
		//  (1) Windows
		//  (2) Shortcuts(D)
		//  (3) Struttura interna dello StartMenu(D)
		//  (4) Pannello di destra
		//
		// idea: dovremmo avere due funzioni che, all'atto di 
		// cambiare tavolo, svuotino quello correntemente aperto
		// e lo popolino con gli elementi facenti parte di quello nuovo:
		// 
		//    public void spopola(Table t);
		// 
		//    public void spopola(Table t);
		
		addShortcuts();
	}

	// ******************************************************************************
	// ******************************************************************************
	// ******************************************************************************
	// ******
	// ****** onModuleLoad3()
	// ******
	// ******************************************************************************
	// ******************************************************************************
	// ******************************************************************************

	public void addShortcuts() {
		// -(3)- crea delle finestre; credo che di default, una volta
		// aggiunte al desktop, siano inizialmente invisibili

		Window gridWindow = new ExampleGridWindow();// createGridWindow();
		Window accordionWindow = new ExampleAccordionWindow();// createAccordionWindow();

		// -(4)- crea degli shortcuts da associare alle windows appena
		// create
		Shortcut s1 = new Shortcut();
		s1.setText("Grid Window");
		s1.setId("grid-win-shortcut");
		s1.setData("window", gridWindow);
		s1.addSelectionListener(shortcutListener);
		desktop.addShortcut(s1);
		
		
//		s1.setStyleAttribute("width","48px");
//		s1.setStyleAttribute("height","48px");
//		s1.setIcon();//.setIcon("background","url(./personal/desktop/images/grid48x48.png)");
		

		Shortcut s2 = new Shortcut();
		s2.setText("Accordion Window");
		s2.setId("acc-win-shortcut");
		s2.setData("window", accordionWindow);
		s2.addSelectionListener(shortcutListener);
		desktop.addShortcut(s2);
		
		
		// -(5)- estrae dal desktop la TaskBar(D)
		TaskBar taskBar = desktop.getTaskBar();

		// -(6)- estrae dalla TaskBar lo StartMenu(D)
		StartMenu menu = taskBar.getStartMenu();

		// -(7)- popola lo StartMenu(D)
		menu.setHeading(utente.getEmail());
		menu.setIconStyle("user");

		MenuItem menuItem = new MenuItem("Grid Window");
		menuItem.setData("window", gridWindow);
		menuItem.setIcon(IconHelper.createStyle("icon-grid"));
		menuItem.addSelectionListener(menuListener);
		menu.add(menuItem);

		menuItem = new MenuItem("Tab Window");
		menuItem.setIcon(IconHelper.createStyle("tabs"));
		menuItem.addSelectionListener(menuListener);
		menuItem.setData("window", createTabWindow());
		menu.add(menuItem);

		menuItem = new MenuItem("Accordion Window");
		menuItem.setIcon(IconHelper.createStyle("accordion"));
		menuItem.addSelectionListener(menuListener);
		menuItem.setData("window", accordionWindow);
		menu.add(menuItem);

		menuItem = new MenuItem("Bogus Submenu");
		menuItem.setIcon(IconHelper.createStyle("bogus"));

		Menu sub = new Menu();

		for (int i = 0; i < 5; i++) {
			MenuItem item = new MenuItem("Bogus Window " + (i + 1));
			item.setData("window", createBogusWindow(i));
			item.addSelectionListener(menuListener);
			sub.add(item);
		}

		menuItem.setSubMenu(sub);
		menu.add(menuItem);

		// tools
		MenuItem tool = new MenuItem("Settings");
		tool.setIcon(IconHelper.createStyle("settings"));
		tool.addSelectionListener(new SelectionListener<MenuEvent>() {
			@Override
			public void componentSelected(MenuEvent ce) {
				Info.display("Event", "The 'Settings' tool was clicked");
			}
		});
		menu.addTool(tool);

		menu.addToolSeperator();

		tool = new MenuItem("Logout");
		tool.setIcon(IconHelper.createStyle("logout"));
		tool.addSelectionListener(new SelectionListener<MenuEvent>() {
			@Override
			public void componentSelected(MenuEvent ce) {
				// Info.display("Event", "The 'Logout' tool was clicked");
				redirect(utente.getLogoutUrl());
			}
		});
		menu.addTool(tool);
	}

	public static native void redirect(String url)
	/*-{
		$wnd.location = url;
	}-*/;

	private Window createTabWindow() {
		Window w = new Window();
		w.setMinimizable(true);
		w.setMaximizable(true);
		w.setSize(740, 480);
		w.setIcon(IconHelper.createStyle("tabs"));
		w.setHeading("Tab Window");

		w.setLayout(new FitLayout());

		TabPanel panel = new TabPanel();

		for (int i = 0; i < 4; i++) {
			TabItem item = new TabItem("Tab Item " + (i + 1));
			item.addText("Something useful would be here");
			panel.add(item);
		}

		w.add(panel);
		return w;
	}

	private Window createBogusWindow(int index) {
		Window w = new Window();
		w.setIcon(IconHelper.createStyle("bogus"));
		w.setMinimizable(true);
		w.setMaximizable(true);
		w.setHeading("Bogus Window " + ++index);
		w.setSize(400, 300);
		return w;
	}
}
