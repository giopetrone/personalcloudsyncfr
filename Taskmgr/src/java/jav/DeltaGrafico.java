/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package jav;

import pubsublib.event.AtomEvent;
import jav.CalendarCall;

/**
 *
 * @author marino
 */
public class DeltaGrafico {
    private Grafico vecchio;
    private Grafico nuovo;

    public void createChangeEvents(String nomeFile,String owner) {


        int newsize = nuovo.blocks.length;
        int oldsize = vecchio.blocks.length;
        int newconnections = nuovo.connections.length;
        int oldconnections = vecchio.connections.length;
        int countDone = 0;
        int countRect = 0;
        String utenti ="";


        if(newsize < oldsize || newsize > oldsize)
        {
            AtomEvent event = new AtomEvent();
            event.setActivity("Number of blocks is changed");
            // event.setParameter("assigners",found);
            FeedUtil.addEntry("",nomeFile, event);
        }
        if(newconnections < oldconnections || newconnections > oldconnections)
        {

            AtomEvent event = new AtomEvent();
            event.setActivity("Number of connections is changed");
                 // event.setParameter("assigners",found);
            FeedUtil.addEntry("",nomeFile, event);
        }
        for(int j=0;j<newsize;j++)
              {
                  if(nuovo.blocks[j].imageId.equalsIgnoreCase("rect"))
                  {
                      countRect ++;
                      utenti = nuovo.blocks[j].shared+","+nuovo.blocks[j].writers+","+nuovo.blocks[j].owner;
                      String id = nuovo.blocks[j].id;
                      String newstatus = nuovo.blocks[j].type;
                      if(newstatus == null) newstatus ="";
                      if(newstatus.equalsIgnoreCase("Done")) countDone ++;
                      String duedate = nuovo.blocks[j].date;
                      if(duedate == null){}
                      else if(! duedate.equals("Choose a Date(Optional)") && duedate != null && !duedate.equals(""))
                      {
                          String taskname = nuovo.blocks[j].name;
                          CalendarCall.insertInCalendar(duedate,taskname);

                      }
                      if(newstatus == null) newstatus="";
                      String newassign = nuovo.blocks[j].assign;
                      if(newassign == null) newassign = "";
                      for(int i=0;i<oldsize;i++)
                      {
                          if(vecchio.blocks[i].id.equals(id))
                          {
                              String oldstatus = vecchio.blocks[i].type;
                              if(oldstatus == null) oldstatus = "";
                              String oldassign = vecchio.blocks[i].assign;
                              if(oldassign == null) oldassign = "";
                              if(!newstatus.equals(oldstatus))
                              {

                                  AtomEvent event = new AtomEvent(owner, "TaskManager", "Change Status of Task");
                                  event.setParameter("Task", nuovo.blocks[j].name);
                                //  event.setParameter("Permission", "Write");
                                  event.setParameter("New Status", newstatus);
                                  if(!nuovo.blocks[j].assign.equals("") && nuovo.blocks[j].assign !=null)
                                  {
                                      event.setParameter("Assigned To",nuovo.blocks[j].assign);
                                  }
                                  FeedUtil.addEntry("", nomeFile, event);
                              }
                              if(!newassign.equals(oldassign))
                              {
                                  AtomEvent event = new AtomEvent(owner, "TaskManager", "Change Users assigned to Task");
                                  event.setParameter("Task", nuovo.blocks[j].name);
                                //  event.setParameter("Permission", "Write");
                                  event.setParameter("New Assigned users", newassign);
                                  FeedUtil.addEntry("", nomeFile, event);
                              }
                          }
                      }
                  }
              }
        if(countRect == countDone)
        {
            AtomEvent event = new AtomEvent(owner, "TaskManager", "Workflow is Done");
            event.setParameter("Workflow", nomeFile);
            //  event.setParameter("Permission", "Write");
            event.setParameter("All users", utenti);
            FeedUtil.addEntry("", nomeFile, event);
        }








    }
    /**
     * @return the vecchio
     */
    public Grafico getVecchio() {
        return vecchio;
    }

    /**
     * @param vecchio the vecchio to set
     */
    public void setVecchio(Grafico vecchio) {
        this.vecchio = vecchio;
    }

    /**
     * @return the nuovo
     */
    public Grafico getNuovo() {
        return nuovo;
    }

    /**
     * @param nuovo the nuovo to set
     */
    public void setNuovo(Grafico nuovo) {
        this.nuovo = nuovo;
    }

}
