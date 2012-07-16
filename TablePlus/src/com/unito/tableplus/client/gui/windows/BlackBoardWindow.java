package com.unito.tableplus.client.gui.windows;

import java.util.ArrayList;
import java.util.List;

import com.extjs.gxt.ui.client.Style.HorizontalAlignment;
import com.extjs.gxt.ui.client.Style.Orientation;
import com.extjs.gxt.ui.client.Style.Scroll;
import com.extjs.gxt.ui.client.data.BaseModel;
import com.extjs.gxt.ui.client.event.ButtonEvent;
import com.extjs.gxt.ui.client.event.MenuEvent;
import com.extjs.gxt.ui.client.event.SelectionListener;
import com.extjs.gxt.ui.client.store.ListStore;
import com.extjs.gxt.ui.client.util.Margins;
import com.extjs.gxt.ui.client.widget.Info;
import com.extjs.gxt.ui.client.widget.Layout;
import com.extjs.gxt.ui.client.widget.LayoutContainer;
import com.extjs.gxt.ui.client.widget.button.Button;
import com.extjs.gxt.ui.client.widget.form.ComboBox;
import com.extjs.gxt.ui.client.widget.form.ComboBox.TriggerAction;
import com.extjs.gxt.ui.client.widget.form.FormButtonBinding;
import com.extjs.gxt.ui.client.widget.form.FormPanel;
import com.extjs.gxt.ui.client.widget.form.TextArea;
import com.extjs.gxt.ui.client.widget.grid.ColumnConfig;
import com.extjs.gxt.ui.client.widget.grid.ColumnModel;
import com.extjs.gxt.ui.client.widget.grid.Grid;
import com.extjs.gxt.ui.client.widget.layout.CenterLayout;
import com.extjs.gxt.ui.client.widget.layout.FitLayout;
import com.extjs.gxt.ui.client.widget.layout.FormData;
import com.extjs.gxt.ui.client.widget.layout.RowData;
import com.extjs.gxt.ui.client.widget.layout.RowLayout;
import com.extjs.gxt.ui.client.widget.menu.Menu;
import com.extjs.gxt.ui.client.widget.menu.MenuItem;
import com.google.gwt.core.client.GWT;
import com.google.gwt.user.client.rpc.AsyncCallback;
import com.unito.tableplus.client.TablePlus;
import com.unito.tableplus.client.services.ServiceFactory;
import com.unito.tableplus.client.services.TableServiceAsync;
import com.unito.tableplus.shared.model.BlackBoardMessage;
import com.unito.tableplus.shared.model.BlackBoardMessageType;
import com.unito.tableplus.shared.model.Table;

public class BlackBoardWindow extends WindowPlus {
	private final TableServiceAsync tableService = ServiceFactory
			.getTableServiceInstance();

	private Table table;
	private LayoutContainer mainContainer;
	private Grid<BaseModel> grid;
	private ListStore<BaseModel> messagesStore;
	private Button loadButton;
	private Button messageButton;
	private Layout fitLayout = new FitLayout();
	private Layout centerLayout = new CenterLayout();
	private FormPanel inputPanel;
	private Menu contextMenu;
	private MenuItem deleteItem;

	public BlackBoardWindow(Table table) {
		super();
		setSize(600, 300);
		this.table = table;
		setHeading("Blackboard");
		setLayout(new RowLayout(Orientation.VERTICAL));
		setButtonAlign(HorizontalAlignment.LEFT);
		mainContainer = new LayoutContainer();
		mainContainer.setLayout(fitLayout);
		mainContainer.setScrollMode(Scroll.AUTOY);

		messagesStore = new ListStore<BaseModel>();
		grid = new Grid<BaseModel>(messagesStore, getColumnModel());

		contextMenu = new Menu();
		deleteItem = new MenuItem();
		deleteItem.setText("Delete Message");

		deleteItem.addSelectionListener(new SelectionListener<MenuEvent>() {
			@Override
			public void componentSelected(MenuEvent ce) {
				final BaseModel selected = grid.getSelectionModel()
						.getSelectedItem();
				String key = selected.get("key").toString();
				tableService.removeMessage(key, new AsyncCallback<Void>() {

					@Override
					public void onFailure(Throwable caught) {
						GWT.log("Unable to delete message", caught);
						Info.display("Error", "Unable to delete message.");
					}

					@Override
					public void onSuccess(Void result) {
						messagesStore.remove(selected);
						contextMenu.setEnabled(messagesStore.getCount() > 0);
					}

				});
			}
		});
		contextMenu.add(deleteItem);

		grid.setContextMenu(contextMenu);

		mainContainer.add(grid);
		add(mainContainer, new RowData(1, 1, new Margins(4)));

		loadButton = new Button("Load Messages", new SelectionListener<ButtonEvent>() {
			@Override
			public void componentSelected(ButtonEvent ce) {
				loadMessages();
			}
		});
		messageButton = new Button("Add Message",
				new SelectionListener<ButtonEvent>() {
					@Override
					public void componentSelected(ButtonEvent ce) {
						showInputPanel();
					}
				});
		addButton(loadButton);
		addButton(messageButton);
	}

	private void loadMessages() {
		messagesStore.removeAll();
		mask();
		if (table.getKey() != null)
			tableService.getTableMessages(table.getKey(),
					new AsyncCallback<List<BlackBoardMessage>>() {

						@Override
						public void onFailure(Throwable caught) {
							GWT.log("Unable to load blackboard messages for table: "
									+ table.getName(), caught);
							Info.display("Error", "Unable to load messages.");
							unmask();
						}

						@Override
						public void onSuccess(List<BlackBoardMessage> result) {
							fillGrid(result);
							unmask();
						}
					});
		
	}

	private void showInputPanel() {
		if (inputPanel == null)
			inputPanel = buildPanel();
		mainContainer.remove(grid);
		loadButton.disable();
		messageButton.disable();
		mainContainer.setLayout(centerLayout);
		mainContainer.add(inputPanel);
		mainContainer.layout();
	}

	private void postMessage(final BlackBoardMessage message) {
		tableService.addBlackBoardMessage(table.getKey(), message,
				new AsyncCallback<Boolean>() {

					@Override
					public void onFailure(Throwable caught) {
						GWT.log("Failed to post message: ", caught);
						Info.display("Error", "Failed to post message.");
					}

					@Override
					public void onSuccess(Boolean result) {
						mainContainer.remove(inputPanel);
						mainContainer.setLayout(fitLayout);
						mainContainer.add(grid);
						loadButton.enable();
						messageButton.enable();
						mainContainer.layout();
						loadMessages();
					}
				});
	}

	private void fillGrid(List<BlackBoardMessage> messages) {
		BaseModel model;
		for (BlackBoardMessage message : messages) {
			model = new BaseModel();
			model.set("key", message.getKey());
			model.set("type", message.getType());
			model.set("author", message.getAuthor());
			model.set("content", message.getContent());
			model.set("date", message.getDate());
			messagesStore.add(model);
		}
		contextMenu.setEnabled(messagesStore.getCount() > 0);
	}

	private ColumnModel getColumnModel() {

		ColumnConfig type = new ColumnConfig("type", "Type", 60);
		ColumnConfig author = new ColumnConfig("author", "Author", 150);
		ColumnConfig content = new ColumnConfig("content", "Content", 270);
		ColumnConfig date = new ColumnConfig("date", "Date", 120);

		List<ColumnConfig> config = new ArrayList<ColumnConfig>();
		config.add(type);
		config.add(author);
		config.add(content);
		config.add(date);

		return new ColumnModel(config);
	}

	private FormPanel buildPanel() {
		final FormPanel panel = new FormPanel();
		FormData formData = new FormData("-20");
		panel.setHeading("New Message");
		panel.setFrame(true);
		panel.setWidth(350);

		ListStore<BaseModel> typeStore = new ListStore<BaseModel>();
		BaseModel typeModel;
		for (BlackBoardMessageType type : BlackBoardMessageType.values()) {
			typeModel = new BaseModel();
			typeModel.set("type", type);
			typeStore.add(typeModel);
		}

		final ComboBox<BaseModel> combo = new ComboBox<BaseModel>();
		combo.setFieldLabel("Type");
		combo.setDisplayField("type");
		combo.setTriggerAction(TriggerAction.ALL);
		combo.setStore(typeStore);
		combo.setAllowBlank(false);
		combo.setEditable(false);
		combo.setWidth(120);
		panel.add(combo, formData);

		final TextArea content = new TextArea();
		content.setPreventScrollbars(true);
		content.setFieldLabel("Message");
		content.setAllowBlank(false);
		panel.add(content, formData);

		Button sendButton = new Button("Send");
		panel.addButton(sendButton);
		Button cancelButton = new Button("Cancel");
		panel.addButton(cancelButton);

		panel.setButtonAlign(HorizontalAlignment.CENTER);

		FormButtonBinding binding = new FormButtonBinding(panel);
		binding.addButton(sendButton);

		cancelButton.addSelectionListener(new SelectionListener<ButtonEvent>() {
			@Override
			public void componentSelected(ButtonEvent ce) {
				mainContainer.remove(inputPanel);
				mainContainer.setLayout(fitLayout);
				mainContainer.add(grid);
				loadButton.enable();
				messageButton.enable();
				mainContainer.layout();

			}
		});

		sendButton.addSelectionListener(new SelectionListener<ButtonEvent>() {
			@Override
			public void componentSelected(ButtonEvent ce) {
				String author = TablePlus.getUser().getUsername();
				BlackBoardMessageType type = combo.getValue().get("type");
				BlackBoardMessage message = new BlackBoardMessage(author, type,
						content.getValue());
				content.clear();
				postMessage(message);
			}

		});

		return panel;
	}
}
