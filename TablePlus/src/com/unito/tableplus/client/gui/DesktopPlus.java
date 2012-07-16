package com.unito.tableplus.client.gui;

import java.util.ArrayList;
import java.util.List;

import com.extjs.gxt.desktop.client.Desktop;
import com.extjs.gxt.desktop.client.Shortcut;
import com.extjs.gxt.desktop.client.StartMenu;
import com.extjs.gxt.ui.client.Style.Orientation;
import com.extjs.gxt.ui.client.event.ComponentEvent;
import com.extjs.gxt.ui.client.event.MenuEvent;
import com.extjs.gxt.ui.client.event.SelectionListener;
import com.extjs.gxt.ui.client.util.IconHelper;
import com.extjs.gxt.ui.client.util.Margins;
import com.extjs.gxt.ui.client.widget.ContentPanel;
import com.extjs.gxt.ui.client.widget.Info;
import com.extjs.gxt.ui.client.widget.TabItem;
import com.extjs.gxt.ui.client.widget.TabPanel;
import com.extjs.gxt.ui.client.widget.layout.FitLayout;
import com.extjs.gxt.ui.client.widget.layout.RowData;
import com.extjs.gxt.ui.client.widget.layout.RowLayout;
import com.extjs.gxt.ui.client.widget.menu.Menu;
import com.extjs.gxt.ui.client.widget.menu.MenuItem;
import com.google.gwt.core.client.GWT;
import com.google.gwt.user.client.rpc.AsyncCallback;
import com.unito.tableplus.client.TablePlus;
import com.unito.tableplus.client.gui.windows.MyResourcesWindow;
import com.unito.tableplus.client.gui.windows.WalletWindow;
import com.unito.tableplus.client.gui.windows.WindowPlus;
import com.unito.tableplus.client.services.MessagingServiceAsync;
import com.unito.tableplus.client.services.ServiceFactory;
import com.unito.tableplus.client.services.UserServiceAsync;
import com.unito.tableplus.shared.model.ChannelMessageType;

public class DesktopPlus extends Desktop {

	private List<TableUI> tables;
	private TableUI activeTable;
	private RightPanel activeRightPanel;

	// listener dedicato al menu
	private SelectionListener<MenuEvent> menuListener;
	private SelectionListener<MenuEvent> tableMenuListener;

	// listener dedicato agli shortcut
	private SelectionListener<ComponentEvent> shortcutListener;
	protected StartMenu startMenu;
	private List<MenuItem> menuItems = new ArrayList<MenuItem>();
	private Menu tablesSubMenu = new Menu();

	private final UserServiceAsync userService = ServiceFactory
			.getUserServiceInstance();
	protected final MessagingServiceAsync messagingService = ServiceFactory
			.getChatServiceInstance();

	public DesktopPlus() {
		super();
		tables = new ArrayList<TableUI>();
		// setta il listener del menu
		setMenuListener(new SelectionListener<MenuEvent>() {
			@Override
			public void componentSelected(MenuEvent me) {
				String s = ((MenuItem) me.getItem()).getText();
				if (s.contains("Table") || s.contains("Personal Table")) {
					switchToTable(s);
				} else if (s.equals("Add Table")) {
					// TODO: do something
				} else {
					itemSelected(me);
				}
			}
		});

		// setta il listener per il cambio dei tavoli
		tableMenuListener = new SelectionListener<MenuEvent>() {
			@Override
			public void componentSelected(MenuEvent me) {
				String s = ((MenuItem) me.getItem()).getText();
				switchToTable(s);
			}
		};

		// (4)setta il listener delle icone
		setShortcutListener(new SelectionListener<ComponentEvent>() {
			@Override
			public void componentSelected(ComponentEvent ce) {
				itemSelected(ce);
			}
		});

		setStartMenu();

	}

	public void addFixedShortcuts() {
		// (5) crea degli shortcuts da associare alle windows
		// wallet
		WindowPlus walletWindow = new WalletWindow();
		addWindow(walletWindow);
		Shortcut s = new Shortcut();
		s.setText("Wallet");
		s.setId("wallet-win-shortcut");
		s.setData("window", walletWindow);
		s.addSelectionListener(shortcutListener);
		this.addShortcut(s);

		// my resources
		WindowPlus myResourcesWindow = new MyResourcesWindow();
		addWindow(myResourcesWindow);
		s = new Shortcut();
		s.setText("My Resources");
		s.setId("myresources-win-shortcut");
		s.setData("window", myResourcesWindow);
		s.addSelectionListener(shortcutListener);
		this.addShortcut(s);
	}

	public void setStartMenu() {
		startMenu = taskBar.getStartMenu();

		// popola lo StartMenu
		if (TablePlus.getUser() != null)
			startMenu.setHeading(TablePlus.getUser().getEmail());
		startMenu.setIconStyle("user");

		MenuItem menuItem = new MenuItem("Add Table");
		menuItem.addSelectionListener(menuListener);
		menuItems.add(menuItem);
		startMenu.add(menuItem);

		menuItem = new MenuItem("Switch Table");
		setTableSubMenu();
		menuItem.setSubMenu(tablesSubMenu);
		startMenu.add(menuItem);

		menuItem = new MenuItem("Tab Window");
		menuItem.setIcon(IconHelper.createStyle("tabs"));
		menuItem.addSelectionListener(menuListener);
		menuItems.add(menuItem);
		menuItem.setData("window", createTabWindow());
		startMenu.add(menuItem);

		menuItem = new MenuItem("Bogus Submenu");
		menuItem.setIcon(IconHelper.createStyle("bogus"));

		Menu sub = new Menu();

		for (int i = 0; i < 5; i++) {
			MenuItem item = new MenuItem("Bogus Window " + (i + 1));
			item.setData("window", createBogusWindow(i));
			item.addSelectionListener(menuListener);
			menuItems.add(item);
			sub.add(item);
		}

		menuItem.setSubMenu(sub);
		startMenu.add(menuItem);

		// tools
		MenuItem tool = new MenuItem("Settings");
		tool.setIcon(IconHelper.createStyle("settings"));
		tool.addSelectionListener(new SelectionListener<MenuEvent>() {
			@Override
			public void componentSelected(MenuEvent ce) {
				Info.display("Event", "The 'Settings' tool was clicked");
			}
		});
		startMenu.addTool(tool);

		startMenu.addToolSeperator();

		tool = new MenuItem("Logout");
		tool.setIcon(IconHelper.createStyle("logout"));
		tool.addSelectionListener(new SelectionListener<MenuEvent>() {
			@Override
			public void componentSelected(MenuEvent ce) {
				// TODO: send message
				redirect(TablePlus.getLogoutUrl());
			}
		});
		startMenu.addTool(tool);
	}

	public void setTableSubMenu() {
		MenuItem item = new MenuItem("Personal Table");
		item.addSelectionListener(tableMenuListener);
		menuItems.add(item);
		tablesSubMenu.add(item);
	}

	public void loadPersonalTable(TableUI personalTable) {

		activeTable = TablePlus.getPersonalTable();
		activeRightPanel = activeTable.getRightPanel();
		createRightPanel();

		for (Shortcut s : activeTable.getShortcuts()) {
			this.addShortcut(s);
			s.addSelectionListener(shortcutListener);
		}

		switchToTable("Personal Table");

	}

	public void switchToTable(String s) {
		unloadCurrentTable();

		if (s.equals("Personal Table")) {
			loadTable(TablePlus.getPersonalTable());
		} else
			for (TableUI t : tables) {
				if (t.getTableName().equals(s)) {
					loadTable(t);
				}
			}

	}

	private void unloadCurrentTable() {

		// if the active table is not the Personal Table
		if (!activeTable.getTableKey().equals(0L)) {
			for (Shortcut s : activeTable.getShortcuts()) {
				s.setVisible(false);
			}
			messagingService.sendMessage(TablePlus.getUser().getKey(), "",
					ChannelMessageType.USERAWAY, activeTable.getTableMembers(),
					activeTable.getTableKey(), new AsyncCallback<String>() {

						@Override
						public void onFailure(Throwable caught) {
							GWT.log("Unable to send AWAY message");
						}

						@Override
						public void onSuccess(String result) {
						}

					});
		}

		for (WindowPlus w : activeTable.getWindows()) {
			if (w.isVisible()) {
				w.setClosedBySwitch(true);
				w.setWasOpen(true);
				w.hide();
			}
		}

		activeRightPanel.removeFromParent();
	}

	public void loadTable(TableUI table) {
		this.activeTable = table;

		// carica gli shortcuts e i rispettivi listener
		for (Shortcut s : activeTable.getShortcuts()) {
			s.setVisible(true);
		}

		for (WindowPlus w : activeTable.getWindows()) {
			if (w.getWasOpen()) {
				// w.setPagePosition(w.getPreviousPosition());
				w.show();
				w.setWasOpen(false);
			}
		}

		// carica il pannello di destra
		activeRightPanel = table.getRightPanel();
		desktop.add(activeRightPanel, new RowData(350, 1, new Margins(8)));
		desktop.layout();

		if (!activeTable.getTableKey().equals(0L))
			messagingService.sendMessage(TablePlus.getUser().getKey(), "",
					ChannelMessageType.USERONLINE,
					activeTable.getTableMembers(), activeTable.getTableKey(),
					new AsyncCallback<String>() {

						@Override
						public void onFailure(Throwable caught) {
							GWT.log("Unable to send ONLINE message");
						}

						@Override
						public void onSuccess(String result) {

						}

					});
	}

	public void createRightPanel() {
		desktop.setLayout(new RowLayout(Orientation.HORIZONTAL));

		// creo due ContentPanel
		ContentPanel leftCP = new ContentPanel();
		// rightPersonalPanel = new RightPersonalPanel(desktop, utente, this);

		// definisco il primo
		leftCP.setVisible(false);

		desktop.add(leftCP, new RowData(1, 100));
		desktop.add(activeRightPanel, new RowData(350, 1, new Margins(8)));
		desktop.layout();
	}

	// reazione agli eventi
	private void itemSelected(ComponentEvent ce) {
		WindowPlus w;
		if (ce instanceof MenuEvent) {
			MenuEvent me = (MenuEvent) ce;
			w = me.getItem().getData("window");
		} else {
			w = ce.getComponent().getData("window");
		}
		if (!getWindows().contains(w)) {
			addWindow(w);
		}
		if (w != null && !w.isVisible()) {
			w.show();
		} else {
			w.toFront();
		}
	}

	public void addTable(TableUI t) {
		this.tables.add(t);
		for (Shortcut s : t.getShortcuts()) {
			this.addShortcut(s);
			s.addSelectionListener(shortcutListener);
			s.setVisible(false);
		}
		updateTablesList(t);
	}

	public void updateTablesList(TableUI t) {
		MenuItem item = new MenuItem(t.getTableName());
		item.addSelectionListener(tableMenuListener);
		menuItems.add(item);
		tablesSubMenu.add(item);
		taskBar.layout();
	}

	private WindowPlus createTabWindow() {
		WindowPlus w = new WindowPlus();
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

	private WindowPlus createBogusWindow(int index) {
		WindowPlus w = new WindowPlus();
		w.setIcon(IconHelper.createStyle("bogus"));
		w.setMinimizable(true);
		w.setMaximizable(true);
		w.setHeading("Bogus Window " + ++index);
		w.setSize(400, 300);
		return w;
	}

	public SelectionListener<MenuEvent> getMenuListener() {
		return menuListener;
	}

	public void setMenuListener(SelectionListener<MenuEvent> menuListener) {
		this.menuListener = menuListener;
	}

	public SelectionListener<ComponentEvent> getShortcutListener() {
		return shortcutListener;
	}

	public void setShortcutListener(
			SelectionListener<ComponentEvent> shortcutListener) {
		this.shortcutListener = shortcutListener;
	}

	public RightPanel getCurrentRightPanel() {
		return activeRightPanel;
	}

	public void setCurrentRightPanel(RightPanel rightPanel) {
		this.activeRightPanel = rightPanel;
	}

	public List<TableUI> getTables() {
		return tables;
	}

	public void setTables(List<TableUI> tables) {
		this.tables = tables;
	}

	public Long getActiveTableKey() {
		return activeTable.getTableKey();
	}

	public static native void redirect(String url)
	/*-{
		$wnd.location = url;
	}-*/;

}
