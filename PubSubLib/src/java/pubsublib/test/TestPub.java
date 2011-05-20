/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package pubsublib.test;

import pubsublib.pubsubhubbub.Discovery;
import pubsublib.pubsubhubbub.Publisher;

/**
 *
 * @author marino
 */
public class TestPub {

    public  void testPublisher(String hub, String feed) throws Exception {
        Publisher publisher = new Publisher();
        if (hub.equals("")) {
            hub = "http://localhost:9090";  // era 8080
        }
        if (feed.equals("")) {
            feed = "http://localhost/Atomi/marinofeed.xml";
        }
        int status = publisher.execute(hub, feed);
        System.out.println("Return status : " + status);
    }

    public static void main(String[] args) {
        try {
            new TestPub().testPublisher("","");
   //       new TestPub().testPublisher("http://pubsubhubbub.appspot.com","http://localhost/Atomi/marinofeed.xml");
           Discovery discovery = new Discovery();
            System.out.println("in test publisher trovo hub: " + discovery.getHub("http://localhost/Atomi/marinofeed.xml"));
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public static void mainPiemonte(String[] args) {
        try {
            new TestPub().testPublisher("http://www.piemonte.di.unito.it/Pubsubhub","http://www.piemonte.di.unito.it/Atomi/marinofeed.xml");
   //       new TestPub().testPublisher("http://pubsubhubbub.appspot.com","http://localhost/Atomi/marinofeed.xml");
           Discovery discovery = new Discovery();
            System.out.println("in test publisher trovo hub: " + discovery.getHub("http://www.piemonte.di.unito.it/Atomi/marinofeed.xml"));
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

     public  void testPublisherLocalhost(String hub, String feed) throws Exception {
        Publisher publisher = new Publisher();
        if (hub.equals("")) {
            hub = "http://localhost:9090";
        }
        if (feed.equals("")) {
            feed = "http://localhost/Atomi/marinofeed.xml";
        }
        int status = publisher.execute(hub, feed);
        System.out.println("Return status : " + status);
    }
}