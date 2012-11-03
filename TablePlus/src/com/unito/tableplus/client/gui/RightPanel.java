package com.unito.tableplus.client.gui;

import com.extjs.gxt.ui.client.Style.Orientation;
import com.extjs.gxt.ui.client.widget.ContentPanel;
import com.extjs.gxt.ui.client.widget.layout.FillLayout;
import com.unito.tableplus.client.gui.quickviewpanels.MembersPanel;
import com.unito.tableplus.client.gui.quickviewpanels.TablesPanel;


public class RightPanel extends ContentPanel {

	private TableUI tableUI;
	private MembersPanel membersPanel;
	private TablesPanel tablesPanel;

	private boolean isTable = true;

	public RightPanel(TableUI tableUI, boolean isTable) {
		this.tableUI = tableUI;
		this.setTable(isTable);
		setHeading("Quick View");
		setLayout(new FillLayout(Orientation.VERTICAL));
		setCollapsible(true);
		setTitleCollapse(true);
		setBodyStyle("backgroundColor: lightgray;");
		setFrame(true);

		if (isTable)
			tablePanel();
		else
			personalPanel();
	}

	private void personalPanel() {
		tablesPanel = new TablesPanel(this);
		add(tablesPanel);
	}

	private void tablePanel() {
		membersPanel = new MembersPanel(this);
		membersPanel.setHeading("Table <b>"+ tableUI.getTableName() +"</b> members" );
		add(membersPanel);
	}

	

	/**
	 * @return the isTable
	 */
	public boolean isTable() {
		return isTable;
	}

	/**
	 * @param isTable the isTable to set
	 */
	public void setTable(boolean isTable) {
		this.isTable = isTable;
	}

	public TablesPanel getMyTablesPanel() {
		return tablesPanel;
	}

	public void setMyTablesPanel(TablesPanel tablesPanel) {
		this.tablesPanel = tablesPanel;
	}

	public MembersPanel getMembersPanel() {
		return membersPanel;
	}

	public void setMembersPanel(MembersPanel membersPanel) {
		this.membersPanel = membersPanel;
	}

	public TableUI getTableUI() {
		return tableUI;
	}

	public void setTableUI(TableUI tableUI) {
		this.tableUI = tableUI;
	}

}
