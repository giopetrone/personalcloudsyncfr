package pubsublib.test;

import java.io.IOException;
import pubsublib.pubsubhubbub.Discovery;
import pubsublib.pubsubhubbub.Publisher;
import pubsublib.pubsubhubbub.Subscriber;
import pubsublib.pubsubhubbub.Web;

public class Test {

    public static void testPublisherOrig() throws Exception {
        Publisher publisher = new Publisher();
        String hub = "http://pubsubhubbub.appspot.com/publish";
        int status = publisher.execute(hub,
                "http://www.royans.net/rant/feed/");
        System.out.println("Return status : " + status);
        status = publisher.execute(hub,
                "http://www.royans.net/rant/feed/");

        status = publisher.execute(hub,
                "http://www.royans.net/rant/feed/");

        System.out.println("Return status : " + status);
    }

    public static void testPublisher() throws Exception {
        Publisher publisher = new Publisher();
        String hub = "http://pubsubhubbub.appspot.com/publish";
        int status = publisher.execute(hub,
                "http://www.royans.net/rant/feed/");
        System.out.println("Return status : " + status);
        status = publisher.execute(hub,
                "http://www.royans.net/rant/feed/");

        status = publisher.execute(hub,
                "http://www.royans.net/rant/feed/");

        System.out.println("Return status : " + status);
    }

    public static void testSubscriber() throws Exception {
        Web webserver = new Web(8080);
        webserver.start();
        Subscriber subscriber = new Subscriber(webserver);
        Thread.sleep(30000);
        int result = subscriber.subscribe("http://www.royans.net/rant/feed/", "http://www.webtrace.info:8080/", "rkt_at_pobox.com_is_testing", "300");
        System.out.println("Subscribtion status :" + result);
    }

    public static void main(String[] args) {
        try {
            /*
            testPublisher();
            testSubscriber();
            Discovery discovery=new Discovery();
            System.out.println(discovery.getHub("http://www.royans.net/rant/feed/"));
            System.out.println(discovery.getContents("http://www.royans.net/rant/feed/"));

             */

            testSubscriber();
            for (int i = 0; i < 5; i++) {
                testPublisher();
                Thread.sleep(10000);
            }
            Discovery discovery = new Discovery();
            System.out.println(discovery.getHub("http://www.royans.net/rant/feed/"));
            System.out.println(discovery.getContents("http://www.royans.net/rant/feed/"));
            System.out.println(discovery.getContents("http://www.royans.net/rant/feed/"));

        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
