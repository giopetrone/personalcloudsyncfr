package com.unito.tableplus.client.gui.quickviewpanels;

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
import com.unito.tableplus.client.services.MessagingServiceAsync;
import com.unito.tableplus.client.services.ServiceFactory;
import com.unito.tableplus.client.services.TableServiceAsync;
import com.unito.tableplus.client.services.UserServiceAsync;
import com.unito.tableplus.shared.model.ChannelMessageType;
import com.unito.tableplus.shared.model.User;

public class MembersPanel extends ContentPanel {

	private final UserServiceAsync userService = ServiceFactory
			.getUserServiceInstance();
	private final TableServiceAsync tableService = ServiceFactory
			.getTableServiceInstance();
	private final MessagingServiceAsync messagingService = ServiceFactory
			.getChatServiceInstance();

	// componenti
	private RightPanel rightPanel;
	private LayoutContainer leftLayoutContainer;
	private LayoutContainer rightLayoutContainer;
	private TreePanel<ModelData> treePanel;
	private TreeStore<ModelData> treeStore;

	public MembersPanel(RightPanel rightPanel) {
		this.rightPanel = rightPanel;
		leftLayoutContainer = new LayoutContainer();
		rightLayoutContainer = new LayoutContainer();
		treeStore = new TreeStore<ModelData>();
		treeStore.setMonitorChanges(true);
		setCollapsible(false);
		setTitleCollapse(false);
		setBodyStyle("backgroundColor: white;");
		setLayout(new RowLayout(Orientation.VERTICAL));

		populateLeftLayoutContainer();
		populateRightLayoutContainer();
		loadUsers();

	}

	/**
	 * Popola l'area di sinistra, quella con i pulsanti in verticale
	 * 
	 * @return void
	 */

	public void populateLeftLayoutContainer() {
		leftLayoutContainer = new LayoutContainer();
		leftLayoutContainer.setHeight(22);
		leftLayoutContainer.setLayout(new RowLayout(Orientation.HORIZONTAL));
		
		Button backToPersonalTable = new Button();
        backToPersonalTable.setToolTip(new ToolTipConfig(
                        "Back to Personal Table"));
        backToPersonalTable.setIcon(IconHelper.createStyle("monitor_go"));
        backToPersonalTable
                        .addSelectionListener(new SelectionListener<ButtonEvent>() {
                                @Override
                                public void componentSelected(ButtonEvent ce) {
                                        TablePlus.getDesktop().switchToTable("Personal Table");
                                }
                        });
        
		
		Button inviteUser = new Button();
		inviteUser.setToolTip(new ToolTipConfig("Add a member"));
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
						inviteUser(be);
					}
				});
			}
		});

		
		
		Button deleteButton = new Button();
		deleteButton.setToolTip(new ToolTipConfig("Delete member"));
		deleteButton.setIcon(IconHelper.createStyle("delete-user"));

		deleteButton.addSelectionListener(new SelectionListener<ButtonEvent>() {

			@Override
			public void componentSelected(ButtonEvent ce) {
				Info.display("Delete member", "Not implemented yet");
			}

		});
		
		leftLayoutContainer.add(backToPersonalTable);
		leftLayoutContainer.add(inviteUser);
		leftLayoutContainer.add(deleteButton);

		add(leftLayoutContainer);

		

	}

	/**
	 * Popola l'area di destra, quella con le informazioni. Non inserisce però
	 * l'elenco degli utenti, perchè questo richiederebbe una chiamata di
	 * sistema. Crea solo lo scheletro, così da avere una esecuzione sequenziale
	 * di tutte le istruzioni.
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

		rightLayoutContainer.add(treePanel);
		rightLayoutContainer.setHeight("100%");
		rightLayoutContainer.setWidth(322);
		add(rightLayoutContainer);

	}

	public void loadUsers() {
		userService.queryUsers(rightPanel.getTableUI().getTableMembers(),
				new AsyncCallback<List<User>>() {

					@Override
					public void onFailure(Throwable caught) {
						GWT.log("Failed querying users", caught);
					}

					@Override
					public void onSuccess(List<User> result) {
						ModelData data;
						for (User user : result) {
							data = new BaseModelData();
							data.set("name", user.getEmail());
							data.set("icon", "user-offline");
							treeStore.add(data, false);
						}
					}
				});
		layout();
	}
	
	//TODO: make this method more fancy and pleasant to read
	private void inviteUser(MessageBoxEvent be) {
		if (be.getButtonClicked().getItemId().equals(Dialog.OK)) {

			// checks if user exists in database
			final String toBeInvited = be.getValue();
			userService.queryUser("email", toBeInvited,
					new AsyncCallback<User>() {
						@Override
						public void onFailure(Throwable caught) {
							GWT.log("Failed querying user", caught);
						}

						@Override
						public void onSuccess(final User result) {
							// if user does not exist
							if (result == null)
								MessageBox
										.confirm(
												"Confirm",
												"This address is not in our database, do you want to sent an invitation by email?",
												new Listener<MessageBoxEvent>() {
													@Override
													public void handleEvent(
															MessageBoxEvent ce) {
														Button btn = ce
																.getButtonClicked();
														if (btn.getText()
																.equals("Yes"))
														sendInvitationByMail(toBeInvited);
													}
												});
							// if user exist
							else if (!result.getTables().contains(
									rightPanel.getTableUI().getTableKey())) {
								MessageBox
										.confirm(
												"Confirm",
												"This address is in our database, "
														+ "the corrisponding user will gain access to "
														+ "every document shared by this table. Do you wish to continue?",
												new Listener<MessageBoxEvent>() {
													@Override
													public void handleEvent(
															MessageBoxEvent ce) {
														Button btn = ce
																.getButtonClicked();
														if (btn.getText()
																.equals("Yes"))
															addMember(result);
													}
												});
							}

						}
					});
		}
	}

	/**
	 * Adds a user to current table
	 * 
	 * @return void
	 */

	public void addMember(final User newUser) {
		Long currentUserKey = TablePlus.getUser().getKey();
		Long newUserKey = newUser.getKey();
		tableService.addMember(currentUserKey,newUserKey, rightPanel.getTableUI()
				.getTableKey(), new AsyncCallback<Void>() {

			@Override
			public void onFailure(Throwable caught) {
				GWT.log("Failed adding user", caught);
			}

			@Override
			public void onSuccess(Void v) {
				List<Long> recipients = rightPanel.getTableUI()
						.getTableMembers();
				recipients.add(newUser.getKey());
				Info.display("Result",
						"User has been successfully added to table.");
				messagingService.sendMessage(TablePlus.getUser().getKey(),
						newUser.getEmail(), ChannelMessageType.NEWTABLEMEMBER,
						recipients, rightPanel.getTableUI().getTableKey(),
						new AsyncCallback<String>() {

							@Override
							public void onFailure(Throwable caught) {
								GWT.log("Failed to notify other users", caught);
							}

							@Override
							public void onSuccess(String result) {
								GWT.log("User added and notification sent \n" + result);
							}

						});
			}

		});
	}

	public void refreshMembersTree(ChannelMessageType event, Long userKey,
			String userEmail) {
		if (event.equals(ChannelMessageType.NEWCONNECTION)
				&& !userEmail.equals(TablePlus.getUser().getEmail())) {
			ModelData model;
			model = treeStore.findModel("name", userEmail);
			model.set("icon", "user-away");
			treeStore.update(model);

			messagingService.sendMessage(TablePlus.getUser().getKey(), "",
					ChannelMessageType.USERSTATUS, userKey, TablePlus
							.getDesktop().getActiveTableKey(),
					new AsyncCallback<String>() {
						@Override
						public void onFailure(Throwable caught) {
							GWT.log("Failed to send user status", caught);
						}

						@Override
						public void onSuccess(String result) {
						}

					});
		} else if (event.equals(ChannelMessageType.DISCONNECTION)) {
			ModelData model;
			model = treeStore.findModel("name", userEmail);
			model.set("icon", "user-offline");
			treeStore.update(model);
		} else if (event.equals(ChannelMessageType.NEWTABLEMEMBER)) {
			ModelData m = new BaseModelData();
			m.set("name", userEmail);
			m.set("icon", "user-offline");
			treeStore.add(m, false);
		} else if (event.equals(ChannelMessageType.USERONLINE)) {
			ModelData model = treeStore.findModel("name", userEmail);
			model.set("icon", "user-online");
			treeStore.update(model);
			Info.display("User status", "User " + userEmail + " is now online");
		} else if (event.equals(ChannelMessageType.USERAWAY)) {
			ModelData model = treeStore.findModel("name", userEmail);
			model.set("icon", "user-away");
			treeStore.update(model);
			Info.display("User status", "User " + userEmail + " went away.");
		} else if (event.equals(ChannelMessageType.USERBUSY)) {

		}
	}

	public void sendInvitationByMail(String toBeInvited) {
		//TODO: parse email address
		if (toBeInvited != null) {
			messagingService.sendInvitationEmail(TablePlus.getUser().getEmail(),
					toBeInvited,this.rightPanel.getTableUI().getTableKey(),
					new AsyncCallback<Boolean>() {

						@Override
						public void onFailure(Throwable caught) {
							GWT.log("Unable to send invitation email");
						}

						@Override
						public void onSuccess(Boolean result) {
							Info.display("Invitation","Email sent successfully");
						}
					});
		}

	}

}
