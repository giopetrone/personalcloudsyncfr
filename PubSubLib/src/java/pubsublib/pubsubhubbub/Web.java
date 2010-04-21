/*
 *
 *@author royans K tharakan | http://royans.net/ | rkt@pobox.com
 * Released under Apache License 2.0
 *
 */

package pubsublib.pubsubhubbub;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Enumeration;
import java.util.Iterator;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpServletRequest;
import org.mortbay.jetty.Handler;
import org.mortbay.jetty.Request;
import org.mortbay.jetty.Server;
import org.mortbay.jetty.handler.AbstractHandler;

public class Web extends Thread {

	static Web webserver = null;
	int port = 80;
	Server server;

	ArrayList<String> approvedActions = new ArrayList<String>();

	public Web(int port) throws Exception {
		this.port = port;
		this.setup();
	}

	public Web getInstance() {
		if (webserver == null) {
			return webserver;
		}
		return null;
	}

	public void addAction(String hubmode, String hubtopic, String hubverify) {
		String action=hubmode + ":" + hubtopic + ":" + hubverify;
		System.out.println("Adding action :"+action);
		approvedActions.add(action);
	}

	public void setup() throws Exception {
		Handler handler = new AbstractHandler() {
			public void handle(String target, HttpServletRequest request,
					HttpServletResponse response, int dispatch)
					throws IOException, ServletException {

				String hubmode = "";
				String hubtopic = "";
				String hubchallenge = "";
				String hublease = "";
				String hubverify = "";

				// if its a subscriber request handle here
				// if its a hub verification request handle here
				System.out.println("- Web request " + request.getRequestURI());
				Enumeration<String> ii=request.getParameterNames();

				response.setContentType("text/html");

				if (request.getParameter("hub.mode") != null) {
					hubmode = request.getParameter("hub.mode");
				}
				if (request.getParameter("hub.topic") != null) {
					hubtopic = request.getParameter("hub.topic");
				}
				if (request.getParameter("hub.challenge") != null) {
					hubchallenge = request.getParameter("hub.challenge");
				}
				if (request.getParameter("hub.lease") != null) {
					hublease = request.getParameter("hub.lease");
				}
				if (request.getParameter("hub.verify") != null) {
					hubverify = request.getParameter("hub.verify");
				}
				

				String action_found = "";
				if (((hubmode.equals("subscribe")) || (hubmode.equals("unsubscribe")))
						&& (hubmode.length() > 0)) {
					Iterator<String> i = approvedActions.iterator();
					while (i.hasNext()) {
						String action = i.next();
						if (action.startsWith(hubmode + ":" + hubtopic + ":"
								+ hubverify)) {
							response.setStatus(HttpServletResponse.SC_OK);
							response.getWriter().println(hubchallenge);
							action_found = action;							
						}
					}
					if (action_found != "") {
						approvedActions.remove(action_found);
					}
				}

				if (action_found == "") {
					response.setStatus(HttpServletResponse.SC_NOT_FOUND);
				}
				((Request) request).setHandled(true);
			}
		};
		server = new Server(port);
		server.setHandler(handler);
		server.start();
	}

	public void run() {

	}

}
