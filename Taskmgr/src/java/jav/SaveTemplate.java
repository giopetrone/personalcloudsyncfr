package jav;

import com.sun.syndication.feed.synd.*;
import com.sun.syndication.io.SyndFeedInput;
import com.sun.syndication.io.SyndFeedOutput;
import com.sun.syndication.io.XmlReader;
import java.io.File;
import com.google.gson.Gson;
import java.io.BufferedReader;
import java.io.FileWriter;
import java.io.IOException;
import java.io.PrintWriter;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.Writer;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.LinkedList;
import java.util.List;

import pubsublib.event.AtomEvent;

import pubsublib.test.TestPub;

/**
 *
 * @author marino
 */
public class SaveTemplate extends HttpServlet {

    /**
     * Processes requests for both HTTP <code>GET</code> and <code>POST</code> methods.
     * @param request servlet request
     * @param response servlet response
     * @throws ServletException if a servlet-specific error occurs
     * @throws IOException if an I/O error occurs
     */
    private DateFormat DATE_PARSER = new SimpleDateFormat("yyyy-MM-dd");
    static String fileName = "/var/www/Atomi/marinofeed.xml";
    // SyndFeed feed = new SyndFeedImpl();
    SyndFeed feed = new SyndFeedImpl();
    String valore;

    protected void processRequest(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        String valorefile = request.getHeader("filenamemio");
        BufferedReader re = request.getReader();
        String s = re.readLine();
        String owner = request.getHeader("owner");
        String users = request.getHeader("users");
        String login = request.getHeader("login");
        if (users == null) {
            users = "";
        }
        String writers = request.getHeader("writers");
        Gson gson = new Gson();
        Grafico ob = gson.fromJson(s, Grafico.class);

        response.setContentType("text/html;charset=UTF-8");

        PrintWriter out = response.getWriter();
        try {
            if (valorefile.contains("template")) {
                out.println("Dentro IF");
                users = "";
                writers = "";
                int blocks = ob.blocks.length;


                String ownerBlock = ob.blocks[0].owner;
                for (int i = 0; i < blocks; i++) {
                    String imageid = ob.blocks[i].imageId;
                    if (ownerBlock == null) {
                        ob.blocks[i].owner = owner;
                    } else {
                        ob.blocks[i].owner = ownerBlock;
                    }
                    if (imageid.equalsIgnoreCase("rect")) {
                        out.println("DENTRO IF RECT");
                        ob.blocks[i].assign = null;
                        ob.blocks[i].date = null;
                        ob.blocks[i].shared = null;
                        ob.blocks[i].type = null;
                        ob.blocks[i].cat = null;


                    } else {
                        ob.blocks[i].shared = null;

                    }



                }
            } else {
                int blocks = ob.blocks.length;
                List<String> assign = new LinkedList();
                String ownerBlock = ob.blocks[0].owner;
                String assigner = "";
                if (ownerBlock == null) {
                    for (int i = 0; i < blocks; i++) {
                        ob.blocks[i].owner = owner;
                        ob.blocks[i].writers = writers;
                        ob.blocks[i].shared = users;


                    }

                } else {
                    for (int i = 0; i < blocks; i++) {
                        ob.blocks[i].owner = ownerBlock;
                        ob.blocks[i].writers = writers;
                        ob.blocks[i].shared = users;


                    }
                }



                for (int j = 0; j < blocks; j++) {
                    String found = ob.blocks[j].assign;
                    if (found == null) {
                        found = "";
                    }
                    assigner += found + ",";
                }
                assigner = assigner.substring(0, assigner.length() - 1);





                users = users + assigner;
                //    out.println(users);

            }



            String val = GoDoc.saveDiagram(login, valorefile, gson.toJson(ob), users, writers);


            AtomEvent event = new AtomEvent();

            addEntry(feed, event);
            //     new TestPub().testPublisher("http://pubsubhubbub.appspot.com","");
            //    new TestPub().testPublisher("https://code.launchpad.net/subhub","");
            new TestPub().testPublisher("http://localhost:8080", "");
        } catch (Exception ex) {
            ex.printStackTrace();
        } /*   */ finally {
            out.close();
        }
    }

    void addEntry(SyndFeed feed, AtomEvent event) {
        try {
            final SyndFeedInput input = new SyndFeedInput();
            File f = new File(fileName);
            feed = input.build(new XmlReader(f));
            List entries = feed.getEntries();
            SyndEntry entry;
            SyndContent description;
            entry = new SyndEntryImpl();
            entry.setTitle("ROME " + "12");
            entry.setLink("http://wiki.java.net/bin/view/Javawsxml/Rome03");
            //  entry.setPublishedDate(DATE_PARSER.parse("2009-07-" + i));
            entry.setPublishedDate(Calendar.getInstance().getTime());
            description = new SyndContentImpl();
            description.setType("text/plain");
            description.setValue(event.toXml());
            entry.setDescription(description);
            entries.add(entry);
            feed.setEntries(entries);

            Writer writer = new FileWriter(fileName);
            SyndFeedOutput output = new SyndFeedOutput();
            output.output(feed, writer);
            writer.close();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }// <editor-fold defaultstate="collapsed" desc="HttpServlet methods. Click on the + sign on the left to edit the code.">

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
