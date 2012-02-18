package com.unito.tableplus.client.gui.windows;

import com.extjs.gxt.ui.client.event.Events;
import com.extjs.gxt.ui.client.event.Listener;
import com.extjs.gxt.ui.client.event.WindowEvent;
import com.extjs.gxt.ui.client.util.Point;
import com.extjs.gxt.ui.client.widget.Window;

public class WindowPlus extends Window {

	public WindowPlus() {
		super();

		setMinimizable(true);
		setMaximizable(true);
		setSize(400, 300);

		this.addListener(Events.Hide, new Listener<WindowEvent>(){
			public void handleEvent(WindowEvent be) {
				System.out.println("CHE NE SO 2");
				if(!closedBySwitch){
					setPreviousPosition(getPosition(false));
					wasOpen = false;
				}
				setClosedBySwitch(false);
			}
		});
		
		
	}
	
	public void _hide(){
		super.hide();
	}

	boolean closedBySwitch=false;
	boolean wasOpen = false;
	private Point previousPosition;
	int previousWidth = 0;
	int previousHeight = 0;

	public boolean getWasOpen() {
		return wasOpen;
	}

	public void setWasOpen(boolean wasOpen) {
		this.wasOpen = wasOpen;
	}

	

	public int getPreviousWidth() {
		return previousWidth;
	}

	public void setPreviousWidth(int previousWidth) {
		this.previousWidth = previousWidth;
	}

	public int getPreviousHeight() {
		return previousHeight;
	}

	public void setPreviousHeight(int previousHeight) {
		this.previousHeight = previousHeight;
	}

	public boolean isClosedBySwitch() {
		return closedBySwitch;
	}

	public void setClosedBySwitch(boolean closedBySwitch) {
		this.closedBySwitch = closedBySwitch;
	}

	public Point getPreviousPosition() {
		return previousPosition;
	}

	public void setPreviousPosition(Point previousPosition) {
		this.previousPosition = previousPosition;
	}
	
	

}
