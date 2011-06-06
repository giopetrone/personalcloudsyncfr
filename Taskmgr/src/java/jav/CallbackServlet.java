/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package jav;

import pubsub.FeedUtil;
import java.io.IOException;

import java.io.PrintWriter;
import java.util.List;
import javax.servlet.ServletException;
import javax.servlet.ServletInputStream;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import event.AtomEvent;

/**
 *
 * @author marino
 */
public class CallbackServlet extends HttpServlet {

    /** 
     * Processes requests for both HTTP <code>GET</code> and <code>POST</code> methods.
     * @param request servlet request
     * @param response servlet response
     * @throws ServletException if a servlet-specific error occurs
     * @throws IOException if an I/O error occurs
     */
    protected void processRequest(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {

        // ricordare che deploy di callabck e' sotto  <CallbackServlett>/subscribe/pippo
         System.err.println("$$$$$$$$$$$$$$$$Dentro callbackServlet$$$$$$$$$$$$$$$$$");
        /*
        System.err.println("headers richiesta:");
        Enumeration e = request.getHeaderNames();
        while (e.hasMoreElements()) {
        String headers = (String) e.nextElement();
        if (headers != null) {
        System.err.println(headers + " " + request.getHeader(headers));
        }
        }
        System.err.println("\n");
         * */

        response.setContentType("text/html;charset=UTF-8");
        PrintWriter out = response.getWriter();
        try {
            String refresh = request.getHeader("notifica");
           
            if (refresh != null) {
                //ciclic ajax call from UI
                String notif = (String) getServletContext().getAttribute("atomo");
                out.print("Notif: "+notif);
                if (notif != null) {  // we have received new feed content
                    out.println(notif); // send it to client
                    // set new feed content to nul to avoid duplicate notifications
                    getServletContext().setAttribute("atomo", null);
                } else {
                    System.err.println("session, get=" + getServletContext());
                    out.println("nulla");
                }
                out.close();
            } else {
                // request from HUB: challenge or callback
                String hubmode = "";
                if (request.getParameter("hub.mode") != null) {
                    hubmode = request.getParameter("hub.mode");
                }
                if (((hubmode.equals("subscribe")) || (hubmode.equals("unsubscribe")))
                        && (hubmode.length() > 0)) {
                    String hubchallenge = "";
                    if (request.getParameter("hub.challenge") != null) {
                        hubchallenge = request.getParameter("hub.challenge");
                    }
                    response.setStatus(HttpServletResponse.SC_OK);
                    out.print(hubchallenge);
                    /*
                    String hubtopic = "";
                    String hublease = "";
                    String hubverify = "";
                    if (request.getParameter("hub.topic") != null) {
                    hubtopic = request.getParameter("hub.topic");
                    }
                    if (request.getParameter("hub.lease") != null) {
                    hublease = request.getParameter("hub.lease");
                    }
                    if (request.getParameter("hub.verify") != null) {
                    hubverify = request.getParameter("hub.verify");
                    }

                    System.err.println("\nin callback for hub request mode: "
                    + hubmode + " topic: "
                    + hubtopic + "challenge: "
                    + hubchallenge + " verify: "
                    + hubverify + " lease: "
                    + hublease + "\n");
                     */
                } else {
                    ServletInputStream inStream = request.getInputStream();
                    String s = "";
                    byte[] b = new byte[1024];
                    int c;
                    while ((c = inStream.read(b)) != -1) {
                        s += new String(b, 0, c);
                    }
                    //   System.err.println("session put =" + getServletContext());
                    // save new feed content; at next refresh call it will be given to client
                    getServletContext().setAttribute("atomo", s);
                    List<AtomEvent> notifications = FeedUtil.createAtom(s);
                    if (!notifications.isEmpty()) {
                        System.err.println("new feed content");
                        for (AtomEvent ev : notifications) {
                            System.err.println("new feed content =\n " + ev.toString(true));
                        }
                    }
                }
            }
        } catch (Exception ex) {
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
