package com.unito.tableplus.client;

import java.util.Map;

import com.google.gwt.appengine.channel.client.ChannelFactory;
import com.google.gwt.core.client.EntryPoint;
import com.google.gwt.core.client.GWT;
import com.google.gwt.user.client.rpc.AsyncCallback;
import com.unito.tableplus.client.gui.DesktopPlus;
import com.unito.tableplus.client.services.ChannelCreatedCallbackImpl;
import com.unito.tableplus.client.services.LoginServiceAsync;
import com.unito.tableplus.client.services.MessagingServiceAsync;
import com.unito.tableplus.client.services.ServiceFactory;
import com.unito.tableplus.client.services.TableServiceAsync;
import com.unito.tableplus.client.services.UserServiceAsync;
import com.unito.tableplus.shared.model.LoginInfo;
import com.unito.tableplus.shared.model.Table;
import com.unito.tableplus.shared.model.User;
import com.unito.tableplus.shared.model.UserStatus;

public class TablePlus implements EntryPoint {

	private static final UserServiceAsync userService = ServiceFactory
			.getUserServiceInstance();

	private static final TableServiceAsync tableService = ServiceFactory
			.getTableServiceInstance();

	protected final MessagingServiceAsync chatService = ServiceFactory
			.getMessagingServiceInstance();

	private final LoginServiceAsync loginService = ServiceFactory
			.getloginServiceInstance();

	private static String loginUrl;

	private static String logoutUrl;

	private LoginInfo loginInfo = null;

	/**
	 * Current user.
	 */
	private static User user;

	private static DesktopPlus desktop;

	private static Map<Long, Table> tablesMap;

	@Override
	public void onModuleLoad() {
		String homepageURL = com.google.gwt.user.client.Window.Location
				.getHref();

		loginService.login(homepageURL, new AsyncCallback<LoginInfo>() {
			@Override
			public void onFailure(Throwable error) {
				GWT.log("Error in login call: ", error);
			}

			@Override
			public void onSuccess(LoginInfo result) {
				loginInfo = result;
				if (loginInfo.isLoggedIn()) {
					setLogoutUrl(loginInfo.getLogoutUrl());
					loadUser();
				} else {
					loginUrl = loginInfo.getLoginUrl();
					new DesktopPlus();
				}
			}
		});
	}

	/**
	 * Loads the current logged user then calls the loadsTable method.
	 */
	private void loadUser() {
		userService.loadUser(loginInfo, new AsyncCallback<User>() {

			@Override
			public void onFailure(Throwable caught) {
				GWT.log("Failed loading user");
			}

			@Override
			public void onSuccess(User result) {
				result.setStatus(UserStatus.ONLINE);
				user = result;
				loadTables();
			}
		});
	}

	/**
	 * Loads tables for current user then calls the loadsMembers method.
	 */
	private void loadTables() {
		tableService.queryTables(user.getTables(),
				new AsyncCallback<Map<Long, Table>>() {
					@Override
					public void onFailure(Throwable caught) {
						GWT.log("Failed loading user tables");
					}

					@Override
					public void onSuccess(Map<Long, Table> result) {
						tablesMap = result;
						for (final Table t : tablesMap.values()) {
							loadTableMembers(t);
						}
						startCommunicationChannel();
						desktop = new DesktopPlus(tablesMap);
					}
				});
	}

	/**
	 * Loads the members for the loaded tables. Sets current user status as
	 * <b>ONLINE</b> on all tables.
	 * 
	 */
	public static void loadTableMembers(final Table t) {
		userService.queryUsers(t.getMembers(),
				new AsyncCallback<Map<Long, User>>() {

					@Override
					public void onFailure(Throwable caught) {
						GWT.log("Failed loading members for table: "
								+ t.getName(), caught);
					}

					@Override
					public void onSuccess(Map<Long, User> result) {
						result.get(user.getKey()).setStatus(UserStatus.ONLINE);
						t.setUsersMap(result);
						tablesMap.put(t.getKey(), t);
						if(t.getMembers()!= null)
							loadMembersStatus(t);
					}

				});
	}
	
	private static void loadMembersStatus(final Table t){
		tableService.getUsersStatus(t.getKey(), new AsyncCallback<Map<Long, UserStatus>>() {

			@Override
			public void onFailure(Throwable caught) {
				GWT.log("Failed loading members for table: "
						+ t.getName(), caught);
				
			}

			@Override
			public void onSuccess(Map<Long, UserStatus> result) {
				if(result != null)
				for(Long k : result.keySet())
					t.getUsersMap().get(k).setStatus(result.get(k));
			}
			
		});
	}

	/**
	 * Starts the communication channel.
	 */
	private void startCommunicationChannel() {
		final String id = user.getKey().toString();
		chatService.createChannel(id, new AsyncCallback<String>() {

			@Override
			public void onFailure(Throwable caught) {
				GWT.log("Failed getting channel API token", caught);
			}

			@Override
			public void onSuccess(String token) {
				ChannelFactory.createChannel(token,
						new ChannelCreatedCallbackImpl());
			}
		});

	}

	/**
	 * Updates current user querying database.
	 */
	public static void updateUser() {
		userService.queryUser(user.getKey(), new AsyncCallback<User>() {
			@Override
			public void onFailure(Throwable caught) {
				GWT.log("Failure querying user in updateUser.", caught);
			}

			@Override
			public void onSuccess(User result) {
				user = result;
				for (Long key : result.getTables()) {
					if (!tablesMap.containsKey(key)) {
						addNewTable(key);
					}
				}
			}
		});
	}

	private static void addNewTable(Long tableKey) {
		tableService.queryTable(tableKey, new AsyncCallback<Table>() {

			@Override
			public void onFailure(Throwable caught) {
				GWT.log("Failed to add new table.", caught);

			}

			@Override
			public void onSuccess(Table result) {
				tablesMap.put(result.getKey(), result);
				desktop.getRightPanel().getTablesPanel().updateContent();
				desktop.updateContent();
				loadTableMembers(result);
			}

		});
	}

	public static User getUser() {
		return user;
	}

	public static void setUser(User user) {
		TablePlus.user = user;
	}

	public static String getLoginUrl() {
		return loginUrl;
	}

	public static void setLoginUrl(String loginUrl) {
		TablePlus.loginUrl = loginUrl;
	}

	/**
	 * @return the logoutUrl
	 */
	public static String getLogoutUrl() {
		return logoutUrl;
	}

	/**
	 * Sets the logout URL
	 * 
	 * @param logoutUrl
	 *            the logoutUrl to set
	 */
	public static void setLogoutUrl(String logoutUrl) {
		TablePlus.logoutUrl = logoutUrl;
	}

	public static DesktopPlus getDesktop() {
		return desktop;
	}

	public static void setDesktop(DesktopPlus desktopPlus) {
		desktop = desktopPlus;
	}
}
