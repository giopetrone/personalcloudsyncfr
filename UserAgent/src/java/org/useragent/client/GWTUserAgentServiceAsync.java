/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package org.useragent.client;

import com.google.gwt.user.client.rpc.AsyncCallback;

/**
 *
 * @author giovanna
 */
public interface GWTUserAgentServiceAsync {
    public void myMethod(String s, AsyncCallback<String> callback);
    public void authenticate(String s, AsyncCallback callback);
    public void addEvents(AsyncCallback callback);
}
