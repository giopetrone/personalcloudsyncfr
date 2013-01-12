package com.unito.tableplus.client.gui;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import com.extjs.gxt.desktop.client.Desktop;
import com.extjs.gxt.desktop.client.Shortcut;
import com.extjs.gxt.desktop.client.StartMenu;
import com.extjs.gxt.ui.client.Style.Orientation;
import com.extjs.gxt.ui.client.event.ButtonEvent;
import com.extjs.gxt.ui.client.event.ComponentEvent;
import com.extjs.gxt.ui.client.event.Listener;
import com.extjs.gxt.ui.client.event.MenuEvent;
import com.extjs.gxt.ui.client.event.MessageBoxEvent;
import com.extjs.gxt.ui.client.event.SelectionListener;
import com.extjs.gxt.ui.client.util.IconHelper;
import com.extjs.gxt.ui.client.util.Margins;
import com.extjs.gxt.ui.client.widget.Dialog;
import com.extjs.gxt.ui.client.widget.Info;
import com.extjs.gxt.ui.client.widget.MessageBox;
import com.extjs.gxt.ui.client.widget.Window;
import com.extjs.gxt.ui.client.widget.button.Button;
import com.extjs.gxt.ui.client.widget.layout.FlowLayout;
import com.extjs.gxt.ui.client.widget.layout.RowData;
import com.extjs.gxt.ui.client.widget.layout.RowLayout;
import com.extjs.gxt.ui.client.widget.menu.Menu;
import com.extjs.gxt.ui.client.widget.menu.MenuItem; 
import com.google.gwt.core.client.GWT;
import com.google.gwt.user.client.rpc.AsyncCallback;
import com.unito.tableplus.client.TablePlus;
import com.unito.tableplus.client.gui.panels.RightPanel;
import com.unito.tableplus.client.gui.windows.BlackBoardWindow;
import com.unito.tableplus.client.gui.windows.BookmarkWindow;
import com.unito.tableplus.client.gui.windows.MyObjectsWindow;
import com.unito.tableplus.client.gui.windows.ChatWindow;
import com.unito.tableplus.client.gui.windows.TableObjectsWindow;
import com.unito.tableplus.client.gui.windows.WalletWindow;
import com.unito.tableplus.client.gui.windows.WindowPlus;
import com.unito.tableplus.client.services.MessagingServiceAsync;
import com.unito.tableplus.client.services.ServiceFactory;
import com.unito.tableplus.client.services.TableServiceAsync;
import com.unito.tableplus.client.services.UserServiceAsync;
import com.unito.tableplus.shared.model.ChannelMessageType;
import com.unito.tableplus.shared.model.Table;

public class DesktopPlus extends Desktop {

	private static final UserServiceAsync userService = ServiceFactory
			.getUserServiceInstance();
	private static final TableServiceAsync tableService = ServiceFactory
			.getTableServiceInstance();
	private static final MessagingServiceAsync messagingService = ServiceFactory
			.getMessagingServiceInstance();

	private Map<Long, Table> tables;
	private Map<Long, Boolean> chatStatus;
	private Table activeTable;

	private RightPanel rightPanel;

	private StartMenu startMenu;

	private WindowPlus walletWindow;
	private WindowPlus chatWindow;
	private WindowPlus blackboardWindow;
	private WindowPlus tableObjectsWindow;
	private WindowPlus myObjectsWindow;
	private WindowPlus bookmarkWindow;

	private Shortcut walletShortcut;
	private Shortcut tableObjectsShortcut;
	private Shortcut chatShortcut;
	private Shortcut blackboardShortcut;
	private Shortcut myObjectsShortcut;

	private SelectionListener<ComponentEvent> shortcutListener;

	private Menu switchTableMenu;

	private List<String> allTags;

	/**
	 * This constructor must be called when user is <b>NOT</b> logged in. It
	 * shows the login window ad a minimal, disabled desktop.
	 * 
	 * @param loginUrl
	 *            The url where the user will be redirected for logging in.
	 */
	public DesktopPlus() {
		super.getTaskBar().disable();

		Window loginWindow = new Window();
		Button loginButton = new Button("Login");

		loginButton.addSelectionListener(new SelectionListener<ButtonEvent>() {
			@Override
			public void componentSelected(ButtonEvent ce) {
				com.google.gwt.user.client.Window.open(TablePlus.getLoginUrl(),
						"_self", "");
			}
		});

		loginWindow.setHeading("Google Login");
		loginWindow.setLayout(new FlowLayout());
		loginWindow.add(loginButton);
		loginWindow.setClosable(false);
		addWindow(loginWindow);
		loginWindow.show();
	}

	/**
	 * This constructor generates the full desktop environment. Initializes
	 * shortcuts and windows.
	 * 
	 * @param tables
	 *            The map of tables which logged user belongs to.
	 */
	public DesktopPlus(Map<Long, Table> tables) {
		super();
		TablePlus.setDesktop(this);
		super.desktop.setLayout(new RowLayout(Orientation.HORIZONTAL));

		this.tables = tables;

		chatStatus = new HashMap<Long, Boolean>();

		walletWindow = new WalletWindow();
		tableObjectsWindow = new TableObjectsWindow();
		chatWindow = new ChatWindow();
		blackboardWindow = new BlackBoardWindow();
		myObjectsWindow = new MyObjectsWindow();

		this.shortcutListener = new SelectionListener<ComponentEvent>() {
			@Override
			public void componentSelected(ComponentEvent ce) {
				WindowPlus w = ce.getComponent().getData("window");
				w.show();
				w.toFront();
			}
		};

		walletShortcut = new Shortcut();
		walletShortcut.setText("Wallet");
		walletShortcut.setId("wallet-win-shortcut");
		walletShortcut.setData("window", walletWindow);
		walletShortcut.setVisible(true);
		walletShortcut.addSelectionListener(shortcutListener);

		myObjectsShortcut = new Shortcut();
		myObjectsShortcut.setText("My Objects");
		myObjectsShortcut.setId("myresources-win-shortcut");
		myObjectsShortcut.setData("window", myObjectsWindow);
		myObjectsShortcut.setVisible(true);
		myObjectsShortcut.addSelectionListener(shortcutListener);

		tableObjectsShortcut = new Shortcut();
		tableObjectsShortcut.setText("Table Objects");
		tableObjectsShortcut.setId("tableresources-win-shortcut");
		tableObjectsShortcut.setData("window", tableObjectsWindow);
		tableObjectsShortcut.setVisible(false);
		tableObjectsShortcut.addSelectionListener(shortcutListener);

		chatShortcut = new Shortcut();
		chatShortcut.setText("Table Chat");
		chatShortcut.setId("chat-win-shortcut");
		chatShortcut.setData("window", chatWindow);
		chatShortcut.setVisible(false);
		chatShortcut.addSelectionListener(shortcutListener);

		blackboardShortcut = new Shortcut();
		blackboardShortcut.setText("Blackboard");
		blackboardShortcut.setId("blackboard-win-shortcut");
		blackboardShortcut.setData("window", blackboardWindow);
		blackboardShortcut.setVisible(false);
		blackboardShortcut.addSelectionListener(shortcutListener);

		this.addWindow(tableObjectsWindow);
		this.addWindow(chatWindow);
		this.addWindow(blackboardWindow);
		this.addWindow(myObjectsWindow);
		this.addWindow(walletWindow);

		this.addShortcut(walletShortcut);
		this.addShortcut(myObjectsShortcut);
		this.addShortcut(tableObjectsShortcut);
		this.addShortcut(chatShortcut);
		this.addShortcut(blackboardShortcut);

		rightPanel = new RightPanel();
		int leftMargin = (int) (super.desktop.getWidth() * 0.75);
		super.desktop.add(rightPanel, new RowData(1, 1, new Margins(8, 8, 8,
				leftMargin)));
		super.desktop.layout();

		startMenu = super.getStartMenu();
		startMenu.setHeading(TablePlus.getUser().getEmail());
		startMenu.setIconStyle("user");

		MenuItem newTableItem = new MenuItem("Create new Table");
		newTableItem.setIconStyle("monitor_add");
		newTableItem.addSelectionListener(new SelectionListener<MenuEvent>() {
			@Override
			public void componentSelected(MenuEvent ce) {
				createNewTable();
			}
		});

		MenuItem personalTableItem = new MenuItem("Go to Personal Table");
		personalTableItem.setIconStyle("monitor");
		personalTableItem
				.addSelectionListener(new SelectionListener<MenuEvent>() {
					@Override
					public void componentSelected(MenuEvent ce) {
						switchToPersonalTable();
					}
				});

		switchTableMenu = new Menu();

		MenuItem switchTableItem = new MenuItem("Switch to table");
		switchTableItem.setSubMenu(switchTableMenu);
		switchTableItem.setIconStyle("monitor_go");

		switchTableMenu.add(personalTableItem);

		startMenu.add(personalTableItem);
		startMenu.add(newTableItem);
		startMenu.add(switchTableItem);

		MenuItem settings = new MenuItem("Settings");
		settings.setIcon(IconHelper.createStyle("settings"));
		settings.addSelectionListener(new SelectionListener<MenuEvent>() {
			@Override
			public void componentSelected(MenuEvent ce) {
				Info.display("Settings", "Not implemented yet...");
			}
		});

		startMenu.addTool(settings);
		startMenu.addToolSeperator();

		settings = new MenuItem("Logout");
		settings.setIcon(IconHelper.createStyle("logout"));
		settings.addSelectionListener(new SelectionListener<MenuEvent>() {
			@Override
			public void componentSelected(MenuEvent ce) {
				// TODO: send message and close channel
				redirect(TablePlus.getLogoutUrl());
			}
		});
		startMenu.addTool(settings);

		allTags = new ArrayList<String>();
		updateContent();
	}

	public void switchToPersonalTable() {
		if (activeTable != null) {
			sendStatusChange(ChannelMessageType.USERAWAY, getActiveTableKey());
			activeTable.setActive(false);

			activeTable = null;
			tableObjectsShortcut.setVisible(false);
			chatShortcut.setVisible(false);
			blackboardShortcut.setVisible(false);
			chatWindow.setVisible(false);
			blackboardWindow.setVisible(false);
			tableObjectsWindow.setVisible(false);
			myObjectsWindow.setVisible(false);
			rightPanel.updateContent();
		}
	}

	public void switchToTable(Long tableKey) {
		if (activeTable != null) {
			sendStatusChange(ChannelMessageType.USERAWAY, getActiveTableKey());
			activeTable.setActive(false);
		}
		activeTable = tables.get(tableKey);
		activeTable.setActive(true);
		tableObjectsShortcut.setVisible(true);
		chatShortcut.setVisible(true);
		setChatIcon();
		blackboardShortcut.setVisible(true);

		sendStatusChange(ChannelMessageType.USERONLINE, getActiveTableKey());
		chatWindow.updateContent();
		blackboardWindow.updateContent();
		tableObjectsWindow.updateContent();
		myObjectsWindow.updateContent();
		rightPanel.updateContent();
	}

	/**
	 * Creates a new table and stores table data. If everything goes fine
	 * corresponding Table will be stored and added to current user.
	 */
	public void createNewTable() {

		final MessageBox box = MessageBox.prompt("New Table",
				"Please enter new Table name:");
		box.setButtons(MessageBox.OKCANCEL);
		box.addCallback(new Listener<MessageBoxEvent>() {

			@Override
			public void handleEvent(MessageBoxEvent be) {
				if (be.getButtonClicked().getItemId().equals(Dialog.OK)) {
					// TODO Check table name is not empty
					storeNewTable(be.getValue());
				}
			}

		});
	}

	private void sendStatusChange(ChannelMessageType type, Long tableKey) {
		messagingService.sendMessage(TablePlus.getUser().getKey(), "", type,
				activeTable.getKey(), new AsyncCallback<String>() {

					@Override
					public void onFailure(Throwable caught) {
						GWT.log("Failed to send AWAY notification to table."
								+ caught);
					}

					@Override
					public void onSuccess(String result) {
						GWT.log(result);
					}
				});
	}

	private void storeNewTable(String tableName) {
		final Table newTable = new Table(TablePlus.getUser().getKey());
		newTable.setName(tableName);
		tableService.storeNewTable(newTable, TablePlus.getUser(),
				new AsyncCallback<Table>() {

					@Override
					public void onFailure(Throwable caught) {
						Info.display(
								"Table creation",
								"Impossible to create table "
										+ newTable.getName());
						GWT.log("Error creating table " + newTable.getName(),
								caught);
					}

					@Override
					public void onSuccess(Table result) {
						if (result != null) {
							Info.display("Table creation",
									"Table " + result.getName()
											+ " has been created.");
							GWT.log("Table " + result.getName()
									+ " has been created.");
							TablePlus.updateUser();
						} else {
							Info.display(
									"Table creation",
									"Failed to create table "
											+ newTable.getName());
							GWT.log("Failed to create table "
									+ newTable.getName());
						}
					}

				});
	}

	public void updateContent() {
		switchTableMenu.removeAll();
		MenuItem tableItem;
		for (Table t : tables.values()) {
			tableItem = new MenuItem(t.getName());
			tableItem.setId(t.getKey().toString());
			tableItem.setIconStyle("monitor");

			tableItem.addSelectionListener(new SelectionListener<MenuEvent>() {
				@Override
				public void componentSelected(MenuEvent ce) {
					Long tableKey = Long.parseLong(ce.getItem().getId());
					switchToTable(tableKey);
				}
			});
			switchTableMenu.add(tableItem);
		}

	}

	public void setChatStatus(Long tableKey, boolean notify) {
		chatStatus.put(tableKey, notify);
		if (tableKey.equals(activeTable.getKey()))
			if (notify && !chatWindow.isVisible())
				chatShortcut.setId("chat-win-shortcut-notify");
			else
				chatShortcut.setId("chat-win-shortcut");
		chatShortcut.repaint();
	}

	private void setChatIcon() {
		if (chatStatus.containsKey(activeTable.getKey())) {
			if (chatStatus.get(activeTable.getKey()))
				chatShortcut.setId("chat-win-shortcut-notify");
			else
				chatShortcut.setId("chat-win-shortcut");
			chatShortcut.repaint();
		}
	}

	public void showBookmarkWindow(Object b) {
		this.bookmarkWindow = new BookmarkWindow(b);
		this.addWindow(this.bookmarkWindow);
		this.bookmarkWindow.show();
	}

	public Map<Long, Table> getTables() {
		return tables;
	}

	public void setTables(Map<Long, Table> tables) {
		this.tables = tables;
	}

	public Long getActiveTableKey() {
		return activeTable.getKey();
	}

	public Table getActiveTable() {
		return activeTable;
	}

	public RightPanel getRightPanel() {
		return rightPanel;
	}

	public WindowPlus getChatWindow() {
		return chatWindow;
	}

	public List<String> getAllTags() {
		return allTags;
	}

	public void setAllTags(List<String> allTags) {
		this.allTags = allTags;
	}

	public static native void redirect(String url)
	/*-{
		$wnd.location = url;
	}-*/;

}
