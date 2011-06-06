/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package jav;

import pubsub.FeedUtil;
import documentwatcher.GoDoc;
import com.google.gson.Gson;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.PrintWriter;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import maillib.SendMailCl;
import pubsub.Discovery;
import pubsub.Publisher;
import pubsub.Subscriber;

/**
 *
 * @author marino
 */
public class SaveServlet extends HttpServlet {
    public static String notification = "";

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
        String pwd = request.getHeader("pwd");
        String users = request.getHeader("users");
        System.out.println("USERS: " + users);
        String assignees = request.getHeader("assignees");
        System.out.println("Assignees: " + assignees);
        notification = request.getHeader("notification");
        FeedUtil.setLocalMode(notification);

        //     String loadjson = request.getHeader("loadjson");
        boolean publish = request.getHeader("publish") != null;
        String login = request.getHeader("login");
        if (users == null) {
            users = "";
        }
        publish = true; // PER PROVA
        String writers = request.getHeader("writers");
        System.out.println("Writers: " + writers);

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
            String val = GoDoc.saveDiagram(owner, nomeFile, publish, gson.toJson(ob), users, writers, pwd, assignees, notification);
            //    out.println("new or old ?  " + val);
            if (val.equals("new")) {
                ob.createNewEvents(nomeFile, owner);
            } else if (val.equals("notnew")) {
                System.out.println("NOT NEW in SAVE SERVLET");
                dg.createChangeEvents(nomeFile, owner);
            }
            if (!nomeFile.contains("template")) {
               

             /* FORSE INUTILE CON PIEMONTE   SunFtpWrapper ftp = new SunFtpWrapper();
                ftp.uploadFeed(nomeFile); */
         
                if(val.equals("new"))
                {
                    String url = FeedUtil.FeedUrl(nomeFile);
                      Discovery discovery = new Discovery();
                       String hub  = discovery.getHub(url);
                       System.err.println("2 sottoscrivo a:hub " +hub +", "+ url +", " + FeedUtil.GetUrl()+"NotifMgrG/NotifCallbackServlet");
                    new Subscriber().subscribe(url,FeedUtil.GetUrl()+"NotifMgrG/NotifCallbackServlet", "");
//                   
//                    //if(notification.equals("remote"))
////                 //   {
////                        String url = "http://www.piemonte.di.unito.it/Flow/"+nomeFile+".xml";
////                 //        String url = "http://taskmanagerunito.xoom.it/Flow/"+nomeFile+".xml";
////                     //   new TestPub().testPublisher("http://pubsubhubbub.appspot.com", "http://taskmanagerunito.xoom.it/Flow/"+nomeFile+".xml");
////                        new TestSub().testSubscriber(url, "http://www.piemonte.di.unito.it/NotifMgrG/NotifCallbackServlet","");
////                     //       new TestSub().testSubscriber(url, "http://taskmgrunito.appspot.com/NotifCallbackServlet","");
////                       //
////                    }
////                    else if(notification.equalsIgnoreCase("local"))
////                    {
////                        String url = "http://www.piemonte.di.unito.it/Flow/"+nomeFile+".xml";
////                     //   String url = "http://taskmanagerunito.xoom.it/Flow/"+nomeFile+".xml";
////                     //   new TestPub().testPublisher("http://localhost:8080", url);
////                        // LA sottoscrizione viene fatta in NotifMgrG
////                        new TestSub().testSubscriber(url, "http://localhost:8080/NotifMgrG/NotifCallbackServlet", "");
////                    }
                }
                else if(val.equalsIgnoreCase("notnew"))
                {
                    Discovery discovery = new Discovery();
                    String hub =  discovery.getHub(FeedUtil.FeedUrl(nomeFile));
                 //   String hub =  discovery.getHub(FeedUtil.SubFeedName(nomeFile));
                  //  String hub =  discovery.getHub("http://www.piemonte.di.unito.it/Flow/"+nomeFile+".xml");
                    //   String hub =  discovery.getHub("http://taskmanagerunito.xoom.it/Flow/"+nomeFile+".xml");
                   
                    System.out.println("PUBBLICO SU HUB: "+hub);

                     new Publisher().publish(hub, FeedUtil.FeedUrl(nomeFile));
                 //   new TestPub().testPublisher(hub, FeedUtil.SubFeedName(nomeFile));
                 //  new TestPub().testPublisher(hub, "http://www.piemonte.di.unito.it/Flow/"+nomeFile+".xml");
                    //  new TestPub().testPublisher(hub, "http://taskmanagerunito.xoom.it/Flow/"+nomeFile+".xml");

               }
                 //  versione con thread:
                //   FtpThread fp = new FtpThread(nomeFile);
                //   fp.start();
            }
            //       
            out.println(nomeFile);


        } catch (Exception ex) {
            ex.printStackTrace();
        } finally {
            out.close();
        }

    }


    private static String getTypeNotification()
    {
        return notification;
    }

    private static void setTypeNotification(String hub)
    {
        notification = hub;
    }


    public void sendMail(String dest,String login,String pwd,String name)
    {
        try
        {
            System.out.println("%%%%LOGIN: "+login);
            String SMTP_HOST_NAME = "smtp.gmail.com";
            String SMTP_PORT = "465";
            String url = FeedUtil.GetUrl() + "TaskMgr/index.jsp?Flow="+name;
            //15-12-2010 String url = "http://localhost:8081/index.jsp?Flow="+name;
            String url2 = FeedUtil.GetUrl() + "NotifMgrG/settings.jsp?Flow="+name;
            String text = "Hi, a new diagram is interested in you.\nGo and check this workflow "+url+"\n";
            String text2 ="Do not forget to subscribe your Notification Manager to this application: "+url2+"\n";
            String emailMsgTxt = text+text2;

            String emailSubjectTxt = "New Collaborative Workflow: "+name;
            String emailFromAddress = login;
            String SSL_FACTORY = "javax.net.ssl.SSLSocketFactory";
            String[] sendTo = dest.split(",");
            new SendMailCl().sendSSLMessage(sendTo, emailSubjectTxt, emailMsgTxt, emailFromAddress,pwd);
        }
        catch(Exception ex)
        {
            System.out.println("In SendMail Method: "+ex.getMessage());
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
