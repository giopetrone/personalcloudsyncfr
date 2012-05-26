package com.unito.tableplus.client.gui.quickviewpanels;

import java.util.ArrayList;
import java.util.List;

import com.extjs.gxt.ui.client.Style.Orientation;
import com.extjs.gxt.ui.client.Style.Scroll;
import com.extjs.gxt.ui.client.data.BaseModelData;
import com.extjs.gxt.ui.client.data.ModelData;
import com.extjs.gxt.ui.client.data.ModelIconProvider;
import com.extjs.gxt.ui.client.event.ButtonEvent;
import com.extjs.gxt.ui.client.event.Listener;
import com.extjs.gxt.ui.client.event.MessageBoxEvent;
import com.extjs.gxt.ui.client.event.SelectionListener;
import com.extjs.gxt.ui.client.store.TreeStore;
import com.extjs.gxt.ui.client.util.IconHelper;
import com.extjs.gxt.ui.client.util.Params;
import com.extjs.gxt.ui.client.widget.ContentPanel;
import com.extjs.gxt.ui.client.widget.Dialog;
import com.extjs.gxt.ui.client.widget.Info;
import com.extjs.gxt.ui.client.widget.LayoutContainer;
import com.extjs.gxt.ui.client.widget.MessageBox;
import com.extjs.gxt.ui.client.widget.button.Button;
import com.extjs.gxt.ui.client.widget.button.ToggleButton;
import com.extjs.gxt.ui.client.widget.layout.RowLayout;
import com.extjs.gxt.ui.client.widget.tips.ToolTipConfig;
import com.extjs.gxt.ui.client.widget.treepanel.TreePanel;
import com.google.gwt.core.client.GWT;
import com.google.gwt.user.client.rpc.AsyncCallback;
import com.google.gwt.user.client.ui.AbstractImagePrototype;
import com.unito.tableplus.client.TablePlus;
import com.unito.tableplus.client.gui.RightPanel;
import com.unito.tableplus.client.services.TableService;
import com.unito.tableplus.client.services.TableServiceAsync;
import com.unito.tableplus.client.services.NotificationService;
import com.unito.tableplus.client.services.NotificationServiceAsync;
import com.unito.tableplus.client.services.UserService;
import com.unito.tableplus.client.services.UserServiceAsync;
import com.unito.tableplus.shared.model.Notification;
import com.unito.tableplus.shared.model.User;

public class MembersPanel extends ContentPanel {

	public RightPanel rightPanel;

	// componenti
	public LayoutContainer leftLayoutContainer = new LayoutContainer();
	public LayoutContainer rightLayoutContainer = new LayoutContainer();
	public TreePanel<ModelData> treePanel;
	public TreeStore<ModelData> treeStore = new TreeStore<ModelData>();
	// public BaseTreeModel onlineMembersRoot;
	// public BaseTreeModel offlineMembersRoot;
	public ToggleButton setHidden;

	// servizi
	public final UserServiceAsync userService = GWT.create(UserService.class);
	public final TableServiceAsync tableService = GWT
			.create(TableService.class);
	public final NotificationServiceAsync notificationService = GWT
			.create(NotificationService.class);

	// altro
	public User tmpNewUser;
	public String toBeInvited;

	/**
	 * Costruttore
	 * 
	 * @return void
	 */

	public MembersPanel(RightPanel rightPanel_) {
		this.rightPanel = rightPanel_;

		setHeading("Members");
		setCollapsible(true);
		setTitleCollapse(true);
		setBodyStyle("backgroundColor: white;");
		setLayout(new RowLayout(Orientation.VERTICAL));

		populateLeftLayoutContainer();
		populateRightLayoutContainer();

	}

	// @Override
	// protected void onRender(Element parent, int pos) {
	// super.onRender(parent, pos);
	// treePanel.setExpanded(onlineMembersRoot, true);
	// treePanel.setExpanded(offlineMembersRoot, true);
	// }

	/**
	 * Popola l'area di sinistra, quella con i pulsanti in verticale
	 * 
	 * @return void
	 */

	public void populateLeftLayoutContainer() {
		leftLayoutContainer = new LayoutContainer();
		leftLayoutContainer.setHeight(22);
		leftLayoutContainer.setLayout(new RowLayout(Orientation.HORIZONTAL));
		Button inviteUser = new Button();
		inviteUser.setToolTip(new ToolTipConfig("Invite new member"));
		inviteUser.setIcon(IconHelper.createStyle("invite-user"));
		inviteUser.addSelectionListener(new SelectionListener<ButtonEvent>() {
			@Override
			public void componentSelected(ButtonEvent ce) {
				// visualizza il messagebox
				final MessageBox box = MessageBox.prompt("Contact",
						"Please enter new member email:");
				box.setButtons(MessageBox.OKCANCEL);
				box.addCallback(new Listener<MessageBoxEvent>() {
					@Override
					public void handleEvent(MessageBoxEvent be) {
						// List<String>recipientList=new ArrayList<String>();
						// recipientList.add("luigi.cortese00@gmail.it");
						// notificationService.sendEmail(recipientList,
						// "subject",
						// "body",
						// new AsyncCallback<Boolean>(){
						// @Override
						// public void onFailure(Throwable caught) {
						// }
						// @Override
						// public void onSuccess(Boolean result) {
						// System.out.println("EMAIL INVIATA: "+result);
						// }
						// });

						if (be.getButtonClicked().getItemId().equals(Dialog.OK)) {
							// Info.display("MessageBox",
							// "You entered OK: '{0}'", new
							// Params(be.getValue()));

							// (10)controlla se all'email corrisponde un utente
							// iscritto
							toBeInvited = be.getValue();
							userService.queryUser("email", be.getValue(),
									new AsyncCallback<User>() {
										@Override
										public void onFailure(Throwable caught) {
										}

										@Override
										public void onSuccess(User result) {
											// (20)se l'utente inserito � null
											if (result == null)
												MessageBox
														.confirm(
																"Confirm",
																"This address is not in our database, do you want to sent an invitation by eMail?",
																new Listener<MessageBoxEvent>() {
																	public void handleEvent(
																			MessageBoxEvent ce) {
																		Button btn = ce
																				.getButtonClicked();
																		if (btn.getText()
																				.equals("Yes")) {
																			Info.display(
																					"MessageBox",
																					"The 'Yes' button was pressed");
																			sendInvitationByMail();
																		} else
																			Info.display(
																					"MessageBox",
																					"The 'No' button was pressed");
																	}
																});
											// (23)se l'utente inserito non �
											// null
											else {
												tmpNewUser = result;
												MessageBox
														.confirm(
																"Confirm",
																"This address is in our database, the corrisponding user will gain access to every document shared by this table. Do you wish to continue?",
																new Listener<MessageBoxEvent>() {
																	public void handleEvent(
																			MessageBoxEvent ce) {
																		Button btn = ce
																				.getButtonClicked();
																		if (btn.getText()
																				.equals("Yes")) {
																			Info.display(
																					"MessageBox",
																					"The 'Yes' button was pressed");
																			addExistingUserToTable();

																		} else
																			Info.display(
																					"MessageBox",
																					"The 'No' button was pressed");
																	}
																});
											}
										}
									});
						} else
							Info.display("MessageBox",
									"You entered CANCEL: '{0}'",
									new Params(be.getValue()));
					}
				});
			}
		});

		leftLayoutContainer.add(inviteUser);

		// ToggleButton per settarsi invisibile
		setHidden = new ToggleButton();
		setHidden.setToolTip(new ToolTipConfig("Hidden for this table"));
		setHidden.setIcon(IconHelper.createStyle("set-hidden"));
		setHidden.addSelectionListener(new SelectionListener<ButtonEvent>() {
			@Override
			public void componentSelected(ButtonEvent ce) {
				if (setHidden.isPressed()) {
					makeMeHidden();
				} else {
					makeMeVisible();
				}
			}
		});

		leftLayoutContainer.add(setHidden);

		// togglebutton per visualizzare un'unica lista di utenti online/offline
		ToggleButton alfabetical = new ToggleButton();
		alfabetical.setIcon(IconHelper.createStyle("sort_ascending"));
		alfabetical
				.setToolTip(new ToolTipConfig("Merge in alphabetical order"));
		leftLayoutContainer.add(alfabetical);
		add(leftLayoutContainer);

		Button deleteButton = new Button();
		deleteButton.setToolTip(new ToolTipConfig("Delete member"));
		deleteButton.setIcon(IconHelper.createStyle("delete-user"));
//		deleteButton.addSelectionListener(new SelectionListener<ButtonEvent>() {
//
//			@Override
//			public void componentSelected(ButtonEvent ce) {
//				treeStore = new TreeStore<ModelData>();
//				rightLayoutContainer.removeFromParent();
//				rightLayoutContainer = new LayoutContainer();
//				populateRightLayoutContainer();
//				addData();
//				((ContentPanel) rightLayoutContainer.getParent()).layout();
//			}
//
//		});
		leftLayoutContainer.add(deleteButton);

	}

	/**
	 * Refreshes members list
	 * 
	 */

	public void refresh() {
		// System.out.println(Thread.currentThread().getName());
		remove(leftLayoutContainer);
		populateLeftLayoutContainer();
		remove(rightLayoutContainer);
		treeStore = new TreeStore<ModelData>();

		// rightLayoutContainer.removeFromParent();
		rightLayoutContainer = new LayoutContainer();
		populateRightLayoutContainer();
		addData();
		layout();
		((ContentPanel) rightLayoutContainer.getParent()).layout();
	}

	/**
	 * Popola l'area di destra, quella con le informazioni. Non inserisce per�
	 * l'elenco degli utenti, perch� questo richiederebbe una chiamata di
	 * sistema. Crea solo lo scheletro, cos� da avere una esecuzione sequenziale
	 * di tutte le istruzioni.
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

		// onlineMembersRoot = new BaseTreeModel();
		// onlineMembersRoot.set("name", "Online Users");
		// onlineMembersRoot.set("icon", "lightbulb");
		// treeStore.add(onlineMembersRoot, false);

		// offlineMembersRoot = new BaseTreeModel();
		// offlineMembersRoot.set("name", "Offline Users");
		// offlineMembersRoot.set("icon", "lightbulb_off");
		// treeStore.add(offlineMembersRoot, false);

		// qui aggiungevo i membri

		// treePanel.setExpanded(onlineMembersRoot, true);
		// treePanel.setExpanded(offlineMembersRoot, true);
		rightLayoutContainer.add(treePanel);
		rightLayoutContainer.setHeight("100%");
		rightLayoutContainer.setWidth(322);
		add(rightLayoutContainer);

		// rightLayoutContainer.layout();
		// members.layout();
	}

	boolean firsttime = true;

	/**
	 * Aggiunge i "dati" a questo panel, cio� la lista di membri online e
	 * offline. Li recupera da "table.onlineMembersEmail" e
	 * "table.offlineMembersEmail", quindi questo metodo va chiamato solo dopo
	 * aver inizializzato queste due liste
	 * 
	 * @return void
	 */

	public void addData() {
		ModelData m_son;
		for (String userEmail : rightPanel.tableUI.onlineMembersEmail) {
			m_son = new BaseModelData();
			m_son.set("name", userEmail);

			m_son.set("icon", "user-green");
			treeStore.add(m_son, false);

		}

		for (String userEmail : rightPanel.tableUI.offlineMembersEmail) {
			m_son = new BaseModelData();
			m_son.set("name", userEmail);

			m_son.set("icon", "user-silhouette");
			try {
				treeStore.add(m_son, false);
			} catch (Exception e) {
				e.printStackTrace();
			}

		}
		setHidden.toggle(rightPanel.tableUI.hiddenUser);
		layout();
	}

	/**
	 * Aggiunge l'utente tmpNewUser al tavolo corrente
	 * 
	 * @return void
	 */

	public void addExistingUserToTable() {

		tableService.addMemberToTable(tmpNewUser.getKey(),
				rightPanel.tableUI.tableKey, new AsyncCallback<Boolean>() {

					@Override
					public void onFailure(Throwable caught) {
						// Auto-generated method stub

					}

					@Override
					public void onSuccess(Boolean result) {

						// crea una notifica
						Notification n = new Notification();
						n.setSenderEmail(TablePlus.user.getEmail());
						n.setSenderKey(TablePlus.user.getKey());
						n.setEventKind("MEMBERTABLEADD");
						n.setMemberEmail(tmpNewUser.getEmail());
						n.setTableKey(rightPanel.tableUI.tableKey);
						n.setStatus(tmpNewUser.isOnline() ? "ONLINE"
								: "OFFLINE");

						// la spedisce
						throwNotification(n);

					}

				});
		// // (10) aggiungo all'utente il tavolo corrente
		// this.tmpNewUser.addTable(table.getKey());
		// System.out.println(tmpNewUser.getTables().get(
		// tmpNewUser.getTables().size() - 1));
		//
		// // (15) dovrei recuperare la versione aggiornata del tavolo...
		// // Ha senso??? Tutte queste richieste al DB...
		//
		// // (20) aggiungo al tavolo il nuovo utente
		// table.addMember(this.tmpNewUser.getKey());
		//
		// // (30) fornisco all'utente gli accessi in scrittura a tutti i doc
		// del
		// // tavolo
		// tableService.docAccessToNewMember(tmpNewUser, table,
		// new AsyncCallback<Boolean>() {
		// @Override
		// public void onFailure(Throwable caught) {
		// }
		//
		// @Override
		// public void onSuccess(Boolean result) {
		// // (60) aggiorno il member panel
		// }
		// });
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
	 * Sulla base della notifica ricevuta aggiorna la lista dei membri.
	 * 
	 * @return void
	 */

	public void print(Notification n) {
		System.out.println(TablePlus.user.getEmail() + " in "
				+ rightPanel.tableUI.tableName + " (" + n.getEventKind()
				+ "):\n ------ onlineMembersEmail["
				+ rightPanel.tableUI.onlineMembersEmail.size() + "] = "
				+ rightPanel.tableUI.onlineMembersEmail
				+ "\n ------ offlineMembersEmail["
				+ rightPanel.tableUI.offlineMembersEmail.size() + "] = "
				+ rightPanel.tableUI.offlineMembersEmail
				+ "\n ------ hiddenMembersEmail["
				+ rightPanel.tableUI.hiddenMembersEmail.size() + "] = "
				+ rightPanel.tableUI.hiddenMembersEmail
				+ "\n ------ selectivePresenceMembers["
				+ rightPanel.tableUI.selectivePresenceMembers.size() + "] = "
				+ rightPanel.tableUI.selectivePresenceMembers + "\n ------ ");
	}

	public void refreshMembersTree(Notification n) {

		List<String> off = rightPanel.tableUI.offlineMembersEmail;
		List<String> on = rightPanel.tableUI.onlineMembersEmail;
		List<String> hid = rightPanel.tableUI.hiddenMembersEmail;
		List<String> sel = rightPanel.tableUI.selectivePresenceMembers;
		String em = n.getMemberEmail();

		// (10) MEMBERONLINE

		if ((n.getEventKind().equals("MEMBERONLINE"))) {
			if (!hid.contains(em))
				if (sel.contains(em)) {
					if (off.contains(em))
						off.remove(em);
					if (!on.contains(em))
						on.add(em);
				}
		}

		// (20) MEMBEROFFLINE

		if (n.getEventKind().equals("MEMBEROFFLINE")) {
			if (!hid.contains(em))
				if (sel.contains(em)) {
					if (!off.contains(em))
						off.add(em);
					if (on.contains(em))
						on.remove(em);
				}
		}

		// (30) MEMBERVISIBLE

		if (n.getEventKind().equals("MEMBERVISIBLE")) {
			if (hid.contains(em))
				hid.remove(em);
			if (sel.contains(em)) {
				if (off.contains(em))
					off.remove(em);
				if (!on.contains(em))
					on.add(em);
			}
		}

		// (40) MEMBERHIDDEN

		if (n.getEventKind().equals("MEMBERHIDDEN")) {
			if (!hid.contains(em))
				hid.add(em);
			if (sel.contains(em)) {
				if (!off.contains(em))
					off.add(em);
				if (on.contains(em))
					on.remove(em);
			}
		}

		// (50) MEMBERTABLEADD

		if (n.getEventKind().equals("MEMBERTABLEADD")) {
			if (!off.contains(em))
				off.add(em);
		}

		// (60) SELECTIVEPRESENCEON

		if (n.getEventKind().equals("SELECTIVEPRESENCEON")) {
			if (!sel.contains(em))
				sel.add(em);
			if (!hid.contains(em)) {
				if (off.contains(em))
					off.remove(em);
				if (!on.contains(em))
					on.add(em);
			}
		}

		// (70) SELECTIVEPRESENCEOFF

		if (n.getEventKind().equals("SELECTIVEPRESENCEOFF")) {
			if (sel.contains(em))
				sel.remove(em);
			if (!hid.contains(em)) {
				if (!off.contains(em))
					off.add(em);
				if (on.contains(em))
					on.remove(em);
			}
		}

		refresh();
		print(n);

		// System.out.println();
		// for (ModelData m : treeStore.getAllItems())
		// System.out.print(m.get("name") + ", ");
		// System.out.println("\n");
		layout();

	}

	/**
	 * Rende invisibile l'utente corrente per il tavolo corrente
	 * 
	 * @return void
	 */

	public void makeMeHidden() {
		rightPanel.tableUI.hiddenUser = true;
		// (10) mi sposto localmente nella lista di utenti offline
		// String toRemove=null;
		// for(String s:rightPanel.table.onlineMembersEmail)
		// if(s.equals(TablePlus.user.getEmail()))
		// toRemove=s;
		// if(toRemove!=null){
		// rightPanel.table.onlineMembersEmail.remove(toRemove);
		// rightPanel.table.offlineMembersEmail.add(toRemove);
		// }

		// (20) aggiorno l'oggetto Table nel DB
		tableService.addHiddenMemberToTable(TablePlus.user.getKey(),
				rightPanel.tableUI.tableKey, new AsyncCallback<Boolean>() {
					@Override
					public void onFailure(Throwable caught) {
					}

					@Override
					public void onSuccess(Boolean result) {
					}
				});

		// (30) lancio una notifica (aggiorner� la mia vista quando ricever� la
		// notifica, come tutti gli altri)
		Notification n = new Notification();
		n.setEventKind("MEMBERHIDDEN");
		n.setSenderEmail(TablePlus.user.getEmail());
		n.setMemberEmail(TablePlus.user.getEmail());
		n.setSenderKey(TablePlus.user.getKey());
		n.setTableKey(rightPanel.tableUI.tableKey);
		throwNotification(n);
	}

	/**
	 * Rende invisibile l'utente corrente per il tavolo corrente
	 * 
	 * @return void
	 */

	public void makeMeVisible() {
		rightPanel.tableUI.hiddenUser = false;
		// (20) aggiorno l'oggetto Table nel DB
		tableService.removeHiddenMemberFromTable(TablePlus.user.getKey(),
				rightPanel.tableUI.tableKey, new AsyncCallback<Boolean>() {
					@Override
					public void onFailure(Throwable caught) {
					}

					@Override
					public void onSuccess(Boolean result) {
					}
				});

		// (30) lancio una notifica (aggiorner� la mia vista quando ricever� la
		// notifica, come tutti gli altri)
		Notification n = new Notification();
		n.setEventKind("MEMBERVISIBLE");
		n.setSenderEmail(TablePlus.user.getEmail());
		n.setMemberEmail(TablePlus.user.getEmail());
		n.setSenderKey(TablePlus.user.getKey());
		n.setTableKey(rightPanel.tableUI.tableKey);
		throwNotification(n);
	}

	public void sendInvitationByMail() {
		if (toBeInvited != null) {
			List<String> recipientList = new ArrayList<String>();
			recipientList.add(toBeInvited);
			notificationService.sendEmail(TablePlus.user.getEmail(),
					toBeInvited,
					"",
					"",
					this.rightPanel.tableUI.tableKey,
					new AsyncCallback<Boolean>() {
						@Override
						public void onFailure(Throwable caught) {
						}

						@Override
						public void onSuccess(Boolean result) {
							System.out.println("EMAIL INVIATA: " + result);
						}
					});
		}

	}

}
