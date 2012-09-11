package com.unito.tableplus.client.gui.windows;

import java.util.List;
import com.extjs.gxt.ui.client.Style.HorizontalAlignment;
import com.extjs.gxt.ui.client.Style.Orientation;
import com.extjs.gxt.ui.client.Style.Scroll;
import com.extjs.gxt.ui.client.data.BaseModel;
import com.extjs.gxt.ui.client.event.ButtonEvent;
import com.extjs.gxt.ui.client.event.ComponentEvent;
import com.extjs.gxt.ui.client.event.KeyListener;
import com.extjs.gxt.ui.client.event.SelectionListener;
import com.extjs.gxt.ui.client.store.ListStore;
import com.extjs.gxt.ui.client.util.IconHelper;
import com.extjs.gxt.ui.client.util.Margins;
import com.extjs.gxt.ui.client.widget.HorizontalPanel;
import com.extjs.gxt.ui.client.widget.Html;
import com.extjs.gxt.ui.client.widget.HtmlContainer;
import com.extjs.gxt.ui.client.widget.Info;
import com.extjs.gxt.ui.client.widget.Label;
import com.extjs.gxt.ui.client.widget.LayoutContainer;
import com.extjs.gxt.ui.client.widget.VerticalPanel;
import com.extjs.gxt.ui.client.widget.button.Button;
import com.extjs.gxt.ui.client.widget.form.ComboBox;
import com.extjs.gxt.ui.client.widget.form.Radio;
import com.extjs.gxt.ui.client.widget.form.RadioGroup;
import com.extjs.gxt.ui.client.widget.form.TextArea;
import com.extjs.gxt.ui.client.widget.form.ComboBox.TriggerAction;
import com.extjs.gxt.ui.client.widget.layout.RowData;
import com.extjs.gxt.ui.client.widget.layout.RowLayout;
import com.extjs.gxt.ui.client.widget.tips.ToolTipConfig;
import com.google.gwt.core.client.GWT;
import com.google.gwt.user.client.Window;
import com.google.gwt.user.client.rpc.AsyncCallback;
import com.google.gwt.user.client.ui.FlexTable;
import com.google.gwt.user.client.ui.Panel;
import com.google.gwt.user.client.ui.PopupPanel;
import com.google.gwt.user.client.ui.SimplePanel;
import com.google.gwt.user.client.ui.Widget;
import com.unito.tableplus.client.TablePlus;
import com.unito.tableplus.client.services.BookmarkService;
import com.unito.tableplus.client.services.BookmarkServiceAsync;
import com.unito.tableplus.client.services.ServiceFactory;
import com.unito.tableplus.client.services.TableServiceAsync;
import com.unito.tableplus.shared.model.BlackBoardMessageType;
import com.unito.tableplus.shared.model.Bookmark;
import com.unito.tableplus.shared.model.Comment;
import com.unito.tableplus.shared.model.Table;
import com.unito.tableplus.shared.model.VisibilityType;
  
public class BookmarkPopup extends PopupPanel {
	
	private final BookmarkServiceAsync bookmarkService = GWT.create(BookmarkService.class);
	private final TableServiceAsync tableService = ServiceFactory.getTableServiceInstance();
	private Table table;
	private LayoutContainer mainContainer;
	private HtmlContainer historyContainer;
	private TextArea inputArea;
	private Bookmark b;
	private String message ="<div align=\"left\"><br><b>Comments:<br><br></b></div>";	
	private Radio radio = new Radio();  
	private Radio radio2 = new Radio();
	private FlexTable ftable = new FlexTable();
	private HorizontalPanel hpp= new HorizontalPanel();
	private VerticalPanel main= new VerticalPanel();
	
	public BookmarkPopup(final Table table, Bookmark b){
		super(false);
		this.table = table;
		this.b=b;	
		this.setVisible(true);
		this.center();
		
		setStyleName("popup");
		main.setLayout(new RowLayout(Orientation.VERTICAL));
				
		mainContainer = new LayoutContainer();
		mainContainer.setSize(550,500);
		mainContainer.setScrollMode(Scroll.AUTO);			

		setTitle();
		setFrame();
		createFlexTable();
		createCommentPanel();
		radioButton();
		createInputArea();
		createButtonPanel();
		
		main.add(mainContainer);
		setWidget(main);	

	}
	
	private void setTitle() {
		//title	
		//main.setHeading("Bookmark: "+b.getTitle());
		Html title= new Html("<div align=\"center\"><b>\nThe bookmak <u>"+b.getTitle()+"</u> has been shared on this table\n\n</b></div> ");
		title.setHeight(30);
		mainContainer.add(title);
	}
	
	private void setFrame() {
		//frame anteprima				
		LayoutContainer preview=  new LayoutContainer();
		preview.add(hpp);
		hpp.add( new Html("<iframe src='"+b.getUrl()+" width='300'; height='200'<p><a href='"+b.getUrl()+"'>"+b.getTitle()+"</a></p></iframe>"));
		mainContainer.add(preview);
	}

	private void createFlexTable() {
		ftable.insertRow(0);
		ftable.insertRow(1);
		ftable.addCell(0);
		ftable.addCell(0);
		ftable.addCell(1);
		ftable.addCell(1);

		hpp.add(ftable);
				
		ftable.getCellFormatter().addStyleName(0, 1,"FlexTable-button0");
		ftable.getCellFormatter().addStyleName(0, 0,"FlexTable-text0");
		ftable.getCellFormatter().addStyleName(1, 0,"FlexTable-text1");
		ftable.getCellFormatter().addStyleName(1, 1,"FlexTable-button1");
//legend				
		ftable.setWidget(0, 0, getHtmlLegend());
//edit legend				
		Button editLegendButton=new Button("Edit");
		editLegendButton.setToolTip(new ToolTipConfig("Edit the bookmark's legend"));
		editLegendButton.setIcon(IconHelper.createStyle("edit"));			
		ftable.setWidget(0, 1, editLegendButton);
				
		editLegendButton.addSelectionListener(new SelectionListener<ButtonEvent>() {
			public void componentSelected(ButtonEvent ce) {
				ftable.setWidget(0, 0, setFormLegend());
			}
		});					
//tag				
		ftable.setWidget(1, 0,new Html("<div style=\"padding-left:10px;\"><b>Tag:</b>" +
				"<div style=\"padding-top:5px;\">"+b.getTag()+"</div></div>"));
//add tag			
		Button addTag=new Button("Add");
		addTag.setToolTip(new ToolTipConfig("Add a new tag to bookmark"));
		addTag.setIcon(IconHelper.createStyle("addTag"));			
		ftable.setWidget(1, 1,addTag);	
		addTag.addSelectionListener(new SelectionListener<ButtonEvent>() {
			public void componentSelected(ButtonEvent ce) {
				ftable.setWidget(1, 0, setFormTag());
			}

		});		
	}

	private void createCommentPanel() {
		//commenti				
		LayoutContainer commentPanel= new LayoutContainer();
		commentPanel.setScrollMode(Scroll.AUTO);	
		historyContainer = new HtmlContainer(message);
		commentPanel.add(historyContainer);
		mainContainer.add(commentPanel);	
		historyContainer.scrollIntoView(commentPanel);
		//main.add(mainContainer, new RowData(1, 1, new Margins(4)));
		HorizontalPanel hp= new HorizontalPanel();
		mainContainer.add(hp);
		loadComments();	
	}		

	private void createButtonPanel() {
		//close button				
		Button closeButton = new Button("Close");
		closeButton.addSelectionListener(new SelectionListener<ButtonEvent>() {
			public void componentSelected(ButtonEvent ce) {
				hide();
			}
		});
		//go button				
		Button goButton = new Button("Go");
		goButton.setToolTip(new ToolTipConfig("View bookmark"));
		goButton.setIcon(IconHelper.createStyle("go"));
		goButton.addSelectionListener(new SelectionListener<ButtonEvent>() {
			public void componentSelected(ButtonEvent ce) {
				Window.open(b.getUrl(), "", "left=100,top=100,width=600,height=400,menubar,toolbar,resizable");
			}
		});	
		//bottone commento
		Button commentButton = new Button("Comments");
		commentButton.setToolTip(new ToolTipConfig("View and edit comments"));
		commentButton.setIcon(IconHelper.createStyle("comment"));						 			    
							    
		commentButton.addSelectionListener(new SelectionListener<ButtonEvent>(){
			public void componentSelected(ButtonEvent ce) {
		   	  	final CommentsPopup popup = new CommentsPopup(b, table);
		   	   	popup.setStyleName("popup");
		       	popup.setPopupPositionAndShow(new PopupPanel.PositionCallback() {
		           	public void setPosition(int offsetWidth, int offsetHeight) {
			           	int left = (50);
			            int top = (150);
					    popup.setPopupPosition(left, top);
				    }
				});	
				loadComments();	
			}
		});	
		//bottone refresh
		Button refreshButton = new Button("Refresh");
		refreshButton.setToolTip(new ToolTipConfig("Refresh comments"));
		refreshButton.setIcon(IconHelper.createStyle("arrow_refresh"));
		refreshButton.addSelectionListener(new SelectionListener<ButtonEvent>(){
			public void componentSelected(ButtonEvent ce) {
		         loadComments();	
			}
		});	
		//pannello bottoni				
		HorizontalPanel hpButton= new HorizontalPanel();
		hpButton.add(commentButton);
		hpButton.add(refreshButton);
		hpButton.add(goButton);
		hpButton.add(closeButton);	

		mainContainer.add(hpButton);
		
	}

	private void radioButton() {
		//radio button
		HorizontalPanel hpv= new HorizontalPanel();
		radio.setSize(55, 5);
		radio.setBoxLabel("Public");  
		radio.setValue(true);  

		radio2.setBoxLabel("Private");  
		radio2.setSize(55, 5);
		RadioGroup radioGroup = new RadioGroup();  
		radioGroup.setFieldLabel("Comment Visibility: ");  
		radioGroup.add(radio);  
		radioGroup.add(radio2);  
		hpv.add(radioGroup);
		radioGroup.setIntStyleAttribute("font-size", 6);
		radioGroup.setStyleName("html");
		radioGroup.setStyleAttribute("font-size", "6px");
		//main.add(hpv, new RowData(1, -1, new Margins(10)));
		mainContainer.add(hpv);
	}

	private void createInputArea() {
		//input area			    
		inputArea = new TextArea();
		inputArea.setEmptyText("Leave a comment");
		inputArea.setHeight(40);
		inputArea.setWidth(545);
		mainContainer.add(inputArea, new RowData(1, -1, new Margins(4)));
		inputArea.addKeyListener(new KeyListener() {
			@Override
			public void componentKeyPress(ComponentEvent event) {
				if (event.getKeyCode() == 13) {
					addComment();
				}
			}
		});	
	}



	public Widget getHtmlLegend() {
		return new Html("<div style=\"padding-left:10px;\"><b>Legend:</b>" +
				"<div style=\"padding-top:5px;padding-bottom:5px\">"+b.getLegend()+"</div></div>");
	}

	public void addComment() {
		if (inputArea.getValue() != null){
			
			Comment c;
			if (radio.getValue()){
				c = new Comment(inputArea.getValue(), TablePlus.getUser().getEmail());		
			}
			else {
				c = new Comment(inputArea.getValue(), TablePlus.getUser().getEmail(), VisibilityType.PRIVATE);	
			}
			bookmarkService.addComment(b.getKey(), c,new AsyncCallback<Boolean>() {
					@Override
					public void onFailure(Throwable caught) {
						GWT.log("Failed to add comment to bookmark: ", caught);
						Info.display("Error", "Failed to add comment to bookmark.");
					}
					@Override
					public void onSuccess(Boolean result) {
						inputArea.setValue("");
						loadComments();
					}
			});	
				
		}
	}
	
	public void loadComments() {
		bookmarkService.getComments(b.getKey(),new AsyncCallback<List<Comment>>() {
			@Override
			public void onFailure(Throwable caught) {
				GWT.log("Unable to load comments of bookmarks: "+ b.getTitle(), caught);
				Info.display("Error", "Unable to load comments.");
			}
			@Override
			public void onSuccess(List<Comment> result) {
				if (result.size()>0) {
				for(Comment c: result){
					System.out.println(c.toString());
					//se i commenti sono privati
					if(c.isPrivate()){
						//se i commenti sono stati fatti dallo stesso utente sono visualizzati e sottolineati
						if (c.getAuthor().equals(TablePlus.getUser().getEmail())){
							message+="<div><div style=\"color: #191970;float:left;\">&lt<u>"+c.getAuthor()
									+"</u>&gt </div><div style=\"color:	#3D3D4C;float:left;\">"+c.getDateString()+"</div> - "
									+c.getComment()+"</div>";	
						}
						else 	message+="<div><div style=\"color: #191970;float:left;\">&lt"+c.getAuthor()
								+"&gt Commento Privato</div>";	
					}
					//se sono pubblici
					else {
						//se i commenti sono stati fatti dallo stesso utente sono visualizzati e sottolineati
						if (c.getAuthor().equals(TablePlus.getUser().getEmail())){
							message+="<div><div style=\"color: #191970;float:left;\">&lt<u>"+c.getAuthor()
									+"</u>&gt </div><div style=\"color:	#3D3D4C;float:left;\">"+c.getDateString()+"</div> - "
									+c.getComment()+"</div>";	
						}
						else
						message+="<div><div style=\"color: #191970;float:left;\">&lt"+c.getAuthor()
							+"&gt </div><div style=\"color:	#3D3D4C;float:left;\">"+c.getDateString()+"</div> - "
							+c.getComment()+"</div>";	
						}
				}
				historyContainer.add(new Label(message+"<br>"), "");
				message="<div align=\"left\"><br><b>Comments:<br><br></b></div>";	
				}
				else {
					message="<div align=\"left\"><br><b>There are no comments for the bookmark!</b><br></div>";
					historyContainer.add(new Label(message+"<br>"), "");
				}
			}
		});					
	}	
	
	public LayoutContainer setFormLegend(){
		final LayoutContainer panel = new LayoutContainer();
		panel.setStyleAttribute("padding-left", "10px");
		
		Label label=new Label("Insert the new legend:");
		panel.setSize(160, 150);
		
		label.setStyleAttribute("padding-bottom", "10px");
		panel.add(label);	
		
		final TextArea legend = new TextArea();
		legend.setHeight(50);
		legend.setWidth(120);
		legend.setValue(b.getLegend());
		legend.setPreventScrollbars(true);
		legend.setAllowBlank(false);
		legend.setStyleAttribute("padding-top", "10px");
		legend.setStyleAttribute("padding-bottom", "10px");
		panel.add(legend);	
		
	    HorizontalPanel hp= new HorizontalPanel();  
	    hp.setWidth(160);
	    hp.setStyleAttribute("padding-top", "17px");
	    hp.setHorizontalAlign(HorizontalAlignment.LEFT);
		Button saveButton = new Button("Save");
		hp.add(saveButton);
		saveButton.setStyleAttribute("padding-right", "10px");
		
		Button cancelButton = new Button("Cancel");
		
		hp.add(cancelButton);
		cancelButton.addSelectionListener(new SelectionListener<ButtonEvent>() {
			@Override
			public void componentSelected(ButtonEvent ce) {
				ftable.setWidget(0, 0, getHtmlLegend());
			}
		});
		saveButton.addSelectionListener(new SelectionListener<ButtonEvent>() {
			@Override
			public void componentSelected(ButtonEvent ce) {
				editLegend(legend.getValue());
			}
		});
		panel.add(hp);
		return panel;
	}
	
	public LayoutContainer setFormTag(){
		final LayoutContainer panel = new LayoutContainer();
		panel.setStyleAttribute("padding-left", "10px");
		
		Label label=new Label("Add a tag to bookmark:");		
		label.setStyleAttribute("padding-bottom", "10px");
		panel.add(label);	
		ListStore<BaseModel> tagStore = new ListStore<BaseModel>();
		BaseModel tagModel;
		for (String tag : b.getTag()) {
			tagModel = new BaseModel();
			tagModel.set("type", tag);
			tagStore.add(tagModel);
		}

		final ComboBox<BaseModel> combo = new ComboBox<BaseModel>();
		combo.setFieldLabel("Tag");
		combo.setDisplayField("tag");
		combo.setTriggerAction(TriggerAction.ALL);
		combo.setStore(tagStore);
		combo.setAllowBlank(false);
		combo.setEditable(false);
		combo.setWidth(120);
		combo.setStyleAttribute("padding-top", "10px");
		panel.add(combo);	
		
	    HorizontalPanel hp= new HorizontalPanel();  
	    hp.setWidth(160);
	    hp.setStyleAttribute("padding-top", "17px");
	    hp.setHorizontalAlign(HorizontalAlignment.LEFT);
		Button saveButton = new Button("Save");
		hp.add(saveButton);
		saveButton.setStyleAttribute("padding-right", "10px");
		
		Button cancelButton = new Button("Cancel");
		ftable.setWidget(1, 0, viewTag());
		hp.add(cancelButton);
		cancelButton.addSelectionListener(new SelectionListener<ButtonEvent>() {
			@Override
			public void componentSelected(ButtonEvent ce) {
				
			}
		});
		saveButton.addSelectionListener(new SelectionListener<ButtonEvent>() {
			@Override
			public void componentSelected(ButtonEvent ce) {
				
			}
		});
		panel.add(hp);
		return panel;
	}
	
	public Widget viewTag() {
		// TODO Auto-generated method stub
		return null;
	}

	public void editLegend(final String value) {
		bookmarkService.editLegend(b.getKey(),value, new AsyncCallback<Boolean>() {
			@Override
			public void onFailure(Throwable caught) {
				GWT.log("Unable to edit the bookmark's legend! ", caught);
				Info.display("Error", "Unable to edit the bookmark's legend.");
			}
			@Override
			public void onSuccess(Boolean result) {
				GWT.log("Successfully edited bookmark's legend! ");
				Info.display("Succes", "Successfully edited bookmark's legend.");
				refresh();
				System.out.println("New Legend: "+b.getLegend());
				ftable.setWidget(0, 0, getHtmlLegend());					
			}
		});			
	}

	public void refresh() {
		
		bookmarkService.queryBookmark(b.getKey(),new AsyncCallback<Bookmark>() {
			@Override
			public void onFailure(Throwable caught) {
				GWT.log("Unable to load the bookmark! ", caught);
				Info.display("Error", "Unable to load the bookmarks.");
			}
			@Override
			public void onSuccess(Bookmark result) {			
				b=result;
				System.out.println("refresh "+b.toString());
			}
		});
	}



}


