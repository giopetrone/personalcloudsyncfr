/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package jav;

import com.google.gdata.client.GoogleService;
import com.google.gdata.client.calendar.CalendarService;
import com.google.gdata.data.*;
import com.google.gdata.data.calendar.CalendarEntry;
import com.google.gdata.data.calendar.CalendarEventEntry;
import com.google.gdata.data.calendar.CalendarEventFeed;
import com.google.gdata.data.calendar.CalendarFeed;
import com.google.gdata.data.extensions.*;
import com.google.gdata.util.AuthenticationException;
import com.google.gdata.util.ServiceException;
import java.io.IOException;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 *
 * @author marino
 */
public class CalendarCall {

    String googleCalFeed = "http://www.google.com/calendar/feeds/";
    CalendarService myService = null;
    String googleUserMail;
    String guestEmail;
    String ownedCalendarsFeed = "default/owncalendars/full";
    String allCalendarsFeed = "/allcalendars/full";
    //   String allCalendarsFeed = "default";
    String privateCalendarsFeed = "/private/full";
    URL privateUrl;
    URL ownedUrl;
    URL allUrl;
    // static int count = 0; //  temporaneo
    static CalendarCall TheCall = null;

    public static CalendarCall getCalendarCall(String googleUserMail, String pwd) {
        if (TheCall == null) {
            TheCall = new CalendarCall(googleUserMail, pwd);
        }
        return TheCall;
    }

    public CalendarCall() {
        myService = new CalendarService("di.unito.it.sharedCalendar");
    }

    public CalendarCall validate(String googleUserMail, String pwd) {
        try {
            myService.setUserCredentials(googleUserMail, pwd);
        } catch (Exception ex) {
            // ex.printStackTrace();
            return null;
        }
        return this;
    }

    public CalendarCall(String googleUserMail, String pwd) {
        System.out.println("Calendar Call : mail,pwd " + googleUserMail + "," + pwd);
        this.googleUserMail = googleUserMail;
        myService = new CalendarService("di.unito.it.sharedCalendar");
        try {
            myService.setUserCredentials(googleUserMail, pwd);
            privateUrl = new URL(googleCalFeed + googleUserMail + privateCalendarsFeed);
            //     ownedUrl = new URL(googleCalFeed + googleUserMail + ownedCalendarsFeed);
            ownedUrl = new URL(googleCalFeed + ownedCalendarsFeed);

            //  allUrl = new URL(googleCalFeed + googleUserMail + allCalendarsFeed);
            allUrl = new URL(googleCalFeed + allCalendarsFeed);
        } catch (Exception ex) {
            ex.printStackTrace();
        }
    }

    /*
     * parametri: googleUserMail e pwd per login
     * guestEmail : in quale calendario POSSEDUTO inseriro' l'evento
     */

    public CalendarCall(String googleUserMail, String pwd, String guestEmail) {

        this.googleUserMail = googleUserMail;
        this.guestEmail = guestEmail;
        myService = new CalendarService("di.unito.it.sharedCalendar");
        try {
            myService.setUserCredentials(googleUserMail, pwd);

            ownedUrl = new URL(googleCalFeed + ownedCalendarsFeed);
            System.out.println("Calendar Call : mail,pwd,guest " + googleUserMail + "," + pwd + "," + guestEmail);
            System.out.println("URL: " + googleCalFeed + ownedCalendarsFeed);
        } catch (Exception ex) {
            ex.printStackTrace();
        }
    }

    public List getCalendarEvents() { //(String googleUserMail, String pwd) {
        List retList = new ArrayList();
        try {
            CalendarFeed resultFeed = myService.getFeed(ownedUrl, CalendarFeed.class);
            System.out.println("Calendars you own:");
            //   System.out.println();
            for (int i = 0; i < resultFeed.getEntries().size(); i++) {
                CalendarEntry entry = resultFeed.getEntries().get(i);
                String urlref = entry.getLink(Link.Rel.ALTERNATE, Link.Type.ATOM).getHref();
                //   System.out.println("edit link: [" + urlref + "]");
                String toMatc = urlref.replaceFirst("%40", "@");
                if (toMatc.contains(guestEmail)) {
//                    System.out.println("\t" + entry.getTitle().getPlainText());
//                    System.out.println("edit link: [" + entry.getLink(Link.Rel.ALTERNATE, Link.Type.ATOM).getHref() + "]");
                    // final URL feedURL = new URL(entry.getLink(Link.Rel.ALTERNATE, Link.Type.ATOM).getHref());
                    final URL feedURL = new URL(urlref);
                    CalendarEventFeed unFeed = myService.getFeed(feedURL, CalendarEventFeed.class);
                    retList = unFeed.getEntries();
                    break;
                }
            }
        } catch (Exception ex) {
            ex.printStackTrace();
        }
        if (retList.isEmpty()) {
            System.out.println("\t no appts or not found calendar:" + guestEmail);
        }
        return retList;
    }

    public void insertEvent(long absTime, String title, String content, int durata) {
        try {
            EventEntry myEntry = new EventEntry();
            myEntry.setTitle(new PlainTextConstruct(title));
            myEntry.setContent(new PlainTextConstruct(content));
            //   Person author = new Person("Marino Segnan", null, googleUserMail);
            Person author = new Person(googleUserMail, null, googleUserMail);
            myEntry.getAuthors().add(author);
            DateTime startTime = new DateTime(absTime);
            DateTime endTime = new DateTime(absTime + durata * 3600 * 1000);
            When eventTimes = new When();
            eventTimes.setStartTime(startTime);
            eventTimes.setEndTime(endTime);
            myEntry.addTime(eventTimes);
            CalendarFeed resultFeed = myService.getFeed(ownedUrl, CalendarFeed.class);
            for (int i = 0; i < resultFeed.getEntries().size(); i++) {
                CalendarEntry entry = resultFeed.getEntries().get(i);
                String urlref = entry.getLink(Link.Rel.ALTERNATE, Link.Type.ATOM).getHref();
                System.out.println("edit link: [" + urlref + "]");
                String toMatc = urlref.replaceFirst("%40", "@");
                if (toMatc.contains(guestEmail)) {
                    final URL postURL = new URL(urlref);
                    EventEntry insertedEntry = myService.insert(postURL, myEntry);
                    System.out.println("inserApt fatta");
                    return;
                }
            }
        } catch (Exception ex) {
            ex.printStackTrace();
        }
    }
    
    public void printCalendar(List retList) {
        Iterator it = retList.iterator();
        while (it.hasNext()) {
            CalendarEventEntry en = (CalendarEventEntry) it.next();
            List<When> lw = en.getTimes();
            System.out.println("evento titolo = " + en.getTitle().getPlainText());
            if (lw.size() > 0) {
                When wh = lw.get(0);
                System.out.println(" " + wh.getStartTime().toString() + "  " + wh.getEndTime().toString());
            }
        }
    }

    public static void insertInCalendar(String duedate,String taskname)
    {
        try {
            CalendarService myService = new CalendarService("exampleCo-exampleApp-1");
            myService.setUserCredentials("fabrizio.torretta@gmail.com", "gregorio");
            URL postUrl = new URL("https://www.google.com/calendar/feeds/fabrizio.torretta@gmail.com/private/full");
            CalendarEventEntry myEntry = new CalendarEventEntry();
            myEntry.setTitle(new PlainTextConstruct(taskname));
            myEntry.setContent(new PlainTextConstruct("Task to do"));
            
            System.out.println("%%%%%%%%%%");
          
       //     String year = duedate.substring(0,4);
       //     String month = duedate.substring(5,7);
         //   String day = duedate.substring(8,10);
            String start = "T00:00:00-00:00";
            String end = "T21:59:00-00:00";
        //    duedate = year+"-"+month+"-"+day;
            DateTime startTime = DateTime.parseDateTime(duedate+start);
            System.out.println(startTime);
            DateTime endTime = DateTime.parseDateTime(duedate+end);
            //    DateTime startTime = DateTime.parseDateTime("2010-09-17T15:00:00-08:00");
      //      DateTime startTime = DateTime.parseDateTime("2010-09-03T22:00:00-00:00");
          //  DateTime endTime = DateTime.parseDateTime("2010-09-04T21:59:00-00:00");
            When eventTimes = new When();
            eventTimes.setStartTime(startTime);
            eventTimes.setEndTime(endTime);
            myEntry.addTime(eventTimes);
            // Send the request and receive the response:
            CalendarEventEntry insertedEntry = myService.insert(postUrl, myEntry);
        } catch (IOException ex) {
            Logger.getLogger(CalendarCall.class.getName()).log(Level.SEVERE, null, ex);
            System.out.println(ex.toString());
        } catch (ServiceException ex) {
            Logger.getLogger(CalendarCall.class.getName()).log(Level.SEVERE, null, ex);
            System.out.println(ex.toString());
        }
    }



    public static void insertTaskInCalendar(String duedate,String user, String pwd, String taskname)
    {
        try {
            CalendarService myService = new CalendarService("exampleCo-exampleApp-1");
            myService.setUserCredentials(user, pwd);
            URL postUrl = new URL("https://www.google.com/calendar/feeds/"+user+"/private/full");
            CalendarEventEntry myEntry = new CalendarEventEntry();
            myEntry.setTitle(new PlainTextConstruct(taskname));
            myEntry.setContent(new PlainTextConstruct("Task to do"));

            System.out.println("%%%%%%%%%%");
            System.out.println(duedate);
            String start = "T00:00:00-00:00";
            String end = "T21:59:00-00:00";
        //    duedate = year+"-"+month+"-"+day;
            DateTime startTime = DateTime.parseDateTime(duedate+start);
            System.out.println(startTime);
            DateTime endTime = DateTime.parseDateTime(duedate+end);
            //    DateTime startTime = DateTime.parseDateTime("2010-09-17T15:00:00-08:00");
      //      DateTime startTime = DateTime.parseDateTime("2010-09-03T22:00:00-00:00");
          //  DateTime endTime = DateTime.parseDateTime("2010-09-04T21:59:00-00:00");
            When eventTimes = new When();
            eventTimes.setStartTime(startTime);
            eventTimes.setEndTime(endTime);
            myEntry.addTime(eventTimes);
            // Send the request and receive the response:
            CalendarEventEntry insertedEntry = myService.insert(postUrl, myEntry);
        } catch (IOException ex) {
            Logger.getLogger(CalendarCall.class.getName()).log(Level.SEVERE, null, ex);
            System.out.println(ex.toString());
        } catch (ServiceException ex) {
            Logger.getLogger(CalendarCall.class.getName()).log(Level.SEVERE, null, ex);
            System.out.println(ex.toString());
        }
    }




    public static void main(String[] args) throws MalformedURLException, AuthenticationException, IOException, ServiceException {
        CalendarCall c = new CalendarCall("fabrizio.torretta@gmail.com", "gregorio", "fabrizio.torretta@gmail.com");
        long timeInMillis = System.currentTimeMillis();
        System.out.println(timeInMillis);
        // 1000*3600*24*30  == millisec*secondi*ore*giorni
      //  c.insertEvent(timeInMillis+ 1000*3600*24, "prova Fabri", "appuntamento", 2);
        CalendarService myService = new CalendarService("exampleCo-exampleApp-1");
        myService.setUserCredentials("fabrizio.torretta@gmail.com", "gregorio");

       URL postUrl = new URL("https://www.google.com/calendar/feeds/fabrizio.torretta@gmail.com/private/full");
    CalendarEventEntry myEntry = new CalendarEventEntry();

    myEntry.setTitle(new PlainTextConstruct("Task to do 2"));
    myEntry.setContent(new PlainTextConstruct("Meet for a quick lesson."));

//    DateTime startTime = DateTime.parseDateTime("2010-09-17T15:00:00-08:00");
    DateTime startTime = DateTime.parseDateTime("2010-09-03T22:00:00-00:00");

     DateTime endTime = DateTime.parseDateTime("2010-09-04T21:59:00-00:00");
    When eventTimes = new When();
    eventTimes.setStartTime(startTime);
    eventTimes.setEndTime(endTime);
   // myEntry.addTime(eventTimes);

// Send the request and receive the response:
 //  CalendarEventEntry insertedEntry = myService.insert(postUrl, myEntry);
 //  CalendarEventEntry myEntry = new CalendarEventEntry();
    String duedate ="2010/09/12";
    String year = duedate.substring(0,4);
            String month = duedate.substring(5,7);
            String day = duedate.substring(8,10);
            String start = "T00:00:00-00:00";
            String end = "T21:59:00-00:00";
            System.out.println(year);
            duedate = year+"-"+month+"-"+day;
            System.out.println(duedate+start);
            DateTime startTime2 = DateTime.parseDateTime(duedate+start);
            System.out.println(startTime2);



// Send the request and receive the response:
CalendarEventEntry insertedEntry = myService.insert(postUrl, myEntry);
       // List aList = c.getCalendarEvents();
       // c.printCalendar(aList);

// Send the request and receive the response:

    }

}
