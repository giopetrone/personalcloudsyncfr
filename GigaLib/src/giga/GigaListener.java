/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package giga;

//import documentwatcher.GoogleDocsThread;
import appsusersevents.client.EventDescription;
import com.gigaspaces.events.NotifyActionType;
import com.j_spaces.core.client.EntryArrivedRemoteEvent;

import com.j_spaces.core.client.INotifyDelegatorFilter;
import java.rmi.RemoteException;
import java.util.ArrayList;
import net.jini.core.event.EventRegistration;
import net.jini.core.event.RemoteEvent;
import net.jini.core.event.RemoteEventListener;
import net.jini.core.event.UnknownEventException;

/**
 *
 * @author marino
 */
public class GigaListener implements RemoteEventListener {

    private Notification66 session = null;
    private ArrayList<EventDescription> eventBuf = new ArrayList();
    boolean doneFilters = false;
    private boolean hasDefaultListener = false;
    private boolean getPreexistingEvents = false;
    EventRegistration registration = null;
    private ArrayList<INotifyDelegatorFilter> filters = new ArrayList();

    public GigaListener() {
    }
    /*
    public GigaListener(boolean createDefaultListener) {
    hasDefaultListener = createDefaultListener;
    }
     */

    public GigaListener(boolean createDefaultListener, boolean getPreexistingEvents) {
        hasDefaultListener = createDefaultListener;
        this.getPreexistingEvents = getPreexistingEvents;
    }

    // if default session created
    // we do not return it: i.e. cannot be removed
    // next method  necessary because the Gigaspace may have not been
    // started; therefore each time a method tries to call
    // giga functionalities, it calls started() to make sure,
    // otherwise the call is ignored
    private boolean started() {
        // method may return false if gigaspaces has
        // not started yet
        if (session != null) {
            return true;
        }
        try {
            session = Notification66.StartSession("jini://localhost/./mySpace", true);
            if (session == null) {
                return false;
            }
            if (hasDefaultListener) {
                session.addEventToSession(this);
            }
            // load events preexisting on gigaspace
            if (getPreexistingEvents) {
                loadPreviousEntries();
            }
            return true;
        } catch (Exception ex) {
            ex.printStackTrace();
        }
        return false;
    }

    public EventRegistration addEvent(EventDescription desc) {
        Filter f = new Filter();
        f.setDesc(desc);
        return addFilter(f);
    }

    public EventRegistration addFilter(INotifyDelegatorFilter filter) {
        if (started()) {
            registration = session.addFilterToSession(this, filter);
        }
        filters.add(filter);
        return registration;
    }

    public void removeListener() {
        session.removeListener(registration);
    }

    private void loadPreviousEntries() {
        // load all the events present in the
        // space before this application has started
        // if any filter, run it
        MyMetaDataEntry[] ents = session.readAllEntries();
        for (int i = 0; i < ents.length; i++) {
            EventDescription evt = ents[i].getEvent();
            if (filters.size() == 0) {
                eventBuf.add(evt);
            } else {
                for (int j = 0; j < filters.size(); j++) {
                    Filter f = (Filter) filters.get(j);
                    if (f.checkEvent(evt)) {
                        eventBuf.add(evt);
                        break;
                    }
                }
            }
        }
    }

    public void notify(RemoteEvent theEvent) throws UnknownEventException,
            RemoteException {
        try {
            // since we are using NotifyDelegator, we can obtain the entry
            // that triggered the event
            EntryArrivedRemoteEvent arrivedRemoteEvent =
                    (EntryArrivedRemoteEvent) theEvent;
            // MyMetaDataEntry msg = (MyMetaDataEntry) arrivedRemoteEvent.getEntry();
            MyMetaDataEntry msg = (MyMetaDataEntry) arrivedRemoteEvent.getObject();

            // put in buffer for getEvents()
            eventBuf.add(msg.getEvent());

            NotifyActionType notifyType = arrivedRemoteEvent.getNotifyActionType();

            String msgStr = "NOTIFY- Type:" + Notification66.getNotifyDesc(notifyType) + "; " + "Event-Sequence#: " +
                    theEvent.getSequenceNumber() + "; " + "Content: '" + msg.printString() + "';";

            System.out.println(msgStr);
        } catch (Exception ex) {
            ex.printStackTrace();
        }
    }

    public EventDescription[] getEvents() {
        EventDescription[] de = new EventDescription[eventBuf.size()];
        if (started()) {
            for (int i = 0; i < eventBuf.size(); i++) {
                de[i] = eventBuf.get(i);
            }
        }
        eventBuf.clear();
        return de;
    }

    public void putEvent(EventDescription desc) {
        if (started()) {
            session.writeXMLObject(desc);
        }

    }

    public void putEvents(EventDescription[] events) {
        for (int i = 0; i < events.length; i++) {
            putEvent(events[i]);
        }
    }

}
