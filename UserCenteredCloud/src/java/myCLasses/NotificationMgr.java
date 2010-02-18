package myCLasses;

/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Set;

/**
 *
 * @author liliana
 */
public class NotificationMgr {


    private String userID; // user account

    private UserAgt userAgt; // user agent of user

    private HashMap<String, ChatClient> chatClients;

    private ArrayList<EventDescription> eventList; //list of events to be delivered to the user

                // for each group the user belongs to, this specifies the group members
    private HashMap<String, ArrayList<String>> groupsTable;

                // projection of the global spheresTable on the individual user
    private HashMap<String, ArrayList<String>> spheresTable;

    private HashMap<String, ArrayList<EventDescription>> notificationLists = new HashMap();
            // notification lists for each sphere the user is involved in

    private int numFilteredEvents; // number of filtered events




    public NotificationMgr(String usr, UserAgt ua, HashMap<String,ChatClient> chClients) {
        userID = usr; 
        userAgt = ua;
        chatClients = chClients; // set all chat clients

        eventList = new ArrayList(); // list of user events to be notified
        groupsTable = new HashMap();

        spheresTable = new HashMap(); // initializes spheresTable
        ArrayList<String> userSpheres = new ArrayList();
        userSpheres.add("unknown");
        spheresTable.put("userID", userSpheres);

        notificationLists = new HashMap();
        notificationLists.put("unknown", new ArrayList()); //add notification list for ambiguous notifications
        EventUtilities.setEventMatchTable();//sets the match table for identifying redundant events
    }

    public String getUserID() {
        return userID;
    }

    
        // adds a new group to the group table and updates the
    // spheres table accordingly
    public void addGroup(Sphere sph) {
        String gName = sph.getName();
        if (gName!=null) {
            EventUtilities.addGroup(gName, sph.getMembers(), groupsTable, spheresTable);
            if (!notificationLists.containsKey(gName))
                notificationLists.put(gName, new ArrayList()); // adds the notification list
        }
    }

    // removes a group from the group table and updates the
    // userSpheres accordingly
    public void deleteGroup(String gName) {
        if (gName!=null) {
            EventUtilities.deleteGroup(gName, groupsTable, spheresTable);
            if (notificationLists.containsKey(gName))// removes notification list
                notificationLists.remove(gName);
        }
    }

    public String getGroupsTableAsString() {
        return groupsTable.toString();
    }

    public ArrayList<EventDescription> getEventList() {
        return eventList;
    }

    // adds an event to the userEventList and cyclically disambiguates the list by context
    // needs to be synchronized because events are generated in parallel
    // --> can generate concurrentModificationException
    public synchronized void addEvent(EventDescription ev) {

        boolean relevantEvent = ev.getProcessed().equalsIgnoreCase("byContext") &&  // ev processed by EventAnalyzer
             (userID.equalsIgnoreCase(ev.getUser()) || // ev is by user
              ev.getDestinatari().contains(userID) ||  // ev is for user
              EventUtilities.intersects(ev.getSpheres(), spheresTable.get(userID)));

        if (!relevantEvent) // ignore events which should not be received
            return;         // in publish and subscribe

        eventList.add(ev); // add to event list for presentation
        if (!userID.equalsIgnoreCase(ev.getUser())) // not an auto-notification
            applyFilters(ev); // applies filters for notifying ev
        if (ConfigurationSettings.forgetEvents && // system in "forget" modality
                    eventList.size()>ConfigurationSettings.NOTIFICATION_LENGTH)
            eventList.remove(0); // forgets oldest event
    }

    // applies notification filters (context filter, no filter, etc.)
    private void applyFilters(EventDescription ev) {
        boolean notify = false; // by default, do not notify
                                    // context filter
        if (ConfigurationSettings.contextFilter) {
            ArrayList<String> spheres = ev.getSpheres();
            ArrayList<String> relSpheres = ev.getRelevantSpheres();
            ArrayList<String> cf = userAgt.getCurrentFocus();
            if (EventUtilities.intersects(spheres, cf) || // in focus
                EventUtilities.intersects(relSpheres, cf))
                notify = true;
            else {                              // belongs to "noFilter" list of user
                ArrayList<String> noFilter = userAgt.getNoFilter();
                if (EventUtilities.intersects(spheres, noFilter) ||
                    EventUtilities.intersects(relSpheres, noFilter))
                    notify = true;
            }
        } else notify = true; // no filter
        if (notify) {
            String sender = ev.getUser();
            String receiver = userID; // receiver is always current user
            String message = getNotificationString(ev);
            genIM(receiver, sender, message);
        }
        else numFilteredEvents++; //count filtered event for notification summary
           // but do not store the event (it will be shown in the web space)
           // NB: auto-notifications do not contribute to incrementinf numFilteredEvents
           // bacause this method is not invoked in that case.
    }

            //sends incremental summary of unread notifications
    public void sendNotificationSummary() {
        String receiver = userID;
        String url = "http://localhost:8080/UserCenteredCloud/notifications.jsp";
        String message = "You have " + numFilteredEvents + 
                         " unread notifications " + url;
        if (numFilteredEvents>0) 
            genIM(receiver, "utntest11@gmail.com", message);
    }

    protected void genIM(String receiver, String sender, String message) {
        if (ConfigurationSettings.internetConnection) {
            //receiver = "liliana.ardissono@gmail.com";
            //sender = "gio.petrone@gmail.com";
            System.err.println("SENDER: " + sender + "MESSAGE: " + message);
            ChatClient chClient = chatClients.get(sender);
            if (chClient!=null) {
                try {
                    chClient.sendMessage(message, receiver);
                }
            catch (Exception e) {System.err.println("NotificationMgr: problem in IM - " + e.toString());}
            }
            else System.err.println("NotificationMgr: NULL CHAT CLIENT!!");
        }
        else System.err.println("IM---- " + message); // run offline
    }


            // returns the notification list(s) where the event should be displayed
    private ArrayList<String> assignEventToNotificationLists(EventDescription ev) {

        ArrayList<String> lists = new ArrayList();
                                    // check event spheres
        ArrayList<String> spheres = ev.getSpheres();
        if (spheres==null) spheres = new ArrayList(); // patch
        if (spheres.size()>=1) {// non ambiguous event --> notify in all specified lists
            for (int i=0; i<spheres.size(); i++)
                lists.add(spheres.get(i)); // all event spheres
        } 
        else {                  // spheres.size() ==0
            ArrayList<String> relSphs = ev.getRelevantSpheres();
            if (relSphs==null)
                relSphs = new ArrayList(); // patch
            else if (relSphs.size()==1)    // event disambiguated by context
                lists.add(relSphs.get(0));
            // if relSphs includes more than one sphere --> do not assign to lists
        }
        if (lists.size()==0) // the event has not been assigned to any notification list
            lists.add("unknown"); // catch all
        return lists;
    }

    // prepares the notification lists for presenting the
    // events stored in eventList
    public void refreshNotificationLists() {
        cleanNotificationLists(); // empties the notification lists
        EventUtilities.disambiguateEventList(eventList); // disambiguate event list
        EventUtilities.cleanEventList(eventList);//cleans eventList from redundant events
        for (int i=0; i<eventList.size(); i++) {
            EventDescription ev = eventList.get(i);
            ArrayList<String> lists = assignEventToNotificationLists(ev);
            for (int j=0; j<lists.size(); j++) { // add event to all relevant notification lists
                String listName = lists.get(j);
                ArrayList<EventDescription> notifList = notificationLists.get(listName);
                if (notifList!=null)
                    notifList.add(ev);
                else {
                    System.err.println("NotificationMgr " + userID +
                                   ": unexpected event - sphere: " + listName + "; EV: " + ev);
                    notifList = notificationLists.get("unknown");
                    notifList.add(ev);
                }
            }
        }
    }

                        // removes notifications from all notification lists
    private void cleanNotificationLists() {
        Set keys = notificationLists.keySet(); 
        Iterator it = keys.iterator();
        while (it.hasNext()) {
            ArrayList<EventDescription> notifList = notificationLists.get(it.next());
            if (notifList!=null)
                notifList.clear();
        }
    }

            // returns notifications by sphere, formatted as unordered lists
    public String getFormattedNotifications() {
        refreshNotificationLists();// prepares the notification lists
        String out = "";
        Set keys = notificationLists.keySet();
        Iterator it = keys.iterator();
        while (it.hasNext()) {
            String sphere = (String)it.next(); // notification list of user sphere
            if (!sphere.equalsIgnoreCase("unknown")) {
                out = out + "<h2>" + sphere + ":</h2><p> " +
                                // retrieve list of notifications of sphere
                      getNotificationListAsString(sphere) + "</p>";
            }
        }
                                    // leave "unknown" notification list as the last one
        out = out + "<h2>Other notifications:</h2><p> " +
              getNotificationListAsString("unknown") + "</p>";
        return out;
    }

    // returns a String including all the notifications related to a user sphere
    // in HTML
    private String getNotificationListAsString(String sphere) {
        String out = "";
        ArrayList<EventDescription> notifications = notificationLists.get(sphere);
        if (notifications==null)
            System.err.println("NotificationMgr: " + userID + "; the notification list of sphere " +
                               sphere + " does not exist!");
        else {
            out = "<ul>"; // start notification list
            for (int k=0; k<notifications.size(); k++) {
                EventDescription ev = notifications.get(k);
                out = out + "<li>" + getNotificationString(ev) + "</li>";
            }
            out = out + "</ul>"; // end list
        }
        return out;
    }

                    // generates string describing one notification
    private String getNotificationString(EventDescription ev) {
        String app = ev.getApplication();
        String evName = ev.getEventName();
        ArrayList<String>parameterNames = ev.getParameterNames();
        String out = //app + ": " +
                     evName + " by " + ev.getUser() + "; ";
        //out = out + "sphere/users: ";
        //if (ev.getSpheres().size()>0)
        //    out = out + ev.getSpheres().toString() + "; ";
        //else out = out + ev.getDestinatari().toString() + "; ";
        for (int i=0; i<parameterNames.size(); i++) {
            String name = parameterNames.get(i);
            out = out + name + ": " + ev.getParameter(name) + "; ";
        }
        return  out;
    }

    //prunes "eventList" by removing the events before "date"
    public void pruneEventListByDate(Date date) {
        EventUtilities.deleteEventsBeforeDate(eventList, date);
    }


    public void clear() { // cleans the event list used for the notifications
        eventList = new ArrayList();
        cleanNotificationLists();
        numFilteredEvents = 0;
    }
}//end class
