package com.unito.tableplus.client.services;


import com.google.gwt.appengine.channel.client.Channel;
import com.google.gwt.appengine.channel.client.ChannelFactory.ChannelCreatedCallback;
import com.google.gwt.appengine.channel.client.SocketListener;

public class ChannelCreatedCallbackImpl implements ChannelCreatedCallback {
	
	final Object obj;
	
	public ChannelCreatedCallbackImpl(Object obj){
		this.obj = obj;
	}

	@Override
	public void onChannelCreated(Channel channel) {
		SocketListener socketlistener =  new SocketListenerImpl(this.obj);
		channel.open(socketlistener);
	}
}
