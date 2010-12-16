/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package jav;

import com.sun.syndication.feed.synd.SyndContent;
import com.sun.syndication.feed.synd.SyndContentImpl;
import com.sun.syndication.feed.synd.SyndEntry;
import com.sun.syndication.feed.synd.SyndEntryImpl;
import com.sun.syndication.feed.synd.SyndFeed;
import com.sun.syndication.feed.synd.SyndFeedImpl;
import com.sun.syndication.feed.synd.SyndLinkImpl;
import com.sun.syndication.io.SyndFeedInput;
import com.sun.syndication.io.SyndFeedOutput;
import com.sun.syndication.io.XmlReader;
import java.io.File;
import java.io.FileWriter;
import java.io.InputStreamReader;
import java.io.StringReader;
import java.io.Writer;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;
import javax.servlet.ServletInputStream;
import pubsublib.event.AtomEvent;

/**
 *
 * @author marino
 */
public class FeedUtil {

    public static String SubFeedName(String flowName) {

        return "http://taskmanagerunito.xoom.it/Flow/"+flowName+".xml";
    }

     public static String RemoteFeedName(String flowName) {
        return "/webspace/httpdocs" + "/Flow/" + flowName + ".xml";
    }

    public static String FileFeedName(String flowName) {
        return "/var/www/html" + "/Flow/" + flowName + ".xml";
    //    return "/var/www" + "/Flow/" + flowName + ".xml";
    //    return "http://taskmgrunito.x10.mx/" +flowName +".xml";
    }

    public static boolean FeedWriteOk(String flowName) {
        boolean ret = true;
        try {
            String fileName = FileFeedName(flowName);
            final SyndFeedInput input = new SyndFeedInput();
            File f = new File(fileName);
            SyndFeed feed = input.build(new XmlReader(f));
        } catch (Exception ex) {
            //  ex.printStackTrace();
            ret = false;
        }
        return ret;
    }

    static boolean CreateFeedFile(String flowName) {
        DateFormat DATE_PARSER = new SimpleDateFormat("yyyy-MM-dd");

        SyndFeed feed = new SyndFeedImpl();
        List entries = new ArrayList();
        String fileName = FileFeedName(flowName);
        File f = new File(fileName);
        if (f.exists()) {
            return false;
        }
        boolean ok = false;

        try {
            feed.setFeedType("atom_1.0");
            feed.setTitle("Feed for flow: " + flowName + " (created with ROME)");
            feed.setDescription("This feed has been created using ROME (Java syndication utilities");

            List links = new ArrayList();
            SyndLinkImpl link = new SyndLinkImpl();
            link.setHref("http://rome.dev.java.net");
            link.setRel("alternate");
            links.add(link);
            link = new SyndLinkImpl();
            // HUB PER VERSIONE LOCALE
            String  hub = SaveServlet.getTypeNotification();
            if(hub == null || hub.equals("") || hub.equalsIgnoreCase("local")) link.setHref("http://localhost:8080");
            else link.setHref("http://pubsubhubbub.appspot.com");
            link.setRel("hub");
            links.add(link);
            feed.setLinks(links);
            // <link rel="hub" href="http://pubsubhubbub.appspot.com"/>
            Writer writer = new FileWriter(fileName);
            SyndFeedOutput output = new SyndFeedOutput();
            output.output(feed, writer);
            writer.close();
            System.err.println("The feed has been written to the file [" + fileName + "]");
            ok = true;
        } catch (Exception ex) {
            ex.printStackTrace();
            System.out.println("ERROR: " + ex.getMessage());
        }
        return ok;
    }



    static boolean CreateFeedFile(String flowName,String type) {
        DateFormat DATE_PARSER = new SimpleDateFormat("yyyy-MM-dd");

        SyndFeed feed = new SyndFeedImpl();
        List entries = new ArrayList();
        String fileName = FileFeedName(flowName);
        File f = new File(fileName);
        if (f.exists()) {
            return false;
        }
        boolean ok = false;

        try {
            feed.setFeedType("atom_1.0");
            feed.setTitle("Feed for flow: " + flowName + " (created with ROME)");
            feed.setDescription("This feed has been created using ROME (Java syndication utilities");

            List links = new ArrayList();
            SyndLinkImpl link = new SyndLinkImpl();
            link.setHref("http://rome.dev.java.net");
            link.setRel("alternate");
            links.add(link);
            link = new SyndLinkImpl();
            // HUB PER VERSIONE LOCALE
            if(type.equals("local"))  link.setHref("http://localhost:8080");
            else if(type.equals("remote")) link.setHref("http://pubsubhubbub.appspot.com");
            else link.setHref(type);
            link.setRel("hub");
            links.add(link);
            feed.setLinks(links);
            // <link rel="hub" href="http://pubsubhubbub.appspot.com"/>
            Writer writer = new FileWriter(fileName);
            SyndFeedOutput output = new SyndFeedOutput();
            output.output(feed, writer);
            writer.close();
            System.err.println("The feed has been written to the file [" + fileName + "]");
            ok = true;
        } catch (Exception ex) {
            ex.printStackTrace();
            System.out.println("ERROR: " + ex.getMessage());
        }
        return ok;
    }

    public static void main(String[] args) {
        try {
            System.out.println("esiste? " + FeedWriteOk("ccc.txt"));
            CreateFeedFile("ccc.txt");
            System.out.println("esiste? " + FeedWriteOk("ccc.txt"));
        } catch (Exception ex) {
            ex.printStackTrace();
        }
    }

    static void addLink(SyndEntry entry, String href) {
        List links = entry.getLinks();
        if (links == null) {
            links = new ArrayList();
            entry.setLinks(links);
        }
        SyndLinkImpl link = new SyndLinkImpl();
        link.setHref(href);
        link.setRel("alternate");
        links.add(link);
    }

    static boolean addEntry(String editLink, String flowName, AtomEvent event) {
        if (!FeedWriteOk(flowName)) {  // crea feed se non esiste
            CreateFeedFile(flowName);
        }
        SyndFeed feed = new SyndFeedImpl();
        try {
            String fileName = FileFeedName(flowName);
            final SyndFeedInput input = new SyndFeedInput();
            File f = new File(fileName);
            feed = input.build(new XmlReader(f));
            List entries = feed.getEntries();
            SyndEntry entry;
            SyndContent description;
            entry = new SyndEntryImpl();
            entry.setTitle("Flow (specificare meglio)");
            // set links to google documents; maybe could be link
            // taskmanager with http://localhost:8081?Flow=filename.txt
         //     addLink(entry, editLink);
            addLink(entry, "http://localhost:8080/TaskMgr/index.jsp?Flow=" + flowName);
         //     addLink(entry, "http://localhost:8081/index.jsp?Flow=" + flowName);
            //  entry.setPublishedDate(DATE_PARSER.parse("2009-07-" + i));
            entry.setPublishedDate(Calendar.getInstance().getTime());
           setDescription(entry, event, "text/plain");
          //    setDescription(entry, event, "text/html");
            entries.add(entry);
            feed.setEntries(entries);
            Writer writer = new FileWriter(fileName);
            SyndFeedOutput output = new SyndFeedOutput();
            output.output(feed, writer);
            writer.close();
        } catch (Exception e) {
            e.printStackTrace();
            return false;
        }
        return true;
    }


    static boolean addEntries(String editLink, String flowName,List<AtomEvent> event) {
        if (!FeedWriteOk(flowName)) {  // crea feed se non esiste
            CreateFeedFile(flowName);
        }
        SyndFeed feed = new SyndFeedImpl();
        try {
            String fileName = FileFeedName(flowName);
            final SyndFeedInput input = new SyndFeedInput();
            File f = new File(fileName);
            feed = input.build(new XmlReader(f));
            List entries = feed.getEntries();
            for(int i=0;i<event.size();i++)
            {

                SyndEntry entry;
                    SyndContent description;
                    AtomEvent singlevent = event.get(i);
                    entry = new SyndEntryImpl();
                    entry.setTitle("Flow (specificare meglio)");
                    entry.setUri("http://localhost:8080/TaskMgr/index.jsp?Flow=" + flowName+i);
       //                 entry.setUri("http://localhost:8081/index.jsp?Flow=" + flowName+i);
                    // set links to google documents; maybe could be link
                    // taskmanager with http://localhost:8081?Flow=filename.txt
                 //     addLink(entry, editLink);
                    addLink(entry, "http://localhost:8080/TaskMgr/index.jsp?Flow=" + flowName);
            //        addLink(entry, "http://localhost:8081/index.jsp?Flow=" + flowName);
                    //  entry.setPublishedDate(DATE_PARSER.parse("2009-07-" + i));
                    entry.setPublishedDate(Calendar.getInstance().getTime());
                   setDescription(entry, singlevent, "text/plain");
                  //    setDescription(entry, event, "text/html");
                    entries.add(entry);
            }

            feed.setEntries(entries);
            Writer writer = new FileWriter(fileName);
            SyndFeedOutput output = new SyndFeedOutput();
            output.output(feed, writer);
            writer.close();
        } catch (Exception e) {
            e.printStackTrace();
            return false;
        }
        return true;
    }


    static void setDescription(SyndEntry entry, AtomEvent event, String style) {
        SyndContent description = new SyndContentImpl();
        description.setType(style);
        if (style.equals("text/plain")) {
            description.setValue(event.toXml());
            entry.setDescription(description);
        } else if (style.equals("text/html")) {
            List cont = new ArrayList();
        //    String content = "<H2>the event</H2> " +event.toXml() + "</P>";
            String content = "<H2>here is the event</H2> " +event.toHtml(false) + "</P>";
            description.setValue(content);
            entry.setDescription(description);
            description = new SyndContentImpl();
            description.setType("text/plain");
            content = event.toXml();
            description.setValue(content);
            cont.add(description);
            entry.setContents(cont);
        }

    }

    public static List<AtomEvent> createAtom(String s) {
        SyndFeedInput input = new SyndFeedInput();
        List<AtomEvent> retEvent = new ArrayList();
        try {
            SyndFeed feed = input.build(new StringReader(s));
            List<SyndEntry> entries = feed.getEntries();
            for (SyndEntry entry : entries) {
                SyndContent description = entry.getDescription();
                String value = description.getValue();
                retEvent.add(AtomEvent.fromXml(value));
            }
        } catch (Exception ex) {
            ex.printStackTrace();
        }
        return retEvent;
    }

    public static AtomEvent createAtomOld(ServletInputStream inStream) {
        SyndFeedInput input = new SyndFeedInput();
        AtomEvent retEvent = null;
        try {
            SyndFeed feed = input.build(new InputStreamReader(inStream));
            List<SyndEntry> entries = feed.getEntries();
            SyndEntry entry = entries.get(0); // per ora 1 sola entry nuova ad ogni callback
            SyndContent description = entry.getDescription();
            String value = description.getValue();
            retEvent = AtomEvent.fromXml(value);
            if (retEvent != null) {
                System.err.println(" utente evento: " + retEvent.getUser());
            }
        } catch (Exception ex) {
            ex.printStackTrace();
        }
        return retEvent;
    }
}

/*

SyndEntry entry;
SyndContent description;
entry = new SyndEntryImpl();
entry.setTitle("ROME v1.0");
entry.setLink("http://wiki.java.net/bin/view/Javawsxml/Rome01");
entry.setPublishedDate(DATE_PARSER.parse("2004-06-08"));
description = new SyndContentImpl();
description.setType("text/plain");
description.setValue("Initial release of ROME");
entry.setDescription(description);
entries.add(entry);


feed.setEntries(entries);*/