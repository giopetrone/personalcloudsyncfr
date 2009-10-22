/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package org.docwatcher.client;

import com.google.gwt.user.client.rpc.AsyncCallback;

/**
 *
 * @author giovanna
 */
public interface GWTDocWatcherServiceAsync {
    public void myMethod(String s, AsyncCallback<String> callback);
    public void authenticate(String googleId, AsyncCallback<String> callback);
    public void startDocWatcher(AsyncCallback<String> callback);
}
