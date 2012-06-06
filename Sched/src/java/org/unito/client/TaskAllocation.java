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

    public String stringaData(String s);

    public ViaVai schedule(ViaVai daFare, String mode, String old, String modalita);

    public ViaVai scheduleRequest(ViaVai daFare, String taskName, String taskNet, String mu, String mode, String user, String modalita);

    public String removeTask(String s);
}
