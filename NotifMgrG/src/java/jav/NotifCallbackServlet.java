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
import java.util.ArrayList;
import java.util.List;
import javax.servlet.ServletException;
import javax.servlet.ServletInputStream;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import pubsublib.event.AtomEvent;
import maillib.SendMailCl;

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
                //ciclic ajax call from UI
                String notif = (String) getServletContext().getAttribute("atomo");
                if (notif != null) {  // we have received new feed content
                    out.println(notif); // send it to client
                    // set new feed content to nul to avoid duplicate notifications
                    getServletContext().setAttribute("atomo", null);
                } else {
                    System.err.println("session, get=" + getServletContext());
                    out.println("nulla");
                }
                out.close();
            } else {
                // request from HUB: challenge or callback
                String hubmode = "";
                if (request.getParameter("hub.mode") != null) {
                    hubmode = request.getParameter("hub.mode");
                }
                if (((hubmode.equals("subscribe")) || (hubmode.equals("unsubscribe")))
                        && (hubmode.length() > 0)) {
                    String hubchallenge = "";
                    if (request.getParameter("hub.challenge") != null) {
                        hubchallenge = request.getParameter("hub.challenge");
                    }
                    response.setStatus(HttpServletResponse.SC_OK);
                    out.print(hubchallenge);
                    /*
                    String hubtopic = "";
                    String hublease = "";
                    String hubverify = "";
                    if (request.getParameter("hub.topic") != null) {
                    hubtopic = request.getParameter("hub.topic");
                    }
                    if (request.getParameter("hub.lease") != null) {
                    hublease = request.getParameter("hub.lease");
                    }
                    if (request.getParameter("hub.verify") != null) {
                    hubverify = request.getParameter("hub.verify");
                    }

                    System.err.println("\nin callback for hub request mode: "
                    + hubmode + " topic: "
                    + hubtopic + "challenge: "
                    + hubchallenge + " verify: "
                    + hubverify + " lease: "
                    + hublease + "\n");
                     */
                } else
                {
                    ServletInputStream inStream = request.getInputStream();
                    String s = "";
                    byte[] b = new byte[1024];
                    int c;
                    while ((c = inStream.read(b)) != -1) {
                        s += new String(b, 0, c);
                    }
                    System.err.println("notifcallbackservlet, string notified=" +s);
                    //   System.err.println("session put =" + getServletContext());
                    // save new feed content; at next refresh call it will be given to client
                    String taskname = "";
                    String newstatus = "";
                    String dest="";
                    String workflow ="";
                    String allusers = "";
                    String modifier = "";
                    String nomeFile = "";
                    String vecchia = (String) getServletContext().getAttribute("atomo");
                    if(vecchia == null) System.err.println("Vecchia null");
                    else System.err.println("VECCHIA NOT NULL: "+vecchia);
                    getServletContext().setAttribute("atomo", s);
                    List<AtomEvent> notifications = FeedUtil.createAtom(s);
                    System.err.println("NOTIFICATIONS: "+notifications.size());
                    if (!notifications.isEmpty()) {
                        System.err.println("new feed content");
                        for (AtomEvent cont : notifications) {
                            System.err.println("new feed content =\n " + cont.toString(true));
                           
                            String activity = cont.getActivity();
                            if(activity.equalsIgnoreCase("Change Status of Task"))
                            {
                                taskname = cont.getParameter("Task");
                                nomeFile = cont.getParameter("File");
                                dest = cont.getParameter("Assigned To");
                                if(dest == null) dest="";
                                modifier = cont.getUser();
                                if(!dest.equals("") && dest != null)
                                {

                                    String emailSubjectTxt = "Update of task "+taskname+" status";

                                    String[] sendTo =  dest.split(",");


                       //gio per incomp param
                   //     new SendMailCl().sendSSLMessage(sendTo, emailSubjectTxt, emailMsgTxt, emailFromAddress,pwd);
                                    for(int i=0;i<sendTo.length;i++)
                                    {
                                        String destim = sendTo[i];
                                        //Controllo se il destinatario si e' iscritto alle notifiche
                                        if(destim.contains("gmail.com") && !destim.equals("") && !destim.equalsIgnoreCase(modifier)) 
                                        {
                                           String sub = WriterPermission.checkNotifications(destim, nomeFile,"Changestatusoftask");
                                           if(sub.equalsIgnoreCase("subscribed")) sendGMsg(emailSubjectTxt,destim);
                                        }
                                    }
                                }
                            }
                            else if(activity.equalsIgnoreCase("Workflow is Done"))
                            {
                                workflow = cont.getParameter("Workflow");
                                allusers = cont.getParameter("All users");
                                allusers = allusers.substring(1, allusers.length());
                                String[] sendTo = allusers.split(",");
                                List destinatari = new ArrayList();
                                for(int j=0;j<sendTo.length;j++)
                                {
                                    String sub = WriterPermission.checkNotifications(sendTo[j],workflow,"workflowisdone");
                                    System.out.println("SUB: "+sub);
                                    if(sub.equalsIgnoreCase("subscribed") && sub !=null) destinatari.add(sendTo[j]);

                                }
                                String emailFromAddress = email;
                                String SSL_FACTORY = "javax.net.ssl.SSLSocketFactory";
                                String SMTP_HOST_NAME = "smtp.gmail.com";
                                String SMTP_PORT = "465";
                                String[] destinatarifinali = (String[]) destinatari.toArray(new String[0]);
                                String emailSubjectTxt ="The workflow "+workflow+" is completed";
                                String link = "http://taskmanagerunito.xoom.it/Flow/"+workflow+".xml";
                                String text = "The workflow "+workflow+" is completed.\nYou can see the feed at: "+link;
                                new SendMailCl().sendSSLMessage(destinatarifinali, emailSubjectTxt, text, email,pwd);
                            }

                        }
                    }


                  
 //DA rendere parametrico -- GIO
                    /*
                    if(checksend==true)
                    {

                        String SMTP_HOST_NAME = "smtp.gmail.com";
                        String SMTP_PORT = "465";
                        String emailMsgTxt = "The task "+taskname + " has changed status.\nThe new status is "+newstatus;
                        String emailSubjectTxt = "Update of task "+taskname+" status";
                        String emailFromAddress = email;
                        String SSL_FACTORY = "javax.net.ssl.SSLSocketFactory";
                        String[] sendTo =  dest.split(",");

                          
                       //gio per incomp param
                   //     new SendMailCl().sendSSLMessage(sendTo, emailSubjectTxt, emailMsgTxt, emailFromAddress,pwd);
                        for(int i=0;i<sendTo.length;i++)
                        {
                            String destim = sendTo[i];
                            if(destim.contains("gmail.com") && !destim.equals("")) sendGMsg(emailSubjectTxt,destim);
                        }
                    }
                    if(workflowdone==true)
                    {
                        allusers = allusers.substring(1, allusers.length());
                        String[] sendTo = allusers.split(",");
                       
                        String emailSubjectTxt ="The workflow "+workflow+" is completed";
                        String link = "http://taskmanagerunito.xoom.it/Flow/"+workflow+".xml";
                        String text = "The workflow "+workflow+" is completed.\nYou can see the feed at: "+link;
                    //gio per incomp param
                            

                    }*/
                    
                }
            }
        } catch (Exception ex) {
            System.out.println("DENTRO CALLBACK SERVLET: "+ex.getMessage());
            ex.printStackTrace();
        } finally {
            out.close();
        }
    }

    // connettere il chat client ch, una sola volta, prima di cominciare
// a mandare IMs (se no fallisce). Serve account google e password
    private static void connectChatClient(ChatClient ch, String account, String password) {
        try {
            ch.login(account, password);
        } catch (Exception e) {
            System.err.println("Failed chatClient login: "
                    + account + "; " + e.toString());
        }
    }

// disconnettere il chat client al termine dello stream di IMs
    private static void disconnectChatClient(ChatClient ch) {
        try {
            ch.disconnect();
        } catch (Exception e) {
            System.err.println("Failed ChatClient logout" + e.toString());
        }
    }

// per mandare un IM ad un chat client
    private void genIM(ChatClient chClient, String receiver, String message) {

        if (chClient != null) {
            try {
                chClient.sendMessage(message, receiver);
            } catch (Exception e) {
                System.err.println("Problem in IM - " + e.toString());
            }
        } else {
            System.err.println("NULL CHAT CLIENT!!");
        }
    }

    // subscribe per tasks mgr
    private String sendGMsg(String s,String dest) {
        // Do something interesting with 's' here on the server.
        try {
            connectChatClient(chClient, "icemgr09@gmail.com", "sync09fr");
          //  connectChatClient(chClient, "sgnmrn@gmail.com", "micio11");
            //chClient.sendGTalkMsg("gio.petrone@gmail.com", "sgnmrn@gmail.com", "micio11", s, false);
           
            genIM(chClient, dest, s);
             System.out.println("%%%%%%%%%%% DOPO IL SEND IM %%%%%%%%%%%%%");
            disconnectChatClient(chClient);
        } catch (Exception e) {
            System.err.println("ECCEZIONE chat: "+e.getMessage());
        }
        return "Server says: " + s;
    }

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
