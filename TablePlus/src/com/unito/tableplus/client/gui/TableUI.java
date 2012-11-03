package com.unito.tableplus.client.gui;

import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;
import com.extjs.gxt.desktop.client.Shortcut;
import com.unito.tableplus.client.gui.windows.BlackBoardWindow;
import com.unito.tableplus.client.gui.windows.BookmarkWindow;
import com.unito.tableplus.client.gui.windows.BookmarkWindowList;
import com.unito.tableplus.client.gui.windows.ChatWindow;
import com.unito.tableplus.client.gui.windows.TableResourcesWindow;
import com.unito.tableplus.client.gui.windows.WindowPlus;
import com.unito.tableplus.shared.model.Bookmark;
import com.unito.tableplus.shared.model.Table;

public class TableUI {

	private RightPanel rightPanel;
	private List<WindowPlus> windows = new ArrayList<WindowPlus>();
	private List<Shortcut> shortcuts = new ArrayList<Shortcut>();
	private Shortcut s1;
	private Shortcut s2;
	private Shortcut s3;
	private Shortcut s4;
	private WindowPlus chatWindow;
	private WindowPlus blackboardWindow;
	private WindowPlus tableResourcesWindow;
	private WindowPlus bookmarkWindowList;
	private WindowPlus bookmarkWindow;
	private Table table;
	private String tableName;
	private Long tableKey;
	private List<Long> tableMembers;
	private List<String> allTags= new LinkedList<String>();
	/**
	 * Costruttore per personal table
	 * 
	 * @return void
	 */

	public TableUI() {
		this.rightPanel = new RightPanel(this, false);
		this.setTableKey(0L);
	}

	// clicked except for chatwindow, look at commented code below
	public TableUI(Table table) {
		this.setTable(table);
		this.setTableName(table.getName());
		this.setTableKey(table.getKey());
		this.setTableMembers(table.getMembers());
		this.rightPanel = new RightPanel(this, true);

		tableResourcesWindow = new TableResourcesWindow();
		chatWindow = new ChatWindow(this);
		blackboardWindow = new BlackBoardWindow(getTable());
		bookmarkWindowList = new BookmarkWindowList(this, getTable());
		

		addWindow(tableResourcesWindow);
		addWindow(chatWindow);
		addWindow(blackboardWindow);
		addWindow(bookmarkWindowList);

		// table resources
		s1 = new Shortcut();
		s1.setText("Table Resources");
		s1.setId("tableresources-win-shortcut");
		s1.setData("window", tableResourcesWindow);
		this.addShortcut(s1);

		// chat
		s2 = new Shortcut();
		s2.setText("Table Chat");
		s2.setId("chat-win-shortcut");
		s2.setData("window", chatWindow);
		this.addShortcut(s2);

		// blackboard
		s3 = new Shortcut();
		s3.setText("Blackboard");
		s3.setId("blackboard-win-shortcut");
		s3.setData("window", blackboardWindow);
		this.addShortcut(s3);

		// my bookmark list

		s4 = new Shortcut();
		s4.setText("My Bookmark List");
		s4.setId("resource-win-shortcut");
		s4.setData("window", bookmarkWindowList);
		this.addShortcut(s4);

	}

	public RightPanel getRightPanel() {
		return rightPanel;
	}

	public void setRightPanel(RightPanel rightPanel) {
		this.rightPanel = rightPanel;
	}

	public List<WindowPlus> getWindows() {
		return windows;
	}

	public void setWindows(List<WindowPlus> windows) {
		this.windows = windows;
	}

	public void addWindow(WindowPlus wp) {
		this.windows.add(wp);
	}

	public List<Shortcut> getShortcuts() {
		return shortcuts;
	}

	public void setShortcuts(List<Shortcut> shortcuts) {
		this.shortcuts = shortcuts;
	}

	public void addShortcut(Shortcut s) {
		this.shortcuts.add(s);
	}

	/**
	 * @return the tableMembers
	 */
	public List<Long> getTableMembers() {
		return tableMembers;
	}

	/**
	 * @param tableMembers
	 *            the tableMembers to set
	 */
	public void setTableMembers(List<Long> tableMembers) {
		this.tableMembers = tableMembers;
	}

	/**
	 * @return the tableName
	 */
	public String getTableName() {
		return tableName;
	}

	/**
	 * @param tableName
	 *            the tableName to set
	 */
	public void setTableName(String tableName) {
		this.tableName = tableName;
	}

	/**
	 * @return the tableKey
	 */
	public Long getTableKey() {
		return tableKey;
	}

	/**
	 * @param tableKey
	 *            the tableKey to set
	 */
	public void setTableKey(Long tableKey) {
		this.tableKey = tableKey;
	}

	public WindowPlus getTableChatWindow() {
		return chatWindow;
	}

	public void setTableChatWindow(WindowPlus tableChatWindow) {
		this.chatWindow = tableChatWindow;
	}

	/**
	 * @return the table
	 */
	public Table getTable() {
		return table;
	}

	/**
	 * @param table
	 *            the table to set
	 */
	public void setTable(Table table) {
		this.table = table;
	}

	public void chatNotify(boolean notify) {
		if (notify) {
			this.s2.setId("chat-win-shortcut-notify");
			this.s2.repaint();
		} else {
			this.s2.setId("chat-win-shortcut");
			this.s2.repaint();
		}
	}

	public void showBookmarkWindow(Bookmark b){
        this.bookmarkWindow = new BookmarkWindow(b);
        this.addWindow(this.bookmarkWindow);
        this.bookmarkWindow.show();
	}

	public List<String> getAllTags() {
		return allTags;
	}
	
	public void setAllTags(List<String> allTags) {
		this.allTags=allTags;
	}

}
