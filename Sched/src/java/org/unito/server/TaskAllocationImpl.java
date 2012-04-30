/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package org.unito.server;

import com.google.gwt.user.server.rpc.RemoteServiceServlet;

import org.unito.client.TaskAllocation;
import org.unito.client.TaskGroup;
import org.unito.client.ViaVai;

/**
 *     
 * @author marino
 */
public class TaskAllocationImpl extends RemoteServiceServlet implements TaskAllocation {

    public String myMethod(String s) {
        // Do something interesting with 's' here on the server.
        System.err.println("mymethod");
        return "Server says: " + s;
    }

    public String removeTask(String s) {
        // Do something interesting with 's' here on the server.
        return new TaskCall().removeTask(s);
    }

    public ViaVai schedule(ViaVai daFare, String mode, String old) {
        System.err.println("in taskimpl.schedule");
        TaskGroup ret = new TaskCall().doIt(new TaskGroup(daFare), mode, old);
        return ret != null? new ViaVai(ret): null;
    }

    public ViaVai scheduleRequest(ViaVai daFare, String taskName, String taskNet, String mu, String mode, String user) {
        System.err.println("in taskimpl.scheduleRequest");
        TaskGroup ret = new TaskCall().doRequest(new TaskGroup(daFare), taskName, taskNet, mu, mode, user);
        return ret != null? new ViaVai(ret): null;
    }
}
