/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package org.unito.client;

import com.google.gwt.user.client.rpc.IsSerializable;
import java.util.ArrayList;

/**
 *
 * @author marino
 */
public class ViaVai implements IsSerializable {

    private Task[] tasks = null;
    private Interval[] globalSchedule = null;
    private Interval[] taskSchedule = null;
    private Interval choice = null;
    private String selectedTask = null;

    public ViaVai() {
    }

    public ViaVai(TaskGroup t) {
        this.selectedTask = t.getSelectedTask();
        this.choice = t.getChoice();
        this.tasks = new Task[t.getTasks().size()];
        int i = 0;
        for (Task ta : t.getTasks()) {
            tasks[i++] = ta;
        }
        this.globalSchedule = new Interval[t.getCurrSchedule().size()];
        i = 0;
        for (Interval inte : t.getCurrSchedule()) {
            //   System.out.println("creo viavai sche= "+ inte.getMin());
            globalSchedule[i++] = inte;
        }
        // System.out.println("t=null??? " + t);
        ArrayList<Interval> iopo = t.getTaskSchedule();
        this.taskSchedule = new Interval[iopo.size()];
        for (int j = 0; j < iopo.size(); j++) {
            System.out.println("creo viavai sche user conflicts= " + t.getTaskSchedule().size() + " " + taskSchedule.length);
            taskSchedule[j] = iopo.get(j);
        }
    }

    /**
     * @return the tasks
     */
    public Task[] getTasks() {
        return tasks;
    }

    /**
     * @return the globalSchedule
     */
    public Interval[] getCurrSchedule() {
        return getGlobalSchedule();
    }

    /**
     * @param tasks the tasks to set
     */
    public void setTasks(Task[] tasks) {
        this.setTasks(tasks);
    }

    /**
     * @param globalSchedule the globalSchedule to set
     */
    public void setCurrSchedule(Interval[] currSchedule) {
        this.setGlobalSchedule(currSchedule);
    }

    /**
     * @return the taskSchedule
     */
    public Interval[] getTaskSchedule() {
        return taskSchedule;
    }

    /**
     * @param taskSchedule the taskSchedule to set
     */
    public void setTaskSchedule(Interval[] taskSchedule) {
        this.setTaskSchedule(taskSchedule);
    }

    /**
     * @return the globalSchedule
     */
    public Interval[] getGlobalSchedule() {
        return globalSchedule;
    }

    /**
     * @param globalSchedule the globalSchedule to set
     */
    public void setGlobalSchedule(Interval[] globalSchedule) {
        this.setGlobalSchedule(globalSchedule);
    }

    /**
     * @return the choice
     */
    public Interval getChoice() {
        return choice;
    }

    /**
     * @param choice the choice to set
     */
    public void setChoice(Interval choice) {
        this.choice = choice;
    }

    /**
     * @return the selectedTask
     */
    public String getSelectedTask() {
        return selectedTask;
    }

    /**
     * @param selectedTask the selectedTask to set
     */
    public void setSelectedTask(String selectedTask) {
        this.selectedTask = selectedTask;
    }
    
}
