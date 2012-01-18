/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package org.unito.server;

import JaCoP.core.*;
import JaCoP.constraints.*;
import JaCoP.core.IntVar;
import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import org.apache.http.HttpResponse;
import org.apache.http.client.ClientProtocolException;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.entity.StringEntity;
import org.apache.http.impl.client.DefaultHttpClient;
import org.unito.client.Task;
import org.unito.client.TaskGroup;


/**
 *
 * @author marino
 */
public class ServerTask {

    private String name; // task name
    private Store store; // Finite Domain variables store
    private ArrayList<IntVar> FDVars; // list of FD variables
    private IntVar start;
    private IntVar end;
    private IntVar duration;

    /* creates a task by specifying extremes of start and end intervals.
    Suitable for creating new tasks, where the start and end intervals
    have not yet been modified due to constraint propagation */
    public ServerTask(String name, int start1, int start2,
            int end1, int end2, int d,
            Store st, ArrayList<IntVar> FDVars) {
        this.name = name;
        this.store = st;
        this.FDVars = FDVars;
        // declare FD variables and impose local constraints on task
        this.start = new IntVar(st, name + "Start", start1, start2);
        FDVars.add(this.start);
        this.end = new IntVar(st, name + "End", end1, end2);
        FDVars.add(this.end);
        this.duration = new IntVar(st, name + "Dur", d, d);
        FDVars.add(this.duration);
        st.impose(new XltY(this.start, this.end));
        st.impose(new Distance(this.start, this.end, this.duration));
    }

    /* Creates a new task specifying start and end intervals as domains */
    public ServerTask(String name, IntDomain startDom, IntDomain endDom,
            IntDomain durationDom, Store st, ArrayList<IntVar> FDVars) {
        this.name = name;
        this.store = st;
        this.FDVars = FDVars;
        // declare FD variables and impose local constraints on task
        this.start = new IntVar(st, name + "Start", startDom);
        FDVars.add(this.start);
        this.end = new IntVar(st, name + "End", endDom);
        FDVars.add(this.end);
        this.duration = new IntVar(st, name + "Dur", durationDom);
        FDVars.add(this.duration);
        st.impose(new XltY(this.start, this.end));
        st.impose(new Distance(this.start, this.end, this.duration));
    }

    public String getName() {
        return this.name;
    }

    public IntVar getDuration() {
        return this.duration;
    }

    public IntVar getEnd() {
        return this.end;
    }

    public IntVar getStart() {
        return this.start;
    }

    public String toString() {
        return "Task " + name + "; " + start + " ; "
                + end + "; duration " + duration;
    }

    /* clones a task and includes it in a specified store */
    public ServerTask clone(Store st, ArrayList<IntVar> FDVarList) {
        IntDomain startDom = this.start.dom().cloneLight();
        IntDomain endDom = this.end.dom().cloneLight();
        IntDomain durDom = this.duration.dom().cloneLight();
        ServerTask taskClone = new ServerTask(this.name, startDom, endDom,
                durDom, st, FDVarList);
        return taskClone;
    }

    /* impose that this occurs before the task in input */
    public boolean imposeBefore(ServerTask t) {
        if (t != null) {
            Constraint c = new XlteqY(this.end, t.getStart());
            store.impose(c);
            System.out.println("vincolobefore:" + c.toString());
            return true;
        } else {
            return false;
        }
    }

    /* impose that this occurs before or after the task in input */
    public boolean imposeAfter(ServerTask t) {
        if (t != null) {
            Constraint c = new XlteqY(t.getEnd(), this.getStart());
            store.impose(c);
            System.out.println("vincoloafter:" + c.toString());
            return true;
        } else {
            return false;
        }
    }

    /* impose that this occurs before or after the task in input */
    public boolean imposeNonOverlap(ServerTask t) {
        if (t != null) {
            PrimitiveConstraint c1 = new XlteqY(this.end, t.getStart());
            PrimitiveConstraint c2 = new XlteqY(t.getEnd(), this.getStart());
            PrimitiveConstraint[] c = {c1, c2};
            store.impose(new Or(c));
            return true;
        } else {
            return false;
        }
    }

    public static TaskGroup doRequest(TaskGroup iTask, String taskName) {

        String pr = new Request(iTask, taskName,"","").toServerString();
        System.err.println("in dorequest request=\n" + pr);
        if (true){  // per prova, per ora va in crash
              ArrayList<org.unito.client.Interval> arra = MyDOMParserBean.getIntervals(null);
            TaskGroup ret = new TaskGroup();
            ret.setTaskSchedule(arra);
            return ret;
        }
        try {
            DefaultHttpClient httpClient = new DefaultHttpClient();
            HttpPost postRequest = new HttpPost(
                    "http://localhost:3000/modstn/startintervals");
            StringEntity input = new StringEntity(pr);
            input.setContentType("text/xml");
            postRequest.setEntity(input);
            HttpResponse response = httpClient.execute(postRequest);
            if (response.getStatusLine().getStatusCode() != 200) {
                throw new RuntimeException("Failed : HTTP error code : "
                        + response.getStatusLine().getStatusCode());
            }
            InputStream is = response.getEntity().getContent();
            ArrayList<org.unito.client.Interval> arra = MyDOMParserBean.getIntervals(is);
            TaskGroup ret = new TaskGroup();
            ret.setTaskSchedule(arra);
            /*  BufferedReader br = new BufferedReader(
            new InputStreamReader((response.getEntity().getContent())));
            String output;
            System.out.println("Output from Server .... \n");
            while ((output = br.readLine()) != null) {
            System.out.println(output);
            } */
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

    public static TaskGroup doIt(TaskGroup taskGroup, String mode) {
        TaskStoreSave ts = new TaskStoreSave(); // create store of tasks
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
        TaskStoreSave cts = ts.clone();
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
                ArrayList<ServerTask> tasks = ts.getTasks();
                TaskGroup ret = new TaskGroup(taskGroup);
                for (int k = 0; k < tasks.size(); k++) {
                    ServerTask st = tasks.get(k);
                    String[] arra = ("" + st.start).split(" ", -1);
                    int start = Integer.parseInt(arra[2]);
                    arra = ("" + st.end).split(" ", -1);
                    int end = Integer.parseInt(arra[2]);
                    arra = ("" + st.duration).split(" ", -1);
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
                ArrayList<ServerTask> tasks = cts.getTasks();
                TaskGroup ret = new TaskGroup(taskGroup);
                for (int k = 0; k < tasks.size(); k++) {
                    ServerTask st = tasks.get(k);
                    String[] arra = ("" + st.start).split(" ", -1);
                    int start = Integer.parseInt(arra[2]);
                    arra = ("" + st.end).split(" ", -1);
                    int end = Integer.parseInt(arra[2]);
                    arra = ("" + st.duration).split(" ", -1);
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

    public static void main(String[] args) {
        TaskStoreSave ts = new TaskStoreSave(); // create store of tasks
        ts.addTask("T1", 3, 20, 3, 20, 3);
        ts.addTask("T2", 1, 20, 1, 20, 5);
        ts.addTask("T3", 2, 6, 5, 20, 10);

        ArrayList<String> taskNames = new ArrayList<String>();
        taskNames.add("T1");
        taskNames.add("T2");
        taskNames.add("T3");
        ts.imposeNonOverlap(taskNames);
        ts.imposeBefore("T2", "T1");

        // creo il clone e reimpongo i constraint di base
        TaskStoreSave cts = ts.clone();
        cts.imposeNonOverlap(taskNames);
        //cts.imposeBefore("T1", "T2");




        System.out.println("Tasks before constraint propagation:");
        ts.printTasks();

        System.out.println("\nThe set of constraints is consistent? "
                + ts.checkConsistency() + "\n");
        boolean result = ts.genSchedule("start");
        if (result) {
            System.out.println("\nProposed schedule (start tasks as soon as possible):");
        } else {
            System.out.println("*** No solution!");
        }
        ts.printTasks();
        System.out.println();

        boolean otherResult = cts.genSchedule("end");
        if (otherResult) {
            System.out.println("\nAlternative schedule (schedule tasks with earlier deadlines first):");
        } else {
            System.out.println("*** No solution!");
        }
        cts.printTasks();
    }
}
