/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package web;

import com.sun.syndication.feed.synd.SyndContent;
import com.sun.syndication.feed.synd.SyndEntry;
import com.sun.syndication.feed.synd.SyndFeed;
import com.sun.syndication.io.SyndFeedInput;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.util.Enumeration;
import java.util.Iterator;
import java.util.List;
import javax.servlet.ServletException;
import javax.servlet.ServletInputStream;

import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import maillib.HubMailMsg;
import pubsublib.event.AtomEvent;

/**
 *
 * @author giovanna
 */
public class Callback extends HttpServlet {

    /** 
     * Processes requests for both HTTP <code>GET</code> and <code>POST</code> methods.
     * @param request servlet request
     * @param response servlet response
     * @throws ServletException if a servlet-specific error occurs
     * @throws IOException if an I/O error occurs
     */
    private boolean debugStream = false;
    private boolean printHeaders = false;
    private boolean many = true;

    protected void processRequest(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {

        // ricordare che deplkoy di callabck e' stooo ...../subscribe/pippo

        response.setContentType("text/html;charset=UTF-8");
        PrintWriter out = response.getWriter();

        System.err.println("CALLBACK inizio I");
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
                if (printHeaders) {
                    System.err.println("in callback, headers notifica:");
                    Enumeration e = request.getHeaderNames();
                    while (e.hasMoreElements()) {
                        String headers = (String) e.nextElement();
                        if (headers != null) {
                            System.err.println(headers + " " + request.getHeader(headers));
                        }
                    }
                    System.err.println("\n");
                }
                ServletInputStream inStream = request.getInputStream();
                if (debugStream) {
                    String s = "";
                    byte[] b = new byte[1024];
                    int c;
                    while ((c = inStream.read(b)) != -1) {
                        s += new String(b, 0, c);
                    }
                    System.err.println("new raw feed content =\n " + s);
                } else {
                    SyndFeedInput input = new SyndFeedInput();
                    try {
                        SyndFeed feed = input.build(new InputStreamReader(inStream));
                        List<SyndEntry> entries = feed.getEntries();
                        if (many) {
                            Iterator<SyndEntry> it = entries.iterator();
                            while (it.hasNext()) {
                                SyndEntry entry = it.next();
                                SyndContent description = entry.getDescription();
                                String value = description.getValue();
                                HubMailMsg event = (HubMailMsg)MailHubEvents.fromXml(value);
                                if (event != null) {
                                    System.err.println("callback: " + event.toString());
                                    SemInterpreter sem = new SemInterpreter();
                                    SmartEvent smart = sem.transformEvent(event);
                                    MailHubEvents mhubE = new MailHubEvents();
                                    String s = mhubE.toXML(smart);
                                    System.out.println("callback: smart event = " + s );
                                   
                                    mhubE.publishMailEvent(s, "smart");
                                }
                            }
                        } else {
                            SyndEntry entry = entries.get(0); // per ora 1 sola entry nuova ad ogni callback
                            SyndContent description = entry.getDescription();
                            String value = description.getValue();
                            AtomEvent event = AtomEvent.fromXml(value);
                            if (event != null) {
                                System.err.println("utente evento: " + event.getUser());
                            }
                        }
                    } catch (Exception ex) {
                        ex.printStackTrace();
                    }
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
