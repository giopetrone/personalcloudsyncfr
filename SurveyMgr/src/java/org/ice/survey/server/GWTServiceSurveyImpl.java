/*
 * GWTServiceSurveyImpl.java
 *
 * Created on March 18, 2009, 3:11 PM
 *
 * To change this template, choose Tools | Template Manager
 * and open the template in the editor.
 */
package org.ice.survey.server;

import appsusersevents.client.CloudUsers;
import appsusersevents.client.EventDescription;

import appsusersevents.client.SingleUser;
import com.google.gwt.user.server.rpc.RemoteServiceServlet;
import giga.GigaListener;
import giga.Subscription;
import googlecontacts.ContactCall;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Set;
import javax.servlet.http.HttpSession;
import org.ice.survey.client.GWTServiceSurvey;

/**
 *
 * @author giovanna
 */
public class GWTServiceSurveyImpl extends RemoteServiceServlet implements
        GWTServiceSurvey {

    HashMap<String, GigaListener> sessionListeners = new HashMap();
    ArrayList addedFilterList = new ArrayList(); // lista di utenti gia' sottoscritti   
    HashMap<String, ArrayList<String>> eventSubscrData = new HashMap();  // chiave = applicazione e value = lista di eventi a cui sottoscriversi
    CloudUsers cloudUsers = new CloudUsers();
    //vecchia versione
   // HashMap<String, ArrayList<EventDescription>> usersData = new HashMap();  // chiave = destinatario e value = lista di eventi (domande) che i filtri fanno passare (per quello user)
// nuova versione
    String me = "";
    ArrayList<EventDescription> domande = new ArrayList();


    @Override
    public void init() {
        // inizializzazione di userData, in futuro leggere gli utenti da users.xml,
        // l'arrayList conterra' la lista delle domande per l'utente
        //OLD 14-10-09
//        usersData.put("gio.petrone@gmail.com", new ArrayList());
//        usersData.put("sgnmrn@gmail.com", new ArrayList());
//        usersData.put("marino@di.unito.it", new ArrayList());
//        usersData.put("lg.petrone@gmail.com", new ArrayList());
//        usersData.put("annamaria.goy@gmail.com", new ArrayList());
        ArrayList aL1 = new ArrayList();
        aL1.add("MeetingProposal");
        eventSubscrData.put("CommonCalendar", aL1);
        ArrayList aL2 = new ArrayList();
        aL2.add("MembershipProposal");
        eventSubscrData.put("GroupMgr", aL2);

        // caricare utenti

    }
    // occorre aggiungere i metodi di get con pattern matching

    private GigaListener getListener() {
        String sId = getSession().getId();
        GigaListener ret = sessionListeners.get(sId);
        // System.out.println("sessionListeners: sessionId =  " + sId);
        if (ret == null) {
            ret = new GigaListener(false, false);
            sessionListeners.put(sId, ret);
        }
        return ret;
    }

    private HttpSession getSession() {
        // Get the current request and then return its session
        return this.getThreadLocalRequest().getSession();
    }

    public EventDescription[] getEvents(String userName) {

        EventDescription[] tmp = null;
        if (userName != null) {
            if (!alreadySubscr(userName)) {
                //le due subscribe con domanda sono solo x debug con GigaMgrUI
                //    subscribeTo("domanda1", "gio");
                //    subscribeTo("domanda2", "gio");
                Set<String> apps = eventSubscrData.keySet();
                Iterator<String> iter = apps.iterator();
                while (iter.hasNext()) {
                    String app = iter.next();
                    ArrayList<String> events = eventSubscrData.get(app);
                    for (String ev : events) {
                        subscribeTo(ev, userName, app); // user arrivera' dal Gadget+iGooglepage
                    }
                }
                // subscribeTo("MeetingProposal", userName, "CommonCalendar"); // user arrivera' dal Gadget+iGooglepage
                // stampe di debug per multiple session
                //   HttpSession sess = this.getThreadLocalRequest().getSession();
                //  System.out.println("SESSIONE id = : " + sess.getId());
                addedFilterList.add(userName);
            }
            tmp = getListener().getEvents();
            if (tmp == null) {
                System.out.println("SURVEYMGR getEvents tmp NULL");
            } else if (tmp.length == 0) {
                // System.out.println("SURVEYMGR getEvents tmp size 0");
            } else {
                System.out.println("!!!!!!!!!!!!!!!!!!!!!!SURVEY  getEvents tmp SIZE e = " + tmp.length);
                // TEMP indice 0 : assumiamo che tutti gli eventi che arrivano con getEvents(), abbiano lo stesso destinataio (plausibile per come sono costruiti i filtri)
            //    String dest = tmp[0].getDestinatario();
                for (int i = 0; i < tmp.length; i++) {
                    System.out.println("!!!!!!!!!!!!!!!!!!!!!!SURVEY  getEvents tmp eventName = " + tmp[i].getEventName());
                    System.out.println("!!!!!!!!!!!!!!!!!!!!!! SURVEY getEvents tmp user = " + tmp[i].getUser());               
                    System.out.println("!!!!!!!!!!!!!!!!!!!!!! SURVEY getEvents tmp eventId = " + tmp[i].getEventId());
                   // (usersData.get(dest)).add(tmp[i]);    // si aggiunge un evento, ovvero in questo caso si assume una domanda alla lista delle domande per dest
                  domande.add(tmp[i]);
                }
                //printUsersData();
            }
        }
        return tmp;

    }

    // si chiama putEvents ma e' identica ad un publishEvent
    public void putEvents(EventDescription[] events, String userName) {
        if (userName != null) {
            if (!alreadySubscr(userName)) {
                //       if (!addedFilter) {
                //le due subscribe con domanda sono solo x debug con GigaMgrUI
                //  subscribeTo("domanda1", "gio");
                // subscribeTo("domanda2", "gio");
                Set<String> apps = eventSubscrData.keySet();
                Iterator<String> iter = apps.iterator();
                while (iter.hasNext()) {
                    String app = iter.next();
                    ArrayList<String> eventsL = eventSubscrData.get(app);
                    for (String ev : eventsL) {
                        subscribeTo(ev, userName, app); // user arrivera' dal Gadget+iGooglepage
                    }
                }

                addedFilterList.add(userName);
            }
        }
        getListener().putEvents(events);
    }
// method e method2 :metodi di prova da sostituire con i veri metodi della app

    // metodo di prova per solo "RMI"
    public String myMethod(String s) {
        // Do something interesting with 's' here on the server.

        return "Server RMI (no GIGA) says: " + s;
    }
    // metodo di prova per Giga

    public String myMethod2(String s) {
        // Do something interesting with 's' here on the server.
   /*     if (listener != null) {
        getEvents();
        }
         * */
        return "Server GIGA says 222: " + s;
    }

    // si chiama sendEventToGiga ma e' identica ad un publishEvent (GIO)
//
    public String sendEventToGiga(String questionId, String answer, String user) {
        GigaListener listener = getListener();
        //   System.out.println("SURVEYMGR sendEventToGiga INIZIO @@@@@@@@@@@@  ");
     //   ArrayList<EventDescription> questions = usersData.get(user);  //il parametro user e' lo user corrente (il "me" della UI)
        //   System.out.println("SURVEYMGR sendEventToGiga questions size =  @@@@@@@@@@@@  " + questions.size());
        if (listener != null) {
            boolean found = false;
            if (domande == null) {
                System.out.println("SURVEYMGR sendEventToGiga : questions is NULL ");
            } else if (domande.size() == 0) {
                System.out.println("SURVEYMGR  sendEventToGiga : questions  lungh 0");
            } else {
                int i = 0;
                int ind = 0;
                while (!found && i < domande.size()) {
                    //       System.out.println("sendEventToGiga : questions  eventId e questionId  = " + (questions.get(i)).getEventId() + " " + questionId);
                    if ((domande.get(i)).getEventId().equals(questionId)) {
                        found = true;
                        ind = i;
                    }
                    i++;
                }
                EventDescription[] events = new EventDescription[1];
                events[0] = (domande.get(ind)).copyEd();   // copia oggetto EventDescription
                // correlazione tra Proposal e Answer: da parametrizzare ulteriormente
                String evReceived = events[0].getEventName();
                if (evReceived.equals("MeetingProposal")) {
                    events[0].setEventName("MeetingAnswer");
                }
                if (evReceived.equals("MembershipProposal")) {
                    events[0].setEventName("MembershipSurveyAnswer");
                }
                events[0].setApplication("SurveyMgr");
                String userTmp = events[0].getUser();
                // System.out.println("SURVEYMGR: SendEveTOGIga: event name = " + events[0].getEventName());
                // System.out.println("SURVEYMGR: SendEveTOGIga: user (prima)  = " + events[0].getUser());
                //  events[0].setUser(events[0].getDestinatario());
                events[0].setUser(user);
                // System.out.println("SURVEYMGR: SendEveTOGIga: user (dopo)  = " + events[0].getUser());                
                events[0].setDestinatario(userTmp); // TO DELETE
                events[0].addDestinatario(userTmp);  // NUOVO x lista dest 12-10-09
                //    System.out.println("SURVEYMGR: SendEveTOGIga: application = " + events[0].getApplication());
                //   events[0].getParameters().add(0, answer);  // DA CANCELLRE con sotto
                events[0].setParameter("answer", answer);  //da modificare dopo il nuovo EventDescrip
                //  System.out.println("SURVEYMGR: SendEveTOGIga: parameters = " + events[0].getParameters());
                removeEvent(domande.get(ind).getEventId(), domande);
                //  printUsersData();
                //  System.out.println("SURVEYMGR: SendEveTOGIga: size di events  = " + events.length);
                listener.putEvents(events);
            }
        }
        return "inviato evento a GIGA ";
    }

    private boolean removeEvent(String evId, ArrayList<EventDescription> aList) {
        int i = 0;
        boolean trovato = false;
        EventDescription ed = null;
        while (i < aList.size() && !trovato) {
            ed = aList.get(i);
            if (ed.getEventId().equals(evId)) {
                trovato = true;
                aList.remove(i);
            }
            i++;
        }
        return trovato;
    }

//    private void printUsersData() {
//        System.out.println("-------- stampa usersData : ----");
//        Set<String> users = usersData.keySet();
//        Iterator<String> iter = users.iterator();
//        while (iter.hasNext()) {
//            String user = iter.next();
//            ArrayList<EventDescription> arL = usersData.get(user);
//            System.out.println("user = " + user + " data = ");
//            for (int i = 0; i < arL.size(); i++) {
//                System.out.println(arL.get(i).getDescription() + "  ---  ");
//            }
//        }
//        System.out.println("-------- fine stampa usersData : ----");
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
        System.out.println("ho fatto la new di Subscription di SURVEY grande");
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
        res = addedFilterList.contains(userName);
        return res;
    }

    public Boolean validateUser(String name, String pwd) {
        //    return new ServerToClient().validateUser(name,pwd);
        ContactCall cC = new ContactCall(name, pwd);
        boolean val = cC.validate(name, pwd);
        System.out.print("sono in SURVEY validateUser = " + val);
        return new Boolean(val);
    }

    //------------------------------ login openId methods
    private void getInfo() {

        System.out.println("GETINFO attr names" + getSession().getAttributeNames().toString());


    }

    public String authenticate(String s) {
        String userEmail = "";
        SingleUser sU = cloudUsers.getUser(s);
        //src=   System.out.println("AUTHENTICATE " + s);
        if (sU != null) {
            userEmail = sU.getMailAddress();
            me= userEmail;
        } else {
            System.out.println("singleUSer NULL");
        }
        return userEmail;
    }
    //------------------------------
}
