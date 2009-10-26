/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package giga;

import appsusersevents.client.EventDescription;
import com.gigaspaces.events.NotifyActionType;
import net.jini.core.entry.UnusableEntryException;
import com.j_spaces.core.IJSpace;
import com.j_spaces.core.client.EntryArrivedRemoteEvent;
import com.j_spaces.core.client.INotifyDelegatorFilter;
import java.util.ArrayList;

/**
 *
 * @author marino
 */
public class Filter implements INotifyDelegatorFilter {

    private String user;
    protected ArrayList <EventDescription> descList;

    public void init(IJSpace space, Object entry) {
        System.out.println(" ->> Init called - Registration Template:" + entry);
    }

// only messages with m_string = aaa will be delivered to the registered client
    public boolean process(EntryArrivedRemoteEvent theEvent) {
        EntryArrivedRemoteEvent arrivedRemoteEvent = (EntryArrivedRemoteEvent) theEvent;
        MyMetaDataEntry msg;
        boolean valore = false;
        try {
            msg = (MyMetaDataEntry) arrivedRemoteEvent.getObject();
            NotifyActionType notifyType = arrivedRemoteEvent.getNotifyActionType();
            //  String pippo =  Notification66.getNotifyDesc(notifyType);
            String msgStr = " ->> filter.process called - Type:" + Notification66.getNotifyDesc(notifyType) + " - Data:" + msg;
            System.out.println(msgStr);
            EventDescription des = msg.getEvent();
            if (des != null) {
                System.out.println("des  eventName (evento corrente)  = " + des.getEventName());
                valore = checkEvent(des);
            }
            else System.out.println("filter.process des  NULL");
        } catch (UnusableEntryException e) {
            e.printStackTrace();
        }
        System.out.println("filter da': " + valore);
        return valore;
    }

    public void close() {
        System.out.println(" ->> closed called");
    }

    public boolean checkEvent(EventDescription evt) {

         System.out.println("sono in filter.checkEvent");
         for (EventDescription curr: descList) {
             if (evt.compatibleWith(curr))
                 return true;
         }
         return false;
    //   return desc.match(evt) == 0;
    // return true;
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
     * @return the desc
     */
    public ArrayList <EventDescription> getDescList() {
        return descList;
    }

    /**
     * @param desc the desc to set
     */
    public void addDesc(EventDescription desc) {
        this.descList.add(desc);
    }
}

