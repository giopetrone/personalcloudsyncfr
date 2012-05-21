package it.unito.utility;

import it.unito.json.JSONArray;
import it.unito.json.JSONException;
import it.unito.json.JSONObject;
import it.unito.json.JSONTokener;
import it.unito.model.Document;
import it.unito.model.Group;
import it.unito.model.Message;
import it.unito.model.MessageType;

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

import android.R.integer;
import android.util.Log;

public class ProxyUtils {
	private static HttpClient client;
	private static HttpPost post;
	private static final String PROXY_URL = "http://10.0.2.2:8888/tableplus/proxy";

	/**
	 * Method: first time only create object
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

	//create the JSON Object for different request and then make connection with server and put response into another JSON Object (send at the activity)
	public static JSONObject proxyCall(String operation, Object param)
			throws Exception {
		JSONObject jsRequest = new JSONObject();
		if (operation.equals("queryUser")) {
			jsRequest.put("request", "queryUser");
			jsRequest.put("userEmail", param);
		} else if (operation.equals("queryTable")) {
			jsRequest.put("request", "queryTable");
			jsRequest.put("tableKey", param);
		} else if (operation.equals("queryTables")) {
			jsRequest.put("request", "queryTables");
			jsRequest.put("tableKeysList", param);
		} else if (operation.equals("deleteMessage")) {
			jsRequest.put("request", "deleteMessage");
			jsRequest.put("messageKey", param);
		} else if (operation.equals("queryMessages")) {
			jsRequest.put("request", "queryMessages");
			jsRequest.put("tableKey", param);
		} else if (operation.equals("queryUsersStatus")) {
			jsRequest.put("request", "queryUsersStatus");
			jsRequest.put("tableKey", param);
		} else if (operation.equals("queryUsers")) {
            jsRequest.put("request", "queryUsers");
            jsRequest.put("userKeysList", param);
		}else
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
	
	//create the JSON Object for switchTable request
	public static JSONObject switchTable(String operation, Object UserKey, Object prevTableKey, Object currentTableKey)
			throws Exception {
		JSONObject jsRequest = new JSONObject();
		if (operation.equals("switchTable")) {
			jsRequest.put("request", "switchTable");
			jsRequest.put("userKey", UserKey);
			jsRequest.put("prevTable", prevTableKey);
			jsRequest.put("currentTable", currentTableKey);
		}  else
			System.out.println("ProxyCall, operazione non riconosciuta");
		Log.i("switchTable"," " +jsRequest);
		HttpEntity entity = new StringEntity(jsRequest.toString());
		post.setEntity(entity);
		HttpResponse response = client.execute(post);
		entity = response.getEntity();
		InputStream is = entity.getContent();
		JSONTokener jt;
		jt = new JSONTokener(is);
		JSONObject jsResp = new JSONObject(jt);
		Log.i("switchTable jsResp",""+jsResp);
		if (response.getEntity() != null) {
			response.getEntity().consumeContent();
		}
		is.close();
		return jsResp;
	}
	

	//create the JSON Object for hideMe request
	public static JSONObject hideMe(String operation, Object userKey, Object tableKey)
			throws Exception {
		JSONObject jsRequest = new JSONObject();
		if (operation.equals("toggleHide")) {
			jsRequest.put("request", "toggleHide");
			jsRequest.put("userKey", userKey);
			jsRequest.put("tableKey", tableKey);
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
		if (response.getEntity() != null) {
			response.getEntity().consumeContent();
		}
		is.close();
		return jsResp;
	}
	
	
	//create a JSON Object for requeste for adding a message in the BlackBoard
	public static JSONObject newMessage(String operation, Object author,Object tablekey, Object messagetype, Object messagecontent)
			throws Exception {
		JSONObject jsRequest = new JSONObject();
		System.out.println("proxyCall: " + operation);
		if (operation.equals("addBlackBoard")) {
			jsRequest.put("request", "writeMessage");
			jsRequest.put("authorKey", author);
			jsRequest.put("tableKey", tablekey);
			jsRequest.put("messageType", messagetype);
			jsRequest.put("messageContent", messagecontent);
		} else
			System.out.println("ProxyCall, operazione non riconosciuta");
		HttpEntity entity = new StringEntity(jsRequest.toString());
		post.setEntity(entity);
		HttpResponse response = client.execute(post);
		entity = response.getEntity();
		InputStream istream = entity.getContent();
		JSONTokener jtok;
		jtok = new JSONTokener(istream);
		JSONObject jsResponse = new JSONObject(jtok);
		if (response.getEntity() != null) {
			response.getEntity().consumeContent();
		}
		istream.close();
		return jsResponse;
	}
	
	// convert a JSON Object to a Group Object (if is compatible)	 
	public static Group convertToGroup(JSONObject jsGroup) {
		if (jsGroup == null)
			return null;
		Group g = new Group();
		try {
			g.setKey(jsGroup.getLong("key"));
			g.setName(jsGroup.getString("name"));
			JSONArray jsMembers = jsGroup.getJSONArray("members");
			List<Long> members = new ArrayList<Long>();
			for (int i = 0; i < jsMembers.length(); i++) {
				members.add(jsMembers.getLong(i));
			}
			g.setMembers(members);
			g.setCreator(jsGroup.getLong("creator"));
			g.setOwner(jsGroup.getLong("owner"));
			JSONArray jsDocs = jsGroup.getJSONArray("documents");
			List<String> docs = new ArrayList<String>();
			
			for (int i = 0; i < jsDocs.length(); i++) {
				docs.add(jsDocs.getString(i));
			}
			
			g.setDocuments(docs);
		} catch (JSONException e) {
			Log.i("ProxyUtils", e.toString());
		}
		return g;
	}

	// convert a JSON Array to a List of Message (if is compatible)	 
	public static List<Message> convertToMessagesList(JSONArray jsMessages)
			throws JSONException {
		List<Message> messages = new ArrayList<Message>();
		
		for (int i = 0; i < jsMessages.length(); i++) {
			JSONObject jsMex = (JSONObject) jsMessages.get(i);
			messages.add(convertToMessage(jsMex));
		}
		return messages;
	}

	// convert a JSON Object to a Message (if is compatible)	 
	public static Message convertToMessage(JSONObject jsMex)
			throws JSONException {
		if (jsMex == null)
			return null;
		Message mex = new Message();
		mex.setAuthor(jsMex.getLong("author"));
		String type = jsMex.getString("type");
		if (type.equals("INFO")) {
			mex.setType(MessageType.INFO);
		} else if (type.equals("PROBLEM")) {
			mex.setType(MessageType.PROBLEM);
		} else if (type.equals("TODO")) {
			mex.setType(MessageType.TODO);
		} else if (type.equals("GENERIC")) {
			mex.setType(MessageType.GENERIC);
		}
		mex.setContent(jsMex.getString("content"));
		mex.setKey(jsMex.getString("key"));
		return mex;
	}

	// convert a JSON Array to a List of ViewTable (if is compatible)	 
	public static List<ViewTable> convertToViewTableList(JSONArray jsTables)
			throws JSONException {
		List<ViewTable> tables = new LinkedList<ViewTable>();
		for (int i = 0; i < jsTables.length(); i++) {
			JSONObject group = (JSONObject) jsTables.get(i);
			tables.add(new ViewTable(group.getString("name"), group
					.getLong("key"), group.getInt("documents"), group
					.getInt("members")));
		}
		return tables;
	}

}
