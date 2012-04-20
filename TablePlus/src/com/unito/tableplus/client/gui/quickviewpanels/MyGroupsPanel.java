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
import com.unito.tableplus.client.gui.Table;
import com.unito.tableplus.client.services.GroupService;
import com.unito.tableplus.client.services.GroupServiceAsync;
import com.unito.tableplus.client.services.NotificationService;
import com.unito.tableplus.client.services.NotificationServiceAsync;
import com.unito.tableplus.client.services.UserService;
import com.unito.tableplus.client.services.UserServiceAsync;
import com.unito.tableplus.shared.model.Group;
import com.unito.tableplus.shared.model.Notification;
import com.unito.tableplus.shared.model.User;

public class MyGroupsPanel extends ContentPanel {

	public RightPanel rightPanel;

	// componenti
	public LayoutContainer leftLayoutContainer = new LayoutContainer();
	public LayoutContainer rightLayoutContainer = new LayoutContainer();
	public TreePanel<ModelData> treePanel;
	public TreeStore<ModelData> treeStore = new TreeStore<ModelData>();

	// servizi
	protected final GroupServiceAsync groupService = GWT
			.create(GroupService.class);
	protected final UserServiceAsync userService = GWT
			.create(UserService.class);
	protected final NotificationServiceAsync notificationService = GWT
			.create(NotificationService.class);

	/**
	 * Costruttore
	 * 
	 * @return void
	 */

	public MyGroupsPanel(RightPanel rightPanel_) {
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
		// Button per creare un gruppo
		Button addGroup = new Button();
		addGroup.setToolTip(new ToolTipConfig("Create new Table"));
		addGroup.setIcon(IconHelper.createStyle("monitor_add"));
		addGroup.addSelectionListener(new SelectionListener<ButtonEvent>() {
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
							createNewGroup(be.getValue());
						}
					}

				});
			}
		});
		leftLayoutContainer.add(addGroup);
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
						
						
						
						TablePlus.desktop.switchToTable(((Table) be.getItem()
								.get("table")).groupName);
					};
				});

		rightLayoutContainer.add(treePanel);
		rightLayoutContainer.setHeight("100%");
		rightLayoutContainer.setWidth(300);
		add(rightLayoutContainer);

		// myGroups.layout();
	}

	/**
	 * Crea un nuovo gruppo: (1) crea l'oggetto Group; (2) lo memorizza nel DB
	 * 
	 * @return void
	 */

	private Group newGroup = null;

	public void createNewGroup(String groupName) {

		// (10)crea un nuovo gruppo
		newGroup = new Group(TablePlus.user.getKey());
		newGroup.setName(groupName);

		// (20)aggiunge il nuovo gruppo al db
		groupService.storeGroup(newGroup, new AsyncCallback<Long>() {
			@Override
			public void onFailure(Throwable caught) {
			}

			@Override
			public void onSuccess(Long result) {

				createNewGroup_20(result);
			}
		});

	}

	/**
	 * Crea un nuovo gruppo -2-: (1) aggiorna l'utente corrente in locale; (2)
	 * aggiunge all'utente il gruppo; (3) aggiorna l'utente corrente nel db.
	 * 
	 * @return void
	 */

	private Long newGroupKey;

	public void createNewGroup_20(Long newGroupKey_) {
		this.newGroupKey = newGroupKey_;
		// (24)aggiorna l'utente corrente
		userService.queryUser(TablePlus.user.getKey(),
				new AsyncCallback<User>() {
					@Override
					public void onFailure(Throwable caught) {
					}

					@Override
					public void onSuccess(User result_) {
						//TablePlus.user = result_;

						// (25)aggiunge all'utente il gruppo appena creato
						TablePlus.user.addGroup(newGroupKey);

						// (27)aggiorna l'utente nel db
						userService.storeUser(TablePlus.user,
								new AsyncCallback<Void>() {
									@Override
									public void onFailure(Throwable caught) {
									}

									@Override
									public void onSuccess(Void result) {
										groupService.queryGroup(newGroupKey,
												new AsyncCallback<Group>() {
													@Override
													public void onFailure(
															Throwable caught) {
													}

													@Override
													public void onSuccess(
															Group result) {
														createNewTable(result);
													}
												});
									}
								});

					}
				});

	}

	/**
	 * Crea un tavolo sulla base di un gruppo, aggiorna la vista corrente
	 * 
	 * @return void
	 */

	public void createNewTable(Group g) {

		// (30)crea il tavolo corrispondente Table table1 = new
		Table table1 = new Table(g);

		// (40)aggiunge il nuovo tavolo al desktop
		TablePlus.desktop.addGroupTable(table1);

		// (50)carica il nuovo table
		Info.display("Group added: " + table1.groupName, "Ready to join!");
		// desktop.switchToTable(table1.getGroup().getName());

		// (60)aggiunge il nuovo table alla lista del personalpanel
		addNewGroupToTree(table1);

		// (70)lancia una notifica di creazione nuovo tavolo
		Notification n = new Notification();
		n.setSenderEmail(TablePlus.user.getEmail());
		n.setSenderKey(TablePlus.user.getKey());
		n.setEventKind("NEWTABLE");
		n.setGroupKey(g.getKey());

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
		for (Table t : TablePlus.desktop.getGroupTables()) {
			m = new BaseModelData();
			m.set("name", t.groupName);
			m.set("icon", "monitor");
			m.set("table", t);
			treeStore.add(m, false);
		}
	}

	/**
	 * Aggiunge un gruppo all'elenco
	 * 
	 * @return void
	 */

	public void addNewGroupToTree(Table t) {
		ModelData m = new BaseModelData();
		m.set("name", t.groupName);
		m.set("icon", "monitor");
		m.set("table", t);
		treeStore.add(m, false);
	}

}
