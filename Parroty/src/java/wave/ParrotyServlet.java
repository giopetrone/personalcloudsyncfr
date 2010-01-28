/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package wave;

import com.google.wave.api.AbstractRobotServlet;
import com.google.wave.api.Blip;
import com.google.wave.api.Event;
import com.google.wave.api.EventType;
import com.google.wave.api.RobotMessageBundle;
import com.google.wave.api.TextView;
import com.google.wave.api.Wavelet;
import java.io.IOException;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

/**
 *
 * @author giovanna
 */
public class ParrotyServlet extends AbstractRobotServlet {
   
   @Override
  public void processEvents(RobotMessageBundle bundle) {
    Wavelet wavelet = bundle.getWavelet();

    if (bundle.wasSelfAdded()) {
      Blip blip = wavelet.appendBlip();
      TextView textView = blip.getDocument();
      textView.append("I'm alive!");
    }

    for (Event e: bundle.getEvents()) {
      if (e.getType() == EventType.WAVELET_PARTICIPANTS_CHANGED) {
        Blip blip = wavelet.appendBlip();
        TextView textView = blip.getDocument();
        textView.append("Hi, everybody!");
      }
    }
  }


   

    /** 
     * Returns a short description of the servlet.
     * @return a String containing servlet description
     */
    @Override
    public String getServletInfo() {
        return "Short description";
    }// </editor-fold>

}
