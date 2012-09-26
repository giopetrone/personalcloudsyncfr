package aa.vv.cc.server.ps;

import java.io.IOException;
import java.net.InetAddress;

import com.example.su.Web;

//import PubSubHubbub.Web;
//import PubSubHubbub.Subscriber;

public class Test {

	private Web webserver;
	private static Subscriber sbcbr;
	private static String hostname = null;   
	private static Integer webserverPort = 8080;

    private void startServer(){
    	try {
    		webserver = new Web(webserverPort);
			
	//		sbcbr = new Subscriber(webserver);
			
			InetAddress addr = InetAddress.getLocalHost(); 
			hostname = addr.getHostName();
			hostname = "http://" + hostname + ":" + Integer.toString(webserverPort) + "/";
		   
		} catch (Exception e) {
			e.printStackTrace();
			System.out.println("WebServer can not start");
		}

    }
    private void findHost(){
    	try {
    		//webserver = new Web(webserverPort);
			
			//sbcbr = new Subscriber();//webserver);
			
			InetAddress addr = InetAddress.getLocalHost(); 
			hostname = addr.getHostName();   
			hostname = "http://" + hostname + ":" + Integer.toString(webserverPort) + "/";
			hostname = "http://" + hostname ;
			hostname = "http://appengine.google.com";
			hostname = "http://sgnmrnsubgwt.appspot.com";
			//System.err.println("hostname="+hostname);
		   
		} catch (Exception e) {
			e.printStackTrace();
			System.out.println("WebServer can not start");
		}

    }
    public String testSubscriber(String feed, String callback, String token) {
        try
        {
        	findHost();
            Subscriber subscriber = new Subscriber();//null);
            Thread.sleep(1000);
            if (feed.equals("")) {
                feed = "http://sgnmrnsub.appspot.com/feed";
            }
            if (callback.equals("")) {
                callback = "http://sgnmrnsubgwt.appspot.com/callback";
            }
            if (token.equals("")) {
                token = "pippo";
            }

            // ricordare che il deployment della servlet e' sotto ......subscribe/pippo

           // int result = subscriber.subscribe(feed, callback, token, "300");
            int result = subscriber.subscribe("http://pubsubhubbub.appspot.com", "http://sgnmrnsubgwt.appspot.com/feed", hostname, null, null);
            //System.out.println("Subscribtion status :" + result);
            return "Subscribtion status :" + result;
        }catch(Exception ex)
        {
            System.err.println("ERRORE IN testSubscriber"+ex.getMessage());
            return "error";
        }
    }


	public static void main(String[] args) {
		 try {
             new Test().testSubscriber("http://www.piemonte.di.unito.it/Atomi/marinofeed.xml", "http://aioeoio:8080/PubSubLib/callback", "");
           // new TestSub().testSubscriber("", "", "");
            for (int i =0; i < 1;i++) {
                Thread.sleep(1000);
                System.err.println(""+i);
            }
           // new TestSub().testSubscriber("http://www.piemonte.di.unito.it/Atomi/marinofeed.xml", "", "");
            //Discovery discovery = new Discovery();
          //    new TestPub().testPublisher("http://pubsubhubbub.appspot.com/","http://taskmanagerunito.xoom.it/Flow/remote.txt.xml");
            //System.out.println("in test subscriber trovo hub: " + discovery.getHub("http://localhost/Atomi/marinofeed.xml"));
        //    System.out.println(discovery.getContents("http://taskmanagerunito.xoom.it/Flow/pubbl.txt.xml"));
        } catch (Exception e) {
            e.printStackTrace();
        }
	}
	public static void mainOLD(String[] args) {
		try {
			
			   String hub = "http://myhub.example.com/endpoint";
			   String hub_topic = "http://publisher.example.com/topic.xml";
			   
			    new Test().startServer();

			   int statusCode = sbcbr.subscribe(hub, hub_topic, hostname, null, null);
			   
			   if (statusCode == 204){
				   System.out.println("the status code of the subscription is 204: the request was verified and that the subscription is active");
			   } else if (statusCode == 202){
				   System.out.println("the status code of the subscription is 202: the subscription has yet to be verified (i.e., the hub is using asynchronous verification)");
			   } else{
				   System.out.println("the status code of the subscription is:" + statusCode);   
			   }
			   
		
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
	
	  public  String testPublisher(String hub, String feed) throws Exception {
	        Publisher publisher = new Publisher();
	        if (hub.equals("")) {
	            hub = "http://localhost:9090";  // era 8080
	        }
	        if (feed.equals("")) {
	            feed = "http://localhost/Atomi/marinofeed.xml";
	        }
	        int status = publisher.execute(hub, feed);
	        return "Return status : " + status;
	    }
	  
   
}