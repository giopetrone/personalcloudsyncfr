package com.unito.tableplus.client.gui.panels;

import com.extjs.gxt.ui.client.Style.Orientation;
import com.extjs.gxt.ui.client.widget.ContentPanel;
import com.extjs.gxt.ui.client.widget.layout.FillLayout;
import com.unito.tableplus.client.TablePlus;

public class RightPanel extends ContentPanel {

	private MembersPanel membersPanel;
	private TablesPanel tablesPanel;

	public RightPanel() {
		setHeading("Quick View");
		setLayout(new FillLayout(Orientation.VERTICAL));
		setCollapsible(true);
		setTitleCollapse(true);
		setBodyStyle("backgroundColor: lightgray;");
		setFrame(true);
		tablesPanel = new TablesPanel();
		tablesPanel.updateContent();
		TablePlus.getDesktop().getTables();
		membersPanel = new MembersPanel();
		add(tablesPanel);
	}

	public void updateContent() {
		if (TablePlus.getDesktop().getActiveTable() != null) {
			//tablesPanel.collapse();
			this.add(membersPanel);
			membersPanel.setHeading("Table <b>"
					+ TablePlus.getDesktop().getActiveTable().getName()
					+ "</b> members");
			membersPanel.updateContent();
		} else{
			tablesPanel.expand();
			this.remove(membersPanel);
		}
		this.layout();
	}

	public TablesPanel getTablesPanel() {
		return this.tablesPanel;
	}

	public MembersPanel getMembersPanel() {
		return this.membersPanel;
	}
}
