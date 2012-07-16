package com.unito.tableplus.client.services;


import com.google.gwt.appengine.channel.client.Channel;
import com.google.gwt.appengine.channel.client.ChannelFactory.ChannelCreatedCallback;
import com.google.gwt.appengine.channel.client.SocketListener;

public class ChannelCreatedCallbackImpl implements ChannelCreatedCallback {
        @Override
        public void onChannelCreated(Channel channel) {
                SocketListener messageListener =  new MessageListenerImpl();
                channel.open(messageListener);
        }
}
