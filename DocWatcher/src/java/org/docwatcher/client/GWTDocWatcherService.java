/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package org.docwatcher.client;

import com.google.gwt.user.client.rpc.RemoteService;
import com.google.gwt.user.client.rpc.RemoteServiceRelativePath;

/**
 *
 * @author giovanna
 */
@RemoteServiceRelativePath("gwtdocwatcherservice")
public interface GWTDocWatcherService extends RemoteService {

    public String myMethod(String s);

    public String authenticate(String googleId);

    public void startDocWatcher();
}
