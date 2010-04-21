/*
 *
 *@author royans K tharakan | http://royans.net/ | rkt@pobox.com
 * Released under Apache License 2.0
 *
 */

package pubsublib.pubsubhubbub;

import java.net.URL;
import java.util.ArrayList;
import java.util.List;
import org.apache.http.HeaderElement;
import org.apache.http.HeaderElementIterator;
import org.apache.http.HttpResponse;
import org.apache.http.client.entity.UrlEncodedFormEntity;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.conn.ClientConnectionManager;
import org.apache.http.conn.ConnectionKeepAliveStrategy;
import org.apache.http.conn.params.ConnManagerParams;
import org.apache.http.conn.params.ConnPerRouteBean;
import org.apache.http.conn.scheme.PlainSocketFactory;
import org.apache.http.conn.scheme.Scheme;
import org.apache.http.conn.scheme.SchemeRegistry;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.impl.conn.tsccm.ThreadSafeClientConnManager;
import org.apache.http.message.BasicHeaderElementIterator;
import org.apache.http.message.BasicNameValuePair;
import org.apache.http.params.BasicHttpParams;
import org.apache.http.params.HttpParams;
import org.apache.http.protocol.HTTP;
import org.apache.http.protocol.HttpContext;
import org.apache.http.NameValuePair;

public class Publisher {

	DefaultHttpClient httpClient = null;

	public Publisher() {
		HttpParams params = new BasicHttpParams();
		ConnManagerParams.setMaxTotalConnections(params, 200);
		ConnPerRouteBean connPerRoute = new ConnPerRouteBean(20);
		connPerRoute.setDefaultMaxPerRoute(50);
		ConnManagerParams.setMaxConnectionsPerRoute(params, connPerRoute);

		SchemeRegistry schemeRegistry = new SchemeRegistry();
		schemeRegistry.register(new Scheme("http", PlainSocketFactory
				.getSocketFactory(), 80));
		ClientConnectionManager cm = new ThreadSafeClientConnManager(params,
				schemeRegistry);
		httpClient = new DefaultHttpClient(cm, params);

		httpClient.setKeepAliveStrategy(new ConnectionKeepAliveStrategy() {

			public long getKeepAliveDuration(HttpResponse response,
					HttpContext context) {
				HeaderElementIterator it = new BasicHeaderElementIterator(
						response.headerIterator(HTTP.CONN_KEEP_ALIVE));
				while (it.hasNext()) {
					HeaderElement he = it.nextElement();
					String param = he.getName();
					String value = he.getValue();
					if (value != null && param.equalsIgnoreCase("timeout")) {
						try {
							return Long.parseLong(value) * 1000;
						} catch (NumberFormatException ignore) {
						}
					}
				}
				// default keepalive is 60 seconds. This is higher than usual
				// since the number of hubs it should be talking to should be
				// small
				return 30 * 1000;
			}
		});
	}

	/*
	 * @throws IOException If an input or output exception occurred
	 * 
	 * @param The Hub address you want to publish it to
	 * 
	 * @param The topic_url you want to publish
	 * 
	 * @return HTTP Response code. 200 is ok. Anything else smells like trouble
	 */
	public int execute(String hub, String topic_url) throws Exception {

		if ((hub != null) && (topic_url != null)) {

			// URL should validate if the strings are really URLs. Will throw
			// Exception if it isn't
			@SuppressWarnings("unused")
			URL verifying_topic_url = new URL(topic_url);
			@SuppressWarnings("unused")
			URL hub_url = new URL(hub);

			HttpPost httppost = new HttpPost(hub);

			List<NameValuePair> nvps = new ArrayList<NameValuePair>();
			nvps.add(new BasicNameValuePair("hub.mode", "publish"));
			nvps.add(new BasicNameValuePair("hub.url", topic_url));
			httppost.setEntity(new UrlEncodedFormEntity(nvps, HTTP.UTF_8));

			httppost.setHeader("Content-type",
					"application/x-www-form-urlencoded");
			httppost.setHeader("User-agent", "flagthis.pubsubhubbub 0.2");

			GetThread thread = new GetThread(httpClient, httppost);
			thread.start();
			thread.join();

			return thread.httpresponse.getStatusLine().getStatusCode();
		}
		return 400;
	}
}
