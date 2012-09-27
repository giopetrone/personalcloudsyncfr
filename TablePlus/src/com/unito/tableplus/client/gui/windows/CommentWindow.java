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
import com.extjs.gxt.ui.client.util.IconHelper;
import com.extjs.gxt.ui.client.util.Margins;
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
import com.unito.tableplus.client.TablePlus;
import com.unito.tableplus.client.services.BookmarkService;
import com.unito.tableplus.client.services.BookmarkServiceAsync;
import com.unito.tableplus.client.services.ServiceFactory;
import com.unito.tableplus.client.services.TableServiceAsync;
import com.unito.tableplus.shared.model.Bookmark;
import com.unito.tableplus.shared.model.Comment;
import com.unito.tableplus.shared.model.Table;
import com.unito.tableplus.shared.model.VisibilityType;

public class CommentWindow extends WindowPlus {
	
	// crea il servizio per segnalibri
	private final BookmarkServiceAsync bookmarkService = GWT.create(BookmarkService.class);
	private final TableServiceAsync tableService = ServiceFactory.getTableServiceInstance();
	
	private ListStore<BaseModel> commentStore;	
	private LayoutContainer mainContainer;
	private Grid<BaseModel> grid;
	private Button loadButton;
	private Button addButton;
	private Button deleteButton;
	private Layout fitLayout = new FitLayout();
	private Layout centerLayout = new CenterLayout();
	private FormPanel inputPanel;
	private Menu contextMenu;
	private MenuItem deleteItem;
	private MenuItem editItem;
	private boolean edit=false;	
	private	Bookmark b;
	private	Table table;
	private int maxWidthComment=120;
	private int maxWidthAuthor=130;
	private Radio radio1;

	//	public CommentWindow(final Bookmark b, final Table table) {
	public CommentWindow(final Table table) {
		super();
		this.table=table;
		getBookmark();      
	}

	private void getBookmark() {
		//provvisorio: prendo il primo segnalibro salvato, nella futura implementazione 
		//verra preso come parametro al click sul bottone comment della finestra BookmarkWindows
		
		tableService.getTableBookmark(table.getKey(),new AsyncCallback<List<Bookmark>>() {
			@Override
			public void onFailure(Throwable caught) {
				GWT.log("Unable to load the bookmark! ", caught);
				Info.display("Error", "Unable to load the bookmarks.");
				unmask();
			}
			@Override
			public void onSuccess(List<Bookmark> result) {			
				b=result.get(0);
				createWindow();
			}
		});
	}

	protected void createWindow() {
	    getPixelWidth();
	    setSize(maxWidthAuthor+309+maxWidthComment, 250);
	    
		setHeading("Bookmark's "+b.getTitle()+" comments");
		setLayout(new RowLayout(Orientation.VERTICAL));
		setButtonAlign(HorizontalAlignment.LEFT);
		mainContainer = new LayoutContainer();
		mainContainer.setLayout(fitLayout);
		mainContainer.setScrollMode(Scroll.AUTOY);
		
		commentStore = new ListStore<BaseModel>();
		grid = new Grid<BaseModel>(commentStore, getColumnModel());
		grid.setHeight(250);
		contextMenu = new Menu();
		deleteItem = new MenuItem();
		deleteItem.setText("Delete Comment");
		deleteItem.addSelectionListener(new SelectionListener<MenuEvent>() {
			@Override
			public void componentSelected(MenuEvent ce) {
				delete();
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
		add(mainContainer, new RowData(1, 1, new Margins(4)));
		
//refresh		
		loadButton = new Button("Refresh", new SelectionListener<ButtonEvent>() {
			@Override
			public void componentSelected(ButtonEvent ce) {
				loadComments();
			}
		});
		loadButton.setToolTip(new ToolTipConfig("Refresh comments"));
		loadButton.setIcon(IconHelper.createStyle("arrow_refresh"));
//add
		addButton = new Button("Add Comment", new SelectionListener<ButtonEvent>() {
			@Override
			public void componentSelected(ButtonEvent ce) {
				addComment();
			}
		});
		addButton.setToolTip(new ToolTipConfig("Add comment"));
		addButton.setIcon(IconHelper.createStyle("add"));		
//delete	
		deleteButton = new Button("Delete All", new SelectionListener<ButtonEvent>() {
			@Override
			public void componentSelected(ButtonEvent ce) {
				System.out.println("deleteComment");

				deleteComment();
			}
		});
		deleteButton.setToolTip(new ToolTipConfig("Delete All"));
		deleteButton.setIcon(IconHelper.createStyle("deleteAll"));		
		
		addButton(loadButton);
		addButton(addButton);
		addButton(deleteButton);

		loadComments();
    }
	private void deleteComment() {
		for (final BaseModel bm : commentStore.getModels()){
			if (bm.get("author").equals(TablePlus.getUser().getEmail())){
				System.out.println("deleteComment: "+bm.get("comment"));

				bookmarkService.deleteComment(bm.get("key").toString(), new AsyncCallback<Boolean>() {
					@Override
					public void onFailure(Throwable caught) {
						GWT.log("Unable to delete comment", caught);
						Info.display("Error", "Unable to delete comment.");
					}
					@Override
					public void onSuccess(Boolean result) {
						System.out.println("cancellato commento");
						commentStore.remove(bm);
						loadComments();
					}
				});
			}
		}
	}
	protected void addComment() {
		inputPanel = newComment();
		showForm();
	}

	private FormPanel newComment() {
		final FormPanel panel = new FormPanel();
		FormData formData = new FormData("-20");
		panel.setHeading("New Comment from "+TablePlus.getUser().getEmail());
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
		comment.setAllowBlank(true);
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
				rebuild();
			}
		});
		saveButton.addSelectionListener(new SelectionListener<ButtonEvent>() {
			@Override
			public void componentSelected(ButtonEvent ce) {
				Comment c;
				if (radio1.getValue()) c = new Comment(comment.getValue(),TablePlus.getUser().getEmail());
				else c = new Comment(comment.getValue(),TablePlus.getUser().getEmail(), VisibilityType.PRIVATE);
				addComment(c);
				comment.clear();
				rebuild();
			}
		});
		return panel;
	}

	public void addComment(Comment c) {
		bookmarkService.addComment(b.getKey(), c, new AsyncCallback<Boolean>() {
			@Override
			public void onFailure(Throwable caught) {
				GWT.log("Failed to post comment: ", caught);
				Info.display("Error", "Failed to post comment.");
			}
			@Override
			public void onSuccess(Boolean result) {
				GWT.log("Successfully posted comment ");
				Info.display("Success", "Successfully posted comment ");
				loadComments();
			}
		});
	}

	public void delete() {
		final BaseModel selected = grid.getSelectionModel().getSelectedItem();
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
				contextMenu.setEnabled(commentStore.getCount() > 0);
			}
		});
		
	}

	private void showInputPanel(String c, String key) {
		inputPanel = buildPanel(c, key);
		showForm();
	} 
    
	private FormPanel buildPanel(String c, final String key) {
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
				rebuild();
			}
		});
		saveButton.addSelectionListener(new SelectionListener<ButtonEvent>() {
			@Override
			public void componentSelected(ButtonEvent ce) {
				Comment c;
				if (radio1.getValue()) c = new Comment(comment.getValue(),TablePlus.getUser().getEmail());
				else c = new Comment(comment.getValue(),TablePlus.getUser().getEmail(), VisibilityType.PRIVATE);
				editComment(c, key);
				comment.clear();
				rebuild();
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

	private void editComment(final Comment comment, final String key) {
		bookmarkService.addComment(b.getKey(), comment, new AsyncCallback<Boolean>() {
			@Override
			public void onFailure(Throwable caught) {
				GWT.log("Failed to post comment: ", caught);
				Info.display("Error", "Failed to post comment.");
			}
			@Override
			public void onSuccess(Boolean result) {
				
				bookmarkService.deleteComment(key, new AsyncCallback<Boolean>() {
					@Override
					public void onFailure(Throwable caught) {
						GWT.log("Unable to delete comment", caught);
						Info.display("Error", "Unable to delete comment.");
					}
					@Override
					public void onSuccess(Boolean result) {
						edit=true;
					}
				});
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
			model.set("visibility", c.getVisibilty());
			commentStore.add(model);
		}
		contextMenu.setEnabled(commentStore.getCount() > 0);
	}

	private ColumnModel getColumnModel() {
		ColumnConfig author = new ColumnConfig("author", "Author", maxWidthAuthor);
		ColumnConfig comment = new ColumnConfig("comment", "Comment", maxWidthComment);
		ColumnConfig date = new ColumnConfig("date", "Date", 200);
		ColumnConfig visibility = new ColumnConfig("visibility", "Visibility", 60);
		List<ColumnConfig> config = new ArrayList<ColumnConfig>();
		config.add(author);
		config.add(comment);
		config.add(date);
		config.add(visibility);
		return new ColumnModel(config);
	}

	private int maxWidthAuthor(List<Comment> result) {
		int maxWidthAuthor=0;
		for (Comment c: result){
			if (c.getAuthor().length()>maxWidthAuthor)
				maxWidthAuthor=c.getAuthor().length();
		}
		return maxWidthAuthor;
	}
	
	private int maxWidthComment(List<Comment> result) {
		int maxWidthComment=0;
		for (Comment c: result){
			if (c.getComment().length()>maxWidthComment)
				maxWidthComment=c.getComment().length();
		}
		return maxWidthComment;
	}
		
	private void showForm() {	
		mainContainer.remove(grid);
		loadButton.disable();
		addButton.disable();
		mainContainer.setLayout(centerLayout);
		mainContainer.add(inputPanel);
		mainContainer.layout();
		
	}

	private void rebuild() {
		mainContainer.remove(inputPanel);
		mainContainer.setLayout(fitLayout);
		mainContainer.add(grid);
		loadButton.enable();
		addButton.enable();
		mainContainer.layout();
	}

	private void getPixelWidth() {
		bookmarkService.getComments(b.getKey(),new AsyncCallback<List<Comment>>() {
			@Override
			public void onFailure(Throwable caught) {
				GWT.log("Unable to load comments for bookmark: "+ b.getTitle(), caught);
				Info.display("Error", "Unable to load comments.");
			}
			@Override
			public void onSuccess(List<Comment> result) {
				maxWidthAuthor=maxWidthAuthor(result)*8;
				maxWidthComment=maxWidthComment(result)*8;
			}
		});
	}

}

