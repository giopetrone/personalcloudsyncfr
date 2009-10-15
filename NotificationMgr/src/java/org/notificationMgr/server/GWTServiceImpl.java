/*
 * GWTServiceImpl.java
 *
 * Created on June 29, 2009, 11:42 AM
 *
 * To change this template, choose Tools | Template Manager
 * and open the template in the editor.
 */
package org.notificationMgr.server;

import appsusersevents.client.CloudUsers;
import appsusersevents.client.EventDescription;
import appsusersevents.client.EventUtilities;
import appsusersevents.client.SingleUser;
import com.google.gwt.user.server.rpc.RemoteServiceServlet;
import giga.GigaListener;
import giga.Subscription;
import googlecontacts.ContactCall;
import googletalkclient.ChatClient;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Set;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpSession;
import org.notificationMgr.client.GWTService;

/**
 *
 * @author giovanna
 */
public class GWTServiceImpl extends RemoteServiceServlet implements
        GWTService {

    HashMap<String, GigaListener> sessionListeners = new HashMap();
    //  GigaListener listener = null;
    //GigaListener listener = new GigaListener();
    //   boolean addedFilter = false;
    ArrayList addedFilterList = new ArrayList(); // lista di utenti gia' sottoscritti
    HashMap<String, ArrayList<String>> eventSubscrData = new HashMap();  // chiave = applicazione e value = lista di eventi a cui sottoscriversi
    ChatClient chClient = new ChatClient();
    HashMap<String, String> logPasswdData = new HashMap();
    CloudUsers cloudUsers = new CloudUsers();
    //vecchia versione 14-10-09
    //   HashMap<String, ArrayList<EventDescription>> usersData = new HashMap();  // chiave = destinatario e value = lista di eventi che i filtri fanno passare (per quello user)
// nuova versione
    String me = "";
    ArrayList<EventDescription> notifiche = new ArrayList();
    //lili
    private String currentTab; // active user tab (in focus)
    private ArrayList<EventDescription> eventList; //list of events to be delivered to the user
    int count; // counter used for disambiguating the eventList cyclically;
    final int CYCLE = 5; // threashold for eventList maintenance
    //  spheres  of the individual user
    ArrayList<String> userSpheres = new ArrayList();
    private HashMap<String, ArrayList<EventDescription>> notificationLists = new HashMap();
    // notification lists for each sphere the user is involved in
    // presTable: specification of the parameters for presentation on browser/minibrowser
    private HashMap<String, HashMap<String, ArrayList<String>>> presentationTable = new HashMap();

// end lili
    @Override
    public void init() {
        System.out.println("SONO IN init ");
        logPasswdData.put("gio.petrone@gmail.com", "mer20ia05");
        logPasswdData.put("annamaria.goy@gmail.com", "tex_willer");
        presentationTable = setPresentationTable();
        userSpheres.add("unknown");
        notificationLists.put("unknown", new ArrayList()); //add notification list for ambiguous notifications

        EventUtilities.setEventMatchTable();//sets the match table for identifying redundant events
        ArrayList aL = new ArrayList();
        aL.add("MeetingProposal");
        aL.add("MeetingConfirmation");
        eventSubscrData.put("CommonCalendar", aL);
        ArrayList aL2 = new ArrayList();
        aL2.add("MeetingAnswer");
        eventSubscrData.put("SurveyMgr", aL2);

        ArrayList aL3 = new ArrayList();
        aL3.add("MembershipProposal");
        aL3.add("GroupCreated");
        aL3.add("GroupDeleted");
        aL3.add("GroupModified");
        eventSubscrData.put("GroupMgr", aL3);


        ArrayList aL4 = new ArrayList();
        aL4.add("DocCreated");
        aL4.add("DocUpdated");
        aL4.add("DocRemoved");
        eventSubscrData.put("GoogleDocs", aL4);
    }
    // occorre aggiungere i metodi di get con pattern matching

    private GigaListener getListener() {
        String sId = getSession().getId();
        GigaListener ret = sessionListeners.get(sId);
        //       System.out.println("sessionListeners SIZE = " + sessionListeners.size());
        //      System.out.println("sessionListeners: sessionId =  " + sId);
        if (ret == null) {
            ret = new GigaListener(false, false);
            sessionListeners.put(sId, ret);
        }
        //    System.out.println("sessionListeners: ret =  " + ret);
        return ret;
    }

    private HttpSession getSession() {
        // Get the current request and then return its session
        return this.getThreadLocalRequest().getSession();
    }

    private String getParameter(String paramname) {
        // Get the current request and then return its session
        String paramValue = "";

        HttpServletRequest req = this.getThreadLocalRequest();

//         System.out.println("@@@@@@@@@@@@ queryString " + req.getQueryString());
//          System.out.println("@@@@@@@@@@@@ queryRequestUrl " + req.getRequestURL());

//         System.out.println("@@@@@@@@@@@@ method " + req.getMethod());
        paramValue = req.getParameter(paramname);
//          System.out.println("@@@@@@@@@@@@ parameter name " + paramname);
//            System.out.println("@@@@@@@@@@@@ parameter " + paramValue);
        return paramValue;

    }

    public EventDescription[] getEvents(String userName) {
        //TEMP !!!! x prova parametri servlet !!!!
        System.out.println("******NotificationMgr dopo getParameter " + this.getParameter("p"));
        // FINE TEMP
        EventDescription[] tmp = null;
        if (userName != null) {
            //           System.out.println("SONO IN getTEVENTS subsc MeetingP e username =  " + userName);

            if (!alreadySubscr(userName)) {
                Set<String> apps = eventSubscrData.keySet();
                Iterator<String> iter = apps.iterator();
                while (iter.hasNext()) {
                    String app = iter.next();
                    ArrayList<String> events = eventSubscrData.get(app);
                    for (String ev : events) {
                        subscribeTo(ev, userName, app); // user arrivera' dal Gadget+iGooglepage
                    }
                }
                // stampe di debug per multiple session
                //    HttpSession sess = this.getThreadLocalRequest().getSession();
                //     System.out.println("SESSIONE id = : " + sess.getId());
                addedFilterList.add(userName);
            }
            tmp = getListener().getEvents();
            if (tmp == null) {
                System.out.println("NOTIF getEvents tmp NULL");
            } else if (tmp.length == 0) {
                System.out.println(" NOTIF getEvents tmp size 0");
            } else {
                // TEMP indice 0 : assumiamo che tutti gli eventi che arrivano con getEvents(), abbiano lo stesso destinataio (plausibile per come sono costruiti i filtri)
                //    String dest = tmp[0].getDestinatario();
                //  ArrayList aL = usersData.get(dest);  // eventi da mostrare all'utente
                for (int i = 0; i < tmp.length; i++) {
                    // generalizzare controlli/nome degli eventi
                    if (tmp[i].getEventName().equals("MeetingAnswer") || tmp[i].getEventName().equals("MembershipSurveyAnswer")) {
                        removeEvent(tmp[i].getCorrelationId(), notifiche);
                        removeEvent(tmp[i].getCorrelationId(), tmp);
                    } else { // per costruire messagio x GTalk
                        notifiche.add(tmp[i]);
                        String evName = tmp[i].getEventName();
                        //  String destName = tmp[i].getDestinatario();
                        String msg = evName;
                        if (tmp[i].getEventName().equals("MeetingProposal")) {
                            msg = " to participate  : " + tmp[i].getParameter("Date");
                            // msg = msg + "  Please connect to  http://localhost:8080/SurveyMgr/";
                            msg = msg;
                        } else if (tmp[i].getEventName().equals("MembershipProposal")) {
                            //     System.out.println("++++NOtification getEvents evName del IM = " + evName);
                            msg = " to join  : " + tmp[i].getParameter("groupName");
                            // msg = msg + "  Please connect to  http://localhost:8080/SurveyMgr/";
                            msg = msg;
                        } else if (tmp[i].getEventName().equals("GroupCreated")) {
                            msg = ": " + tmp[i].getParameter("groupName");
                        } else if (tmp[i].getEventName().equals("GroupModified")) {
                            msg = ": " + tmp[i].getParameter("groupName");
                        } else if ((tmp[i].getEventName().equals("GroupDeleted"))) {
                            msg = ": " + tmp[i].getParameter("groupName");
                        } else if ((tmp[i].getEventName().equals("MeetingConfirmation"))) {
                            msg = ": " + tmp[i].getParameter("Date");
                        } else if ((tmp[i].getEventName().equals("DocCreated"))) {
                            msg = tmp[i].getParameter("docName") + " created  : " + tmp[i].getParameter("Date") + " document available at : " + tmp[i].getParameter("docLink");
                        } else if ((tmp[i].getEventName().equals("DocUpdated"))) {
                            msg = tmp[i].getParameter("docName") + " updated  : " + tmp[i].getParameter("Date") + " document available at : " + tmp[i].getParameter("docLink");
                        } else if ((tmp[i].getEventName().equals("DocRemoved"))) {
                            msg = tmp[i].getParameter("docName") + " removed  : " + tmp[i].getParameter("date");
                        }
                        try {
                            // al post della login ricavare la login di destName
                            //    chClient.sendGTalkMsg("annamaria.goy@gmail.com", iceMgrLogin, iceMgrPasswd, msg, false);
                            //  chClient.sendGTalkMsg(destName, userName, logPasswdData.get(userName), msg, false);
                            // VALE PER TUTTE LE NOTIFICHE DA DIVERSE APPS ?
                            System.out.println("++++NOtification getEvents evName del IM  msg = " + msg);
                            //System.out.println("++++NOtification getEvents destName del IM = " + destName);
                            chClient.sendGTalkMsg(me, tmp[i].getUser(), logPasswdData.get(tmp[i].getUser()), msg, false);
                        } catch (Exception e) {
                            System.out.println("ECCEZIONE chat");
                        }
                    }

                    /*  System.out.println("!!!!!!!!!!!!!!!!!!!!!! getEvents tmp eventName = " + tmp[i].getEventName());
                    System.out.println("!!!!!!!!!!!!!!!!!!!!!! getEvents tmp user = " + tmp[i].getUser());
                    System.out.println("!!!!!!!!!!!!!!!!!!!!!! getEvents tmp destinatario = " + tmp[i].getDestinatario());
                    System.out.println("!!!!!!!!!!!!!!!!!!!!!! getEvents tmp eventId = " + tmp[i].getEventId());*/
                }
                //     printUsersData();
/*
                System.out.println("NOTIFICATION : getEvents tmp :  ");
                for (int i = 0; i <  tmp.length; i++) {
                System.out.println(tmp[i].getDescription());
                }
                 */
            }
        }
        return tmp;

    }

// si chiama putEvents ma e' identica ad un publishEvent
    public void putEvents(EventDescription[] events, String userName) {
        if (userName != null) {
            System.out.println("SONO IN PUTEVENTS subsc MeetingP e username =  " + userName);
            if (!alreadySubscr(userName)) {
                //       if (!addedFilter) {
                //le due subscribe con domanda sono solo x debug con GigaMgrUI
                //  subscribeTo("domanda1", "gio");
                // subscribeTo("domanda2", "gio");

                subscribeTo("MeetingProposal", userName, "CommonCalendar"); // user arrivera' dal Gadget+iGooglepage
                // addedFilter = true;
                addedFilterList.add(userName);
            }

        }
        getListener().putEvents(events);
        //  listener.putEvents(events);
    }
// method e method2 :metodi di prova da sostituire con i veri metodi della app

// metodo di prova per solo "RMI"
    public String myMethod(
            String s) {
        // Do something interesting with 's' here on the server.

        return "Server RMI (no GIGA) says: " + s;
    }
// metodo di prova per Giga

    public String myMethod2(
            String s) {
        // Do something interesting with 's' here on the server.
   /*     if (listener != null) {
        getEvents();
        }
         * */
        return "Server GIGA says 222: " + s;
    }

// si chiama sendEventToGiga ma e' identica a SurveyMgr ma, se la si utilizzasse occorre modificarla come GroupMgr 18-6-09
//
    public String sendEventToGiga(String questionId, String answer, String user) {
        GigaListener listener = getListener();
        //   ArrayList<EventDescription> questions = usersData.get(user);  //il parametro user e' lo user corrente (il "me" della UI)
        if (listener != null) {
            boolean found = false;
            if (notifiche == null) {
                System.out.println("NofiMgr sendEventToGiga : questions is NULL ");
            } else if (notifiche.size() == 0) {
                System.out.println("NotifMgr sendEventToGiga : questions  lungh 0");
            } else {
                int i = 0;
                int ind = 0;
                while (!found && i < notifiche.size()) {
                    if ((notifiche.get(i)).getEventId().equals(questionId)) {
                        found = true;
                        ind =
                                i;
                    }

                    i++;
                }

                EventDescription[] events = new EventDescription[1];
                events[0] = (notifiche.get(ind)).copyEd();   // copia oggetto EventDescription
                events[0].setEventName("MeetingAnswer");
                events[0].setApplication("SurveyMgr");
                String userTmp = events[0].getUser();
                // events[0].setUser(events[0].getDestinatario());
                events[0].setUser(me);
                events[0].removeDestinatario(me);
                events[0].addDestinatario(userTmp);
                events[0].setParameter("answer", answer);
                //     events[0].getParameters().add(0, answer);
                // System.out.println("NotifMgr SendEveTOGIga: parameters = " + events[0].getParameters());
                // System.out.println("NotifMgr SendEveTOGIga: size di events  = " + events.length);
                removeEvent(notifiche.get(ind).getEventId(), notifiche);
                //    printUsersData();
                listener.putEvents(events);

            }

        }
        return "inviato evento a GIGA ";
    }

    private boolean removeEvent(String corrId, ArrayList<EventDescription> aList) {
        int i = 0;
        boolean trovato = false;
        EventDescription ed = null;
        while (i < aList.size() && !trovato) {
            ed = aList.get(i);
            if (ed.getCorrelationId().equals(corrId)) {
                trovato = true;
                aList.remove(i);
            }

            i++;
        }

        return trovato;
    }

    private boolean removeEvent(String corrId, EventDescription[] a) {
        int i = 0;
        boolean trovato = false;
        EventDescription ed = null;
        while (i < a.length && !trovato) {
            ed = a[i];
            if (ed.getCorrelationId().equals(corrId)) {
                trovato = true;
            }

            i++;
        }

        for (int j = i--; j <
                a.length; j++) {
            a[j] = a[j++];
        }

        return trovato;
    }

//    private void printUsersData() {
//        System.out.println("-------- stampa usersData in NOTIFICATION : ----");
//        Set<String> users = usersData.keySet();
//        Iterator<String> iter = users.iterator();
//        while (iter.hasNext()) {
//            String user = iter.next();
//            ArrayList<EventDescription> arL = usersData.get(user);
//            System.out.println("user = " + user + " data = ");
//            for (int i = 0; i <
//                    arL.size(); i++) {
//                System.out.println(arL.get(i).getDescription() + "  ---  ");
//            }
//
//        }
//        System.out.println("-------- fine stampa usersData in NOTIFICATION : ----");
//    }
// modificare con : evento (s) a cui ci si sottoscrive + sottoscrittore, per esempio "GigaMgrUI"
    private String subscribeTo(String evName, String user) {
        // invia a Giga il nome dell'evento a cui l'utente si vuole sottoscrivere
        Subscription f = new Subscription();
        System.out.println("ho fatto la new di Subscription piccola");
        GigaListener listener = getListener();
        if (listener != null) {
            EventDescription evDescr = new EventDescription(evName);
            evDescr.setEventName(evName);
            // evDescr.setUser(user);
            f.setDesc(evDescr);
            listener.addFilter(f);
        }

        return "inviato evento a cui ci si sottoscrivere a GIGA " + evName;
    }

// vera
    private String subscribeTo(String evName, String dest, String app) {
        // invia a Giga il nome dell'evento a cui l'utente si vuole sottoscrivere
        Subscription f = new Subscription();
        System.out.println("ho fatto la new di Subscription grande");
        System.out.println("evName = " + evName + "  dest = " + dest + " app = " + app);
        EventDescription evDescr = new EventDescription(evName);
        evDescr.setEventName(evName);
        //      evDescr.setDestinatario(dest);
        evDescr.addDestinatario(dest);
        evDescr.setApplication(app);
        f.setDesc(evDescr);
        getListener().addFilter(f);

        return "inviato evento a cui ci si sottoscrivere a GIGA " + evName;
    }
// DA SOSTITUIRE CON persistanza DB

    private boolean alreadySubscr(String userName) {
        boolean res = false;
        res =
                addedFilterList.contains(userName);
        return res;
    }

    public boolean validateUser(String name, String pwd) {
        //    return new ServerToClient().validateUser(name,pwd);

        ContactCall cC = new ContactCall(name, pwd);
        boolean val = cC.validate(name, pwd);
        System.out.print("sono in SURVEY validate = " + val);

        return val;
    }

    public String authenticate(String s) {
        String userEmail = "";
        SingleUser sU = cloudUsers.getUser(s);
        //src=   System.out.println("AUTHENTICATE " + s);
        if (sU != null) {
            userEmail = sU.getMailAddress();
            me = userEmail;
        } else {
            System.out.println("singleUSer NULL");
        }
        return userEmail;
    }
    // lili methods
    //for each application, for each event type, it specifies the relevant parameters to be checked
// forse va nel client

    private static HashMap<String, HashMap<String, ArrayList<String>>> setPresentationTable() {
        HashMap<String, HashMap<String, ArrayList<String>>> table = new HashMap();
        HashMap<String, ArrayList<String>> commonCalendar = new HashMap();
        ArrayList date = new ArrayList();
        date.add("date");
        commonCalendar.put("MeetingProposal", date);
        commonCalendar.put("MeetingConfirmation", date);

        HashMap<String, ArrayList<String>> groupMgr = new HashMap();
        ArrayList<String> groupName = new ArrayList();
        groupName.add("groupName");
        groupMgr.put("MembershipProposal", groupName);
        groupMgr.put("GroupCreated", groupName);
        groupMgr.put("GroupDeleted", groupName);

        HashMap<String, ArrayList<String>> surveyMgr = new HashMap();
        ArrayList<String> noParameters = new ArrayList();
        surveyMgr.put("MeetingAnswer", noParameters);
        surveyMgr.put("MembershipAnswer", noParameters);

        HashMap<String, ArrayList<String>> googleDocs = new HashMap();
        ArrayList<String> docFields = new ArrayList();
        docFields.add("docName");
        docFields.add("docLink");
        docFields.add("date");
        googleDocs.put("DocCreated", docFields);
        googleDocs.put("DocUpdated", docFields);
        ArrayList<String> docFields0 = new ArrayList();
        docFields0.add("docName");
        docFields0.add("date");
        googleDocs.put("DocRemoved", docFields0);

        table.put("CommonCalendar", commonCalendar);
        table.put("GroupMgr", groupMgr);
        table.put("SurveyMgr", surveyMgr);
        table.put("GoogleDocs", googleDocs);
        return table;
    }

    // che farsene di processEvents ?  ex addEvents
    private void processEvents() {
        EventDescription[] procEvents = getListener().getEvents();
        EventDescription ev = null;
        for (int i = 0; i < procEvents.length; i++) {
            ev = procEvents[i];
            if (ev.getProcessed().equalsIgnoreCase("byContext")) { // event processed by EventAnalyzer
                eventList.add(ev);
                if (count >= CYCLE) {
                    EventUtilities.disambiguateEventList(eventList);
                    count = 0;
                }
            } else if (ev.getProcessed().equalsIgnoreCase("byUserAgt")) {
                EventUtilities.modifyEvent(ev, eventList);
                EventUtilities.disambiguateEventList(eventList);
            }
        }

    }

    // ex refreshNotificationLists  DA MODIFICARE
    public HashMap<String, ArrayList<EventDescription>> getProcessedEvents(String userName) {
        EventDescription[] procEvents = null;
        // DA FARE
        EventUtilities.cleanEventList(eventList);//cleans the eventList from redundant events
        for (int i = 0; i < userSpheres.size(); i++) {
            String sphere = userSpheres.get(i);
            ArrayList<EventDescription> notifList = notificationLists.get(sphere);
            if (notifList != null) {
                notifList.clear(); //resets the notification list
            }
        }
        for (int i = 0; i < eventList.size(); i++) {
            EventDescription ev = eventList.get(i);
            ArrayList<String> lists = assignEventToNotificationLists(ev, currentTab);
            for (int j = 0; j < lists.size(); j++) { // add event to all relevant notification lists
                String listName = lists.get(j);
                ArrayList<EventDescription> notifList = notificationLists.get(listName);
                if (notifList != null) {
                    notifList.add(ev);
                } else {
                    System.out.println("NotificationMgr " +
                            ": unexpected event - sphere: " + listName + "; EV: " + ev);
                    notifList = notificationLists.get("unknown");
                    notifList.add(ev);
                }
            }
        }
        return notificationLists;
    }

    // returns the notification list(s) where the event should be displayed:
    // either one specific list, or all the spheres, or all the relevantSpheres,
    // depending on the spheres/relevantSpheres fields, and on the current tab
    private ArrayList<String> assignEventToNotificationLists(EventDescription ev, String tab) {
        ArrayList<String> lists = new ArrayList();
        ArrayList<String> spheres = ev.getSpheres();
        ArrayList<String> relevantSpheres = ev.getRelevantSpheres();
        if (spheres.size() >= 1) // non ambiguous event --> notify in all specified lists
        {
            for (int i = 0; i < spheres.size(); i++) {
                lists.add(spheres.get(i)); // all event spheres
            }
        }
        if (spheres.size() == 0 && relevantSpheres.size() > 0) { // ambiguous event but
            if (relevantSpheres.contains(tab)) // there are estimated relevant spheres
            {
                lists.add(tab);     // put in relevantSphere corresponding to current tab
            } else {
                for (int i = 0; i < relevantSpheres.size(); i++) {
                    lists.add(relevantSpheres.get(i)); // all relevant spheres
                }
            }
        }
        if (lists.size() == 0) // the event has not been assigned to any notification list
        {
            lists.add("unknown"); // catch all
        }
        return lists;
    }
    // end lili
}
