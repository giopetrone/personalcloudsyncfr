package com.unito.tableplus.client.gui;

import com.extjs.gxt.desktop.client.Shortcut;
import com.extjs.gxt.desktop.client.TaskBar;
import com.extjs.gxt.ui.client.event.MenuEvent;
import com.extjs.gxt.ui.client.event.SelectionListener;
import com.extjs.gxt.ui.client.util.IconHelper;
import com.extjs.gxt.ui.client.widget.Info;
import com.extjs.gxt.ui.client.widget.TabItem;
import com.extjs.gxt.ui.client.widget.TabPanel;
import com.extjs.gxt.ui.client.widget.Window;
import com.extjs.gxt.ui.client.widget.layout.FitLayout;
import com.extjs.gxt.ui.client.widget.menu.Menu;
import com.extjs.gxt.ui.client.widget.menu.MenuItem;
import com.google.gwt.core.client.GWT;
import com.google.gwt.user.client.rpc.AsyncCallback;
import com.unito.tableplus.client.services.UserService;
import com.unito.tableplus.client.services.UserServiceAsync;
import com.unito.tableplus.shared.model.User;

public class PersonalTable extends Table {

	String logoutUrl;

	// crea il servizio per l'utente
	private final UserServiceAsync userService = GWT.create(UserService.class);

	public PersonalTable(DesktopPlus desktop_, User user_, String logoutUrl_) {

		super(desktop_, user_, new PersonalPanel(desktop_, user_));

		this.logoutUrl = logoutUrl_;

		// -(1)- crea delle finestre; credo che di default, una volta
		// aggiunte al desktop, siano inizialmente invisibili

		Window gridWindow = new ExampleGridWindow();// createGridWindow();
		this.addWindow(gridWindow);

		// -(2)- crea degli shortcuts da associare alle windows appena
		// create
		Shortcut s1 = new Shortcut();
		s1.setText("Grid Window");
		s1.setId("grid-win-shortcut");
		s1.setData("window", gridWindow);
		this.addShortcut(s1);

		// -(3)- estrae dal desktop la TaskBar(D)
		TaskBar taskBar_ = desktop.getTaskBar();
		this.taskBar=taskBar_;

		// -(4)- estrae dalla TaskBar lo StartMenu(D)
		startMenu = taskBar.getStartMenu();

		// -(5)- popola lo StartMenu(D)
		startMenu.setHeading(user.getEmail());
		startMenu.setIconStyle("user");

		MenuItem menuItem = new MenuItem("Add Table");
		menuItem.addSelectionListener(desktop.getMenuListener());// menuListener);
		this.addMenuItem(menuItem);
		startMenu.add(menuItem);

		menuItem = new MenuItem("Switch Table");
		menuItem.setSubMenu(getSubMenu());
		startMenu.add(menuItem);

		menuItem = new MenuItem("Tab Window");
		menuItem.setIcon(IconHelper.createStyle("tabs"));
		menuItem.addSelectionListener(desktop.getMenuListener());// menuListener);
		this.addMenuItem(menuItem);
		menuItem.setData("window", createTabWindow());
		startMenu.add(menuItem);

		menuItem = new MenuItem("Bogus Submenu");
		menuItem.setIcon(IconHelper.createStyle("bogus"));

		Menu sub = new Menu();

		for (int i = 0; i < 5; i++) {
			MenuItem item = new MenuItem("Bogus Window " + (i + 1));
			item.setData("window", createBogusWindow(i));
			item.addSelectionListener(desktop.getMenuListener());// menuListener);
			this.addMenuItem(item);
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
				user.setOnline(false);
				userService.storeUser(user, new AsyncCallback<Void>() {

					@Override
					public void onFailure(Throwable caught) {
						// Auto-generated method stub

					}

					@Override
					public void onSuccess(Void result) {
						// Auto-generated method stub
						System.out.println("SUCCESSO");
					}

				});
				redirect(logoutUrl);
			}
		});
		startMenu.addTool(tool);
	}
	
	public Menu groupsSubMenu; 

	public Menu getSubMenu() {
		groupsSubMenu = new Menu();

		MenuItem item_ = new MenuItem("Personal Table");
		item_.addSelectionListener(desktop.getMenuListener());
		this.addMenuItem(item_);
		groupsSubMenu.add(item_);

		item_ = new MenuItem("Group 1");
		item_.addSelectionListener(desktop.getMenuListener());
		this.addMenuItem(item_);
		groupsSubMenu.add(item_);

		item_ = new MenuItem("Group 2");
		item_.addSelectionListener(desktop.getMenuListener());
		this.addMenuItem(item_);
		groupsSubMenu.add(item_);
		
		return groupsSubMenu;
	}

	public void updateGroupsList() {
		groupsSubMenu.add(new MenuItem("Prova"));
		taskBar.layout();
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
