package com.unito.tableplus.client.gui.windows;

import com.extjs.gxt.ui.client.widget.Html;
import com.extjs.gxt.ui.client.widget.layout.FillLayout;
import com.extjs.gxt.ui.client.widget.layout.FitLayout;

public class DocWindow extends WindowPlus{
	private String url=null;
	
	public DocWindow(String url){
		super();
		this.url=url;
		setHeading("DocWindow");
		setLayout(new FitLayout());
		this.add(new Html("<iframe src='"+url+"' width='100%' height='100%' style='border:0px'> " +
				"Sembra che il tuo browser non legga gli iFrame..." +
				"</iframe>"));
	}
	
	

}
