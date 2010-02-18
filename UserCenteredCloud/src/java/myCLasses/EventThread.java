package myCLasses;

/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

import java.util.*;
import java.util.Random;

/**
 *
 * @author liliana
 */
public class EventThread extends Thread {

    private EventAnalyzer eventAnalyzer;
    private ArrayList<EventDescription> eventPool;
    private ArrayList<NotificationMgr> notificationManagers;
    private ArrayList<UserAgt> userAgents;

            // number generator
    private Random random;

 //   static EventThread theThread = null;

// per ucciderlo , chiamare interrupt();

    public EventThread(String str, EventAnalyzer evAn,
                       ArrayList<NotificationMgr>notifMgrs,
                       ArrayList<UserAgt>userAgts,
                       ArrayList<EventDescription> evPool) {
        super(str);
        eventAnalyzer = evAn;
        notificationManagers = notifMgrs;
        userAgents = userAgts;
        eventPool = evPool;
        random = new Random();
    }

    public void run() {
        boolean moreEvents = true; // checks if pool size is exhausted
        for (int i=0; i<ConfigurationSettings.timerCycles && moreEvents; i++) {
            //      System.out.println(i + " " + getName());
            try {
               int numEvents = eventPool.size();
               if (numEvents>0) {
                   Date d = new Date();
                   int index = random.nextInt(eventPool.size());
                   EventDescription e = eventPool.get(index);
                                    // set timetag of event to be thrown
                   e.setTime(d.toString());
                   eventAnalyzer.publishEvent(e, userAgents, notificationManagers);
                                //remove event from eventPool
                   eventPool.remove(index);
               }
               else moreEvents = false; // stops thread cycle
               sleep(ConfigurationSettings.timerDelay * 1000);
            } catch (InterruptedException e) {System.err.println(e.toString());}
        }
        System.out.println("\n Event Thread Terminated! " + getName());
    }

/*    public static void main(String[] args) {
        //  String docs = new Util().readApps("documentList.xml");
        EventThread theThread = new EventThread("DocumentWatcher");

        theThread.start();
    }
*/

}
