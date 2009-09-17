/*
 * GWTServiceSurvey.java
 *
 * Created on March 18, 2009, 3:11 PM
 *
 * To change this template, choose Tools | Template Manager
 * and open the template in the editor.
 */

package org.ice.survey.client;
import appsusersevents.client.EventDescription;

import com.google.gwt.user.client.rpc.RemoteService;

/**
 *
 * @author giovanna
 */
public interface GWTServiceSurvey extends RemoteService{

    public String myMethod(String s);

    public String myMethod2(String s);
    public String  sendEventToGiga(String questionId, String answer, String me);
  //  public String subscribeTo(String s);


    public EventDescription[] getEvents(String userName);

    public void putEvents(EventDescription[] events, String userName);
     public Boolean validateUser(String name, String pwd);
}
