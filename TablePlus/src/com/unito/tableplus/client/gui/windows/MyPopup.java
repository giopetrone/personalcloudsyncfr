package com.unito.tableplus.client.gui.windows;

import com.extjs.gxt.ui.client.event.ButtonEvent;
import com.extjs.gxt.ui.client.event.SelectionListener;
import com.extjs.gxt.ui.client.util.IconHelper;
import com.extjs.gxt.ui.client.widget.HorizontalPanel;
import com.extjs.gxt.ui.client.widget.Html;
import com.extjs.gxt.ui.client.widget.VerticalPanel;
import com.extjs.gxt.ui.client.widget.button.Button;
import com.extjs.gxt.ui.client.widget.tips.ToolTipConfig;
import com.google.gwt.core.client.GWT;
import com.google.gwt.user.client.Window;
import com.google.gwt.user.client.ui.HTML;
import com.google.gwt.user.client.ui.Label;
import com.google.gwt.user.client.ui.PopupPanel;
import com.unito.tableplus.client.services.BookmarkService;
import com.unito.tableplus.client.services.BookmarkServiceAsync;
import com.unito.tableplus.shared.model.Bookmark;
import com.unito.tableplus.shared.model.Table;

public class MyPopup extends PopupPanel {
	VerticalPanel panelTitle;
	// crea il servizio per segnalibri
	protected final BookmarkServiceAsync bookmarkService = GWT.create(BookmarkService.class);
		Bookmark b;
		String title;
		
	    public MyPopup(final Bookmark b, final Table table) {
		      super(true);
		      this.b=b;
		      
		      //pannello principale
		      VerticalPanel panel = new VerticalPanel();
				
		      //titolo
	          title=b.getTitle();
	          panelTitle = new VerticalPanel();
			  panelTitle.add(new HTML("Title: "+title));
			  panel.add(panelTitle);
			  
			  panelTitle.add( new Html("<iframe src='"+b.getUrl()+"<p><a href='"+b.getUrl()+"'>"+b.getTitle()+"</a></p></iframe>"));
			  

			  panelTitle.setStyleName("popup-title");
		      //contenuto (descrizione)
		      VerticalPanel panelContent = new VerticalPanel();
		      panelContent.add(new HTML("\nLegend: "+b.getLegend()));
		      panelContent.add(new Label(b.getUrl()));
		      panel.add(panelContent);
		      panelContent.setStyleName("popup-panel");
		      //footer
		      HorizontalPanel panelFooter = new HorizontalPanel();
		      Button closeButton = new Button("Close");
		      closeButton.addSelectionListener(new SelectionListener<ButtonEvent>() {
		    	  public void componentSelected(ButtonEvent ce) {
		              hide();
		          }
		      });
		      Button goButton = new Button("Go");
		      goButton.setToolTip(new ToolTipConfig("View bookmark"));
		      goButton.setIcon(IconHelper.createStyle("go"));
		      goButton.addSelectionListener(new SelectionListener<ButtonEvent>() {
		    	  public void componentSelected(ButtonEvent ce) {
		    		  Window.open(b.getUrl(), "", "left=100,top=100,width=600,height=400,menubar,toolbar,resizable");
		              hide();
		             
		          }
		      });		      
		      panelFooter.add(goButton);
		      panelFooter.add(closeButton);


		      panel.add(panelFooter);
		      panelFooter.setStyleName("popup-footer");
		      setWidget(panel);
	    }

}

