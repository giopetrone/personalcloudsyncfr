/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package giga;

import appsusersevents.client.EventDescription;
import java.rmi.RemoteException;
import java.util.logging.Level;
import java.util.logging.Logger;
import net.jini.core.event.RemoteEvent;
import net.jini.core.event.RemoteEventListener;
import net.jini.core.event.UnknownEventException;
import net.jini.core.lease.Lease;
import com.j_spaces.core.IJSpace;
import com.j_spaces.core.client.EntryArrivedRemoteEvent;

import com.gigaspaces.events.EventSessionFactory;
import com.gigaspaces.events.EventSessionConfig;
import com.gigaspaces.events.DataEventSession;
import com.gigaspaces.events.NotifyActionType;

// before running this file, execute
import com.j_spaces.core.client.INotifyDelegatorFilter;
import com.j_spaces.core.client.SQLQuery;
import net.jini.core.event.EventRegistration;
import net.jini.core.lease.UnknownLeaseException;
import org.openspaces.core.space.UrlSpaceConfigurer;
//import com.j_spaces.core.client.NotifyDelegator;
//import com.j_spaces.core.client.NotifyModifiers;
public class Notification66 implements RemoteEventListener {

// IJSpace extends the JavaSpace interface and represents a 
    // GigaSpace instance

    /*
     * thre are 2 steps:
     * 1) call StartSession()
     * */
    private IJSpace mSpace;
    private DataEventSession dataSession;

    public Notification66(IJSpace space) {
        mSpace = space;
    }

    public static Notification66 StartSession(String spaceName, boolean clear) {
        System.out.println("\nWelcome to Gigaspaces NotifyDelegator Example");
        IJSpace space = null;
        try {

            space = new UrlSpaceConfigurer(spaceName).space();
            if (space == null) {
                System.out.println("Space not found: " + spaceName);
                System.exit(-1);
            }
            if (clear) {
                System.out.println("Cleaning Space...");
                space.clear(null, null);
            }
        } catch (Exception ex) {
            // limito output:    ex.printStackTrace();
            return null;
        }
        Notification66 noti = new Notification66(space);
        noti.createDataSession();
        return noti;
    }

    private void createDataSession() {
        try {
            EventSessionFactory factory = EventSessionFactory.getFactory(mSpace);
            EventSessionConfig config = new EventSessionConfig();
            config.setFifo(true);
            dataSession = factory.newDataEventSession(config, null);
        } catch (Exception ex) {
            ex.printStackTrace();
        }
    }

    public EventRegistration addEventToSession(RemoteEventListener listener) {
        EventRegistration reg = null;
        if (dataSession == null) {
            createDataSession();
        }
        try {
            reg = dataSession.addListener(new MyMetaDataEntry(), listener != null ? listener : this, Lease.FOREVER, null, null, NotifyActionType.NOTIFY_ALL);
            System.out.println("event Notification registered.");
        } catch (Exception ex) {
            ex.printStackTrace();
        }
        return reg;
    }

    public EventRegistration addSqlToSession(RemoteEventListener listener, String whereString) {
        EventRegistration reg = null;
        if (dataSession == null) {
            createDataSession();
        }
        try {
            SQLQuery query = new SQLQuery(new MyMetaDataEntry(), whereString);
            reg = dataSession.addListener(query, listener != null ? listener : this, Lease.FOREVER, null, null, NotifyActionType.NOTIFY_ALL);
            System.out.println("SQL Notification registered.");
        } catch (Exception ex) {
            ex.printStackTrace();
        }
        return reg;
    }

    public EventRegistration addFilterToSession(RemoteEventListener listener, INotifyDelegatorFilter filter) {
        EventRegistration reg = null;
        if (dataSession == null) {
            createDataSession();
        }
        try {
            reg = dataSession.addListener(new MyMetaDataEntry(), listener != null ? listener : this, Lease.FOREVER, null, filter != null ? filter : new Filter(), NotifyActionType.NOTIFY_ALL);
            System.out.println("Filtered Notification registered.");
        } catch (Exception ex) {
            ex.printStackTrace();
        }
        return reg;
    }

    public void removeListener(EventRegistration reg ) {
        try {
            dataSession.removeListener(reg);
        } catch (RemoteException ex) {
            Logger.getLogger(Notification66.class.getName()).log(Level.SEVERE, null, ex);
        } catch (UnknownLeaseException ex) {
            Logger.getLogger(Notification66.class.getName()).log(Level.SEVERE, null, ex);
        }
    }


    public MyMetaDataEntry [] readAllEntries() {
        MyMetaDataEntry[] ret = null;
        try {
            MyMetaDataEntry msg = new MyMetaDataEntry();
            ret = (MyMetaDataEntry[]) mSpace.readMultiple(msg, null, 10000);
        } catch (Exception ex) {
            ex.printStackTrace();
        }
        return ret;
    }

    public String readEntryContent() {
        MyMetaDataEntry ret = null;
        try {
            MyMetaDataEntry msg = new MyMetaDataEntry();


            ret = (MyMetaDataEntry) mSpace.read(msg, null, 10000);
        } catch (Exception ex) {
            ex.printStackTrace();
        }
        return ret.getContent();
    }

    public void writeXMLObject(Object o) {
        Lease leases[] = new Lease[3];
        try {
        //    System.out.println("Writing entry to space...");
            MyMetaDataEntry msg = new MyMetaDataEntry(o);
            //   msg.senderId = "gio";
            leases[0] = mSpace.write(msg, null, 20000); // cambiato in 20000
            System.out.println("Writing xml entry '" + o);
        } catch (Exception ex) {
            ex.printStackTrace();
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
            NotifyActionType notifyType = arrivedRemoteEvent.getNotifyActionType();

            String msgStr = "NOTIFY66 Type:" + getNotifyDesc(notifyType) + "; " + "Event-Sequence#: " +
                    theEvent.getSequenceNumber() + "; " + "Content: '" + msg.printString() + "';";

            System.out.println(msgStr);
        } catch (Exception ex) {
            ex.printStackTrace();
        }
    }

    public static String getNotifyDesc(NotifyActionType notifyType) {
        String desc = "";
        /*
        System.out.println("notifyType= " + notifyType.getModifier());
        System.out.println("write= " + NotifyActionType.NOTIFY_WRITE.getModifier());
        System.out.println("take= " + NotifyActionType.NOTIFY_TAKE.getModifier());
        System.out.println("update= " + NotifyActionType.NOTIFY_UPDATE.getModifier());
         * */
        if (notifyType == NotifyActionType.NOTIFY_WRITE) {
            desc = "Write";
        }
        if (notifyType == NotifyActionType.NOTIFY_TAKE) {
            desc = "Take";
        }
        if (notifyType == NotifyActionType.NOTIFY_LEASE_EXPIRATION) {
            desc = "LeaseExpiration";
        }
        if (notifyType == NotifyActionType.NOTIFY_UPDATE) {
            desc = "Update";
        }
        if (notifyType == NotifyActionType.NOTIFY_ALL) {
            desc = "ALL";
        }
        return desc;
    }

    public static void main(String[] args) {

        Notification66 session;
        try {
            session = Notification66.StartSession("jini://localhost/./mySpace", true);
            session.addEventToSession(null);
            //        session.createEventSession();
            for (int i = 0; i < 10; i++) {
                Thread.currentThread().sleep(10000);
            }
        //  String val = session.readEntry();
        //  System.out.println(" entry letta= " +val);

        } catch (Exception ex) {
            ex.printStackTrace();
        }
    }

    void go() throws Exception {
        Lease leases[] = new Lease[3];

        // Registering for all Notifies using NOTIFY_ALL
        // Events will be delivered in FIFO Mode
        mSpace.snapshot(new MyMetaDataEntry());
        //    Entry m_Template = new MyMetaDataEntry();
        /* Register the NotifyDelegator:
         * com.j_spaces.core.client.NotifyDelegator(IJSpace space,
         * Entry template,
         * Transaction txn, RemoteEventListener listener,
         * long lease, MarshalledObject handback,
         * boolean fifoEnabled, int notifyMask)
         */
        //   createEventSession();
        /*     NotifyDelegator m_NotifyDelegator = new NotifyDelegator( m_Space,
        m_Template, null, this, Lease.FOREVER, null , false ,
        NotifyModifiers.NOTIFY_ALL); */


        System.out.println("Writing entries to space...");
        for (int i = 0; i < 3; i++) {
            MyMetaDataEntry msg = new MyMetaDataEntry();
            msg.setContent("Hello World " + i);
            leases[i] = mSpace.write(msg, null, 5000);
            System.out.println("Writing entry '" + i + "' ...Done");
        }

        System.out.println("Update 2 entries in space...");
        for (int i = 0; i < 2; i++) {
            MyMetaDataEntry msg = new MyMetaDataEntry();
            msg.setContent("Hello World " + i);
            MyMetaDataEntry ret = (MyMetaDataEntry) mSpace.read(msg, null, 1000);
            if (ret != null) {
                mSpace.update(ret, null, Lease.FOREVER, 1000);
                System.out.println("Update entry '" + i + "' ...Done");
            }
        }

        System.out.println("Take an entry from space...");
        MyMetaDataEntry msg = new MyMetaDataEntry();
        msg.setContent("Hello World 0");
        MyMetaDataEntry result = (MyMetaDataEntry) mSpace.take(msg, null, 1000);
        System.out.println("Take an entry from space '" + result.getContent() + "' ...Done");

        System.out.println("Cancel a lease from an entry...");
        leases[1].cancel();
        System.out.println("Cancel a lease from an entry...Done");

        // wait to make sure that all notifications arrive
        Thread.sleep(2000);
    }

    public EventDescription getEventOld() {
        MyMetaDataEntry ent = null;
        try {
            MyMetaDataEntry msg = new MyMetaDataEntry();
            ent = (MyMetaDataEntry) mSpace.read(msg, null, 10000);
            return ent.getEvent();
        } catch (Exception ex) {
            ex.printStackTrace();
        }
        return null;
    }

    public String readEntryOld() {
        MyMetaDataEntry ret = null;
        try {
            MyMetaDataEntry msg = new MyMetaDataEntry();


            ret = mSpace == null ? null : (MyMetaDataEntry) mSpace.read(msg, null, 10000);
        } catch (Exception ex) {
            ex.printStackTrace();
        }
        return ret == null ? null : ret.getContent();
    }

    public void writeEntryOld(String s) {
        Lease leases[] = new Lease[3];
        try {
            System.out.println("Writing entry to space...");
            MyMetaDataEntry msg = new MyMetaDataEntry();
            msg.setContent(s);
            leases[0] = mSpace.write(msg, null, 1000000);
            System.out.println("Writing entry '" + s + "' ...Done");
        } catch (Exception ex) {
            ex.printStackTrace();
        }

    }
/*
    public static Notification66 startOld(String spaceName, boolean clear) {
        System.out.println("\nWelcome to Gigaspaces NotifyDelegator Example");
        System.out.println("This example demonstrates GigaSpaces Extended " +
                "Notify Options");

        IJSpace space = null;
        try {
            space = (IJSpace) SpaceFinder.find(spaceName);
            if (space == null) {
                System.out.println("Space not found: " + spaceName);
                System.exit(-1);
            }
            if (clear) {
                System.out.println("Cleaning Space...");
                space.clear(null, null);
            }
        } catch (Exception ex) {
            // limito output:    ex.printStackTrace();
            return null;
        }
        return new Notification66(space);
    }
  */
}

