/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package jav;

import com.google.gdata.client.docs.DocsService;
import googletalkclient.ChatClient;
import java.io.IOException;
import java.io.PrintWriter;
import javax.servlet.ServletException;
import javax.servlet.ServletInputStream;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import maillib.SendMailCl;




/**
 *
 * @author fabrizio
 */
public class NotifCallbackServlet extends HttpServlet {
     ChatClient chClient = new ChatClient();
   
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
             String refresh = request.getHeader("notifica");
            String email = "Administration@taskmgrunito.appspotmail.com";
            String pwd = "gregorio";
            if (refresh != null) {
                //ciclic ajax call from UI
                String notif = (String) getServletContext().getAttribute("atomo");
                if (notif != null) {  // we have received new feed content
                    out.println(notif); // send it to client
                    // set new feed content to nul to avoid duplicate notifications
                    getServletContext().setAttribute("atomo", null);
                } else {
                    System.err.println("session, get=" + getServletContext());
                    out.println("nulla");
                }
                out.close();
            } else {
                // request from HUB: challenge or callback
                String hubmode = "";
                if (request.getParameter("hub.mode") != null) {
                    hubmode = request.getParameter("hub.mode");
                }
                if (((hubmode.equals("subscribe")) || (hubmode.equals("unsubscribe")))
                        && (hubmode.length() > 0)) {
                    String hubchallenge = "";
                    if (request.getParameter("hub.challenge") != null) {
                        hubchallenge = request.getParameter("hub.challenge");
                    }
                    response.setStatus(HttpServletResponse.SC_OK);
                    out.print(hubchallenge);
                    /*
                    String hubtopic = "";
                    String hublease = "";
                    String hubverify = "";
                    if (request.getParameter("hub.topic") != null) {
                    hubtopic = request.getParameter("hub.topic");
                    }
                    if (request.getParameter("hub.lease") != null) {
                    hublease = request.getParameter("hub.lease");
                    }
                    if (request.getParameter("hub.verify") != null) {
                    hubverify = request.getParameter("hub.verify");
                    }

                    System.err.println("\nin callback for hub request mode: "
                    + hubmode + " topic: "
                    + hubtopic + "challenge: "
                    + hubchallenge + " verify: "
                    + hubverify + " lease: "
                    + hublease + "\n");
                     */
                } else
                {
                    ServletInputStream inStream = request.getInputStream();
                    String s = "";

                    byte[] b = new byte[1024];
                    int c;
                    while ((c = inStream.read(b)) != -1) {
                        s += new String(b, 0, c);
                        
                    }
                    getServletContext().setAttribute("atomo", s);
                    System.err.println("DOPO CONTEXT");
                    s = s.replaceAll("&gt;", ">");
                    s = s.replaceAll("&lt;", "<");
                    String modifier = "";
                    String nomeFile = "";
                    System.err.println("@@@@@@@@@@@@@@@@@@@@@@@@@@");
                    System.err.println(s);
                    System.err.println("@@@@@@@@@@@@@@@@@@@@@@@@@@@");
                    String[] entries = s.split("<entry>");
                    String taskname = "";
                    String newstatus = "";
                    String assignees="";
                    String workflow ="";
                    String allusers = "";
                    for(int i=0;i<entries.length;i++)
                    {
                        System.out.println("%%%%%%%%%%%%%%%%%%%%%%%");
                        System.err.println(entries[i]);
                        
                        if(entries[i].contains("Update Diagram"))
                        {
                               modifier = getUser(entries[i]);
                               nomeFile = getFlowName(entries[i]);
                            
                        }
                        else if(entries[i].contains("Workflow is Done"))
                        {
                            allusers = getAllUsers(entries[i]);
                        }
                        
                      
                    }
                    for(int i=0;i<entries.length;i++)
                    {
                        if(entries[i].contains("Change Status of Task"))
                        {
                            taskname = getTaskName(entries[i]);
                            if(entries[i].contains("Assigned To"))  
                            {
                                System.err.println("C'e' ASSIGNED TO");
                                assignees = getAssignees(entries[i]);
                            }
                            if(assignees != null && !assignees.equals("") && !assignees.equalsIgnoreCase(modifier))
                            {
                                String[] destinatarifinali = assignees.split(",");
                                for(int j=0;j<destinatarifinali.length;j++)
                                {
                                    if(destinatarifinali[j] != null && !destinatarifinali[j].equals(""))
                                    {
                                        String permissionFile = extractPermission(destinatarifinali[j]);
                                        String subscribe = GoDoc.checkPermission(permissionFile, nomeFile+".txt","Changestatusoftask");
                                        if(subscribe.equals("subscribe"))
                                        {
                                            String emailSubjectTxt ="The task: "+taskname+" has changed status";
                                            String[] destinatario = {destinatarifinali[j]};
                                            String text = "The task: "+taskname+" in the workflow "+nomeFile+" has changed status";
                                            new SendMailCl().sendSSLMessage(destinatario, emailSubjectTxt, text, email,pwd);

                                        }


                                    }

                                }
                            }
                        }
                    }
                    for(int i=0;i<entries.length;i++)
                    {
                        if(entries[i].contains("A task has been deleted"))
                        {
                            taskname = getTaskName(entries[i]);
                            assignees = getAssignees(entries[i]);
                        }
                        if(assignees != null && !assignees.equals("") && !assignees.equalsIgnoreCase(modifier))
                            {
                                String[] destinatarifinali = assignees.split(",");
                                for(int j=0;j<destinatarifinali.length;j++)
                                {
                                    if(destinatarifinali[j] != null && !destinatarifinali[j].equals(""))
                                    {
                                        String permissionFile = extractPermission(destinatarifinali[j]);
                                        String subscribe = GoDoc.checkPermission(permissionFile, nomeFile+".txt","Dletetask");
                                        if(subscribe.equals("subscribe"))
                                        {
                                            String emailSubjectTxt ="The task: "+taskname+" has been deleted";
                                            String[] destinatario = {destinatarifinali[j]};
                                            String text = "The task: "+taskname+" in the workflow "+nomeFile+" has been deleted";
                                            new SendMailCl().sendSSLMessage(destinatario, emailSubjectTxt, text, email,pwd);

                                        }


                                    }

                                }
                            }
                    }



                    DocsService service = null;
                    if(allusers != null && !allusers.equals("") && !nomeFile.equals("") )
                    {
                        if(allusers.contains(" ")) allusers = allusers.replace(" ", "");
                        String[] destinatarifinali = allusers.split(",");
                        for(int j=0;j<destinatarifinali.length;j++)
                        {
                            if(destinatarifinali[j] != null && !destinatarifinali[j].equals(""))
                            {
                                String permissionFile = extractPermission(destinatarifinali[j]);
                                System.err.println("$$$$$$$$$$ "+permissionFile);
                                String subscribe = GoDoc.checkPermission(permissionFile, nomeFile+".txt","Workflowisdone");
                                if(subscribe.equals("subscribe"))
                                {
                                    String emailSubjectTxt ="The workflow: "+nomeFile+" is completed";
                                    String[] destinatario = {destinatarifinali[j]};
                                    String link = "http://taskmanagerunito.xoom.it/Flow/"+nomeFile+".txt.xml";
                                    String text = "The workflow is completed.\nYou can see the feed at: "+link;
                                    new SendMailCl().sendSSLMessage(destinatario, emailSubjectTxt, text, email,pwd);

                                }

                            }

                        }
                        
                    }
           
}}}
                    catch(Exception ex){System.err.println("Error: "+ex.getMessage());}
           
         finally { 
            out.close();
        }
    }


    private String getAllUsers(String entry)
    {
        try
        {
            int index = entry.indexOf("All users</string>");
            int index2 = entry.lastIndexOf("</string>");
            String allusers = entry.substring(index+31, index2);
            System.err.println("ALLUSERS: "+allusers);
            return allusers;
        }catch(Exception ex)
        {
            System.err.println("errore in get all users: "+ex.getMessage());
            return ex.toString();
        }

    }

    private String getTaskName(String entry)
    {
        try
        {
            
            String[] split = entry.split("<string>Task</string>");
            String tofind = split[1];
            int index = tofind.indexOf("<string>");
            int index2 = tofind.indexOf("</string>");
            String taskname = tofind.substring(index+8,index2);
           
            System.err.println("TASK NAME: "+taskname);
            return taskname;
        }catch(Exception ex)
        {
            System.err.println("errore in get taskname: "+ex.getMessage());
            return ex.toString();
        }
    }

    private String getAssignees(String entry)
    {
        try
        {
            
            String[] split = entry.split("<string>Assigned To</string>");
            String tofind = split[1];
            int index = tofind.indexOf("<string>");
            int index2 = tofind.indexOf("</string>");
            String assignees = tofind.substring(index+8, index2);
            if(assignees.contains(",")) assignees = assignees.replaceAll(",","");
            System.err.println("ASSIGNEES: "+assignees);
            return assignees;
        }catch(Exception ex)
        {
            System.err.println("errore in get assignees: "+ex.getMessage());
            return ex.toString();
        }
    }





    private String getUser(String entry)
    {
        try
        {

            int userindex = entry.indexOf("<user>");
            int userindex2 = entry.indexOf("</user>");
            String user = entry.substring(userindex+6, userindex2);
            System.err.println("MODIFIER: "+user);
            return user;
        }catch(Exception ex)
        {
            System.err.println("errore in get user: "+ex.getMessage());
            return ex.toString();
        }
    }

    private String extractPermission(String user)
    {
        int index = user.indexOf("@");
        String nomeDoc = user.substring(0,index);
        if(nomeDoc.contains(".")) nomeDoc = nomeDoc.replace(".", "_");
        nomeDoc += ".txt";
        return nomeDoc;
    }



    private String getFlowName(String entry)
    {
        try
        {
            int indexFile = entry.indexOf("File");
            int indexFile2 = entry.indexOf(".txt</string>");
            String flow = entry.substring(indexFile+26,indexFile2);
            System.err.println("Flow Name: "+flow);
            return flow;
        }catch(Exception ex)
        {
            System.err.println("errore in get flow: "+ex.getMessage());
            return ex.toString();
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
