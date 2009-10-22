/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package org.docwatcher.server;
import giga.*;
import appsusersevents.client.EventDescription;

import appsusersevents.client.SingleUser;
import appsusersevents.server.Util;
import java.io.InputStream;
import java.util.ArrayList;


/**
 *
 * @author giovanna
 */
public class GoogleDocsThread extends Thread {
    private ArrayList<SingleUser> uList = new ArrayList();
    private DocumentListDemo googleDoc = null;
    GigaListener writer = null;
    static GoogleDocsThread theThread = null;
    //anna gio
    String docMakerLogin = "";
    String docMakerPasswd = "";

    public GoogleDocsThread(String str, String docMakerLogin, String docMakerPasswd) {
        super(str);
        this.docMakerLogin = docMakerLogin;
        this.docMakerPasswd = docMakerPasswd;
        this.writer = new GigaListener(true, false);
        googleDoc = new DocumentListDemo(loadState(), docMakerLogin, docMakerPasswd);
    }

    public void run() {
        for (int i = 0; i < 100; i++) {
            //      System.out.println(i + " " + getName());
            try {
                for (SingleUser su : uList) {
                    ArrayList<EventDescription> events = googleDoc.doStuffSingle(su);
                    for (EventDescription desc : events) {
                        writer.putEvent(desc);
                    }
                }
                sleep(10 * 1000);
            } catch (InterruptedException e) {
            }
        }
        System.out.println("\n DONE googleThread! " + getName());
    }

    public static void mainWatcher(String docMakerLogin, String docMakerPasswd) {
        //  String docs = new Util().readApps("documentList.xml");

        theThread = new GoogleDocsThread("DocumentWatcher",  docMakerLogin, docMakerPasswd );
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
            SingleUser s = new SingleUser(docMakerLogin, docMakerLogin, docMakerPasswd, "");
            uList.add(s);
        }
    }

}
