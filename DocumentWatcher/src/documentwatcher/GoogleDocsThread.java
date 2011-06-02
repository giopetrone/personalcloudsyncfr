/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package documentwatcher;

import giga.*;
import appsusersevents.client.EventDescription;

import appsusersevents.client.SingleUser;
import appsusersevents.server.Util;
import hubstuff.SmartEvent;
import java.io.InputStream;
import java.util.ArrayList;

/**
 *
 * @author marino
 */
public class GoogleDocsThread extends Thread {

    private static boolean publishOnHub = true;
    private ArrayList<SingleUser> uList = new ArrayList();
    private DocumentListDemo googleDoc = null;
    GigaListener writer = null;
    static GoogleDocsThread theThread = null;
    //anna gio
    //    String docMakerLogin = "annamaria.goy@gmail.com";  // fino a che  ???  auth funziona
    //  String docMakerPasswd = "tex_willer";
    //    String docMakerNick= "anna";
    //       String docMakerLogin = "gio.petrone@gmail.com";  // fino a che  ???  auth funziona
    //     String docMakerPasswd = "mer20ia05";
    //       String docMakerNick= "gio";
    //fine Anna gio
    String docMakerLogin = "sgnmrn@gmail.com";  // fino a che  ???  auth funziona
    String docMakerPasswd = "micio11";
    String docMakerNick = "mar";

    public GoogleDocsThread(String str) {
        super(str);
        this.writer = new GigaListener(true, false);
        googleDoc = new DocumentListDemo(loadState());
    }

    public void run() {
        for (int i = 0; i < 100; i++) {
            //      System.out.println(i + " " + getName());
            try {
                for (SingleUser su : uList) {
                    if (publishOnHub) {
                          ArrayList<SmartEvent> events = googleDoc.doStuffHub(su);
                        for (SmartEvent desc : events) {
                            //     writer.putEvent(desc);
                        }
                    } else {
                        ArrayList<EventDescription> events = googleDoc.doStuffSingle(su);
                        for (EventDescription desc : events) {
                            //     writer.putEvent(desc);
                        }
                    }
                }
                sleep(10 * 1000);
            } catch (InterruptedException e) {
            }
        }
        System.out.println("\n DONE googleThread! " + getName());
    }

    public static void main(String[] args) {
        //  String docs = new Util().readApps("documentList.xml");
        theThread = new GoogleDocsThread("DocumentWatcher");
        theThread.setUserList(null);
        theThread.start();
    }

    private String loadState() {
        InputStream is = getClass().getResourceAsStream("documentList.xml");
        String docs = new Util().readApps(is);
        return docs;
    }

    public void setUserList(ArrayList<SingleUser> list) {
        if (list != null) {
            uList = list;
        } else {
            SingleUser s = new SingleUser(docMakerNick, docMakerLogin, docMakerPasswd, "");
            uList.add(s);
        }
    }
}
