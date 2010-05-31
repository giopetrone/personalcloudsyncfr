/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package pubsublib.event;

import com.thoughtworks.xstream.XStream;
import java.util.ArrayList;

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
    private String name = "";
    private String context = "";
    private ArrayList<String> parameters = new ArrayList();
    static long serial = 0;

    public AtomEvent() {
        id = "" + serial++;
        time = "" + System.currentTimeMillis();
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
     * @return the name
     */
    public String getName() {
        return name;
    }

    /**
     * @param name the name to set
     */
    public void setName(String name) {
        this.name = name;
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

    /**
     * @return the parameters
     */
    public ArrayList<String> getParameters() {
        return parameters;
    }

    /**
     * @param parameters the parameters to set
     */
    public void setParameters(ArrayList<String> parameters) {
        this.parameters = parameters;
    }
    public static AtomEvent creaPerProva() {
        AtomEvent ret = new AtomEvent();
        ret.setUser("sgnmrn@gmail.com");
        ret.setApplication("jalava");
        ret.setActivity("review");
        ret.setName("completed");
        return ret;
    }
}
