/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package jav;

/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */


import java.io.IOException;
import java.io.PrintWriter;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import pubsublib.pubsubhubbub.Discovery;
import pubsublib.test.TestSub;

/**
 *
 * @author marino
 */
public class SubscribeServlet extends HttpServlet {

    

    /**
     * Processes requests for both HTTP <code>GET</code> and <code>POST</code> methods.
     * @param request servlet request
     * @param response servlet response
     * @throws ServletException if a servlet-specific error occurs
     * @throws IOException if an I/O error occurs
     */
    protected void processRequest(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {

        // ricordare che deplkoy di callabck e' stooo ...../subscribe/pippo
        response.setContentType("text/html;charset=UTF-8");
         System.err.println("subscribe di Notift");
        PrintWriter out = response.getWriter();

        String casoSubscribe = null;
        try {
            response.setContentType("text/html");
            casoSubscribe = request.getHeader("notifica");
            String nomeFile = request.getHeader("filenamemio");
            String user = request.getHeader("email");
            if(nomeFile == null) nomeFile ="";
            String[] feeds = nomeFile.split(" ");
            System.err.println("subscribe di Notift  1");
            String url = "http://taskmanagerunito.xoom.it/Flow/";
            String feederrati = "";

            if (casoSubscribe != null) {
                   System.err.println("caso =" + casoSubscribe);

                if (casoSubscribe.equals("start") && !nomeFile.equals(""))
                {
                    int count = 0;
                    for(int i=0;i<feeds.length;i++)
                    {
                        int find = feeds[i].indexOf("/");
                        String nomeDoc = feeds[i].substring(0, find);
                        System.out.println("Doc: "+nomeDoc);
                        String notiftype = feeds[i].substring(find+1, feeds[i].length());
                        System.out.println("Notification: "+notiftype);
                        String feed = url+nomeDoc+".xml";
                        String risp ="";
                        int index = user.indexOf("@");
                        user = user.substring(0, index);
                        if(user.contains(".")) user = user.replace(".","_");
                        user +=".txt";
                        String permissionName = "";
                        Discovery discovery = new Discovery();
                        String hub = discovery.getHub(feed);
                        if(hub.equals("http://pubsubhubbub.appspot.com"))
                        {
                            risp = new TestSub().testSubscriber(feed, "http://taskmgrunito.appspot.com/NotifCallbackServlet","");
                        }
                        else risp =   new TestSub().testSubscriber(feed, "http://localhost:8081/NotifMgrG/NotifCallbackServlet", "");
                        GoDoc.savePermissions(user, nomeDoc, notiftype);
                        System.err.println("subscribe al feed= " + feed+" "+risp);
                        if(risp.equalsIgnoreCase("done")) count++;
                        else feederrati += " "+feeds[i];

                     }
                     if(count == feeds.length) out.println("Fatta subscribe");
                     else out.println("Errore nei feeds:"+feederrati);

                      
                     

                } else
                {
                       out.println("Nome Feed Vuoto");
                }
            }
        } catch (Exception ex)
        {
           
            ex.printStackTrace();
        } finally {
            
            out.close();
        }
    }

    // <editor-fold defaultstate="collapsed" desc="HttpServlet methods. Click on the + sign on the left to edit the code.">

    /**
     * Handles the HTTP <code>GET</code> method.
     * @param request servlet request
     * @param response servlet response
     * @throws ServletException if a servlet-specific error occurs
     * @throws IOException if an I/O error occurs
     */
    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        processRequest(request, response);
    }

    /**
     * Handles the HTTP <code>POST</code> method.
     * @param request servlet request
     * @param response servlet response
     * @throws ServletException if a servlet-specific error occurs
     * @throws IOException if an I/O error occurs
     */
    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        processRequest(request, response);
    }

    /**
     * Returns a short description of the servlet.
     * @return a String containing servlet description
     */
    @Override
    public String getServletInfo() {
        return "Short description";
    }// </editor-fold>
}
