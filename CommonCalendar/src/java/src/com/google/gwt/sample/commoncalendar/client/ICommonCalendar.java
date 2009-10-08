/*
 * ICommonCalendar.java
 *
 * Created on December 22, 2008, 1:23 PM
 *
 * To change this template, choose Tools | Template Manager
 * and open the template in the editor.
 */
package src.com.google.gwt.sample.commoncalendar.client;


import appsusersevents.client.CalendarOwner;
import appsusersevents.client.ApplicationDescription;
import appsusersevents.client.EventDescription;
import appsusersevents.client.MyDate;
import appsusersevents.client.SingleUser;
import appsusersevents.client.UserGroup;
import com.google.gwt.user.client.rpc.RemoteService;

//import com.google.gwt.user.client.rpc.RemoteServiceRelativePath;

// usato solo la prima volta, poi sparicse
// guardare javadoc
//   @RemoteServiceRelativePath("stockPrices")
/**
 *
 * @author marino
 */
public interface ICommonCalendar extends RemoteService {

    public ApplicationDescription[] getApplicationTree();

    public SingleUser validateUser(String name, String pwd);

    public UserGroup[] getGroupTree(SingleUser me);

    public EventDescription[] getEvents();

    public void putEvents(EventDescription[] events);

    public CalendarOwner[] getCalendars(SingleUser sU, SingleUser[] users, MyDate startDate,  MyDate endDate);

    public void updateCalendars(CalendarOwner [] calends, int rowIndex);

    public MyDate  cheGiornoOggi();

    public String[] calendario6Mesi();
    public SingleUser authenticate(String s);
}
