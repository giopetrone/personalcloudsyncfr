/*
 * ICommonCalendarAsync.java
 *
 * Created on December 22, 2008, 1:23 PM
 *
 * To change this template, choose Tools | Template Manager
 * and open the template in the editor.
 */

package src.com.google.gwt.sample.commoncalendar.client;
import appsusersevents.client.CalendarOwner;
import appsusersevents.client.EventDescription;
import appsusersevents.client.MyDate;
import appsusersevents.client.SingleUser;
import com.google.gwt.user.client.rpc.AsyncCallback;


/**
 *
 * @author marino
 */
public interface ICommonCalendarAsync {
  //  public void myMethod(String s, AsyncCallback  callback);
  //  public void myMethodS(String [] s, AsyncCallback  callback);
  //   public void  getPrices(String[] prices, AsyncCallback  callback);
      public void getApplicationTree( AsyncCallback  callback);
     public void validateUser(String name, String pwd,  AsyncCallback  callback);
      public void getGroupTree(SingleUser me, AsyncCallback  callback);
       public void getEvents( AsyncCallback  callback);
        public void putEvents(EventDescription[] events, AsyncCallback  callback);
       public void getCalendars(SingleUser sU, SingleUser[] users,MyDate startDate,  MyDate endDate, AsyncCallback  callback);
        public void updateCalendars(CalendarOwner [] calends, int rowIndex, AsyncCallback  callback);
    public void  cheGiornoOggi( AsyncCallback  callback);
    public void calendario6Mesi( AsyncCallback  callback);
}
