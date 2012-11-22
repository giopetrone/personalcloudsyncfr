package com.unito.tableplus.client.gui;

import com.extjs.gxt.ui.client.widget.MessageBox;
import com.extjs.gxt.ui.client.widget.ProgressBar;

public class ProgressBox{
	MessageBox box;
	String title, progressText;
	ProgressBar progressBar;
	double progress;
	
	public ProgressBox(String title, String msg, String progressText){
		this.box = MessageBox.progress(title, msg, progressText);
		this.title = box.getTitle();
		this.progressBar = box.getProgressBar();
		this.progressText = box.getProgressText();
		this.progress = 0;
		this.box.isModal();
	}
	
	public void show(){
		this.box.show();
	}
	
	public void updateContent(double progress, String msg){
		this.progress +=progress;
		this.box.updateProgress(progress, (int) progress + "% Complete");
		this.box.setMessage(msg);
		if(this.progress > 100){
			box.close();
		}
	}
}
