/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package org.unito.server;

import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.Date;
import org.apache.http.HttpResponse;
import org.apache.http.client.ClientProtocolException;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.entity.StringEntity;
import org.apache.http.impl.client.DefaultHttpClient;

import org.unito.client.Task;
import org.unito.client.TaskGroup;
import org.unito.client.UiUser;
import testjacop.MyTask;
import testjacop.TaskStore;
import testjacop.User;

/**
 *
 * @author marino
 */
public class TaskCall {

    static TaskStore ts = new TaskStore();

    public TaskGroup doRequest(TaskGroup iTask, String taskName, String taskNet, String mu, String mode, String user, String modalita) {

        // 2 modes:
        // "startintervals" to request places where a task can be placed
        // "tasknet"   if the user wants to move a task, the constraint solver
        // generates additional constraints aindicating that maybe other tasks
        // have to be moved to make room for it in the specified interval;
        // the resulting net is then passes to jacop for generating a schedule

        System.err.println("in dorequest task=" + taskName + "\n" + "tasknet=" + taskNet + "\n" + "mu=" + mu + "\n" + "mode=" + mode + "\n" + "user=" + user + "\n");
        String pr = new Request(iTask, taskName, taskNet, mu, mode, user).toServerString();
        System.err.println("in dorequest request=\n" + pr);
        System.err.println("Ora di inizio=\n" + new Date());
        try {
            DefaultHttpClient httpClient = new DefaultHttpClient();
            String req = "http://localhost:3000/modstn/" + mu + taskNet;
            System.err.println("in dorequest url=" + req + "\n");
            HttpPost postRequest = new HttpPost(req);

            //   "http://localhost:3000/modstn/startintervals");   oppure "tasknet"
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
             System.err.println("Ora di fine torta=\n" + new Date());
            InputStream is = response.getEntity().getContent();
            /*
             * OLD: ArrayList<org.unito.client.Interval> arra =
             * MyDOMParserBean.getIntervals(is); TaskGroup ret = new
             * TaskGroup(); ret.setTaskSchedule(arra);
             *
             */
            TaskGroup ret = MyDOMParserBean.fillReply(is);
            ret.setSelectedTask(taskName);
            httpClient.getConnectionManager().shutdown();
            if (taskNet.equals("tasknet")) {
                // call Scheduler with result from constraint solver
                TaskGroup ret1 = new TaskCall().doConstraints(ret, "start", "new", modalita);
                System.out.println("dopo gianluca e doConstraints");
                  System.err.println("Ora di fine =\n" + new Date());
                return ret1;
            } else {  // taskNet.equals("tasknet")
                return ret;
            }
        } catch (ClientProtocolException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }
        System.out.println(pr);
        return null;
    }

    private void createUsers(TaskGroup taskGroup) {
        // create users , duplicates are ignored
        for (Task t : taskGroup.getTasks()) {
            for (UiUser uu : t.getUsers()) {
                new User(uu.getId(), uu.getWeight());
            }
        }
    }

    private void timeAndOverlap(TaskGroup taskGroup, TaskStore tas) {
        for (Task t1 : taskGroup.getTasks()) {  // avoid overlap bteween disjoint task couples
            for (Task t2 : taskGroup.getTasks()) {
                if (t1 != t2
                        && !t1.canOverlap()
                        && !t2.canOverlap()
                        && !t1.disJoint(t2) /*
                         * && !taskNames.contains(t1.getName()) &&
                         * !taskNames.contains(t2.getName())
                         */) {
                    tas.imposeNonOverlap(t1.getName(), t2.getName());
                }
            }
        }
        for (Task t : taskGroup.getTasks()) {
            ArrayList<String> bef = t.getBefore();
            for (String se : bef) {
                tas.imposeBefore(t.getName(), se);
                System.out.println("inpomgo " + t.getName() + "before " + se);
            }
            ArrayList<String> aft = t.getAfter();
            for (String se : aft) {
                tas.imposeAfter(t.getName(), se);
                System.out.println("inpomgo " + t.getName() + "after " + se);
            }
        }
    }

    private TaskGroup schedule(TaskGroup taskGroup, TaskStore tas, String mode) {
        boolean result = tas.genSchedule(mode);
        if (result) {
            System.out.println("\nProposed schedule for mode:" + mode);
        } else {
            System.out.println("*** No solution!");
        }
        tas.printTasks();
        System.out.println();
        if (result) {
            ArrayList<MyTask> tasks = tas.getTasks();
            TaskGroup ret = new TaskGroup(taskGroup);
            ret.getCurrSchedule().clear();
            for (int k = 0; k < tasks.size(); k++) {
                MyTask st = tasks.get(k);
                int start = st.getStart().dom().min();
                int end = st.getEnd().dom().min();
                int dura = st.getDuration().dom().min();
                ret.setSchedule(st.getName(), start, end);
                System.out.println("Miotask=" + st.getName() + " " + start + " " + end + " " + dura);
            }
            return ret;
        } else {
            return null;
        }
    }

    public TaskGroup doConstraints(TaskGroup taskGroup, String mode, String old, String modalita) {
        // create store of tasks
        if (!old.equals("old")) {   //riparti da 0
            ts = new TaskStore();
            User.clear();
        }
        ArrayList<String> taskNames = new ArrayList<String>();
        System.err.println("in doit tasks size=" + taskGroup.getTasks().size());
        // messo ora fuori dal loop
         createUsers(taskGroup);
        if (mode.equals("schedule")) {
  //          ArrayList<Interval> scheds = taskGroup.getCurrSchedule();
            int scheU = taskGroup.getOfficialSchedule("Tesista Ugo");
            System.err.println("in doit sche dfor ugo:"+scheU);
            for (Task t : taskGroup.getTasks()) {
                int sche = taskGroup.getOfficialSchedule(t.getName());
                System.err.println("task, sched "+ t.getName() + " "+ sche);
                if (sche == -1){
                    System.out.println("uso vincoli per task: "+t.getName());
                }
                if (sche == -1) {
                    // usa limiti originali
                    ts.addTask(t.getName(), t.getMinStartHour(), t.getMaxEndHour(), t.getMinStartHour(), t.getMaxEndHour(), t.getDuration());
                } else {
                    // posto fisso dettao dallo schedule
                    ts.addTask(t.getName(), sche, sche + t.getDuration(), sche, sche + t.getDuration(), t.getDuration());
                }
                // era qui: createUsers(taskGroup); // forse fuori dal loop?
                for (UiUser u : t.getUsers()) {
                    ts.addActorToTask(t.getName(), User.find(u.getId()));
                }
            }
        } else {
            for (Task t : taskGroup.getTasks()) {
                ts.addTask(t.getName(), t.getMinStartHour(), t.getMaxEndHour(), t.getMinStartHour(), t.getMaxEndHour(), t.getDuration());
            //    createUsers(taskGroup); // forse fuori dal loop?
                for (UiUser u : t.getUsers()) {
                    ts.addActorToTask(t.getName(), User.find(u.getId()));
                }
            }
        }
        timeAndOverlap(taskGroup, ts);
        // creo il clone e reimpongo i constraint di base
        TaskStore cts = ts.clone();
        timeAndOverlap(taskGroup, cts);
        System.out.println("Tasks before constraint propagation:");
        ts.printTasks();
        boolean consistent = ts.checkConsistency();
        System.out.println("\nThe set of constraints is consistent? "
                + consistent + "\n");
        if (mode.equals("schedule")) {
            mode = "start";
        }
        if (consistent) {
            return schedule(taskGroup, mode.equals("start") ? ts : cts, mode);
        } else {
            return null;
        }

    }

    public String removeTask(String task) {
        // Do something interesting with 's' here on the server.
        boolean taskRemoved = false;
        if (ts != null && ts.getTasks().size() > 0) {
            taskRemoved = ts.removeTask(task, true);
        } else {
            System.out.println("no tasks to remove!!");
        }
        if (taskRemoved) {
            System.out.println("task " + task + " removed");
            return "true";
        } else {
            System.out.println("I could not remove the task");
            return null;
        }
    }
}
