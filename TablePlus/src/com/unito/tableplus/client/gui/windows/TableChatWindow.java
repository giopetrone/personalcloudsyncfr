package com.unito.tableplus.client.gui.windows;

import java.util.ArrayList;
import java.util.LinkedList;

import com.extjs.gxt.ui.client.Style.LayoutRegion;
import com.extjs.gxt.ui.client.Style.Orientation;
import com.extjs.gxt.ui.client.Style.Scroll;
import com.extjs.gxt.ui.client.event.ButtonEvent;
import com.extjs.gxt.ui.client.event.ComponentEvent;
import com.extjs.gxt.ui.client.event.KeyListener;
import com.extjs.gxt.ui.client.event.SelectionListener;
import com.extjs.gxt.ui.client.util.Margins;
import com.extjs.gxt.ui.client.widget.Html;
import com.extjs.gxt.ui.client.widget.HtmlContainer;
import com.extjs.gxt.ui.client.widget.LayoutContainer;
import com.extjs.gxt.ui.client.widget.button.Button;
import com.extjs.gxt.ui.client.widget.form.TextArea;
import com.extjs.gxt.ui.client.widget.layout.BorderLayout;
import com.extjs.gxt.ui.client.widget.layout.BorderLayoutData;
import com.extjs.gxt.ui.client.widget.layout.FillLayout;
import com.extjs.gxt.ui.client.widget.layout.FitLayout;
import com.extjs.gxt.ui.client.widget.layout.RowData;
import com.extjs.gxt.ui.client.widget.layout.RowLayout;
import com.google.gwt.core.client.GWT;
import com.google.gwt.user.client.rpc.AsyncCallback;
import com.unito.tableplus.client.TablePlus;
import com.unito.tableplus.client.gui.TableUI;
import com.unito.tableplus.client.services.ChatService;
import com.unito.tableplus.client.services.ChatServiceAsync;
import com.unito.tableplus.shared.model.MessageType;

public class TableChatWindow extends WindowPlus {

	Html allText = new Html("");

	// crea il servizio per la chat
	protected final ChatServiceAsync chatService = GWT
			.create(ChatService.class);

	public TableUI table;

	HtmlContainer allTextTA;
	TextArea myTextTA;
	LinkedList<String> ll;

	LayoutContainer lc;

	public TableChatWindow(TableUI table_) {
		super();
		this.table = table_;
		setHeading("Table Chat");

		BorderLayout borderLayout = new BorderLayout();
		setLayout(new RowLayout(Orientation.VERTICAL));

		// (01) Al centro l'area dei messaggi che arrivano dal tavolo
		lc = new LayoutContainer();

		lc.setLayout(new FitLayout());
		lc.setScrollMode(Scroll.AUTOY);
		allTextTA = new HtmlContainer("<div align=\"center\"><b>Welcome into the chat!</b> <br><br>It seems like none of your friends had nothing to say since your coming. Be the first to say '<i>Hi</i>', test the chat and enjoy!</div>");
		// allTextTA.setBorders(true);
		// allTextTA.setReadOnly(true);
		lc.add(allTextTA);
		allTextTA.scrollIntoView(lc);

		BorderLayoutData centerData = new BorderLayoutData(LayoutRegion.CENTER);
		add(lc, new RowData(1, 1, new Margins(4)));

		// (02) In basso l'area in cui scrivo io
		LayoutContainer southMyTextLC = new LayoutContainer();

		southMyTextLC.setLayout(new RowLayout(Orientation.VERTICAL));
		myTextTA = new TextArea();
		southMyTextLC.add(myTextTA);
		myTextTA.setHeight(50);
		myTextTA.setWidth(width);
		Button sendButton = new Button("Sesnd");
		southMyTextLC.add(sendButton);

		BorderLayoutData southData = new BorderLayoutData(LayoutRegion.SOUTH,
				100);
		add(myTextTA, new RowData(1, -1, new Margins(4)));// southMyTextLC,
															// southData);

		// myTextTA.setCursorPos(pos);
		// myTextTA.getElement().getFirstChildElement().setScrollTop(myTextTA.getElement().getFirstChildElement().getScrollHeight());
		// (03) A destra l'area della lista utenti
		LayoutContainer eastUsersListLC = new LayoutContainer();

		myTextTA.addKeyListener(new KeyListener() {
			// @Override
			// public void componentKeyDown(ComponentEvent event){
			// System.out.println("CIAO "+event.getKeyCode());
			// }
			@Override
			public void componentKeyPress(ComponentEvent event) {
				if (event.getKeyCode() == 13)
					sendMessage();
			}
		});

		// specifiche del button SEND
		sendButton.addSelectionListener(new SelectionListener<ButtonEvent>() {
			@Override
			public void componentSelected(ButtonEvent ce) {
				sendMessage();
			}
		});

	}

	public void manageNewMessage(String sender, String content) {
		allText.setHtml(allText.getHtml()
				+ "<b>&lt;"
				+ sender
				+ "&gt;</b>&nbsp&nbsp"
				+ content.replace("&", "&amp;").replace("<", "&lt;").replace(">", "&gt;")
						.replace("\"", "&quot;") 
						+ "<br>");
		allTextTA.setHtml(allText.getHtml());

		// allTextTA.setCursorPos(allText.getHtml().length());
		lc.getElement().setScrollTop(lc.getElement().getScrollHeight());
		lc.getElement()
				.getFirstChildElement()
				.setScrollTop(
						lc.getElement().getFirstChildElement()
								.getScrollHeight());

		// int cursorPos = myTextTA.getCursorPos();
		// long offsetRatio = cursorPos / myTextTA.getValue().length();
		// //gives 0.0 to 1.0
		// offsetRatio += -0.1; // -0.1 maybe?
		// // Depending on the font you may need to adjust the magic number
		// // to adjust the ratio if it scrolls to far or to short.
		// offsetRatio = offsetRatio>0.0 ? offsetRatio : 0; //make sure
		// //we don't get negative ratios
		// //(negative values may crash some browsers while others ignore it)
		// myTextTA.getElement().setScrollTop(
		// (int) (myTextTA.getElement().getScrollHeight() * offsetRatio) );
	}

	public void sendMessage() {
		// Auto-generated method stub
		if (myTextTA.getValue() != null)
			chatService.sendMessage(TablePlus.user.getEmail(),
					myTextTA.getValue() + "\n", MessageType.GENERIC,
					table.selectivePresenceMembers, table.tableKey,
					new AsyncCallback<String>() {

						@Override
						public void onFailure(Throwable caught) {
							// TODO Auto-generated method stub
							System.out
									.println("chatService.sendMessage() failure da "
											+ TablePlus.user.getEmail());
						}

						@Override
						public void onSuccess(String result) {
							myTextTA.setValue("");
							// TODO Auto-generated method stub
							System.out
									.println("chatService.sendMessage() happy ending da "
											+ TablePlus.user.getEmail()
											+ ": "
											+ result);
						}

					});
	}

}
