/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package org.ice.groupmgr.server;

import appsusersevents.client.EventDescription;
import com.google.gdata.data.contacts.ContactEntry;
import com.google.gdata.data.contacts.ContactGroupEntry;
import com.google.gdata.data.contacts.GroupMembershipInfo;
import com.google.gdata.data.extensions.ExtendedProperty;
import com.google.gdata.util.ServiceException;
import giga.GigaListener;
import giga.Subscription;
import googlecontacts.ContactCallSAV;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Set;
import javax.servlet.http.HttpSession;

/**
 *
 * @author giovanna
 */
public class GroupManagerSAV {

    ContactCallSAV cCall = null;

    // per Giga
    HashMap<String, GigaListener> sessionListeners = new HashMap();
    ArrayList addedFilterList = new ArrayList(); // lista di utenti gia' sottoscritti
    HashMap<String, ArrayList<EventDescription>> usersData = new HashMap();  // chiave = destinatario e value = lista di eventi (domande) che i filtri fanno passare (per quello user)
    HashMap<String, ArrayList<String>> eventSubscrData = new HashMap();  // chiave = applicazione e value = lista di eventi a cui sottoscriversi
    HttpSession session = null;
// fine Giga

    public GroupManagerSAV() {

        System.out.println("======================");
        System.out.println("GroupManager avviato, in attesa di messaggi...");
        System.out.println("======================");
        // GIO : per contattare Contacts
        //   cCall = new ContactCallSAV("gio.petrone@gmail.com", "mer20ia05");
        init();
        cCall = new ContactCallSAV("annamaria.goy@gmail.com", "tex_willer");

    }

    public void init() {
          cCall = new ContactCallSAV("annamaria.goy@gmail.com", "tex_willer");
        // inizializzazione di userData, in futuro leggere gli utenti da users.xml
        usersData.put("gio", new ArrayList());
        usersData.put("mar", new ArrayList());
        ArrayList aL = new ArrayList();
        aL.add("MembershipAnswer");
        eventSubscrData.put("SurveyMgr", aL);
    }

    /*
     * Crea una nuova istanza della classe GigaSpace e la assegna alla variabile
     * gigaSpace.
     *
     * ANNA E GIO : abbiamo cancellato metodi x Giga. Guardare eventualmente on GroupManagerMarco
     */
    //*************************************************************************
    //metodi per Giga
    public void setSession(HttpSession hS) {
        // Get the current request and then return its session
        session = hS;
    }

    private GigaListener getListener() {
        String sId = session.getId();
        GigaListener ret = sessionListeners.get(sId);
        System.out.println("sessionListeners: sessionId =  " + sId);
        if (ret == null) {
            ret = new GigaListener(false, false);
            System.out.println("ho fatto la new gi GigaListener");
            sessionListeners.put(sId, ret);
        }
        return ret;
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
                //            subscribeTo("MeetingProposal", userName, "CommonCalendar"); // user arrivera' dal Gadget+iGooglepage
                addedFilterList.add(userName);
            }
        }
        getListener().putEvents(events);
    }

    private String subscribeTo(String evName, String dest, String app) {
        // invia a Giga il nome dell'evento a cui l'utente si vuole sottoscrivere
        Subscription f = new Subscription();
        //    System.out.println("ho fatto la new di Subscription grande");
        //    System.out.println("evName = " + evName + "  dest = " + dest + " app = " + app);
        EventDescription evDescr = new EventDescription(evName);
        evDescr.setEventName(evName);
      //  evDescr.setDestinatario(dest);
        evDescr.setApplication(app);
        f.setDesc(evDescr);
        getListener().addFilter(f);
        return "inviato evento a cui ci si sottoscrivere a GIGA " + evName;
    }

    private boolean alreadySubscr(String userName) {
        boolean res = false;
        res = addedFilterList.contains(userName);
        return res;
    }


    // si chiama sendEventToGiga ma e' identica ad un publishEvent (GIO)
//
    public String sendEventToGiga(String questionId, String eventName, String user) {

        ArrayList<EventDescription> questions = usersData.get(user);  //il parametro user e' lo user corrente (il "me" della UI)

        /*    boolean found = false;
        if (questions == null) {
        System.out.println("sendEventToGiga : questions is NULL ");
        } else if (questions.size() == 0) {
        System.out.println("sendEventToGiga : questions  lungh 0");
        } else {
        int i = 0;
        int ind = 0;
        while (!found && i < questions.size()) {
        //       System.out.println("sendEventToGiga : questions  eventId e questionId  = " + (questions.get(i)).getEventId() + " " + questionId);
        if ((questions.get(i)).getEventId().equals(questionId)) {
        found = true;
        ind = i;
        }
        i++;
        }
         */
        EventDescription[] events = new EventDescription[1];
        EventDescription eventDesc = new EventDescription();
        eventDesc.setApplication("GroupMgr");
        eventDesc.setSender("GroupMgr");
        eventDesc.setReceiver("SurveyMgr");
        // GroupMembershipProposal
        eventDesc.setEventName(eventName);
        eventDesc.setEventId("0000"); // dopo meccanismo id di Marino : togliere !!!!
        System.out.println("SendEveTOGIga:  " + eventDesc.toString());
        events[0] = eventDesc;
        getListener();
         subscribeTo("GroupMembershipAnswer",  "" , "SurveyMgr")  ;
    //    getListener().putEvents(events);

        //      System.out.println("SendEveTOGIga: eventName= " + events[0].getEventName());
            /*   String userTmp = events[0].getUser();
        System.out.println("SendEveTOGIga: user (prima)  = " + events[0].getUser());
        events[0].setUser(events[0].getDestinatario());
        System.out.println("SendEveTOGIga: user (dopo)  = " + events[0].getUser());
        System.out.println("SendEveTOGIga: destinatario  (prima) = " + events[0].getDestinatario());
        events[0].setDestinatario(userTmp);
        System.out.println("SendEveTOGIga: destinatario  (dopo) = " + events[0].getDestinatario());

        System.out.println("SendEveTOGIga: parameters = " + events[0].getParameters());
        System.out.println("SendEveTOGIga: size di events  = " + events.length);
        //              removeEvent(questions.get(ind).getEventId(), questions);
        printUsersData();
         */



        //    }

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

    private void printUsersData() {
        System.out.println("-------- stampa usersData : ----");
        Set<String> users = usersData.keySet();
        Iterator<String> iter = users.iterator();
        while (iter.hasNext()) {
            String user = iter.next();
            ArrayList<EventDescription> arL = usersData.get(user);
            System.out.println("user = " + user + " data = ");
            for (int i = 0; i < arL.size(); i++) {
                System.out.println(arL.get(i).getDescription() + "  ---  ");
            }
        }
        System.out.println("-------- fine stampa usersData : ----");
    }
    // fine metodi per Giga
    //*************************************************************************
    //*************************************************************************

    public synchronized List<ContactGroupEntry> getGruppi() {
        List<ContactGroupEntry> gruppi = cCall.getUserGroups();
        return gruppi;
    }

    // GIO : Modificato eliminando static
    public synchronized List<ContactEntry> getContatti() {
        List<ContactEntry> contatti = cCall.getUserContacts();
        return contatti;
    }

    public synchronized List<ContactEntry> getMembriGruppo(String idGruppo) {
        List<ContactEntry> contatti = cCall.getUserContacts();
        List<ContactEntry> membri = new ArrayList<ContactEntry>();
        for (ContactEntry c : contatti) {
            for (GroupMembershipInfo gmi : c.getGroupMembershipInfos()) {
                if (gmi.equals(idGruppo)) {
                    membri.add(c);
                }
            }
        }

        return membri;
    }

    public synchronized static List<ContactGroupEntry> getGruppiContatto(int idContatto) {
        List<ContactGroupEntry> gruppi = new ArrayList<ContactGroupEntry>();
        // invocazione di metodo tipo getGroupss (idContact)  API di Contacts
        /*    resultSet = statement.executeQuery(
        "SELECT GRUPPI.ID, GRUPPI.NOME FROM CONTATTI, GRUPPI, " +
        "MEMBRIGRUPPO WHERE MEMBRIGRUPPO.IDCONTATTO = " + idContatto +
        " AND MEMBRIGRUPPO.IDCONTATTO = CONTATTI.ID AND " +
        "MEMBRIGRUPPO.IDGRUPPO = GRUPPI.ID ORDER BY GRUPPI.NOME");
         */
        // ciclo sulla lista di gruppi per inserirli  nella lista gruppi
// serve ?? dipende da return type di  API
        /*    while (resultSet.next()) {
        gruppi.add(new Gruppo(resultSet.getInt("id"), resultSet.getString(
        "nome")));
        }
         * */


        return gruppi;
    }

    public synchronized static List<ContactEntry> getNotMembriGruppo(int idGruppo) {
        List<ContactEntry> contatti = new ArrayList<ContactEntry>();


        /*  resultSet = statement.executeQuery(
        "SELECT CONTATTI.ID, CONTATTI.COGNOME, CONTATTI.NOME, " +
        "CONTATTI.MAIL FROM CONTATTI, GRUPPI, MEMBRIGRUPPO " +
        "EXCEPT (SELECT CONTATTI.ID, CONTATTI.COGNOME, CONTATTI.NOME, " +
        "CONTATTI.MAIL FROM CONTATTI, GRUPPI, MEMBRIGRUPPO WHERE " +
        "MEMBRIGRUPPO.IDGRUPPO = " + idGruppo +
        " AND MEMBRIGRUPPO.IDGRUPPO = GRUPPI.ID AND " +
        "MEMBRIGRUPPO.IDCONTATTO = CONTATTI.ID) ORDER BY " +
        "COGNOME, NOME");
         */

        /*
        while (resultSet.next()) {
        contatti.add(new Contatto(resultSet.getInt("id"), resultSet.getString(
        "cognome"), resultSet.getString("nome"), resultSet.getString(
        "mail")));
        }
         */

        return contatti;
    }

    // OK
    public synchronized boolean creaGruppo(String nomeGruppo, List<ContactEntry> contatti) throws ServiceException, IOException {

        boolean ok = true;

        ExtendedProperty additionalInfo = new ExtendedProperty();
        additionalInfo.setName("nameAdditionalInfo");
        additionalInfo.setValue("valueAdditionalInfo");
        ContactGroupEntry cGroupE = cCall.creaGruppo(nomeGruppo, " ", additionalInfo);

        /* invio evento creaione gruppo
        resultSet = statement.executeQuery(
        "SELECT ID FROM GRUPPI WHERE NOME = '" + nomeGruppo + "'");

        if (resultSet.next()) {

        int idGruppo = resultSet.getInt("id");
         */


        //     eventDesc.setUserGroup(idGruppo + "");
        //      eventDesc.setDescription("Sei stato aggiunto al gruppo " +        nomeGruppo);
     
        String groupId = cGroupE.getId();

        sendEventToGiga("", "GroupMembershipProposal", "");  // temporaneo
        for (ContactEntry c : contatti) {
            c.addGroupMembershipInfo(new GroupMembershipInfo(false, groupId));
            //      eventDesc.setUser(c.getMail());
            //     eventDesc.setDataId(c.getId() + "");
            //     gigaSpace.write(eventDesc);
    //        sendEventToGiga("", "MembershipProposal", "");

        }


        return ok;
    }

    public synchronized static boolean eliminaGruppo(String nomeGruppo,
            int idGruppo) {

        boolean ok = true;

        //      List<ContactEntry> membriGruppo = getMembriGruppo(idGruppo);


        /*
        statement.executeUpdate("DELETE FROM GRUPPI WHERE ID = " + idGruppo);

        statement.executeUpdate("DELETE FROM MEMBRIGRUPPO WHERE IDGRUPPO = " +
        idGruppo);


        EventDescription eventDesc = new EventDescription();
        eventDesc.setSender("GroupManagerSAV");
        eventDesc.setApplication("GroupManagerSAV");
        eventDesc.setEventName("eliminaGruppo");
        eventDesc.setDescription("Il gruppo " + nomeGruppo +
        " è stato eliminato.");
        for (Contatto c : membriGruppo) {
        eventDesc.setReceiver(c.getMail());
        gigaSpace.write(eventDesc);
        }
         */

        return ok;
    }

    public synchronized static boolean modificaGruppo(String nomeGruppo,
            int idGruppo,
            List<ContactEntry> contatti) {

        boolean ok = true;



        // lista dei membri del vecchio gruppo.
//        List<ContactEntry> oldContatti = getMembriGruppo(idGruppo);

        /*              EventDescription eventDesc = new EventDescription();
        eventDesc.setSender("GroupManagerSAV");
        eventDesc.setApplication("GroupManagerSAV");
        eventDesc.setEventName("modificaGruppo");

        eventDesc.setDescription("Sei stato rimosso dal gruppo " + nomeGruppo);

        for (Contatto c : oldContatti) {
        if (!contatti.contains(c)) {
        statement.executeUpdate("DELETE FROM MEMBRIGRUPPO WHERE IDGRUPPO = " +
        idGruppo + " AND IDCONTATTO = " + c.getId());
        eventDesc.setReceiver(c.getMail());
        gigaSpace.write(eventDesc);
        } else {
        contatti.remove(c);
        }
        }


        eventDesc = new EventDescription();
        eventDesc.setApplication("GroupManagerSAV");
        eventDesc.setEventName("modificaGruppo");
        eventDesc.setSender("GroupManagerSAV");
        eventDesc.setReceiver("SurveyManager");
        eventDesc.setDescription("Sei stato aggiunto al gruppo " +
        nomeGruppo);
        eventDesc.setUserGroup(idGruppo + "");
        for (Contatto c : contatti) {
        statement.executeUpdate("INSERT INTO MEMBRIGRUPPO (IDGRUPPO, IDCONTATTO, STATO) VALUES (" +
        idGruppo + ", " + c.getId() + ", 0)");
        eventDesc.setDataId(c.getId() + "");
        eventDesc.setUser(c.getMail());
        gigaSpace.write(eventDesc);
        }

        }
         */
        return ok;
    }

    public static int getIdContatto(String mailContatto) {
        int idContatto = -1;

        /*
        resultSet = statement.executeQuery(
        "SELECT ID FROM CONTATTI WHERE MAIL = '" + mailContatto + "'");

        if (resultSet.next()) {
        idContatto = resultSet.getInt("id");
        }
         * */

        return idContatto;
    }

    private synchronized static int confermaMembro(int idGruppo, int idContatto) {
        int ris = -1;
        /*

        ris = statement.executeUpdate("UPDATE MEMBRIGRUPPO SET STATO = 1 WHERE IDCONTATTO = " +
        idContatto + " AND IDGRUPPO = " + idGruppo);
         * */

        return ris;
    }
}
