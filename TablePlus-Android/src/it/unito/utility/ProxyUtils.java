package it.unito.utility;

import it.unito.json.JSONArray;
import it.unito.json.JSONException;
import it.unito.json.JSONObject;
import it.unito.json.JSONTokener;
import it.unito.model.Group;
import it.unito.model.Message;

import java.io.InputStream;
import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;

import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.entity.StringEntity;
import org.apache.http.impl.client.DefaultHttpClient;


import android.util.Log;

public class ProxyUtils {
	private static HttpClient client;
	private static HttpPost post;
	private static final String PROXY_URL = "http://10.0.2.2:8888/tableplus/proxy";

	/**
	 * Method: da richiamare la prima volta per istanziare gli oggetti
	 */
	public static void init() {
		client = new DefaultHttpClient();
		post = new HttpPost(PROXY_URL);
	}

	public static HttpClient getHttpClient() {
		return client;
	}
	
	public static HttpPost getHttpPost() {
		return post;
	}
	

	public static JSONObject proxyCall(String operation, Object param)
			throws Exception {
		JSONObject jsRequest = new JSONObject();

		System.out.println("proxyCall" + operation);
		if (operation.equals("queryUser")) {
			jsRequest.put("request", "queryUser");
			jsRequest.put("userEmail", param);
		} else if (operation.equals("queryTable")) {
			jsRequest.put("request", "queryTable");
			jsRequest.put("tableKey", param);
			System.out.println("QUERY TABLES JSON OBJ: "+jsRequest);		
		} else if (operation.equals("queryTables")) {
			jsRequest.put("request", "queryTables");
			jsRequest.put("tablesKeyList", param);
		} else if (operation.equals("deleteMessage")) {
			jsRequest.put("request", "deleteMessage");
			jsRequest.put("messageKey", param);
		} else if (operation.equals("queryMessages")) {
			jsRequest.put("request", "queryMessages");
			jsRequest.put("tableKey", param);
		} else
			System.out.println("ProxyCall, operazione non riconosciuta");
		System.out.println("ProxyCall, 1");
		HttpEntity entity = new StringEntity(jsRequest.toString());
		System.out.println("jsRequest: "+jsRequest.toString());
		post.setEntity(entity);
		System.out.println("ProxyCall, 3");
		HttpResponse response = client.execute(post);
		System.out.println("ProxyCall, 4");
		entity = response.getEntity();
		System.out.println("ProxyCall, 5");

		InputStream is = entity.getContent();
		System.out.println("ProxyCall, 6");

		JSONTokener jt;
		System.out.println("ProxyCall, 7");

		jt = new JSONTokener(is);
		System.out.println("ProxyCall, 8");

		JSONObject jsResp = new JSONObject(jt);
		System.out.println("jsResp= " + jsResp.toString());
		Eccezione(jsResp);
		// consuma la response, altrimenti non posso fare altre richieste
		if (response.getEntity() != null) {
			response.getEntity().consumeContent();
		}
		is.close();
		return jsResp;
	}
	
	public static void Eccezione(JSONObject resp){
		String confronto="ERROR";
	//	Exception risposta=resp.getString("status") ;
		//if(confronto.equals(risposta)){			
//		}
	}
	
	public static JSONObject proxyCallM(String operation, Object author,Object tablekey,Object messagetype,Object messagecontent)
	throws Exception {
			JSONObject jsRequest = new JSONObject();
			System.out.println("proxyCall: " + operation);
			if (operation.equals("addBlackBoard")) {
					jsRequest.put("request", "writeMessage");
					jsRequest.put("authorKey", author);
					jsRequest.put("tableKey", tablekey);
					jsRequest.put("messageType", messagetype);
					jsRequest.put("messageContent", messagecontent);
					System.out.println("JSON OB: "+jsRequest);

			} else
				System.out.println("ProxyCall, operazione non riconosciuta");
			HttpEntity entity = new StringEntity(jsRequest.toString());
			post.setEntity(entity);
			HttpResponse response = client.execute(post);
			entity = response.getEntity();
			InputStream istream = entity.getContent();
			JSONTokener jtok;
			jtok = new JSONTokener(istream);
			System.out.println("JTOK"+jtok);
			System.out.println("ProxyCall, 1");

			JSONObject jsResponse = new JSONObject(jtok);
			System.out.println("ProxyCall, 2");
			System.out.println("jsResp= " + jsResponse.toString());
			// consuma la response, altrimenti non posso fare altre richieste
			if (response.getEntity() != null) {
				response.getEntity().consumeContent();
			}	
			istream.close();
			return jsResponse;
	}

	
	/**
	 * Converte un JSONObject, contenente un oggetto Group, in un oggetto Group
	 * */
	public static Group convertToGroup(JSONObject jsGroup){
		if(jsGroup==null) return null;
		Group g= new Group();
		try {
			g.setKey(jsGroup.getLong("key"));
			g.setName(jsGroup.getString("name"));
			
			JSONArray jsMembers=jsGroup.getJSONArray("members");
			List<Long> members= new ArrayList<Long>();
			for(int i=0;i<jsMembers.length();i++){
				members.add(jsMembers.getLong(i));
			}
			g.setMembers(members);
			
			g.setCreator(jsGroup.getLong("creator"));
			g.setOwner(jsGroup.getLong("owner"));
			
			JSONArray jsDocs=jsGroup.getJSONArray("documents");
			List<String> docs= new ArrayList<String>();
			for(int i=0;i<jsDocs.length();i++){
				docs.add(jsDocs.getString(i));
			}
			g.setDocuments(docs);
		} catch (JSONException e) {
			Log.i("ProxyUtils",e.toString());
		}
		return g;
	}
	
}

