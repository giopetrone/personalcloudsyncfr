package com.unito.tableplus.client.gui;

import com.unito.tableplus.shared.model.User;

public class PersonalTable extends Table {

	public PersonalTable(DesktopPlus desktop_, User user_) {

		super(desktop_, user_, new PersonalPanel(desktop_, user_));

	}

}
