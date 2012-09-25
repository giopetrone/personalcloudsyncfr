package com.unito.tableplus.client.gui.windows;

import java.util.List;
import com.extjs.gxt.ui.client.Style.HorizontalAlignment;
import com.extjs.gxt.ui.client.Style.Orientation;
import com.extjs.gxt.ui.client.Style.Scroll;
import com.extjs.gxt.ui.client.event.ButtonEvent;
import com.extjs.gxt.ui.client.event.ComponentEvent;
import com.extjs.gxt.ui.client.event.KeyListener;
import com.extjs.gxt.ui.client.event.SelectionListener;
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
import com.extjs.gxt.ui.client.widget.form.Radio;
import com.extjs.gxt.ui.client.widget.form.RadioGroup;
import com.extjs.gxt.ui.client.widget.form.TextArea;
import com.extjs.gxt.ui.client.widget.layout.RowData;
import com.extjs.gxt.ui.client.widget.layout.RowLayout;
import com.extjs.gxt.ui.client.widget.tips.ToolTipConfig;
import com.google.gwt.core.client.GWT;
import com.google.gwt.event.dom.client.ChangeEvent;
import com.google.gwt.event.dom.client.ChangeHandler;
import com.google.gwt.user.client.Window;
import com.google.gwt.user.client.rpc.AsyncCallback;
import com.google.gwt.user.client.ui.FlexTable;
import com.google.gwt.user.client.ui.ListBox;
import com.google.gwt.user.client.ui.PopupPanel;
import com.google.gwt.user.client.ui.Widget;
import com.unito.tableplus.client.TablePlus;
import com.unito.tableplus.client.services.BookmarkService;
import com.unito.tableplus.client.services.BookmarkServiceAsync;
import com.unito.tableplus.client.services.ServiceFactory;
import com.unito.tableplus.client.services.TableServiceAsync;
import com.unito.tableplus.shared.model.Bookmark;
import com.unito.tableplus.shared.model.Comment;
import com.unito.tableplus.shared.model.Table;
import com.unito.tableplus.shared.model.VisibilityType;
  
public class BookmarkWindow extends WindowPlus {
	
	private final BookmarkServiceAsync bookmarkService = GWT.create(BookmarkService.class);
	private final TableServiceAsync tableService = ServiceFactory.getTableServiceInstance();
	
	private Table table;
	private LayoutContainer mainContainer;
	private HtmlContainer historyContainer;
	private TextArea inputArea;	
	private Bookmark resource;
	private String message ="<div align=\"left\"><br><b>Comments :<br><br></b></div>";	
	private Radio radio = new Radio();  
	private Radio radio2 = new Radio();
	private FlexTable ftable = new FlexTable();
	private HorizontalPanel hpp= new HorizontalPanel();
	private HorizontalPanel hpTag= new HorizontalPanel();  
	private HorizontalPanel hpAnnotation= new HorizontalPanel();  
	private HorizontalPanel hpTag2;  
	private HorizontalPanel hpAnnotation2;  
	private Button saveTag = new Button("Save");
	private Button cancelTag = new Button("Cancel");
	private Button saveLegend = new Button("Save");
	private Button cancelLegend = new Button("Cancel");
	private Button saveAnnotation = new Button("Save");
	private Button cancelAnnotation = new Button("Cancel");
	private ListBox listTag;
	private ListBox listAnnotation;
	private TextArea inputAnnotation;
	private Button editLegendButton=new Button("Edit");
	private Button removeTag=new Button("Remove");
	private Button addTag=new Button("Add");
	private Button removeAnnotation=new Button("Remove");
	private Button addAnnotation=new Button("Add");
	private TextArea inputTag = new TextArea();
	
	public BookmarkWindow(final Table table){
	
	//public BookmarkWindow(final Table table, Bookmark resource){
		super();
		this.table = table;
		//this.resource=resource;
		setSize(590,500);
		
		setLayout(new RowLayout(Orientation.VERTICAL));
				
		mainContainer = new LayoutContainer();
		mainContainer.setScrollMode(Scroll.AUTO);			

		getObject();
		//System.out.println("creata finestra!!!"+resource.toString());

	}
	
	private void getObject() {
		//provvisorio: prendo il primo segnalibro salvato, nella futura implementazione 
		//verra preso come parametro al doppio click sull'elemento della BookmarkWindowsList
		tableService.getTableBookmark(table.getKey(),new AsyncCallback<List<Bookmark>>() {
			@Override
			public void onFailure(Throwable caught) {
				GWT.log("Unable to load the bookmark! ", caught);
				Info.display("Error", "Unable to load the bookmarks.");
				unmask();
			}
			@Override
			public void onSuccess(List<Bookmark> result) {			
				resource=result.get(0);
		//fine parte provvisoria	
				refresh();
				setTitle();
				setFrame();
				createFlexTable();
				createCommentPanel();
				radioButton();
				createInputArea();
				createButtonPanel();
			}
		});
	}
	
	private void setTitle() {
		//title	
		String objectName=resource.getClass().getName().substring(33);
		setHeading(objectName+": "+resource.getTitle());
		Html title= new Html("<div align=\"center\"><b>\nThe "+objectName
				+" <u>"+resource.getTitle() +"</u> has been shared on this table\n\n</b></div> ");
		title.setHeight(30);
		title.setStyleAttribute("padding-top", "7px");
		add(title);
	} 
	
	private void setFrame() {
		//frame anteprima				
		LayoutContainer preview=  new LayoutContainer();
		preview.add(hpp);
		hpp.setStyleAttribute("padding-left", "7px");
		hpp.add( new Html("<iframe src='"+resource.getUrl()+" width='300'; height='223'<p><a href='"+resource.getUrl()+"'>"+resource.getTitle()+"</a></p></iframe>"));
		add(preview);
	}
	
	private void createFlexTable() {
		ftable.insertRow(0);
		ftable.insertRow(1);
		ftable.insertRow(2);
		ftable.addCell(0);
		ftable.addCell(0);
		ftable.addCell(1);
		ftable.addCell(1);
		ftable.addCell(2);
		ftable.addCell(2);
		
		hpp.add(ftable);
		ftable.setHeight("223px");
		
		ftable.getCellFormatter().addStyleName(0, 0,"FlexTable-text0");		
		ftable.getCellFormatter().addStyleName(0, 1,"FlexTable-button0");
		
		ftable.getCellFormatter().addStyleName(1, 0,"FlexTable-text1");
		ftable.getCellFormatter().addStyleName(1, 1,"FlexTable-button1");
		
		ftable.getCellFormatter().addStyleName(2, 0,"FlexTable-text2");
		ftable.getCellFormatter().addStyleName(2, 1,"FlexTable-button2");
//legend				
		ftable.setWidget(0, 0, getHtmlLegend());
//edit legend				
		
		editLegendButton.setWidth(65);
		editLegendButton.setStyleAttribute("padding-left", "10px");
		editLegendButton.setToolTip(new ToolTipConfig("Edit the bookmark's legend"));
		editLegendButton.setIcon(IconHelper.createStyle("edit"));			
		ftable.setWidget(0, 1, editLegendButton);
				
		editLegendButton.addSelectionListener(new SelectionListener<ButtonEvent>() {
			public void componentSelected(ButtonEvent ce) {
				editLegendButton.disable();
				ftable.setWidget(0, 0, setFormLegend());
			}
		});					
//tag				
		ftable.setWidget(1, 0, getTag());
//add tag		
		VerticalPanel vp= new VerticalPanel();
		addTag.setWidth(65);
		addTag.setToolTip(new ToolTipConfig("Add a new tag to bookmark"));
		addTag.setIcon(IconHelper.createStyle("addTag"));		
		vp.setStyleAttribute("padding-left", "10px");

		vp.add(addTag);
		addTag.setStyleAttribute("padding-bottom", "5px");
		addTag.addSelectionListener(new SelectionListener<ButtonEvent>() {
			public void componentSelected(ButtonEvent ce) {
				addTag.disable();
				removeTag.disable();
				ftable.setWidget(1, 0, setFormTag());
			}
		});		
	
//remove tag				
		removeTag.setWidth(65);
		removeTag.setToolTip(new ToolTipConfig("Remove a tag from bookmark"));
		removeTag.setIcon(IconHelper.createStyle("removeTag"));		
		vp.add(removeTag);
		ftable.setWidget(1, 1,vp);	
		if(resource.getTag().size()==0) removeTag.disable();
		removeTag.addSelectionListener(new SelectionListener<ButtonEvent>() {
			public void componentSelected(ButtonEvent ce) {
				addTag.disable();
				removeTag.disable();
				ftable.setWidget(1, 0, setFormRemoveTag());
			}
		});	
//annotation				
		ftable.setWidget(2, 0, getAnnotation());
//add annotation		
		VerticalPanel vp2= new VerticalPanel();
		vp2.setStyleAttribute("padding-left", "10px");

		addAnnotation.setWidth(65);
		addAnnotation.setToolTip(new ToolTipConfig("Add annotation to bookmark"));
		addAnnotation.setIcon(IconHelper.createStyle("addTag"));	
		addAnnotation.setStyleAttribute("padding-bottom", "5px");

		vp2.add(addAnnotation);
		addAnnotation.addSelectionListener(new SelectionListener<ButtonEvent>() {
			public void componentSelected(ButtonEvent ce) {
				addAnnotation.disable();
				removeAnnotation.disable();
				ftable.setWidget(2, 0, setFormAnnotation());
			}
		});		
			
//remove annotation			
		removeAnnotation.setWidth(65);
		if(resource.getAnnotation().size()==0) removeAnnotation.disable();
		removeAnnotation.setToolTip(new ToolTipConfig("Remove annotation's bookmark"));
		removeAnnotation.setIcon(IconHelper.createStyle("removeTag"));		
		vp2.add(removeAnnotation);
		ftable.setWidget(2, 1,vp2);	
		removeAnnotation.addSelectionListener(new SelectionListener<ButtonEvent>() {
			public void componentSelected(ButtonEvent ce) {
				addAnnotation.disable();
				removeAnnotation.disable();
				ftable.setWidget(2, 0, setFormRemoveAnnotation());
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
		add(mainContainer, new RowData(1, 1, new Margins(4)));
		HorizontalPanel hp= new HorizontalPanel();
		mainContainer.add(hp);
		loadComments();	
	}
	
	private void radioButton() {
		//radio button
		HorizontalPanel fp= new HorizontalPanel();
		
		radio.setSize(55, 5);
		radio.setBoxLabel("Public");  
		radio.setValue(true);  

		radio2.setBoxLabel("Private");  
		radio2.setSize(55, 5);
		RadioGroup radioGroup = new RadioGroup();  
		Label label=new Label("New Comment Visibility: ");  
		label.setStyleAttribute("font-size", "10pt");
		label.setStyleAttribute("padding-right", "10px");
		
		radioGroup.setStyleAttribute("padding-top", "5px");
		
		radioGroup.add(radio);  
		radioGroup.add(radio2);
		fp.add(label);
		fp.add(radioGroup);
		
		add(fp, new RowData(1, -1, new Margins(10)));
	}

	private void createInputArea() {
		//input area			    
		inputArea = new TextArea();
		inputArea.setEmptyText("Leave a comment");
		inputArea.setHeight(40);
		inputArea.setWidth(width);
		add(inputArea, new RowData(1, -1, new Margins(4)));
		inputArea.addKeyListener(new KeyListener() {
			@Override
			public void componentKeyPress(ComponentEvent event) {
				if (event.getKeyCode() == 13) {
					addComment();
				}
			}
		});	
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
				Window.open(resource.getUrl(), "", "left=100,top=100,width=600,height=400,menubar,toolbar,resizable");
			}
		});	
		//bottone commento
		Button commentButton = new Button("Comments");
		commentButton.setToolTip(new ToolTipConfig("View and edit comments"));
		commentButton.setIcon(IconHelper.createStyle("comment"));						 			    
							    
		commentButton.addSelectionListener(new SelectionListener<ButtonEvent>(){
			public void componentSelected(ButtonEvent ce) {
		   	  	final CommentsPopup popup = new CommentsPopup(resource, table);
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
		hpButton.setSpacing(6);
		hpButton.add(commentButton);
		hpButton.add(refreshButton);
		hpButton.add(goButton);
		hpButton.add(closeButton);	

		add(hpButton);
		
	}

	private Widget getHtmlLegend() {
		return new Html("<div style=\"padding-left:10px;\"><b>Legend:</b>" +
				"<div style=\"padding-top:5px;padding-bottom:5px\">"+resource.getLegend()+"</div></div>");
	}

	private void addComment() {
		if (inputArea.getValue() != null){
			Comment c;
			if (radio.getValue()){
				c = new Comment(inputArea.getValue(), TablePlus.getUser().getEmail());		
			}
			else {
				c = new Comment(inputArea.getValue(), TablePlus.getUser().getEmail(), VisibilityType.PRIVATE);	
			}
			bookmarkService.addComment(resource.getKey(), c,new AsyncCallback<Boolean>() {
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
		bookmarkService.getComments(resource.getKey(),new AsyncCallback<List<Comment>>() {
			
			@Override
			public void onFailure(Throwable caught) {
				GWT.log("Unable to load comments of bookmarks: "+ resource.getTitle(), caught);
				Info.display("Error", "Unable to load comments.");
				unmask();
			}
			@Override
			public void onSuccess(List<Comment> result) {
				if (result.size()>0) {
				for(Comment c: result){
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
				message ="<div align=\"left\"><br><b>Comments :<br><br></b></div>";
				}
				else {
					message="<div align=\"left\"><br><b>There are no comments for the bookmark!</b><br></div>";
					historyContainer.add(new Label(message+"<br>"), "");
				}
				unmask();
			}
		});					
	}	
	
	public LayoutContainer setFormLegend(){
		final LayoutContainer panel = new LayoutContainer();
		panel.setStyleAttribute("padding-left", "10px");
		Label label=new Label("Insert the new legend:");
		label.setStyleAttribute("padding-bottom", "5px");
		panel.add(label);	
		final TextArea legend = new TextArea();
		legend.setHeight(35);
		legend.setWidth(150);
		legend.setValue(resource.getLegend());
		legend.setPreventScrollbars(true);
		legend.setAllowBlank(false);
		legend.setStyleAttribute("padding-top", "5px");
		legend.setStyleAttribute("padding-bottom", "5px");
		panel.add(legend);	
		
	    HorizontalPanel hp= new HorizontalPanel();  
	    hp.setStyleAttribute("padding-top", "10px");
	    hp.setHorizontalAlign(HorizontalAlignment.LEFT);
		saveLegend.setHeight(20);
		hp.add(saveLegend);
		saveLegend.setStyleAttribute("padding-right", "10px");
		
		cancelLegend.setHeight(20);
		hp.add(cancelLegend);
		cancelLegend.addSelectionListener(new SelectionListener<ButtonEvent>() {
			@Override
			public void componentSelected(ButtonEvent ce) {
				ftable.setWidget(0, 0, getHtmlLegend());
				editLegendButton.enable();
			}
		});
		saveLegend.addSelectionListener(new SelectionListener<ButtonEvent>() {
			@Override
			public void componentSelected(ButtonEvent ce) {
				editLegend(legend.getValue());
				editLegendButton.enable();
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
		
	    HorizontalPanel hp1= new HorizontalPanel();  
	    panel.add(hp1);
	    hp1.setStyleAttribute("padding-top", "5px");

		inputTag = new TextArea();
		inputTag.setHeight(20);
		
		inputTag.setPreventScrollbars(true);
		inputTag.setAllowBlank(false);
		inputTag.setStyleAttribute("padding-rigth", "10px");
		
		
		if(resource.getTag().size()>0) {
			inputTag.setValue(resource.getTag().get(0));
			listTag = new ListBox();
			inputTag.setWidth(60);
			for (String tag : resource.getTag()) {
				listTag.addItem(tag);
			}
			listTag.setHeight("20px");
			listTag.setWidth("80px");
			listTag.addChangeHandler(new ChangeHandler(){
				@Override
				public void onChange(ChangeEvent event) {
					inputTag.setValue(listTag.getItemText(listTag.getSelectedIndex()));
				}
			 });
			hp1.add(listTag);
		}	
		else inputTag.setWidth(140);
		hp1.add(inputTag);	
		
				
	    hpTag.setStyleAttribute("padding-top", "7px");
	    hpTag.setHorizontalAlign(HorizontalAlignment.LEFT);
		
		hpTag.add(saveTag);
		saveTag.setStyleAttribute("padding-right", "10px");
		saveTag.setHeight(20);
		
		cancelTag.setHeight(20);
		hpTag.add(cancelTag);

		cancelTag.addSelectionListener(new SelectionListener<ButtonEvent>() {
			@Override
			public void componentSelected(ButtonEvent ce) {
				ftable.setWidget(1, 0, getTag());
				addTag.enable();
				removeTag.enable();
			}
		});
		saveTag.addSelectionListener(new SelectionListener<ButtonEvent>() {
			@Override
			public void componentSelected(ButtonEvent ce) {
				addTag(inputTag.getValue().toUpperCase());
				addTag.enable();
				removeTag.enable();
			}
		});
		panel.add(hpTag);
		return panel;
	}
	public LayoutContainer setFormRemoveTag(){
		final LayoutContainer panel = new LayoutContainer();
		panel.setStyleAttribute("padding-left", "10px");
		Label label=new Label("Remove a tag to bookmark:");		
		label.setStyleAttribute("padding-bottom", "5px");
		panel.add(label);	
		
	    HorizontalPanel hp1= new HorizontalPanel();  
	    panel.add(hp1);
	    hp1.setStyleAttribute("padding-top", "5px");
	    
	    listTag = new ListBox();
		for (String tag : resource.getTag()) {
			listTag.addItem(tag);
		}
		
		listTag.setHeight("20px");
		listTag.setWidth("120px");

		hp1.add(listTag);
		hpTag2= new HorizontalPanel();	
	    hpTag2.setStyleAttribute("padding-top", "5px");
	    hpTag2.setHorizontalAlign(HorizontalAlignment.LEFT);
		
		Button remove=new Button("Remove");
		remove.setStyleAttribute("padding-right", "10px");
		remove.setHeight(20);
		hpTag2.add(remove);
		
		cancelTag.setHeight(20);
		hpTag2.add(cancelTag);

		cancelTag.addSelectionListener(new SelectionListener<ButtonEvent>() {
			@Override
			public void componentSelected(ButtonEvent ce) {
				addTag.enable();
				if(resource.getTag().size()>0) removeTag.enable();
				ftable.setWidget(1, 0, getTag());
			}
		});
		remove.addSelectionListener(new SelectionListener<ButtonEvent>() {
			@Override
			public void componentSelected(ButtonEvent ce) {
				addTag.enable();
				if(resource.getTag().size()>0)  removeTag.enable();
				removeTag(listTag.getSelectedIndex());
			}
		});
		panel.add(hpTag2);
		return panel;
	}
	
	public Widget getTag() {
		return new Html("<div style=\"padding-left:10px;\"><b>Tag:</b>" +
				"<div style=\"padding-top:5px;\">"+resource.getTagString()+"</div></div>");
	}

	public void addTag(final String tag) {
		bookmarkService.addTag(resource.getKey(),tag, new AsyncCallback<Boolean>() {
			@Override
			public void onFailure(Throwable caught) {
				GWT.log("Unable to add tag! ", caught);
				Info.display("Error", "Unable to add the tag.");
				unmask();
			}
			@Override
			public void onSuccess(Boolean result) {
				GWT.log("Successfully added tag! ");
				Info.display("Success", "Successfully added tag");
				resource.addTag(tag);
				ftable.setWidget(1, 0, getTag());
			}
		});		
		removeTag.enable();
	}

	public void removeTag(final int tag) {
		bookmarkService.removeTag(resource.getKey(),tag, new AsyncCallback<Boolean>() {
			@Override
			public void onFailure(Throwable caught) {
				GWT.log("Unable to remove tag! ", caught);
				Info.display("Error", "Unable to remove the tag.");
				unmask();
			}
			@Override
			public void onSuccess(Boolean result) {
				GWT.log("Successfully removed tag! ");
				Info.display("Success", "Successfully removed tag");
				resource.removeTag(tag);
				ftable.setWidget(1, 0, getTag());
			}
		});		
		refresh();
	}
	
	public void editLegend(final String value) {
		bookmarkService.editLegend(resource.getKey(),value, new AsyncCallback<Boolean>() {
			@Override
			public void onFailure(Throwable caught) {
				GWT.log("Unable to edit the bookmark's legend! ", caught);
				Info.display("Error", "Unable to edit the bookmark's legend.");
				unmask();
			}
			@Override
			public void onSuccess(Boolean result) {
				GWT.log("Successfully edited bookmark's legend! ");
				Info.display("Succes", "Successfully edited bookmark's legend.");
				resource.setLegend(value);
				ftable.setWidget(0, 0, getHtmlLegend());
			}
		});			
	}

	public void refresh() {
		bookmarkService.queryBookmark(resource.getKey(),new AsyncCallback<Bookmark>() {
			@Override
			public void onFailure(Throwable caught) {
				GWT.log("Unable to load the bookmark! ", caught);
				Info.display("Error", "Unable to load the bookmarks.");
			}
			@Override
			public void onSuccess(Bookmark result) {			
				resource=result;
				ftable.setWidget(0, 0, getHtmlLegend());
				ftable.setWidget(1, 0, getTag());
				loadComments();	
			}
		});
	}
	
	public Widget getAnnotation() {
		return new Html("<div style=\"padding-left:10px;\"><b>Annotation:</b>" +
				"<div style=\"padding-top:5px;\">"+resource.getAnnotationString()+"</div></div>");
	}
	
	public LayoutContainer setFormAnnotation(){
		final LayoutContainer panel = new LayoutContainer();
		panel.setStyleAttribute("padding-left", "10px");
		Label label=new Label("Add annotation to bookmark:");		
		label.setStyleAttribute("padding-bottom", "5px");
		panel.add(label);	
		
		inputAnnotation = new TextArea();
		inputAnnotation.setHeight(20);
		inputAnnotation.setWidth(150);
		inputAnnotation.setPreventScrollbars(true);
		inputAnnotation.setAllowBlank(false);
		inputAnnotation.setStyleAttribute("padding-rigth", "10px");
		inputAnnotation.setStyleAttribute("padding-top", "5px");
		panel.add(inputAnnotation);
				
	    hpAnnotation.setHorizontalAlign(HorizontalAlignment.LEFT);
		
	    hpAnnotation.add(saveAnnotation);
	    hpAnnotation.setStyleAttribute("padding-top", "10px");
		saveAnnotation.setStyleAttribute("padding-right", "10px");
		saveAnnotation.setHeight(20);
		
		cancelAnnotation.setHeight(20);
		hpAnnotation.add(cancelAnnotation);

		cancelAnnotation.addSelectionListener(new SelectionListener<ButtonEvent>() {
			@Override
			public void componentSelected(ButtonEvent ce) {
				ftable.setWidget(2, 0, getAnnotation());
				addAnnotation.enable();
				if(resource.getAnnotation().size()>0) removeAnnotation.enable();
			}
		});
		saveAnnotation.addSelectionListener(new SelectionListener<ButtonEvent>() {
			@Override
			public void componentSelected(ButtonEvent ce) {
				addAnnotation(inputAnnotation.getValue());
				addAnnotation.enable();
				if(resource.getAnnotation().size()>0)  removeAnnotation.enable();
			}
		});
		panel.add(hpAnnotation);
		return panel;
	}

	public void addAnnotation(final String annotation) {
		bookmarkService.addAnnotation(resource.getKey(),annotation, new AsyncCallback<Boolean>() {
			@Override
			public void onFailure(Throwable caught) {
				GWT.log("Unable to add annotation! ", caught);
				Info.display("Error", "Unable to add annotation.");
				unmask();
			}
			@Override
			public void onSuccess(Boolean result) {
				GWT.log("Successfully added tag! ");
				Info.display("Success", "Successfully added annotation");
				resource.addAnnotation(annotation);
				ftable.setWidget(2, 0, getAnnotation());
			}
		});		
		removeAnnotation.enable();
	}

	public LayoutContainer setFormRemoveAnnotation(){
		final LayoutContainer panel = new LayoutContainer();
		panel.setStyleAttribute("padding-left", "10px");
		Label label=new Label("Remove annotation:");		
		label.setStyleAttribute("padding-bottom", "5px");
		panel.add(label);	
		
	    HorizontalPanel hp1= new HorizontalPanel();  
	    panel.add(hp1);
	    hp1.setStyleAttribute("padding-top", "5px");
	    
	    listAnnotation = new ListBox();
		for (String annotation : resource.getAnnotation()) {
			listAnnotation.addItem(annotation);
		}
		listAnnotation.setHeight("20px");
		listAnnotation.setWidth("120px");
		hp1.add(listAnnotation);
		hpAnnotation2= new HorizontalPanel();	
	    hpAnnotation2.setStyleAttribute("padding-top", "5px");
	    hpAnnotation2.setHorizontalAlign(HorizontalAlignment.LEFT);
		Button removeButton= new Button("Remove");
		hpAnnotation2.add(removeButton);
		
		removeButton.setStyleAttribute("padding-right", "10px");
		removeButton.setHeight(20);
		
		cancelAnnotation.setHeight(20);
		hpAnnotation2.add(cancelAnnotation);

		cancelAnnotation.addSelectionListener(new SelectionListener<ButtonEvent>() {
			@Override
			public void componentSelected(ButtonEvent ce) {
				addAnnotation.enable();
				if(resource.getAnnotation().size()>0) removeAnnotation.enable();
				ftable.setWidget(2, 0, getAnnotation());
			}
		});
		removeButton.addSelectionListener(new SelectionListener<ButtonEvent>() {
			@Override
			public void componentSelected(ButtonEvent ce) {
				addAnnotation.enable();
				if(resource.getAnnotation().size()>0) removeAnnotation.enable();
				removeAnnotation(listAnnotation.getSelectedIndex());
			}
		});
		panel.add(hpAnnotation2);
		return panel;
	}

	public void removeAnnotation(final int annotation) {
		bookmarkService.removeAnnotation(resource.getKey(),annotation, new AsyncCallback<Boolean>() {
			@Override
			public void onFailure(Throwable caught) {
				GWT.log("Unable to remove annotation! ", caught);
				Info.display("Error", "Unable to remove annotation.");
				unmask();
			}
			@Override
			public void onSuccess(Boolean result) {
				GWT.log("Successfully removed tag! ");
				Info.display("Success", "Successfully removed annotation");
				resource.removeAnnotation(annotation);
				ftable.setWidget(2, 0, getAnnotation());
			}
		});		
		if (resource.getAnnotation().size()==0) removeAnnotation.disable();
	}
	
}


