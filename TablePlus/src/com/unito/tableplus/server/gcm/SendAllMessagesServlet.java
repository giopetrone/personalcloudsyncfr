package com.unito.tableplus.server.gcm;

import static com.google.appengine.api.taskqueue.TaskOptions.Builder.withUrl;

import com.google.appengine.api.taskqueue.Queue;
import com.google.appengine.api.taskqueue.QueueFactory;
import com.google.appengine.api.taskqueue.TaskOptions;
import com.google.appengine.api.taskqueue.TaskOptions.Method;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

/**
 * Servlet that adds a new message to all registered devices.
 * <p>
 * This servlet is used just by the browser (i.e., not device).
 */
@SuppressWarnings("serial")
public class SendAllMessagesServlet extends BaseServlet {

  /**
   * Processes the request to add a new message.
   */
  @Override
  protected void doPost(HttpServletRequest req, HttpServletResponse resp)
      throws IOException, ServletException {
	List<String> devices = Datastore.getDevices();
    String status;
    if (devices.isEmpty()) {
      status = "Message ignored as there is no device registered!";
    } else {
    	
     // Queue queue = QueueFactory.getQueue("gcm");
      Queue queue = QueueFactory.getDefaultQueue();

    	
    	// NOTE: check below is for demonstration purposes; a real application
      // could always send a multicast, even for just one recipient
      if (devices.size() == 1) {
        // send a single message using plain post
        String device = devices.get(0);
        //aggiunto /gcmdemo4

        System.out.println("i devices sono " + device);
        queue.add(withUrl("/tableplus/send").param(
            SendMessageServlet.PARAMETER_DEVICE, device));
        System.out.println("Passato : queue.add(withUrl(/tableplus/send).");
        status = "Single message queued for registration id " + device;
      } else {
        // send a multicast message using JSON
        // must split in chunks of 1000 devices (GCM limit)
        int total = devices.size();
        List<String> partialDevices = new ArrayList<String>(total);
        int counter = 0;
        int tasks = 0;
        for (String device : devices) {
          counter++;
          partialDevices.add(device);
          int partialSize = partialDevices.size();
          if (partialSize == Datastore.MULTICAST_SIZE || counter == total) {
            String multicastKey = Datastore.createMulticast(partialDevices);
            logger.fine("Queuing " + partialSize + " devices on multicast " +
                multicastKey);
            //aggiunto /gcmdemo4
            TaskOptions taskOptions = TaskOptions.Builder
                .withUrl("/tableplus/send")
                .param(SendMessageServlet.PARAMETER_MULTICAST, multicastKey)
                .method(Method.POST);
            queue.add(taskOptions);
            partialDevices.clear();
            tasks++;
          }
        }
        status = "Queued tasks to send " + tasks + " multicast messages to " +
            total + " devices";
      }
    }
    req.setAttribute(HomeServlet.ATTRIBUTE_STATUS, status.toString());
    
 
   getServletContext().getRequestDispatcher("/tableplus/home").forward(req, resp);
  }

}
