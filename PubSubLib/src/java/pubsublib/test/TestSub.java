/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package pubsublib.test;

import pubsublib.pubsubhubbub.Discovery;
import pubsublib.pubsubhubbub.Subscriber;

/**
 *
 * @author marino
 */
public class TestSub {

    public void testSubscriber(String feed, String callback, String token) throws Exception {
        Subscriber subscriber = new Subscriber(null);
        Thread.sleep(1000);
        if (feed.equals("")) {
            feed = "http://localhost/Atomi/marinofeed.xml";
        }
        if (callback.equals("")) {
            callback = "http://localhost:8081/PubSubLib/callback";
        }
        if (token.equals("")) {
            token = "pippo";
        }

        // ricordare che il deployment della servlet e' sotto ......subscribe/pippo

        int result = subscriber.subscribe(feed, callback, token, "300");
        System.out.println("Subscribtion status :" + result);
    }

    public static void main(String[] args) {
        try {
            new TestSub().testSubscriber("", "", "");
            Discovery discovery = new Discovery();
            System.out.println("in test subscriber trovo hub: " + discovery.getHub("http://localhost/Atomi/marinofeed.xml"));
            //  System.out.println(discovery.getContents("http://localhost/Atomi/marinofeed.xml"));
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
