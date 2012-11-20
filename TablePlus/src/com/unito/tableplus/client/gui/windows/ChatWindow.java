package com.unito.tableplus.client.gui.windows;

import com.extjs.gxt.ui.client.Style.Orientation;
import com.extjs.gxt.ui.client.Style.Scroll;
import com.extjs.gxt.ui.client.event.ComponentEvent;
import com.extjs.gxt.ui.client.event.Events;
import com.extjs.gxt.ui.client.event.KeyListener;
import com.extjs.gxt.ui.client.event.Listener;
import com.extjs.gxt.ui.client.util.Margins;
import com.extjs.gxt.ui.client.widget.HtmlContainer;
import com.extjs.gxt.ui.client.widget.Info;
import com.extjs.gxt.ui.client.widget.LayoutContainer;
import com.extjs.gxt.ui.client.widget.form.TextArea;
import com.extjs.gxt.ui.client.widget.layout.FitLayout;
import com.extjs.gxt.ui.client.widget.layout.RowData;
import com.extjs.gxt.ui.client.widget.layout.RowLayout;
import com.google.gwt.core.client.GWT;
import com.google.gwt.user.client.rpc.AsyncCallback;
import com.unito.tableplus.client.TablePlus;
import com.unito.tableplus.client.services.MessagingServiceAsync;
import com.unito.tableplus.client.services.ServiceFactory;
import com.unito.tableplus.shared.model.ChannelMessageType;
import com.unito.tableplus.shared.model.Table;

public class ChatWindow extends WindowPlus {

	protected final MessagingServiceAsync messagingService = ServiceFactory
			.getMessagingServiceInstance();

	private Table activeTable;
	private LayoutContainer mainContainer;
	private HtmlContainer historyContainer;
	private TextArea inputArea;

	private final String welcomeMessage = "<div align=\"center\"><b>Welcome into the chat!</b> <br><br>"
			+ "It seems like none of your friends had nothing to say since your coming. "
			+ "Be the first to say '<i>Hi!</i>'</div>";

	public ChatWindow() {
		super();

		setHeading("Table Chat");
		setLayout(new RowLayout(Orientation.VERTICAL));
		setVisible(false);

		mainContainer = new LayoutContainer();
		mainContainer.setLayout(new FitLayout());
		mainContainer.setScrollMode(Scroll.AUTOY);

		historyContainer = new HtmlContainer(welcomeMessage);
		historyContainer.scrollIntoView(mainContainer);

		inputArea = new TextArea();
		inputArea.setHeight(50);
		inputArea.setWidth(width);

		inputArea.addKeyListener(new KeyListener() {
			@Override
			public void componentKeyPress(ComponentEvent event) {
				if (event.getKeyCode() == 13)
					sendMessage();
			}
		});

		mainContainer.add(historyContainer);
		add(mainContainer, new RowData(1, 1, new Margins(4)));
		add(inputArea, new RowData(1, -1, new Margins(4)));

		this.addListener(Events.Show, new Listener<ComponentEvent>() {
			public void handleEvent(ComponentEvent ce) {
			}
		});
	}


	public void sendMessage() {
		if (inputArea.getValue() != null)
			messagingService.sendMessage(TablePlus.getUser().getKey(),
					inputArea.getValue(), ChannelMessageType.CHAT, activeTable.getKey(),
					new AsyncCallback<String>() {

						@Override
						public void onFailure(Throwable caught) {
							GWT.log("Error sending message", caught);
							Info.display("Chat Error",
									"Message could not be sent. Please try again.");
						}

						@Override
						public void onSuccess(String result) {
							inputArea.setValue("");
							updateContent();
						}

					});
	}

	@Override
	public void updateContent() {
		activeTable = TablePlus.getDesktop().getActiveTable();
		String history = activeTable.getChatHistory();
		if (history != null)
			historyContainer.setHtml(history);
		else
			historyContainer.setHtml(welcomeMessage);
	}

}
