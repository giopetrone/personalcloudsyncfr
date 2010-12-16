/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package pubsublib.test;


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
 * It creates a feed and writes it to a file.
 * <p>
 *
 */
public class FeedWriter {

    private DateFormat DATE_PARSER = new SimpleDateFormat("yyyy-MM-dd");
    static String fileName = "/var/www/html/Atomi/marinofeed.xml";
    //static String fileName = "/var/www/Atomi/marinofeed.xml";
   // SyndFeed feed = new SyndFeedImpl();
   SyndFeed feed = new SyndFeedImpl();
    List entries = new ArrayList();

    public static void main(String[] args) {
        final SyndFeedInput input = new SyndFeedInput();
        try {
            File f = new File(fileName);
            final SyndFeed feed =
                    input.build(new XmlReader(f));
           new FeedWriter().addEntry(feed);
           System.out.println("pubblico su hub aggiornamento");

      new TestPub().testPublisher("http://localhost:8080","");

        } catch (Exception ex) {ex.printStackTrace();
        }
     //  new FeedWriter().doIt();
    }

    void addEntry(SyndFeed feed) {
        SyndLinkImpl s = new SyndLinkImpl();
        s.setHref("http://localhost:8080");
        s.setRel("hub");

        List links = feed.getLinks();
        for (int i=0; i< links.size();i++){
            System.err.println(links.get(i).toString());
        }
       links.add(s); feed.setLinks(links);
      //  List entries = feed.getEntries();
         SyndEntry entry;
        SyndContent description;
        entry = new SyndEntryImpl();
        entry.setTitle("ROME " + "12");
        entry.setLink("http://wiki.java.net/bin/view/Javawsxml/Rome03");
        //  entry.setPublishedDate(DATE_PARSER.parse("2009-07-" + i));
        entry.setPublishedDate(Calendar.getInstance().getTime());
        description = new SyndContentImpl();
        description.setType("text/xml");
        description.setValue( AtomEvent.creaPerProva().toXml());

     /*     description.setType("text/html");
        description.setValue("<p>More Bug fixes, mor API changes, some new features and some Unit testing</p>"
                + "<p>For details check the <a href=\"http://wiki.java.net/bin/view/Javawsxml/RomeChangesLog#RomeV03\">Changes Log</a></p>");
 */
      entry.setDescription(description);
        entries.add(entry);
        feed.setEntries(entries);
try{
        Writer writer = new FileWriter(fileName);
        SyndFeedOutput output = new SyndFeedOutput();
        output.output(feed, writer);
        writer.close();
         } catch (Exception e) {
            e.printStackTrace();
        }
    }


    private void appendAtom(int i) throws Exception {
        SyndEntry entry;
        SyndContent description;
        entry = new SyndEntryImpl();
        entry.setTitle("ROME " + (i + 3));
        entry.setLink("http://wiki.java.net/bin/view/Javawsxml/Rome03");
       // possibile che non ci sia??? entry.setId("http://wiki.java.net/bin/view/Javawsxml/Rome03");
        //  entry.setPublishedDate(DATE_PARSER.parse("2009-07-" + i));
        entry.setPublishedDate(Calendar.getInstance().getTime());
        description = new SyndContentImpl();
        description.setType("text/html");
        description.setValue("<p>More Bug fixes, mor API changes, some new features and some Unit testing</p>"
                + "<p>For details check the <a href=\"http://wiki.java.net/bin/view/Javawsxml/RomeChangesLog#RomeV03\">Changes Log</a></p>");
        entry.setDescription(description);
        entries.add(entry);
        feed.setEntries(entries);

        Writer writer = new FileWriter(fileName);
        SyndFeedOutput output = new SyndFeedOutput();
        output.output(feed, writer);
        writer.close();
    }
    private static String url =
            //  "http://localhost:8080/Roma/publishJSP.jsp";
            "http://localhost:8080";

    /*static void sendContent(String file) {
        try {
            HttpClient httpclient = new DefaultHttpClient();
            InputStreamEntity reqEntity = new InputStreamEntity(
                    new FileInputStream(file), -1);
            reqEntity.setContentType("binary/octet-stream");
            reqEntity.setChunked(true);
            // It may be more appropriate to use FileEntity class in this particular
            // instance but we are using a more generic InputStreamEntity to demonstrate
            // the capability to stream out data from any arbitrary source
            //
            // FileEntity entity = new FileEntity(file, "binary/octet-stream");
            HttpPost httppost = new HttpPost(url);
            httppost.setEntity(reqEntity);

            System.out.println("executing request " + httppost.getRequestLine());
            HttpResponse response = httpclient.execute(httppost);
            HttpEntity resEntity = response.getEntity();

            System.out.println("----------------------------------------");
            System.out.println(response.getStatusLine());
            if (resEntity != null) {
                System.out.println("Response content length: " + resEntity.getContentLength());
                System.out.println("Chunked?: " + resEntity.isChunked());
            }
            if (resEntity != null) {
                resEntity.consumeContent();
            }

            // When HttpClient instance is no longer needed,
            // shut down the connection manager to ensure
            // immediate deallocation of all system resources
            httpclient.getConnectionManager().shutdown();
        } catch (Exception e) {
            e.printStackTrace();
        }

    }*/

    void doIt() {
        boolean ok = false;

        try {
            //  String feedType = args[0];
            // String fileName = args[1];
            String feedType = "atom_1.0";



            feed.setFeedType(feedType);

            feed.setTitle("Sample Feed (created with ROME)");
            feed.setLink("http://rome.dev.java.net");
            feed.setDescription("This feed has been created using ROME (Java syndication utilities");

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

            entry = new SyndEntryImpl();
            entry.setTitle("ROME v2.0");
            entry.setLink("http://wiki.java.net/bin/view/Javawsxml/Rome02");
            entry.setPublishedDate(DATE_PARSER.parse("2004-06-16"));
            description = new SyndContentImpl();
            description.setType("text/plain");
            description.setValue("Bug fixes, minor API changes and some new features");
            entry.setDescription(description);
            entries.add(entry);

            entry = new SyndEntryImpl();
            entry.setTitle("ROME v3.0");
            entry.setLink("http://wiki.java.net/bin/view/Javawsxml/Rome03");
            entry.setPublishedDate(DATE_PARSER.parse("2004-07-27"));
            description = new SyndContentImpl();
            description.setType("text/html");
            description.setValue("<p>More Bug fixes, mor API changes, some new features and some Unit testing</p>"
                    + "<p>For details check the <a href=\"http://wiki.java.net/bin/view/Javawsxml/RomeChangesLog#RomeV03\">Changes Log</a></p>");
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
        cicla();
        //   sendContent(fileName);
    }

    void cicla() {
        for (int i = 0; i < 5; i++) {
            try {

                Thread.sleep(20 * 1000);
                appendAtom(i);
                System.out.println("scritta nuoo atom: " + i);
            } catch (Exception e) {
            }
        }
        System.out.println("\n DONE aggiorna atom ");
    }
}
