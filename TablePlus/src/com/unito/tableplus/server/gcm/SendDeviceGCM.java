package com.unito.tableplus.server.gcm;

import static com.google.appengine.api.taskqueue.TaskOptions.Builder.withUrl;

import java.io.IOException;
import java.io.PrintWriter;
import java.util.ArrayList;
import java.util.List;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.json.JSONObject;

import com.google.appengine.api.taskqueue.Queue;
import com.google.appengine.api.taskqueue.QueueFactory;
import com.google.appengine.api.taskqueue.TaskOptions;
import com.google.appengine.api.taskqueue.TaskOptions.Method;

/**
 * This class send notification  to Android device using email's email
 * 
 * @param user
 *            the email of user
 */
public class SendDeviceGCM extends BaseServlet {

	private static final long serialVersionUID = 1L;

	private static final String USER = "user";

	static final String ATTRIBUTE_STATUS = "status";

	protected void doGet(HttpServletRequest req, HttpServletResponse resp)
			throws IOException, ServletException {
		
		System.out.println("SONO ARRIVATO alla servlet SendDeviceGCM");


		String utente = getParameter(req, USER);
		List<String> dispositivi = Datastore.getRegIdFromEmail(utente);

		Queue queue = QueueFactory.getDefaultQueue();

		if (dispositivi.isEmpty()) {
			System.out.println("Non ci sono Id registrati per l'utente "
					+ utente);

		} else {

			if (dispositivi.size() == 1) {
				System.out.println("SONO ARRIVATO alla servlet SendDeviceGCM mando la notifica ad un unico device");

				queue.add(withUrl("/tableplus/send").param(	SendMessageServlet.PARAMETER_DEVICE, dispositivi.get(0)));

			} else {

				// send a multicast message using JSON
				// must split in chunks of 1000 devices (GCM limit)
				System.out.println("SONO ARRIVATO alla servlet SendDeviceGCM mando la notifica a multicasr message");

				int total = dispositivi.size();
				List<String> partialDevices = new ArrayList<String>(total);
				int counter = 0;
				int tasks = 0;
				for (String device : dispositivi) {
					counter++;
					partialDevices.add(device);
					int partialSize = partialDevices.size();
					if (partialSize == Datastore.MULTICAST_SIZE
							|| counter == total) {
						String multicastKey = Datastore
								.createMulticast(partialDevices);
						logger.fine("Queuing " + partialSize
								+ " devices on multicast " + multicastKey);
						// aggiunto /gcmdemo4
						TaskOptions taskOptions = TaskOptions.Builder
								.withUrl("/tableplus/send")
								.param(SendMessageServlet.PARAMETER_MULTICAST,
										multicastKey).method(Method.POST);
						queue.add(taskOptions);
						partialDevices.clear();
						tasks++;

					}

				}
			}
		}

	}

	@Override
	protected void doPost(HttpServletRequest req, HttpServletResponse resp)
			throws IOException, ServletException {
		doGet(req, resp);
	}

}
