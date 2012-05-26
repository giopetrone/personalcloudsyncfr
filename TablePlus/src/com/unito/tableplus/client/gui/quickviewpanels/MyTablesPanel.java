package com.unito.tableplus.client.gui.quickviewpanels;

import com.extjs.gxt.ui.client.Style.Orientation;
import com.extjs.gxt.ui.client.Style.Scroll;
import com.extjs.gxt.ui.client.data.BaseModelData;
import com.extjs.gxt.ui.client.data.ModelData;
import com.extjs.gxt.ui.client.data.ModelIconProvider;
import com.extjs.gxt.ui.client.event.ButtonEvent;
import com.extjs.gxt.ui.client.event.Events;
import com.extjs.gxt.ui.client.event.Listener;
import com.extjs.gxt.ui.client.event.MessageBoxEvent;
import com.extjs.gxt.ui.client.event.SelectionListener;
import com.extjs.gxt.ui.client.event.TreePanelEvent;
import com.extjs.gxt.ui.client.store.TreeStore;
import com.extjs.gxt.ui.client.util.IconHelper;
import com.extjs.gxt.ui.client.widget.ContentPanel;
import com.extjs.gxt.ui.client.widget.Dialog;
import com.extjs.gxt.ui.client.widget.Info;
import com.extjs.gxt.ui.client.widget.LayoutContainer;
import com.extjs.gxt.ui.client.widget.MessageBox;
import com.extjs.gxt.ui.client.widget.button.Button;
import com.extjs.gxt.ui.client.widget.layout.RowLayout;
import com.extjs.gxt.ui.client.widget.tips.ToolTipConfig;
import com.extjs.gxt.ui.client.widget.treepanel.TreePanel;
import com.google.gwt.core.client.GWT;
import com.google.gwt.user.client.rpc.AsyncCallback;
import com.google.gwt.user.client.ui.AbstractImagePrototype;
import com.unito.tableplus.client.TablePlus;
import com.unito.tableplus.client.gui.RightPanel;
import com.unito.tableplus.client.gui.TableUI;
import com.unito.tableplus.client.services.TableService;
import com.unito.tableplus.client.services.TableServiceAsync;
import com.unito.tableplus.client.services.NotificationService;
import com.unito.tableplus.client.services.NotificationServiceAsync;
import com.unito.tableplus.client.services.UserService;
import com.unito.tableplus.client.services.UserServiceAsync;
import com.unito.tableplus.shared.model.Table;
import com.unito.tableplus.shared.model.Notification;
import com.unito.tableplus.shared.model.User;

public class MyTablesPanel extends ContentPanel {

	public RightPanel rightPanel;

	// componenti
	public LayoutContainer leftLayoutContainer = new LayoutContainer();
	public LayoutContainer rightLayoutContainer = new LayoutContainer();
	public TreePanel<ModelData> treePanel;
	public TreeStore<ModelData> treeStore = new TreeStore<ModelData>();

	// servizi
	protected final TableServiceAsync tableService = GWT
			.create(TableService.class);
	protected final UserServiceAsync userService = GWT
			.create(UserService.class);
	protected final NotificationServiceAsync notificationService = GWT
			.create(NotificationService.class);

	/**
	 * Costruttore
	 * 
	 * @return void
	 */

	public MyTablesPanel(RightPanel rightPanel_) {
		this.rightPanel = rightPanel_;

		setHeading("My Tables");
		setCollapsible(true);
		setTitleCollapse(true);
		setBodyStyle("backgroundColor: white;");
		setLayout(new RowLayout(Orientation.HORIZONTAL));

		populateLeftLayoutContainer();
		populateRightLayoutContainer();
	}

	/**
	 * Popola l'area di sinistra, quella con i pulsanti in verticale
	 * 
	 * @return void
	 */

	public void populateLeftLayoutContainer() {
		// Button per creare un tavolo
		Button addTable = new Button();
		addTable.setToolTip(new ToolTipConfig("Create new Table"));
		addTable.setIcon(IconHelper.createStyle("monitor_add"));
		addTable.addSelectionListener(new SelectionListener<ButtonEvent>() {
			@Override
			public void componentSelected(ButtonEvent ce) {
				final MessageBox box = MessageBox.prompt("New Table",
						"Please enter new Table name:");
				box.setButtons(MessageBox.OKCANCEL);
				box.addCallback(new Listener<MessageBoxEvent>() {

					@Override
					public void handleEvent(MessageBoxEvent be) {
						// Auto-generated method stub
						if (be.getButtonClicked().getItemId().equals(Dialog.OK)) {
							createNewTable(be.getValue());
						}
					}

				});
			}
		});
		leftLayoutContainer.add(addTable);
		add(leftLayoutContainer);
	}

	/**
	 * Popola l'area di destra, quella con le informazioni
	 * 
	 * @return void
	 */

	public void populateRightLayoutContainer() {
		rightLayoutContainer.setScrollMode(Scroll.AUTO);

		treePanel = new TreePanel<ModelData>(treeStore);

		treePanel.setIconProvider(new ModelIconProvider<ModelData>() {

			public AbstractImagePrototype getIcon(ModelData model) {
				if (model.get("icon") != null) {
					return IconHelper.createStyle((String) model.get("icon"));
				} else {
					return null;
				}
			}

		});
		treePanel.setDisplayProperty("name");
		treePanel.addListener(Events.OnDoubleClick,
				new Listener<TreePanelEvent<ModelData>>() {
					public void handleEvent(TreePanelEvent<ModelData> be) {
						
						
						
						TablePlus.desktop.switchToTable(((TableUI) be.getItem()
								.get("table")).tableName);
					};
				});

		rightLayoutContainer.add(treePanel);
		rightLayoutContainer.setHeight("100%");
		rightLayoutContainer.setWidth(300);
		add(rightLayoutContainer);

		// myTables.layout();
	}

	/**
	 * Crea un nuovo tavolo: (1) crea l'oggetto Table; (2) lo memorizza nel DB
	 * 
	 * @return void
	 */

	private Table newTable = null;

	public void createNewTable(String tableName) {

		// (10)crea un nuovo tavolo
		newTable = new Table(TablePlus.user.getKey());
		newTable.setName(tableName);

		// (20)aggiunge il nuovo tavolo al db
		tableService.storeTable(newTable, new AsyncCallback<Long>() {
			@Override
			public void onFailure(Throwable caught) {
			}

			@Override
			public void onSuccess(Long result) {

				createNewTable_20(result);
			}
		});

	}

	/**
	 * Crea un nuovo tavolo -2-: (1) aggiorna l'utente corrente in locale; (2)
	 * aggiunge all'utente il tavolo; (3) aggiorna l'utente corrente nel db.
	 * 
	 * @return void
	 */

	private Long newTableKey;

	public void createNewTable_20(Long newTableKey_) {
		this.newTableKey = newTableKey_;
		// (24)aggiorna l'utente corrente
		userService.queryUser(TablePlus.user.getKey(),
				new AsyncCallback<User>() {
					@Override
					public void onFailure(Throwable caught) {
					}

					@Override
					public void onSuccess(User result_) {
						//TablePlus.user = result_;

						// (25)aggiunge all'utente il tavolo appena creato
						TablePlus.user.addTable(newTableKey);

						// (27)aggiorna l'utente nel db
						userService.storeUser(TablePlus.user,
								new AsyncCallback<Void>() {
									@Override
									public void onFailure(Throwable caught) {
									}

									@Override
									public void onSuccess(Void result) {
										tableService.queryTable(newTableKey,
												new AsyncCallback<Table>() {
													@Override
													public void onFailure(
															Throwable caught) {
													}

													@Override
													public void onSuccess(
															Table result) {
														createNewTable(result);
													}
												});
									}
								});

					}
				});

	}

	/**
	 * Crea un tavolo sulla base di un tavolo, aggiorna la vista corrente
	 * 
	 * @return void
	 */

	public void createNewTable(Table t) {

		// (30)crea il tavolo corrispondente Table table1 = new
		TableUI table1 = new TableUI(t);

		// (40)aggiunge il nuovo tavolo al desktop
		TablePlus.desktop.addTable(table1);

		// (50)carica il nuovo table
		Info.display("Table added: " + table1.tableName, "Ready to join!");
		// desktop.switchToTable(table1.getTable().getName());

		// (60)aggiunge il nuovo table alla lista del personalpanel
		addNewTableToTree(table1);

		// (70)lancia una notifica di creazione nuovo tavolo
		Notification n = new Notification();
		n.setSenderEmail(TablePlus.user.getEmail());
		n.setSenderKey(TablePlus.user.getKey());
		n.setEventKind("NEWTABLE");
		n.setTableKey(t.getKey());

		this.throwNotification(n);

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
						// Auto-generated method stub

					}

					@Override
					public void onSuccess(Boolean result) {
						// Auto-generated method stub

					}

				});
	}

	/**
	 * Aggiunge l'elenco dei miei gruppi
	 * 
	 * @return void
	 */

	public void addData() {
		ModelData m;
		for (TableUI t : TablePlus.desktop.getTables()) {
			m = new BaseModelData();
			m.set("name", t.tableName);
			m.set("icon", "monitor");
			m.set("table", t);
			treeStore.add(m, false);
		}
	}

	/**
	 * Aggiunge un tavolo all'elenco
	 * 
	 * @return void
	 */

	public void addNewTableToTree(TableUI t) {
		ModelData m = new BaseModelData();
		m.set("name", t.tableName);
		m.set("icon", "monitor");
		m.set("table", t);
		treeStore.add(m, false);
	}

}
