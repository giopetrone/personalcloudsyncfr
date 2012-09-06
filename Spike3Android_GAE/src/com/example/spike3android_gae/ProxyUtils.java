package com.example.spike3android_gae;
import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.entity.StringEntity;
import org.apache.http.impl.client.DefaultHttpClient;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import org.json.JSONTokener;

import java.io.InputStream;

public class ProxyUtils {

    private static HttpClient client;
    private static HttpPost post;
    //private static final String PROXY_SERVLET_URL = "http://tableplusplus.appspot.com/tableplus/proxy";
    private static final String PROXY_SERVLET_URL ="http://127.0.0.1:8888/spike2_gwtgaeapp/proxyServlet";

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

    //create the JSON Object for different request and then make connection with server and put response into another JSON Object (send at the activity)
    public static JSONObject proxyCall(String operation, Object param)
                    throws Exception {
            JSONObject jsRequest = new JSONObject();
            if (operation.equals("firstTest")) {
                    jsRequest.put("request", "firstTest");
                    jsRequest.put("parameter", param);
            } else
                    System.out.println("ProxyCall, operazione non riconosciuta");
            
            HttpEntity entity = new StringEntity(jsRequest.toString());
            post.setEntity(entity);
            HttpResponse response = client.execute(post);
            entity = response.getEntity();
            InputStream is = entity.getContent();
            JSONTokener jt;
            jt = new JSONTokener(is);
            JSONObject jsResp = new JSONObject(jt);
            // use response so i can do more request
            if (response.getEntity() != null) {
                    response.getEntity().consumeContent();
            }
            is.close();
            return jsResp;
    }
}
    
