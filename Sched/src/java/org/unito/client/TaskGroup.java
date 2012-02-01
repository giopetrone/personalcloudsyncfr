/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package org.unito.client;

import com.google.gwt.user.client.Window;
import java.util.ArrayList;

/**
 *
 * @author marino
 */
public class TaskGroup {

    static int giornata = 12;
    static int oreLavoro = 60;
    private static ArrayList<TaskGroup> history = new ArrayList(); // lista di allocazioni
    private ArrayList<Interval> globalSchedule = new ArrayList();  // list of schedule for all tasks
    private ArrayList<Interval> taskSchedule = new ArrayList();  // possible starting intervals for a task
    private ArrayList<Task> tasks = new ArrayList();
    private StartInterval choice = null;

    public static TaskGroup addTaskGroup() {
        TaskGroup ret = new TaskGroup();
        history.add(ret);
        return ret;
    }

    public TaskGroup() {
    }

    public TaskGroup(TaskGroup tg) { // copisa task del parametro
        tasks = tg.tasks;
    }

    public void setChoiceForTask(String tName) {
        Task t = get(tName);
        choice = new StartInterval(tName, t.getOfficialSchedule(), t.getOfficialSchedule() + 1);
        Window.alert("in taskgroup.setChoiceForTas task: " + tName + " " + choice.getMin() + ", " + choice.getMax());
    }

    public TaskGroup(ViaVai via) { // copia task del parametro
        Task[] taski = via.getTasks();
        for (int i = 0; i < taski.length; i++) {
            tasks.add(taski[i]);
        }
        Interval[] currScheduli = via.getCurrSchedule();
        for (int i = 0; i < currScheduli.length; i++) {
            globalSchedule.add(currScheduli[i]);
        }
        Interval[] taskScheduli = via.getTaskSchedule();
        for (int i = 0; i < taskScheduli.length; i++) {
            taskSchedule.add(taskScheduli[i]);
        }
        choice = via.getChoice();
    }

    public static void reset() {
        //     Window.alert("currente history size = "+ history.size());
        history.clear();
    }

    public static TaskGroup current() {
        //     Window.alert("currente history size = "+ history.size());
        return history.get(history.size() - 1);
    }

    public ArrayList<Task> getTasks() {
        return tasks;
    }

    public void addScheduledTask(Task t, int start, int end) {
        tasks.add(t);
        globalSchedule.add(new StartInterval(t.getName(), start, end));
    }

    public void setSchedule(Task t) {
        int sta = t.getSchedule();
        //  Window.alert("setschedule task= "+t.getName() +" sta="+sta);
        if (sta < 0) {
            sta = possibileInizio(t.getName());
            //     Window.alert("possinileinizio= "+sta);
        }
        Interval inte = findIntervalForTask(t.getName());
        if (inte == null) {
            globalSchedule.add(new StartInterval(t.getName(), sta, sta));
        } else {
            inte.setMin(sta);
            inte.setMax(sta);
            //   Window.alert("cambio schedule:"+t.getDefaultSchedule());
            // inte.setMin(t.getDefaultSchedule());
            //  inte.setMax(t.getDefaultSchedule());
        }
    }

    public static String checkAndAddTask(String name, String firstStartHour, String lastEndHour, String duration, String before, String after, String schedule, boolean overlap) {
        if (TaskGroup.current().get(name) != null) {
            return "task already existent: " + name;
        }
        int fis = Integer.parseInt(firstStartHour);
        int las = Integer.parseInt(lastEndHour);
        String msg = "";
        if (fis < 0 || las < 0 || fis >= oreLavoro || las > oreLavoro) {
            msg += "invalid start or end hour: must be 0 <= VALUE <= " + oreLavoro + "\n";
        }
        int dur = Integer.parseInt(duration);
        if (dur < 1 || dur > oreLavoro) {
            msg += "invalid duration: must be 0 <= VALUE <= " + oreLavoro + "\n";
        }
        String[] tmp = before.split(" ", -1);
        for (int i = 0; i < tmp.length; i++) {
            if (!tmp[i].equals("") && !tmp[i].equals(" ")) {
                if (!TaskGroup.exists(tmp[i])) {
                    msg += "Before: Task not found: " + tmp[i] + "\n";
                }
            }
        }
        tmp = after.split(" ", -1);
        for (int i = 0; i < tmp.length; i++) {
            if (!tmp[i].equals("") && !tmp[i].equals(" ")) {
                if (!TaskGroup.exists(tmp[i])) {
                    msg += "After: Task not found: " + tmp[i] + "\n";
                }
            }
        }
        if (msg.equals("")) {
            Task tat = new Task(name, firstStartHour, lastEndHour, duration, before, after, schedule, overlap);
            TaskGroup.add(tat);
            TaskGroup.current().setSchedule(tat);
        }
        return msg;
    }

    public static void add(Task t) {
        current().tasks.add(t);
    }

    public  void addI(Task t) {
        tasks.add(t);
    }

    public ArrayList<Interval> getCurrSchedule() {
        return globalSchedule;
    }

    public void addSchedule(Interval interval) {
        globalSchedule.add(interval);
    }

    public void changeSchedule(Interval interval) {
        Interval inte = findIntervalForTask(interval.getName());
        inte.setMin(interval.getMin());
        inte.setMax(interval.getMax());
    }

    public void removeSchedule(String task) {
        Interval inte = findIntervalForTask(task);
        globalSchedule.remove(inte);
    }

    public void clearSchedule() {
        globalSchedule.clear();
    }

    public void updateTaskSlots(TaskGroup tg) {
        taskSchedule = tg.getTaskSchedule();
    }

    public static void updateSchedule(TaskGroup tg) {
        if (history.contains(tg)) {
            Window.alert("in TaskGroup.updateSchedule() duplicate tg");
        } else {
            history.add(tg);
        }
    }

    private Interval findIntervalForTask(String name) {
        for (Interval inte : globalSchedule) {
            if (inte.getName().equals(name)) {
                return inte;
            }
        }
        return null;
    }

    public int getOfficialSchedule(String name) {
        Interval inte = findIntervalForTask(name);
        return inte == null ? -1 : inte.getMin();
    }

    private int possibileInizio(String name) {
        boolean[] libero = new boolean[oreLavoro];
        for (int i = 0; i < oreLavoro; i++) {
            libero[i] = true;
        }
        Task questo = get(name);
        for (Task t : current().tasks) {
            if (t != questo) {
                int sta = current().getOfficialSchedule(t.getName());
                if (sta >= 0) {
                    for (int i = 0; i < t.getDuration(); i++) {
                        libero[sta + i] = false;
                    }
                }
            }
        }
        int inizio = questo.getFirstStartHour();
        int fine = questo.getLastEndHour();
        int dura = questo.getDuration();
        for (int k = inizio; k < fine - dura; k++) {
            if (libero[k]) {  // un' ora libera
                boolean ok = true;
                for (int kk = k + 1; kk < k + dura && ok; kk++) {
                    ok = libero[kk];
                }
                if (ok) {
                    return k;
                }
            }
        }
        return -1;
    }

    public static String[] retr(boolean showAlt) {
        String[] ret = new String[oreLavoro];
        for (int i = 0; i < oreLavoro; i++) {
            ret[i] = "";
        }
        // Window.alert("in retr task sz =" + current().tasks.size());
        for (Task t : current().tasks) {
            int sta = current().getOfficialSchedule(t.getName());
            //   Window.alert("in retr task=" + t.getName() + " " +sta);
            if (sta >= 0) {
                for (int i = 0; i < t.getDuration(); i++) {
                    if (ret[sta + i].equals("")) {
                        ret[sta + i] = t.getName();
                    } else {
                        ret[sta + i] += "; " + t.getName();
                    }
                }
            }
        }
        if (showAlt) {
            for (Interval inte : current().taskSchedule) {
                Task ta = current().get(inte.getName());
                //  Window.alert("task, min max dur"+ta.getName()+ " "+inte.getMin()+ " "+inte.getMax()+" "+ + ta.getDuration());
                for (int j = inte.getMin(); j <= inte.getMax(); j++) { // MINORE O MINORE UGUALE?????
                    for (int i = 0; i < ta.getDuration(); i++) {
                        // colora come possibili tutte le caselle dove piazzare task scelto
                        if (ret[j + i].equals("")) {
                            ret[j + i] = "***";
                        }
                    }
                }
            }
        }
        return ret;
    }

    public static boolean exists(String name) {
        for (Task t : current().tasks) {
            if (t.getName().equals(name)) {
                return true;
            }
        }
        return false;
    }

    public Task getI(String name) {

        for (Task t : tasks) {
             System.out.println("task="+name);
            if (t.getName().equals(name)) {
                return t;
            }
        }
        return null;
    }

    public static Task get(String name) {
        for (Task t : current().tasks) {
            if (t.getName().equals(name)) {
                return t;
            }
        }
        return null;
        //    System.out.println("get, task not found: " + name);
        //    return new Task();
    }

    public static void remove(String name) {
        for (Task t : current().tasks) {
            if (t.getName().equals(name)) {
                current().tasks.remove(t);
            }
        }
        current().removeSchedule(name);
    }

    public static void change(Task t) {
        for (Task c : current().tasks) {
            if (t.getName().equals(c.getName())) {
                current().tasks.remove(c);
                current().tasks.add(t);
                current().setSchedule(t);
            }
        }
    }

    public static void esempioLili() {

        TaskGroup tg = current();
        Task task = new Task("WTec1", 1, 3, 2);
        tg.addScheduledTask(task, 1, 3);
        task = new Task("Prog1", 3, 5, 2);
        tg.addScheduledTask(task, 3, 5);
        task = new Task("Meeting WOO", 6, 10, 4);
        tg.addScheduledTask(task, 6, 10);
        task = new Task("Stud visit hours", 13, 15, 2);
        tg.addScheduledTask(task, 13, 15);
        task = new Task("WTec2", 15, 17, 2);
        tg.addScheduledTask(task, 15, 17);
        task = new Task("Dentist app", 20, 21, 1);
        tg.addScheduledTask(task, 20, 21);
        task = new Task("Library meet", 25, 27, 2);
        tg.addScheduledTask(task, 25, 27);
        task = new Task("Prog2", 27, 29, 2);
        tg.addScheduledTask(task, 27, 29);
        task = new Task("Phd meet", 31, 33, 2);
        tg.addScheduledTask(task, 31, 33);
        task = new Task("Ph Call Smith", 33, 34, 1);
        tg.addScheduledTask(task, 33, 34);
        task = new Task("write paper", 37, 39, 2);
        tg.addScheduledTask(task, 37, 39);
        task = new Task("WTec3", 39, 41, 2);
        tg.addScheduledTask(task, 39, 41);
        task = new Task("Faculty meet", 44, 46, 2);
        tg.addScheduledTask(task, 44, 46);
        task = new Task("WTec4", 49, 51, 2);
        tg.addScheduledTask(task, 49, 51);
        task = new Task("Prog3", 51, 53, 2);
        tg.addScheduledTask(task, 51, 53);

        //   Window.alert("corrente in esempio"+ current().tasks);
    }

    public static void esempio() {

        TaskGroup tg = current();
        Task task = new Task("T1", 0, 10, 4);
        tg.addScheduledTask(task, 6, 10);
        task = new Task("T2", 24, 40, 2);
        tg.addScheduledTask(task, 24, 26);
        task = new Task("T3", 20, 50, 4);
        tg.addScheduledTask(task, 36, 40);
        //   Window.alert("corrente in esempio"+ current().tasks);
    }

    public static void lunch() {
        TaskGroup tg = current();
        Task task = new Task("Lunch1", 4, 5, 1);
        tg.addScheduledTask(task, 4, 5);
        task = new Task("Lunch2", 16, 17, 1);
        tg.addScheduledTask(task, 16, 17);
        task = new Task("Lunch3", 28, 29, 1);
        tg.addScheduledTask(task, 28, 29);
        task = new Task("Lunch4", 40, 41, 1);
        tg.addScheduledTask(task, 40, 41);
        task = new Task("Lunch5", 52, 53, 1);
        tg.addScheduledTask(task, 52, 53);
        //   Window.alert("corrente in esempio"+ current().tasks);
    }

    /**
     * @return the taskSchedule
     */
    public ArrayList<Interval> getTaskSchedule() {
        return taskSchedule;
    }

    /**
     * @param taskSchedule the taskSchedule to set
     */
    public void setTaskSchedule(ArrayList<Interval> taskSchedule) {
        this.taskSchedule = taskSchedule;
    }

    public void addTaskSchedule(Interval inte) {
        taskSchedule.add(inte);
    }

    /**
     * @return the choice
     */
    public StartInterval getChoice() {
        return choice;
    }

    /**
     * @param choice the choice to set
     */
    public void setChoice(StartInterval choice) {
        this.choice = choice;
    }
}
