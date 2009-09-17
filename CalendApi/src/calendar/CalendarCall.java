/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package calendar;

import com.google.gdata.client.*;
import com.google.gdata.client.Query.CategoryFilter;
import com.google.gdata.client.calendar.CalendarService;
import com.google.gdata.data.*;
import com.google.gdata.data.calendar.CalendarEntry;
import com.google.gdata.data.calendar.CalendarEventEntry;
import com.google.gdata.data.calendar.CalendarEventFeed;
import com.google.gdata.data.calendar.CalendarFeed;
import com.google.gdata.data.extensions.*;
//import giga.Notification66;
import java.net.URL;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

/**
 *
 * @author marino
 */
public class CalendarCall {

    String googleCalFeed = "http://www.google.com/calendar/feeds/";
    CalendarService myService = null;
    String googleUserMail;
    String ownedCalendarsFeed = "default/owncalendars/full";
    String allCalendarsFeed = "/allcalendars/full";
    //   String allCalendarsFeed = "default";
    String privateCalendarsFeed = "/private/full";
    URL privateUrl;
    URL ownedUrl;
    URL allUrl;
    static int count = 0; //  temporaneo
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

     public CalendarCall validate (String googleUserMail, String pwd) {
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

    public List getCalendarEventsBoh(String googleUserMail, String pwd) {
        List retList = new ArrayList();
        try {
            CalendarFeed resultFeed = myService.getFeed(allUrl, CalendarFeed.class);
            List<CalendarEntry> li = resultFeed.getEntries();

            for (int i = 0; i < li.size(); i++) {
                CalendarEntry entry = li.get(i);
                Link l = entry.getSelfLink();
                System.out.println("\t" + entry.getTitle().getPlainText() + " " +
                        entry.getSelfLink().getHref());
            }
            if (count < li.size()) {
                CalendarEntry entry = li.get(count);
                entry.declareExtensions(myService.getExtensionProfile());
                count++;
                System.out.println("A calendar you own:");
                System.out.println();
                entry = entry.getSelf();
                String s = entry.getSelfLink().getHref();
                System.out.println("\t" + entry.getTitle().getPlainText() + " " + s);
                URL provaUrl = new URL(s);
                // CalendarEventEntry retEntry = myService.getEntry(provaUrl, CalendarEventEntry.class);
                //  retEntry.getSummary()
                //  System.out.println("entr="+retEntry.getTitle().getPlainText()+" "+retEntry.getClass());
                CalendarEventFeed unFeed = myService.getFeed(provaUrl, CalendarEventFeed.class);
                //    unFeed.declareExtensions(myService.getExtensionProfile());
                retList = unFeed.getEntries();
            }
        } catch (Exception ex) {
            ex.printStackTrace();
        }
        return retList;
    }

    public List getCalendarEvents() { //(String googleUserMail, String pwd) {
        List retList = new ArrayList();
        try {
            CalendarEventFeed unFeed = myService.getFeed(privateUrl, CalendarEventFeed.class);
            retList = unFeed.getEntries();
        } catch (Exception ex) {
            ex.printStackTrace();
        }
        return retList;
    }

    public void insertEvent(long absTime) {
        try {
            EventEntry myEntry = new EventEntry();
            myEntry.setTitle(new PlainTextConstruct("riuniuone SETA"));
            myEntry.setContent(new PlainTextConstruct("Meet for a quick discussion."));
            Person author = new Person("Marino Segnan", null, googleUserMail);
            myEntry.getAuthors().add(author);
            //    DateTime startTime = DateTime.parseDateTime("2006-04-17T15:00:00-08:00");
            //     DateTime endTime = DateTime.parseDateTime("2006-04-17T17:00:00-08:00");
            DateTime startTime = new DateTime(absTime);
            DateTime endTime = new DateTime(absTime + 3600 * 1000);
            When eventTimes = new When();
            eventTimes.setStartTime(startTime);
            eventTimes.setEndTime(endTime);
            myEntry.addTime(eventTimes);
// Send the request and receive the response:
            EventEntry insertedEntry = myService.insert(privateUrl, myEntry);
        } catch (Exception ex) {
            ex.printStackTrace();
        }
        System.out.println("inserApt fatta");
    }

    public void pulizia() {
        try {
            URL fedUrl = new URL(googleCalFeed + googleUserMail + "/private/full");
            CalendarEventFeed unFeed = myService.getFeed(fedUrl, CalendarEventFeed.class);
            List le = unFeed.getEntries();   ////EventEntry.class);
            Iterator it = le.iterator();
            while (it.hasNext()) {
                CalendarEventEntry en = (CalendarEventEntry) it.next();
                System.out.println(en.getTitle().getPlainText());
                if (en.getTitle().getPlainText().equals("riuniuone SETA")) {
                    URL entryUrl = new URL(en.getSelfLink().getHref());
                    myService.delete(entryUrl);
                    System.out.println("delete fatta");
                }
            }
        } catch (Exception ex) {
            ex.printStackTrace();
        }
    }

    public void insertEvent(long start, long end, String title) {
        try {
            EventEntry myEntry = new EventEntry();
            myEntry.setTitle(new PlainTextConstruct(title));
            myEntry.setContent(new PlainTextConstruct("Meet for a quick discussion."));
            Person author = new Person("Marino Segnan", null, googleUserMail);
            myEntry.getAuthors().add(author);
            //    DateTime startTime = DateTime.parseDateTime("2006-04-17T15:00:00-08:00");
            //     DateTime endTime = DateTime.parseDateTime("2006-04-17T17:00:00-08:00");
            DateTime startTime = new DateTime(start);
            DateTime endTime = new DateTime(end);
            When eventTimes = new When();
            eventTimes.setStartTime(startTime);
            eventTimes.setEndTime(endTime);
            myEntry.addTime(eventTimes);
// Send the request and receive the response:
            EventEntry insertedEntry = myService.insert(privateUrl, myEntry);
        } catch (Exception ex) {
            ex.printStackTrace();
        }
        System.out.println("inserApt fatta");
    }

    void leggiCalGio(/*Notification66 session, */String googleUserMail, String pwd) {
        try {
            URL feedUrl1 = new URL("http://www.google.com/calendar/feeds/gio.petrone%40gmail.com/private-bfcbd34c38b024bfd8c88366a5b4a3e1/full");
            URL feedUrl = new URL("http://www.google.com/calendar/feeds/gio.petrone%40gmail.com/private/full");
            new CalendarEventFeed().declareExtensions(myService.getExtensionProfile());
            CalendarEventFeed myFeed = myService.getFeed(feedUrl, CalendarEventFeed.class);
            System.out.println("dopo accesso cal gio");
            List le = myFeed.getEntries();
            Iterator it = le.iterator();
            while (it.hasNext()) {
                CalendarEventEntry en = (CalendarEventEntry) it.next();
                List<When> lw = en.getTimes();
                System.out.println("evento titolo = " + en.getTitle().getPlainText());
                if (lw.size() > 0) {
                    When wh = lw.get(0);
                    System.out.println(" " + wh.getStartTime().toString() + "  " + wh.getEndTime().toString());
                }
            }

          /* NON VA READONLY !!!! */  EventEntry myEntry = new EventEntry();
            myEntry.setTitle(new PlainTextConstruct("prova"));
            myEntry.setContent(new PlainTextConstruct("Meet for a quick discussion."));
            Person author = new Person("Marino Segnan", null, googleUserMail);
            myEntry.getAuthors().add(author);
                DateTime startTime = DateTime.parseDateTime("2009-06-17T15:00:00-08:00");
                DateTime endTime = DateTime.parseDateTime("2009-06-17T17:00:00-08:00");

            When eventTimes = new When();
            eventTimes.setStartTime(startTime);
            eventTimes.setEndTime(endTime);
            myEntry.addTime(eventTimes);
// Send the request and receive the response:
            EventEntry insertedEntry = myService.insert(feedUrl, myEntry);
           /* */

        } catch (Exception ex) {
            ex.printStackTrace();
        }
    }

    public void doIt(/*Notification66 session, */String googleUserMail, String pwd) {
        try {
            CalendarService myService = new CalendarService("di.unito.it.sharedCalendar");
            myService.setUserCredentials(googleUserMail, pwd);
            URL gioUrl = new URL(googleCalFeed + "gio.petrone@gmail.com" + "/public/basic");

            // Requesting a feed
            //    URL feedUrl = new URL(googleCalFeed+ googleUserMail + "/private/full");
            //  URL feedUrl = new URL(googleCalFeed + googleUserMail + "/allcalendars/full");
            URL feedUrl = new URL("http://www.google.com/calendar/feeds/gio.petrone%40gmail.com/private-bfcbd34c38b024bfd8c88366a5b4a3e1/full");

            //  URL feedUrl = new URL("http://www.google.com/calendar/feeds/gio.petrone%40gmail.com/public/full");

// Mark the feed as an Event feed:
            new CalendarEventFeed().declareExtensions(myService.getExtensionProfile());

            // Send the request and receive the response:
            //    Feed myFeed = myService.getFeed(feedUrl, Feed.class);
            CalendarEventFeed myFeed = myService.getFeed(feedUrl, CalendarEventFeed.class);
            System.out.println("dopo accesso cal gio");
            List le = myFeed.getEntries();
            Iterator it = le.iterator();
            CalendarEventEntry en2 = null;
            while (it.hasNext()) {
                CalendarEventEntry en = (CalendarEventEntry) it.next();
                en2 = en;
                //   System.out.println("cal titolo = " + en.getTitle().getPlainText() + " " + en.getClass());

                List<When> lw = en.getTimes();
                //    MyCalendarEventEntry mc = new MyCalendarEventEntry(en);
                //    session.writeXMLObject(mc);//   session.writeObject(en);
                System.out.println("evento titolo = " + en.getTitle().getPlainText());
                if (lw.size() > 0) {
                    When wh = lw.get(0);
                    System.out.println(" " + wh.getStartTime().toString() + "  " + wh.getEndTime().toString());
                }
            }
            // prova mar

            URL provaUrl = new URL(en2.getSelfLink().getHref());
            System.out.println("URL = " + provaUrl);

            EventEntry retEntry = myService.getEntry(provaUrl, EventEntry.class);
            System.out.println("entr=" + retEntry.getTitle().getPlainText());



            //  URL fedUrl = new URL(googleCalFeed + "sgnmrn@gmail.com" + "/public/full");
            URL fedUrl = new URL(googleCalFeed + "gio.petrone@gmail.com" + "/public/full");
            //  URL fedUrl = new URL(googleCalFeed + googleUserMail + "/private/full");


            CalendarEventFeed unFeed = myService.getFeed(fedUrl, CalendarEventFeed.class);

            //    Feed unFeed = myService.getFeed(fedUrl,Feed.class);
            List le1 = unFeed.getEntries();   ////EventEntry.class);

            Iterator it1 = le1.iterator();

            while (it1.hasNext()) {
                CalendarEventEntry en = (CalendarEventEntry) it1.next();
                List<When> lw = en.getTimes();
                //    MyCalendarEventEntry mc = new MyCalendarEventEntry(en);
                //    session.writeXMLObject(mc);//   session.writeObject(en);
                System.out.println("evento titolo = " + en.getTitle().getPlainText());
                if (lw.size() > 0) {
                    When wh = lw.get(0);
                    System.out.println(" " + wh.getStartTime().toString() + "  " + wh.getEndTime().toString());
                }
            }


            System.exit(0);


            // fine prova mar











// Inserting a new item
            URL postUrl =
                    //              new URL(googleCalFeed+ googleUserMail + "/private/full");
                    new URL(googleCalFeed + googleUserMail + "/allcalendars/full");
            EventEntry myEntry = new EventEntry();

            myEntry.setTitle(new PlainTextConstruct("impegno con Darcy"));
            myEntry.setContent(new PlainTextConstruct("Meet for a quick lesson."));

            Person author = new Person("Elizabeth Bennet", null, googleUserMail);
            myEntry.getAuthors().add(author);

            //    DateTime startTime = DateTime.parseDateTime("2006-04-17T15:00:00-08:00");
            //     DateTime endTime = DateTime.parseDateTime("2006-04-17T17:00:00-08:00");
            DateTime startTime = DateTime.now();
            DateTime endTime = DateTime.now();
            When eventTimes = new When();
            eventTimes.setStartTime(startTime);
            eventTimes.setEndTime(endTime);
            myEntry.addTime(eventTimes);

// Send the request and receive the response:
            EventEntry insertedEntry = myService.insert(postUrl, myEntry);

//Requesting a specific entry

            URL entryUrl = new URL(insertedEntry.getSelfLink().getHref());
            EventEntry retrievedEntry = myService.getEntry(entryUrl, EventEntry.class);

// Searching entries
            Query myQuery = new Query(feedUrl);
            myQuery.setFullTextQuery("Evento");
            Feed myResultsFeed = myService.query(myQuery, Feed.class);
            if (myResultsFeed.getEntries().size() > 0) {
                Entry firstMatchEntry = myResultsFeed.getEntries().get(0);
                String myEntryTitle = firstMatchEntry.getTitle().getPlainText();
                System.out.println("myEntryTitle= " + myEntryTitle);
            }

// Querying by category
            Category myCategory = new Category("by_liz");
            CategoryFilter myCategoryFilter = new CategoryFilter(myCategory);
            myQuery.addCategoryFilter(myCategoryFilter);
            Feed myCategoryResultsFeed = myService.query(myQuery, Feed.class);

//Updating an item
            retrievedEntry.setTitle(new PlainTextConstruct("Important meeting"));
            URL editUrl = new URL(retrievedEntry.getEditLink().getHref());
            EventEntry updatedEntry = myService.update(editUrl, myEntry);

// Deleting an item
            URL deleteUrl = new URL(updatedEntry.getEditLink().getHref());
        // NON CANCELLO   myService.delete(deleteUrl);
        } catch (Exception ex) {
            ex.printStackTrace();
        }
        System.out.println("CIAO");

    }

    public static void main(String[] args) {
        CalendarCall c = getCalendarCall("sgnmrn@gmail.com", "micio11");
        c.leggiCalGio("sgnmrn@gmail.com", "micio11");
      /*  while (true) {
            List le = c.getCalendarEventsBoh("sgnmrn@gmail.com", "micio11");
            if (le.size() == 0) {
                break;
            }

            Iterator it = le.iterator();

            while (it.hasNext()) {
                CalendarEventEntry en = (CalendarEventEntry) it.next();
                List<When> lw = en.getTimes();
                //    MyCalendarEventEntry mc = new MyCalendarEventEntry(en);
                //    session.writeXMLObject(mc);//   session.writeObject(en);
                System.out.println("evento titolo = " + en.getTitle().getPlainText());
                if (lw.size() > 0) {
                    When wh = lw.get(0);
                    System.out.println(" " + wh.getStartTime().toString() + "  " + wh.getEndTime().toString());
                }

            }
        }*/
    }
}
