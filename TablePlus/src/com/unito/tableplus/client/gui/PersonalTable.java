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
import com.unito.tableplus.shared.model.User;

public class PersonalTable extends Table {
	
	String logoutUrl;
	
	public PersonalTable(DesktopPlus desktop, User user, String logoutUrl_) {
		
		super(desktop,user,new PersonalPanel(desktop,user));
	
		this.logoutUrl=logoutUrl_;
		
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
		TaskBar taskBar = desktop.getTaskBar();
		this.setTaskBar(taskBar);

		// -(4)- estrae dalla TaskBar lo StartMenu(D)
		startMenu = taskBar.getStartMenu();

		// -(5)- popola lo StartMenu(D)
		startMenu.setHeading(user.getEmail());
		startMenu.setIconStyle("user");

		
		
		MenuItem menuItem = new MenuItem("Personal Table");
		menuItem.addSelectionListener(desktop.getMenuListener());// menuListener);
		this.addMenuItem(menuItem);
		startMenu.add(menuItem);
		
		menuItem = new MenuItem("Group 1");
		menuItem.addSelectionListener(desktop.getMenuListener());// menuListener);
		this.addMenuItem(menuItem);
		startMenu.add(menuItem);
		
		menuItem = new MenuItem("Group 2");
		menuItem.addSelectionListener(desktop.getMenuListener());// menuListener);
		this.addMenuItem(menuItem);
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
				redirect(logoutUrl);
			}
		});
		startMenu.addTool(tool);
		
		
		

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
