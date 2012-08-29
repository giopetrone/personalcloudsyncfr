package com.unito.tableplus.client.gui.windows;

import java.util.ArrayList;
import java.util.List;
import com.extjs.gxt.ui.client.Style.HorizontalAlignment;
import com.extjs.gxt.ui.client.Style.Scroll;
import com.extjs.gxt.ui.client.data.BaseModel;
import com.extjs.gxt.ui.client.event.ButtonEvent;
import com.extjs.gxt.ui.client.event.MenuEvent;
import com.extjs.gxt.ui.client.event.SelectionListener;
import com.extjs.gxt.ui.client.store.ListStore;
import com.extjs.gxt.ui.client.widget.HorizontalPanel;
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
import com.extjs.gxt.ui.client.widget.layout.FitLayout;
import com.extjs.gxt.ui.client.widget.layout.FormData;
import com.extjs.gxt.ui.client.widget.menu.Menu;
import com.extjs.gxt.ui.client.widget.menu.MenuItem;
import com.google.gwt.core.client.GWT;
import com.google.gwt.user.client.rpc.AsyncCallback;
import com.google.gwt.user.client.ui.FlowPanel;
import com.google.gwt.user.client.ui.Panel;
import com.google.gwt.user.client.ui.PopupPanel;
import com.unito.tableplus.client.TablePlus;
import com.unito.tableplus.client.services.BookmarkService;
import com.unito.tableplus.client.services.BookmarkServiceAsync;
import com.unito.tableplus.client.services.CommentService;
import com.unito.tableplus.client.services.CommentServiceAsync;
import com.unito.tableplus.shared.model.Bookmark;
import com.unito.tableplus.shared.model.Comment;
import com.unito.tableplus.shared.model.Table;

public class CommentsPopup extends PopupPanel {
	
	// crea il servizio per segnalibri
	protected final BookmarkServiceAsync bookmarkService = GWT.create(BookmarkService.class);
	protected final CommentServiceAsync commentService = GWT.create(CommentService.class);
	
	private LayoutContainer mainContainer;
	private Grid<BaseModel> grid;
	private ListStore<BaseModel> commentStore;
	private Button loadButton;
	private Layout fitLayout = new FitLayout();
	private FormPanel inputPanel;
	private Menu contextMenu;
	private MenuItem deleteItem;
	private MenuItem editItem;
	private Panel popupPanel= new FlowPanel();
	private boolean edit=false;
	private Bookmark b;
	
    public CommentsPopup(final Bookmark b, final Table table) {
	    super(false);
	    this.b=b;
	    setPixelSize(350, 400);
	   	add(popupPanel);
		mainContainer = new LayoutContainer();
		mainContainer.setLayout(fitLayout);
		mainContainer.setScrollMode(Scroll.AUTO);
		commentStore = new ListStore<BaseModel>();
		grid = new Grid<BaseModel>(commentStore, getColumnModel());
		grid.setHeight(200);
		contextMenu = new Menu();
		deleteItem = new MenuItem();
		deleteItem.setText("Delete Comment");
		deleteItem.addSelectionListener(new SelectionListener<MenuEvent>() {
			@Override
			public void componentSelected(MenuEvent ce) {
				final BaseModel selected = grid.getSelectionModel().getSelectedItem();
				String key = selected.get("key").toString();
				bookmarkService.deleteComment(key, new AsyncCallback<Void>() {
					@Override
					public void onFailure(Throwable caught) {
						GWT.log("Unable to delete comment", caught);
						Info.display("Error", "Unable to delete comment.");
					}
					@Override
					public void onSuccess(Void result) {
						commentStore.remove(selected);
						contextMenu.setEnabled(commentStore.getCount() > 0);
					}
				});
			}
		});
		contextMenu.add(deleteItem);
		
		//edit comment, cancella il precedente e crea nuovo commento
		
		editItem = new MenuItem();
		editItem.setText("Edit Comment");
		editItem.addSelectionListener(new SelectionListener<MenuEvent>() {
			@Override
			public void componentSelected(MenuEvent ce) {
				final BaseModel selected = grid.getSelectionModel().getSelectedItem();
				final String key = selected.get("key").toString();
				bookmarkService.editComment(b, key, new AsyncCallback<String>() {

					@Override
					public void onFailure(Throwable caught) {
						GWT.log("Unable to edit comment", caught);
						Info.display("Error", "Unable to edit comment.");
					}
					@Override
					public void onSuccess(String c) {		
						showInputPanel(c, key);
						if (edit){
							commentStore.remove(selected);
							contextMenu.setEnabled(commentStore.getCount() > 0);
						}
					}
				});
			}
		});
		contextMenu.add(editItem);		
		grid.setContextMenu(contextMenu);
		mainContainer.add(grid);
		popupPanel.add(mainContainer);
		
		loadButton = new Button("Load Comment", new SelectionListener<ButtonEvent>() {
			@Override
			public void componentSelected(ButtonEvent ce) {
				loadComments();
			}
		});
		HorizontalPanel hp= new HorizontalPanel();
		
		mainContainer.add(hp);
		hp.add(loadButton);

	    Button closeButton = new Button("Close");
	    closeButton.addSelectionListener(new SelectionListener<ButtonEvent>() {
	    	public void componentSelected(ButtonEvent ce) {
	              hide();
	        }
	    });
	    hp.add(closeButton);
		loadComments();
    }
    
	private void showInputPanel(String c, String key) {
		inputPanel = buildPanel(c, key);
		popupPanel.add(inputPanel);

	} 
    
	private FormPanel buildPanel(String c, final String key) {
		final FormPanel panel = new FormPanel();
		FormData formData = new FormData("-20");
		panel.setHeading("Edit Comment");
		panel.setFrame(true);
		panel.setWidth(350);
		
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
		panel.setButtonAlign(HorizontalAlignment.CENTER);
		FormButtonBinding binding = new FormButtonBinding(panel);
		binding.addButton(saveButton);
		cancelButton.addSelectionListener(new SelectionListener<ButtonEvent>() {
			@Override
			public void componentSelected(ButtonEvent ce) {
				inputPanel.hide();
			}
		});
		saveButton.addSelectionListener(new SelectionListener<ButtonEvent>() {
			@Override
			public void componentSelected(ButtonEvent ce) {
				Comment c = new Comment(comment.getValue(),TablePlus.getUser().getEmail());
				addComment(c);
				comment.clear();
				bookmarkService.deleteComment(key, new AsyncCallback<Void>() {
					@Override
					public void onFailure(Throwable caught) {
						GWT.log("Unable to delete comment", caught);
						Info.display("Error", "Unable to delete comment.");
					}
					@Override
					public void onSuccess(Void result) {
						edit=true;
					}
				});
				inputPanel.hide();
			}
		});
		return panel;
	}
	
	private void loadComments() {
		commentStore.removeAll();
		if (b.getKey() != null)
			bookmarkService.getComments(b.getKey(),new AsyncCallback<List<Comment>>() {
				@Override
				public void onFailure(Throwable caught) {
					GWT.log("Unable to load comments for bookmark: "+ b.getTitle(), caught);
					Info.display("Error", "Unable to load comments.");
				}
				@Override
				public void onSuccess(List<Comment> result) {
					fillGrid(result);
				}
			});
	}

	private void addComment(final Comment comment) {
		bookmarkService.addComment(b.getKey(), comment, new AsyncCallback<Boolean>() {
			@Override
			public void onFailure(Throwable caught) {
				GWT.log("Failed to post comment: ", caught);
				Info.display("Error", "Failed to post comment.");
			}
			@Override
			public void onSuccess(Boolean result) {
				loadComments();
			}
		});
	}

	private void fillGrid(List<Comment> result) {
		BaseModel model;
		for (final Comment c : result) {
			model = new BaseModel();
			model.set("key", c.getKey());
			model.set("author", c.getAuthor());
			model.set("comment", c.getComment());
			model.set("date", c.getDate());
			commentStore.add(model);
		}
		contextMenu.setEnabled(commentStore.getCount() > 0);
	}

	private ColumnModel getColumnModel() {
		ColumnConfig author = new ColumnConfig("author", "Author", 100);
		ColumnConfig comment = new ColumnConfig("comment", "Comment", 150);
		ColumnConfig date = new ColumnConfig("date", "Date", 100);
		List<ColumnConfig> config = new ArrayList<ColumnConfig>();
		config.add(author);
		config.add(comment);
		config.add(date);
		return new ColumnModel(config);
	}
}  

