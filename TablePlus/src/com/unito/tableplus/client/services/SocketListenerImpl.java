package com.unito.tableplus.client.services;


import com.google.gwt.appengine.channel.client.SocketError;
import com.google.gwt.appengine.channel.client.SocketListener;
import com.google.gwt.json.client.JSONArray;
import com.google.gwt.json.client.JSONObject;
import com.google.gwt.json.client.JSONParser;
import com.google.gwt.json.client.JSONString;
import com.google.gwt.json.client.JSONValue;
import com.google.gwt.user.client.Window;
import com.unito.tableplus.client.TablePlus;
import com.unito.tableplus.client.gui.TableUI;
import com.unito.tableplus.client.gui.windows.GroupChatWindow;

public class SocketListenerImpl implements SocketListener {
	
	final Object obj;

	public SocketListenerImpl(Object obj) {
		this.obj = obj;
	}

	@Override
	public void onOpen() {
		//Window.alert("Channel opened!");
		
	}

	@Override
	public void onMessage(String message) {
		//DO SOMETHING WITH THE OBJECT HERE
		//System.out.println("Sono "+TablePlus.user.getEmail()+", messaggio ricevuto:\n \""+message+"\"");
		
		//in realtà dovrei chiamare solo il tavolo a cui è stato
		//mandato il messaggio, nella send dovrei indicare, OLTRE 
		//ai mittenti, anche il gruppo/tavolo di destinazione
		
        JSONValue jv = JSONParser.parseStrict(message);
        JSONObject jo = jv.isObject();
        
        JSONValue jvSender = jo.get("sender");
        JSONValue jvContent = jo.get("content");
        JSONValue jvGroupKey = jo.get("groupKey");
        
        JSONArray jaSender = jvSender.isArray();
        JSONArray jaContent = jvContent.isArray();
        JSONArray jaGroupKey = jvGroupKey.isArray();
        
        jvSender = jaSender.get(0);
        jvContent = jaContent.get(0);
        jvGroupKey = jaGroupKey.get(0);
        
        JSONString jsSender = jvSender.isString();
        JSONString jsContent = jvContent.isString();
        JSONString jsGroupKey = jvGroupKey.isString();
        
        String sender = jsSender.stringValue();
        String content =  jsContent.stringValue();
        Long groupKey = Long.valueOf(jsGroupKey.stringValue());
        
        //Window.alert(sender+": "+content); 
        
        System.out.println(sender+" -- "+content+" -- "+groupKey);
		
		for(TableUI t:TablePlus.desktop.getGroupTables())
			if(t.groupKey.compareTo(groupKey)==0){
				System.out.println("TROVATO: "+groupKey);
				((GroupChatWindow)t.groupChatWindow).manageNewMessage(sender,content);
			}
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
