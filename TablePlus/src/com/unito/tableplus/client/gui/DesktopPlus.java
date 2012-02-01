package com.unito.tableplus.client.gui;

import java.util.ArrayList;
import java.util.List;

import com.extjs.gxt.desktop.client.Desktop;
import com.extjs.gxt.desktop.client.Shortcut;
import com.extjs.gxt.ui.client.Style.Orientation;
import com.extjs.gxt.ui.client.event.ComponentEvent;
import com.extjs.gxt.ui.client.event.MenuEvent;
import com.extjs.gxt.ui.client.event.SelectionListener;
import com.extjs.gxt.ui.client.util.Margins;
import com.extjs.gxt.ui.client.widget.ContentPanel;
import com.extjs.gxt.ui.client.widget.Window;
import com.extjs.gxt.ui.client.widget.layout.RowData;
import com.extjs.gxt.ui.client.widget.layout.RowLayout;
import com.extjs.gxt.ui.client.widget.menu.MenuItem;

public class DesktopPlus extends Desktop {

	private List<Table> tables=new ArrayList<Table>();
	private Table currentTable;
	private PersonalTable personalTable;
	private RightPanel currentRightPanel;
	// listener dedicato al menu
	private SelectionListener<MenuEvent> menuListener;
	// listener dedicato agli shortcut
	private SelectionListener<ComponentEvent> shortcutListener;

	public DesktopPlus() {
		super();

		setMenuListener(new SelectionListener<MenuEvent>() {
			@Override
			public void componentSelected(MenuEvent me) {
				String s = ((MenuItem) me.getItem()).getText();

				if (s.contains("Group") || s.contains("Personal Table")) {
					switchToTable(s);
				} else {
					itemSelected(me);
				}
			}
		});

		setShortcutListener(new SelectionListener<ComponentEvent>() {
			@Override
			public void componentSelected(ComponentEvent ce) {
				itemSelected(ce);
			}
		});

	}

	public void loadPersonalTable(PersonalTable personalTable_) {

		personalTable = personalTable_;
		currentTable = personalTable;
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
		if(s.equals("Group 1"))
			loadTable(tables.get(0));
		if(s.equals("Group 2"))
			loadTable(tables.get(1));
		else if(s.equals("Personal Table"))
			loadTable(personalTable);
	}

	public void unloadCurrentTable() {

		
		for (Shortcut s : currentTable.getShortcuts()) {
			s.setVisible(false);
			System.out.println("Reso invisibile uno shortcut");
		}

		currentRightPanel.removeFromParent();
		System.out.println("UNLOAD TABLE");
	}

	public void loadTable(Table table) {
		this.currentTable = table;

		// carica gli shortcuts e i rispettivi listener
		for (Shortcut s : currentTable.getShortcuts()) {
			System.out.println("caricato uno shortcut");
			s.setVisible(true);
		}
		
		//carica il pannello di destra
		currentRightPanel=table.getRightPanel();
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
		Window w;
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

	public PersonalTable getPersonalTable() {
		return personalTable;
	}

	public void setPersonalTable(PersonalTable personalTable) {
		this.personalTable = personalTable;
	}

	public List<Table> getTables() {
		return tables;
	}

	public void setTables(List<Table> tables) {
		this.tables = tables;
	}
	
	public void addTable(Table t){
		this.tables.add(t);
		for (Shortcut s : t.getShortcuts()) {
			System.out.println("caricato uno shortcut");
			this.addShortcut(s);
			s.addSelectionListener(shortcutListener);
			s.setVisible(false);
		}
	}

}
