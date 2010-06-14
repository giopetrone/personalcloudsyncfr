/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package pubsublib.event;

import com.thoughtworks.xstream.XStream;
import java.util.ArrayList;
import java.util.Calendar;

/**
 *
 * @author marino
 */
public class AtomEvent {

    private String id = "";
    private String time = "";
    private String user = "";
    private String application = "";
    private String activity = "";
    private String context = "";
    private ArrayList<String> parameters = new ArrayList();

    private static long serial = 0;

    public AtomEvent() {
        id = "" + serial++;
        time = "" + System.currentTimeMillis();
    }

    public AtomEvent(String user, String application, String activity) {
        id = "" + serial++;
        time = "" + System.currentTimeMillis();
        this.user = user;
        this.application = application;
        this.activity = activity;
    }

    public String toString(boolean corto) {
        String ret = "EV";
        if (!user.isEmpty()) {
            ret += " User(" + user + ")";
        }
        if (!application.isEmpty()) {
            ret += " Application(" + application + ")";
        }
        if (!activity.isEmpty()) {
            ret += " Activity(" + activity + ")";
        }
        if (!parameters.isEmpty()) {
            ret += " PAR(";
            for (int i = 0; i < parameters.size(); i += 2) {
                ret += parameters.get(i) + "=" + parameters.get(i + 1);
                if (i < parameters.size() + 1) {
                    ret += ", ";
                }
            }
            ret += ")";
        }
        if (!corto) {
            if (!time.isEmpty()) {
                Calendar cal = Calendar.getInstance();
                cal.setTimeInMillis(Long.parseLong(time));
                ret += " Time(" + cal.toString()+ ")";
            }
               if (!id.isEmpty()) {
                ret += " Id(" + id + ")";
            }
            if (!context.isEmpty()) {
                ret += " Context(" + context + ")";
            }
        }
        return ret;
    }

    public static AtomEvent fromXml(String s) {
        XStream xstream = new XStream();
        try {
            Object ob = xstream.fromXML(s);
            if (ob.getClass() == pubsublib.event.AtomEvent.class) {
                return (AtomEvent) ob;
            }
        } catch (com.thoughtworks.xstream.io.StreamException ex) {
            System.err.println("xstream, getEvent, error in AtomEvent content = " + s);
        }
        return null;
    }

    public String toXml() {
        XStream xstream = new XStream();
        String s = xstream.toXML(this);
        return s;
    }

    private void addParameter(String name, String value) {
        parameters.add(name);
        parameters.add(value);
    }

    public String getParameter(String name) {
        String ret = null;
        for (int i = 0; i < parameters.size(); i += 2) {
            String parName = parameters.get(i);
            if (parName.equalsIgnoreCase(name)) { // found
                i++;
                if (i < parameters.size()) {
                    ret = parameters.get(i);
                }
                break;
            }
        }
        return ret;
    }

    public boolean setParameter(String name, String value) {
        for (int i = 0; i < parameters.size(); i += 2) {
            String parName = parameters.get(i);
            if (parName.equalsIgnoreCase(name.toLowerCase())) {
                parameters.set(i + 1, value);
                return true;
            }
        }
        addParameter(name, value);
        return false;
    }

    public ArrayList<String> getParameterNames() {
        ArrayList<String> ret = new ArrayList();
        for (int i = 0; i < parameters.size(); i += 2) {
            ret.add(parameters.get(i));
        }
        return ret;
    }

    /**
     * @return the user
     */
    public String getUser() {
        return user;
    }

    /**
     * @param user the user to set
     */
    public void setUser(String user) {
        this.user = user;
    }

    /**
     * @return the application
     */
    public String getApplication() {
        return application;
    }

    /**
     * @param application the application to set
     */
    public void setApplication(String application) {
        this.application = application;
    }

    /**
     * @return the acitivity
     */
    public String getActivity() {
        return activity;
    }

    /**
     * @param acitivity the acitivity to set
     */
    public void setActivity(String acitivity) {
        this.activity = acitivity;
    }

    /**
     * @return the context
     */
    public String getContext() {
        return context;
    }

    /**
     * @param context the context to set
     */
    public void setContext(String context) {
        this.context = context;
    }

    public static AtomEvent creaPerProva() {
        AtomEvent ret = new AtomEvent();
        ret.setUser("sgnmrn@gmail.com");
        ret.setApplication("jalava");
        ret.setActivity("review");
        ret.setParameter("name", "completed");
        return ret;
    }
}
