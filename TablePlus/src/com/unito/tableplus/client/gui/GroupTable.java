package com.unito.tableplus.client.gui;

import com.extjs.gxt.desktop.client.Shortcut;
import com.unito.tableplus.client.gui.windows.GroupResourcesWindow;
import com.unito.tableplus.client.gui.windows.WindowPlus;
import com.unito.tableplus.shared.model.Group;
import com.unito.tableplus.shared.model.User;

public class GroupTable extends Table {
	private Group group;

	public GroupTable(DesktopPlus desktop_, User user_, Group group_) {
		super(desktop_, user_, new GroupPanel(desktop_, user_, group_));
		this.group = group_;

		// -(1)- crea delle finestre; credo che di default, una volta
		// aggiunte al desktop, siano inizialmente invisibili
		WindowPlus groupResourcesWindow = new GroupResourcesWindow();// createGridWindow();
		addWindow(groupResourcesWindow);

		// group resources
		Shortcut s = new Shortcut();
		s.setText("Group Resources");
		s.setId("groupresources-win-shortcut");
		s.setData("window", groupResourcesWindow);
		this.addShortcut(s);

	}

	public Group getGroup() {
		return group;
	}

	public void setGroup(Group group) {
		this.group = group;
	}

}
