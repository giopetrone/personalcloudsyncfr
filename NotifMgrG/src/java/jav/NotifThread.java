/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package jav;

import googlecontacts.ContactCall;
import googlecontacts.ContactsExampleParameters;
import googletalkclient.ChatClient;
import java.util.ArrayList;
import java.util.List;
import maillib.SendMailCl;
import pubsublib.event.AtomEvent;

/**
 *
 * @author fabrizio
 */
public class NotifThread extends Thread {

    List<AtomEvent> eventi;
    ChatClient chClient = new ChatClient();
    String email = "icemgr09@gmail.com";
    String pwd = "sync09fr";

    public NotifThread(List<AtomEvent> eventi) {
        this.eventi = eventi;

    }

    public void run() {
        try {
            
            System.err.println("NOTIFICATIONS: " + eventi.size());
           

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
                                    if (sub.equalsIgnoreCase("subscribe"))
                                    {
                                        //sendGMsg(emailSubjectTxt, destim);
                                        chClient.sendGMsg(chClient,emailSubjectTxt, destim);
                                    }
                                }
                            }
                        }
                    } else if (activity.equalsIgnoreCase("a task has been deleted"))
                    {
                        String taskdeletedname = cont.getParameter("Task");
                        String filename = cont.getParameter("File");
                        String assigned = cont.getParameter("Assigned To");
                        if (assigned == null) {
                            assigned = "";
                        }
                        String modifier2 = cont.getUser();
                        if (!assigned.equals("") && assigned != null)
                        {


                            List destinators = new ArrayList();
                            String[] receivers = assigned.split(",");

                            for (int i = 0; i < receivers.length; i++)
                            {
                                String destinator = receivers[i];
                                //Controllo se il destinatario si e' iscritto alle notifiche
                                if (!destinator.equals("") && !destinator.equalsIgnoreCase(modifier2))
                                {

                                    String userDoc = extractPermission(destinator);
                                    System.out.println("File Name in delete: " + userDoc);
                                     String emailSubjectTxt = "A task assigned to you: " + taskdeletedname + "has been deleted";
                                    String sub = GoDoc.checkPermission(userDoc, filename, "Deletetask");
                                    System.out.println("SUB STATUS IN DELETE: " + sub);
                                    if (sub.equalsIgnoreCase("subscribe") && sub != null)
                                    {
                                        destinators.add(destinator);
                                         chClient.sendGMsg(chClient, emailSubjectTxt, destinator);
                                    }
                                }
                            }
                           
                            if(!destinators.isEmpty())
                            {
                            String[] destinatarifinali = (String[]) destinators.toArray(new String[0]);
                            String emailSubjectTxt = "A task assigned to you: "+taskdeletedname+" has been deleted";
                            String link = "http://taskmanagerunito.xoom.it/Flow/"+filename+".xml";
                            String text = "The task "+taskdeletedname+" has been deleted.\nYou can see the feed at: "+link;
                            new SendMailCl().sendSSLMessage(destinatarifinali, emailSubjectTxt, text, email,pwd);
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
                        for (int j = 0; j < destemail.length; j++)
                        {
                            if (destemail[j].contains("@"))
                            {
                                String userPermissionDoc = extractPermission(destemail[j]);
                                if (userPermissionDoc.contains(" ")) {
                                    userPermissionDoc = userPermissionDoc.replace(" ", "");
                                }
                                System.out.println("Nome doc x workflowdone: " + userPermissionDoc);
                                String sub = GoDoc.checkPermission(userPermissionDoc, workflow, "Workflowisdone");
                                System.out.println("SUB: " + sub);
                                String emailSubjectTxt = "The workflow " + workflow + " is completed";
                                if (sub.equalsIgnoreCase("subscribe") && sub != null)
                                {
                                   chClient.sendGMsg(chClient, emailSubjectTxt, destemail[j]);
                                    destemaildone.add(destemail[j]);
                                }
                             
                            }
                            // String sub = WriterPermission.checkNotifications(sendTo[j],workflow,"Workflowisdone");


                        }
                        if(!destemaildone.isEmpty())
                            {
                            String[] destinatarifinali = (String[]) destemaildone.toArray(new String[0]);
                            String emailSubjectTxt = "The Workflow : "+workflow+" has been completed";
                            String link = "http://taskmanagerunito.xoom.it/Flow/"+workflow+".xml";
                            String text = "The Workflow : "+workflow+" has been completed\nYou can see the feed at: "+link;
                            new SendMailCl().sendSSLMessage(destinatarifinali, emailSubjectTxt, text, email,pwd);
                            System.out.println("%%%% SEND MAIL IN Workflow done %%%%%");
                            }



                    }

                }
                //  String[] to = {"fabrizio.torretta@gmail.com"};
                //  new SendMailCl().sendSSLMessage(to,"Prova", "text", email,pwd);

            




    }
    catch



        (



Exception ex) {
            ex.printStackTrace();
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
//    private String sendGMsg(String s,String dest) {
//        // Do something interesting with 's' here on the server.
//        try {
//            connectChatClient(chClient, "icemgr09@gmail.com", "sync09fr");
//          //  connectChatClient(chClient, "sgnmrn@gmail.com", "micio11");
//            //chClient.sendGTalkMsg("gio.petrone@gmail.com", "sgnmrn@gmail.com", "micio11", s, false);
//
//            genIM(chClient, dest, s);
//             System.out.println("%%%%%%%%%%% DOPO IL SEND IM %%%%%%%%%%%%%");
//            disconnectChatClient(chClient);
//        } catch (Exception e) {
//            System.err.println("ECCEZIONE chat: "+e.getMessage());
//        }
//        return "Server says: " + s;
//    }

//    private ContactCall connectContact(String userMail, String psswd) {
//        ContactCall cCallTmp = null;
//        try {
//            String[] myArg = {"--username=" + userMail, "--password=" + psswd, "-contactfeed", "--action=update"};  // OK
//            //   String[] myArg = {"--username=annamaria.goy@gmail.com", "--password=tex_willer", "-contactfeed", "--action=update"};  // OK
//            //String[] myArg = {"--username=" + iceMgrLogin, "--password=" + iceMgrPasswd, "-contactfeed", "--action=update"};  // OK
//            ContactsExampleParameters parameters = new ContactsExampleParameters(myArg); // X USAGE
//            cCallTmp = new ContactCall(parameters);
//            //FINE NUOVO
//        } catch (Exception e) {
//            System.out.println("error");
//        }
//        return cCallTmp;
//    }


    private String extractPermission(String user)
    {

        int index = user.indexOf("@");
        String nomeDoc = user.substring(0,index);
        if(nomeDoc.contains(".")) nomeDoc = nomeDoc.replace(".", "_");
        nomeDoc += ".txt";
        return nomeDoc;
    }

}
