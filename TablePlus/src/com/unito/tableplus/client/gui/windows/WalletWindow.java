package com.unito.tableplus.client.gui.windows;

import com.extjs.gxt.ui.client.event.ButtonEvent;
import com.extjs.gxt.ui.client.event.SelectionListener;
import com.extjs.gxt.ui.client.util.Margins;
import com.extjs.gxt.ui.client.widget.LayoutContainer;
import com.extjs.gxt.ui.client.widget.button.Button;
import com.extjs.gxt.ui.client.widget.layout.VBoxLayoutData;
import com.google.gwt.core.client.GWT;
import com.google.gwt.user.client.Window;
import com.google.gwt.user.client.rpc.AsyncCallback;
import com.unito.tableplus.client.services.DriveService;
import com.unito.tableplus.client.services.DriveServiceAsync;
import com.unito.tableplus.client.services.DropBoxService;
import com.unito.tableplus.client.services.DropBoxServiceAsync;
import com.unito.tableplus.client.services.FacebookService;
import com.unito.tableplus.client.services.FacebookServiceAsync;

public class WalletWindow extends WindowPlus {

	private final DropBoxServiceAsync dropboxService = GWT
			.create(DropBoxService.class);
	private final DriveServiceAsync driveService = GWT
			.create(DriveService.class);
	private final FacebookServiceAsync facebookService = GWT
			.create(FacebookService.class);
	private LayoutContainer container;
	private Button driveButton, dropboxButton, facebookButton;

	public WalletWindow() {
		super();
		setHeading("Wallet");
		
		container = new LayoutContainer();  

		driveButton = new Button("Add Google Drive");
		driveButton.addSelectionListener(new SelectionListener<ButtonEvent>() {
			@Override
			public void componentSelected(ButtonEvent ce) {
				driveService.getAuthorizationURL(new AsyncCallback<String>() {
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

		dropboxButton = new Button("Add Dropbox");
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
		
		facebookButton = new Button("Add Facebook");
		facebookButton
				.addSelectionListener(new SelectionListener<ButtonEvent>() {
					@Override
					public void componentSelected(ButtonEvent ce) {
						facebookService.getAuthUrl(new AsyncCallback<String>() {
							@Override
							public void onFailure(Throwable caught) {
								Window.alert("Unable to retrieve url"+caught);
							}

							@Override
							public void onSuccess(String result) {
								redirect(result);
							}

						});
					}
				});
		
  
        container.add(driveButton, new VBoxLayoutData(new Margins(15, 0, 0, 5)));  
        container.add(dropboxButton, new VBoxLayoutData(new Margins(5, 0, 0, 5)));
        container.add(facebookButton, new VBoxLayoutData(new Margins(5, 0, 0, 5)));
        add(container);
	}

	public void redirect(String url) {
		Window.open(url, "_self", "");
	}

	@Override
	public void updateContent() {
		this.layout();	
	}
}
