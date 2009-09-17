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

public class ChatClient implements MessageListener {

    XMPPConnection connection;

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

    public void sendGTalkMsg(String receiver, String sender, String passwdSender, String mess, boolean test) throws XMPPException, IOException {
        // declare variables

        String msg;
        // turn on the enhanced debugger
        XMPPConnection.DEBUG_ENABLED = true;
        // provide your login information here
        // c.login("gio.petrone@gmail.com", "mer20ia05");
        this.login(sender, passwdSender);
        this.displayBuddyList();
        this.sendMessage(mess, receiver);
        System.out.println("------------------------------- in sendGTalkMsg, dopo la send");
        if (test) {
            BufferedReader br = new BufferedReader(new InputStreamReader(System.in));
            System.out.println("-----");
            System.out.println("Enter your message in the console.");
            System.out.println("All messages will be sent to annamaria.goy");
            System.out.println("-----\n");
            while (!(msg = br.readLine()).equals("bye")) {
                // your buddy's gmail address goes here
                //c.sendMessage(msg, "sgnmrn@gmail.com");
                //c.sendMessage(msg, "annamaria.goy@gmail.com");
                this.sendMessage(msg, receiver);

            }
        }

        this.disconnect();
    //System.exit(0);
    }

    public static void main(String args[]) throws XMPPException, IOException {
        ChatClient chClient = new ChatClient();
        chClient.sendGTalkMsg("annamaria.goy@gmail.com", "gio.petrone@gmail.com", "mer20ia05", "ciaoNew", true);
    }

    public static void Oldmain(String args[]) throws XMPPException, IOException {
        // declare variables
        ChatClient c = new ChatClient();
        BufferedReader br = new BufferedReader(new InputStreamReader(System.in));
        String msg;


        // turn on the enhanced debugger
        XMPPConnection.DEBUG_ENABLED = true;


        // provide your login information here
        c.login("gio.petrone@gmail.com", "mer20ia05");


        c.displayBuddyList();
        System.out.println("-----");
        System.out.println("Enter your message in the console.");
        System.out.println("All messages will be sent to annamaria.goy");
        System.out.println("-----\n");

        while (!(msg = br.readLine()).equals("bye")) {
            // your buddy's gmail address goes here
            //c.sendMessage(msg, "sgnmrn@gmail.com");
            c.sendMessage(msg, "annamaria.goy@gmail.com");
        }

        c.disconnect();
        System.exit(0);
    }
}

