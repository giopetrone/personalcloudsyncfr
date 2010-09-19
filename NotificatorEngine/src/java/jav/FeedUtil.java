/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package jav;

import com.sun.syndication.feed.synd.SyndContent;
import com.sun.syndication.feed.synd.SyndContentImpl;
import com.sun.syndication.feed.synd.SyndEntry;
import com.sun.syndication.feed.synd.SyndFeed;
import com.sun.syndication.io.SyndFeedInput;

import java.io.InputStreamReader;
import java.io.StringReader;
import java.util.ArrayList;
import java.util.List;
import javax.servlet.ServletInputStream;


/**
 *
 * @author marino
 */
public class FeedUtil {

    

   

   

   


    


   


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
