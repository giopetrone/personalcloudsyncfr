package myCLasses;

/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

import java.util.ArrayList;
import java.util.HashMap;

/**
 *
 * @author liliana
 */
public class EventAnalyzer {

    private ArrayList<EventDescription> eventHistory;
    int count; // counter used for disambiguating the eventHistory cyclically;
    final int CYCLE = 5; // threashold for eventList maintenance
    // for each defined group, this specifies the group members
    private HashMap<String, ArrayList<String>> groupsTable;
    // for each user, specifies the list of spheres (groups) he belongs to
    // this is computed by analyzing the groupsTable at each modification
    private HashMap<String, ArrayList<String>> userSpheresTable;

    public EventAnalyzer() {
        eventHistory = new ArrayList();
        groupsTable = new HashMap();
        userSpheresTable = new HashMap();
    }

       // processo di distribuzione degli eventi, da ricreare con il publish e subscribe:
    // tutti tranne EventAnalyzer si sottoscrivono ad eventi gia' processati da qualcuno
    // cioe' con campo process!="no".
    // EventAnalyzer si sottoscrive a "no" per fare la prima disambiguazione
    // degli eventi e li ripubblica con campo processed a "byContext".
    // EventAnalyser si sottoscrive a "byUserAgt" per modificare i propri eventi
    // sulla base delle informazioni ulteriormente prodotte da UserAgt

    // needs to be synchronized because events are generated in parallel
    // --> can generate concurrentModificationException
    public synchronized void publishEvent(EventDescription event,
                                ArrayList<UserAgt> userAgents,
                                ArrayList<NotificationMgr> notifManagers) {

        UserAgt uActor = EventUtilities.getUserAgent(userAgents, event.getUser());
                            // contextBased disambiguation of event
                            // NB: for actors!=userID the uActor is null
        addEvent(event, uActor);
                                    // feeding the user agents BEFORE Notif managers
                                    // in order to react to possible focus change
        for (int i=0; i< userAgents.size(); i++) {
            boolean focusChange = userAgents.get(i).addEvent(event);
            if (focusChange)
                notifManagers.get(i).sendNotificationSummary();
        }
                            // feeding the Notification Managers
        for (int i=0; i< notifManagers.size(); i++) 
            notifManagers.get(i).addEvent(event);
    }


    public ArrayList<EventDescription> getEventHistoryAsString() {
        return eventHistory;
    }

    // adds a new group to the group table and updates the
    // userSpheresTable accordingly
    public void addGroup(Sphere sph) {
        EventUtilities.addGroup(sph.getName(), sph.getMembers(),
                                groupsTable, userSpheresTable);
    }

    // removes a group from the group table and updates the
    // userSpheresTable accordingly
    public void deleteGroup(String gName) {
        EventUtilities.deleteGroup(gName, groupsTable, userSpheresTable);
    }

    public String getGroupsTableAsString() {
        return groupsTable.toString();
    }

    public String getUserSpheresTableAsString() {
        return userSpheresTable.toString();
    }

    
    // analyzes an event and tries to disambiguate the reference spheres, if needed.
    // If the source application handles spheres, the event is not disambiguated
    // because the presence of multiple spheres/destination users is interpreted
    // as wanted by the user.
    // This method returns the hopefully disambiguated event.
    public synchronized void addEvent(EventDescription ev, UserAgt uActor) {
        if (ev.getProcessed().equalsIgnoreCase("no")) { // new event - analyze and publish again
            ev.setProcessed("byContext"); // classify event as processed by context
            if (ev.getSpheres().size()==0) // ev needs to be disambiguated
                disambiguatedEvent(ev, uActor);
            eventHistory.add(ev);
            count++;
            if (count >= CYCLE) {
                EventUtilities.disambiguateEventList(eventHistory);
                count = 0;
            }
        }  /* else if (processed.equalsIgnoreCase("byUserAgt")) { // MAI!!!
            ev = new EventDescription(event.getApplication(), event.getExplicitEvent(),
                    processed, event.getUser(), event.getUser(),
                    destinatari, spheres, event.getTime(), event.getParameters());
            EventUtilities.modifyEvent(ev, eventHistory);
        } */
        // ignore all the other events (processed "byContext", etc.)
    }

    private void disambiguatedEvent(EventDescription ev, UserAgt uActor) {
        ArrayList<String> destinatari = ev.getDestinatari();
        ArrayList<String> rSphs = new ArrayList(); // relevant spheres of event
        if (destinatari.size()!=0) {
            ArrayList<String> userSpheres = userSpheresTable.get(ev.getUser());
                      // add user spheres having the same members as destinatari
            if (userSpheres!=null) {
                for (int i=0; i<userSpheres.size(); i++) {
                    String sph = userSpheres.get(i);
                    ArrayList<String> members = groupsTable.get(sph);
                    if (!sph.equalsIgnoreCase("unknown") &&
                            EventUtilities.equalSets(destinatari, members))
                        rSphs.add(sph);
                }
            }
            if (rSphs.size()>0) { // disambiguation failed
                        // -> attempt to restrict eSphs with last focus of user
                        // CONTINUITY HYPOTHESIS
                ArrayList<String> lastFocus = new ArrayList();
                if(uActor!=null) // se actor!=userID uActor e' null
                    lastFocus = uActor.getLastFocus();
                ArrayList<String> inters = EventUtilities.getIntersection(rSphs, lastFocus);
                if (inters.size()>0)
                    rSphs = inters;
            }
        ev.setRelevantSpheres(rSphs);
        }
    }

    public void clear() {
        eventHistory = new ArrayList();
        count = 0;
    }

}// end class


