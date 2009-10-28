/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package org.useragent.client;

import com.google.gwt.user.client.rpc.RemoteService;
import com.google.gwt.user.client.rpc.RemoteServiceRelativePath;

/**
 *
 * @author giovanna
 */
@RemoteServiceRelativePath("gwtuseragentservice")
public interface GWTUserAgentService extends RemoteService {
    public String myMethod(String s);
     public String authenticate(String s);
      public void addEvents();
}
