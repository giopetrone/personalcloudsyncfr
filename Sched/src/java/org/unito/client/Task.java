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
public class Task implements IsSerializable {
    private String name;
    private int firstStartHour = 0;
    private int lastEndHour = 1;
    private int duration = 1;
     private int schedule = -1; // used to set a specific starting hour;
     // the class Taskgroup contains a list of schedules for all the tasks
     // and also a list of possible alternative intervals for a specific task

    private boolean overlap = false;
    private ArrayList<String> before = new ArrayList();
    private ArrayList<String> after = new ArrayList();
    private ArrayList<Interval> intervals = new ArrayList();

    public String toRequest(boolean tasco) {
        String ret = "";
        if (tasco) {
            ret += "<task name=\"";
            ret += getName();
            ret += "\" dur=\"";
            ret += getDuration();
            ret += "\" end=\"";
            ret += new Interval(getName(), getFirstStartHour(), getLastEndHour()).toRequest();
            ret += "\" start=\"";
            ret += new Interval(getName(), getFirstStartHour(), getLastEndHour()).toRequest();
            ret += "\"/>";
        } else {
            if (!before.isEmpty()) {
                ret += "<prec name=\"";
                ret += getName();
                ret += "\" >\n";
                for (String pr : getBefore()) {
                    ret += " <succ name=\"";
                    ret += pr;
                    ret += "\" />\n";
                }
                ret += "</prec>\n";
            }
        }
        return ret;
    }

    public ArrayList<String> getBefore() {
        return before;
    }

    public ArrayList<String> getAfter() {
        return after;
    }

    public int getOfficialSchedule(){
        return TaskGroup.current().getOfficialSchedule(getName());
    }
    
     public int getSchedule(){
        return schedule;
    }

    public Task() {
     //   this.name = "NULL";
      //  this.duration = 0;
    }

    public Task(String name, int firstStartHour, int lastEndHour, int duration) {
        this.name = name;
        this.duration = duration;
        if (firstStartHour >= 0) {
            this.firstStartHour = firstStartHour;
        }
        if (lastEndHour >= 0) {
            this.lastEndHour = lastEndHour;
        }
    }

    public String toString() {
        return "name: " + getName() + " start: " + getFirstStartHour() + " end: " + getLastEndHour() + "duration: " + getDuration() + "schedule: " + getSchedule();
    }

    public Task(String name, String firstStartHour, String lastEndHour, String duration, String before, String after, String schedule, boolean overlap) {
        this.name = name;
        this.overlap = overlap;
        this.duration = Integer.parseInt(duration);
        if (Integer.parseInt(firstStartHour) >= 0) {
            this.firstStartHour = Integer.parseInt(firstStartHour);
        }
        if (Integer.parseInt(lastEndHour) >= 0) {
            this.lastEndHour = Integer.parseInt(lastEndHour);
        }
        int sche = schedule.equals("")? -1: Integer.parseInt(schedule);
        this.schedule = sche;     //occorre capire che fare !!!!!!!!
     //   this.startHour = this.firstStartHour;
     //   this.endHour = this.firstStartHour + this.duration;
        String[] tmp = before.split(" ", -1);
        for (int i = 0; i < tmp.length; i++) {
            if (TaskGroup.exists(tmp[i])) {
                //  Window.alert("aggiungo before a:" + name + " "+ tmp[i] );
                this.before.add(tmp[i]);
            }
        }
        tmp = after.split(" ", -1);
        for (int i = 0; i < tmp.length; i++) {
            if (TaskGroup.exists(tmp[i])) {
                //   Window.alert("aggiungo after a:" + name + " "+ tmp[i] );
                this.after.add(tmp[i]);
            }
        }
     //   Window.alert(this.toString());
    }
/*
    public int getStartHour() {
        return startHour;
    }

    public int getEndHour() {
        return startHour + duration;
    }
*/
    public int getDuration() {
        return duration;
    }

    public int getMinStartHour() {
        return getFirstStartHour();
    }

    public int getMaxEndHour() {
        return getLastEndHour();
    }

 /*   public void setStartHour(int startHour) {
        this.startHour = startHour;
    }

    public int getstartHourDay() {
        return startHour % giornata;
    }

    public int getEndHourDay() {
        return (startHour + duration) % giornata;
    }

    public int getStartDay() {
        return startHour / giornata;
    }

    public int getEndDay() {
        return (startHour + duration) / giornata;
    }
*/
    public String getName() {
        return name;
    }


    public void setDuration(int duration) {
        this.duration = duration;
    }

    public static void alloca() {
    }

    public String beforeString() {
        if (getBefore().isEmpty()) {
            return "          ";
        }
        String ret = "";
        for (String s : getBefore()) {
            ret += s;
        }
        return ret;
    }

    public String afterString() {
        if (getAfter().isEmpty()) {
            return "          ";
        }
        String ret = "";
        for (String s : getAfter()) {
            ret += s;
        }
        return ret;
    }

    public boolean getOverlap() {
        return isOverlap();
    }

    /**
     * @param schedule the schedule to set
     */
    public void setSchedule(int schedule) {
        this.schedule = schedule;
    }

    /**
     * @param name the name to set
     */
    public void setName(String name) {
        this.name = name;
    }

    /**
     * @return the firstStartHour
     */
    public int getFirstStartHour() {
        return firstStartHour;
    }

    /**
     * @param firstStartHour the firstStartHour to set
     */
    public void setFirstStartHour(int firstStartHour) {
        this.firstStartHour = firstStartHour;
    }

    /**
     * @return the lastEndHour
     */
    public int getLastEndHour() {
        return lastEndHour;
    }

    /**
     * @param lastEndHour the lastEndHour to set
     */
    public void setLastEndHour(int lastEndHour) {
        this.lastEndHour = lastEndHour;
    }

    /**
     * @return the overlap
     */
    public boolean isOverlap() {
        return overlap;
    }

    /**
     * @param overlap the overlap to set
     */
    public void setOverlap(boolean overlap) {
        this.overlap = overlap;
    }

    /**
     * @param before the before to set
     */
    public void setBefore(ArrayList<String> before) {
        this.before = before;
    }

    /**
     * @param after the after to set
     */
    public void setAfter(ArrayList<String> after) {
        this.after = after;
    }

    /**
     * @return the intervals
     */
    public ArrayList<Interval> getIntervals() {
        return intervals;
    }

    /**
     * @param intervals the intervals to set
     */
    public void setIntervals(ArrayList<Interval> intervals) {
        this.intervals = intervals;
    }
}
