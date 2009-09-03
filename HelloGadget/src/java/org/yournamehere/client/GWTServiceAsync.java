/*
 * GWTServiceAsync.java
 *
 * Created on April 9, 2009, 2:08 PM
 *
 * To change this template, choose Tools | Template Manager
 * and open the template in the editor.
 */

package org.yournamehere.client;
import com.google.gwt.user.client.rpc.AsyncCallback;


/**
 *
 * @author giovanna
 */
public interface GWTServiceAsync {
    public void myMethod(String s, AsyncCallback callback);
}
