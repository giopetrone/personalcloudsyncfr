/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package pubsublib.test;

import com.sun.syndication.feed.synd.SyndContent;
import com.sun.syndication.feed.synd.SyndEntry;
import com.sun.syndication.feed.synd.SyndFeed;
import com.sun.syndication.io.SyndFeedInput;
import pubsublib.event.AtomEvent;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.util.Enumeration;
import java.util.List;
import javax.servlet.ServletException;
import javax.servlet.ServletInputStream;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

/**
 *
 * @author marino
 */
public class callback extends HttpServlet {

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
        PrintWriter out = response.getWriter();
        String hubmode = "";
        String hubtopic = "";
        String hubchallenge = "";
        String hublease = "";
        String hubverify = "";
        try {
            response.setContentType("text/html");

            if (request.getParameter("hub.mode") != null) {
                hubmode = request.getParameter("hub.mode");
            }
            if (request.getParameter("hub.topic") != null) {
                hubtopic = request.getParameter("hub.topic");
            }
            if (request.getParameter("hub.challenge") != null) {
                hubchallenge = request.getParameter("hub.challenge");
            }
            if (request.getParameter("hub.lease") != null) {
                hublease = request.getParameter("hub.lease");
            }
            if (request.getParameter("hub.verify") != null) {
                hubverify = request.getParameter("hub.verify");
            }
            if (((hubmode.equals("subscribe")) || (hubmode.equals("unsubscribe")))
                    && (hubmode.length() > 0)) {
                //  response.setStatus(HttpServletResponse.SC_OK);
                response.getWriter().print(hubchallenge);
            }
            if (hubmode.equals("subscribe")) {
                System.err.println("\nin callback for hub request mode: "
                        + hubmode + " topic: "
                        + hubtopic + "challenge: "
                        + hubchallenge + " verify: "
                        + hubverify + " lease: "
                        + hublease + "\n");
            } else {
                System.err.println("headers richiesta:");
                Enumeration e = request.getHeaderNames();
                while (e.hasMoreElements()) {
                    String headers = (String) e.nextElement();
                    if (headers != null) {
                        System.err.println(headers + " " + request.getHeader(headers));
                    }
                }
                System.err.println("\n");
              
                ServletInputStream inStream = request.getInputStream();
                /*
                 String s = "";
                byte[] b = new byte[1024];
                 int c;
                while ((c = inStream.read(b)) != -1) {
                s += new String(b, 0, c);
                }
                System.err.println("new feed content =\n " + s);*/
                SyndFeedInput input = new SyndFeedInput();
                try {
                    SyndFeed feed = input.build(new InputStreamReader(inStream));
                    List<SyndEntry> entries = feed.getEntries();
                    SyndEntry entry = entries.get(0); // per ora 1 sola entry nuova ad ogni callback
                    SyndContent description = entry.getDescription();
                    String value = description.getValue();
                    AtomEvent event = AtomEvent.fromXml(value);
                    if (event != null) {
                        System.err.println("utente evento: " + event.getUser());
                    }
                } catch (Exception ex) {
                    ex.printStackTrace();
                }
            }
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
