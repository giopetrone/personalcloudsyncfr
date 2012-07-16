package com.unito.tableplus.client.gui;

import com.extjs.gxt.ui.client.Style.Orientation;
import com.extjs.gxt.ui.client.data.ModelData;
import com.extjs.gxt.ui.client.dnd.DND.Operation;
import com.extjs.gxt.ui.client.dnd.DropTarget;
import com.extjs.gxt.ui.client.dnd.TreePanelDragSource;
import com.extjs.gxt.ui.client.dnd.TreePanelDropTarget;
import com.extjs.gxt.ui.client.event.DNDEvent;
import com.extjs.gxt.ui.client.event.DNDListener;
import com.extjs.gxt.ui.client.widget.ContentPanel;
import com.extjs.gxt.ui.client.widget.layout.FillLayout;
import com.extjs.gxt.ui.client.widget.treepanel.TreePanel;
import com.google.gwt.user.client.rpc.AsyncCallback;
import com.unito.tableplus.client.TablePlus;
import com.unito.tableplus.client.gui.quickviewpanels.MembersPanel;
import com.unito.tableplus.client.gui.quickviewpanels.MyResourcesPanel;
import com.unito.tableplus.client.gui.quickviewpanels.MyTablesPanel;
import com.unito.tableplus.client.gui.quickviewpanels.TableResourcesPanel;
import com.unito.tableplus.client.services.ServiceFactory;
import com.unito.tableplus.client.services.TableServiceAsync;

public class RightPanel extends ContentPanel {

	// servizi
	private final TableServiceAsync tableService = ServiceFactory
			.getTableServiceInstance();

	// componenti
	private TableUI tableUI;
	private MyResourcesPanel myResourcesPanel;
	private MembersPanel membersPanel;
	private MyTablesPanel myTablesPanel;
	private TableResourcesPanel tableResourcesPanel;

	// altro
	private boolean isTable = true;

	public RightPanel(TableUI tableUI, boolean isTable) {
		this.tableUI = tableUI;
		this.setTable(isTable);

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

	/**
	 * Istruzioni riservate al personal table
	 * 
	 * @return void
	 */

	public void personalPanel() {

		setHeading("Quick View - <b><u>Personal Table</u></b>");

		myResourcesPanel = new MyResourcesPanel(this);
		add(myResourcesPanel);

		myTablesPanel = new MyTablesPanel(this);
		add(myTablesPanel);
	}

	/**
	 * Istruzioni riservate al table table
	 * 
	 * @return void
	 */

	public void tablePanel() {

		setHeading("Quick View - " + tableUI.getTableName());

		membersPanel = new MembersPanel(this);
		add(membersPanel);

		myResourcesPanel = new MyResourcesPanel(this);
		add(myResourcesPanel);

		tableResourcesPanel = new TableResourcesPanel(this);
		add(tableResourcesPanel);

		addDnd();
	}

	/**
	 * Aggiunge la funzionalità di drag and drop dagli oggetti personali a
	 * quelli di gruppo
	 * 
	 * @return void
	 */

	public void addDnd() {

		DNDListener listener = new DNDListener() {
			@SuppressWarnings({ "unchecked", "rawtypes" })
			@Override
			public void dragStart(DNDEvent e) {

				TreePanel tree = ((TreePanel) e.getComponent());
				ModelData sel = tree.getSelectionModel().getSelectedItem();

				if (sel != null && tree.getStore().getParent(sel) == null) {
					e.setCancelled(true);
					e.getStatus().setStatus(false);
					return;
				}
				super.dragStart(e);
			}

			@Override
			public void dragDrop(DNDEvent e) {
				System.out.println("ID ---> " + e.getTarget().getId());
				// se il rilascio dell'oggetto avviene nella lista dei Table
				// objects...
				if (e.getDropTarget().getClass().toString()
						.contains("TreePanelDropTarget")) {
					@SuppressWarnings("rawtypes")
					TreePanel tree = ((TreePanel) e.getComponent());
					ModelData sel = tree.getSelectionModel().getSelectedItem();

					tableService.addDocumentToTable((String) sel.get("docId"),
							TablePlus.getUser(), tableUI.getTableKey(),
							new AsyncCallback<Boolean>() {
								@Override
								public void onFailure(Throwable caught) {
								}

								@Override
								public void onSuccess(Boolean result) {
									// (01) Aggiorno la lista di documenti nella
									// classe Table

									// (02) Aggiorno la vista dei documenti nel
									// tableResourcesPanel
								}
							});
					super.dragDrop(e);
				}

				// se il rilascio dell'oggetto avviene sul desktop
				else if (e.getTarget().getId().equals("x-desktop")
						|| e.getTarget().getId().equals("x-auto-2")) {

					// recupero l'oggetto
					@SuppressWarnings("rawtypes")
					TreePanel tree = ((TreePanel) e.getComponent());
					ModelData sel = tree.getSelectionModel().getSelectedItem();

					// chiamo il metodo di tableUI che si occupa di aggiungere
					// lo shortcut
					tableUI.addDriveShortcut((String) sel.get("name"),
							(String) sel.get("link"), (String) sel.get("docId"));

					super.dragDrop(e);
				}

			}
		};

		TreePanelDragSource source = new TreePanelDragSource(
				myResourcesPanel.getTreePanel());
		source.addDNDListener(listener);

		TreePanelDropTarget target = new TreePanelDropTarget(
				tableResourcesPanel.treePanel);
		target.setOperation(Operation.COPY);

		DropTarget dropTarget = new DropTarget(TablePlus.getDesktop().getDesktop());
		dropTarget.setOperation(Operation.COPY);

		// target.setFeedback(Feedback.BOTH);
	}

	public void addDndFromQuickviewToDesktop() {
		// DragSource source=new dragSource();
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

	public MyTablesPanel getMyTablesPanel() {
		return myTablesPanel;
	}

	public void setMyTablesPanel(MyTablesPanel myTablesPanel) {
		this.myTablesPanel = myTablesPanel;
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
