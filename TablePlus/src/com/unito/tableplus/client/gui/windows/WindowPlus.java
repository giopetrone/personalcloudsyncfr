package com.unito.tableplus.client.gui.windows;

import java.util.HashMap;
import java.util.Map;

import com.extjs.gxt.ui.client.event.ComponentEvent;
import com.extjs.gxt.ui.client.event.Events;
import com.extjs.gxt.ui.client.event.Listener;
import com.extjs.gxt.ui.client.util.Point;
import com.extjs.gxt.ui.client.widget.Window;
import com.unito.tableplus.client.TablePlus;

public abstract class WindowPlus extends Window {

	Map<Long, WindowState> statesMap;
	WindowState state;

	public WindowPlus() {
		super();
		setMinimizable(true);
		setMaximizable(true);
		setSize(400, 300);
		this.statesMap = new HashMap<Long, WindowState>();
		this.state = new WindowState();

//		this.addListener(Events.Hide, new Listener<ComponentEvent>() {
//			public void handleEvent(ComponentEvent ce) {
//				storeState();
//			}
//		});

	}

	private void storeState() {
		this.state.setHeight(this.getHeight());
		this.state.setWidth(this.getWidth());
		this.state.setPosition(this.getPosition(false));
		this.state.setVisible(this.isVisible());
		this.statesMap.put(TablePlus.getDesktop().getActiveTableKey(), state);
	}

	public void restoreState() {
		this.state = statesMap.get(TablePlus.getDesktop().getActiveTableKey());
		if (this.state != null) {
			this.setHeight(state.getHeight());
			this.setWidth(state.getWidth());
			this.setPosition(state.getPosition().x, state.getPosition().y);
			this.setVisible(state.isVisible());
		}
	}

	public abstract void updateContent();

	private class WindowState {
		private Point position;
		private boolean isVisible;
		int width, height;

		public Point getPosition() {
			return position;
		}

		public void setPosition(Point position) {
			this.position = position;
		}

		public boolean isVisible() {
			return isVisible;
		}

		public void setVisible(boolean isVisible) {
			this.isVisible = isVisible;
		}

		public int getWidth() {
			return width;
		}

		public void setWidth(int width) {
			this.width = width;
		}

		public int getHeight() {
			return height;
		}

		public void setHeight(int height) {
			this.height = height;
		}
	}
}
