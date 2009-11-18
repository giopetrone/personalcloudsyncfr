/*
 * GWTService.java
 *
 * Created on June 29, 2009, 11:42 AM
 *
 * To change this template, choose Tools | Template Manager
 * and open the template in the editor.
 */
package org.notificationMgr.client;

import appsusersevents.client.EventDescription;
import com.google.gwt.user.client.rpc.RemoteService;
import java.util.ArrayList;
import java.util.HashMap;

/**
 *
 * @author giovanna
 */
public interface GWTService extends RemoteService {

    public String myMethod(String s);

    public String myMethod2(String s);

    public String sendEventToGiga(String questionId, String answer, String me);
    //  public String subscribeTo(String s);

    public EventDescription[] getEvents(String userName);

    public void putEvents(EventDescription[] events, String userName);

//    public boolean validateUser(String name, String pwd);

    public String authenticate(String s);

   // public HashMap<String, ArrayList<EventDescription>> processEvents(String userName);
    public HashMap<String, ArrayList<EventDescription>> addEvents(String userName);
}
