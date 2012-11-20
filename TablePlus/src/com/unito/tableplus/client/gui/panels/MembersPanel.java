package com.unito.tableplus.client.gui.panels;

import java.util.Map;

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
import com.unito.tableplus.client.services.MessagingServiceAsync;
import com.unito.tableplus.client.services.ServiceFactory;
import com.unito.tableplus.client.services.TableServiceAsync;
import com.unito.tableplus.client.services.UserServiceAsync;
import com.unito.tableplus.shared.model.ChannelMessageType;
import com.unito.tableplus.shared.model.User;
import com.unito.tableplus.shared.model.UserStatus;

public class MembersPanel extends ContentPanel {

	private final UserServiceAsync userService = ServiceFactory
			.getUserServiceInstance();
	private final TableServiceAsync tableService = ServiceFactory
			.getTableServiceInstance();
	private final MessagingServiceAsync messagingService = ServiceFactory
			.getMessagingServiceInstance();

	private LayoutContainer leftLayoutContainer;
	private LayoutContainer rightLayoutContainer;
	private TreePanel<ModelData> treePanel;
	private TreeStore<ModelData> treeStore;

	public MembersPanel() {
		setCollapsible(false);
		setTitleCollapse(false);
		setBodyStyle("backgroundColor: white;");
		setLayout(new RowLayout(Orientation.VERTICAL));

		leftLayoutContainer = new LayoutContainer();
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
						TablePlus.getDesktop().switchToPersonalTable();
					}
				});

		Button inviteUser = new Button();
		inviteUser.setToolTip(new ToolTipConfig("Add a member"));
		inviteUser.setIcon(IconHelper.createStyle("invite-user"));
		inviteUser.addSelectionListener(new SelectionListener<ButtonEvent>() {
			@Override
			public void componentSelected(ButtonEvent ce) {
				MessageBox box = MessageBox.prompt("Contact",
						"Please enter new member email:");
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

		rightLayoutContainer = new LayoutContainer();
		treeStore = new TreeStore<ModelData>();
		treeStore.setMonitorChanges(true);
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

		add(leftLayoutContainer);
		add(rightLayoutContainer);

	}

	public void updateContent() {
		mask();
		treeStore.removeAll();
		Map<Long, User> users = TablePlus.getDesktop().getTables()
				.get(TablePlus.getDesktop().getActiveTableKey()).getUsersMap();
		if (users != null) {
			ModelData data;
			for (User user : users.values()) {
				data = new BaseModelData();
				data.set("id", user.getKey());
				data.set("name", user.getEmail());
				data.set("icon", getStatusIcon(user.getStatus()));
				treeStore.add(data, false);
			}
		}
		unmask();
	}

	public void updateUserStatus(Long id, UserStatus status) {
		ModelData model = treeStore.findModel("id", id);
		model.set("icon", getStatusIcon(status));
		treeStore.update(model);
	}

	private String getStatusIcon(UserStatus status) {
		String icon = "";
		if (status.equals(UserStatus.OFFLINE))
			icon = "user-offline";
		else if (status.equals(UserStatus.ONLINE))
			icon = "user-online";
		else if (status.equals(UserStatus.AWAY))
			icon = "user-away";
		else if (status.equals(UserStatus.BUSY))
			icon = "user-busy";
		return icon;
	}

	private void inviteUser(MessageBoxEvent be) {
		if (be.getButtonClicked().getItemId().equals(Dialog.OK)) {
			final String email = be.getValue();
			userService.queryUser("email", email, new AsyncCallback<User>() {
				@Override
				public void onFailure(Throwable caught) {
					GWT.log("Failed querying user", caught);
				}

				@Override
				public void onSuccess(User result) {
					if (result == null)
						confirmEmail(email);
					else {
						Long activeTableKey = TablePlus.getDesktop()
								.getActiveTableKey();
						if (result.getTables().contains(activeTableKey)) {
							MessageBox.info("Info", "User is already a table member", null);
						} else
							confirmAdd(result);
					}
				}
			});
		}
	}

	private void confirmEmail(final String email) {
		MessageBox
				.confirm(
						"Confirm",
						"This address is not in our database, do you want to sent an invitation by email?",
						new Listener<MessageBoxEvent>() {
							@Override
							public void handleEvent(MessageBoxEvent ce) {
								Button btn = ce.getButtonClicked();
								if (btn.getText().equals("Yes"))
									sendInvitationEmail(email);
							}
						});
	}

	private void confirmAdd(final User newUser) {
		MessageBox
				.confirm(
						"Confirm",
						"This address is in our database, "
								+ "the corrisponding user will gain access to "
								+ "every document shared by this table. Do you wish to continue?",
						new Listener<MessageBoxEvent>() {
							@Override
							public void handleEvent(MessageBoxEvent ce) {
								Button btn = ce.getButtonClicked();
								if (btn.getText().equals("Yes"))
									addMember(newUser);
							}
						});

	}

	/**
	 * Adds a user to current table
	 * 
	 * @return void
	 */

	public void addMember(final User newUser) {
		Long currentUserKey = TablePlus.getUser().getKey();
		final Long newUserKey = newUser.getKey();
		final Long tableKey = TablePlus.getDesktop().getActiveTable().getKey();
		tableService.addMember(currentUserKey, newUserKey, tableKey,
				new AsyncCallback<Void>() {

					@Override
					public void onFailure(Throwable caught) {
						GWT.log("Failed adding user", caught);
					}

					@Override
					public void onSuccess(Void v) {
						Info.display("Result",
								"User has been successfully added to table.");
						messagingService.sendMessage(TablePlus.getUser()
								.getKey(), newUserKey.toString(),
								ChannelMessageType.NEWTABLEMEMBER,
								tableKey, new AsyncCallback<String>() {

									@Override
									public void onFailure(Throwable caught) {
										GWT.log("Failed to notify other users",
												caught);
									}

									@Override
									public void onSuccess(String result) {
										GWT.log("User added and notification sent \n"
												+ result);
									}

								});
					}

				});
	}

	public void sendInvitationEmail(String email) {
		// TODO: parse email address
		final Long tableKey = TablePlus.getDesktop().getActiveTable().getKey();
		if (email != null) {
			messagingService.sendInvitationEmail(
					TablePlus.getUser().getEmail(), email, tableKey,
					new AsyncCallback<Boolean>() {

						@Override
						public void onFailure(Throwable caught) {
							GWT.log("Unable to send invitation email");
						}

						@Override
						public void onSuccess(Boolean result) {
							Info.display("Invitation",
									"Email sent successfully");
						}
					});
		}

	}
}
