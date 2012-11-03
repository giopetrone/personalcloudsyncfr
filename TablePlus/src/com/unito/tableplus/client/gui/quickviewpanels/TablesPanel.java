package com.unito.tableplus.client.gui.quickviewpanels;

import java.util.List;

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
import com.unito.tableplus.client.services.ServiceFactory;
import com.unito.tableplus.client.services.TableServiceAsync;
import com.unito.tableplus.client.services.UserServiceAsync;
import com.unito.tableplus.shared.model.Table;

public class TablesPanel extends ContentPanel {

	private RightPanel rightPanel;
	private LayoutContainer leftLayoutContainer;
	private LayoutContainer rightLayoutContainer;
	private TreePanel<ModelData> treePanel;
	private TreeStore<ModelData> treeStore;

	private final UserServiceAsync userService = ServiceFactory
			.getUserServiceInstance();
	private final TableServiceAsync tableService = ServiceFactory
			.getTableServiceInstance();

	public TablesPanel(RightPanel rightPanel) {
		this.setRightPanel(rightPanel);
		this.leftLayoutContainer = new LayoutContainer();
		this.rightLayoutContainer = new LayoutContainer();
		this.treeStore = new TreeStore<ModelData>();
		setHeading("My Tables");
		setCollapsible(false);
		setTitleCollapse(false);
		setBodyStyle("backgroundColor: white;");
		setLayout(new RowLayout(Orientation.HORIZONTAL));
		mask("Loading...");
		populateLeftLayoutContainer();
		populateRightLayoutContainer();
	}

	/**
	 * Popola l'area di sinistra, quella con i pulsanti in verticale
	 * 
	 * @return void
	 */

	public void populateLeftLayoutContainer() {
		
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

			@Override
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
					@Override
					public void handleEvent(TreePanelEvent<ModelData> be) {

						TablePlus.getDesktop().switchToTable(
								((TableUI) be.getItem().get("table"))
										.getTableName());
					};
				});

		rightLayoutContainer.add(treePanel);
		rightLayoutContainer.setHeight("100%");
		rightLayoutContainer.setWidth(300);
		add(rightLayoutContainer);
		
		loadTablesList();
	}

	/**
	 * Creates a new table and stores table data. Updates user. If everything
	 * goes fine corresponding TableUI will be created.
	 * 
	 */

	public void createNewTable(String tableName) {

		final Table newTable = new Table(TablePlus.getUser().getKey());
		newTable.setName(tableName);

		tableService.storeTable(newTable, new AsyncCallback<Long>() {
			@Override
			public void onFailure(Throwable caught) {
				GWT.log("Failed storing new table", caught);
			}

			@Override
			public void onSuccess(final Long newTableKey) {

				TablePlus.getUser().addTable(newTableKey);
				newTable.setKey(newTableKey);
				// aggiorna l'utente nel db
				userService.storeUser(TablePlus.getUser(),
						new AsyncCallback<Void>() {
							@Override
							public void onFailure(Throwable caught) {
								GWT.log("Failed storing new table", caught);
							}

							@Override
							public void onSuccess(Void v) {
								TableUI table = new TableUI(newTable);
								TablePlus.getDesktop().addTable(table);
								Info.display("New Table created",
										table.getTableName()
												+ " is ready to join!");
								addNewTableToTree(table);
							}
						});
			}
		});

	}

	/**
	 * Fills the tables list
	 * 
	 * @return void
	 */

	public void loadTablesList() {
		tableService.queryTables(TablePlus.getUser().getTables(),
				new AsyncCallback<List<Table>>() {
					@Override
					public void onFailure(Throwable caught) {
						GWT.log("Failure querying tables in loadTablesList");
						unmask();
					}

					@Override
					public void onSuccess(List<Table> result) {
						for (Table table : result) {
							TableUI t = new TableUI(table);
							TablePlus.getDesktop().addTable(t);
							addTable(t);
						}
						unmask();
					}
				});
	}

	public void addTable(TableUI table) {
		ModelData m = new BaseModelData();
		m.set("key", table.getTableKey());
		m.set("name", table.getTableName());
		m.set("icon", "monitor");
		m.set("table", table);
		treeStore.add(m, false);
	}

	/**
	 * Adds a table to tables list
	 * 
	 * @return void
	 */

	public void addNewTableToTree(TableUI table) {
		ModelData m = new BaseModelData();
		m.set("name", table.getTableName());
		m.set("icon", "monitor");
		m.set("table", table);
		treeStore.add(m, false);
	}

	public RightPanel getRightPanel() {
		return rightPanel;
	}

	public void setRightPanel(RightPanel rightPanel) {
		this.rightPanel = rightPanel;
	}

}
