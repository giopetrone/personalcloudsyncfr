/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
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
import java.util.List;
import pubsublib.event.AtomEvent;
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
        String writers = request.getHeader("writers");
        Gson gson = new Gson();
        Grafico ob = gson.fromJson(s, Grafico.class);

        response.setContentType("text/html;charset=UTF-8");
       
        PrintWriter out = response.getWriter();
        try {
            /* TODO output your page here */
          //  out.println("<html>");
           // out.println("<head>");
           // out.println("<title>Servlet SaveServlet</title>");
          //  out.println("</head>");
          //  out.println("<body>");
          //  out.println("<h1> nome grafico " + valorefile + "</h1>");

        //    out.println("<h1> salvando diagramma " + val + "</h1>");
          /*  out.println("<h1> Connessione 0: " + ob.connections[0].target +  "</h1>");
            out.println("<h1> Connessione 1: "  +ob.connections[1].target +  "</h1>");
            out.println("<h1> Connessione 2: "  +ob.connections[2].target +  "</h1>");
            out.println("</body>");
            out.println("</html>");*/
            AtomEvent event = new AtomEvent();
          /*  
          String date = ob.blocks[0].date;
          String type = ob.blocks[0].type;
          String shared =ob.blocks[0].shared;
          String assign =ob.blocks[0].assign;
          String desc = ob.blocks[0].desc;
          String cat = ob.blocks[0].cat;
          String nome = ob.blocks[0].name;
          int lungh = ob.blocks.length;
          String size = Integer.toString(lungh);
          
          event.setParameter("Numero Task", size);
          event.setParameter("Nome",nome);
          event.setParameter("TYPE", type);
          event.setParameter("Due DATE", date);
          event.setParameter("SHARED with users", shared);
          event.setParameter("ASSIGN to users", assign);
          event.setParameter("Description",desc);
          event.setParameter("Category",cat); */
          int blocks = ob.blocks.length;
      
          String ownerBlock = ob.blocks[0].owner;
          if(ownerBlock == null)
          {
              for(int i=0;i<blocks;i++)
              {
                  ob.blocks[i].owner = owner;
                  ob.blocks[i].writers = writers;
                  ob.blocks[i].shared = users;


              }

          }
          else
          {
              for(int i=0;i<blocks;i++)
              {
                  ob.blocks[i].owner = ownerBlock;
                  ob.blocks[i].writers = writers;
                  ob.blocks[i].shared = users;


              }
          }

      /*    int lungh = ob.blocks.length;

          for(int i=0;i<lungh;i++)
          {
              //CERCHIAMO BLOCCHI A FORMA DI CONNECTION
              if(ob.blocks[i].imageId.equalsIgnoreCase("ellipse"))
              {
                  String id = ob.blocks[i].id;
                  String andor = ob.blocks[i].type;
                  List<String> list = new ArrayList();
                  int conn = ob.connections.length;
                  //TROVATO CONNECTION BLOCK, PRENDIAMO GLI ARCHI
                  for(int j=0;j<conn;j++)
                  {
                      //VEDIAMO SE IL NOSTRO BLOCCO FA DA TARGET
                      String target = ob.connections[j].target;
                      if(id.equals(target))
                      {
                          //PRENDIAMO LA CONNESSIONE SORGENTE:ABBIAMO NOI COME TARGET E LA SORGENTE
                          String source = ob.connections[j].source;
                          for(int l=0;l<lungh;l++)
                          {
                              String id2 = ob.blocks[l].id;
                              //MAPP TRA SOURCE E ID BLOCK PER TROVARE IL TIPO
                              if(id2.equalsIgnoreCase(source))
                              {
                                 //IL TIPO E' stato aggiunto
                                 String type = ob.blocks[l].type;
                                 list.add(type);

                              }
                          }

                      }
                  }
                  //ABBIAMO NELLA LISTA TUTTI I TIPI DEI NODI SORGENTI
                  //NOSTRO NODO E' AND
                  if(andor.equalsIgnoreCase("and"))
                  {
                     List<String> not = new ArrayList();
                     List<String> done = new ArrayList();
                     for(int h=0;h<list.size();h++)
                     {
                         String check = list.get(h);
                         //SIAMO in AND QUINDI SE UNO DEI DUE e' ROSSO, TARGET DIVENTA ROSSO
                         
                         if(check.equalsIgnoreCase("done"))
                         {
                             done.add(check);
                         }
                     }
                     if(done.size() != list.size())
                     {
                             for(int z=0;z<conn;z++)
                             {
                                 String source = ob.connections[z].source;
                                 if(id.equalsIgnoreCase(source))
                                 {
                                     String target = ob.connections[z].target;
                                     for(int j=0;j<lungh;j++)
                                     {
                                         String id3 = ob.blocks[j].id;
                                         if(target.equalsIgnoreCase(id3))
                                         {
                                             ob.blocks[j].type = "Not Started yet";
                                             
                                             String typee = ob.blocks[j].type;
                                             event.setParameter("TYPE", typee);
                                         }
                                     }
                                 }
                             }
                     }
                     else if(done.size() == list.size())
                     {
                         for(int z=0;z<conn;z++)
                             {
                                 String source = ob.connections[z].source;
                                 if(id.equalsIgnoreCase(source))
                                 {
                                     String target = ob.connections[z].target;
                                     for(int j=0;j<lungh;j++)
                                     {
                                         String id3 = ob.blocks[j].id;
                                         if(target.equalsIgnoreCase(id3))
                                         {
                                             ob.blocks[j].type = "In progress";
                                      
                                             ob.blocks[j].content.replaceAll("orange", "yellow");
                                            // ob.blocks[j].content.replace("Click to edit", "ciao");
                                       //      String content2 = "<div style="z-index: 2; position: absolute; width: 100px; top: 17px;" id="ele" class="hello" name="a"><span class="editable" id="span" name="mytextarea"><span style="color: orange;" id="nome" name="nome">Click to edit</span><input id="owner" name="owner" type="hidden"><input id="assign" name="assign" type="hidden"><input id="shared" name="shared" type="hidden"></span></div><img style="z-index: 1; position: absolute;" src="/img/flowchart/rect_f0f0f0_w100h50.gif" name="background" height="50" width="100">";
                                         //    val = ob.blocks[j].content.contains("ciao");
                                           
                                            
                                             
                                            // out.print(val);
                                            
                                        //     out.print(val);
                                         
                                         }
                                     }
                                 }
                             }

                     }
                         
                     

                  }
                  if(andor.equalsIgnoreCase("or"))
                  {

                     List<String> done = new ArrayList();
                     List<String> not = new ArrayList();
                     for(int h=0;h<list.size();h++)
                     {
                         //out.print("Size:" +list.size());
                         String check = list.get(h);
                         //out.print(check);

                         if(check.equalsIgnoreCase("done"))
                         {
                             done.add(check);
                            // out.print("dentro if");
                          //   out.print("Size Done :" +done.size());

                         }
                        // else if(check.equalsIgnoreCase("not started yet")) not.add(check);
                     }
                     

                         if(done.size() >= 1)
                         {
                             for(int z=0;z<conn;z++)
                             {
                                 String source = ob.connections[z].source;
                                 if(id.equalsIgnoreCase(source))
                                 {
                                     String target = ob.connections[z].target;
                                     for(int j=0;j<lungh;j++)
                                     {
                                         String id3 = ob.blocks[j].id;
                                         if(target.equalsIgnoreCase(id3))
                                         {
                                            // out.println("3");
                                             ob.blocks[j].type = "In progress";

                                            // String typee = ob.blocks[j].type;
                                            // event.setParameter("TYPE", typee);
                                         }
                                     }
                                 }
                             }
                     }
                         else
                         {

                            for(int z=0;z<conn;z++)
                             {
                                 String source = ob.connections[z].source;
                                 if(id.equalsIgnoreCase(source))
                                 {
                                     String target = ob.connections[z].target;
                                     for(int j=0;j<lungh;j++)
                                     {
                                         String id3 = ob.blocks[j].id;
                                         if(target.equalsIgnoreCase(id3))
                                         {
                                             //out.println("3");
                                             ob.blocks[j].type = "Not Started yet";

                                            // String typee = ob.blocks[j].type;
                                            // event.setParameter("TYPE", typee);
                                         }
                                     }
                                 }
                             }
                         }
                  }
                  

              }
          }*/

    //      ob.blocks[0].desc = "CIAO!!!!!!!!!!!!";
          //gson.toJson(ob);
         // gson.toJsonTree(ob);
         
          //AtomEvent event = new AtomEvent();
          String val = GoDoc.saveDiagram(valorefile, gson.toJson(ob));
           out.println(gson.toJson(ob));
          addEntry(feed,event);
     //     new TestPub().testPublisher("http://pubsubhubbub.appspot.com","");
       //    new TestPub().testPublisher("https://code.launchpad.net/subhub","");
          new TestPub().testPublisher("http://localhost:8080","");
             } catch (Exception ex) {ex.printStackTrace();
        }



          /*   */
         finally {
            out.close();
        }
    }

    void addEntry(SyndFeed feed,AtomEvent event) {
        try
        {
            final SyndFeedInput input = new SyndFeedInput();
            File f = new File(fileName);
            feed =  input.build(new XmlReader(f));
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