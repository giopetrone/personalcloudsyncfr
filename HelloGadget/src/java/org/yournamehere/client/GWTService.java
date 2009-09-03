/*
 * GWTService.java
 *
 * Created on April 9, 2009, 2:08 PM
 *
 * To change this template, choose Tools | Template Manager
 * and open the template in the editor.
 */

package org.yournamehere.client;
import com.google.gwt.user.client.rpc.RemoteService;

/**
 *
 * @author giovanna
 */
public interface GWTService extends RemoteService{
    public String myMethod(String s);
}
