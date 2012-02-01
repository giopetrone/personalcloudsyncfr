package com.unito.tableplus.client.gui;

import java.util.ArrayList;
import java.util.List;

import com.extjs.gxt.desktop.client.Shortcut;
import com.extjs.gxt.desktop.client.StartMenu;
import com.extjs.gxt.desktop.client.TaskBar;
import com.extjs.gxt.ui.client.widget.Window;
import com.extjs.gxt.ui.client.widget.menu.MenuItem;
import com.unito.tableplus.shared.Utente;

public class Table {
	
	private List<Window> windows;
	private List<Shortcut> shortcuts;
	protected StartMenu startMenu;
	private List<MenuItem> menuItems;
	


	private TaskBar taskBar;
	
	public Utente utente;
	private DesktopPlus desktop;
	protected RightPanel rightPanel;
	
	public Table(DesktopPlus desktop, Utente utente,RightPanel rightPanel){
		this.desktop=desktop;
		this.utente=utente;
		this.rightPanel=rightPanel;
		
		windows=new ArrayList<Window>();
		shortcuts=new ArrayList<Shortcut>();
		startMenu=new StartMenu();
		menuItems = new ArrayList<MenuItem>();
		taskBar=new TaskBar();
	}
	
	public void addWindow(Window w){
		windows.add(w);
	}
	
	public void addShortcut(Shortcut s){
		shortcuts.add(s);
	}
	
	public void addMenuItem(MenuItem mi){
		menuItems.add(mi);
	}
	
	public List<Window> getWindows() {
		return windows;
	}
	public void setWindows(List<Window> windows) {
		this.windows = windows;
	}
	public List<Shortcut> getShortcuts() {
		return shortcuts;
	}
	public void setShortcuts(List<Shortcut> shortcuts) {
		this.shortcuts = shortcuts;
	}
	public StartMenu getStartMenu() {
		return startMenu;
	}
	public void setStartMenu(StartMenu startMenu) {
		this.startMenu = startMenu;
	}
	
	public TaskBar getTaskBar() {
		return taskBar;
	}
	public void setTaskBar(TaskBar taskBar) {
		this.taskBar = taskBar;
	}

	public List<MenuItem> getMenuItems() {
		return menuItems;
	}

	public void setMenuItems(List<MenuItem> menuItems) {
		this.menuItems = menuItems;
	}

	public Utente getUtente() {
		return utente;
	}

	public void setUtente(Utente utente) {
		this.utente = utente;
	}

	public DesktopPlus getDesktop() {
		return desktop;
	}

	public void setDesktop(DesktopPlus desktop) {
		this.desktop = desktop;
	}

	public RightPanel getRightPanel() {
		return rightPanel;
	}

	public void setRightPanel(RightPanel rightPanel) {
		this.rightPanel = rightPanel;
	}
	

	

}
