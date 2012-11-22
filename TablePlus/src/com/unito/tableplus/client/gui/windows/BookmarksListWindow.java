package com.unito.tableplus.client.gui.windows;

import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;
import com.extjs.gxt.ui.client.Style.HorizontalAlignment;
import com.extjs.gxt.ui.client.Style.Orientation;
import com.extjs.gxt.ui.client.Style.Scroll;
import com.extjs.gxt.ui.client.data.BaseModel;
import com.extjs.gxt.ui.client.event.ButtonEvent;
import com.extjs.gxt.ui.client.event.MenuEvent;
import com.extjs.gxt.ui.client.event.SelectionListener;
import com.extjs.gxt.ui.client.store.ListStore;
import com.extjs.gxt.ui.client.util.IconHelper;
import com.extjs.gxt.ui.client.util.Margins;
import com.extjs.gxt.ui.client.widget.Info;
import com.extjs.gxt.ui.client.widget.Layout;
import com.extjs.gxt.ui.client.widget.LayoutContainer;
import com.extjs.gxt.ui.client.widget.button.Button;
import com.extjs.gxt.ui.client.widget.form.FormButtonBinding;
import com.extjs.gxt.ui.client.widget.form.FormPanel;
import com.extjs.gxt.ui.client.widget.form.TextArea;
import com.extjs.gxt.ui.client.widget.grid.ColumnConfig;
import com.extjs.gxt.ui.client.widget.grid.ColumnData;
import com.extjs.gxt.ui.client.widget.grid.ColumnModel;
import com.extjs.gxt.ui.client.widget.grid.Grid;
import com.extjs.gxt.ui.client.widget.grid.GridCellRenderer;
import com.extjs.gxt.ui.client.widget.layout.CenterLayout;
import com.extjs.gxt.ui.client.widget.layout.FitLayout;
import com.extjs.gxt.ui.client.widget.layout.FormData;
import com.extjs.gxt.ui.client.widget.layout.RowData;
import com.extjs.gxt.ui.client.widget.layout.RowLayout;
import com.extjs.gxt.ui.client.widget.menu.Menu;
import com.extjs.gxt.ui.client.widget.menu.MenuItem;
import com.extjs.gxt.ui.client.widget.tips.ToolTipConfig;
import com.google.gwt.core.client.GWT;
import com.google.gwt.user.client.Window;
import com.google.gwt.user.client.rpc.AsyncCallback;
import com.google.gwt.user.client.ui.ListBox;
import com.unito.tableplus.client.TablePlus;
import com.unito.tableplus.client.services.BookmarkService;
import com.unito.tableplus.client.services.BookmarkServiceAsync;
import com.unito.tableplus.client.services.ServiceFactory;
import com.unito.tableplus.client.services.TableServiceAsync;
import com.unito.tableplus.client.services.UserService;
import com.unito.tableplus.client.services.UserServiceAsync;
import com.unito.tableplus.shared.model.Bookmark;
import com.unito.tableplus.shared.model.Comment;
import com.unito.tableplus.shared.model.Resource;
import com.unito.tableplus.shared.model.Table;
import com.unito.tableplus.shared.model.User;

public class BookmarksListWindow extends WindowPlus {

	private final UserServiceAsync userService = GWT.create(UserService.class);
	private final BookmarkServiceAsync bookmarkService = GWT.create(BookmarkService.class);
	public static final TableServiceAsync tableService = ServiceFactory.getTableServiceInstance();

	private LayoutContainer mainContainer;
	private Grid<BaseModel> grid;
	private ListStore<BaseModel> bookmarksStore;
	private Button loadButton;
	private Button addButton;
	private Button filterButton;
	private Layout fitLayout = new FitLayout();
	private Layout centerLayout = new CenterLayout();
	private FormPanel inputPanel;
	private Menu contextMenu;
	private MenuItem deleteItem;
	private MenuItem openItem;
	private MenuItem share;
	private List<String> allTags = new LinkedList<String>();
	private List<Bookmark> resource;

	public BookmarksListWindow() {
		super();
		setSize(635, 350);
		setHeading("Resource");
		setVisible(false);

		setLayout(new RowLayout(Orientation.VERTICAL));
		setButtonAlign(HorizontalAlignment.LEFT);
		mainContainer = new LayoutContainer();
		mainContainer.setLayout(fitLayout);
		mainContainer.setScrollMode(Scroll.AUTOY);
		bookmarksStore = new ListStore<BaseModel>();
		grid = new Grid<BaseModel>(bookmarksStore, getColumnModel());
		contextMenu = new Menu();
		deleteItem = new MenuItem();
		deleteItem.setText("Delete Bookmark");
		deleteItem.addSelectionListener(new SelectionListener<MenuEvent>() {
			@Override
			public void componentSelected(MenuEvent ce) {
				final BaseModel selected = grid.getSelectionModel()
						.getSelectedItem();
				String key = selected.get("key").toString();
				userService.removeBookmark(key, new AsyncCallback<Void>() {
					@Override
					public void onFailure(Throwable caught) {
						GWT.log("Unable to delete bookmark", caught);
						Info.display("Error", "Unable to delete bookmark.");
					}

					@Override
					public void onSuccess(Void result) {
						bookmarksStore.remove(selected);
						contextMenu.setEnabled(bookmarksStore.getCount() > 0);
					}
				});
			}
		});
		contextMenu.add(deleteItem);

		openItem = new MenuItem();
		openItem.setText("Open Bookmark Properties Window");
		openItem.addSelectionListener(new SelectionListener<MenuEvent>() {
			@Override
			public void componentSelected(MenuEvent ce) {
				final BaseModel selected = grid.getSelectionModel().getSelectedItem();
				String key = selected.get("key").toString();
				bookmarkService.queryBookmark(key,new AsyncCallback<Bookmark>() {
					@Override
					public void onFailure(Throwable caught) {
						GWT.log("Unable to load bookmarks for user: "+ 
								TablePlus.getUser().getFirstName()+" "+TablePlus.getUser().getLastName(), caught);
						Info.display("Error","Unable to load bookmarks.");
						unmask();
					}
					@Override
					public void onSuccess(Bookmark b) {
						TablePlus.getDesktop().showBookmarkWindow(b);
					}
				});
			}
		});
		contextMenu.add(openItem);
		share = new MenuItem();
		share.setText("Share on Table");
		share.setIcon(IconHelper.createStyle("menu-share"));

		share.addSelectionListener(new SelectionListener<MenuEvent>() {
			@Override
			public void componentSelected(MenuEvent ce) {
				final BaseModel selected = grid.getSelectionModel()
						.getSelectedItem();
				String key = selected.get("key").toString();
				bookmarkService.queryBookmark(key,new AsyncCallback<Bookmark>() {
					@Override
					public void onFailure(Throwable caught) {
						GWT.log("Unable to load bookmarks for user: "+ 
								TablePlus.getUser().getFirstName()+" "+TablePlus.getUser().getLastName(), caught);
						Info.display("Error","Unable to load bookmarks.");
						unmask();
					}
					@Override
					public void onSuccess(Bookmark b) {
						shareResource(b);
					}
				});
			}
				
		});
		contextMenu.add(share);
		grid.setContextMenu(contextMenu);
		mainContainer.add(grid);

		add(mainContainer, new RowData(1, 1, new Margins(4)));
		loadButton = new Button("Refresh",
				new SelectionListener<ButtonEvent>() {
					@Override
					public void componentSelected(ButtonEvent ce) {
						loadBookmark();
					}
				});
		loadButton.setToolTip(new ToolTipConfig("Refresh objects"));
		loadButton.setIcon(IconHelper.createStyle("arrow_refresh"));

		addButton = new Button("Add", new SelectionListener<ButtonEvent>() {
			@Override
			public void componentSelected(ButtonEvent ce) {
				showInputPanel();
			}
		});
		addButton.setToolTip(new ToolTipConfig("Add Object"));
		addButton.setIcon(IconHelper.createStyle("add"));

		filterButton = new Button("Filter By Tag",
				new SelectionListener<ButtonEvent>() {
					@Override
					public void componentSelected(ButtonEvent ce) {
						showFilterPanel();
					}
				});
		filterButton.setToolTip(new ToolTipConfig("Filter resources by Tag"));
		filterButton.setIcon(IconHelper.createStyle("filter"));

		addButton(loadButton);
		addButton(addButton);
		addButton(filterButton);
	}

	private void showFilterPanel() {
		inputPanel = buildTagPanel();
		mainContainer.remove(grid);
		loadButton.disable();
		addButton.disable();
		filterButton.disable();
		mainContainer.setLayout(centerLayout);
		mainContainer.add(inputPanel);
		mainContainer.layout();
	}

	private FormPanel buildTagPanel() {
		final FormPanel panel = new FormPanel();
		panel.setLayoutData(Orientation.HORIZONTAL);
		final FormData formData = new FormData("-20");
		panel.setHeading("New Filter");
		panel.setFrame(true);
		panel.setWidth(350);

		final ListBox listTag = new ListBox();
		for (String tag : TablePlus.getDesktop().getAllTags()) {
			listTag.addItem(tag);
		}
		listTag.setHeight("20px");

		panel.add(listTag, formData);

		Button saveButton = new Button("Save Filter");
		panel.addButton(saveButton);
		Button cancelButton = new Button("Cancel");
		panel.addButton(cancelButton);
		panel.setButtonAlign(HorizontalAlignment.CENTER);
		FormButtonBinding binding = new FormButtonBinding(panel);
		binding.addButton(saveButton);
		cancelButton.addSelectionListener(new SelectionListener<ButtonEvent>() {
			@Override
			public void componentSelected(ButtonEvent ce) {
				mainContainer.remove(inputPanel);
				mainContainer.setLayout(fitLayout);
				mainContainer.add(grid);
				loadButton.enable();
				addButton.enable();
				filterButton.enable();
				mainContainer.layout();
			}
		});
		saveButton.addSelectionListener(new SelectionListener<ButtonEvent>() {
			@Override
			public void componentSelected(ButtonEvent ce) {
				bookmarksStore.removeAll();
				mask();
				String tag = listTag.getItemText(listTag.getSelectedIndex());
				List<Bookmark> tagFilter = new LinkedList<Bookmark>();
				for (Bookmark b : resource) {
					for (String t : b.getTag())
						if (t.equals(tag))
							tagFilter.add(b);
				}
				fillGrid(tagFilter);
				mainContainer.remove(inputPanel);
				mainContainer.setLayout(fitLayout);
				mainContainer.add(grid);
				loadButton.enable();
				addButton.enable();
				filterButton.enable();
				mainContainer.layout();
			}
		});
		return panel;

	}

	private void loadBookmark() {
		bookmarksStore.removeAll();
		mask();
		//if (activeTable.getKey() != null)
			userService.loadBookmarks(TablePlus.getUser().getKey(),new AsyncCallback<List<Bookmark>>() {
				@Override
				public void onFailure(Throwable caught) {
					GWT.log("Unable to load bookmarks for user: "+ TablePlus.getUser().getFirstName(), caught);
					Info.display("Error", "Unable to load bookmarks.");
					unmask();
				}

				@Override
				public void onSuccess(List<Bookmark> result) {
					if (result != null) {
						//activeTable.setBookmarks(result);
						resource = result;
						fillGrid(resource);
					}
					unmask();
				}
			});
	}

	private void showInputPanel() {
		inputPanel = buildPanel();
		mainContainer.remove(grid);
		loadButton.disable();
		addButton.disable();
		filterButton.disable();
		mainContainer.setLayout(centerLayout);
		mainContainer.add(inputPanel);
		mainContainer.layout();

	}

	private void shareBookmark(final Bookmark bookmark) {
		userService.addBookmark(TablePlus.getUser().getKey(),bookmark,new AsyncCallback<Boolean>() {
			@Override
			public void onFailure(Throwable caught) {
				GWT.log("Failed to share bookmark: ", caught);
				Info.display("Error", "Failed to share bookmark.");
			}

			@Override
			public void onSuccess(Boolean result) {
				mainContainer.remove(inputPanel);
				mainContainer.setLayout(fitLayout);
				mainContainer.add(grid);
				loadButton.enable();
				addButton.enable();
				mainContainer.layout();
				loadBookmark();
			}
		});
	}

	private void fillGrid(List<Bookmark> bookmarks) {
		allTags = new LinkedList<String>();
		for (final Bookmark bookmark : bookmarks) {
			allTags.addAll(bookmark.getTag());
			final BaseModel model = new BaseModel();
			model.set("key", bookmark.getKey());
			model.set("title", bookmark.getTitle());
			model.set("url", bookmark.getUrl());
			model.set("legend", bookmark.getLegend());
			bookmarkService.getComments(bookmark.getKey(),new AsyncCallback<List<Comment>>() {
				@Override
				public void onFailure(Throwable caught) {
					GWT.log("Unable to load the comment's number",caught);
					Info.display("Error","Unable to load the comment's number.");
					unmask();
				}
				@Override
				public void onSuccess(List<Comment> result) {
					model.set("comment", result.size());
					unmask();
				}
			});
			model.set("tag", bookmark.getTagString());
			model.set("annotation", bookmark.getAnnotationNumber());
			bookmarksStore.add(model);
		}
		contextMenu.setEnabled(bookmarksStore.getCount() > 0);
		TablePlus.getDesktop().setAllTags(allTags);
		if (TablePlus.getDesktop().getAllTags().size() == 0) filterButton.disable();
	}

	private ColumnModel getColumnModel() {
		ColumnConfig title = new ColumnConfig("title", "Title", 70);
		final ColumnConfig url = new ColumnConfig("url", "Url", 160);
		ColumnConfig legend = new ColumnConfig("legend", "Legend", 120);
		ColumnConfig comment = new ColumnConfig("comment", "Comment", 55);
		ColumnConfig tag = new ColumnConfig("tag", "Tag", 70);
		ColumnConfig annotation = new ColumnConfig("annotation", "Annotation",
				65);
		ColumnConfig go = new ColumnConfig("go", "Open", 55);
		GridCellRenderer<BaseModel> buttonRenderer = new GridCellRenderer<BaseModel>() {
			public Object render(final BaseModel model, String property,
					ColumnData config, final int rowIndex, final int colIndex,
					ListStore<BaseModel> store, Grid<BaseModel> grid) {

				Button goButton = new Button("Go");
				goButton.setWidth(45);
				goButton.setToolTip(new ToolTipConfig(
						"Open resource in new tab"));
				goButton.setIcon(IconHelper.createStyle("go"));
				goButton.addSelectionListener(new SelectionListener<ButtonEvent>() {
					public void componentSelected(ButtonEvent ce) {
						Window.open(model.get("url").toString(), "",
								"left=100,top=100,width=600,height=400,menubar,toolbar,resizable");
					}
				});
				return goButton;
			}
		};
		go.setRenderer(buttonRenderer);
		List<ColumnConfig> config = new ArrayList<ColumnConfig>();
		config.add(go);
		config.add(title);
		config.add(url);
		config.add(legend);
		config.add(comment);
		config.add(tag);
		config.add(annotation);

		return new ColumnModel(config);

	}

	private FormPanel buildPanel() {
		final FormPanel panel = new FormPanel();
		panel.setLayoutData(Orientation.HORIZONTAL);
		final FormData formData = new FormData("-20");
		panel.setHeading("New Bookmark");
		panel.setFrame(true);
		panel.setWidth(350);

		final TextArea title = new TextArea();
		title.setHeight(20);
		title.setPreventScrollbars(true);
		title.setFieldLabel("Title");
		title.setAllowBlank(false);
		panel.add(title, formData);

		final TextArea url = new TextArea();
		url.setHeight(20);
		url.setPreventScrollbars(true);
		url.setFieldLabel("Url");
		url.setAllowBlank(false);
		panel.add(url, formData);

		final TextArea legend = new TextArea();
		legend.setHeight(35);
		legend.setPreventScrollbars(true);
		legend.setFieldLabel("Legend");
		legend.setAllowBlank(true);
		panel.add(legend, formData);

		final TextArea comment = new TextArea();
		comment.setHeight(35);
		comment.setPreventScrollbars(true);
		comment.setFieldLabel("Comment");
		comment.setAllowBlank(true);
		panel.add(comment, formData);

		final TextArea annotation = new TextArea();
		annotation.setHeight(20);
		annotation.setPreventScrollbars(true);
		annotation.setFieldLabel("Annotation");
		annotation.setAllowBlank(true);
		panel.add(annotation, formData);

		final TextArea tag = new TextArea();
		tag.setHeight(20);
		tag.setPreventScrollbars(true);
		tag.setFieldLabel("Tag");
		tag.setAllowBlank(true);
		panel.add(tag, formData);
		if (allTags.size() > 0)
			tag.setToolTip("All existing tags: \n" + allTags.toString());
		else
			tag.setToolTip("There are no tags");
		Button saveButton = new Button("Save");
		panel.addButton(saveButton);
		Button cancelButton = new Button("Cancel");
		panel.addButton(cancelButton);
		panel.setButtonAlign(HorizontalAlignment.CENTER);
		FormButtonBinding binding = new FormButtonBinding(panel);
		binding.addButton(saveButton);
		cancelButton.addSelectionListener(new SelectionListener<ButtonEvent>() {
			@Override
			public void componentSelected(ButtonEvent ce) {
				mainContainer.remove(inputPanel);
				mainContainer.setLayout(fitLayout);
				mainContainer.add(grid);
				loadButton.enable();
				addButton.enable();
				filterButton.enable();
				mainContainer.layout();
			}
		});
		saveButton.addSelectionListener(new SelectionListener<ButtonEvent>() {
			@Override
			public void componentSelected(ButtonEvent ce) {
				Bookmark bookmark = new Bookmark();
				bookmark.setTitle(title.getValue());
				bookmark.setUrl(url.getValue());
				bookmark.setLegend(legend.getValue());
				if (annotation.getValue() != null)
					bookmark.addAnnotation(annotation.getValue());
				if (tag.getValue() != null)
					bookmark.addTag(tag.getValue().toUpperCase());
				if (comment.getValue() != null) {
					Comment c = new Comment(comment.getValue(), TablePlus
							.getUser().getEmail());
					bookmark.addComment(c);
				}
				title.clear();
				url.clear();
				legend.clear();
				comment.clear();
				annotation.clear();
				tag.clear();
				shareBookmark(bookmark);
			}
		});
		return panel;
	}

	@Override
	public void updateContent() {
		loadBookmark();
	}

	private void shareResource(Resource selectedResource) {
		User user = TablePlus.getUser();
		Table table = TablePlus.getDesktop().getActiveTable();
		if (table == null)
			Info.display("Share resource", "Cannot share on personal table!");
		else
			tableService.addResource(selectedResource, user, table.getKey(),
					new AsyncCallback<Boolean>() {

						@Override
						public void onFailure(Throwable caught) {
							GWT.log("Failed to share selected resource: ",
									caught);
							Info.display("Share resource",
									"Failed to share selected resource.");
						}

						@Override
						public void onSuccess(Boolean result) {
							if (result)
								Info.display("Share resource",
										"Resource has been successfully shared.");
							else
								Info.display("Share resource",
										"Resource could not be shared.");
						}

					});
	}
}
