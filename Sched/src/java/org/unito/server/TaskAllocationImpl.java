/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package org.unito.server;

import com.google.gwt.user.server.rpc.RemoteServiceServlet;
import java.text.SimpleDateFormat;

import java.util.Calendar;

import org.unito.client.TaskAllocation;
import org.unito.client.TaskGroup;
import org.unito.client.ViaVai;

/**
 *     
 * @author marino
 */
public class TaskAllocationImpl extends RemoteServiceServlet implements TaskAllocation {

    public String stringaData(String s) {
        // Do something interesting with 's' here on the server.
        // System.err.println("mymethod");
        String ret = "Week from: ";
        Calendar cal = Calendar.getInstance();
        SimpleDateFormat df = new SimpleDateFormat("EEE, d MMM yyyy");
        int day = cal.get(Calendar.DAY_OF_WEEK) - 2;
        cal.add(Calendar.DAY_OF_WEEK, -day);
        //  String from = "Week from: "+ cal.roll(Calendar.DAY_OF_WEEK, -day);
        ret += df.format(cal.getTime());
        cal.add(Calendar.DAY_OF_WEEK, 7 - day);
        ret += " To: " + df.format(cal.getTime());
        return ret;
    }

    public String removeTask(String s) {
        // Do something interesting with 's' here on the server.
        return new TaskCall().removeTask(s);
    }

    public ViaVai schedule(ViaVai daFare, String mode, String old, String modalita) {
        System.err.println("in taskimpl.schedule");
        TaskGroup ret = new TaskCall().doConstraints(new TaskGroup(daFare), mode, old, modalita);
        return ret != null ? new ViaVai(ret) : null;
    }

    public ViaVai scheduleRequest(ViaVai daFare, String taskName, String taskNet, String mu, String mode, String user, String modalita) {
        // "startintervals", "", "move", "pippo"
        //  "startintervals", "", "insert", "pippo",
        //  "tasknet", "", "move", "pippo",
        // "tasknet", "", "insert", "pippo",
        System.err.println("in taskimpl.scheduleRequest");
        TaskGroup ret = new TaskCall().doRequest(new TaskGroup(daFare), taskName, taskNet, mu, mode, user, modalita);
        return ret != null ? new ViaVai(ret) : null;
    }

    public static void main(String[] args) {

        Calendar cal = Calendar.getInstance();
        SimpleDateFormat df = new SimpleDateFormat("EEE, d MMM yyyy");
        int day = cal.get(Calendar.DAY_OF_WEEK) - 2;
        cal.add(Calendar.DAY_OF_WEEK, -day);
        //  String from = "Week from: "+ cal.roll(Calendar.DAY_OF_WEEK, -day);
        System.err.println("data:" + df.format(cal.getTime()));
        cal.add(Calendar.DAY_OF_WEEK, 7 - day);
        //  String from = "Week from: "+ cal.roll(Calendar.DAY_OF_WEEK, -day);
        System.err.println("data:" + df.format(cal.getTime()));
    }
}
