package dida.spikes3.server;

import java.io.IOException;
import java.io.InputStream;
import java.io.PrintWriter;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.json.JSONObject;
import org.json.JSONTokener;

public class ProxyServlet extends HttpServlet {

	 private static final long serialVersionUID = -6455653509373554816L;
	 
	 @Override
     protected void doPost(HttpServletRequest req, HttpServletResponse resp)
                     throws ServletException, IOException {

             InputStream is = req.getInputStream();
             try {
                     JSONTokener jt = new JSONTokener(is);
                     JSONObject jo = new JSONObject(jt);

                     String request = jo.getString("request");
                     PrintWriter pw = resp.getWriter();

                     if (request.equals("firstTest"))
                             createResult(jo, pw);
                     else {
                             JSONObject rj = new JSONObject();
                             rj.put("status", "ERROR");
                             rj.put("error", "Request unkown.");
                             pw.print(rj);
                             pw.flush();
                             pw.close();
                     }

             } catch (Exception e) {
                     System.err.println("Error while processing post request.");
                     System.err.println(e);
             }

	 }
	 
     private void createResult(JSONObject jo, PrintWriter pw) {
         try {
                 //String email = jo.getString("userEmail");
                 
                 JSONObject rj = new JSONObject();
                 
                 rj.put("status", "OK");

         } catch (Exception e) {
                 System.err.println("Error while querying user");
                 System.err.println(e);
         } finally {
                 pw.close();
         }
 }

}
