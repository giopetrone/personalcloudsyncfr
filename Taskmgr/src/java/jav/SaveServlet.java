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
        System.out.println("in save servlet");
        String nomeFile = request.getHeader("filenamemio");
        BufferedReader re = request.getReader();
        String flowSource = re.readLine();
        String owner = request.getHeader("owner");
        String users = request.getHeader("users");
       String loadjson = request.getHeader("loadjson");
    
      //  int loadblocks = Integer.parseInt(request.getHeader("blocks"));
      //  int loadconnections = Integer.parseInt(request.getHeader("connections"));
        if(users == null) users="";

        String login = request.getHeader("login");
        if (users == null) {
            users = "";
        }

        String writers = request.getHeader("writers");

        Gson gson = new Gson();
     //   vecchia versione Grafico ob = gson.fromJson(flowSource, Grafico.class);

        DeltaGrafico dg = gson.fromJson(flowSource, DeltaGrafico.class);
        
        Grafico ob = dg.getNuovo();
        
      //  System.err.println(ob.faGrafo());
        
        
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
                  if(found == null) found="";
                  if(!found.equals(""))
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
              dg.createChangeEvents(nomeFile,owner);
            /*  if(loadjson!=null){

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
                      if(newstatus == null) newstatus="";
                      String newassign = ob.blocks[j].assign;
                      if(newassign == null) newassign = "";
                      for(int i=0;i<oldsize;i++)
                      {
                          if(ob2.blocks[i].id.equals(id))
                          {
                              String oldstatus = ob2.blocks[i].type;
                              if(oldstatus == null) oldstatus = "";
                              String oldassign = ob2.blocks[i].assign;
                              if(oldassign == null) oldassign = "";
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
          }*/
              //System.err.println(size);
          }
       //   AtomEvent event = new AtomEvent();

      //    FeedUtil.addEntry(nomeFile, event);

          new TestPub().testPublisher("http://localhost:8080",FeedUtil.SubFeedName(nomeFile));

    
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
