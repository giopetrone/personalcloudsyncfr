package com.unito.tableplus.client.gui.windows;

import java.util.List;
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
import com.google.gwt.user.client.Window;
import com.google.gwt.user.client.rpc.AsyncCallback;
import com.google.gwt.user.client.ui.PopupPanel;
import com.unito.tableplus.client.TablePlus;
import com.unito.tableplus.client.services.BookmarkService;
import com.unito.tableplus.client.services.BookmarkServiceAsync;
import com.unito.tableplus.client.services.ServiceFactory;
import com.unito.tableplus.client.services.TableServiceAsync;
import com.unito.tableplus.shared.model.Bookmark;
import com.unito.tableplus.shared.model.Comment;
import com.unito.tableplus.shared.model.Table;
import com.unito.tableplus.shared.model.VisibilityType;
import com.extjs.gxt.ui.client.core.Template;  
  
public class BookmarkWindow extends WindowPlus {
	
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
	
	public BookmarkWindow(final Table table){
		super();
		this.table = table;
		setSize(500,500);
		
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
				b=result.get(0);
				System.out.println(b.toString());
				b.addTagCategory("IT");
//----fine parte provvisoria	
				setHeading("Bookmark: "+b.getTitle());
				setLayout(new RowLayout(Orientation.VERTICAL));
				
				mainContainer = new LayoutContainer();
				mainContainer.setScrollMode(Scroll.AUTO);			
//title				
				Html title= new Html("<div align=\"center\"><b>\nThe bookmak <u>"+b.getTitle()+"</u> has been shared on this table\n\n</b></div> ");
				title.setHeight(30);
				mainContainer.add(title);
				
//frame anteprima				
				LayoutContainer preview=  new LayoutContainer();
				HorizontalPanel hpp= new HorizontalPanel();
				preview.add(hpp);
				hpp.add( new Html("<iframe src='"+b.getUrl()+" width='200'; height='200'<p><a href='"+b.getUrl()+"'>"+b.getTitle()+"</a></p></iframe>"));
				mainContainer.add(preview);
//legend				
				VerticalPanel vpp= new VerticalPanel();
				hpp.add(vpp);
				vpp.add(new Html("<div style=\"padding-left:10px;\"><b>Legend:</b>" +
						"<div style=\"padding-top:5px;padding-bottom:5px\">"+b.getLegend()+"</div></div>"));
//edit legend				
				Button editLegendButton=new Button("Edit");
				editLegendButton.setToolTip(new ToolTipConfig("Edit the bookmark's legend"));
				editLegendButton.setIcon(IconHelper.createStyle("edit"));			
				vpp.add(editLegendButton);
//tooltip edit legend	 
				ToolTipConfig config = new ToolTipConfig();  
			  
				config.setText("Edit the bookmark's legend");  
				config.setTitle("Edit legend");  
				config.setMouseOffset(new int[] {0, 0});  
				config.setAnchor("left");  
				config.setTemplate(new Template(getTemplate(GWT.getHostPageBaseURL())));  
				config.setCloseable(true);  
				config.setMaxWidth(415);  
				editLegendButton.setToolTip(config);  
			    
//tag
				vpp.add(new Html("<div style=\"padding-left:10px;padding-top:15px;\"><b>Tag:</b>" +
						"<div style=\"padding-top:5px;\">"+b.getTag()+"</div></div>"));
//add tag			
				Button addTag=new Button("Add");
				addTag.setToolTip(new ToolTipConfig("Add a new tag to bookmark"));
				addTag.setIcon(IconHelper.createStyle("addTag"));			
				vpp.add(addTag);				
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

//pannello bottoni				
				HorizontalPanel hpButton= new HorizontalPanel();
				hpButton.add(commentButton);
				hpButton.add(refreshButton);
				hpButton.add(goButton);
				hpButton.add(closeButton);	
				hpButton.add(hpv);
				add(hpButton);
				
				unmask();
				
			}
		});
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
				unmask();
			}
			@Override
			public void onSuccess(List<Comment> result) {
				
				for(Comment c: result){
					if(c.isPrivate()){
						//se i commenti sono stati fatti dallo stesso utente
						if (c.getAuthor().equals(TablePlus.getUser().getEmail())){
							message+="<div><div style=\"color: #191970;float:left;\">&lt<u>"+c.getAuthor()
									+"</u>&gt </div><div style=\"color:	#3D3D4C;float:left;\">"+c.getDateString()+"</div> - "
									+c.getComment()+"</div>";	
						}
						else 	message+="<div><div style=\"color: #191970;float:left;\">&lt"+c.getAuthor()
								+"&gt Commento Privato</div>";	
					}
					else message+="<div><div style=\"color: #191970;float:left;\">&lt"+c.getAuthor()
							+"&gt </div><div style=\"color:	#3D3D4C;float:left;\">"+c.getDateString()+"</div> - "
							+c.getComment()+"</div>";	
				}
				historyContainer.add(new Label(message+"<br>"), "");
				if (result.size()>0) message="<div align=\"left\"><br><b>Comments:<br><br></b></div>";	
				else message="<div align=\"left\"><b>There are no comments for the bookmark!</b><br></div>";	
				unmask();
			}
		});					
	}	
	
	
	  private native String getTemplate(String base) /*-{ 
	    var html = [ 
	    '<div><form style="margin: 0px 0px 5px 15px">', 	   
	    '<form>Insert the new legend: <input type="text" name="newLegend"></input><br></form>',
	    '<br>',
	    '</div>' ]; 
	    return html.join(""); 
	  }-*/;  
}


