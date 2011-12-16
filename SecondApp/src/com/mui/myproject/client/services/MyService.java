package com.mui.myproject.client.services;

import com.google.gwt.user.client.rpc.RemoteService;
import com.google.gwt.user.client.rpc.RemoteServiceRelativePath;

@RemoteServiceRelativePath("myService")
public interface MyService extends RemoteService{
	public String myMethod(String s);

}
