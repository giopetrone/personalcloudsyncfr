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
    HashMap<String, ArrayList<EventDescription>> usersData = new HashMap();  // chiave = destinatario e value = lista di eventi che i filtri fanno passare (per quello user)
    HashMap<String, ArrayList<String>> eventSubscrData = new HashMap();  // chiave = applicazione e value = lista di eventi a cui sottoscriversi
    ChatClient chClient = new ChatClient();
    HashMap<String, String> logPasswdData = new HashMap();
       CloudUsers cloudUsers = new CloudUsers();

    @Override
    public void init() {
        System.out.println("SONO IN init ");
        logPasswdData.put("gio.petrone@gmail.com", "mer20ia05");
        logPasswdData.put("annamaria.goy@gmail.com", "tex_willer");
        // inizializzazione di userData, in futuro leggere gli utenti da users.xml
        usersData.put("gio.petrone@gmail.com", new ArrayList());
        usersData.put("sgnmrn@gmail.com", new ArrayList());
        usersData.put("marino@di.unito.it", new ArrayList());
        usersData.put("lg.petrone@gmail.com", new ArrayList());
        usersData.put("annamaria.goy@gmail.com", new ArrayList());
        usersData.put("fabrizio.torretta@gmail.com", new ArrayList());
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
         String paramValue ="";

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
                String dest = tmp[0].getDestinatario();
                ArrayList aL = usersData.get(dest);  // eventi da mostrare all'utente
                for (int i = 0; i < tmp.length; i++) {
                    // generalizzare controlli/nome degli eventi
                    if (tmp[i].getEventName().equals("MeetingAnswer") || tmp[i].getEventName().equals("MembershipSurveyAnswer")) {
                        removeEvent(tmp[i].getCorrelationId(), aL);
                        removeEvent(tmp[i].getCorrelationId(), tmp);
                    } else { // per costruire messagio x GTalk
                        aL.add(tmp[i]);
                        String evName = tmp[i].getEventName();
                        String destName = tmp[i].getDestinatario();
                        String msg = evName;
                        if (tmp[i].getEventName().equals("MeetingProposal")) {
                            msg = " to participate  : " + tmp[i].getParameter("Date");
                           // msg = msg + "  Please connect to  http://localhost:8080/SurveyMgr/";
                             msg = msg ;
                        } else if (tmp[i].getEventName().equals("MembershipProposal")) {
                            //     System.out.println("++++NOtification getEvents evName del IM = " + evName);
                            msg = " to join  : " + tmp[i].getParameter("groupName");
                           // msg = msg + "  Please connect to  http://localhost:8080/SurveyMgr/";
                             msg = msg ;
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
                            System.out.println("++++NOtification getEvents destName del IM = " + destName);
                            chClient.sendGTalkMsg(destName, tmp[i].getUser(), logPasswdData.get(tmp[i].getUser()), msg, false);
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
        ArrayList<EventDescription> questions = usersData.get(user);  //il parametro user e' lo user corrente (il "me" della UI)
        if (listener != null) {
            boolean found = false;
            if (questions == null) {
                System.out.println("sendEventToGiga : questions is NULL ");
            } else if (questions.size() == 0) {
                System.out.println("sendEventToGiga : questions  lungh 0");
            } else {
                int i = 0;
                int ind = 0;
                while (!found && i < questions.size()) {
                    System.out.println("sendEventToGiga : questions  eventId e questionId  = " + (questions.get(i)).getEventId() + " " + questionId);
                    if ((questions.get(i)).getEventId().equals(questionId)) {
                        found = true;
                        ind =
                                i;
                    }

                    i++;
                }

                EventDescription[] events = new EventDescription[1];
                events[0] = (questions.get(ind)).copyEd();   // copia oggetto EventDescription
                events[0].setEventName("MeetingAnswer");

                events[0].setApplication("SurveyMgr");
                String userTmp = events[0].getUser();
                System.out.println("SendEveTOGIga: user (prima)  = " + events[0].getUser());
                events[0].setUser(events[0].getDestinatario());
                System.out.println("SendEveTOGIga: user (dopo)  = " + events[0].getUser());
                System.out.println("SendEveTOGIga: destinatario  (prima) = " + events[0].getDestinatario());
                events[0].setDestinatario(userTmp);
                System.out.println("SendEveTOGIga: destinatario  (dopo) = " + events[0].getDestinatario());
                events[0].setParameter("answer", answer);
                //     events[0].getParameters().add(0, answer);
                System.out.println("SendEveTOGIga: parameters = " + events[0].getParameters());
                System.out.println("SendEveTOGIga: size di events  = " + events.length);
                removeEvent(questions.get(ind).getEventId(), questions);
                printUsersData();

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

    private void printUsersData() {
        System.out.println("-------- stampa usersData in NOTIFICATION : ----");
        Set<String> users = usersData.keySet();
        Iterator<String> iter = users.iterator();
        while (iter.hasNext()) {
            String user = iter.next();
            ArrayList<EventDescription> arL = usersData.get(user);
            System.out.println("user = " + user + " data = ");
            for (int i = 0; i <
                    arL.size(); i++) {
                System.out.println(arL.get(i).getDescription() + "  ---  ");
            }

        }
        System.out.println("-------- fine stampa usersData in NOTIFICATION : ----");
    }
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
        evDescr.setDestinatario(dest);
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
        if (sU != null)
         userEmail = sU.getMailAddress();
        else System.out.println("singleUSer NULL");
        return userEmail;
    }
}
