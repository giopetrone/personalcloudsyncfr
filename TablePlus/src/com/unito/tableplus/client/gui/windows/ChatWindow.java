package com.unito.tableplus.client.gui.windows;

import com.extjs.gxt.ui.client.Style.Orientation;
import com.extjs.gxt.ui.client.Style.Scroll;
import com.extjs.gxt.ui.client.event.ComponentEvent;
import com.extjs.gxt.ui.client.event.KeyListener;
import com.extjs.gxt.ui.client.util.Margins;
import com.extjs.gxt.ui.client.widget.Html;
import com.extjs.gxt.ui.client.widget.HtmlContainer;
import com.extjs.gxt.ui.client.widget.LayoutContainer;
import com.extjs.gxt.ui.client.widget.form.TextArea;
import com.extjs.gxt.ui.client.widget.layout.FitLayout;
import com.extjs.gxt.ui.client.widget.layout.RowData;
import com.extjs.gxt.ui.client.widget.layout.RowLayout;
import com.google.gwt.core.client.GWT;
import com.google.gwt.user.client.rpc.AsyncCallback;
import com.unito.tableplus.client.TablePlus;
import com.unito.tableplus.client.gui.TableUI;
import com.unito.tableplus.client.services.MessagingServiceAsync;
import com.unito.tableplus.client.services.ServiceFactory;
import com.unito.tableplus.shared.model.ChannelMessageType;

public class ChatWindow extends WindowPlus {

	protected final MessagingServiceAsync chatService = ServiceFactory
			.getChatServiceInstance();

	private TableUI table;
	private LayoutContainer mainContainer;
	private Html history;
	private HtmlContainer historyContainer;
	private TextArea inputArea;
	
	private final String message = "<div align=\"center\"><b>Welcome into the chat!</b> <br><br>"
			+ "It seems like none of your friends had nothing to say since your coming. "
			+ "Be the first to say '<i>Hi</i>', test the chat and enjoy!</div>";

	public ChatWindow(TableUI table) {
		super();
		this.table = table;

		setHeading("Table Chat");
		setLayout(new RowLayout(Orientation.VERTICAL));

		history = new Html("");
		mainContainer = new LayoutContainer();
		mainContainer.setLayout(new FitLayout());
		mainContainer.setScrollMode(Scroll.AUTOY);
		
		historyContainer = new HtmlContainer(message);

		mainContainer.add(historyContainer);
		historyContainer.scrollIntoView(mainContainer);

		add(mainContainer, new RowData(1, 1, new Margins(4)));


		inputArea = new TextArea();
		inputArea.setHeight(50);
		inputArea.setWidth(width);

		add(inputArea, new RowData(1, -1, new Margins(4)));

		inputArea.addKeyListener(new KeyListener() {
			@Override
			public void componentKeyPress(ComponentEvent event) {
				if (event.getKeyCode() == 13)
					sendMessage();
			}
		});
	}

	public void manageNewMessage(String sender, String content) {
		history.setHtml(history.getHtml()
				+ "<b>&lt;"
				+ sender
				+ "&gt;</b>&nbsp&nbsp"
				+ content.replace("&", "&amp;").replace("<", "&lt;")
						.replace(">", "&gt;").replace("\"", "&quot;") + "<br>");
		historyContainer.setHtml(history.getHtml());

		mainContainer.getElement().setScrollTop(mainContainer.getElement().getScrollHeight());
		mainContainer.getElement()
				.getFirstChildElement()
				.setScrollTop(
						mainContainer.getElement().getFirstChildElement()
								.getScrollHeight());
	}

	public void sendMessage() {
		if (inputArea.getValue() != null)
			chatService.sendMessage(TablePlus.getUser().getKey(),
					inputArea.getValue() + "\n", ChannelMessageType.CHAT,
					table.getTableMembers(), table.getTableKey(),
					new AsyncCallback<String>() {

						@Override
						public void onFailure(Throwable caught) {
							GWT.log("Error sending message", caught);
						}

						@Override
						public void onSuccess(String result) {
							inputArea.setValue("");
						}

					});
	}

}
