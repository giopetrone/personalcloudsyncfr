/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package org.unito.client;

import com.google.gwt.user.client.rpc.RemoteService;
import com.google.gwt.user.client.rpc.RemoteServiceRelativePath;

/**
 *
 * @author marino
 */
@RemoteServiceRelativePath("taskallocation")
public interface TaskAllocation extends RemoteService {

    public String myMethod(String s);

    public ViaVai schedule(ViaVai daFare, String mode, String old);

    public ViaVai scheduleRequest(ViaVai daFare, String taskName, String taskNet, String mu, String mode, String user);

    public String removeTask(String s);
}
