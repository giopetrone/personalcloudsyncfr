/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package org.yournamehere.client;

import com.google.gwt.user.client.rpc.RemoteService;
import com.google.gwt.user.client.rpc.RemoteServiceRelativePath;
import java.util.ArrayList;

/**
 *
 * @author marino
 */
@RemoteServiceRelativePath("gwtservice")
public interface GWTService extends RemoteService {
    public String myMethod(String s);
    public ArrayList<String> myMethod1(ArrayList<String> s);
}
