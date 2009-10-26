/*
 * GWTServiceImpl.java
 *
 * Created on May 19, 2009, 12:41 PM
 *
 * To change this template, choose Tools | Template Manager
 * and open the template in the editor.
 */
package org.ice.groupmgr.server;

import appsusersevents.client.CloudUsers;
import appsusersevents.client.EventDescription;

import appsusersevents.client.SingleUser;
import com.google.gdata.data.contacts.ContactEntry;
import com.google.gdata.data.contacts.ContactGroupEntry;
import com.google.gdata.data.contacts.GroupMembershipInfo;
import com.google.gdata.data.extensions.Email;
import com.google.gdata.data.extensions.ExtendedProperty;
import com.google.gwt.user.server.rpc.RemoteServiceServlet;


import giga.GigaListener;
//import giga.Subscription;
import googlecontacts.ContactCall;
import googlecontacts.ContactsExampleParameters;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;

import java.util.Set;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpSession;
import org.ice.groupmgr.client.ContattoModelData;
import org.ice.groupmgr.client.GWTService;
import org.ice.groupmgr.client.GruppoModelData;

/**
 *
 * @author giovanna
 */
public class GWTServiceImpl extends RemoteServiceServlet implements
        GWTService {

    //  GroupManagerSAV gMgr = new GroupManagerSAV();
    // ContactCallSAV cCall = null;
    ContactCall cCall = null;
    // per Giga
    HashMap<String, GigaListener> sessionListeners = new HashMap();
    ArrayList addedFilterList = new ArrayList(); // lista di utenti gia' sottoscritti
  //  HashMap<String, ArrayList<EventDescription>> usersData = new HashMap();  // chiave = destinatario e value = lista di eventi (domande) che i filtri fanno passare (per quello user)
    HashMap<String, ArrayList<String>> eventSubscrData = new HashMap();  // chiave = applicazione e value = lista di eventi a cui sottoscriversi
    HttpSession session = null;
    // fine Giga
    String iceMgrLogin = "iceMgr09@gmail.com";
    String iceMgrPasswd = "sync09fr";
    String groupMakerLogin = "";
    String groupMakerPasswd = "";
    // HashMap<String, String> logPasswdData = new HashMap();
    // per gestire risposte
    HashMap<String, GroupInProgress> groupsInProgress = new HashMap();
    private boolean gruppoConfermato = false;
    CloudUsers cloudUsers = new CloudUsers();

    public void init() {

        //    logPasswdData.put("gio.petrone@gmail.com", "mer20ia05");
        //    logPasswdData.put("annamaria.goy@gmail.com", "tex_willer");
        // inizializzazione di userData, in futuro leggere gli utenti da users.xml
     //   usersData.put("gio.petrone@gmail.com", new ArrayList());
//        usersData.put("sgnmrn@gmail.com", new ArrayList());
//        usersData.put("marino@di.unito.it", new ArrayList());
//        usersData.put("lg.petrone@gmail.com", new ArrayList());
//        usersData.put("annamaria.goy@gmail.com", new ArrayList());
        ArrayList aL = new ArrayList();
        aL.add("MembershipSurveyAnswer");
        eventSubscrData.put("SurveyMgr", aL);

    }

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
        System.out.println("******GroupMgr getEvents  all'inizio");
        //TEMP !!!! x prova parametri servlet !!!!
        //    System.out.println("******GroupMgr dopo getParameter " + this.getParameter("p"));
        // FINE TEMP
        EventDescription[] tmp = null;
        if (userName != null) {
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
                //   HttpSession sess = this.getThreadLocalRequest().getSession();
                //  System.out.println("SESSIONE id = : " + sess.getId());
                addedFilterList.add(userName);
            }
            tmp = getListener().getEvents();
            if (tmp == null) {
                System.out.println("GroupMgr getEvents  tmp NULL");
            } else if (tmp.length == 0) {
                System.out.println("GroupMgr  getEvents tmp size 0");
            } else {
                System.out.println("************GroupMgr  getEvents tmp SIZE = " + tmp.length);
                // TEMP indice 0 : assumiamo che tutti gli eventi che arrivano con getEvents(), abbiano lo stesso destinataio (plausibile per come sono costruiti i filtri)
          //      String dest = tmp[0].getDestinatario();
                for (int i = 0; i < tmp.length; i++) {

                    System.out.println("!!!!!!!!!!!!!!!!!!!!!!GroupMgr  getEvents tmp eventName = " + tmp[i].getEventName());
                    System.out.println("!!!!!!!!!!!!!!!!!!!!!! GroupMgr getEvents tmp user = " + tmp[i].getUser());

                    if (tmp[i].getEventName().equals("MembershipSurveyAnswer")) {
                        addRisposta(tmp[i]);
                        if (gruppoConfermato) {
                            System.out.println("!!!!!!!!!!!!!!!!!!!!!! GroupMgr  getEvents GRUPPO CONFERMATO ++++  user = " + tmp[i].getUser());

                            String nGruppo = tmp[i].getParameter("groupName");
                            List<ContattoModelData> cont = groupsInProgress.get(nGruppo).getContatti();
                            confermaGruppo(nGruppo, cont, true);
                            gruppoConfermato = false;
                        }
                    }
                }
            }
        }
        return tmp;

    }

    // si chiama putEvents ma e' identica ad un publishEvent
    public void putEvents(EventDescription[] events, String userName) {
        if (userName != null) {
            if (!alreadySubscr(userName)) {
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

    private String subscribeTo(String evName, String dest, String app) {
        // invia a Giga il nome dell'evento a cui l'utente si vuole sottoscrivere
         EventDescription template = new EventDescription("*");
        template.setEventName(evName);
        template.setApplication(app);
        template.addDestinatario(dest);
        getListener().addEvent(template);

//        Subscription f = new Subscription();
//        System.out.println(" ho fatto la new di Subscription in GROUPMGR  grande evName = " + evName + "  dest = " + dest + " app = " + app);
//        EventDescription evDescr = new EventDescription(evName);
//        evDescr.setEventName(evName);
//   //     evDescr.setDestinatario(dest);
//        evDescr.addDestinatario(dest);
//        evDescr.setApplication(app);
//        f.setDesc(evDescr);
//        getListener().addFilter(f);
        return "inviato evento a cui ci si sottoscrivere a GIGA " + evName;
    }

    private boolean alreadySubscr(String userName) {
        boolean res = false;
        res = addedFilterList.contains(userName);
        return res;
    }

//  public String sendEventToGiga(String groupName, String eventName, String user, String dest) {
    //  private String sendEventToGiga(EventDescription eventDesc, String user) {
    private String sendEventToGiga(EventDescription eventDesc) {

        EventDescription[] events = new EventDescription[1];
        System.out.println("GROUPMGR SendEveTOGIga I :  " + eventDesc.getEventName());
        events[0] = eventDesc;
        getListener().putEvents(events); // invio a Giga evento GroupProposal per ogni destinatario
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
    // fine metodi per Giga
    //*************************************************************************
    //*************************************************************************

    //ok
    public List<ContattoModelData> getContatti() {
        List<ContattoModelData> listaRis = new ArrayList<ContattoModelData>();
        if (cCall == null) {
            System.out.println("cCall NULL");
        } else {
            List<ContactEntry> listaContatti = cCall.getUserContacts();
            for (ContactEntry c : listaContatti) {
                String email = getEmailAddress(c);
                listaRis.add(new ContattoModelData(c.getId(), c.getTitle().getPlainText(), " ", email, "2"));
                //   System.out.println("getContatti " + c.getTitle().getPlainText() + " "+ (c.getImAddresses()).get(0).toString());
            }
        }
        return listaRis;
    }

    private String getEmailAddress(ContactEntry c) {
        String email = "";
        for (Email e : c.getEmailAddresses()) {
            if (e.getPrimary()) {
                email = e.getAddress();
            }
        }
        return email;
    }
    //    public List[] getMembriGruppo(int idGruppo) { FIORE

    public List[] getMembriGruppo(String idGruppo) {
// gli URI dei gruppi saranno tutti diversi !! QUindi propagazione deve non ricopiare URI del iceMgr
        System.out.println("sono in getMembriGruppo");
        List<ContactEntry> contatti = cCall.getUserContacts();  // tutti i contatti dell'ICE
        List<ContattoModelData> membri = new ArrayList<ContattoModelData>();  // membri del gruppo passato per parametro
        for (ContactEntry c : contatti) {
            for (GroupMembershipInfo gmi : c.getGroupMembershipInfos()) {
                if ((gmi.getHref()).equals(idGruppo)) {
                    System.out.println("sono in getMebriGruppo idGrouppo cercatoI =  " + idGruppo);
                    System.out.println("sono in getMebriGruppo idGrouppoURI =  " + gmi.getHref());
                    String email = getEmailAddress(c);
                    membri.add(new ContattoModelData(c.getId(), c.getTitle().getPlainText(), " ", email, "2"));
                }
            }
        }
        List<ContattoModelData> listaNonNelGruppo = getNotMembriGruppo(idGruppo);
        List[] liste = {membri, listaNonNelGruppo};
        return liste;
    }

    private List<ContactEntry> getMembriGruppoCE(String idGruppo) {
        System.out.println("sono in getMembriGruppoCE");
        List<ContactEntry> contatti = cCall.getUserContacts();  // tutti i contatti dell'ICE
        List<ContactEntry> membri = new ArrayList<ContactEntry>();  // membri del gruppo passato per parametro
        boolean membroTrovato = false;
        for (ContactEntry c : contatti) {
            for (GroupMembershipInfo gmi : c.getGroupMembershipInfos()) {
                if ((gmi.getHref()).equals(idGruppo)) {
                    System.out.println("sono in getMebriGruppo idGrouppo cercatoI =  " + idGruppo);
                    System.out.println("sono in getMebriGruppo idGrouppoURI =  " + gmi.getHref());
                    membri.add(c);
                }
            }
        }
        return membri;

    }

    private List<ContattoModelData> getNotMembriGruppo(String idGruppo) {
        System.out.println("sono in getNOTMembriGruppo");
        List<ContactEntry> contatti = cCall.getUserContacts();  // tutti i contatti dell'ICE
        List<ContattoModelData> nonMembri = new ArrayList<ContattoModelData>();  // membri del gruppo passato per parametro
        boolean membroTrovato = false;
        for (ContactEntry c : contatti) {
            for (GroupMembershipInfo gmi : c.getGroupMembershipInfos()) {
                if ((gmi.getHref()).equals(idGruppo)) {
                    System.out.println("sono in getMebriGruppo idGrouppo cercatoI =  " + idGruppo);
                    System.out.println("sono in getMebriGruppo idGrouppoURI =  " + gmi.getHref());
                }
            }
            if (!membroTrovato) {
                String email = getEmailAddress(c);
                nonMembri.add(new ContattoModelData(c.getId(), c.getTitle().getPlainText(), " ", email, "2"));
            }
        }
        return nonMembri;

    }

    //TODO
    public List<ContactGroupEntry> getGruppiContatto(int idContatto) {
        List<ContactEntry> contatti = cCall.getUserContacts();  // tutti i contatti dell'ICE
        List<ContactGroupEntry> gruppi = new ArrayList<ContactGroupEntry>();  // membri del gruppo passato per parametro
        for (ContactEntry c : contatti) {
            for (GroupMembershipInfo gmi : c.getGroupMembershipInfos()) {
                //       if (gmi.equals(idGruppo)) {
                //          membri.add(c);
                }
        }
        return gruppi;
    }

//ok
    public List<GruppoModelData> getGruppi() {
        List<GruppoModelData> listaGruppi = new ArrayList<GruppoModelData>();
        if (cCall == null) {
            System.out.println("cCall NULL");
        } else {
            for (ContactGroupEntry c : cCall.getUserGroups()) {
                listaGruppi.add(new GruppoModelData(c.getId() + "", c.getTitle().getPlainText()));
                System.out.println("sono in getGruppi gruppo e id :  " + c.getTitle().getPlainText() + " " + c.getId());
            }
        }
        System.out.println("sono in getGruppi size di listGruppi " + listaGruppi.size());
        return listaGruppi;
    }

// ok
    public boolean creaGruppo(String nome, List<ContattoModelData> contatti) {

        boolean ret = true;
        //crea evento di MembershipProposal per ogni membro del gruppo
        GroupInProgress gP = new GroupInProgress(contatti);
        // old version : un evento x ogni dest
//        for (ContattoModelData c : contatti) {
//            EventDescription eventDesc = new EventDescription();
//            eventDesc.setCorrelationId(eventDesc.getEventId());  // CHIEDERE a MARINO
//            eventDesc.setApplication("GroupMgr");
//            eventDesc.setDestinatario(c.getMail());
//            System.out.println("sono in GROUPMGR in creaGruppo dest = " + c.getMail());
//            eventDesc.setParameter("groupName", nome);
//            // MembershipProposal
//            eventDesc.setEventName("MembershipProposal");
//            eventDesc.setUser(groupMakerLogin);  // TEMP fino a che solo lil groupMakerr puo' creare gruppi
//            //per gestione risposte
//            gP.addRequest(c.getMail());
//            groupsInProgress.put(nome, gP);
//            //fine gestione risposte
//            sendEventToGiga(eventDesc, c.getMail());
//        }
        // nuova versione
        EventDescription eventDesc = new EventDescription();
        eventDesc.setCorrelationId(eventDesc.getEventId());  // CHIEDERE a MARINO
        eventDesc.setApplication("GroupMgr");
        eventDesc.setParameter("groupName", nome);
        // MembershipProposal
        eventDesc.setEventName("MembershipProposal");
        eventDesc.setUser(groupMakerLogin);
        for (ContattoModelData c : contatti) {
           // eventDesc.setDestinatario(c.getMail()); //da elimimare in futuro
            eventDesc.addDestinatario(c.getMail());
            //per gestione risposte
            gP.addRequest(c.getMail());
            groupsInProgress.put(nome, gP);
            //fine gestione risposte
            // sendEventToGiga(eventDesc, c.getMail());
        }
        sendEventToGiga(eventDesc);


        return ret;
    }

    private boolean confermaGruppo(String nome, List<ContattoModelData> contatti, boolean crea) {
        List<ContactEntry> listaContatti = new ArrayList<ContactEntry>();  // conterra' i membri del nuovo gruppo
        List<ContactEntry> allContatti = cCall.getUserContacts();   //tutti i contatti dell'ICE
        boolean ret = true;
        String groupId = "";
        // crea gruppo
        ExtendedProperty additionalInfo = new ExtendedProperty();
        additionalInfo.setName("nameAdditionalInfo");
        additionalInfo.setValue("valueAdditionalInfo");
        if (cCall == null) {
            System.out.println("cCall NULL");
        } else {
            ContactGroupEntry cGroupE = cCall.creaGruppo(nome, " ", additionalInfo);  //crea gruppo in Contacts
            //  fine crea gruppo
            groupId = cGroupE.getId();
            EventDescription eventDesc = new EventDescription();
            if (crea) {
                eventDesc.setEventName("GroupCreated");
            } else {
                eventDesc.setEventName("GroupModified");
            }
            // eventDesc.setCorrelationId(eventDesc.getEventId());  // CHIEDERE a MARINO ????
            eventDesc.setApplication("GroupMgr");
            eventDesc.setParameter("groupName", nome);
            eventDesc.setUser(groupMakerLogin);
            for (ContattoModelData cmd : contatti) {
                boolean trovato = false;
                Iterator<ContactEntry> it = allContatti.iterator();
                while (!trovato && it.hasNext()) {
                    ContactEntry cE = it.next();
                    String mail = getEmailAddress(cE);
                    if (cmd.getMail().equals(mail)) {
                        trovato = true;
                        // GIO :aggiungi idGruppo al contatto
                        try {
                            cCall.setGroupMembership(cE.getId(), groupId);
                  //          eventDesc.setDestinatario(cmd.getMail()); // da eliminare mandare a Giga evento createdGroup per ogni utente
                            eventDesc.addDestinatario(cmd.getMail());
                        } catch (Exception e) {
                            System.out.println("GroupMgr ERRORE creaGruppo");
                        }
                    }
                }//while
            } // for   
            sendEventToGiga(eventDesc);
            esportaGruppo(nome, contatti, iceMgrLogin, iceMgrPasswd);  // per ICE mgr
            // ciclo su tutti i membri del gruppo (contatti) per aggiornare i loro gruppi (nei loro contatti Google)
            // valutare se questo modo automatico va sostituito con un pulsante nella UI, non si sfrutta il for precedente per futura invocazioen dalle singole UI
            for (ContattoModelData cmd : contatti) {
                String mail = cmd.getMail();
                // String pwd = logPasswdData.get(mail);
                SingleUser sU = cloudUsers.getUserByEmail(mail);
                if (sU != null) {
                    String pwd = sU.getPwd();
                    if (!mail.equals(groupMakerLogin)) {
                        esportaGruppo(nome, contatti, mail, pwd);  // per ogni membro del gruppo
                    }
                }
            }
        } // else
        return ret;
    }

    private ContactCall connectContact(String userMail, String psswd) {
        ContactCall cCallTmp = null;
        try {
            String[] myArg = {"--username=" + userMail, "--password=" + psswd, "-contactfeed", "--action=update"};  // OK
            //   String[] myArg = {"--username=annamaria.goy@gmail.com", "--password=tex_willer", "-contactfeed", "--action=update"};  // OK
            //String[] myArg = {"--username=" + iceMgrLogin, "--password=" + iceMgrPasswd, "-contactfeed", "--action=update"};  // OK
            ContactsExampleParameters parameters = new ContactsExampleParameters(myArg); // X USAGE
            cCallTmp = new ContactCall(parameters);
            //FINE NUOVO
        } catch (Exception e) {
            System.out.println("error");
        }
        return cCallTmp;
    }

    private boolean esportaGruppo(String nome, List<ContattoModelData> contatti, String targetLogin, String targetPasswd) {
        boolean ret = true;

        List<ContactEntry> listaContatti = new ArrayList<ContactEntry>();  // conterra' i membri del nuovo gruppo
        // ContactCall cc = connectContact("annamaria.goy@gmail.com", "tex_willer");
        ContactCall cc = connectContact(targetLogin, targetPasswd);
        List<ContactEntry> allContatti = cc.getUserContacts();   //tutti i contatti dell'utente
        System.out.println("sono in esportaGruppo");
        String groupId = "";
        // crea gruppo per l'utente/membro
        ExtendedProperty additionalInfo = new ExtendedProperty();
        additionalInfo.setName("nameAdditionalInfo");
        additionalInfo.setValue("valueAdditionalInfo");
        if (cc == null) {
            System.out.println("cc NULL");
        } else {
            ContactGroupEntry cGroupE = cc.creaGruppo(nome, " ", additionalInfo);  //crea gruppo in Contacts
            //  fine crea gruppo del nuovo membro
            groupId = cGroupE.getId();
            System.out.println("***** esportaGruppo : gruppo id = " + groupId);
            for (ContattoModelData cmd : contatti) {
                boolean trovato = false;
                Iterator<ContactEntry> it = allContatti.iterator();
                while (!trovato && it.hasNext()) {
                    ContactEntry cE = it.next();
                    String mail = getEmailAddress(cE);
                    if (cmd.getMail().equals(mail)) {
                        System.out.println("***** esportaGruppo : in contattoModelData id = " + cmd.getMail() + " in contactEntry id = " + mail);
                        trovato = true;
                        // GIO :aggiungi idGruppo al contatto
                        try {
                            //  ??? Se lo faremo, si crea il gruppo di contatti per ogni membro del gruppo ???? FORSE
                            cc.setGroupMembership(cE.getId(), groupId);
                        } catch (Exception e) {
                            System.out.println("GroupMgr ERRORE esportaGruppo");
                        }
                    }
                }//while
                } // for
            } // else

        return ret;
    }

    private String getGroupId(String groupName, ContactCall cc) {
        String groupId = "";
        if (cc == null) {
            System.out.println("cc NULL");
        } else {
            for (ContactGroupEntry c : cc.getUserGroups()) {
                if (c.getTitle().getPlainText().equals(groupName)) {
                    groupId = c.getId();
                    System.out.println("sono in getGroupId gruppo e id :  " + c.getTitle().getPlainText() + " " + c.getId());
                }
            }
        }
        if (groupId.equals("")) {
            System.out.println("sono in getGroupId gruppo :   ID VUOTO");
        }
        return groupId;
    }

    private void esportaCancellazioneGruppo(String groupName, String targetLogin, String targetPasswd) {

        ContactCall cc = connectContact(targetLogin, targetPasswd);
        String groupId = getGroupId(groupName, cc);
        System.out.println("GroupMgr esportaCancellazioneGruppo + targetLogin +targetPasswd " + targetLogin + " " + targetPasswd);
        cc.deleteGroup(groupId);  //elimina  gruppo in Contact

    }

    private boolean esportaGruppoSAV(String nome, List<ContattoModelData> contatti) {
        List<ContactEntry> listaContatti = new ArrayList<ContactEntry>();  // conterra' i membri del nuovo gruppo
        // ContactCall cc = connectContact("annamaria.goy@gmail.com", "tex_willer");
        ContactCall cc = connectContact(iceMgrLogin, iceMgrPasswd);
        List<ContactEntry> allContatti = cc.getUserContacts();   //tutti i contatti dell'utente
        System.out.println("sono in esportaGruppo");
        boolean ret = true;
        String groupId = "";
        // crea gruppo per l'utente/membro
        ExtendedProperty additionalInfo = new ExtendedProperty();
        additionalInfo.setName("nameAdditionalInfo");
        additionalInfo.setValue("valueAdditionalInfo");
        if (cc == null) {
            System.out.println("cc NULL");
        } else {
            ContactGroupEntry cGroupE = cc.creaGruppo(nome, " ", additionalInfo);  //crea gruppo in Contacts
            //  fine crea gruppo del nuovo membro
            groupId = cGroupE.getId();
            System.out.println("***** esportaGruppo : gruppo id = " + groupId);
            for (ContattoModelData cmd : contatti) {
                boolean trovato = false;
                Iterator<ContactEntry> it = allContatti.iterator();
                while (!trovato && it.hasNext()) {
                    ContactEntry cE = it.next();
                    String mail = getEmailAddress(cE);
                    if (cmd.getMail().equals(mail)) {
                        System.out.println("***** confermaGruppo : in contattoModelData id = " + cmd.getMail() + " in contactEntry id = " + mail);
                        trovato = true;
                        // GIO :aggiungi idGruppo al contatto
                        try {
                            //  ??? Se lo faremo, si crea il gruppo di contatti per ogni membro del gruppo ???? FORSE
                            cc.setGroupMembership(cE.getId(), groupId);
                        } catch (Exception e) {
                            System.out.println("GroupMgr ERRORE esportaGruppo");
                        }
                    }
                }//while
            } // for
        } // else
        return ret;
    }

    public boolean eliminaGruppo(String nomeGruppo, String idGruppo, boolean elimina) {
        boolean ret = true;
        //  return GroupManagerSAV.eliminaGruppo(nomeGruppo, idGruppo);
        //    List<ContactEntry> listaContatti = new ArrayList<ContactEntry>();  // conterra' i membri del nuovo gruppo
        List<ContactEntry> membri = getMembriGruppoCE(idGruppo);
        System.out.println("groupMgr impl : sono in eliminaGruppo");
        if (cCall == null) {
            System.out.println("cCall NULL");
        } else {
            if (elimina) {
                EventDescription eventDesc = new EventDescription();
                eventDesc.setEventName("GroupDeleted");
                // serve settare correlationId ???
                eventDesc.setApplication("GroupMgr");
                eventDesc.setParameter("groupName", nomeGruppo);
                eventDesc.setUser(groupMakerLogin);
                Iterator<ContactEntry> it = membri.iterator();
                while (it.hasNext()) {
                    ContactEntry cE = it.next();
                    String mail = getEmailAddress(cE);
                    eventDesc.addDestinatario(mail);
                 //   eventDesc.setDestinatario(mail); // da elimianre TEMP
                    //DA UTILIZZARE SOLO IN modifica gruppo
         /*       try {
                    cancellato = cCall.removeGroupMembership(cE.getId(), idGruppo);
                    } catch (Exception e) {
                    System.out.println("GROUPMGR  deleteGruppo ERRORE");
                    }
                     */
                    // mandare a Giga evento createdGroup per ogni utente
                    // if (elimina) {
                    // EventDescription eventDesc = new EventDescription();
                    // eventDesc.setEventName("GroupDeleted");
                    // serve settare correlationId ???
                    //  eventDesc.setApplication("GroupMgr");
                    // eventDesc.setDestinatario(mail);
                    // eventDesc.setParameter("groupName", nomeGruppo);
                    // eventDesc.setUser(groupMakerLogin);
                    //     sendEventToGiga(eventDesc, mail);
                    //    sendEventToGiga(eventDesc);
                    //  }
                }//while

                sendEventToGiga(eventDesc);
            }
            cCall.deleteGroup(idGruppo);  //elimina  gruppo in Contact
            // invocare la deleteGroup su ogni contatto del gruppo e sul iceMgr  DA FARE
            esportaCancellazioneGruppo(nomeGruppo, iceMgrLogin, iceMgrPasswd);  // per ICE mgr
            // ciclo su tutti i membri del gruppo (contatti) per aggiornare i loro gruppi (nei loro contatti Google)
            // valutare se questo modo automatico va sostituito con un pulsante nella UI, non si sfrutta il for precedente per futura invocazioen dalle singole UI
            for (ContactEntry cmd : membri) {
                String mail = getEmailAddress(cmd);
                //   String pwd = logPasswdData.get(mail);
                SingleUser sU = cloudUsers.getUserByEmail(mail);
                if (sU != null) {
                    String pwd = sU.getPwd();
                    if (!mail.equals(groupMakerLogin)) {
                        esportaCancellazioneGruppo(nomeGruppo, mail, pwd);  // per ogni membro del gruppo
                    }
                }

            }

        } // else




        return ret;
    }
// al momento 1-9-09 modifica gruppo, esegue : una eliminaGruppo + una creaGruppo !!!! (GIO+ANNA)

    public boolean modificaGruppo(String nomeGruppo, String idGruppo, List<ContattoModelData> contatti) {

        eliminaGruppo(nomeGruppo, idGruppo, false);
        confermaGruppo(nomeGruppo, contatti, false);
        /*
        List<ContactEntry> listaContatti = new ArrayList<ContactEntry>();
        for (ContattoModelData cmd : contatti) {
        }
         */
        return true;

    }

// metodi per gestire le risposte dal Suvey per ogni invitato
    public boolean addRisposta(EventDescription evt) {
        // remove a recipient each time a confirmation is received
        boolean eliminato = false;      
        System.out.println("in GROUPMGR.addrisposta: user  " + evt.getUser());  //chi risponde

        // if the destination is this user AND the answer
        // relates to this meeting proposal, check if answe == yes
        //       if (evt.getDestinatario().equals(template.getUser()) &&
        //              evt.getSessionId().equals(sessionId)) {
        ArrayList params = evt.getParameters();
        if (!params.isEmpty()) {
            String answ = evt.getParameter("answer");
            System.out.println("GROUPMGR, Answer = " + answ);
            if (answ.equalsIgnoreCase("Yes")) {
                String gN = evt.getParameter("groupName");
                eliminato =
                        groupsInProgress.get(gN).addAnswer(evt.getUser());
                //            eliminato = risposte.remove(evt.getUser());
                gruppoConfermato =
                        (groupsInProgress.get(gN).getRisposte().size() == 0);
                System.out.println("GROUPMGR,gruppoConfermato = : " + gruppoConfermato);
            } else {
                System.out.println("GROUPMGR, utente ha rsiposto no o errore: " + answ);
            }

        }
        //    }
        return eliminato;
    }

    public boolean validateUser(String name, String pwd) {
        //    return new ServerToClient().validateUser(name,pwd);

        ContactCall cC = new ContactCall(name, pwd);
        boolean val = cC.validate(name, pwd);
        System.out.print("sono in GroupMgr validate = " + val);
        groupMakerLogin = name;
        groupMakerPasswd = pwd;
        try {

            //     String[] myArg = {"--username=annamaria.goy@gmail.com", "--password=tex_willer", "-contactfeed", "--action=list"};  // OK
            String[] myArg = {"--username=" + groupMakerLogin, "--password=" + groupMakerPasswd, "-contactfeed", "--action=update"};  // OK
            ContactsExampleParameters parameters = new ContactsExampleParameters(myArg); // X USAGE
            cCall = new ContactCall(parameters);
            //   groupMakerLogin = "annamaria.goy@gmail.com";
            //  groupMakerPasswd = "tex_willer";
            //FINE NUOVO
        } catch (Exception e) {
            System.out.println("error");
        }
        System.out.println("GROUPMGR, in validateUser groupMaker login e pws:  " + groupMakerLogin + " " + groupMakerPasswd);
        return val;
    }

    public String authenticate(String s) {
        String userEmail = "";
        String pwd = "";
        SingleUser sU = cloudUsers.getUser(s);
        //src=   System.out.println("AUTHENTICATE " + s);
        if (sU != null) {
            userEmail = sU.getMailAddress();
            pwd = sU.getPwd();
        } else {
            System.out.println("singleUSer NULL");
        }

        ContactCall cC = new ContactCall(userEmail, pwd);
        boolean val = cC.validate(userEmail, pwd);
        System.out.print("sono in GroupMgr validate = " + val);
        groupMakerLogin = userEmail;
        groupMakerPasswd = pwd;
        try {

            //     String[] myArg = {"--username=annamaria.goy@gmail.com", "--password=tex_willer", "-contactfeed", "--action=list"};  // OK
            String[] myArg = {"--username=" + groupMakerLogin, "--password=" + groupMakerPasswd, "-contactfeed", "--action=update"};  // OK
            ContactsExampleParameters parameters = new ContactsExampleParameters(myArg); // X USAGE
            cCall = new ContactCall(parameters);
            //   groupMakerLogin = "annamaria.goy@gmail.com";
            //  groupMakerPasswd = "tex_willer";
            //FINE NUOVO
        } catch (Exception e) {
            System.out.println("error");
        }
        return userEmail;
    }
}
