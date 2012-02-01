package com.unito.tableplus.client;

import com.extjs.gxt.desktop.client.Shortcut;
import com.extjs.gxt.ui.client.widget.Window;
import com.unito.tableplus.client.gui.DesktopPlus;
import com.unito.tableplus.client.gui.ExampleAccordionWindow;
import com.unito.tableplus.client.gui.GroupPanel;
import com.unito.tableplus.client.gui.Table;
import com.unito.tableplus.shared.Utente;

public class DataMaker {
	public Table getTable1(DesktopPlus desktop,Utente utente) {

		Table table1 = new Table(desktop,utente,new GroupPanel(desktop,utente));
		
		Window accordionWindow = new ExampleAccordionWindow();
		table1.addWindow(accordionWindow);

		Shortcut s2 = new Shortcut();
		s2.setText("Accordion Window");
		s2.setId("acc-win-shortcut");
		s2.setData("window", accordionWindow);
		//s1.addSelectionListener(desktop.getShortcutListener());
		table1.addShortcut(s2);
		
		Shortcut s22 = new Shortcut();
		s22.setText("Accordion Window 2");
		s22.setId("acc-win-shortcut");
		s22.setData("window", accordionWindow);
		//s1.addSelectionListener(desktop.getShortcutListener());
		table1.addShortcut(s22);
		
		
		return table1;

	}
	
	
	public Table getTable2(DesktopPlus desktop,Utente utente) {

		Table table2 = new Table(desktop,utente,new GroupPanel(desktop,utente));
		
		Window accordionWindow = new ExampleAccordionWindow();
		table2.addWindow(accordionWindow);

		Shortcut s2 = new Shortcut();
		s2.setText("Accordion Window 3");
		s2.setId("acc-win-shortcut");
		s2.setData("window", accordionWindow);
		//s1.addSelectionListener(desktop.getShortcutListener());
		table2.addShortcut(s2);
		
		Shortcut s22 = new Shortcut();
		s22.setText("Accordion Window 4");
		s22.setId("acc-win-shortcut");
		s22.setData("window", accordionWindow);
		//s1.addSelectionListener(desktop.getShortcutListener());
		table2.addShortcut(s22);
		
		Shortcut s222 = new Shortcut();
		s222.setText("Accordion Window 5");
		s222.setId("acc-win-shortcut");
		s222.setData("window", accordionWindow);
		//s1.addSelectionListener(desktop.getShortcutListener());
		table2.addShortcut(s222);
		
		return table2;

	}
	


}
