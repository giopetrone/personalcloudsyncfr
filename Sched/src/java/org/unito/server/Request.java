/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package org.unito.server;

import org.unito.client.StartInterval;
import org.unito.client.Task;
import org.unito.client.TaskGroup;

/**
 *
 * @author marino
 */
public class Request {

    private TaskGroup tasks;
    String action = ""; //move";
    String taskNet = ""; //move";
    String task = "";
   // StartInterval choice = new StartInterval("",0,0);
    StartInterval choice = null;
    String requestString1 = "<request>\n";
    String actionString1 = "<action task=\"";
    String actionString2 = "\" type=\"";
    String actionString3 = "\" />\n";    // era con un \" in piu' al fondo???
    String choiceString1 = "<choice start=\"";
    String choiceString2 = "\" />\n";
    String tasknetString1 = "<tasknet>\n<tasks>\n";
    String tasknetString2 = "</tasks>\n<precs>\n";
    String tasknetString3 = "</precs>\n</tasknet>\n";
    String scheduleString1 = "<schedule>\n";
    String scheduleString2 = "</schedule>\n";
    String requestString2 = "</request>\n";
    /*
    <request>
    <action task="A1" type="move" />
    <choice start="(10,12)" />
    <tasknet>
    <tasks>
    <task name="A1" dur="3" end="(1,20)" start="(1,20)" />
    <task name="A2" dur="2" end="(1,30)" start="(1,30)" />
    </tasks>
    <precs>
    <prec name="A1">
    <succ name="A2" />
    <succ name="A3" />
    </prec>
    <prec name="A2">
    <succ name="A3" />
    </prec>
    </precs>
    </tasknet>
    <schedule>
    <stask name="A1" start="15" />
    <stask name="A2" start="18" />
    </schedule>
    </request>
     */

    public Request(TaskGroup itasks, String tName, String taskNet, String action) {
        task = tName;
        tasks = itasks;
        this.action = action;
        this.taskNet = taskNet;
        this.choice = itasks.getChoice();
    }

    public String toServerString() {
        String ret = requestString1;
        ret += actionString1;
        ret += task;
        ret += actionString2;
        ret += action;
        ret += actionString3;
        if (choice != null){
        ret += choiceString1;
        ret += choice.toRequest();
        ret += choiceString2;
        }
        ret += tasknetString1;

        if (tasks != null) {
            for (Task t : tasks.getTasks()) {
                ret += t.toRequest(true) + "\n";
            }
        }
        ret += tasknetString2;
        if (tasks != null) {
            for (Task t : tasks.getTasks()) {
                ret += t.toRequest(false);
            }
        }
        ret += tasknetString3;
        ret += scheduleString1;
        //  <stask name="A1" start="15" />
         if (tasks != null) {
            for (Task t : tasks.getTasks()) {
                int sta = tasks.getOfficialSchedule(t.getName());
                ret += "<stask name=\""+t.getName()+"\" start=\""+ sta + "\" />\n";
            }
        }
        ret += scheduleString2;
        ret += requestString2;
        return ret;
    }
}