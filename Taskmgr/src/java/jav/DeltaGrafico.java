/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package jav;

import pubsub.FeedUtil;
import event.AtomEvent;

import java.util.ArrayList;
import java.util.List;

/**
 *
 * @author marino
 */
public class DeltaGrafico {

    private Grafico vecchio;
    private Grafico nuovo;

    public void createChangeEvents(String nomeFile, String owner) {


        int newsize = nuovo.blocks.length;
        int oldsize = vecchio.blocks.length;
        int newconnections = nuovo.connections.length;
        int oldconnections = vecchio.connections.length;
        List<String> newtaskids = new ArrayList();
        List<String> oldtaskids = new ArrayList();
        List<String> deleteTask = new ArrayList();
        int countDone = 0;
        int countRect = 0;
        List<AtomEvent> listaeventi = new ArrayList();
        String utenti = "";


        if (newsize < oldsize || newsize > oldsize) {
            AtomEvent event = new AtomEvent();
            event.setActivity("Number of blocks is changed");
            // event.setParameter("assigners",found);
            // FeedUtil.addEntry("",nomeFile, event);
            listaeventi.add(event);
        }
        if (newconnections < oldconnections || newconnections > oldconnections) {

            AtomEvent event = new AtomEvent();
            event.setActivity("Number of connections is changed");
            // FeedUtil.addEntry("",nomeFile, event);
            listaeventi.add(event);
        }
        oldtaskids = getOldTaskCount(oldsize);

        newtaskids = getNewTaskCount(newsize);

        deleteTask = getDeleteTask(oldtaskids, newtaskids);

        if (!deleteTask.isEmpty()) {
            for (int j = 0; j < oldsize; j++) {
                if (vecchio.blocks[j].imageId.equalsIgnoreCase("rect")) {
                    String id = vecchio.blocks[j].id;
                    for (int z = 0; z < deleteTask.size(); z++) {
                        String idelete = deleteTask.get(z);
                        if (id.equalsIgnoreCase(idelete)) {

                            String assigned = vecchio.blocks[j].assign;
                            if (assigned != null && !assigned.equals("")) {
                                assigned = assigned.substring(1, assigned.length());
                                String modifier = vecchio.blocks[j].owner;
                                String name = vecchio.blocks[j].name;
                                AtomEvent event = new AtomEvent(modifier, "TaskManager", "A task has been deleted");
                                event.setParameter("Task", name);
                                event.setParameter("Assigned To", assigned);
                                event.setParameter("File", nomeFile);
                                listaeventi.add(event);


                            }
                        }

                    }
                }
            }
        }


        for (int j = 0; j < newsize; j++) {
            if (nuovo.blocks[j].imageId.equalsIgnoreCase("rect")) {
                countRect++;
                utenti = nuovo.blocks[j].shared + "," + nuovo.blocks[j].writers + "," + nuovo.blocks[j].owner;
                String id = nuovo.blocks[j].id;
                String newstatus = nuovo.blocks[j].type;
                if (newstatus == null) {
                    newstatus = "";
                }
                if (newstatus.equalsIgnoreCase("Done")) {
                    countDone++;
                }
                String duedate = nuovo.blocks[j].date;

                if (newstatus == null) {
                    newstatus = "";
                }
                String newassign = nuovo.blocks[j].assign;
                if (newassign == null) {
                    newassign = "";
                }
                for (int i = 0; i < oldsize; i++) {
                    if (vecchio.blocks[i].id.equals(id)) {
                        String oldstatus = vecchio.blocks[i].type;
                        if (oldstatus == null) {
                            oldstatus = "";
                        }
                        String oldassign = vecchio.blocks[i].assign;
                        if (oldassign == null) {
                            oldassign = "";
                        }
                        if (!newstatus.equals(oldstatus)) {

                            AtomEvent event = new AtomEvent(owner, "TaskManager", "Change Status of Task");
                            event.setParameter("Task", nuovo.blocks[j].name);
                            event.setParameter("Permission", "Write");
                            event.setParameter("New Status", newstatus);
                            if (nuovo.blocks[j].assign == null) {
                                nuovo.blocks[j].assign = "";
                            }
                            if (!nuovo.blocks[j].assign.equals("") && nuovo.blocks[j].assign != null) {
                                event.setParameter("Assigned To", nuovo.blocks[j].assign);
                                event.setParameter("File", nomeFile);
                            }
                            listaeventi.add(event);
                        }
                        if (!newassign.equals(oldassign)) {
                            AtomEvent event = new AtomEvent(owner, "TaskManager", "Change Users assigned to Task");
                            event.setParameter("Task", nuovo.blocks[j].name);

                            event.setParameter("New Assigned users", newassign);

                            listaeventi.add(event);
                        }
                    }
                }
            }
        }
        if (countRect == countDone && countDone > 0) {
            AtomEvent event = new AtomEvent(owner, "TaskManager", "Workflow is Done");
            event.setParameter("Workflow", nomeFile);
            //  event.setParameter("Permission", "Write");
            event.setParameter("All users", utenti);
            listaeventi.add(event);
        }

        FeedUtil.addEntries("", nomeFile, listaeventi,FeedUtil.isLocalMode()? "local":"remote");






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

    public List<String> getNewTaskCount(int newsize) {
        List<String> newtasks = new ArrayList();
        for (int j = 0; j < newsize; j++) {
            if (nuovo.blocks[j].imageId.equalsIgnoreCase("rect")) {
                newtasks.add(nuovo.blocks[j].id);
            }
        }
        return newtasks;
    }

    public List<String> getOldTaskCount(int oldsize) {
        List<String> oldtasks = new ArrayList();

        for (int j = 0; j < oldsize; j++) {
            if (vecchio.blocks[j].imageId.equalsIgnoreCase("rect")) {
                oldtasks.add(vecchio.blocks[j].id);
            }
        }
        return oldtasks;
    }

    public List<String> getDeleteTask(List<String> oldtaskids, List<String> newtaskids) {
        List<String> found = new ArrayList();
        boolean foundid = false;
        for (int j = 0; j < oldtaskids.size(); j++) {
            String id = oldtaskids.get(j);
            for (int z = 0; z < newtaskids.size(); z++) {
                String id2 = newtaskids.get(z);
                if (id2.equalsIgnoreCase(id)) {
                    foundid = true;
                }
            }
            if (foundid == false) {
                found.add(id);
            }
        }
        return found;

    }
}
