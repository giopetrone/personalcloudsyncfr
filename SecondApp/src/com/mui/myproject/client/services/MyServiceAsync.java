package com.mui.myproject.client.services;

import com.google.gwt.user.client.rpc.AsyncCallback;

public interface MyServiceAsync {
	public void myMethod(String s, AsyncCallback<String> callback);

}
