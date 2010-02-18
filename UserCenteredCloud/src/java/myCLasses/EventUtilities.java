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
public class EventUtilities {

            // matchTable: specification of the parameters for identifying redundant events
    private static HashMap<String, HashMap<String, ArrayList<String>>> eventMatchTable = setEventMatchTable();

    public static HashMap<String, HashMap<String, ArrayList<String>>> getEventMatchTable() {
        return eventMatchTable;
    }

    //sets the matchTable to be used for identifying redundant events
    //for each application, for each event type, it specifies the relevant parameters to be checked
    public static HashMap<String, HashMap<String, ArrayList<String>>> setEventMatchTable() {
        HashMap<String, HashMap<String, ArrayList<String>>> table = new HashMap();
                                            // CommonCalendar
        HashMap<String, ArrayList<String>> groupCalendar = new HashMap();
        ArrayList subj = new ArrayList();
        subj.add("subject");
        groupCalendar.put("MeetingProposal", subj);
        groupCalendar.put("MeetingConfirmation", subj);
                                            // GroupMgr
        HashMap<String, ArrayList<String>> groupMgr = new HashMap();
        ArrayList<String>groupName = new ArrayList();
        groupName.add("groupName");
        groupMgr.put("MembershipProposal", groupName);
        groupMgr.put("GroupCreated", groupName);
        groupMgr.put("GroupDeleted", groupName);
                                            // SurveyMgr
        HashMap<String, ArrayList<String>> surveyMgr = new HashMap();
        ArrayList<String>noParameters = new ArrayList();
        surveyMgr.put("MeetingAnswer", noParameters);
        surveyMgr.put("MembershipAnswer", noParameters);
                                            // GoogleDocs
        HashMap<String, ArrayList<String>> googleDocs = new HashMap();
        ArrayList<String>docName = new ArrayList();
        docName.add("docName");
        googleDocs.put("DocCreated", docName);
        googleDocs.put("DocUpdated", docName);
        googleDocs.put("DocRemoved", docName);

                                            // GoogleMail
        HashMap<String, ArrayList<String>> googleMail = new HashMap();
        ArrayList<String>subject = new ArrayList();
        subject.add("subject");
        googleMail.put("MailSent", subject);
        googleMail.put("MailReceived", subject);

        // set table
        table.put("GroupCalendar", groupCalendar);
        table.put("GroupMgr", groupMgr);
        table.put("SurveyMgr", surveyMgr);
        table.put("GoogleDocs", googleDocs);
        table.put("GoogleMail", googleMail);
        return table;
    }

    public static boolean intersects(ArrayList<String> list1, ArrayList<String> list2) {
        if (list1==null || list2==null)
            return false;
        boolean out = false;
        for (int i=0; i<list1.size(); i++) {
            if (list2.contains(list1.get(i))) {
                out = true;
                break;
            }
        }
        return out;
    }

    public static ArrayList<String> getIntersection(ArrayList<String> list1,
                                                    ArrayList<String> list2) {
        ArrayList<String> out = new ArrayList();
        if (list1.size()!=0 && list2.size()!=0)
            for (int i=0; i<list1.size(); i++) {
                String item = list1.get(i);
                if (list2.contains(item))
                    out.add(item);
            }
        return out;
    }

                                        // clones an array list of Strings
    public static ArrayList<String> cloneArrayListOfString(ArrayList<String> a) {
        ArrayList<String> out = new ArrayList();
        if (a!=null)
            for (int i=0; i<a.size(); i++) {
                String s = a.get(i);
                out.add(s);
            }
        return out;
    }
    
    public static String genString(ArrayList<String> a) {
        // returns the list of receivers of a mail message as a String
        String s = "";
        for (int i=0; i<a.size(); i++)
            s = s + a.get(i) + " ";
        return s;
    }

            // checks whether s1 and s2 contain the same elements (strings)
    public static boolean equalSets(ArrayList<String> s1, ArrayList<String> s2) {
        boolean equal = true;
        if (s1.size()!= s2.size())
            equal = false;
        int i=0;
        while (i<s1.size() && equal) {
            String item = s1.get(i);
            if (!s2.contains(item))
                equal = false;
            i++;
        }
        return equal;
    }


    // selects from an event list the events with timestamp in the specified interval
    public static ArrayList<EventDescription> selectEventsByTime(ArrayList<EventDescription> eventList,
                                  Date startDate, Date endDate) {
        ArrayList out = new ArrayList();
        if (startDate==null && endDate==null)// all events
            out = eventList;
        if (startDate!=null && endDate==null) {// events AFTER or AT startDate
            for (int i = 0; i < eventList.size(); i++) {
                EventDescription ev = (EventDescription) eventList.get(i);
                if (ev.getTime().compareTo(startDate.toString()) >= 0)
                    out.add(ev);
            }
        }
        if (startDate==null && endDate!=null) { //events BEFORE or AT endDate
            for (int i = 0; i < eventList.size(); i++) {
                EventDescription ev = (EventDescription) eventList.get(i);
                if (ev.getTime().compareTo(startDate.toString()) <= 0)
                    out.add(ev);
            }
        }
        return out;
    }


   // removes from eventList the events whose date is before "date"
   public static void deleteEventsBeforeDate(ArrayList<EventDescription> eventList, Date date) {
       int i=0;
       int size = eventList.size();
       while (i<size) {
            EventDescription ev = (EventDescription) eventList.get(i);
            String s = ev.getTime();
            if (s.compareTo(date.toString())<0) {
                eventList.remove(i);
                size--;
            }
            else i++;
       }
   }


    // cleans a list of events from redundant events,
    // e.g., cyclic events which are published every few minutes
    // NB: ignores the "user" in the match.
    public static void cleanEventList(ArrayList<EventDescription> events) {
        int i = events.size()-1;
        while (i>=0) {
            EventDescription pivot = events.get(i);
            ArrayList<String> parNames = pivot.getMatchParameters();
            EventDescription template = pivot.createtemplate(true, false,//application, explicitEvenr
                    false, true, false, true, true, //processed, eventName, user, spheres, relevantSpheres,
                    false, false, true, //time, eventId, activity
                    false, false, false, false, //userGroup, dataId, sender, receiver
                    false, false, false, // correlationId, sessionId, destinatario,
                    parNames);
            //System.err.println("PIVOT: " + pivot + "; position: " + i);
            for (int j=i-1; j>=0; j--) {    // removes redundant events leaving the
                EventDescription ev = events.get(j); // most recent one
                //System.err.println("EVENTO: " + ev);
                if (ev.compatibleWith(template)) {
                    if (ev.getTime().compareTo(pivot.getTime())>0) {// ev more recent than pivot
                        int index = events.indexOf(pivot);
                        events.set(index, ev);// replace pivot with ev
                        pivot = ev;//updates the pivot
                    }
                    events.remove(j);
                }
            }
            i= events.indexOf(pivot)-1;
        }
    }


    // disambiguate relevant spheres of "ev" by analyzing the history of events.
    // Then, applies disambiguation to all the related events in the history
    // NB: it ignores the eventName in order to reason about all the actions
    // that are performed on the same objects (specified by the parameters)
    private static void historyEventDisambiguation(EventDescription ev,
                                        ArrayList<EventDescription> eventList) {
        ArrayList<String> evSphs = ev.getRelevantSpheres();
        ArrayList<String> relevant = new ArrayList();
        for (int i=0; i<evSphs.size(); i++)// clones relevantSpheres
            relevant.add(evSphs.get(i));
        if (relevant.contains("unknown"))
                relevant.remove("unknown"); //unknown sphere must not be considered
        EventDescription template = ev.createtemplate(true, false,//application, explicitEvent
                false, false, false, false, false, //processed, eventName, user, spheres, estimatedSpheres,
                false, false, false, //time, eventId, activity
                false, false, false, false, //userGroup, dataId, sender, receiver
                false, false, false, // correlationId, sessionId, destinatario,
                ev.getParameterNames()); // parameterNames
        ArrayList<Integer> indexes = new ArrayList();
        for (int i=0; i<eventList.size(); i++){//computes the intersection with previous events
            EventDescription oldEv = eventList.get(i);
            if (oldEv.compatibleWith(template)) { // similar to template
                //System.err.println("OLDEV: " + oldEv);
                indexes.add(new Integer(i));
                ArrayList<String> oldSpheres = oldEv.getRelevantSpheres();
                for (int j=0; j<relevant.size(); j++) {
                    String sph = relevant.get(j);
                    if (!oldSpheres.contains(sph))
                        relevant.remove(sph);
                }
            }
        }
        ev.setRelevantSpheres(relevant); //NB: relevant might be []
        for (int i=0; i<indexes.size(); i++) {
            int k = indexes.get(i).intValue();
            eventList.get(k).setRelevantSpheres(relevant);
        }
    }

    // Attempts to reduce the relevantSpheres list on a history basis
    public static void disambiguateEventList(ArrayList<EventDescription> eventList) {
        for (int i=0; i<eventList.size(); i++) {
            EventDescription pivot = eventList.get(i);
            if (pivot.getRelevantSpheres().size()>1) // ambiguous event
                historyEventDisambiguation(pivot, eventList);
        }
    }

            // given an event re-published by an application, updates the event history
    public static void modifyEvent(EventDescription ev, ArrayList<EventDescription> eventList) {
        EventDescription template= ev.createtemplate(true, false,//application, explicitEvent
                false, true, true, true, true, //processed, eventName, user, spheres, estimatedSpheres,
                false, false, false, //time, eventId, activity
                false, false, false, false, //userGroup, dataId, sender, receiver
                false, false, true, // correlationId, sessionId, destinatario,
                ev.getParameterNames()); // parameterNames
        for (int i=eventList.size()-1; i>=0; i--) {
            EventDescription oldEv = eventList.get(i);
            if (oldEv.compatibleWith(template)) {
                oldEv.setRelevantSpheres(ev.getRelevantSpheres());
                oldEv.setProcessed(ev.getProcessed());
            }
        }
    }

    public static void addGroup(String gName, ArrayList<String> members,
                            HashMap<String, ArrayList<String>> groupsTable,
                            HashMap<String, ArrayList<String>> userSpheresTable) {
        if (groupsTable.containsKey(gName))
                System.err.println("EventUtilities: group " + gName +
                                   " is already existing!");
        else {
            if (members==null)  {
                System.err.println("EventUtilities: member list of " + gName + " is null!");
                members = new ArrayList();
            }
            groupsTable.put(gName, members);
            for (int i=0; i<members.size(); i++) { // updates the userSpheresTable
                addUserSphere(members.get(i), gName, userSpheresTable);
            }
        }
    }

    public static void deleteGroup(String gName, HashMap<String, ArrayList<String>> groupsTable,
                            HashMap<String, ArrayList<String>> userSpheresTable) {
        if (groupsTable.containsKey(gName)) {
            ArrayList<String> users = groupsTable.get(gName);
            for (int i=0; i< users.size(); i++) {
                removeUserSphere(users.get(i), gName, userSpheresTable);
            }
            groupsTable.remove(gName);
        }
        else System.err.println("EventUtilities: group " + gName + " does not exist!");
    }

    private static boolean addUserSphere(String userID, String sph,
                                         HashMap<String, ArrayList<String>> userSpheresTable) {
        ArrayList<String> spheres = null;
        if (!userSpheresTable.containsKey(userID)) { // adds user to groups table, if does not exist
            spheres = new ArrayList();
            spheres.add("unknown");// initializes the sphere list of userID with "unknown"
            userSpheresTable.put(userID, spheres);
        }
        else spheres = userSpheresTable.get(userID);
        if (sph!=null && !spheres.contains(sph)) {
            spheres.add(sph);// adds the sphere
            return true;
        }
        else {
            System.err.println("EventUtilities: " + userID + "- sphere null or attempt to add an existing sphere!");
            return false;
        }
    }

    private static boolean removeUserSphere(String userID, String sph,
                                            HashMap<String, ArrayList<String>> userSpheresTable) {
        if (userSpheresTable.containsKey(userID)) {
            ArrayList<String> spheres = userSpheresTable.get(userID);
            if (spheres.contains(sph))
                spheres.remove(sph);
            return true;
        }
        else {
            System.err.println("EventUtilities: sphere " + sph + " not listed in userSpheresTable of " + userID);
            return false;
        }
    }

    public static UserAgt getUserAgent(ArrayList<UserAgt> userAgents, String userID) {
        UserAgt userAgent = null;
        for (int i=0; i<userAgents.size(); i++) 
            if (userID.equalsIgnoreCase(userAgents.get(i).getUserID()))
                userAgent = userAgents.get(i);
        return userAgent;
    }

     public static NotificationMgr getNotificationMgr(ArrayList<NotificationMgr> notifs, String userID) {
        NotificationMgr notif = null;
        for (int i=0; i<notifs.size(); i++) 
            if (userID.equalsIgnoreCase(notifs.get(i).getUserID()))
                notif = notifs.get(i);
        return notif;
    }

                            // creates a sphere in the service cloud
                            // and feeds the user agents, etc., accordingly
    public static void createUserSphere(String sphName, ArrayList<String> members,
                                        EventAnalyzer evAn, UserAgt ua,
                                        NotificationMgr notif) {
        Sphere sph = new Sphere(sphName, members);
        evAn.addGroup(sph);
        ua.addGroup(sph);
        notif.addGroup(sph);
    }

                            // creates a chat client for each cloud user (for sending IMs)
    public static HashMap<String, ChatClient> createChatClients(HashMap<String, String> passwords) {
        HashMap<String, ChatClient> chatClients = new HashMap();
        Set accounts = passwords.keySet();
        Iterator it = accounts.iterator();
        while (it.hasNext()) {
            String account = (String)it.next(); // google account
            ChatClient chClient = new ChatClient();
            chatClients.put(account, chClient);
        }
        return chatClients;
    }

                            // opens chat connection for each cloud user
    public static void connectChatClients(HashMap<String, ChatClient> chatClients) {
        Set accounts = chatClients.keySet();
        Iterator it = accounts.iterator();
        while (it.hasNext()) {
            String account = (String)it.next(); // google account
            ChatClient ch = chatClients.get(account);
            String password = ConfigurationSettings.passwords.get(account); // google pwd
            try {
                ch.login(account, password);
             } catch (Exception e) {System.err.println("EventUtilities: Failed chatClient login: " +
                     account + "; " + e.toString());}
        }
    }

                    // disconnects all the chat clients for sending messages
    public static void disconnectChatClients(HashMap<String, ChatClient> chatClients) {
        Set accounts = chatClients.keySet();
        Iterator it = accounts.iterator();
        while (it.hasNext()) {
            String account = (String)it.next(); // google account
            ChatClient ch = chatClients.get(account);
            try {
                ch.disconnect();
             } catch (Exception e) {System.err.println("EventUtilities: Failed chatClient logout: " +
                     account + "; " + e.toString());}
        }
    }


}//end class

