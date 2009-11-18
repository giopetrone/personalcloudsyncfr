/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package appsusersevents.client;

import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;

/**
 *
 * @author liliana
 */
public class EventUtilities {

            // matchTable: specification of the parameters for identifying redundant events
    private static HashMap<String, HashMap<String, ArrayList<String>>> eventMatchTable = setEventMatchTable();

    // for each application, specifies if it explicitly handles spheres or not
    private static HashMap<String, String> applicationsTable = setApplicationsTable();

    public static HashMap<String, String> setApplicationsTable() {
        HashMap<String, String> table = new HashMap();
        table.put("CommonCalendar", "yes");
        table.put("GroupMgr", "yes");
        table.put("SurveyMgr", "yes");
        table.put("GoogleDocs", "no");
        return table;
    }

    public static boolean applicationHandlesSpheres(String application) {
        String answer = "no";
        if (applicationsTable.containsKey(application))
                answer = applicationsTable.get(application);
        else System.out.println("EventUtilities: unknown application: " + application);
        if (answer.equalsIgnoreCase("yes"))
            return true;
        else return false;
    }

    public static HashMap<String, HashMap<String, ArrayList<String>>> getEventMatchTable() {
        return eventMatchTable;
    }

    //sets the matchTable to be used for identifying redundant events
    //for each application, for each event type, it specifies the relevant parameters to be checked
    public static HashMap<String, HashMap<String, ArrayList<String>>> setEventMatchTable() {
        HashMap<String, HashMap<String, ArrayList<String>>> table = new HashMap();
                                            // CommonCalendar
        HashMap<String, ArrayList<String>> commonCalendar = new HashMap();
        ArrayList date = new ArrayList();
        date.add("date");
        commonCalendar.put("MeetingProposal", date);
        commonCalendar.put("MeetingConfirmation", date);
                                            // GroupMgr
        HashMap<String, ArrayList<String>> groupMgr = new HashMap();
        ArrayList<String>groupName = new ArrayList();
        groupName.add("groupName");
        groupMgr.put("MembershipProposal", groupName);
        groupMgr.put("GroupCreated", groupName);
        groupMgr.put("GroupDeleted", groupName);
         groupMgr.put("GroupModified", groupName);
                                            // SurveyMgr
        HashMap<String, ArrayList<String>> surveyMgr = new HashMap();
        ArrayList<String> noParameters = new ArrayList();
        surveyMgr.put("MeetingAnswer", noParameters);
        surveyMgr.put("MembershipAnswer", noParameters);
                                            // GoogleDocs
        HashMap<String, ArrayList<String>> googleDocs = new HashMap();
        ArrayList<String>docName = new ArrayList();
        docName.add("docName");
        googleDocs.put("DocCreated", docName);
        googleDocs.put("DocUpdated", docName);
        googleDocs.put("DocRemoved", docName);
                                            // set table
        table.put("CommonCalendar", commonCalendar);
        table.put("GroupMgr", groupMgr);
        table.put("SurveyMgr", surveyMgr);
        table.put("GoogleDocs", googleDocs);
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
                    false, false, false, true, //tab, time, eventId, activity
                    false, false, false, false, //userGroup, dataId, sender, receiver
                    false, false, false, // correlationId, sessionId, destinatario,
                    parNames);
            //System.out.println("PIVOT: " + pivot + "; position: " + i);
            for (int j=i-1; j>=0; j--) {    // removes redundant events leaving the
                EventDescription ev = events.get(j); // most recent one
                //System.out.println("EVENTO: " + ev);
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
                false, false, false, false, //tab, time, eventId, activity
                false, false, false, false, //userGroup, dataId, sender, receiver
                false, false, false, // correlationId, sessionId, destinatario,
                ev.getParameterNames()); // parameterNames
        ArrayList<Integer> indexes = new ArrayList();
        for (int i=0; i<eventList.size(); i++){//computes the intersection with previous events
            EventDescription oldEv = eventList.get(i);
            if (oldEv.compatibleWith(template)) { // similar to template
                //System.out.println("OLDEV: " + oldEv);
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
                false, false, false, false, //tab, time, eventId, activity
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
                System.out.println("EventUtilities: " + gName + "- attempt to create an existing group in the group table!");
        else {
            if (members==null)  {
                System.out.println("EventUtilities: " + gName +  " has a null list of members!");
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
        else System.out.println("EventUtilities: " + gName + "- attempt to delete a non existing group from the group table!");
    }

    public static boolean addUserSphere(String userID, String sph,
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
            System.out.println("EventUtilities: " + userID + "- sphere null or attempt to add an existing sphere!");
            return false;
        }
    }

    private static boolean removeUserSphere(String userID, String sph,
                                            HashMap<String, ArrayList<String>> userSpheresTable) {
        if (userSpheresTable.containsKey(userID)) { // adds user to groups table, if does not exist
            ArrayList<String> spheres = userSpheresTable.get(userID);
            if (spheres.contains(sph))
                spheres.remove(sph);
            return true;
        }
        else {
            System.out.println("EventUtilities: attempt to remove sphere " + sph + " not listed in the userSpheresTable of " + userID);
            return false;
        }
    }

}//end class