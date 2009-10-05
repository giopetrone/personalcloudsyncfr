/*
 * GWTServiceSurveyAsync.java
 *
 * Created on March 18, 2009, 3:11 PM
 *
 * To change this template, choose Tools | Template Manager
 * and open the template in the editor.
 */
package org.ice.survey.client;

import appsusersevents.client.EventDescription;
import com.google.gwt.user.client.rpc.AsyncCallback;

/**
 *
 * @author giovanna
 */
public interface GWTServiceSurveyAsync {

    public void myMethod(String s, AsyncCallback callback);

    public void myMethod2(String s, AsyncCallback callback);

    public void sendEventToGiga(String questionId, String answer, String me, AsyncCallback callback);

//    public void subscribeTo(String s, AsyncCallback callback);
    public void getEvents(String userName, AsyncCallback callback);

    public void putEvents(EventDescription[] events, String userName, AsyncCallback callback);
       public void validateUser(String name, String pwd,  AsyncCallback  callback);
       public void authenticate(String s, AsyncCallback callback);
}
