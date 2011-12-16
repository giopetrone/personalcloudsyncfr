package com.mui.myproject.client;

import com.extjs.gxt.ui.client.Registry;
import com.extjs.gxt.ui.client.event.ButtonEvent;
import com.extjs.gxt.ui.client.event.SelectionListener;
import com.extjs.gxt.ui.client.widget.ContentPanel;
import com.extjs.gxt.ui.client.widget.Info;
import com.extjs.gxt.ui.client.widget.Window;
import com.extjs.gxt.ui.client.widget.button.Button;
import com.extjs.gxt.ui.client.widget.button.ToggleButton;
import com.extjs.gxt.ui.client.widget.tips.ToolTipConfig;
import com.google.gwt.core.client.GWT;
import com.google.gwt.user.client.rpc.AsyncCallback;
import com.mui.myproject.client.components.LinkFeedPopup;
import com.mui.myproject.client.services.FeedServiceAsync;
import com.mui.myproject.client.services.MyService;
import com.mui.myproject.client.services.MyServiceAsync;
import com.mui.myproject.client.windows.FeedWindow;
import com.mui.myproject.shared.model.Feed;

public class TesiNavigationPanel extends ContentPanel {

	MyServiceAsync service = (MyServiceAsync) GWT.create(MyService.class);

	public TesiNavigationPanel() {
		setHeading("Navigation");

		// aggiunge un button per il link
		final ToggleButton btnLinkFeed = new ToggleButton("Link feed");
		btnLinkFeed.setIconStyle("link-feed");
		addButton(btnLinkFeed);

		// crea un tooltip e lo aggiunge al link button
		ToolTipConfig linkFeedToolTipConfig = new ToolTipConfig();
		linkFeedToolTipConfig.setTitle("Link to existing RSS feed");
		linkFeedToolTipConfig
				.setText("Allows you to enter the URL of an existing RSS feed you would like to link to");
		btnLinkFeed.setToolTip(linkFeedToolTipConfig);

		// crea un popup per link button
		final LinkFeedPopup linkFeedPopup = new LinkFeedPopup();
		linkFeedPopup.setConstrainViewport(true);

		// aggiunge un listener al link button
		btnLinkFeed.addSelectionListener(new SelectionListener<ButtonEvent>() {
			@Override
			public void componentSelected(ButtonEvent ce) {
				if (btnLinkFeed.isPressed()) {
					linkFeedPopup.show(btnLinkFeed.getElement(), "bl-tl?");
				} else {
					linkFeedPopup.hide();
				}
			}
		});

		// aggiunge il button per create
		final Button btnCreateFeed = new Button("RPC call");
		btnCreateFeed.setIconStyle("create-feed");

		// aggiunge listener a create button
		btnCreateFeed
				.addSelectionListener(new SelectionListener<ButtonEvent>() {
					@Override
					public void componentSelected(ButtonEvent ce) {
						createNewFeedWindow();
						chiamaServizio("uno");
						
					}
				});

		ToolTipConfig createNewToolTipConfig = new ToolTipConfig();
		createNewToolTipConfig.setTitle("Create a new RSS feed");
		createNewToolTipConfig.setText("Creates a new RSS feed");
		btnCreateFeed.setToolTip(createNewToolTipConfig);

		addButton(btnCreateFeed);

	}

	private void createNewFeedWindow() {
		final FeedServiceAsync feedService = Registry
				.get(RSSReaderConstants.FEED_SERVICE);
		feedService.createNewFeed(new AsyncCallback<Feed>() {
			@Override
			public void onFailure(Throwable caught) {
				Info.display("RSSReader", "Unable to create a new feed");
			}

			@Override
			public void onSuccess(Feed feed) {
				final Window newFeedWindow = new FeedWindow(feed,ret);
				newFeedWindow.show();
			}
		});
	}

	String ret=null;
	void chiamaServizio(String prova) {
		service.myMethod(prova, new AsyncCallback<String>() {
			public void onFailure(Throwable caught) {
				System.out.println("Chiamata fallita");
			}

			public void onSuccess(String result) {
				ret=result;
			}
		});
	}
}
