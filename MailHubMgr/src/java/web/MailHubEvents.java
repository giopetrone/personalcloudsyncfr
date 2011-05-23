/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package web;

import com.sun.syndication.io.XmlReader;
import com.sun.syndication.feed.synd.SyndFeed;
import com.sun.syndication.io.SyndFeedInput;
import com.thoughtworks.xstream.XStream;
import java.io.File;
import maillib.HubMailMsg;
import pubsublib.test.FeedWriter;
import pubsublib.test.TestPub;
import pubsublib.test.TestSub;

/**
 *
 * @author giovanna
 */
public class MailHubEvents {
     private static boolean piemonte = false;

    public static void main(String[] args) {
        
         new TestSub().testSubscriber("http://localhost/Atomi/eventiRawMail.xml", "http://localhost:8080/HubEventsMgr/Callback", "");
        
        // TODO code application logic here
//        NewGmail gmail = new NewGmail();
//        ArrayList<HubMailMsg> msg = gmail.getMailMessages();
//        if (msg.size() > 0) {
//            HubMailMsg hm = msg.get(0);
//            // hm.getTextContent(msg[0]); 
//
//            String s = toXML(hm);
//            System.out.println("messaggio in XML \n " + s);
//
//        }

    }

    // public static String toXML(HubMailMsg hm) {
    public String toXML(HubMailMsg hm) {
        XStream xstream = new XStream();
        String s = xstream.toXML(hm);
        return s;
    }

  //   public static HubMailMsg fromXml(String s) {
    public  static HubMailMsg fromXml(String s) {
        XStream xstream = new XStream();
        try {
            Object ob = xstream.fromXML(s);
            if (ob.getClass() == maillib.HubMailMsg.class) {

                return (HubMailMsg) ob;
            }
        } catch (com.thoughtworks.xstream.io.StreamException ex) {
            System.err.println("xstream, getEvent, error in HubMailMsg content = " + s);
        }
        return null;
    }
    
   public void publishMailEvent(String content) {
       final SyndFeedInput input = new SyndFeedInput();
        try {
            File f = new File(getFileName());
            final SyndFeed feed = input.build(new XmlReader(f));
            new FeedWriter().addEntry(feed, content, f);
            System.out.println("addMailEvent : pubblico su hub aggiornamento");

            new TestPub().testPublisher("http://localhost:9090", "");

        } catch (Exception ex) {
            ex.printStackTrace();
        }
   }
   
    private static String getFileName() {
        return piemonte ? "/var/www/html/Atomi/eventiRawMail.xml" : "/var/www/Atomi/eventiRawMail.xml";
    }

    public void subscribeTo() {
        try {
            new TestSub().testSubscriber("http://localhost/Atomi/eventiRawMail.xml", "http://localhost:8080/HubEventsMgr/Callback", "");
          /*  for (int i =0; i < 100;i++) {
                Thread.sleep(1000);
                System.err.println("a");
            }*/
           // new TestSub().testSubscriber("http://www.piemonte.di.unito.it/Atomi/marinofeed.xml", "", "");
            //Discovery discovery = new Discovery();
          //    new TestPub().testPublisher("http://pubsubhubbub.appspot.com/","http://taskmanagerunito.xoom.it/Flow/remote.txt.xml");
            //System.out.println("in test subscriber trovo hub: " + discovery.getHub("http://localhost/Atomi/marinofeed.xml"));
        //    System.out.println(discovery.getContents("http://taskmanagerunito.xoom.it/Flow/pubbl.txt.xml"));
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
    
      
}
