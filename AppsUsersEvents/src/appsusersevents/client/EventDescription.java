/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package appsusersevents.client;

//import java.io.Serializable;
import java.util.ArrayList;

/**
 *
 * @author marino
 * this class could be made more general
 */
public class EventDescription extends TreeElement /*implements Serializable*/ {

    private String application = any;      //applicazione che genera l'evento (X es. COmmonCalendar)
    private String filRouge = any;         // identificatore di quella particolare singola applicazione;
    // es. missione estero di Tizio caio dal xx xx al xx a Roma
    private String activity = any;           // attivita' specifica di una APP (x ora non usate)
    private String eventName = any;     // X es. MeetingProposal
    private String userGroup = any;     //   finora x marino significava alias di un insieme di contatti, potrebbe la sfera
    private String user = any;         // utente che ha generato l'evento
    // private String destinatario = any;   // utente  a cui e' destinato l'evento che potrebbe essere un gruppo o un singolo utente
    private ArrayList<String> destinatari = new ArrayList(); // lista dei destinatari di un evento
    private String dataId = any;  //NON utilizzato fino a 6-2009
    private String sender = any;     // NON utilizzato fino a 6-2009
    private String receiver = any;   //NON utilizzato fino a 6-2009
    private String eventId = any;   // id univoco generato alal ProgI dal costruttore
    private String sessionId = any;   //  sessionId ottenuto dalla rispettiva servlet
    private String correlationId = any;  //  NON utilizzato fino a 6-2009 per trhead di conversazione
    private String time = any;  //NON utilizzato fino a 6-2009
    private ArrayList<String> parameters = new ArrayList();
    private static EventDescription AnyEvent =
            new EventDescription();
    private static String any = "*";
    private static long EventNumber = 0;
    private String explicitEvent = "true";  // da gui o no?
    private String tab = "";  // attivita utente
    // private String estimatedSphere = "";  // calcolata dallo user agent
    private ArrayList<String> spheres = new ArrayList(); // sfere dell'utente
    private ArrayList<String> involvedUsers = new ArrayList(); // utenti coinvolti se non sono indicati come gruppi(emails)
    //lili
    private String processed; // event has been processed for disambiguation
    private ArrayList<String> relevantSpheres; // relevant spheres identified by context

    /**
     * @return the anyDescription
     */
    public static EventDescription getAnyEvent() {
        return AnyEvent;
    }

    /**
     * @param aAnyDescription the anyDescription to set
     */
    public static void setAnyEvent(EventDescription aAnyDescription) {
        AnyEvent = aAnyDescription;
    }

    public EventDescription() {
        application = any;
        activity = any;
        eventName = any;
        userGroup = any;
        user = any;
        sessionId = any;
        correlationId = any;
        dataId = any;
        sender = any;
        receiver = any;
        time = any;
        filRouge = any;
        explicitEvent = "true";
        processed = "no";
        destinatari = new ArrayList();
        spheres = new ArrayList();
        relevantSpheres = new ArrayList();
        tab = "unknown";
        parameters = new ArrayList();
        eventId = "" + EventNumber;
        EventNumber++;
    }

    public EventDescription(String name) {
        super(name);
        application = any;
        activity = any;
        eventName = any;
        userGroup = any;
        user = any;
        sessionId = any;
        correlationId = any;
        dataId = any;
        sender = any;
        receiver = any;
        time = any;
        //      destinatario = any;
        filRouge = any;
        explicitEvent = "true";
        processed = "no";
        destinatari = new ArrayList();
        spheres = new ArrayList();
        relevantSpheres = new ArrayList();
        tab = "unknown";
        parameters = new ArrayList();
        eventId = "" + EventNumber;
        EventNumber++;
    }

    public EventDescription(String app, String explicit, String proc,
            String evName, String u,
            ArrayList<String> dest,
            ArrayList<String> sph, String tab, String time,
            ArrayList<String> params) {
        application = app;
        explicitEvent = explicit;
        processed = proc;
        eventName = evName;
        user = u;
        spheres = sph;
        relevantSpheres = new ArrayList();
        this.tab = tab;
        this.time = time;
        parameters = params;
//        parameterNames = new ArrayList();
//        for (int i=0; i<parameters.size(); i +=2)
//            parameterNames.add(parameters.get(i));

        // variabili aggiunte per far funzionare compatibleWith()
        activity = any;
        userGroup = any;
        dataId = any;
        sender = any;
        destinatari = dest;
        correlationId = any;
        sessionId = any;
    }
    //
    //  -1 if no match;
    //   0 exact match
    //  in future add more sofisticated rules

    private boolean fieldMatch(String first, String second) {
        return first.compareTo(second) == 0;
    }

    private boolean fieldCompatible(String first, String second) {
        if (second.equals("*")) {
            return true;
        } else {
            return first.equalsIgnoreCase(second);
        }
    }

    public boolean match(EventDescription template) {
        return fieldMatch(application, template.application) &&
                fieldMatch(activity, template.activity) &&
                fieldMatch(eventName, template.eventName) &&
                fieldMatch(userGroup, template.userGroup) &&
                fieldMatch(user, template.user) &&
                //   fieldMatch(eventId, template.eventId) &&
                fieldMatch(dataId, template.dataId) &&
                fieldMatch(sender, template.sender) &&
                fieldMatch(receiver, template.receiver) &&
                fieldMatch(time, template.time) &&
                fieldMatch(correlationId, template.correlationId) &&
                fieldMatch(sessionId, template.sessionId);
        //         fieldMatch(destinatario, template.destinatario);
    }

    public boolean compatibleWith(EventDescription template) {
        return fieldCompatible(application, template.application) &&
                fieldCompatible(activity, template.activity) &&
                fieldCompatible(eventName, template.eventName) &&
                fieldCompatible(processed, template.processed) &&
                fieldCompatible(userGroup, template.userGroup) &&
                fieldCompatible(user, template.user) &&
                //   fieldCompatible(eventId, template.eventId) &&
                fieldCompatible(dataId, template.dataId) &&
                fieldCompatible(sender, template.sender) &&
                fieldCompatible(receiver, template.receiver) &&
                fieldCompatible(time, template.time) &&
                fieldCompatible(correlationId, template.correlationId) &&
                fieldCompatible(sessionId, template.sessionId) &&
                destinatariCompatible(template) &&
                spheresCompatible(template) &&
                relevantSpheresCompatible(template) &&
                parametersCompatible(template);
    }

    public EventDescription copyEd() {
        EventDescription newEvD = new EventDescription();
        newEvD.setApplication(this.getApplication());
        newEvD.setExplicitEvent(this.getExplicitEvent());
        newEvD.setProcessed(this.getProcessed());
        newEvD.setActivity(this.getActivity());
        newEvD.setEventName(this.getEventName());
        newEvD.setUserGroup(this.getUserGroup());
        newEvD.setUser(this.getUser());
        newEvD.setSpheres(this.getSpheres());
        newEvD.setRelevantSpheres(this.getRelevantSpheres());
        newEvD.setTab(this.getTab());
        // newEvD.setDestinatario(this.getDestinatario());
        newEvD.setDestinatari(this.getDestinatari());
        // NON SI DEVE COPIARE eventId che deve essere nuovo
        eventId = "" + EventNumber;
        EventNumber++;
        newEvD.setDataId(this.getDataId());
        newEvD.setSender(this.getSender());
        newEvD.setReceiver(this.getReceiver());
        newEvD.setSessionId(this.getSessionId());
        newEvD.setCorrelationId(this.getCorrelationId());
        newEvD.setTime(this.getTime());
        ArrayList arList = new ArrayList();
        for (int i = 0; i < this.parameters.size(); i++) {
            arList.add((this.parameters).get(i));
        }
        newEvD.setParameters(arList);
        return newEvD;
    }

    public EventDescription(FlowEvent event, TreeElement us) {

        // StockWatcher.debug("INIZIO");
        ArrayList ar = UserGroup.getPath(us);
        for (int i = 0; i < ar.size(); i++) {
            TreeElement ch = (TreeElement) ar.get(i);
            if (i == 0) {
                userGroup = ch.getName();
            } else if (i == 1) {
                user = ch.getName();
            } else {
                System.err.println("ERRORE: troppo user" + ch.getName());
            }
        }
        //     StockWatcher.debug("UNO");
        ar = ApplicationDescription.getPath(null, event);
        for (int i = 0; i < ar.size(); i++) {
            TreeElement ch = (TreeElement) ar.get(i);
            if (i == 0) {
                application = ch.getName();
            } else if (i == 1) {
                activity = ch.getName();
            } else if (i == 2) {
                eventName = ch.getName();
            } else {
                System.err.println("ERRORE: troopo evento" + ch.getName());
            }
        }
        //     StockWatcher.debug("DUE");
    }

    public String getDescription() {
        String ret = getApplication() + "." +
                getActivity() + "." +
                getEventName() + "." +
                getUserGroup() + "." +
                getUser() + "." +
                //   getDestinatario() + "." +
                getEventId() + "." +
                getDataId() + "." +
                getSender() + "." +
                getReceiver() + "." +
                getSessionId() + "." +
                getCorrelationId() + "." +
                getTime() + "." +
                explicitEvent + "." +
                tab + ".";
        ret += ".{";
        int sz = parameters.size();

        for (int i = 0; i < sz; i++) {
            ret += parameters.get(i);
            if (i < sz - 1) {
                ret += ",";
            }
        }
        ret += "}.{";
        sz = involvedUsers.size();

        for (int i = 0; i < sz; i++) {
            ret += involvedUsers.get(i);
            if (i < sz - 1) {
                ret += ",";
            }
        }
        ret += "}.{";
        sz = spheres.size();

        for (int i = 0; i < sz; i++) {
            ret += spheres.get(i);
            if (i < sz - 1) {
                ret += ",";
            }
        }
        ret += "}";
        return ret;
    }

    public String getUserGroup() {
        return userGroup;
    }

    public void setUserGroup(String userGroup) {
        this.userGroup = userGroup;
    }

    public String getUser() {
        return user;
    }

    public void setUser(String user) {
        this.user = user;
    }

    public String getApplication() {
        return application;
    }

    public void setApplication(String application) {
        this.application = application;
    }

    public String getActivity() {
        return activity;
    }

    public void setActivity(String activity) {
        this.activity = activity;
    }

    public String getEventName() {
        return eventName;
    }

    public void setEventName(String eventName) {
        this.eventName = eventName;
    }

    /**
     * @return the eventId
     */
    public String getEventId() {
        return eventId;
    }

    /**
     * @param eventId the eventId to set
     */
    public void setEventId(String eventId) {
        this.eventId = eventId;
    }

    /**
     * @return the dataId
     */
    public String getDataId() {
        return dataId;
    }

    /**
     * @param dataId the dataId to set
     */
    public void setDataId(String dataId) {
        this.dataId = dataId;
    }

    /**
     * @return the sender
     */
    public String getSender() {
        return sender;
    }

    /**
     * @param sender the sender to set
     */
    public void setSender(String sender) {
        this.sender = sender;
    }

    /**
     * @return the receiver
     */
    public String getReceiver() {
        return receiver;
    }

    /**
     * @param receiver the receiver to set
     */
    public void setReceiver(String receiver) {
        this.receiver = receiver;
    }

    /**
     * @return the parameters
     */
    public ArrayList getParameters() {
        return parameters;
    }

    /**
     * @param parameters the parameters to set
     */
    public void setParameters(ArrayList parameters) {
        this.parameters = parameters;
    }

    /**
     * @return the time
     */
    public String getTime() {
        return time;
    }

    /**
     * @param time the time to set
     */
    public void setTime(String time) {
        this.time = time;
    }

    @Override
    public ArrayList getChildren() {
        return new ArrayList();

    }

    /*
    @Override
    public String getName() {
    return getDescription();
    }
     */
    /**
     * @return the sessionId
     */
    public String getSessionId() {
        return sessionId;
    }

    /**
     * @param sessionId the sessionId to set
     */
    public void setSessionId(String sessionId) {
        this.sessionId = sessionId;
    }

//    public String getDestinatario() {
//        return destinatario;
//    }
//
//    /**
//     * @param correlationId the correlationId to set
//     */
//    public void setDestinatario(String destinatario) {
//        this.destinatario = destinatario;
//    }
    /**
     * @return the correlationId
     */
    public String getCorrelationId() {
        return correlationId;
    }

    /**
     * @param correlationId the correlationId to set
     */
    public void setCorrelationId(String correlationId) {
        this.correlationId = correlationId;
    }

    public void setParameter(String name, String value) {
        for (int i = 0; i < parameters.size(); i += 2) {
            String parName = parameters.get(i);
            if (parName.equalsIgnoreCase(name.toLowerCase())) {
                //   System.err.println("EventDescription replacing parameter already set: " + name);
                parameters.set(i + 1, value);
                return;
            }
        }
        parameters.add(name.toLowerCase());
        parameters.add(value);
    }

    public void addParameter(String name, String value) {
        /*  for (int i = 0; i < parameters.size(); i += 2) {
        String parName = parameters.get(i);
        if (parName.equals(name)) {
        System.err.println("EventDescription replacing parameter already set: " + name);
        parameters.set(i + 1, value);
        return;
        }
        }*/
        parameters.add(name);
        parameters.add(value);
    }

    public String getParameter(String name) {
        String ret = null;
        for (int i = 0; i < parameters.size(); i += 2) {
            String parName = parameters.get(i);
            if (parName.equalsIgnoreCase(name.toLowerCase())) { // found
                i++;
                if (i < parameters.size()) {
                    ret = parameters.get(i);
                }
                break;
            }
        }
        return ret;
    }

    public String getParameterString() {
        String ret = "";
        for (int i = 0; i < parameters.size(); i += 2) {
            String name = parameters.get(i);
            String value = parameters.get(i + 1);
            ret += name + " = " + value;
            if (i + 1 < parameters.size() - 1) {
                ret += " , ";
            }
        }
        return ret;
    }

    /**
     * @return the filRouge
     */
    public String getFilRouge() {
        return filRouge;
    }

    /**
     * @param filRouge the filRouge to set
     */
    public void setFilRouge(String filRouge) {
        this.filRouge = filRouge;
    }

    /**
     * @return the explicitEvent
     */
    public String getExplicitEvent() {
        return explicitEvent;
    }

    /**
     * @param explicitEvent the explicitEvent to set
     */
    public void setExplicitEvent(String explicitEvent) {
        this.explicitEvent = explicitEvent;
    }

    /**
     * @return the tab
     */
    public String getTab() {
        return tab;
    }

    /**
     * @param tab the tab to set
     */
    public void setTab(String tab) {
        this.tab = tab;
    }

    /**
     * @return the spheres
     */
    public ArrayList<String> getSpheres() {
        return spheres;
    }

    /**
     * @param spheres the spheres to set
     */
    public void setSpheres(ArrayList<String> spheres) {
        this.spheres = spheres;
    }

    /**
     * @return the involvedUsers
     */
    public ArrayList<String> getInvolvedUsers() {
        return involvedUsers;
    }

    /**
     * @param involvedUsers the involvedUsers to set
     */
    public void setInvolvedUsers(ArrayList<String> involvedUsers) {
        this.involvedUsers = involvedUsers;
    }

    public ArrayList<String> getDestinatari() {
        return destinatari;
    }

    public void setDestinatari(ArrayList<String> destinatari) {
        this.destinatari = destinatari;
    }

    public void addDestinatario(String destName) {
        destinatari.add(destName);
    }

    public void removeDestinatario(String destName) {
        destinatari.remove(destName);
    }

    public String getProcessed() {
        return processed;
    }

    public void setProcessed(String processed) {
        this.processed = processed;
    }

    public ArrayList<String> getRelevantSpheres() {
        return relevantSpheres;
    }

    public void setRelevantSpheres(ArrayList<String> relevantSpheres) {
        this.relevantSpheres = relevantSpheres;
    }

    // returns the names of all the parameters of "this"
    public ArrayList<String> getParameterNames() {
        ArrayList<String> parameterNames = new ArrayList();
        for (int i = 0; i < parameters.size(); i += 2) {
            parameterNames.add(parameters.get(i));
        }
        return parameterNames;
    }

    public String toString() {
        return //"app: " + application +
                //"; evName:" +
                eventName +
                "; usr: " + user +
                "; dest: " + destinatari.toString() +
                "; processed: " + processed +
                "; spheres: " + spheres.toString() +
                "; relevant spheres: " + relevantSpheres.toString() +
                //" est.sph: " + estimatedSphere +
                //"; tab: " + tab +
                //"; timetag: " + time +
                getParameterString();
    }

    public boolean parametersCompatible(EventDescription template) {
        boolean out = true;
        for (int i = 0; i < parameters.size(); i += 2) {
            String name = parameters.get(i);
            String value = parameters.get(i + 1);
            if (!fieldCompatible(value, template.getParameter(name))) {
                out = false;
                break;
            }
        }
        return out;
    }

    // checks compatibility of field "spheres"
    public boolean spheresCompatible(EventDescription template) {
        return check(spheres, template.getSpheres());
    }

    // checks compatibility of field "relevantSpheres"
    public boolean relevantSpheresCompatible(EventDescription template) {
        return check(relevantSpheres, template.getRelevantSpheres());
    }

    // if the template has the list of spheres empty, the spheres are
    // irrelevant for the compatibility check or the event has an empty sphere list.
    // In the other cases, the method returns true if the first list of spheres
    // has an intersection with the list of spheres of template (perfect mach is meaningless
    // because cyclic events could be disambiguated along time)
    private boolean check(ArrayList<String> sphs, ArrayList<String> templSpheres) {
        boolean out = false;
        if (templSpheres.size() == 0) // * case or empty sphere field
        {
            out = true;
        } else {
            out = EventUtilities.intersects(sphs, templSpheres);
        }
        return out;
    }

    // returns true if the destinatari field of the template is empty
    // or if it has the same list of destinatary users (leaving order of occurrence apart)
    public boolean destinatariCompatible(EventDescription template) {
        boolean out = true;
        ArrayList<String> dest = template.getDestinatari();
        if (dest.size() != 0) { // if size==0 --> * case
            if (dest.size() != destinatari.size()) // not the same list of users
            {
                out = false;
            } else {
                for (int i = 0; i < destinatari.size(); i++) {
                    if (!dest.contains(destinatari.get(i))) {
                        out = false;
                        break;
                    }
                }
            }
        }
        return out;
    }
    // creates a template with the same structure as this, where
    // only some fields are instantiated, and the others are set to "*" or []
    // NB: the template must be used in read-only mode, as its complex fields
    // (e.g., spheres) are not cloned from those of the source event

    public EventDescription createtemplate(boolean application, boolean explicitEvent,
            boolean processed, boolean eventName, boolean user,
            boolean spheres, boolean relevantSpheres,
            boolean tab, boolean time, boolean eventId, boolean activity,
            boolean userGroup, boolean dataId, boolean sender, boolean receiver,
            boolean correlationId, boolean sessionId,
            boolean destinatari, ArrayList<String> parNames) {
        EventDescription ev = copyEd(); // clones the event "this"
        if (!application) {
            ev.setApplication("*"); // sets all irrelevant fields to "*"
        }
        if (!explicitEvent) {
            ev.setExplicitEvent("*");
        }
        if (!processed) {
            ev.setProcessed("*");
        }
        if (!eventName) {
            ev.setEventName("*");
        }
        if (!user) {
            ev.setUser("*");
        }
        if (!spheres) {
            ev.setSpheres(new ArrayList());
        }
        if (!relevantSpheres) {
            ev.setRelevantSpheres(new ArrayList());
        }
        if (!tab) {
            ev.setTab("*");
        }
        if (!time) {
            ev.setTime("*");
        }
        // eventId??
        if (!activity) {
            ev.setActivity("*");
        }
        if (!userGroup) {
            ev.setUserGroup("*");
        }
        if (!dataId) {
            ev.setDataId("*");
        }
        if (!sender) {
            ev.setSender("*");
        }
        if (!receiver) {
            ev.setReceiver("*");
        }
        if (!correlationId) {
            ev.setCorrelationId("*");
        }
        if (!sessionId) {
            ev.setSessionId("*");
        }
        if (!destinatari) {
            ev.setDestinatari(new ArrayList());
        }
        // sets all the parameters not occurring in "parameterNames" to "*"
        for (int i = 0; i < parameters.size(); i += 2) {
            String name = parameters.get(i);
            if (parNames != null) {
                if (!parNames.contains(name)) {
                    ev.setParameter(name, "*");
                }
            }
        }
        return ev;
    }

    // returns the list of parameter names associated to "this"
    // in the match table
    public ArrayList<String> getMatchParameters() {
        return (EventUtilities.getEventMatchTable()).get(application).get(eventName);
    }

    public void addSphere(String sphere) {
        spheres.add(sphere);
    }
}
