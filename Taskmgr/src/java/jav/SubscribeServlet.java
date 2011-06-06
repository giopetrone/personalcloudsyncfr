package jav;

/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
import pubsub.FeedUtil;
import java.io.IOException;
import java.io.PrintWriter;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import pubsub.Subscriber;

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
        PrintWriter out = response.getWriter();

        String casoSubscribe = null;
        try {
            response.setContentType("text/html");
            casoSubscribe = request.getHeader("notifica");
            String nomeFile = request.getHeader("filenamemio");
            if (casoSubscribe != null) {
                if (casoSubscribe.equals("start")) {
                    System.err.println("1 sottoscrivo a:"+FeedUtil.SubFeedName(nomeFile) +", "+"http://localhost:8080/CallbackServlet");
                    new  Subscriber().subscribe(FeedUtil.SubFeedName(nomeFile), "http://localhost:8080/CallbackServlet", "");
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
