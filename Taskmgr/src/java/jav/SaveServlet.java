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
import java.util.LinkedList;
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
    //  String valore;
    protected void processRequest(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        System.err.println("in save servlet");
        String nomeFile = request.getHeader("filenamemio");
        BufferedReader re = request.getReader();
        String flowSource = re.readLine();
        String owner = request.getHeader("owner");
        String users = request.getHeader("users");
        String loadjson = request.getHeader("loadjson");
       // System.err.println(loadjson);
      //  int loadblocks = Integer.parseInt(request.getHeader("blocks"));
      //  int loadconnections = Integer.parseInt(request.getHeader("connections"));
        if(users == null) users="";

        String login = request.getHeader("login");
        if (users == null) {
            users = "";
        }

        String writers = request.getHeader("writers");

        Gson gson = new Gson();
        Grafico ob = gson.fromJson(flowSource, Grafico.class);
        
        
        response.setContentType("text/html;charset=UTF-8");

        PrintWriter out = response.getWriter();
        try {
            if (nomeFile.contains("template")) {
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
                        ob.blocks[i].template = "template";

                    } else {
                        ob.blocks[i].template = "template";
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




         for(int j=0;j<blocks;j++)
         {
             if(ob.blocks[j].imageId.equalsIgnoreCase("rect"))
             {
                 System.out.println("qui dentro");
              String found = ob.blocks[j].assign;
              if(found != null) assigner += ","+found + ",";
              
             }

         }

              
                //    assigner = assigner.substring(0, assigner.length() - 1);






                users = users + assigner;
            //   System.out.println("USERS: "+users);

            }


            
          
          String val = GoDoc.saveDiagram(owner,  nomeFile, gson.toJson(ob), users, writers);
          out.println("VALLLLLLLLLLLLLL "+val);
          if(val.equals("new"))
          {
              
              for(int j=0;j<ob.blocks.length;j++)
            {

              if(ob.blocks[j].imageId.equalsIgnoreCase("rect"))
              {
                  String found = ob.blocks[j].assign;
                  if(found !=null)
                  {
                      AtomEvent event = new AtomEvent(owner, "TaskManager", "Assign User");
                      event.setParameter("Task", ob.blocks[j].name);
                    //  event.setParameter("Permission", "Write");
                      event.setParameter("Who", found);
                      FeedUtil.addEntry("", nomeFile, event);
                  }
              }

             }

          }
          else if(val.equals("notnew"))
          {
              if(loadjson!=null){

              Gson gsonLoad = new Gson();
              Grafico ob2 = gsonLoad.fromJson(loadjson, Grafico.class);
              int newsize = ob.blocks.length;
              int oldsize = ob2.blocks.length;
              int newconnections = ob.connections.length;
              int oldconnections = ob2.connections.length;
              if(newsize < oldsize || newsize > oldsize)
              {

                  AtomEvent event = new AtomEvent();
                  event.setActivity("Number of blocks is changed");
                     // event.setParameter("assigners",found);
                   FeedUtil.addEntry("",nomeFile, event);
              }
              if(newconnections < oldconnections || newconnections > oldconnections)
              {

                  AtomEvent event = new AtomEvent();
                  event.setActivity("Number of connections is changed");
                     // event.setParameter("assigners",found);
                   FeedUtil.addEntry("",nomeFile, event);
              }
              for(int j=0;j<newsize;j++)
              {
                  if(ob.blocks[j].imageId.equalsIgnoreCase("rect"))
                  {
                      String id = ob.blocks[j].id;
                      String newstatus = ob.blocks[j].type;
                      String newassign = ob.blocks[j].assign;
                      for(int i=0;i<oldsize;i++)
                      {
                          if(ob2.blocks[i].id.equals(id))
                          {
                              String oldstatus = ob2.blocks[i].type;
                              String oldassign = ob2.blocks[i].assign;
                              if(!newstatus.equals(oldstatus))
                              {

                                  AtomEvent event = new AtomEvent(owner, "TaskManager", "Change Status of Task");
                                  event.setParameter("Task", ob.blocks[j].name);
                                //  event.setParameter("Permission", "Write");
                                  event.setParameter("New Status", newstatus);
                                  FeedUtil.addEntry("", nomeFile, event);
                              }
                              if(!newassign.equals(oldassign))
                              {
                                  AtomEvent event = new AtomEvent(owner, "TaskManager", "Change Users assigned to Task");
                                  event.setParameter("Task", ob.blocks[j].name);
                                //  event.setParameter("Permission", "Write");
                                  event.setParameter("New Assigned users", newassign);
                                  FeedUtil.addEntry("", nomeFile, event);
                              }
                          }
                      }
                  }
              }
              //System.err.println(length);
          }//System.err.println(size);
          }
       //   AtomEvent event = new AtomEvent();

      //    FeedUtil.addEntry(nomeFile, event);

          new TestPub().testPublisher("http://localhost:8080",FeedUtil.SubFeedName(nomeFile));

         // out.println(gson.toJson(ob));


            

            // out.println(gson.toJson(ob));

      /*


              int length = ob.blocks.length;
              if(length < loadblocks || length > loadblocks)
              {

                  AtomEvent event = new AtomEvent();
                  event.setActivity("Number of blocks is changed");
                     // event.setParameter("assigners",found);
                   FeedUtil.addEntry("",nomeFile, event);
              }
              int connlength = ob.connections.length;
              if(connlength < loadconnections || connlength > loadconnections)
              {

                  AtomEvent event = new AtomEvent();
                 event.setActivity("Number of connections is changed");
                     // event.setParameter("assigners",found);
                   FeedUtil.addEntry("",nomeFile, event);
              }



       int lungh = ob.blocks.length;

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


    //      ob.blocks[0].desc = "CIAO!!!!!!!!!!!!";
          //gson.toJson(ob);
         // gson.toJsonTree(ob);
         
          
             } catch (Exception ex) {ex.printStackTrace();
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
            //AtomEvent(String user, String application, String activity)
        //   String editLink = GoDoc.saveDiagram(owner,  nomeFile, gson.toJson(ob), users, writers);
      //      AtomEvent event = new AtomEvent(login != null ? login : owner, "TaskManager", "Save");
      //      event.setParameter("File", nomeFile);
      //     FeedUtil.addEntry(editLink, nomeFile, event);
            new TestPub().testPublisher("", FeedUtil.SubFeedName(nomeFile));
            out.print("pubblicato evento");
        } catch (Exception ex) {
            ex.printStackTrace();
        } /*   */ finally {
            out.close();
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
