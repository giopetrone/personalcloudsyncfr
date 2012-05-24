package com.unito.tableplus.client.gui;

import com.extjs.gxt.ui.client.Style.Orientation;
import com.extjs.gxt.ui.client.data.ModelData;
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
import com.unito.tableplus.client.gui.quickviewpanels.GroupResourcesPanel;
import com.unito.tableplus.client.gui.quickviewpanels.MembersPanel;
import com.unito.tableplus.client.gui.quickviewpanels.MyGroupsPanel;
import com.unito.tableplus.client.gui.quickviewpanels.MyResourcesPanel;
import com.unito.tableplus.client.gui.quickviewpanels.WalletPanel;
import com.unito.tableplus.client.services.GroupService;
import com.unito.tableplus.client.services.GroupServiceAsync;
import com.unito.tableplus.client.services.NotificationService;
import com.unito.tableplus.client.services.NotificationServiceAsync;
import com.unito.tableplus.shared.model.Notification;

public class RightPanel extends ContentPanel {
	
	// servizi
	public final GroupServiceAsync groupService = GWT
			.create(GroupService.class);
	public final NotificationServiceAsync notificationService = GWT
			.create(NotificationService.class);

	// componenti
	private ToolBar toolBar = null;
	public TableUI table;
	public WalletPanel walletPanel;
	public MyResourcesPanel myResourcesPanel;
	public MembersPanel membersPanel;
	public MyGroupsPanel myGroupsPanel;
	public GroupResourcesPanel groupResourcesPanel;

	// altro
	public boolean isGroupTable = true;

	/**
	 * Costruttore
	 * 
	 * @return void
	 */

	public RightPanel(TableUI table_, boolean isGroupTable_) {
		this.table = table_;
		this.isGroupTable = isGroupTable_;

		setLayout(new FillLayout(Orientation.VERTICAL));
		setCollapsible(true);
		setTitleCollapse(true);
		setBodyStyle("backgroundColor: lightgray;");
		setFrame(true);

		addToolBar();

		if (isGroupTable)
			groupPanel();
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

		myGroupsPanel = new MyGroupsPanel(this);
		add(myGroupsPanel);
	}

	/**
	 * Istruzioni riservate al group table
	 * 
	 * @return void
	 */

	public void groupPanel() {

		setHeading("Quick View - "+table.groupName);
		
		membersPanel = new MembersPanel(this);
		add(membersPanel);
		
		myResourcesPanel = new MyResourcesPanel(this);
		add(myResourcesPanel);

		groupResourcesPanel = new GroupResourcesPanel(this);
		add(groupResourcesPanel);
		
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
				@SuppressWarnings("rawtypes")
				TreePanel tree = ((TreePanel) e.getComponent());
				ModelData sel = tree.getSelectionModel().getSelectedItem();

				groupService.addDocumentToGroup((String) sel.get("docId"),
						TablePlus.user, table.groupKey,
						new AsyncCallback<Boolean>() {
							@Override
							public void onFailure(Throwable caught) {
							}

							@Override
							public void onSuccess(Boolean result) {
								//(01) Aggiorno la lista di documenti nella classe Table
								
								
								//(02) Aggiorno la vista dei documenti nel groupResourcesPanel
							}
						});
				super.dragDrop(e);
			}
		};

		TreePanelDragSource source = new TreePanelDragSource(
				myResourcesPanel.treePanel);
		source.addDNDListener(listener);

		TreePanelDropTarget target = new TreePanelDropTarget(
				groupResourcesPanel.treePanel);
		target.setOperation(Operation.COPY);
		// target.setFeedback(Feedback.BOTH);
	}

}
