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

 private  void connectChatClient(ChatClient ch, String account, String password) {
        try {
            ch.login(account, password);
        } catch (Exception e) {
            System.err.println("Failed chatClient login: "
                    + account + "; " + e.toString());
        }
    }

    // disconnettere il chat client al termine dello stream di IMs
    private  void disconnectChatClient(ChatClient ch) {
        try {
            ch.disconnect();
        } catch (Exception e) {
            System.err.println("Failed ChatClient logout" + e.toString());
        }
    }

// per mandare un IM ad un chat client
    private void genIM( String receiver, String message) {

        if (this != null) {
            try {
                sendMessage(message, receiver);
            } catch (Exception e) {
                System.err.println("Problem in IM - " + e.toString());
            }
        } else {
            System.err.println("NULL CHAT CLIENT!!");
        }
    }

    /**
     *
     * @param s  message
     * @param dest email destination
     * @return
     */
    public String sendGMsg(String s, String dest) {
        // Do something interesting with 's' here on the server.
        try {
            connectChatClient(this, email, pwd);
                 
            genIM(dest, s);
            System.out.println("%%%%%%%%%%% DOPO IL SEND IM %%%%%%%%%%%%%");
            disconnectChatClient(this);
        } catch (Exception e) {
            System.err.println("ECCEZIONE chat: " + e.getMessage());
        }
        return "Server says: " + s;
    }

    // end nuove GIO
    public static void main(String args[]) throws XMPPException, IOException {
        ChatClient chClient = new ChatClient();
        //   chClient.sendGTalkMsg("gio.petrone@gmail.com", "sgnmrn@gmail.com", "micio11", "ciaoNew", true);
        chClient.sendGMsg("ciao", "gio.petrone@gmail.com");
    }

    
}

