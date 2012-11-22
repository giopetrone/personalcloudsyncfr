package com.unito.tableplus.client.gui.windows;

import com.extjs.gxt.ui.client.widget.Window;

public abstract class WindowPlus extends Window {

	public WindowPlus() {
		super();
		setMinimizable(true);
		setMaximizable(true);
		setSize(400, 300);
	}

	public abstract void updateContent();
}
