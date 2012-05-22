/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package org.unito.client;

import com.google.gwt.user.client.Window;
import com.google.gwt.user.client.rpc.IsSerializable;
import java.util.ArrayList;

/**
 *
 * @author marino
 */
public class TaskGroup implements IsSerializable {

    static int giornata = 12;
    static int oreLavoro = 60;
    private static ArrayList<TaskGroup> history = new ArrayList(); // lista di allocazioni
    private ArrayList<Interval> globalSchedule = new ArrayList();  // list of schedule for all tasks
    private ArrayList<Interval> taskSchedule = new ArrayList();  // possible starting intervals for a task
    private ArrayList<Task> tasks = new ArrayList();
    private Interval choice = null;
    private String selectedTask = null;
    static boolean lunch = false;

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
        choice = new Interval(tName, t.getOfficialSchedule(), t.getOfficialSchedule() + 1);
        //   Window.alert("in taskgroup.setChoiceForTas task: " + tName + " " + choice.getMin() + ", " + choice.getMax());
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
        selectedTask = via.getSelectedTask();
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
        globalSchedule.add(new Interval(t.getName(), start, end));
    }

    public int setSchedule(Task t) {
        // find time to schedule as early as possible the task
        // set to -1 if no time has been found;
        // first try user schedulke (if any), then try any poossible iunterval
        int sta = possibileInizio(t.getName(), t.getSchedule());
        if (sta == -1) {
            // deschedula
            removeSchedule(t.getName());
            return -1;
        }
        //   Window.alert("setschedule task= "+t.getName() +" sta="+sta);
        Interval inte = findIntervalForTask(t.getName());
        if (inte == null) {
            globalSchedule.add(new Interval(t.getName(), sta, sta));
        } else {
            inte.setMin(sta);
            inte.setMax(sta);
            //   Window.alert("cambio schedule:"+t.getDefaultSchedule());
            // inte.setMin(t.getDefaultSchedule());
            //  inte.setMax(t.getDefaultSchedule());
        }
        return sta;
    }

    public static int addScheduleTask(String name, String firstStartHour, String lastEndHour, String duration, String before, String after, String schedule, String users, boolean overlap) {
        Task tat = new Task(name, firstStartHour, lastEndHour, duration, before, after, schedule, users, overlap);
        TaskGroup.add(tat);
        //  Window.alert("lastendhour="+lastEndHour);
        int sched = TaskGroup.current().setSchedule(tat);
        return sched;
    }

    public static String checkTask(String name, String firstStartHour, String lastEndHour, String duration, String before, String after, String schedule, String users, boolean overlap) {
        int fis = Integer.parseInt(firstStartHour);
        int las = Integer.parseInt(lastEndHour);
        String msg = "";
        if (fis < 0 || las < 0 || fis >= oreLavoro || las > oreLavoro) {
            msg += "invalid start or end hour: must be 0 <= VALUE <= " + oreLavoro + "\n";
        }
        if (duration.equals("")) {
            duration = "0";
        }
        int dur = -1;
        try {
            dur = Integer.parseInt(duration);
        } catch (NumberFormatException ex) {
            msg += "invalid duration: " + duration + "\n";
        }
        if (dur < 1 || dur > oreLavoro) {
            msg += "invalid duration: must be 1 <= " + dur + "  <= " + oreLavoro + "\n";
        }

        if (!schedule.equals("")) {
            int sche = Integer.parseInt(schedule);
            if (sche < fis || sche + dur > las) {
                msg += "invalid schedule. must be " + fis + "<= " + sche + "<= " + (las - dur) + "\n";
            }
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
        tmp = users.split(" ", -1);
        for (int i = 0; i < tmp.length; i++) {
            if (!tmp[i].equals("") && !tmp[i].equals(" ")) {
                if (UiUser.find(tmp[i]) == null) {
                    msg += "User not found: " + tmp[i] + "\n";
                }
            }
        }
        return msg;
    }

    public static void add(Task t) {
        current().tasks.add(t);
    }

    public void addI(Task t) {
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

    public ArrayList<String> usersForInterval(int time) {
        // retrun list of users conflicting with schedule at certain time
        for (Interval inte : taskSchedule) {
            if (inte.getMin() == time) {
                return inte.getUsers();
            }
        }
        return null;
    }

    public void updateWith(ViaVai result) {
        selectedTask = result.getSelectedTask();
        updateTaskSlots(new TaskGroup(result));
        /* non funziona e non so perche'!!!!  */  //taskSchedule = Interval.sort(result.getTaskSchedule());
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

    public ArrayList<Task> getUnscheduledTasks() {
        ArrayList<Task> ret = new ArrayList();
        for (Task t : tasks) {
            if (getOfficialSchedule(t.getName()) == -1) {
                ret.add(t);
            }
        }
        return ret;
    }

    public int getOfficialSchedule(String name) {
        Interval inte = findIntervalForTask(name);
        return inte == null ? -1 : inte.getMin();
    }

    private int possibileInizio(String name, int wantedStart) {
        boolean[] libero = new boolean[oreLavoro];
        for (int i = 0; i < oreLavoro; i++) {
            libero[i] = true;
        }
        Task questo = get(name);
        for (int i = 0; i < questo.getFirstStartHour(); i++) {
            libero[i] = false;
        }
        for (int i = questo.getLastEndHour(); i < oreLavoro; i++) {
            libero[i] = false;
        }
        for (Task t : current().tasks) {
            if (t != questo && !t.concurrent(questo)) {
                // tasks cannot run in parallel
                //  Window.alert("blocco "+t.getName());
                int sta = current().getOfficialSchedule(t.getName());
                if (sta >= 0) {
                    for (int i = 0; i < t.getDuration(); i++) {
                        libero[sta + i] = false;
                    }
                }
            }
        }
        if (wantedStart >= 0) {
            int fai = canStart(libero, wantedStart, wantedStart + questo.getDuration(), questo.getDuration());
            if (fai == wantedStart) {
                return fai;
            }
        }
        // return any good time
        return canStart(libero, questo.getFirstStartHour(), questo.getLastEndHour(), questo.getDuration());
    }

    private int canStart(boolean[] libero, int inizio, int fine, int dura) {
        //  Window.alert("canstart"+ inizio+" "+fine + " "+dura + " "+ libero[6]);
        for (int k = inizio; k < fine - dura + 1; k++) {
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

    public static ArrayList<String>[] nomiCaselle(/*boolean showAlt,*/TaskGroup fonte) {
        ArrayList<String>[] ret = new ArrayList[oreLavoro];
        for (int i = 0; i < oreLavoro; i++) {
            ret[i] = new ArrayList();
        }
        // Window.alert("in nomiCaselle task sz =" + current().tasks.size());
        for (Task t : fonte.tasks) {
            int sta = fonte.getOfficialSchedule(t.getName());
            //   Window.alert("in nomiCaselle task=" + t.getName() + " " +sta);
            if (sta >= 0) {
                for (int i = 0; i < t.getDuration(); i++) {
                    if (sta + i >= oreLavoro) {
                        break;
                    }
                    ret[sta + i].add(t.getName());
                }
            }
        }
        return ret;
    }

    public static ArrayList<String>[] stiliCaselle(boolean showAlt, TaskGroup fonte) {
        ArrayList<String>[] ret = new ArrayList[oreLavoro];
        for (int i = 0; i < oreLavoro; i++) {
            ret[i] = new ArrayList();
        }
        //   String stam = "";
        if (showAlt) {
            if (!fonte.taskSchedule.isEmpty()) {
                for (Interval inte : fonte.taskSchedule) {
                    Task ta = fonte.get(inte.getName());
                    if (inte.getMin() == inte.getMax()) {
                        Window.alert("stilicaselle: limiti == !!! " + ta.getName() + " " + inte.getMin());
                    }
                    for (int j = inte.getMin(); j < inte.getMax(); j++) { // MINORE O MINORE UGUALE?????
                        for (int i = 0; i < ta.getDuration(); i++) {
                            if (j + i >= oreLavoro) {
                                break;
                            }
                            // colora come possibili tutte le caselle dove piazzare task scelto
                            if (ret[j + i].isEmpty()) {

                                if (inte.getUsers().isEmpty()) {
                                    //    Window.alert("empty users, good"+ (j+i));
                                    ret[j + i].add("styleAvailable");
                                    //    stam += (j + i) + "L";
                                } else {
                                    ret[j + i].add("styleConflict");
                                    //   stam += (j + i) + "C";
                                }
                            }
                        }
                    }
                }
                //   Window.alert("stam" + stam);
                // fill also current task positions , to be discussed.
                Task tat = fonte.getI(fonte.getSelectedTask());
                //     Window.alert("taskfonte=" + tat);
                int ende = tat.getOfficialSchedule() + tat.getDuration();
                for (int k = tat.getOfficialSchedule(); k < ende; k++) {
                    if (ret[k].isEmpty()) {
                        ret[k].add("styleAvailable");
                    }
                }

            } else {
                Task lui = fonte.getI(fonte.getSelectedTask());
                for (int i = lui.getFirstStartHour(); i < lui.getLastEndHour(); i++) {
                    ret[i].add("styleConflict");
                }
            }
        }
        for (int i = 0;
                i < oreLavoro;
                i++) {
            if (ret[i].isEmpty()) {
                ret[i].add("styleUnused");
            }
        }
        return ret;
    }

    public static ArrayList<String>[] nomiCaselleOLD(boolean showAlt, TaskGroup fonte) {
        ArrayList<String>[] ret = new ArrayList[oreLavoro];


        for (int i = 0; i
                < oreLavoro; i++) {
            ret[i] = new ArrayList();


        } // Window.alert("in nomiCaselle task sz =" + current().tasks.size());
        for (Task t : fonte.tasks) {
            int sta = fonte.getOfficialSchedule(t.getName());
            //   Window.alert("in nomiCaselle task=" + t.getName() + " " +sta);
            if (sta >= 0) {
                for (int i = 0; i < t.getDuration(); i++) {
                    ret[sta + i].add(t.getName());
                }
            }
        }
        if (showAlt) {
            for (Interval inte : fonte.taskSchedule) {
                Task ta = fonte.get(inte.getName());
                //  Window.alert("task, min max dur"+ta.getName()+ " "+inte.getMin()+ " "+inte.getMax()+" "+ + ta.getDuration());
                for (int j = inte.getMin(); j
                        <= inte.getMax(); j++) { // MINORE O MINORE UGUALE?????
                    for (int i = 0; i
                            < ta.getDuration(); i++) {
                        // colora come possibili tutte le caselle dove piazzare task scelto
                        if (ret[j + i].isEmpty()) {
                            ret[j + i].add("***");
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
            // System.out.println("task=" + name);
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

    public static String change(Task t) {
        String ret = "";
        //  Window.alert("change,task new duration=" + t.getDuration());
        for (Task c : current().tasks) {
            if (t.getName().equals(c.getName())) {
                current().tasks.remove(c);
                current().tasks.add(t);
                int start = current().setSchedule(t);
                if (start == -1) {
                    ret = "task has not been scheduled";
                }
            }
        }
        return ret;
    }

    public static void esempioLili() {

        UiUser.createUsers();
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
        UiUser.createUsers();
        TaskGroup tg = current();
        Task task = new Task("T1", 0, 10, 4);
        task.addUser(UiUser.find("liliana"));
        task.addUser(UiUser.find("marino"));
        tg.addScheduledTask(task, 6, 10);

        task = new Task("T2", 24, 40, 2);
        task.addUser(UiUser.find("balbo"));
        tg.addScheduledTask(task, 24, 26);

        task = new Task("T3", 20, 50, 4);
        task.addUser(UiUser.find("gianluca"));
        tg.addScheduledTask(task, 36, 40);
        //   Window.alert("corrente in esempio"+ current().tasks);
    }

    public static ArrayList<UiUser> ContainsUser(ArrayList<String> users, String task) {
        ArrayList<UiUser> ret = new ArrayList();
        Task t = current().get(task);
        if (t != null) {
            for (String us : users) {
                if (t.containsUser(us)) {
                    ret.add(UiUser.find(us));
                }
            }
        }
        return ret;
    }

    public static void lunch() {
        lunch = !lunch;
        TaskGroup tg = current();
        if (lunch) {
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
        } else {
            tg.remove("Lunch1");
            tg.remove("Lunch2");
            tg.remove("Lunch3");
            tg.remove("Lunch4");
            tg.remove("Lunch5");
        }
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
