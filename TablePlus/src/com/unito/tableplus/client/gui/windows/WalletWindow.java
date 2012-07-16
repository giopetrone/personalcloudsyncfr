package com.unito.tableplus.client.gui.windows;

import com.extjs.gxt.ui.client.event.ButtonEvent;
import com.extjs.gxt.ui.client.event.SelectionListener;
import com.extjs.gxt.ui.client.widget.button.Button;
import com.google.gwt.core.client.GWT;
import com.google.gwt.user.client.Window;
import com.google.gwt.user.client.rpc.AsyncCallback;
import com.unito.tableplus.client.services.DriveService;
import com.unito.tableplus.client.services.DriveServiceAsync;
import com.unito.tableplus.client.services.DropBoxService;
import com.unito.tableplus.client.services.DropBoxServiceAsync;

public class WalletWindow extends WindowPlus {

	private final DropBoxServiceAsync dropboxService = GWT
			.create(DropBoxService.class);
	private final DriveServiceAsync driveService = GWT
			.create(DriveService.class);

	public WalletWindow() {
		super();
		setHeading("Wallet");

		Button driveButton = new Button("Add Google Drive");
		driveButton.addSelectionListener(new SelectionListener<ButtonEvent>() {
			@Override
			public void componentSelected(ButtonEvent ce) {
				driveService.getRequestTokenURL(new AsyncCallback<String>() {
					@Override
					public void onFailure(Throwable caught) {
					}

					@Override
					public void onSuccess(String result) {
						redirect(result);
					}
				});
			}
		});

		Button dropboxButton = new Button("Add Dropbox");
		dropboxButton
				.addSelectionListener(new SelectionListener<ButtonEvent>() {
					@Override
					public void componentSelected(ButtonEvent ce) {
						dropboxService.getAuthUrl(new AsyncCallback<String>() {
							@Override
							public void onFailure(Throwable caught) {
								Window.alert("Unable to retrieve url");
							}

							@Override
							public void onSuccess(String result) {
								redirect(result);
							}

						});
					}
				});
//FIXME: manage buttons enabling criterias
//		if (TablePlus.user.getDropboxToken() != null)
//			dropboxButton.setEnabled(false);
//		if (TablePlus.user.getGoogleToken() != null)
//			driveButton.setEnabled(false);
		
		add(driveButton);
		add(dropboxButton);
	}

	public void redirect(String url) {
		Window.open(url, "_self", "");
	}
}
