/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package pubsublib.pubsubhubbub;

import java.io.IOException;
import java.net.URL;
import java.net.URLEncoder;
import org.apache.http.HttpResponse;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.impl.client.DefaultHttpClient;

public class Publisher1 {

	/*
	 * @throws IOException If an input or output exception occurred
	 *
	 * @param The Hub address you want to publish it to
	 *
	 * @param The topic_url you want to publish
	 *
	 * @return HTTP Response code. 200 is ok. Anything else smells like trouble
	 */
	public int publish(String hub, String topic_url) throws IOException {

		if ((hub != null) && (topic_url != null)) {

			// URL should validate if the strings are really URLs. Will throw Exception if it isn't
			@SuppressWarnings("unused")
			URL verifying_topic_url = new URL(topic_url);
			@SuppressWarnings("unused")
			URL hub_url = new URL(hub);

			HttpPost httppost = new HttpPost(hub + "?" + "hub.mode=publish&"
					+ "hub.url=" + URLEncoder.encode(topic_url, "UTF-8"));

			httppost.setHeader("User-agent", "flagthis.pubsubhubbub 0.2");

			DefaultHttpClient httpclient = new DefaultHttpClient();
			HttpResponse response = httpclient.execute(httppost);

			return response.getStatusLine().getStatusCode();
		}
		return 400;
	}

}
