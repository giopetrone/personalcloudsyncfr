package com.unito.tableplus.client.gui.windows;

import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;

import com.extjs.gxt.ui.client.Style.HorizontalAlignment;
import com.extjs.gxt.ui.client.Style.Orientation;
import com.extjs.gxt.ui.client.Style.Scroll;
import com.extjs.gxt.ui.client.data.BaseModel;
import com.extjs.gxt.ui.client.event.ButtonEvent;
import com.extjs.gxt.ui.client.event.Events;
import com.extjs.gxt.ui.client.event.GridEvent;
import com.extjs.gxt.ui.client.event.Listener;
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
import com.extjs.gxt.ui.client.widget.grid.ColumnModel;
import com.extjs.gxt.ui.client.widget.grid.Grid;
import com.extjs.gxt.ui.client.widget.layout.CenterLayout;
import com.extjs.gxt.ui.client.widget.layout.FitLayout;
import com.extjs.gxt.ui.client.widget.layout.FormData;
import com.extjs.gxt.ui.client.widget.layout.RowData;
import com.extjs.gxt.ui.client.widget.layout.RowLayout;
import com.extjs.gxt.ui.client.widget.menu.Menu;
import com.extjs.gxt.ui.client.widget.menu.MenuItem;
import com.extjs.gxt.ui.client.widget.tips.ToolTipConfig;
import com.google.gwt.core.client.GWT;
import com.google.gwt.user.client.rpc.AsyncCallback;
import com.google.gwt.user.client.ui.PopupPanel;
import com.unito.tableplus.client.services.BookmarkService;
import com.unito.tableplus.client.services.BookmarkServiceAsync;
import com.unito.tableplus.client.services.ServiceFactory;
import com.unito.tableplus.client.services.TableServiceAsync;
import com.unito.tableplus.shared.model.Bookmark;
import com.unito.tableplus.shared.model.Comment;
import com.unito.tableplus.shared.model.Table;

public class BookmarkWindowList extends WindowPlus {
	
	private final TableServiceAsync tableService = ServiceFactory.getTableServiceInstance();
	private final BookmarkServiceAsync bookmarkService = GWT.create(BookmarkService.class);
		
	private Table table;
	private LayoutContainer mainContainer;
	private Grid<BaseModel> grid;
	private ListStore<BaseModel> bookmarksStore;
	private Button loadButton;
	private Button addButton;
	private Layout fitLayout = new FitLayout();
	private Layout centerLayout = new CenterLayout();
	private FormPanel inputPanel;
	private Menu contextMenu;
	private MenuItem deleteItem;
	private List<String> allTags= new LinkedList<String>();

	
	public BookmarkWindowList(final Table table) {
		super();
		setSize(650, 350);
		this.table = table;
		setHeading("Bookmark");
		
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
				final BaseModel selected = grid.getSelectionModel().getSelectedItem();
				String key = selected.get("key").toString();
				tableService.removeBookmark(key, new AsyncCallback<Void>() {
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
		addButton = new Button("Add",new SelectionListener<ButtonEvent>() {
			@Override
			public void componentSelected(ButtonEvent ce) {
				showInputPanel();
			}
		});
		addButton.setToolTip(new ToolTipConfig("Add Object"));
		addButton.setIcon(IconHelper.createStyle("add"));		
		addButton(loadButton);
		addButton(addButton);
		grid.addListener(Events.CellMouseUp, new Listener<GridEvent<BaseModel>>() {
            public void handleEvent(GridEvent<BaseModel> ge) {
                int rowIndex = ge.getRowIndex();
                if (rowIndex != -1) {               	
                	final BaseModel selected = grid.getSelectionModel().getSelectedItem();
    				String key = selected.get("key").toString();
    				//getBookmarkPreview(key);
    				getBookmarkWindow(table, key);
    				//setPosition(600,200);
                }
            }
        });
		//CellDoubleClick();

		loadBookmark();
	}
	
	private void CellDoubleClick() {
		grid.addListener(Events.CellDoubleClick, new Listener<GridEvent<BaseModel>>() {
            public void handleEvent(GridEvent<BaseModel> ge) {
                int rowIndex = ge.getRowIndex();
                if (rowIndex != -1) {               	
                	final BaseModel selected = grid.getSelectionModel().getSelectedItem();
    				String key = selected.get("key").toString();
    				getBookmarkWindow(table, key);
                }
            }
        });
		
	}

	public void getBookmarkPreview(String key) {
		bookmarkService.queryBookmark(key,new AsyncCallback<Bookmark>() {
			@Override
			public void onFailure(Throwable caught) {
				GWT.log("Unable to load bookmarks for table: "+ table.getName(), caught);
				Info.display("Error", "Unable to load bookmarks.");
				unmask();
			}
			@Override
			public void onSuccess(Bookmark result) {
			   	final MyPopup popup = new MyPopup(result, table);
            	popup.setStyleName("popup");
            	popup.setPopupPositionAndShow(new PopupPanel.PositionCallback() {
                	public void setPosition(int offsetWidth, int offsetHeight) {
	                	int left = (50);
	                	int top = (150);
	                	popup.setPopupPosition(left, top);
                	}
            	});
			}
		});
	}
	
	public void getBookmarkWindow(final Table table, String key) {
		System.out.println("***** doppio click entra in getBookmarkWindow(final Table table, String key)");
		bookmarkService.queryBookmark(key,new AsyncCallback<Bookmark>() {
			@Override
			public void onFailure(Throwable caught) {
				GWT.log("Unable to load bookmarks for table: "+ table.getName(), caught);
				Info.display("Error", "Unable to load bookmarks.");
				unmask();
			}
			@Override
			public void onSuccess(Bookmark b) {
				//BookmarkWindow bookmarkPopup= new BookmarkWindow(table, b);
				
				BookmarkWindow bookmarkPopup= new BookmarkWindow(table);
				
			}
		});
	}
	
	private void loadBookmark() {
		bookmarksStore.removeAll();
		mask();
		if (table.getKey() != null)
			tableService.getTableBookmark(table.getKey(),new AsyncCallback<List<Bookmark>>() {
				@Override
				public void onFailure(Throwable caught) {
					GWT.log("Unable to load bookmarks for table: "+ table.getName(), caught);
					Info.display("Error", "Unable to load bookmarks.");
					unmask();
				}
				@Override
				public void onSuccess(List<Bookmark> result) {
					fillGrid(result);
					unmask();
				}
			});
	}

	private void showInputPanel() {
		if (inputPanel == null) inputPanel = buildPanel();
		mainContainer.remove(grid);
		loadButton.disable();
		addButton.disable();
		mainContainer.setLayout(centerLayout);
		mainContainer.add(inputPanel);
		mainContainer.layout();
	}

	private void shareBookmark(final Bookmark bookmark) {
		tableService.addBookmark(table.getKey(), bookmark,new AsyncCallback<Boolean>() {
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
					GWT.log("Unable to load the comment's number", caught);
					Info.display("Error", "Unable to load the comment's number.");
					unmask();
				}
				@Override
				public void onSuccess(List<Comment> result) {
					model.set("comment",  result.size());
				}				
			});
			model.set("tag", bookmark.getTagString());
			model.set("annotation", bookmark.getAnnotationNumber());
			bookmarksStore.add(model);
		}
		contextMenu.setEnabled(bookmarksStore.getCount() > 0);
	}

	private ColumnModel getColumnModel() {
		ColumnConfig title = new ColumnConfig("title", "Title", 80);
		ColumnConfig url = new ColumnConfig("url", "Url", 170);
		ColumnConfig legend = new ColumnConfig("legend", "Legend", 145);
		ColumnConfig comment = new ColumnConfig("comment", "Comment", 60);
		ColumnConfig tag = new ColumnConfig("tag", "Tag", 100);
		ColumnConfig annotation = new ColumnConfig("annotation", "Annotation", 60);
		List<ColumnConfig> config = new ArrayList<ColumnConfig>();
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
		tag.setToolTip("All existing tags: \n"+allTags.toString());
		
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
				if (annotation.getValue()!=null)
					bookmark.addAnnotation(annotation.getValue());
				if (tag.getValue()!=null)
					bookmark.addTag(tag.getValue().toUpperCase());
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
}
