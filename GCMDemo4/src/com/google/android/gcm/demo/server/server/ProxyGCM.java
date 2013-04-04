package com.google.android.gcm.demo.server.server;

import java.io.IOException;
import java.io.PrintWriter;
import java.util.List;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.json.JSONArray;
import org.json.JSONObject;
import org.json.JSONTokener;

public class ProxyGCM extends BaseServlet {

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;

	private static final String USER = "user";

	static final String ATTRIBUTE_STATUS = "status";

	protected void doGet(HttpServletRequest req, HttpServletResponse resp)
			throws IOException, ServletException {

		String utente = getParameter(req, USER);

		resp.setContentType("text/html");
		PrintWriter out = resp.getWriter();
		
		PrintWriter pw = resp.getWriter();


		out.print("<html><body>");
		out.print("<head>");
		out.print("  <title>GCM PROXY</title>");
		out.print("  <link rel='icon' href='favicon.png'/>");
		out.print("</head>");

		String status = (String) req.getAttribute(ATTRIBUTE_STATUS);
		if (status != null) {
			out.print(status);
		}

		List<String> dispositivi = Datastore.getRegIdFromEmail(utente);

		JSONObject jidUtente = new JSONObject();

		if (dispositivi.isEmpty()) {
			out.print("<h2>utente " + utente + " non esiste</h2>");
			try {
				jidUtente.put("redId","errore non esiste utente ");
				out.print(jidUtente);
				out.flush();
				out.print("</body></html>");

			} catch (Exception e) {
				System.err.println("Error while querying user");
				System.err.println(e);
			}
			
			finally {
				out.close();
			}
		} else {

			for (String d : dispositivi) {
				out.println("GcmId utente " + utente + d.toString());

				try {
					jidUtente.put("redId",d);
					out.print(jidUtente);
					out.flush();
					out.print("</body></html>");

				} catch (Exception e) {
					System.err.println("Error while querying user");
					System.err.println(e);
				}finally {
					out.close();
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
