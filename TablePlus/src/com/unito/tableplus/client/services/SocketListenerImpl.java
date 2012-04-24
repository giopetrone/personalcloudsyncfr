package com.unito.tableplus.client.services;


import com.google.gwt.appengine.channel.client.SocketError;
import com.google.gwt.appengine.channel.client.SocketListener;
import com.google.gwt.user.client.Window;

public class SocketListenerImpl implements SocketListener {
	
	final Object obj;

	public SocketListenerImpl(Object obj) {
		this.obj = obj;
	}

	@Override
	public void onOpen() {
		Window.alert("Channel opened!");
	}

	@Override
	public void onMessage(String message) {
		//DO SOMETHING WITH THE OBJECT HERE
	}

	@Override
	public void onError(SocketError error) {
		Window.alert("Error: " + error.getDescription());
	}

	@Override
	public void onClose() {
		Window.alert("Channel closed!");
	}

}
