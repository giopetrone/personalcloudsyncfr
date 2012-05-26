package com.unito.tableplus.client.gui;

import com.extjs.gxt.ui.client.Style.Orientation;
import com.extjs.gxt.ui.client.data.ModelData;
import com.extjs.gxt.ui.client.dnd.DragSource;
import com.extjs.gxt.ui.client.dnd.DropTarget;
import com.extjs.gxt.ui.client.dnd.TreePanelDragSource;
import com.extjs.gxt.ui.client.dnd.TreePanelDropTarget;
import com.extjs.gxt.ui.client.dnd.DND.Operation;
import com.extjs.gxt.ui.client.event.DNDEvent;
import com.extjs.gxt.ui.client.event.DNDListener;
import com.extjs.gxt.ui.client.event.MenuEvent;
import com.extjs.gxt.ui.client.event.SelectionListener;
import com.extjs.gxt.ui.client.widget.ContentPanel;
import com.extjs.gxt.ui.client.widget.button.Button;
import com.extjs.gxt.ui.client.widget.layout.AccordionLayout;
import com.extjs.gxt.ui.client.widget.layout.FillLayout;
import com.extjs.gxt.ui.client.widget.menu.Menu;
import com.extjs.gxt.ui.client.widget.menu.MenuItem;
import com.extjs.gxt.ui.client.widget.toolbar.ToolBar;
import com.extjs.gxt.ui.client.widget.treepanel.TreePanel;

import com.google.gwt.core.client.GWT;
import com.google.gwt.user.client.rpc.AsyncCallback;
import com.unito.tableplus.client.TablePlus;
import com.unito.tableplus.client.gui.quickviewpanels.TableResourcesPanel;
import com.unito.tableplus.client.gui.quickviewpanels.MembersPanel;
import com.unito.tableplus.client.gui.quickviewpanels.MyTablesPanel;
import com.unito.tableplus.client.gui.quickviewpanels.MyResourcesPanel;
import com.unito.tableplus.client.gui.quickviewpanels.WalletPanel;
import com.unito.tableplus.client.services.TableService;
import com.unito.tableplus.client.services.TableServiceAsync;
import com.unito.tableplus.client.services.NotificationService;
import com.unito.tableplus.client.services.NotificationServiceAsync;
import com.unito.tableplus.shared.model.Notification;

public class RightPanel extends ContentPanel {

	// servizi
	public final TableServiceAsync tableService = GWT
			.create(TableService.class);
	public final NotificationServiceAsync notificationService = GWT
			.create(NotificationService.class);

	// componenti
	private ToolBar toolBar = null;
	public TableUI tableUI;
	public WalletPanel walletPanel;
	public MyResourcesPanel myResourcesPanel;
	public MembersPanel membersPanel;
	public MyTablesPanel myTablesPanel;
	public TableResourcesPanel tableResourcesPanel;

	// altro
	public boolean isTable = true;

	/**
	 * Costruttore
	 * 
	 * @return void
	 */

	public RightPanel(TableUI tableUI_, boolean isTable_) {
		this.tableUI = tableUI_;
		this.isTable = isTable_;

		setLayout(new FillLayout(Orientation.VERTICAL));
		setCollapsible(true);
		setTitleCollapse(true);
		setBodyStyle("backgroundColor: lightgray;");
		setFrame(true);

		addToolBar();

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

		walletPanel = new WalletPanel(this);
		add(walletPanel);

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

		setHeading("Quick View - " + tableUI.tableName);

		membersPanel = new MembersPanel(this);
		add(membersPanel);

		myResourcesPanel = new MyResourcesPanel(this);
		add(myResourcesPanel);

		tableResourcesPanel = new TableResourcesPanel(this);
		add(tableResourcesPanel);

		addDnd();
	}

	/**
	 * Lancia una notifica
	 * 
	 * @return void
	 */

	public void throwNotification(Notification notification) {
		notificationService.sendNotification(notification,
				new AsyncCallback<Boolean>() {
					@Override
					public void onFailure(Throwable caught) {
					}

					@Override
					public void onSuccess(Boolean result) {
					}
				});
	}

	/**
	 * Aggiunge la toolbar
	 * 
	 * @return void
	 */

	public void addToolBar() {
		toolBar = new ToolBar();
		Button styleButton = new Button("Style");

		MenuItem accordionItem = new MenuItem("Accordion Layout",
				new SelectionListener<MenuEvent>() {
					@Override
					public void componentSelected(MenuEvent ce) {
						setLayout(new AccordionLayout());
						layout();
					}
				});

		MenuItem fillItem = new MenuItem("Fill Layout",
				new SelectionListener<MenuEvent>() {
					@Override
					public void componentSelected(MenuEvent ce) {
						setLayout(new FillLayout(Orientation.VERTICAL));
						layout();
					}
				});

		Menu menu = new Menu();
		menu.add(accordionItem);
		menu.add(fillItem);

		styleButton.setMenu(menu);

		Button button2 = new Button("Menu2");
		toolBar.add(styleButton);
		toolBar.add(button2);
		setTopComponent(toolBar);

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
				System.out.println("ID ---> "+e.getTarget().getId());
				//se il rilascio dell'oggetto avviene nella lista dei Table objects...
				if (e.getDropTarget().getClass().toString()
						.contains("TreePanelDropTarget")) {
					@SuppressWarnings("rawtypes")
					TreePanel tree = ((TreePanel) e.getComponent());
					ModelData sel = tree.getSelectionModel().getSelectedItem();

					tableService.addDocumentToTable((String) sel.get("docId"),
							TablePlus.user, tableUI.tableKey,
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
				else if (e.getTarget().getId().equals("x-desktop")||e.getTarget().getId().equals("x-auto-2")){
					
					//recupero l'oggetto
					@SuppressWarnings("rawtypes")
					TreePanel tree = ((TreePanel) e.getComponent());
					ModelData sel = tree.getSelectionModel().getSelectedItem();
					
					//chiamo il metodo di tableUI che si occupa di aggiungere
					//lo shortcut
					tableUI.addGdocShortcut((String) sel.get("name"),(String) sel.get("link"),(String) sel.get("docId"));
					
					super.dragDrop(e);
				}
				
			}
		};

		TreePanelDragSource source = new TreePanelDragSource(
				myResourcesPanel.treePanel);
		source.addDNDListener(listener);

		TreePanelDropTarget target = new TreePanelDropTarget(
				tableResourcesPanel.treePanel);
		target.setOperation(Operation.COPY);

		DropTarget dropTarget = new DropTarget(TablePlus.desktop.getDesktop());
		dropTarget.setOperation(Operation.COPY);

		// target.setFeedback(Feedback.BOTH);
	}

	public void addDndFromQuickviewToDesktop() {
		// DragSource source=new dragSource();
	}

}
