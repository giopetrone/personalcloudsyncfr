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
            String[] feeds = nomeFile.split(" ");
            System.err.println("subscribe di Notift  1");
            if (casoSubscribe != null) {
                   System.err.println("caso =" + casoSubscribe);

                if (casoSubscribe.equals("start")) {
                    for(int i=0;i<feeds.length;i++)
                    {
                        new TestSub().testSubscriber(FeedUtil.SubFeedName(feeds[i]), "http://localhost:8081/NotifMgrG/NotifCallbackServlet", "");
                //  new TestSub().testSubscriber(FeedUtil.SubFeedName(feeds[i]), "http://localhost:8081/NotifCallbackServlet", "");
                    
                    System.err.println("subscribe di Notift  feed= " + feeds[i]);}
            out.println("fatta subscribe");
                    out.close();
                } else {
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
