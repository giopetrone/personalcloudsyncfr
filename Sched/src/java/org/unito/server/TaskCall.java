/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package org.unito.server;

import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import org.apache.http.HttpResponse;
import org.apache.http.client.ClientProtocolException;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.entity.StringEntity;
import org.apache.http.impl.client.DefaultHttpClient;
import org.unito.client.StartInterval;
import org.unito.client.Task;
import org.unito.client.TaskGroup;
import testjacop.MyTask;
import testjacop.TaskStore;

/**
 *
 * @author marino
 */
public class TaskCall {

    static TaskStore ts = new TaskStore();

    public TaskGroup doRequest(TaskGroup iTask, String taskName, String taskNet, String mode) {

        String pr = new Request(iTask, taskName, taskNet, mode).toServerString();
        System.err.println("in dorequest request=\n" + pr);
        if (false) {  // per prova, per ora va in crash
            ArrayList<org.unito.client.Interval> arra = MyDOMParserBean.getIntervals(null);
            TaskGroup ret = new TaskGroup();
            ret.setTaskSchedule(arra);
            return ret;
        }
        try {
            DefaultHttpClient httpClient = new DefaultHttpClient();
            HttpPost postRequest = new HttpPost(
                       "http://localhost:3000/modstn/"+ taskNet);
                 //   "http://localhost:3000/modstn/startintervals");
            StringEntity input = new StringEntity(pr);
            input.setContentType("text/xml");
            postRequest.setEntity(input);
            HttpResponse response = httpClient.execute(postRequest);
            if (response.getStatusLine().getStatusCode() != 200) {
                throw new RuntimeException("Failed : HTTP error code : "
                        + response.getStatusLine().getStatusCode());
            }
            //  System.out.println("risposta=\n");
            //  response.getEntity().writeTo(System.out);
            InputStream is = response.getEntity().getContent();
            ArrayList<org.unito.client.Interval> arra = MyDOMParserBean.getIntervals(is);
            TaskGroup ret = new TaskGroup();
            ret.setTaskSchedule(arra);
            httpClient.getConnectionManager().shutdown();
            return ret;
        } catch (ClientProtocolException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }
        System.out.println(pr);
        return null;
    }

    public TaskGroup doIt(TaskGroup taskGroup, String mode, String old) {
        // create store of tasks
        if (!old.equals("old")) {   //riparti da 0
             ts = new TaskStore();
        }
        ArrayList<String> taskNames = new ArrayList<String>();
        System.err.println("in doit taskssize=" + taskGroup.getTasks().size());
        for (Task t : taskGroup.getTasks()) {
            ts.addTask(t.getName(), t.getMinStartHour(), t.getMaxEndHour(), t.getMinStartHour(), t.getMaxEndHour(), t.getDuration());
            if (!t.getOverlap()) {
                taskNames.add(t.getName());
            }
        }
        ts.imposeNonOverlap(taskNames);
        for (Task t : taskGroup.getTasks()) {
            ArrayList<String> bef = t.getBefore();
            for (String se : bef) {
                ts.imposeBefore(t.getName(), se);
                System.out.println("inpomgo " + t.getName() + "before " + se);
            }
            ArrayList<String> aft = t.getAfter();
            for (String se : aft) {
                ts.imposeAfter(t.getName(), se);
                System.out.println("inpomgo " + t.getName() + "after " + se);
            }
        }
        // creo il clone e reimpongo i constraint di base
        TaskStore cts = ts.clone();
        cts.imposeNonOverlap(taskNames);
        //cts.imposeBefore("T1", "T2");
        for (Task t : taskGroup.getTasks()) {
            ArrayList<String> bef = t.getBefore();
            for (String se : bef) {
                cts.imposeBefore(t.getName(), se);
                System.out.println("inpomgo " + t.getName() + "before " + se);
            }
            ArrayList<String> aft = t.getAfter();
            for (String se : aft) {
                cts.imposeAfter(t.getName(), se);
                System.out.println("inpomgo " + t.getName() + "after " + se);
            }
        }
        System.out.println("Tasks before constraint propagation:");
        ts.printTasks();
        boolean consistent = ts.checkConsistency();
        System.out.println("\nThe set of constraints is consistent? "
                + consistent + "\n");
        if (!consistent) {
            return null;
        }
        if (mode.equals("start")) {
            boolean result = ts.genSchedule("start");
            if (result) {
                System.out.println("\nProposed schedule (start tasks as soon as possible):");
            } else {
                System.out.println("*** No solution!");
            }
            ts.printTasks();
            System.out.println();
            if (result) {
                ArrayList<MyTask> tasks = ts.getTasks();
                TaskGroup ret = new TaskGroup(taskGroup);
                for (int k = 0; k < tasks.size(); k++) {
                    MyTask st = tasks.get(k);
                    String[] arra = ("" + st.getStart()).split(" ", -1);
                    int start = Integer.parseInt(arra[2]);
                    arra = ("" + st.getEnd()).split(" ", -1);
                    int end = Integer.parseInt(arra[2]);
                    arra = ("" + st.getDuration()).split(" ", -1);
                    int dura = Integer.parseInt(arra[2]);
                    ret.addSchedule(new org.unito.client.Interval(st.getName(), start, end));
                    //  curr.setDuration(dura);
                    System.out.println("Miotask=" + st.getName() + " " + start + " " + end + " " + dura);
                }
                return ret;
            } else {
                return null;
            }
        }
        if (mode.equals("end")) {
            boolean otherResult = cts.genSchedule("end");
            if (otherResult) {
                System.out.println("\nAlternative schedule (schedule tasks with earlier deadlines first):");
            } else {
                System.out.println("*** No solution!");
            }
            cts.printTasks();
            if (otherResult) {
                ArrayList<MyTask> tasks = cts.getTasks();
                TaskGroup ret = new TaskGroup(taskGroup);
                for (int k = 0; k < tasks.size(); k++) {
                    MyTask st = tasks.get(k);
                    String[] arra = ("" + st.getStart()).split(" ", -1);
                    int start = Integer.parseInt(arra[2]);
                    arra = ("" + st.getEnd()).split(" ", -1);
                    int end = Integer.parseInt(arra[2]);
                    arra = ("" + st.getDuration()).split(" ", -1);
                    int dura = Integer.parseInt(arra[2]);
                    ret.addSchedule(new org.unito.client.Interval(st.getName(), start, end));
                    System.out.println("Miotask=" + st.getName() + " " + start + " " + end + " " + dura);
                }
                return ret;
            } else {
                return null;
            }
        }
        return null;
    }

     public String removeTask(String task) {
        // Do something interesting with 's' here on the server.
        boolean taskRemoved = ts == null ? false : ts.removeTask(task);
        if (taskRemoved) {
            System.out.println("task T2 removed");
            return "true";
        } else {
            System.out.println("I could not remove the task");
            return null;
        }
    }

}
