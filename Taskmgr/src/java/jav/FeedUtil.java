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
import java.io.Writer;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;
import pubsublib.event.AtomEvent;

/**
 *
 * @author marino
 */
public class FeedUtil {

    public static String SubFeedName(String flowName) {
        return "http://localhost" + "/Flow/" + flowName + ".xml";
    }

    public static String FileFeedName(String flowName) {
        return "/var/www" + "/Flow/" + flowName + ".xml";
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
            link.setHref("http://localhost:8080");
            link.setRel("hub");
            links.add(link);
            feed.setLinks(links);

            // <link rel="hub" href="http://pubsubhubbub.appspot.com"/>

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


            feed.setEntries(entries);

            Writer writer = new FileWriter(fileName);
            SyndFeedOutput output = new SyndFeedOutput();
            output.output(feed, writer);
            writer.close();

            System.out.println("The feed has been written to the file [" + fileName + "]");

            ok = true;
        } catch (Exception ex) {
            ex.printStackTrace();
            System.out.println("ERROR: " + ex.getMessage());
        }


        if (!ok) {
            System.out.println();
            System.out.println("FeedWriter creates a RSS/Atom feed and writes it to a file.");
            System.out.println("The first parameter must be the syndication format for the feed");
            System.out.println("  (rss_0.90, rss_0.91, rss_0.92, rss_0.93, rss_0.94, rss_1.0 rss_2.0 or atom_0.3)");
            System.out.println("The second parameter must be the file name for the feed");
            System.out.println();
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

    static boolean addEntry(String flowName, AtomEvent event) {
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
            return false;
        }
        return true;
    }
}
