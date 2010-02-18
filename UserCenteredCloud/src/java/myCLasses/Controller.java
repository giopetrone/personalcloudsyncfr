package myCLasses;

/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */


import java.io.IOException;
import javax.servlet.RequestDispatcher;
import javax.servlet.ServletContext;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import java.util.*;
import javax.servlet.ServletConfig;
import javax.servlet.http.HttpSession;

/**
 *
 * @author liliana
 */
public class Controller extends HttpServlet {

    private String userID = "";

    private ArrayList<UserAgt> userAgents;
    private UserAgt userAgt; // user agent of current user

    private ArrayList<NotificationMgr> notifManagers;
    private NotificationMgr notifMgr; // notification manager of current user

    private EventAnalyzer eventAnalyzer;

                            // for timer
    private EventThread theThread;
                            // pool of events (timer)
    ArrayList<EventDescription> eventPool;

                            //chat clients of all cloud users
    HashMap<String, ChatClient> chatClients;



    // initialization of agents (UserAgt, etc.)
    public void init(ServletConfig c) throws ServletException {
        super.init(c);
        userID = ConfigurationSettings.userID;
        ConfigurationSettings.initializePasswords(); // sets google passwords

        eventAnalyzer = new EventAnalyzer();
                                 // user agent dell'utente
        userAgt = new UserAgt(userID);
        userAgents = new ArrayList();
        userAgents.add(userAgt);

                           // create GTalk client if connected to the internet
        if (ConfigurationSettings.internetConnection) {
                           // create chatClient for specific user (one for each user)
            chatClients = EventUtilities.createChatClients(ConfigurationSettings.passwords);
            EventUtilities.connectChatClients(chatClients);
            System.err.println(chatClients.size() +  " CHAT CLIENTS CONNECTED: " + chatClients.toString());
        }
 
                                             // notification Mgr dell'utente
        notifMgr = new NotificationMgr(userID, userAgt, chatClients);
        notifManagers = new ArrayList();
        notifManagers.add(notifMgr);

                    // initializes agents with context information
        ConfigurationSettings.initializeAgents(eventAnalyzer, userAgt, notifMgr);
                    
                                    // initialize user groups for events
        ConfigurationSettings.initializeUserGroups();

                                  //NB: must be created after creation of contexts
        if (ConfigurationSettings.withRandomEvents)
            eventPool = ConfigurationSettings.createEventPool();
        else eventPool = new ArrayList();
   }


    /** 
     * Processes requests for both HTTP <code>GET</code> and <code>POST</code> methods.
     * @param request servlet request
     * @param response servlet response
     * @throws ServletException if a servlet-specific error occurs
     * @throws IOException if an I/O error occurs
     */
    protected void processRequest(HttpServletRequest request, HttpServletResponse response)
    throws ServletException, IOException {

        ServletContext ctx = getServletContext();
        response.setContentType("text/html;charset=UTF-8");

        HttpSession ss = request.getSession();
                            // set core information, if needed
        if (ss.getAttribute("userID")==null)
            ss.setAttribute("userID", userID);
        if (ss.getAttribute("notifMgr")==null)
            ss.setAttribute("notifMgr", notifMgr);
        if (ss.getAttribute("userAgt")==null)
            ss.setAttribute("userAgt", userAgt);

        startTimer();

        //testIMs(); // test that each user can send IM to userID

        String op = request.getParameter("operation");

        if (op==null) {
            RequestDispatcher rd = ctx.getRequestDispatcher("/errorPage.html");
            rd.forward(request, response);
        }
        else if (op.equals("Mail Service")) { // service 1
            RequestDispatcher rd = ctx.getRequestDispatcher("/mailService.jsp");
            rd.forward(request, response);
        } else if (op.equals("Doc Sharing")) {
            RequestDispatcher rd = ctx.getRequestDispatcher("/docService.jsp");
            rd.forward(request, response);
        } else if (op.equals("Group Calendar")) {
            RequestDispatcher rd = ctx.getRequestDispatcher("/groupCalendarService.jsp");
            rd.forward(request, response);
        } else if (op.equals("Quit")) {
            closeSession(ss);
            RequestDispatcher rd = ctx.getRequestDispatcher("/quit.html");
            rd.forward(request, response);

        } else if (op.equals("send")) { // send mail
            handleSendMail(request, response);
            RequestDispatcher rd = ctx.getRequestDispatcher("/mailSent.jsp");
            rd.forward(request, response);

        } else if (op.equals("open")) { // open document
            String fileName = handleDocument(request, response);
            RequestDispatcher rd = ctx.getRequestDispatcher("/" + fileName);
            rd.forward(request, response);
        } else if (op.equals("save")) { // save document
            handleDocument(request, response);
            RequestDispatcher rd = ctx.getRequestDispatcher("/index.jsp");
            rd.forward(request, response);

        } else if (op.equals("Notifications")) { // view notifications page
            RequestDispatcher rd = ctx.getRequestDispatcher("/notifications.jsp");
            rd.forward(request, response);

        } else if (op.equals("searchDate")) { // ask for meeting dates
            searchDate(request, response, ss);
            RequestDispatcher rd = ctx.getRequestDispatcher("/meetingDates.jsp");
            rd.forward(request, response);
        } else if (op.equals("select date")) { // date for meeting selected
            handleMeetingDate(request, response, ss);
            RequestDispatcher rd = ctx.getRequestDispatcher("/meetingConfirmed.jsp");
            rd.forward(request, response);
        } else { // richieste sconosciute --> errore
            RequestDispatcher rd = ctx.getRequestDispatcher("/errorPage.html");
            rd.forward(request, response);
        }
        
    }

    private void closeSession(HttpSession ss) throws ServletException, IOException {
         if (theThread!=null)  {// stop event thread
             theThread.interrupt(); // NON FUNZIONA!!
            System.err.println("THREAD INTERRUPTED?: " + theThread.isInterrupted());
         }
         if (ConfigurationSettings.internetConnection) {
            try {       // if thread still sends IM --> exception (irrelevant!)
                EventUtilities.disconnectChatClients(chatClients); // disconnect from chat
            } catch (Exception e) {System.err.println("Controller: logout - " + e.toString());}
         }
         eventAnalyzer.clear(); // reset event analyzer
         for (int i=0; i<userAgents.size(); i++) // reset user agents
             userAgents.get(i).clear();
         for (int i=0; i<notifManagers.size(); i++) // reset notification managers
             notifManagers.get(i).clear();
         ss.invalidate(); // delete user session
    }


    private void startTimer() {
        if (theThread == null) {        // start timer for event generation
            theThread = new EventThread("EventThread",  eventAnalyzer,
                                        notifManagers, userAgents, eventPool);
            theThread.start();
        }
    }

    private void handleSendMail(HttpServletRequest request, HttpServletResponse response)
                                throws ServletException, IOException {
        ArrayList<String>params = new ArrayList();
        params.add("subject");
        params.add(request.getParameter("subject"));
        EventDescription ev = new EventDescription("GoogleMail", "true", "no", "MailSent",
               userID, ConfigurationSettings.mailReceivers,
               new ArrayList(), new Date().toString(), params);
        eventAnalyzer.publishEvent(ev, userAgents, notifManagers);
    }

    private String handleDocument(HttpServletRequest request, HttpServletResponse response)
                                throws ServletException, IOException {
        String documentName = request.getParameter("documentName");
        ArrayList<String>params = new ArrayList();
        params.add("docName");
        params.add(documentName); // in apertura documento
        EventDescription ev = new EventDescription("GoogleDocs", "true", "no", "DocUpdated",
              userID, new ArrayList(),
              new ArrayList(), new Date().toString(), params);
        if (documentName.equals("document1"))
            ev.setDestinatari(ConfigurationSettings.document1Contributors);
        else // document2: englishCourse
            ev.setDestinatari(ConfigurationSettings.document2Contributors);
        eventAnalyzer.publishEvent(ev, userAgents, notifManagers);
        return documentName + ".jsp";
    }
    
    private void searchDate(HttpServletRequest request, HttpServletResponse response, HttpSession ss)
                                throws ServletException, IOException {
                                              // identify involved spheres
        ArrayList<String> sphs = userAgt.getUserSpheres();
        ArrayList<String> selectedSphs = new ArrayList(); 
        for (int i=0; i<sphs.size(); i++) {
            String sph = sphs.get(i);
            if (request.getParameter(sph)!=null) { // sphere selected by user
                selectedSphs.add(sph);
            }
        }
        String topic = request.getParameter("meetingTopic");
                          // per etichettare l'evento successivo (hidden
                          // forms da jsp non passa correttamente il parametro
        ss.setAttribute("spheresForMeeting", selectedSphs);
        ss.setAttribute("meetingTopic", topic);
                                                // creo evento
        ArrayList<String>params = new ArrayList();
        params.add("subject"); params.add(topic);
        params.add("date"); params.add(""); // date not fixed yet
        EventDescription ev = new EventDescription("GroupCalendar", "true", "no", "MeetingConfirmation",
              userID, new ArrayList(), selectedSphs, new Date().toString(), params);
        eventAnalyzer.publishEvent(ev, userAgents, notifManagers);
    }

    private void handleMeetingDate(HttpServletRequest request, HttpServletResponse response, HttpSession ss)
                                throws ServletException, IOException {

        ArrayList<String> selectedSphs = (ArrayList)ss.getAttribute("spheresForMeeting");
        String topic = (String)ss.getAttribute("meetingTopic");
        ArrayList<String>params = new ArrayList();
        params.add("subject"); params.add(topic);
        params.add("date"); params.add(request.getParameter("meetingDate"));
        EventDescription ev = new EventDescription("GroupCalendar", "true", "no", "MeetingConfirmation",
              userID, new ArrayList(), selectedSphs, new Date().toString(), params);
        eventAnalyzer.publishEvent(ev, userAgents, notifManagers);
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


                        // method for testing IM generation for each cloud user
    private void testIMs() {
        notifMgr.genIM("utntest1@gmail.com", userID, "provaU1");
        notifMgr.genIM("utntest1@gmail.com", "utntest2@gmail.com", "provaU2");
        notifMgr.genIM("utntest1@gmail.com", "utntest3@gmail.com", "provaU3");
        notifMgr.genIM("utntest1@gmail.com", "utntest4@gmail.com", "provaU4");
        notifMgr.genIM("utntest1@gmail.com", "utntest5@gmail.com", "provaU5");
        notifMgr.genIM("utntest1@gmail.com", "utntest6@gmail.com", "provaU6");
        notifMgr.genIM("utntest1@gmail.com", "utntest7@gmail.com", "provaU7");
        notifMgr.genIM("utntest1@gmail.com", "utntest8@gmail.com", "provaU8");
        notifMgr.genIM("utntest1@gmail.com", "utntest9@gmail.com", "provaU9");
        notifMgr.genIM("utntest1@gmail.com", "utntest10@gmail.com", "provaU10");
        notifMgr.genIM("utntest1@gmail.com", "utntest11@gmail.com", "provaU11");
        notifMgr.genIM("utntest1@gmail.com", "utntest12@gmail.com", "provaU12");
    }

}// end class
