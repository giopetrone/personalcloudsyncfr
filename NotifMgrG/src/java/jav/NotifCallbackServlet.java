/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package jav;

import googlecontacts.ContactCall;
import googlecontacts.ContactsExampleParameters;
import googletalkclient.ChatClient;
import java.io.IOException;
import java.io.PrintWriter;
import java.util.List;
import javax.servlet.ServletException;
import javax.servlet.ServletInputStream;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import pubsublib.event.AtomEvent;

/**
 *
 * @author marino
 */
public class NotifCallbackServlet extends HttpServlet {

    ChatClient chClient = new ChatClient();

    /**
     * Processes requests for both HTTP <code>GET</code> and <code>POST</code> methods.
     * @param request servlet request
     * @param response servlet response
     * @throws ServletException if a servlet-specific error occurs
     * @throws IOException if an I/O error occurs
     */
    protected void processRequest(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {

        // ricordare che deploy di callabck e' sotto  <CallbackServlett>/subscribe/pippo

        /*
        System.err.println("headers richiesta:");
        Enumeration e = request.getHeaderNames();
        while (e.hasMoreElements()) {
        String headers = (String) e.nextElement();
        if (headers != null) {
        System.err.println(headers + " " + request.getHeader(headers));
        }
        }
        System.err.println("\n");
         * */
        System.err.println("$$$$$$$$$$$$$$$$$ inzio in callback di notif $$$$$$$$$$$$$$$");
        response.setContentType("text/html;charset=UTF-8");
        PrintWriter out = response.getWriter();
        try {
            String refresh = request.getHeader("notifica");
            String pwd = "sync09fr";
            String email = "icemgr09@gmail.com";
            if (refresh != null) {
                System.err.println("NofiCallB 1  ");
                //ciclic ajax call from UI
                String notif = (String) getServletContext().getAttribute("atomo");
                if (notif != null) {  // we have received new feed content
                    System.err.println("NofiCallB 2  ");
                    out.println(notif); // send it to client
                    // set new feed content to nul to avoid duplicate notifications
                    getServletContext().setAttribute("atomo", null);
                } else {
                    System.err.println("session, get=" + getServletContext());
                    out.println("nulla");
                }
                out.close();
            } else {
                System.err.println("NofiCallB 3  ");
                // request from HUB: challenge or callback
                String hubmode = "";
                if (request.getParameter("hub.mode") != null) {
                    System.err.println("NofiCallB 4  ");
                    hubmode = request.getParameter("hub.mode");
                }
                System.err.println("NofiCallB 5  ");
                if (((hubmode.equals("subscribe")) || (hubmode.equals("unsubscribe")))
                        && (hubmode.length() > 0)) {
                    System.err.println("NofiCallB 6  ");
                    String hubchallenge = "";
                    if (request.getParameter("hub.challenge") != null) {
                        hubchallenge = request.getParameter("hub.challenge");
                        System.err.println("NofiCallB 7  ");
                    }
                    response.setStatus(HttpServletResponse.SC_OK);
                    out.print(hubchallenge);

                } else {
                    System.err.println("NofiCallB 8  ");
                    ServletInputStream inStream = request.getInputStream();
                    String s = "";
                    byte[] b = new byte[1024];
                    int c;
                    while ((c = inStream.read(b)) != -1) {
                        s += new String(b, 0, c);
                    }
                    //System.err.println("notifcallbackservlet, string notified=" +s);

                    //    getServletContext().setAttribute("atomo", s);

                    List<AtomEvent> notifications = FeedUtil.createAtom(s);
                    System.err.println("NOTIFICATIONS: " + notifications.size());
                    System.err.println("NofiCallB 9 " + notifications.size());
                    if (!notifications.isEmpty()) {
                        NotifThread thread = new NotifThread(notifications);
                        thread.start();
                        // sendEvents(notifications);


                    }


                }
            }
        } catch (Exception ex) {
            System.out.println("DENTRO CALLBACK SERVLET: " + ex.getMessage());
            ex.printStackTrace();
        } finally {
            out.close();
        }
    }

    // connettere il chat client ch, una sola volta, prima di cominciare
// a mandare IMs (se no fallisce). Serve account google e password
//    private static void connectChatClient(ChatClient ch, String account, String password) {
//        try {
//            ch.login(account, password);
//        } catch (Exception e) {
//            System.err.println("Failed chatClient login: "
//                    + account + "; " + e.toString());
//        }
//    }
//
//// disconnettere il chat client al termine dello stream di IMs
//    private static void disconnectChatClient(ChatClient ch) {
//        try {
//            ch.disconnect();
//        } catch (Exception e) {
//            System.err.println("Failed ChatClient logout" + e.toString());
//        }
//    }
//
//// per mandare un IM ad un chat client
//    private void genIM(ChatClient chClient, String receiver, String message) {
//
//        if (chClient != null) {
//            try {
//                chClient.sendMessage(message, receiver);
//            } catch (Exception e) {
//                System.err.println("Problem in IM - " + e.toString());
//            }
//        } else {
//            System.err.println("NULL CHAT CLIENT!!");
//        }
//    }
//
//    // subscribe per tasks mgr
//    private String sendGMsg(String s, String dest) {
//        // Do something interesting with 's' here on the server.
//        try {
//            connectChatClient(chClient, "icemgr09@gmail.com", "sync09fr");
//            //  connectChatClient(chClient, "sgnmrn@gmail.com", "micio11");
//            //chClient.sendGTalkMsg("gio.petrone@gmail.com", "sgnmrn@gmail.com", "micio11", s, false);
//
//            genIM(chClient, dest, s);
//            System.out.println("%%%%%%%%%%% DOPO IL SEND IM %%%%%%%%%%%%%");
//            disconnectChatClient(chClient);
//        } catch (Exception e) {
//            System.err.println("ECCEZIONE chat: " + e.getMessage());
//        }
//        return "Server says: " + s;
//    }
    private ContactCall connectContact(String userMail, String psswd) {
        ContactCall cCallTmp = null;
        try {
            String[] myArg = {"--username=" + userMail, "--password=" + psswd, "-contactfeed", "--action=update"};  // OK
            //   String[] myArg = {"--username=annamaria.goy@gmail.com", "--password=tex_willer", "-contactfeed", "--action=update"};  // OK
            //String[] myArg = {"--username=" + iceMgrLogin, "--password=" + iceMgrPasswd, "-contactfeed", "--action=update"};  // OK
            ContactsExampleParameters parameters = new ContactsExampleParameters(myArg); // X USAGE
            cCallTmp = new ContactCall(parameters);
            //FINE NUOVO
        } catch (Exception e) {
            System.out.println("error");
        }
        return cCallTmp;
    }

    private String extractPermission(String user) {

        int index = user.indexOf("@");
        String nomeDoc = user.substring(0, index);
        if (nomeDoc.contains(".")) {
            nomeDoc = nomeDoc.replace(".", "_");
        }
        nomeDoc += ".txt";
        return nomeDoc;
    }

    //GIO
   /* private void sendEvents(List<AtomEvent> eventi) {


    String email = "icemgr09@gmail.com";
    String pwd = "sync09fr";
    System.err.println("NOTIFICATIONS: " + eventi.size());
    try {

    for (AtomEvent cont : eventi) {
    //   System.err.println("new feed content =\n " + cont.toString(true));

    String activity = cont.getActivity();

    if (activity.equalsIgnoreCase("Change Status of Task")) {
    String taskname = cont.getParameter("Task");
    String nomeFile = cont.getParameter("File");
    String dest = cont.getParameter("Assigned To");
    if (dest == null) {
    dest = "";
    }
    String modifier = cont.getUser();
    if (!dest.equals("") && dest != null) {

    String emailSubjectTxt = "Update of task " + taskname + " status";

    String[] sendTo = dest.split(",");

    for (int i = 0; i < sendTo.length; i++) {
    String destim = sendTo[i];
    //Controllo se il destinatario si e' iscritto alle notifiche
    if (destim.contains("gmail.com") && !destim.equals("") && !destim.equalsIgnoreCase(modifier)) {

    String nomeDoc = extractPermission(destim);
    System.out.println("nomeDoc x change status: " + nomeDoc);
    String sub = GoDoc.checkPermission(nomeDoc, nomeFile, "Changestatusoftask");
    System.out.println("SUB STATUS: " + sub);
    if (sub.equalsIgnoreCase("subscribe")) {
    //sendGMsg(emailSubjectTxt, destim);
    chClient.sendGMsg(emailSubjectTxt, destim);
    }
    }
    }
    }
    } else if (activity.equalsIgnoreCase("a task has been deleted")) {
    String taskdeletedname = cont.getParameter("Task");
    String filename = cont.getParameter("File");
    String assigned = cont.getParameter("Assigned To");
    if (assigned == null) {
    assigned = "";
    }
    String modifier2 = cont.getUser();
    if (!assigned.equals("") && assigned != null) {


    List destinators = new ArrayList();
    String[] receivers = assigned.split(",");

    for (int i = 0; i < receivers.length; i++) {
    String destinator = receivers[i];
    //Controllo se il destinatario si e' iscritto alle notifiche
    if (!destinator.equals("") && !destinator.equalsIgnoreCase(modifier2)) {

    String userDoc = extractPermission(destinator);
    System.out.println("File Name in delete: " + userDoc);
    String emailSubjectTxt = "A task assigned to you: " + taskdeletedname + "has been deleted";
    String sub = GoDoc.checkPermission(userDoc, filename, "Deletetask");
    System.out.println("SUB STATUS IN DELETE: " + sub);
    if (sub.equalsIgnoreCase("subscribe") && sub != null) {
    destinators.add(destinator);
    //  sendGMsg(emailSubjectTxt, destinator);
    chClient.sendGMsg(emailSubjectTxt, destinator);
    }
    }
    }

    if (!destinators.isEmpty()) {
    String[] destinatarifinali = (String[]) destinators.toArray(new String[0]);
    String emailSubjectTxt = "A task assigned to you: " + taskdeletedname + " has been deleted";
    String link = "http://www.piemonte.di.unito.it/Flow/" + filename + ".xml";
    //     String link = "http://taskmanagerunito.xoom.it/Flow/"+filename+".xml";
    String text = "The task " + taskdeletedname + " has been deleted.\nYou can see the feed at: " + link;
    new SendMailCl().sendSSLMessage(destinatarifinali, emailSubjectTxt, text, email, pwd);
    System.out.println("%%%% SEND MAIL IN DELETE TASK %%%%%");
    }
    }

    } else if (activity.equalsIgnoreCase("Workflow is Done")) {
    String workflow = cont.getParameter("Workflow");
    String allusers = cont.getParameter("All users");
    System.out.println("USERS prima di substring: " + allusers);
    //allusers = allusers.substring(1, allusers.length());

    String[] destemail = allusers.split(",");
    List destemaildone = new ArrayList();
    for (int j = 0; j < destemail.length; j++) {
    if (destemail[j].contains("@")) {
    String userPermissionDoc = extractPermission(destemail[j]);
    if (userPermissionDoc.contains(" ")) {
    userPermissionDoc = userPermissionDoc.replace(" ", "");
    }
    System.out.println("Nome doc x workflowdone: " + userPermissionDoc);
    String sub = GoDoc.checkPermission(userPermissionDoc, workflow, "Workflowisdone");
    System.out.println("SUB: " + sub);
    String emailSubjectTxt = "The workflow " + workflow + " is completed";
    if (sub.equalsIgnoreCase("subscribe") && sub != null) {
    chClient.sendGMsg(emailSubjectTxt, destemail[j]);
    destemaildone.add(destemail[j]);
    }

    }
    // String sub = WriterPermission.checkNotifications(sendTo[j],workflow,"Workflowisdone");


    }
    if (!destemaildone.isEmpty()) {
    String[] destinatarifinali = (String[]) destemaildone.toArray(new String[0]);
    String emailSubjectTxt = "The Workflow : " + workflow + " has been completed";
    String link = "http://www.piemonte.di.unito.it/Flow/" + workflow + ".xml";
    //  String link = "http://taskmanagerunito.xoom.it/Flow/"+workflow+".xml";
    String text = "The Workflow : " + workflow + " has been completed\nYou can see the feed at: " + link;
    new SendMailCl().sendSSLMessage(destinatarifinali, emailSubjectTxt, text, email, pwd);
    System.out.println("%%%% SEND MAIL IN Workflow done %%%%%");
    }



    }

    }
    } catch (Exception ex) {
    ex.printStackTrace();
    }
    }

     * */
    // <editor-fold defaultstate="collapsed" desc="HttpServlet methods. Click on the + sign on the left to edit the code.">
    /** 
     * Handles the HTTP <code>GET</code> method.
     * @param request servlet request
     * @param response servlet response
     * @throws ServletException if a servlet-specific error occurs
     * @throws IOException if an I/O error occurs
     */
    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        processRequest(request, response);
    }

    /** 
     * Handles the HTTP <code>POST</code> method.
     * @param request servlet request
     * @param response servlet response
     * @throws ServletException if a servlet-specific error occurs
     * @throws IOException if an I/O error occurs
     */
    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        processRequest(request, response);
    }

    /** 
     * Returns a short description of the servlet.
     * @return a String containing servlet description
     */
    @Override
    public String getServletInfo() {
        return "Short description";
    }// </editor-fold>
}
