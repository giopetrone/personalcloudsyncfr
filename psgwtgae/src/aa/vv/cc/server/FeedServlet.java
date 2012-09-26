package aa.vv.cc.server;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.Reader;
import java.io.StringWriter;
import java.io.Writer;
import java.util.Enumeration;

import javax.servlet.http.*;

import org.apache.http.HeaderElement;
import org.apache.http.HttpException;

import aa.vv.cc.client.Util;

@SuppressWarnings("serial")
public class FeedServlet extends HttpServlet {
	public void doGet(HttpServletRequest req, HttpServletResponse resp)
			throws IOException {
		 Enumeration e = req.getHeaderNames();
         while (e.hasMoreElements()) {
             String headers = (String) e.nextElement();
             if (headers != null) {
                 System.err.println(headers + " " + req.getHeader(headers));
             }
         }
         System.err.println("\n");
		
		  String entityTag = "";
		    String lastModified = "";
		    String etag = "abcde";
		 resp.setHeader("If-None-Match", entityTag  );
	        resp.setHeader("If-Modified-Since", 
	                               lastModified  );
	        resp.setHeader("ETag", etag);
		resp.setContentType("application/atom+xml");
		resp.getWriter().println(
				new Util().fromDataStoreToFeed("pippo").toString());
	}
	
}
