package com.example.spike3android_gae;

import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.entity.StringEntity;
import org.apache.http.impl.client.DefaultHttpClient;

import it.unito.json.*;

import java.io.InputStream;
import android.util.Log;

public class ProxyUtils {

	private static HttpClient client;
	private static HttpPost post;
	//URL quando la Web App e' deployed su GAE
	// private static final String PROXY_SERVLET_URL =
	// "http://tableplusplus.appspot.com/tableplus/proxy";
	
	//URL quando la Web App e' deployed su GAE, ma la si esegue nel GAE in locale
	//private static final String PROXY_SERVLET_URL = "http://10.0.2.2:8888/spike2_gwtgaeapp/proxyServlet";
	private static final String PROXY_SERVLET_URL = "http://10.0.2.2:8888/gaeservletproxy";
	
	
	/**
	 * Method: first time only create object
	 */
	public static void init() {
		client = new DefaultHttpClient();
		post = new HttpPost(PROXY_SERVLET_URL);
	}

	public static HttpClient getHttpClient() {
		return client;
	}

	public static HttpPost getHttpPost() {
		return post;
	}

	// create the JSON Object for different request and then make connection
	// with server and put response into another JSON Object (send at the
	// activity)
	public static JSONObject proxyCall(String operation, Object param)
			throws Exception {

		JSONObject jsRequest = new JSONObject();
		Log.i("proxyCall ", "I");

	//	operation = "firstTest";
		if (operation.equals("firstTest")) {
			jsRequest.put("request", "firstTest");
			// jsRequest.put("parameter", param);
		} else
			System.out.println("ProxyCall, operazione non riconosciuta");
		// TMP
		Log.i("proxyCall ", "II");
	
		HttpEntity entity = new StringEntity(jsRequest.toString());

		post.setEntity(entity);

		if (client == null)
			Log.i("proxyCall ", "client NULL");

		HttpResponse response = client.execute(post);
		entity = response.getEntity();
		if (entity == null)
			Log.i("proxyCall", "null");
		else
			Log.i("proxyCall", entity.toString());
		InputStream is = entity.getContent();

		if (is == null)
			Log.i("proxyCall", "null");
		else
			Log.i("proxyCall", is.toString());
		JSONTokener jt;
		jt = new JSONTokener(is);
		JSONObject jsResp = new JSONObject(jt);
		// use response so i can do more request
		  if (response.getEntity() != null) {
		 response.getEntity().consumeContent();
		  }

		is.close();
		Log.i("proxyCall : jsResp ", jsResp.toString());

		return jsResp;

	}
}
