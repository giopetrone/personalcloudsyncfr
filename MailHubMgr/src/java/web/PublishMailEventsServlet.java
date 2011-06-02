/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package web;

import hubstuff.MailHubEvents;
import java.io.IOException;
import java.io.PrintWriter;
import java.util.ArrayList;
import javax.servlet.ServletException; 
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import maillib.HubMailMsg;
import maillib.NewGmail;

/**
 *
 * @author giovanna
 */
 
public class PublishMailEventsServlet extends HttpServlet {

    NewGmail gmail = new NewGmail();

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
            // TODO output your page here
            out.println("<html>");
            out.println("<head>");
            out.println("<title>Servlet MailEventsServlet</title>");
            out.println("</head>");
            out.println("<body>");
            // out.println("<h1>Servlet MailEventsServlet at " + request.getContextPath () + "</h1>");

            //
            gmail = new NewGmail();
            ArrayList<HubMailMsg> msg = gmail.getMailMessages();
            MailHubEvents mhubE = new MailHubEvents();
            HubMailMsg hm=null;
            if (msg.size() > 0) {
                for (int i = 0; i < msg.size(); i++) {
                hm = msg.get(i);
                // hm.getTextContent(msg[0]); 
                String s = mhubE.toXML(hm);
                mhubE.publishMailEvent(s, "raw");
                out.println("messaggio in XML \n " + s);
                out.println("------------ Message " + (i + 1) + " ------------\n");
                out.println("To:    " + hm.getFrom());                
                out.println("\n SentDate : " + hm.getSentDate());
                out.println("\n Subject : " + hm.getSubject());
                 out.println("\n Body : " + hm.getContent());
                 
              //  System.out.print("Message : \n");
               // System.out.print(hm.getContent());
                 
            }
            }
            out.println("</body>");
            out.println("</html>");
        } finally {
            out.close();
        }
        
                      
                

            
    /**
     * SimpleAuthenticator is used to do simple authentication
     * when the SMTP server requires it.
     */
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
