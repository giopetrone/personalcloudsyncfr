/*
 * GWTServiceImpl.java
 *
 * Created on April 9, 2009, 2:08 PM
 *
 * To change this template, choose Tools | Template Manager
 * and open the template in the editor.
 */

package org.yournamehere.server;
import com.google.gwt.user.server.rpc.RemoteServiceServlet;
import javax.servlet.http.HttpSession;
import org.yournamehere.client.GWTService;

/**
 *
 * @author giovanna
 */
public class GWTServiceImpl extends RemoteServiceServlet implements
        GWTService {
    
    public String myMethod(String s) {
        // Do something interesting with 's' here on the server.
               HttpSession sess = this.getThreadLocalRequest().getSession();
                System.out.println("SESSIONE      HelloGadget id = : " + sess.getId());
                try {
                 System.out.println("SESSIONE  HelloGadget readContent  = : " + this.readContent(this.getThreadLocalRequest()));
                }
                catch (Exception e )  {System.out.println("eccez"); }
                    System.out.println("SESSIONE  HelloGadget query string  = : " +  this.getThreadLocalRequest().getQueryString());
                 System.out.println("SESSIONE  HelloGadget names  = : " +  this.getThreadLocalRequest().getParameterNames());
        String val = this.getThreadLocalRequest().getParameter("par");

        return "Server says: " + s + " par = " + val;
    }
}
