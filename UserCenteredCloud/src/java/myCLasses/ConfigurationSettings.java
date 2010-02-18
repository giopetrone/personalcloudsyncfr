package myCLasses;

/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

/**
 *
 * @author liliana
 */
import java.util.*;

/**
 *
 * @author liliana
 */
public class ConfigurationSettings {

                                            // notification policy
    public static boolean contextFilter = false;
                                            // generate IMs / print on server log
    public static final boolean internetConnection = true;
                                            // generate random events or not
    public static final boolean withRandomEvents = true;
                                             // delay for timer cycle
    public static final int timerDelay = 5;
                                            // number of cicles of timer thread before terminating
    public static final int timerCycles = 1000;
                                            // remove less recent events from
                                            // event list of Notification Manager
    public static final boolean forgetEvents = false;
                    // max length of eventList maintained by Notification Manager
    public static final int NOTIFICATION_LENGTH = 30;

        // NB: user utntest11@gmail.com must not be used for events:
        // it is used to send IMs from user centered cloud environment
        // e.g., summaries of filtered events
                                        // current user
    public static final String userID = "utntest1@gmail.com";

                                    // stores Google passwords of all users
    public static HashMap<String, String> passwords = new HashMap();

                       // gruppi di destinatari globali (usati nel Controller.java)
    public static ArrayList<String> document1Contributors = new ArrayList();
    public static ArrayList<String> document2Contributors = new ArrayList();
    public static ArrayList<String> mailReceivers = new ArrayList();


    public static void initializeAgents(EventAnalyzer evAn,
                             UserAgt userAgt, NotificationMgr notifMgr) {
                                        // define spheres
                                        // Home sphere
        ArrayList<String> h = new ArrayList();
        h.add(userID); h.add("utntest2@gmail.com"); h.add("utntest3@gmail.com");
        EventUtilities.createUserSphere("Home", h, evAn, userAgt, notifMgr);
                                        // Project1 ok
        ArrayList<String> p1 = new ArrayList();
        p1.add(userID); 
        p1.add("utntest4@gmail.com"); p1.add("utntest5@gmail.com"); p1.add("utntest6@gmail.com"); // versione OK
        EventUtilities.createUserSphere("Project1", p1, evAn, userAgt, notifMgr);
        /*
                            // Project1 - test di ambiguita'
        ArrayList<String> p1 = new ArrayList();
        p1.add(userID);
        p1.add("utntest2@gmail.com"); p1.add("utntest3@gmail.com");
        //p1.add("utntest4@gmail.com"); // variante ulteriore
        EventUtilities.createUserSphere("Project1", p1, eventAnalyzer, userAgt, notifMgr);
         * */
                                        // Project2
        ArrayList<String> p2 = new ArrayList();
        p2.add(userID); p2.add("utntest7@gmail.com"); 
        p2.add("utntest8@gmail.com"); p2.add("utntest9@gmail.com");
        EventUtilities.createUserSphere("Project2", p2, evAn, userAgt, notifMgr);        
                                        // EnglishCourse
        ArrayList<String> eng = new ArrayList();
        eng.add(userID); eng.add("utntest12@gmail.com");
        EventUtilities.createUserSphere("EnglishCourse", eng, evAn, userAgt, notifMgr);
    }

                             // sets the Google passwords of cloud users
    public static void initializePasswords() {
        passwords.put("utntest1@gmail.com", "passtest");
        passwords.put("utntest2@gmail.com", "passtest");
        passwords.put("utntest3@gmail.com", "passtest");
        passwords.put("utntest4@gmail.com", "passtest");
        passwords.put("utntest5@gmail.com", "passtest");
        passwords.put("utntest6@gmail.com", "passtest");
        passwords.put("utntest7@gmail.com", "passtest");
        passwords.put("utntest8@gmail.com", "passtest");
        passwords.put("utntest9@gmail.com", "passtest");
        passwords.put("utntest10@gmail.com", "passtest");
        passwords.put("utntest11@gmail.com", "passtest");
        passwords.put("utntest12@gmail.com", "passtest");
    }

    public static void initializeUserGroups() {
                    // ora il document1 e' abbinato a u1, u4, u5, u6
                    // per cambiare gruppo di destinatari modificare qui
        document1Contributors.add(userID);
        document1Contributors.add("utntest4@gmail.com");
        document1Contributors.add("utntest5@gmail.com");
        document1Contributors.add("utntest6@gmail.com");
                    // document2
        document2Contributors.add(userID);
        document2Contributors.add("utntest12@gmail.com");
                    // messaggio di posta elettronica
        mailReceivers.add(userID);
        mailReceivers.add("utntest2@gmail.com");
        mailReceivers.add("utntest3@gmail.com");
    }

                                // creates pool of events to be generated randomly
    public static ArrayList<EventDescription> createEventPool() {
                            // gruppi di destinatari locali (per createEventPool)
        ArrayList<String> u1u2u3Group = new ArrayList();
        u1u2u3Group.add(userID);
        u1u2u3Group.add("utntest2@gmail.com");
        u1u2u3Group.add("utntest3@gmail.com");

        ArrayList<String> u1u4u5u6Group = new ArrayList();
        u1u4u5u6Group.add(userID);
        u1u4u5u6Group.add("utntest4@gmail.com");
        u1u4u5u6Group.add("utntest5@gmail.com");
        u1u4u5u6Group.add("utntest6@gmail.com");

        ArrayList<String> u1u7u8u9Group = new ArrayList();
        u1u7u8u9Group.add(userID);
        u1u7u8u9Group.add("utntest7@gmail.com");
        u1u7u8u9Group.add("utntest8@gmail.com");
        u1u7u8u9Group.add("utntest9@gmail.com");

        ArrayList<String> u1u12Group = new ArrayList();
        u1u12Group.add(userID);
        u1u12Group.add("utntest12@gmail.com");
                                            // sphere tags
        ArrayList<String> homeTag = new ArrayList();
        homeTag.add("Home");
        ArrayList<String> project1Tag = new ArrayList();
        project1Tag.add("Project1");
        ArrayList<String> project2Tag = new ArrayList();
        project2Tag.add("Project2");
        ArrayList<String> englishTag = new ArrayList();
        englishTag.add("EnglishCourse");
                                    // parametri degli eventi

                                    // deliverable D1.5
        ArrayList<String> d15 = new ArrayList();
        d15.add("docName"); d15.add("deliverable1.5");
        d15.add("docLink"); d15.add("linkD15");
                                    // deliverable D1.6
        ArrayList<String> d16 = new ArrayList();
        d16.add("docName"); d16.add("deliverable1.6");
        d16.add("docLink"); d16.add("linkD16");
                                    // menu per cena
        ArrayList<String> menuCena = new ArrayList();
        menuCena.add("docName"); menuCena.add("menuCena");
        menuCena.add("docLink"); menuCena.add("linkMenuCena");
                                    // data di riunione
        ArrayList<String> riunione = new ArrayList();
        riunione.add("subject"); riunione.add("project review");
        riunione.add("date"); riunione.add("27/02/2010");
                                    // data di cena
        ArrayList<String> cena = new ArrayList();
        cena.add("subject"); cena.add("dinner");
        cena.add("date"); cena.add("27/02/2010");

                                            // data di saggio
        ArrayList<String> saggio = new ArrayList();
        saggio.add("subject"); saggio.add("final show");
        saggio.add("date"); saggio.add("06/06/2010");

                                            // data di lezione inglese
        ArrayList<String> lezioneInglese = new ArrayList();
        lezioneInglese.add("subject"); lezioneInglese.add("english class");
        lezioneInglese.add("date"); lezioneInglese.add("13/02/2010");


                            // initializes the pool of events to choose from for notification
        ArrayList<EventDescription> pool = new ArrayList();
        pool.add(new EventDescription("GoogleDocs", "true", "no", "DocUpdated",
                                            "utntest7@gmail.com", u1u7u8u9Group, new ArrayList(),
                                            new Date().toString(), menuCena));
        pool.add(new EventDescription("GoogleDocs", "true", "no", "DocUpdated",
                                            "utntest7@gmail.com", u1u7u8u9Group, new ArrayList(),
                                            new Date().toString(), menuCena));
        pool.add(new EventDescription("GroupCalendar", "true", "no", "MeetingConfirmation",
                                            "utntest7@gmail.com", new ArrayList(), project2Tag,
                                            new Date().toString(), cena));
        pool.add(new EventDescription("GoogleDocs", "true", "no", "DocUpdated",
                                            "utntest9@gmail.com", u1u7u8u9Group, new ArrayList(),
                                            new Date().toString(), d15));
        pool.add(new EventDescription("GoogleDocs", "true", "no", "DocUpdated",
                                            "utntest8@gmail.com", u1u7u8u9Group, new ArrayList(),
                                            new Date().toString(), d15));
        pool.add(new EventDescription("GoogleDocs", "true", "no", "DocCreated",
                                            "utntest9@gmail.com", u1u7u8u9Group, new ArrayList(),
                                            new Date().toString(), d16));
        pool.add(new EventDescription("GoogleDocs", "true", "no", "DocUpdated",
                                            "utntest9@gmail.com", u1u7u8u9Group, new ArrayList(),
                                            new Date().toString(), d16));
        pool.add(new EventDescription("GroupCalendar", "true", "no", "MeetingConfirmation",
                                            "utntest4@gmail.com", new ArrayList(), project1Tag,
                                            new Date().toString(), riunione));

        pool.add(new EventDescription("GroupCalendar", "true", "no", "MeetingConfirmation",
                                            "utntest3@gmail.com", new ArrayList(), homeTag,
                                            new Date().toString(), saggio));

        pool.add(new EventDescription("GroupCalendar", "true", "no", "MeetingConfirmation",
                                            "utntest12@gmail.com", new ArrayList(), englishTag,
                                            new Date().toString(), lezioneInglese));
        return pool;
    }

} // end class
