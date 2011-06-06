/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package jav;

import documentwatcher.GoDoc;
import java.io.IOException;
import java.io.PrintWriter;
import java.util.List;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

/**
 *
 * @author marino
 */
public class LoadServlet extends HttpServlet {

    /**
     * Processes requests for both HTTP <code>GET</code> and <code>POST</code> methods.
     * @param request servlet request
     * @param response servlet response
     * @throws ServletException if a servlet-specific error occurs
     * @throws IOException if an I/O error occurs
     */
    protected void processRequest(HttpServletRequest request, HttpServletResponse response)
    throws ServletException, IOException {
        System.out.println("%%%% IN LOAD SERVLET %%%");
        String valorefile = request.getHeader("filenamemio");
        String owner = request.getHeader("owner");
        String pwd = request.getHeader("pwd");
        String val = null;
        response.setContentType("text/plain");
        PrintWriter out = response.getWriter();
        List<String> list = GoDoc.loadDiagram(valorefile, request.getHeader("refresh") != null,owner,pwd);
        
        if(list.isEmpty())
        {
            val = "DIAGRAMMA NON TROVATO";
           
        }
        else if(list.size() == 3)
        {
            val= list.get(0);
            String people = list.get(1);
            String writers = list.get(2);

            response.setHeader("people", people);
            response.setHeader("writers",writers);
           
        }
        else if(list.size() ==4)
        {
            System.out.println("LIST SIZE 4!!!!!!!!!!");
            val= list.get(0);
            String people = list.get(1);
            String writers = list.get(2);
            String nosave = list.get(3);
            response.setHeader("people", people);
            response.setHeader("writers",writers);
            response.setHeader("nosave",nosave);
        }
        try {
            /* TODO output your page here */
          //  out.println(val);
         //  System.err.println("LOADSERVLET sorgente: "+val);
            out.print(val);
          
           
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