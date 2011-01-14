/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package googletalkclient;

import java.util.*;
import java.io.*;

import org.jivesoftware.smack.Chat;
import org.jivesoftware.smack.packet.Message;
import org.jivesoftware.smack.ConnectionConfiguration;
import org.jivesoftware.smack.MessageListener;
import org.jivesoftware.smack.Roster;
import org.jivesoftware.smack.RosterEntry;
import org.jivesoftware.smack.XMPPConnection;
import org.jivesoftware.smack.XMPPException;

public class ChatClientOld implements MessageListener {

    XMPPConnection connection;
    static ChatClientOld chClient;
    String email = "icemgr09@gmail.com";
    String pwd = "sync09fr";

    public void login(String userName, String password) throws XMPPException {
        ConnectionConfiguration config = new ConnectionConfiguration("talk.google.com", 5222, "gmail.com");
        connection = new XMPPConnection(config);

        connection.connect();
        connection.login(userName, password);
    }

    public void sendMessage(String message, String to) throws XMPPException {
        Chat chat = connection.getChatManager().createChat(to, this);
        chat.sendMessage(message);
    }

    public void displayBuddyList() {
        Roster roster = connection.getRoster();
        Collection<RosterEntry> entries = roster.getEntries();

        System.out.println("\n\n" + entries.size() + " buddy(ies):");
        for (RosterEntry r : entries) {

            if (r.getStatus() != null) {
                System.out.println(r.getUser() + " status " + (r.getStatus()).toString());

            }
        }
    }

    public void disconnect() {
        connection.disconnect();
    }

    public void processMessage(Chat chat, Message message) {
        if (message.getType() == Message.Type.chat) {
            System.out.println(chat.getParticipant() + " says: " + message.getBody());
        }
    }

//    private void sendGTalkMsg(String receiver, String sender, String passwdSender, String mess, boolean test) throws XMPPException, IOException {
//        // declare variables
//
//        String msg;
//        // turn on the enhanced debugger
//        XMPPConnection.DEBUG_ENABLED = true;
//        // provide your login information here
//        // c.login("gio.petrone@gmail.com", "mer20ia05");
//        this.login(sender, passwdSender);
//        //this.displayBuddyList();
//        this.sendMessage(mess, receiver);
//        System.out.println("------------------------------- in sendGTalkMsg, dopo la send " + receiver);
//        if (test) {
//            this.displayBuddyList();
//            BufferedReader br = new BufferedReader(new InputStreamReader(System.in));
//            System.out.println("-----");
//            System.out.println("-----\n");
//            while (!(msg = br.readLine()).equals("bye")) {
//                // your buddy's gmail address goes here
//                //c.sendMessage(msg, "sgnmrn@gmail.com");
//                //c.sendMessage(msg, "annamaria.goy@gmail.com");
//                this.sendMessage(msg, receiver);
//
//            }
//        }
//
//        this.disconnect();
//        //System.exit(0);
//    }

    // nuove GIO
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

    public String sendGMsg(ChatClient chClient, String s, String dest) {
        // Do something interesting with 's' here on the server.
        try {
            connectChatClient(chClient, "icemgr09@gmail.com", "sync09fr");
            //  connectChatClient(chClient, "sgnmrn@gmail.com", "micio11");
            //chClient.sendGTalkMsg("gio.petrone@gmail.com", "sgnmrn@gmail.com", "micio11", s, false);

            genIM(chClient, dest, s);
            System.out.println("%%%%%%%%%%% DOPO IL SEND IM %%%%%%%%%%%%%");
            disconnectChatClient(chClient);
        } catch (Exception e) {
            System.err.println("ECCEZIONE chat: " + e.getMessage());
        }
        return "Server says: " + s;
    }

    // end nuove GIO
    public static void main(String args[]) throws XMPPException, IOException {
        chClient = new ChatClientOld();
        //   chClient.sendGTalkMsg("gio.petrone@gmail.com", "sgnmrn@gmail.com", "micio11", "ciaoNew", true);
       // chClient.sendGMsg(chClient, "ciao", "gio.petrone@gmail.com");
    }

    public static void Oldmain(String args[]) throws XMPPException, IOException {
        // declare variables
        chClient = new ChatClientOld();
        BufferedReader br = new BufferedReader(new InputStreamReader(System.in));
        String msg;


        // turn on the enhanced debugger
        XMPPConnection.DEBUG_ENABLED = true;


        // provide your login information here
        chClient.login("sgnmrn@gmail.com", "micio11");


        chClient.displayBuddyList();
        System.out.println("-----");
        System.out.println("Enter your message in the console.");
        System.out.println("All messages will be sent to annamaria.goy");
        System.out.println("-----\n");

        while (!(msg = br.readLine()).equals("bye")) {
            // your buddy's gmail address goes here
            //c.sendMessage(msg, "sgnmrn@gmail.com");
            chClient.sendMessage(msg, "gio.petrone@gmail.com");
        }

        chClient.disconnect();
        System.exit(0);
    }
}

