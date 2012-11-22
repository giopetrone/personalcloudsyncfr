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
import com.extjs.gxt.ui.client.widget.HorizontalPanel;
import com.extjs.gxt.ui.client.widget.Info;
import com.extjs.gxt.ui.client.widget.Layout;
import com.extjs.gxt.ui.client.widget.LayoutContainer;
import com.extjs.gxt.ui.client.widget.button.Button;
import com.extjs.gxt.ui.client.widget.form.FormButtonBinding;
import com.extjs.gxt.ui.client.widget.form.FormPanel;
import com.extjs.gxt.ui.client.widget.form.Radio;
import com.extjs.gxt.ui.client.widget.form.RadioGroup;
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
import com.unito.tableplus.shared.model.VisibilityType;

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
	private MenuItem commentItem;
	private List<String> allTags = new LinkedList<String>();
	private List<Bookmark> resource;
	
	private ListStore<BaseModel> commentStore;
	private LayoutContainer lc;
	private Grid<BaseModel> gridComment;
	private Menu commentContextMenu;
	private MenuItem deleteCommentItem;
	private MenuItem editItem;
	private boolean edit = false;
	private Button loadGridButton = new Button("Refresh");
	private Button addGridButton = new Button("Add Comment");
	private Button deleteGridButton = new Button("Delete All");
	private Button closeGridButton = new Button("Close Comment Property");
	private HorizontalPanel hp = new HorizontalPanel();
	private Radio radio1;

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
		deleteItem.setIcon(IconHelper.createStyle("deleteAll"));
		deleteItem.addSelectionListener(new SelectionListener<MenuEvent>() {
			@Override
			public void componentSelected(MenuEvent ce) {
				final BaseModel selected = grid.getSelectionModel().getSelectedItem();
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
		openItem.setIcon(IconHelper.createStyle("property"));
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
		commentItem = new MenuItem();
		commentItem.setText("Add & Edit Comments");
		commentItem.setIcon(IconHelper.createStyle("comment"));
		commentItem.addSelectionListener(new SelectionListener<MenuEvent>() {
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
						commentView(b);
					}	
				});
			}
		});
		contextMenu.add(commentItem);
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
		loadButton = new Button("Refresh", new SelectionListener<ButtonEvent>() {
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

		filterButton = new Button("Filter By Tag",new SelectionListener<ButtonEvent>() {
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
		loadBookmark();
	}
	
	private void commentView(final Bookmark b) {
		commentStore = new ListStore<BaseModel>();
		lc = new LayoutContainer();
		lc.setScrollMode(Scroll.AUTOY);
		lc.setWidth(530);
		lc.setLayout(new FitLayout());
		gridComment = new Grid<BaseModel>(commentStore, getCommentColumnModel());
		gridComment.setHeight(200);

		commentContextMenu = new Menu();
		deleteCommentItem = new MenuItem();
		deleteCommentItem.setText("Delete Comment");
		deleteCommentItem.setIcon(IconHelper.createStyle("delete"));
		deleteCommentItem.addSelectionListener(new SelectionListener<MenuEvent>() {
			@Override
			public void componentSelected(MenuEvent ce) {
				delete();
			}
		});
		commentContextMenu.add(deleteCommentItem);
		// edit comment, cancella il precedente e crea nuovo commento
		editItem = new MenuItem();
		editItem.setText("Edit Comment");
		editItem.setIcon(IconHelper.createStyle("edit"));
		editItem.addSelectionListener(new SelectionListener<MenuEvent>() {
			@Override
			public void componentSelected(MenuEvent ce) {
				final BaseModel selected = gridComment.getSelectionModel().getSelectedItem();
				final String key = selected.get("key").toString();
				bookmarkService.editComment(b, key,new AsyncCallback<String>() {
					@Override
					public void onFailure(Throwable caught) {
						GWT.log("Unable to edit comment", caught);
						Info.display("Error", "Unable to edit comment.");
					}
					@Override
					public void onSuccess(String c) {
						showInputPanel(b, c, key);
						if (edit) {
							commentStore.remove(selected);
							commentContextMenu.setEnabled(commentStore.getCount() > 0);
						}
					}
				});
			}
		});
		commentContextMenu.add(editItem);
		gridComment.setContextMenu(commentContextMenu);
		
		loadGridComments(b);
		mainContainer.remove(grid);
		mainContainer.setLayout(centerLayout);
		loadButton.disable();
		addButton.disable();
		filterButton.disable();
		
		lc.add(gridComment);
		mainContainer.add(lc);
		lc.add(commentButton(b));
		mainContainer.layout();

	}
	
	private void showInputPanel(Bookmark b, String c, String key) {
		inputPanel = buildPanel(b, c, key);
		showForm();
	}
	
	private void showForm() {
		mainContainer.removeAll();
		mainContainer.setLayout(centerLayout);
		mainContainer.add(inputPanel);
		mainContainer.layout();
	}
	
	private FormPanel buildPanel(final Bookmark b, String c, final String key) {
		final FormPanel panel = new FormPanel();
		FormData formData = new FormData("-20");
		panel.setHeading("Edit Comment");
		panel.setFrame(true);
		panel.setWidth(350);
		panel.setLabelWidth(110);

		radio1 = new Radio();
		radio1.setBoxLabel("Public");
		radio1.setValue(true);

		Radio radio2 = new Radio();
		radio2.setBoxLabel("Private");

		RadioGroup radioGroup = new RadioGroup();
		radioGroup.setFieldLabel("Comment Visibility");
		radioGroup.add(radio1);
		radioGroup.add(radio2);
		panel.add(radioGroup);
		final TextArea comment = new TextArea();
		comment.setHeight(35);
		comment.setPreventScrollbars(true);
		comment.setFieldLabel("New Comment");
		comment.setAllowBlank(true);
		comment.setValue(c);
		panel.add(comment, formData);

		Button saveButton = new Button("Save");
		panel.addButton(saveButton);
		Button cancelButton = new Button("Cancel");
		panel.addButton(cancelButton);
		saveButton.setStyleAttribute("padding-left", "70px");
		panel.setButtonAlign(HorizontalAlignment.CENTER);
		FormButtonBinding binding = new FormButtonBinding(panel);
		binding.addButton(saveButton);
		cancelButton.addSelectionListener(new SelectionListener<ButtonEvent>() {
			@Override
			public void componentSelected(ButtonEvent ce) {
				rebuildCommentView(b);
			}
		});
		saveButton.addSelectionListener(new SelectionListener<ButtonEvent>() {
			@Override
			public void componentSelected(ButtonEvent ce) {
				Comment c;
				if (radio1.getValue())
					c = new Comment(comment.getValue(), TablePlus.getUser().getEmail());
				else
					c = new Comment(comment.getValue(), TablePlus.getUser().getEmail(), VisibilityType.PRIVATE);
				editComment(b, c, key);
				comment.clear();
				rebuildCommentView(b);
			}
		});
		return panel;
	}

	private void rebuildBookmarkList() {
		mainContainer.remove(lc);
		mainContainer.setLayout(centerLayout);
		loadButton.enable();
		addButton.enable();
		filterButton.enable();
		mainContainer.add(grid);
		mainContainer.layout();
	}
	
	private void rebuildCommentView(Bookmark b) {
		mainContainer.remove(inputPanel);
		mainContainer.setLayout(centerLayout);
		mainContainer.add(lc);
		mainContainer.layout();
	}
	
	private void editComment(final Bookmark b, final Comment comment, final String key) {
		bookmarkService.addComment(b.getKey(), comment,new AsyncCallback<Boolean>() {
			@Override
			public void onFailure(Throwable caught) {
				GWT.log("Failed to post comment: ", caught);
				Info.display("Error", "Failed to post comment.");
			}
			@Override
			public void onSuccess(Boolean result) {
				bookmarkService.deleteComment(key,new AsyncCallback<Boolean>() {
					@Override
					public void onFailure(Throwable caught) {
						GWT.log("Unable to delete comment",caught);
						Info.display("Error","Unable to delete comment.");
					}
					@Override
					public void onSuccess(Boolean result) {
						edit = true;
					}
				});
				loadGridComments(b);
			}
		});
	}

	private HorizontalPanel commentButton(final Bookmark b) {
		hp.add(loadGridButton);
		hp.add(addGridButton);
		hp.add(deleteGridButton);
		hp.add(closeGridButton);
		hp.setStyleAttribute("padding-left", "60px");
		hp.setSpacing(5);
		// refresh
		loadGridButton.addSelectionListener(new SelectionListener<ButtonEvent>() {
			@Override
			public void componentSelected(ButtonEvent ce) {
				loadGridComments(b);
			}
		});
		loadGridButton.setToolTip(new ToolTipConfig("Refresh comments"));
		loadGridButton.setIcon(IconHelper.createStyle("arrow_refresh"));
		loadGridButton.setWidth(70);
		// add
		addGridButton.addSelectionListener(new SelectionListener<ButtonEvent>() {
			@Override
			public void componentSelected(ButtonEvent ce) {
				inputPanel = newComment(b);
				showForm();
			}
		});
		addGridButton.setToolTip(new ToolTipConfig("Add comment"));
		addGridButton.setIcon(IconHelper.createStyle("add"));
		addGridButton.setWidth(100);
		// delete
		deleteGridButton.addSelectionListener(new SelectionListener<ButtonEvent>() {
			@Override
			public void componentSelected(ButtonEvent ce) {
				deleteAllComment(b);
			}
		});
		deleteGridButton.setToolTip(new ToolTipConfig("Delete All"));
		deleteGridButton.setIcon(IconHelper.createStyle("deleteAll"));
		deleteGridButton.setWidth(80);
		closeGridButton.addSelectionListener(new SelectionListener<ButtonEvent>() {
			@Override
			public void componentSelected(ButtonEvent ce) {
				rebuildBookmarkList();
			}
		});
		closeGridButton.setToolTip(new ToolTipConfig("Return to normal view"));
		closeGridButton.setWidth(130);
		return hp;
	}
	
	private void loadGridComments(final Bookmark b) {
		commentStore.removeAll();
		bookmarkService.getComments(b.getKey(), new AsyncCallback<List<Comment>>() {
			@Override
			public void onFailure(Throwable caught) {
				GWT.log("Unable to load comments for bookmark: "+ b.getTitle(), caught);
				Info.display("Error", "Unable to load comments.");
			}
			@Override
			public void onSuccess(List<Comment> result) {
				fillCommentGrid(result);
			}
		});
	}
	
	private void fillCommentGrid(List<Comment> result) {
		BaseModel model = new BaseModel();
		for (final Comment c : result) {
			model.set("key", c.getKey());
			model.set("author", c.getAuthor());
			model.set("comment", c.getComment());
			model.set("date", c.getDate());
			model.set("visibility", c.getVisibilty());
			commentStore.add(model);
			if (model.get("author").toString().equals(TablePlus.getUser().getEmail())) 
				commentContextMenu.setEnabled(true);
			else commentContextMenu.disable();
		}
	}
	
	private ColumnModel getCommentColumnModel() {
		ColumnConfig author = new ColumnConfig("author", "Author", 120);
		ColumnConfig comment = new ColumnConfig("comment", "Comment", 130);
		ColumnConfig date = new ColumnConfig("date", "Date", 200);
		ColumnConfig visibility = new ColumnConfig("visibility", "Visibility",60);
		List<ColumnConfig> config = new ArrayList<ColumnConfig>();
		config.add(author);
		config.add(comment);
		config.add(date);
		config.add(visibility);
		return new ColumnModel(config);
	}	
	
	public void delete() {
		final BaseModel selected = gridComment.getSelectionModel().getSelectedItem();
		String key = selected.get("key").toString();
		bookmarkService.deleteComment(key, new AsyncCallback<Boolean>() {
			@Override
			public void onFailure(Throwable caught) {
				GWT.log("Unable to delete comment", caught);
				Info.display("Error", "Unable to delete comment.");
			}
			@Override
			public void onSuccess(Boolean result) {
				commentStore.remove(selected);
				commentContextMenu.setEnabled(commentStore.getCount() > 0);
			}
		});
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
		for (String tag : TablePlus.getDesktop().getAllTags()) listTag.addItem(tag);
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
						if (t.equals(tag)) tagFilter.add(b);
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
				filterButton.enable();
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
		ColumnConfig annotation = new ColumnConfig("annotation", "Annotation",65);
		ColumnConfig go = new ColumnConfig("go", "Open", 55);
		GridCellRenderer<BaseModel> buttonRenderer = new GridCellRenderer<BaseModel>() {
			public Object render(final BaseModel model, String property,
					ColumnData config, final int rowIndex, final int colIndex,
					ListStore<BaseModel> store, Grid<BaseModel> grid) {

				Button goButton = new Button("Go");
				goButton.setWidth(45);
				goButton.setToolTip(new ToolTipConfig("Open resource in new tab"));
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
					Comment c = new Comment(comment.getValue(), TablePlus.getUser().getEmail());
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
		if (table == null) Info.display("Share resource", "Cannot share on personal table!");
		else
			tableService.addResource(selectedResource, user, table.getKey(),new AsyncCallback<Boolean>() {
				@Override
				public void onFailure(Throwable caught) {
					GWT.log("Failed to share selected resource: ",caught);
					Info.display("Share resource","Failed to share selected resource.");
				}
				@Override
				public void onSuccess(Boolean result) {
					if (result)
						Info.display("Share resource","Resource has been successfully shared.");
					else
						Info.display("Share resource","Resource could not be shared.");
				}
			});
	}

	private void deleteAllComment(final Bookmark b) {
		for (final BaseModel bm : commentStore.getModels()) {
			if (bm.get("author").equals(TablePlus.getUser().getEmail())) {
				bookmarkService.deleteComment(bm.get("key").toString(),new AsyncCallback<Boolean>() {
					@Override
					public void onFailure(Throwable caught) {
						GWT.log("Unable to delete comment", caught);
						Info.display("Error","Unable to delete comment.");
					}

					@Override
					public void onSuccess(Boolean result) {
						commentStore.remove(bm);
						loadGridComments(b);
					}
				});
			}
		}
	}

	private FormPanel newComment(final Bookmark b) {
		final FormPanel panel = new FormPanel();
		FormData formData = new FormData("-20");
		panel.setHeading("New Comment from " + TablePlus.getUser().getEmail());
		panel.setFrame(true);
		panel.setWidth(385);

		panel.setLabelWidth(110);

		radio1 = new Radio();
		radio1.setBoxLabel("Public");
		radio1.setValue(true);

		Radio radio2 = new Radio();
		radio2.setBoxLabel("Private");

		RadioGroup radioGroup = new RadioGroup();
		radioGroup.setFieldLabel("Comment Visibility");
		radioGroup.add(radio1);
		radioGroup.add(radio2);
		panel.add(radioGroup);

		final TextArea comment = new TextArea();
		comment.setHeight(35);
		comment.setPreventScrollbars(true);
		comment.setFieldLabel("New Comment");
		comment.setAllowBlank(false);
		panel.add(comment, formData);

		Button saveButton = new Button("Save");
		panel.addButton(saveButton);
		Button cancelButton = new Button("Cancel");
		panel.addButton(cancelButton);
		saveButton.setStyleAttribute("padding-left", "35px");
		panel.setButtonAlign(HorizontalAlignment.CENTER);
		FormButtonBinding binding = new FormButtonBinding(panel);
		binding.addButton(saveButton);
		cancelButton.addSelectionListener(new SelectionListener<ButtonEvent>() {
			@Override
			public void componentSelected(ButtonEvent ce) {
				rebuildCommentView(b);
			}
		});
		saveButton.addSelectionListener(new SelectionListener<ButtonEvent>() {
			@Override
			public void componentSelected(ButtonEvent ce) {
				Comment c;
				if (radio1.getValue())
					c = new Comment(comment.getValue(), TablePlus.getUser().getEmail());
				else
					c = new Comment(comment.getValue(), TablePlus.getUser().getEmail(), VisibilityType.PRIVATE);
				addComment(b, c);
				comment.clear();
				rebuildCommentView(b);
			}
		});
		return panel;
	}

	public void addComment(final Bookmark b, Comment c) {
		bookmarkService.addComment(b.getKey(), c,new AsyncCallback<Boolean>() {
			@Override
			public void onFailure(Throwable caught) {
				GWT.log("Failed to post comment: ", caught);
				Info.display("Error", "Failed to post comment.");
			}
			@Override
			public void onSuccess(Boolean result) {
				GWT.log("Successfully posted comment ");
				Info.display("Success", "Successfully posted comment ");
				loadGridComments(b);
			}
		});
		commentItem.enable();
	}


}
