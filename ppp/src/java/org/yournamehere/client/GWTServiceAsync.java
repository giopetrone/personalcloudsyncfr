/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package org.yournamehere.client;

import com.google.gwt.user.client.rpc.AsyncCallback;
import java.util.ArrayList;

/**
 *
 * @author marino
 */
public interface GWTServiceAsync {
    public void myMethod(String s, AsyncCallback<String> callback);
     public void  myMethod1(ArrayList<String> s , AsyncCallback<ArrayList<String>> callback);
}
