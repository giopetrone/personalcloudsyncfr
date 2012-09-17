package dida.spikes3.server;

import java.io.IOException;
import java.io.InputStream;
import java.io.PrintWriter;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.json.JSONArray;
import org.json.JSONObject;
import org.json.JSONTokener;

public class ProxyServlet extends HttpServlet {

	private static final long serialVersionUID = -6455653509373554816L;

	@Override
	protected void doGet(HttpServletRequest req, HttpServletResponse resp)
			throws ServletException, IOException {
		InputStream is = req.getInputStream();
		PrintWriter pw = resp.getWriter();
		pw.print("ciao da anna");
		pw.flush();
		pw.close();
	}

	@Override
	protected void doPost(HttpServletRequest req, HttpServletResponse resp)
			throws ServletException, IOException {

		InputStream is = req.getInputStream();
		PrintWriter pw = resp.getWriter();
		try {
			
			JSONTokener jt = new JSONTokener(is);
			JSONObject jo = new JSONObject(jt);
			JSONObject rj = new JSONObject();
			// temp anangio
		 
			String request = jo.getString("request");

			if (request.equals("firstTest"))

				rj.put("status", "OK");
			else {
				rj.put("status", "ERROR");

			}
			  
			//JSONObject object = (JSONObject) new JSONTokener(json).nextValue();
			//String query = object.getString("food");
		//	JSONArray locations = object.getJSONArray("locations");
 
			pw.print(rj);
			pw.flush();

		} catch (Exception e) {
			System.err.println("Error while processing post request.");
			System.err.println(e);
		} finally {
			pw.close();
		}

	}

	private void createResult(JSONObject jo, PrintWriter pw) {
		try {
			// String email = jo.getString("userEmail");

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
