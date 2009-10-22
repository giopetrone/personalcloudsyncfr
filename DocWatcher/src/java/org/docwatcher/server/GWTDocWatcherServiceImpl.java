/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package org.docwatcher.server;

import appsusersevents.client.CloudUsers;
import appsusersevents.client.SingleUser;
import com.google.gwt.user.server.rpc.RemoteServiceServlet;

import org.docwatcher.client.GWTDocWatcherService;

/**
 *
 * @author giovanna
 */
public class GWTDocWatcherServiceImpl extends RemoteServiceServlet implements GWTDocWatcherService {
    CloudUsers cloudUsers = new CloudUsers();
    String docMakerLogin ="";
    String docMakerPasswd ="";
    public String myMethod(String s) {
        // Do something interesting with 's' here on the server.
        return "Server says: " + s;
    }

    public String authenticate(String googleId) {

        SingleUser sU = cloudUsers.getUser(googleId);
        //src=   System.out.println("AUTHENTICATE " + s);
        if (sU != null) {
            docMakerLogin = sU.getMailAddress();
            docMakerPasswd = sU.getPwd();
            // chiamare il main di docWatcherApp
        } else {
            System.out.println("singleUSer NULL");
        }
        return docMakerLogin;
    }

    public void startDocWatcher() {
      //  DocsService service = new DocsService("Document List Demo");
        try {
           // service.setUserCredentials(docMakerLogin, docMakerPasswd);
            GoogleDocsThread.mainWatcher(docMakerLogin, docMakerPasswd );
        } catch (Exception ex) {
            ex.printStackTrace();
        }
        System.out.println("CIAO");
    }

}
