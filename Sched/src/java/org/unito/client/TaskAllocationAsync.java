/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package org.unito.client;

import com.google.gwt.user.client.rpc.AsyncCallback;
import java.util.ArrayList;

/**
 *
 * @author marino
 */
public interface TaskAllocationAsync { 
    public void stringaData(String s, AsyncCallback<String> callback);
    public void removeTask(String s, AsyncCallback<String> callback);
    public void  schedule(ViaVai daFare, String mode, String old, String modalita,  AsyncCallback<ViaVai> callback);
    public void  scheduleRequest(ViaVai daFare, String taskName, String taskNet, String mu, String mode, String user, String modalita,  AsyncCallback<ViaVai> callback);
}
