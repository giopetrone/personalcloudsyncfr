package com.unito.tableplus.client;

import com.extjs.gxt.ui.client.event.ButtonEvent;
import com.extjs.gxt.ui.client.event.SelectionListener;
import com.extjs.gxt.ui.client.widget.Window;
import com.extjs.gxt.ui.client.widget.button.Button;
import com.extjs.gxt.ui.client.widget.layout.FlowLayout;
import com.google.gwt.appengine.channel.client.ChannelFactory;
import com.google.gwt.core.client.EntryPoint;
import com.google.gwt.core.client.GWT;
import com.google.gwt.user.client.rpc.AsyncCallback;
import com.unito.tableplus.client.gui.DesktopPlus;
import com.unito.tableplus.client.gui.TableUI;
import com.unito.tableplus.client.services.ChannelCreatedCallbackImpl;
import com.unito.tableplus.client.services.LoginServiceAsync;
import com.unito.tableplus.client.services.MessagingServiceAsync;
import com.unito.tableplus.client.services.ServiceFactory;
import com.unito.tableplus.client.services.UserServiceAsync;
import com.unito.tableplus.shared.model.LoginInfo;
import com.unito.tableplus.shared.model.User;

public class TablePlus implements EntryPoint {

	private static DesktopPlus desktop;

	private static TableUI personalTable;

	private static final UserServiceAsync userService = ServiceFactory
			.getUserServiceInstance();


	protected final MessagingServiceAsync chatService = ServiceFactory
			.getChatServiceInstance();

	private final LoginServiceAsync loginService = ServiceFactory
			.getloginServiceInstance();

	private static String loginUrl;

	private static String logoutUrl;

	private LoginInfo loginInfo = null;

	private static User user;

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
					loadLoginWindow();
				}
			}
		});
	}

	/**
	 * Crea e visualizza la finestra di login
	 * */
	public void loadLoginWindow() {
		desktop = new DesktopPlus();
		desktop.getTaskBar().disable();

		Window loginWindow = new Window();
		Button loginButton = new Button("Login Google");

		loginButton.addSelectionListener(new SelectionListener<ButtonEvent>() {
			@Override
			public void componentSelected(ButtonEvent ce) {
				com.google.gwt.user.client.Window.open(loginUrl, "_self", "");
			}
		});

		loginWindow.setHeading("Google Login Window");
		loginWindow.setLayout(new FlowLayout());
		loginWindow.add(loginButton);
		loginWindow.setClosable(false);
		desktop.addWindow(loginWindow);
		loginWindow.show();
	}

	/**
	 * Questo metodo viene eseguito se l'utente risulta loggato a TablePlus. Si
	 * occupa di istanziare l'oggetto "user", che rappresenta l'utente corrente.
	 * 
	 */
	public void loadUser() {
		userService.loadUser(loginInfo, new AsyncCallback<User>() {

			@Override
			public void onFailure(Throwable caught) {
				GWT.log("Failed loading user");
			}

			@Override
			public void onSuccess(User result) {
				user = result;
				loadActiveDesktop();
			}
		});
	}
	

	public void loadActiveDesktop() {
		// crea il desktop standard
		desktop = new DesktopPlus();
		desktop.addFixedShortcuts();

		// crea il personalTable
		personalTable = new TableUI();

		// carica il personal table
		desktop.loadPersonalTable(personalTable);
		startCommunicationChannel();
	}	
	
	/**
	 * Avvia il canale di comunicazione per la chat
	 */

	public void startCommunicationChannel() {
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
	
	public static void updateUser(){
		userService.queryUser(user.getKey(), new AsyncCallback<User>() {
			@Override
			public void onFailure(Throwable caught) {
				GWT.log("Failure querying user in updateUser",caught);
			}

			@Override
			public void onSuccess(User result) {
				setUser(result);
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
	 * @param logoutUrl the logoutUrl to set
	 */
	public static void setLogoutUrl(String logoutUrl) {
		TablePlus.logoutUrl = logoutUrl;
	}

	public static DesktopPlus getDesktop() {
		return desktop;
	}

	public static void setDesktop(DesktopPlus desktop) {
		TablePlus.desktop = desktop;
	}

	public static TableUI getPersonalTable() {
		return personalTable;
	}

	public static void setPersonalTable(TableUI personalTable) {
		TablePlus.personalTable = personalTable;
	}
}
