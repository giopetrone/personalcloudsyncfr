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
import com.google.gwt.user.client.Timer;
import com.google.gwt.user.client.rpc.AsyncCallback;
import com.unito.tableplus.client.TablePlus;
import com.unito.tableplus.client.gui.windows.*;
import com.unito.tableplus.client.services.NotificationService;
import com.unito.tableplus.client.services.NotificationServiceAsync;
import com.unito.tableplus.client.services.UserService;
import com.unito.tableplus.client.services.UserServiceAsync;
import com.unito.tableplus.shared.model.Notification;
import com.unito.tableplus.shared.model.User;

public class DesktopPlus extends Desktop {

	private List<Table> groupTables = new ArrayList<Table>();
	private Table currentTable;
	private RightPanel currentRightPanel;
	// listener dedicato al menu
	private SelectionListener<MenuEvent> menuListener;
	private SelectionListener<MenuEvent> groupMenuListener;
	// listener dedicato agli shortcut
	private SelectionListener<ComponentEvent> shortcutListener;
	protected StartMenu startMenu;
	private List<MenuItem> menuItems = new ArrayList<MenuItem>();
	public Menu groupsSubMenu = new Menu();
	String logoutUrl;
	// crea il servizio per l'utente
	private final UserServiceAsync userService = GWT.create(UserService.class);

	// crea il servizio per il notification
	protected final NotificationServiceAsync notificationService = GWT
			.create(NotificationService.class);

	protected User user;

	public DesktopPlus(User user_, String logoutUrl_) {
		// (1)costruttore di Desktop
		super();
		this.user = user_;
		this.logoutUrl = logoutUrl_;

		// (2)setta il listener del menu
		setMenuListener(new SelectionListener<MenuEvent>() {
			@Override
			public void componentSelected(MenuEvent me) {
				String s = ((MenuItem) me.getItem()).getText();

				if (s.contains("Group") || s.contains("Personal Table")) {
					switchToTable(s);
				} else if (s.equals("Add Table")) {
					createTable();
				} else {
					itemSelected(me);
				}
			}
		});

		// (3)setta il listener per il cambio dei tavoli
		groupMenuListener = new SelectionListener<MenuEvent>() {
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

		// calendar
		WindowPlus calendarWindow = new CalendarWindow();
		addWindow(calendarWindow);
		s = new Shortcut();
		s.setText("Calendar");
		s.setId("calendar-win-shortcut");
		s.setData("window", calendarWindow);
		s.addSelectionListener(shortcutListener);
		this.addShortcut(s);

		// groups manager
		WindowPlus groupsManagerWindow = new GroupsManagerWindow();
		addWindow(groupsManagerWindow);
		s = new Shortcut();
		s.setText("Groups Manager");
		s.setId("groupsmanager-win-shortcut");
		s.setData("window", groupsManagerWindow);
		s.addSelectionListener(shortcutListener);
		this.addShortcut(s);

		// p&S manager
		WindowPlus pesManagerWindow = new PesManagerWindow();
		addWindow(pesManagerWindow);
		s = new Shortcut();
		s.setText("P&S Manager");
		s.setId("pesmanager-win-shortcut");
		s.setData("window", pesManagerWindow);
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

		// -(5)- popola lo StartMenu(D)
		startMenu.setHeading(user.getEmail());
		startMenu.setIconStyle("user");

		MenuItem menuItem = new MenuItem("Add Table");
		menuItem.addSelectionListener(menuListener);
		menuItems.add(menuItem);
		startMenu.add(menuItem);

		menuItem = new MenuItem("Switch Table");
		setGroupSubMenu();
		menuItem.setSubMenu(groupsSubMenu);
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
				// Info.display("Event", "The 'Logout' tool was clicked");
				logoutUser();
			}
		});
		startMenu.addTool(tool);
	}

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
						//logoutUser();
					}

				});
	}

	public void logoutUser() {
		userService.queryUser(user.getKey(), new AsyncCallback<User>() {
			@Override
			public void onFailure(Throwable caught) {
			}

			@Override
			public void onSuccess(User result) {
				user = result;
				user.setOnline(false);

				// notifica di un utente offline
				Notification n = new Notification();
				n.setSenderEmail(user.getEmail());
				n.setSenderKey(user.getKey());
				n.setEventKind("MEMBEROFFLINE");
				n.setOwningGroups(user.getGroups());
				n.setMemberEmail(user.getEmail());
				throwNotification(n);

				userService.storeUser(user, new AsyncCallback<Void>() {
					@Override
					public void onFailure(Throwable caught) {
					}

					@Override
					public void onSuccess(Void result) {
					}
				});
				redirect(logoutUrl);
			}
		});
	}

	public void setGroupSubMenu() {
		MenuItem item_ = new MenuItem("Personal Table");
		item_.addSelectionListener(groupMenuListener);
		menuItems.add(item_);
		groupsSubMenu.add(item_);
	}

	public void createTable() {

	}

	public void loadPersonalTable(Table personalTable_) {

		currentTable = TablePlus.personalTable;
		currentRightPanel = currentTable.getRightPanel();
		createRightPanel();

		for (Shortcut s : currentTable.getShortcuts()) {
			this.addShortcut(s);
			s.addSelectionListener(shortcutListener);
		}

		switchToTable("Personal Table");

	}

	public void switchToTable(String s) {
		unloadCurrentTable();

		if (s.equals("Personal Table"))
			loadTable(TablePlus.personalTable);
		else
			for (Table t : groupTables) {
				if (t.groupName.equals(s))
					loadTable(t);
			}

	}

	public Table tableToLeave;

	public void unloadCurrentTable() {

		// parte il timer della presence selettiva (a meno che non sia il
		// personal table)

		if (currentTable.groupKey != null) {
			tableToLeave = currentTable;
			tableToLeave.timer = new Timer() {
				@Override
				public void run() {
					tableToLeave.selectivePresenceOff();
				}
			};
			tableToLeave.timer.schedule(5000);
		}

		// operazioni per l'unload del tavolo
		for (Shortcut s : currentTable.getShortcuts()) {
			s.setVisible(false);
		}

		for (WindowPlus w : currentTable.getWindows()) {
			if (w.isVisible()) {
				w.setClosedBySwitch(true);
				w.setWasOpen(true);

				w.hide();
			}
		}

		currentRightPanel.removeFromParent();
	}

	public Table tableToJoin;

	public void loadTable(Table table) {
		this.currentTable = table;

		// ferma il timer della presence selettiva (a meno che non sia il
		// personal table)

		if (currentTable.groupName != null) {
			tableToJoin = currentTable;
			// System.out.println("AAA");
			// se il timer è attivo (ma non ho un modo per controllare)
			// significa che risulto presenceSelective=true, ma il timer è
			// già partito
			if (tableToJoin.timer != null) {
				// System.out.println("BBB");

				tableToJoin.timer.cancel();
			}// altrimenti, se il timer non è attivo significa (a meno di
				// situazioni particolari che al momento possiamo non
				// considerare) che siamo presenceSelective=false
			if (tableToJoin.selectivePresence == false) {
				// System.out.println("CCC");

				tableToJoin.selectivePresenceOn();
			}
		}

		// operazioni per l'unload del tavolo...

		// carica gli shortcuts e i rispettivi listener
		for (Shortcut s : currentTable.getShortcuts()) {
			s.setVisible(true);
		}

		for (WindowPlus w : currentTable.getWindows()) {
			if (w.getWasOpen()) {
				// w.setPagePosition(w.getPreviousPosition());
				w.show();
				w.setWasOpen(false);
			}
		}

		// carica il pannello di destra
		currentRightPanel = table.getRightPanel();
		// System.out.println("CRP is "+((currentRightPanel==null)?"null":"not null"));
		desktop.add(currentRightPanel, new RowData(350, 1, new Margins(8)));
		desktop.layout();

	}

	public void createRightPanel() {
		desktop.setLayout(new RowLayout(Orientation.HORIZONTAL));

		// creo due ContentPanel
		ContentPanel leftCP = new ContentPanel();
		// rightPersonalPanel = new RightPersonalPanel(desktop, utente, this);

		// definisco il primo
		leftCP.setVisible(false);

		desktop.add(leftCP, new RowData(1, 100));
		desktop.add(currentRightPanel, new RowData(350, 1, new Margins(8)));
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
		return currentRightPanel;
	}

	public void setCurrentRightPanel(RightPanel rightPanel) {
		this.currentRightPanel = rightPanel;
	}

	public List<Table> getGroupTables() {
		return groupTables;
	}

	public void setGroupTables(List<Table> groupTables) {
		this.groupTables = groupTables;
	}

	public void addGroupTable(Table t) {
		this.groupTables.add(t);
		for (Shortcut s : t.getShortcuts()) {
			this.addShortcut(s);
			s.addSelectionListener(shortcutListener);
			s.setVisible(false);
		}
		updateGroupsList(t);
	}

	public void updateGroupsList(Table t) {
		MenuItem item_ = new MenuItem(t.groupName);
		item_.addSelectionListener(groupMenuListener);
		menuItems.add(item_);
		groupsSubMenu.add(item_);
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

	public static native void redirect(String url)
	/*-{
		$wnd.location = url;
	}-*/;

}
