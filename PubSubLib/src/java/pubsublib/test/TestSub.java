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

    public String testSubscriber(String feed, String callback, String token) {
        try
        {
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
            return "done";
        }catch(Exception ex)
        {
            System.err.println("ERRORE IN testSubscriber"+ex.getMessage());
            return "error";
        }
    }

    public static void main(String[] args) {
        try {
            new TestSub().testSubscriber("http://taskmanagerunito.xoom.it/Flow/today.txt.xml", "http://localhost:8081/NotifMgrG/NotifCallbackServlet", "");
            //Discovery discovery = new Discovery();
          //    new TestPub().testPublisher("http://pubsubhubbub.appspot.com/","http://taskmanagerunito.xoom.it/Flow/remote.txt.xml");
            //System.out.println("in test subscriber trovo hub: " + discovery.getHub("http://localhost/Atomi/marinofeed.xml"));
        //    System.out.println(discovery.getContents("http://taskmanagerunito.xoom.it/Flow/pubbl.txt.xml"));
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
