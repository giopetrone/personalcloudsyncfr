/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package org.yournamehere.server;

import com.google.gwt.user.server.rpc.RemoteServiceServlet;
import java.util.ArrayList;

import org.yournamehere.client.GWTService;

/**
 *
 * @author marino
 */
public class GWTServiceImpl extends RemoteServiceServlet implements GWTService {
    public String myMethod(String s) {
        // Do something interesting with 's' here on the server.
        return "Server says: " + s;
    }

      public ArrayList<String> myMethod1(ArrayList<String> ss) {
        // Do something interesting with 's' here on the server.
          ArrayList<String> ret = new ArrayList();
         for (String aa:ss){
             ret.add(aa.toLowerCase());
         }
        return ret;
    }
   /* @Override
	protected void checkPermutationStrongName() throws SecurityException {
		return;
	}
    protected void onBeforeRequestDeserialized(java.lang.String serializedRequest){

    }*/
}
