/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package jav;

import pubsub.FeedUtil;
import java.io.IOException;
import java.io.PrintWriter;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import event.AtomEvent;
import pubsub.Publisher;

/**
 *
 * @author marino
 */
public class ChangeStateServlet extends HttpServlet {

    /** 
     * Processes requests for both HTTP <code>GET</code> and <code>POST</code> methods.
     * @param request servlet request
     * @param response servlet response
     * @throws ServletException if a servlet-specific error occurs
     * @throws IOException if an I/O error occurs
     */
    protected void processRequest(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        response.setContentType("text/html;charset=UTF-8");
        PrintWriter out = response.getWriter();
        try {
            String nomeFile = request.getHeader("filenamemio");
            String user = request.getHeader("user");
            String activity = request.getHeader("activity");
            String state = request.getHeader("state");

            AtomEvent event = new AtomEvent(user, "TaskManager", activity);
            event.setParameter("File", nomeFile);
            event.setParameter("ChangeState", state);

            // occorre decidere che link abbia piu' senso

            //  FeedUtil.addEntry(editLink, nomeFile, event);
            FeedUtil.addEntry("", nomeFile, event, FeedUtil.isLocalMode()? "local":"remote");
            new Publisher().publish("", FeedUtil.SubFeedName(nomeFile));

            /* TODO output your page here */
            out.println("evento pubblicato");
            /*   */
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
