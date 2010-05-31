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
            hub = "http://localhost:8080";
        }
        if (feed.equals("")) {
            feed = "http://localhost/Atomi/marinofeed.xml";
        }
        int status = publisher.execute(hub, feed);
        System.out.println("Return status : " + status);
    }

    public static void main(String[] args) {
        try {
            new TestPub().testPublisher("http://localhost:8080","http://localhost/Atomi/marinofeed.xml");
   //       new TestPub().testPublisher("http://pubsubhubbub.appspot.com","http://localhost/Atomi/marinofeed.xml");
           Discovery discovery = new Discovery();
            System.out.println("in test publisher trovo hub: " + discovery.getHub("http://localhost/Atomi/marinofeed.xml"));
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
    
}
