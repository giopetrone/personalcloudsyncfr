/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package org.unito.client;

import com.google.gwt.user.client.rpc.IsSerializable;

/**
 *
 * @author marino
 */
public class StartInterval extends Interval implements IsSerializable{

    public StartInterval(){}

     public StartInterval(String taskName, int min, int max) {
       super(taskName, min, max);
    }

}
