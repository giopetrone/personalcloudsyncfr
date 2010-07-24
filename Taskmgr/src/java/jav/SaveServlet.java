/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package jav;

import com.google.gson.Gson;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.PrintWriter;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import pubsublib.test.TestPub;

/**
 *
 * @author marino
 */
public class SaveServlet extends HttpServlet {

    /**
     * Processes requests for both HTTP <code>GET</code> and <code>POST</code> methods.
     * @param request servlet request
     * @param response servlet response
     * @throws ServletException if a servlet-specific error occurs
     * @throws IOException if an I/O error occurs
     */
    //  String valore;
    protected void processRequest(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        System.out.println("in save servlet");
        String nomeFile = request.getHeader("filenamemio");
        BufferedReader re = request.getReader();
        String flowSource = re.readLine();
        String owner = request.getHeader("owner");
        String users = request.getHeader("users");
        //     String loadjson = request.getHeader("loadjson");
        boolean publish = request.getHeader("publish") != null;
        String login = request.getHeader("login");
        if (users == null) {
            users = "";
        }
        String writers = request.getHeader("writers");
        //publish = true; // PER PROVA
        Gson gson = new Gson();
        //   vecchia versione Grafico ob = gson.fromJson(flowSource, Grafico.class);
        DeltaGrafico dg = gson.fromJson(flowSource, DeltaGrafico.class);
        Grafico ob = dg.getNuovo();
        //   System.out.println(ob.blocks.length);
        response.setContentType("text/html;charset=UTF-8");

        PrintWriter out = response.getWriter();
        try {
            if (nomeFile.contains("template")) {
                //   System.out.println("TEMPLATE-------");
                ob.setToTemplate(owner);
                users = "";
                writers = "";
            } else {
                users = ob.setUsers(owner, writers, users);
            }
            String val = GoDoc.saveDiagram(owner, nomeFile, publish, gson.toJson(ob), users, writers);
            out.println("new or old ?  " + val);
            if (val.equals("new")) {
                ob.createNewEvents(nomeFile, owner);
            } else if (val.equals("notnew")) {
                dg.createChangeEvents(nomeFile, owner);
            }
            new TestPub().testPublisher("http://localhost:8080", FeedUtil.SubFeedName(nomeFile));
            new TestPub().testPublisher("", FeedUtil.SubFeedName(nomeFile));
            SunFtpWrapper ftp = new SunFtpWrapper();
            ftp.uploadFeed(nomeFile);
            out.print("pubblicato evento");
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
