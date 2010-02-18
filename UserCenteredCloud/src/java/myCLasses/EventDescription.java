package myCLasses;

/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

import java.util.*;

/**
 *
 * @author liliana
 */
public class EventDescription {

    private static int EventNumber;

    private String application;
    private String explicitEvent; // event generated from action on the User Interface
    private String processed; // event has been processed for disambiguation
    private String eventName;
    private String user; // user source of the event
    private ArrayList<String> spheres; // reference spheres
    private ArrayList<String> relevantSpheres; // relevant spheres identified by context
    private String time;
    private ArrayList<String> parameters; // parameters of the event
    private ArrayList<String> parameterNames; // names of the parameters

    //variabili aggiunte per far funzionare il metodo compatibleWith()
    private String eventId;
    private String activity;
    private String userGroup;
    private String dataId;
    private String sender;
    private String receiver;
    private String correlationId;
    private String sessionId;
    private ArrayList<String> destinatari;


    public EventDescription() {
        application = "";
        explicitEvent = "true";
        processed = "no";
        eventName = "";
        user = "";
        destinatari = new ArrayList();
        spheres = new ArrayList();
        relevantSpheres = new ArrayList();
        time = null;
        parameters = new ArrayList();
        parameterNames = new ArrayList();

        // variabili aggiunte per far funzionare compatibleWith()
        eventId = "";
        activity = "";
        userGroup = "";
        dataId = "";
        sender = "";
        receiver = "";
        correlationId = "";
        sessionId = "";

    }

    public EventDescription(String app, String explicit, String proc,
                            String evName, String u,
                            ArrayList<String> dest,
                            ArrayList<String> sph, String time,
                            ArrayList<String> params) {
        application = app;
        explicitEvent = explicit;
        processed = proc;
        eventName = evName;
        user = u;
        spheres = sph;
        relevantSpheres = new ArrayList();
        this.time = time;
        parameters = params;
        parameterNames = new ArrayList();
        for (int i=0; i<parameters.size(); i +=2)
            parameterNames.add(parameters.get(i));

        // variabili aggiunte per far funzionare compatibleWith()
        activity = "";
        userGroup = "";
        dataId = "";
        sender = "";
        destinatari = dest;
        correlationId = "";
        sessionId = "";
    }

    EventDescription(String app, String explicit, String string, String evName, String user) {
        throw new UnsupportedOperationException("Not yet implemented");
    }

    public String getApplication() {
        return application;
    }

    public String getEventName() {
        return eventName;
    }

    public String getExplicitEvent() {
        return explicitEvent;
    }

    public String getProcessed() {
        return processed;
    }

    public String getUser() {
        return user;
    }

    public ArrayList<String> getSpheres() {
        return spheres;
    }

    public ArrayList<String> getRelevantSpheres() {
        return relevantSpheres;
    }

    public String getTime() {
        return time;
    }

    public ArrayList<String> getParameters() {
        return parameters;
    }

    public void setParameters(ArrayList<String> parameters) {
        this.parameters = parameters;
        parameterNames = new ArrayList(); // recomputes parameterNames
        for (int i=0; i<parameters.size(); i +=2)
            parameterNames.add(parameters.get(i));
    }

    // returns the list of parameters <name, value> as a String
    public String getParameterString() {
        String ret = " ";
        for (int i=0; i<parameters.size(); i +=2) {
            String name = parameters.get(i);
            String value = parameters.get(i+1);
            ret += name + " = " + value;
            if (i+1 < parameters.size()-1)
                ret += ", ";
        }
        return ret;
    }

    // returns the names of all the parameters of "this"
    public ArrayList<String> getParameterNames() {
        return parameterNames;
    }

    // returns the value of parameter "name"
    public String getParameter(String name) {
        String ret = null;
        for (int i = 0; i < parameters.size(); i += 2) {
            String parName = parameters.get(i);
            if (parName.equalsIgnoreCase(name)) { // found (loose match)
                i++;
                if (i < parameters.size()) 
                    ret = parameters.get(i);
                break;
            }
        }
        return ret;
    }

    public void setParameter(String name, String value) {
        for (int i = 0; i < parameters.size(); i += 2) {
            String parName = parameters.get(i);
            if (parName.equalsIgnoreCase(name)) { //loose match
                //   System.err.println("EventDescription replacing parameter already set: " + name);
                parameters.set(i + 1, value);
                return;
            }
        }
        parameters.add(name.toLowerCase());
        parameters.add(value);
        parameterNames.add(name.toLowerCase()); // updates parameterNames
    }

    public void setApplication(String application) {
        this.application = application;
    }

    public void setEventName(String eventName) {
        this.eventName = eventName;
    }

    public void setExplicitEvent(String trueFalse) {
        explicitEvent = trueFalse;
    }

    public void setProcessed(String processType) {
        processed = processType;
    }

    public void setUser(String user) {
        this.user = user;
    }


    public void setUserGroup(String userGroup) {
        this.userGroup = userGroup;
    }


    public void setActivity(String activity) {
        this.activity = activity;
    }

    public String getActivity() {
        return activity;
    }

    public String getCorrelationId() {
        return correlationId;
    }

    public String getDataId() {
        return dataId;
    }

    public ArrayList<String> getDestinatari() {
        return destinatari;
    }

    public String getReceiver() {
        return receiver;
    }

    public String getSender() {
        return sender;
    }

    public String getSessionId() {
        return sessionId;
    }

    public String getUserGroup() {
        return userGroup;
    }

    public void addSphere(String sphere) {
        spheres.add(sphere);
    }

    public void setDataId(String dataId) {
        this.dataId = dataId;
    }

    public void setSender(String sender) {
        this.sender = sender;
    }

    public void setReceiver(String receiver) {
        this.receiver = receiver;
    }

    public void setCorrelationId(String correlationId) {
        this.correlationId = correlationId;
    }

    public void setSessionId(String sessionId) {
        this.sessionId = sessionId;
    }

    public void setDestinatari(ArrayList<String> destinatari) {
        this.destinatari = destinatari;
    }

    public void setSpheres(ArrayList<String> sph) {
        spheres = sph;
    }

    public void setRelevantSpheres(ArrayList<String> spheres) {
        relevantSpheres = spheres;
    }

    public void setTime(String timeTag) {
        this.time = timeTag;
    }

    public String toString() {
        return  application + ": " +
                eventName + " by " + user +
                "; dest: " + destinatari.toString() +
                //"; processed: " + processed +
                "; spheres: " + spheres.toString() +
                "; relevant spheres: " + relevantSpheres.toString() +
                //" est.sph: " + estimatedSphere +
                "; timetag: " + time +
                getParameterString();
    }


    //NB: MODIFICATO DA LILIANA RISPETTO A VERSIONE DI MARINO
    private boolean fieldCompatible(String first, String second) {
       if (second.equals("*"))
           return true;
       else
           return first.equalsIgnoreCase(second); //loose match
    }

    public boolean parametersCompatible(EventDescription template) {
        boolean out = true;
        for (int i=0; i<parameters.size(); i +=2) {
            String name = parameters.get(i);
            String value = parameters.get(i+1);
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
        if (templSpheres.size()==0) // * case or empty sphere field
            out = true;
        else out = EventUtilities.intersects(sphs, templSpheres);
        return out;
    }

    // returns true if the destinatari field of the template is empty
    // or if it has the same list of destinatary users (leaving order of occurrence apart)
    public boolean destinatariCompatible(EventDescription template) {
        boolean out = true;
        ArrayList<String> dest = template.getDestinatari();
        if (dest.size()!=0)  { // if size==0 --> * case
            if (dest.size()!=destinatari.size()) // not the same list of users
                out = false;
            else {  for (int i=0; i<destinatari.size(); i++) {
                        if (!dest.contains(destinatari.get(i))) {
                            out = false;
                            break;
                        }
                    }
            }
        }
        return out;
    }

    // returns true if this has the same values for all the variables
    // and parameters instantiated in template. In the evaluation, the
    // method ignores the variables and the parameters instantiated as "*"
    // in the template
    //NB: MODIFICATO DA LILIANA RISPETTO A VERSIONE DI MARINO
    //Fields excluded from comparison: eventId, explicitEvent- check!!
    public boolean compatibleWith(EventDescription template) {
        return fieldCompatible(application, template.application) &&
                fieldCompatible(activity, template.activity) &&
                fieldCompatible(eventName, template.eventName) &&
                fieldCompatible(processed, template.processed) &&
                fieldCompatible(userGroup, template.userGroup) &&
                fieldCompatible(user, template.user) &&
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

    //NB: MODIFICATO DA LILIANA RISPETTO A VERSIONE DI MARINO
    public EventDescription copyEd() {
        EventDescription newEvD = new EventDescription();
        newEvD.setApplication(this.getApplication());
        newEvD.setExplicitEvent(this.getExplicitEvent());
        newEvD.setProcessed(this.getProcessed());
        newEvD.setEventName(this.getEventName());
        newEvD.setUser(this.getUser());
        newEvD.setSpheres(this.getSpheres());
        newEvD.setRelevantSpheres(this.getRelevantSpheres());
        newEvD.setTime(this.getTime());
        // NON SI DEVE COPIARE eventId che deve essere nuovo
        eventId = "" + EventNumber;
        EventNumber++;
        newEvD.setActivity(this.getActivity());
        newEvD.setUserGroup(this.getUserGroup());
        newEvD.setDataId(this.getDataId());
        newEvD.setSender(this.getSender());
        newEvD.setReceiver(this.getReceiver());
        newEvD.setCorrelationId(this.getCorrelationId());
        newEvD.setSessionId(this.getSessionId());
        newEvD.setDestinatari(this.getDestinatari());

        ArrayList arList = new ArrayList();
        for (int i = 0; i < this.parameters.size(); i++) {
            arList.add((this.parameters).get(i));
        }
        newEvD.setParameters(arList);
        return newEvD;
    }

    // creates a template with the same structure as this, where
    // only some fields are instantiated, and the others are set to "*" or []
    // NB: the template must be used in read-only mode, as its complex fields
    // (e.g., spheres) are not cloned from those of the source event
    public EventDescription createtemplate(boolean application, boolean explicitEvent,
                        boolean processed, boolean eventName, boolean user,
                        boolean spheres, boolean relevantSpheres,
                        boolean time, boolean eventId, boolean activity,
                        boolean userGroup, boolean dataId, boolean sender, boolean receiver,
                        boolean correlationId, boolean sessionId,
                        boolean destinatari, ArrayList<String> parNames) {
        EventDescription ev = copyEd(); // clones the event "this"
        if (!application) ev.setApplication("*"); // sets all irrelevant fields to "*"
        if (!explicitEvent) ev.setExplicitEvent("*");
        if (!processed) ev.setProcessed("*");
        if (!eventName) ev.setEventName("*");
        if (!user) ev.setUser("*");
        if (!spheres) ev.setSpheres(new ArrayList());
        if (!relevantSpheres) ev.setRelevantSpheres(new ArrayList());
        if (!time) ev.setTime("*");
        // eventId??
        if (!activity) ev.setActivity("*");
        if (!userGroup) ev.setUserGroup("*");
        if (!dataId) ev.setDataId("*");
        if (!sender) ev.setSender("*");
        if (!receiver) ev.setReceiver("*");
        if (!correlationId) ev.setCorrelationId("*");
        if (!sessionId) ev.setSessionId("*");
        if (!destinatari) ev.setDestinatari(new ArrayList());
                // sets all the parameters not occurring in "parameterNames" to "*"
        for (int i=0; i<parameters.size(); i +=2) {
            String name = parameters.get(i);
            if (!parNames.contains(name))
                ev.setParameter(name, "*");
            }
        return ev;
    }

    // returns the list of parameter names associated to "this"
    // in the match table
    public ArrayList<String> getMatchParameters() {
        return EventUtilities.getEventMatchTable().get(application).get(eventName);
    }


}// end class

