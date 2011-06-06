/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package jav;

import java.net.URL;
import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;
import event.AtomEvent;

/**
 *
 * @author marino
 */
public class Grafico {

    Connection[] connections;
    Block[] blocks;

    public String faGrafo() {
        String ret = "";
        for (int i = 0; i < connections.length; i++) {
            Connection c = connections[i];
            ret += c.source + " -> " + c.target + "\n";
        }
        return ret;
    }

    public void setToTemplate(String owner) {
        String ownerBlock = blocks[0].owner;
        for (int i = 0; i < blocks.length; i++) {
            String imageid = blocks[i].imageId;
            if (ownerBlock == null) {
                blocks[i].owner = owner;
            } else {
                blocks[i].owner = ownerBlock;
            }
            if (imageid.equalsIgnoreCase("rect")) {
                blocks[i].assign = null;
                blocks[i].date = null;
                blocks[i].shared = null;
                blocks[i].type = null;
                blocks[i].cat = null;
                blocks[i].template = "template";
            } else {
                blocks[i].template = "template";
                blocks[i].shared = null;
            }
        }
    }

   public String setUsers(String owner, String writers, String users) {

        List<String> assign = new LinkedList();
        String ownerBlock = blocks[0].owner;
        String assigner = "";
        if (ownerBlock == null) {
            for (int i = 0; i < blocks.length; i++) {
                blocks[i].owner = owner;
                blocks[i].writers = writers;
                blocks[i].shared = users;
            }
        } else {
            for (int i = 0; i < blocks.length; i++) {
                blocks[i].owner = ownerBlock;
                blocks[i].writers = writers;
                blocks[i].shared = users;
            }
        }
        for (int j = 0; j < blocks.length; j++) {
            if (blocks[j].imageId.equalsIgnoreCase("rect")) {
                System.out.println("-------LINKS: " + blocks[j].link);
                String found = blocks[j].assign;
                if (found != null) {
                    assigner += "," + found + ",";
                }
            }
        }
       // users = users + assigner;
        //   System.out.println("USERS: "+users);
        return users;
    }

    public void createNewEvents(String nomeFile, String owner) throws Exception {
        try{
             List<AtomEvent> listaeventi = new ArrayList();
        for (int j = 0; j < blocks.length; j++) {
            if (blocks[j].imageId.equalsIgnoreCase("rect")) {
                String found = blocks[j].assign;
                String duedate = blocks[j].date;
                    //  if(! duedate.equals("Choose a Date(Optional)") && duedate != null && !duedate.equals(""))
                   //   {
                   //       String taskname = blocks[j].name;
                  //        CalendarCall.insertInCalendar(duedate,taskname);

                   //   }
                if (found == null) {
                    found = "";
                }
                if (!found.equals("")) {
                    AtomEvent event = new AtomEvent(owner, "TaskManager", "Assign User");
                    event.setParameter("Task", blocks[j].name);
                    //  event.setParameter("Permission", "Write");
                    event.setParameter("Who", found);
                    listaeventi.add(event);
                }
                String delimiter = ",";
                String[] links;
                if(blocks[j].link == null) blocks[j].link = "";
                if(!blocks[j].link.equals(""))
                {

                links = blocks[j].link.split(delimiter);
                
                for (int i = 0; i < links.length; i++) {
                    
                        AtomEvent event = new AtomEvent(owner, "TaskManager", "Link");
                        event.setParameter("Task", blocks[j].name);
                        //  event.setParameter("Permission", "Write");
                        URL link = new URL(links[i]);
                        event.setParameter("link", link.toExternalForm());
                        event.setParameter("link", link.toString());
                       listaeventi.add(event);
                    }
                }
            }
        }
    // FeedUtil.addEntries("",nomeFile,listaeventi);
    }catch(Exception ex){System.out.println("DENTRO GRAFICO "+ex.getMessage());}}
}
