package it.unito.gui;

import it.unito.json.JSONArray;
import it.unito.json.JSONObject;
import it.unito.utility.ProxyUtils;
import it.unito.utility.TablePlusAndroid;
import it.unito.utility.ViewDoc;

import java.util.ArrayList;
import java.util.List;

import android.app.TabActivity;
import android.content.Intent;
import android.content.res.Resources;
import android.os.Bundle;
import android.util.Log;
import android.widget.TabHost;
import android.widget.Toast;

//Function of the class: create tab for see and use 3 different Activity (relating the same table): ExistantTable, Members and BlackBoard
@SuppressWarnings("deprecation")
public class HelloTabWidgetActivity extends TabActivity {
	private TablePlusAndroid session;
	private JSONObject jsTable;
	private ArrayList<ViewDoc> tableDocs;
	private String info;
	private boolean noException=true;
	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.tab);
		
		Log.i("HelloTab","HelloTab");
		
		String key=getIntent().getStringExtra("key");
		Long tableKey=Long.parseLong(key);
		
		Resources res = getResources(); // Resource object to get Drawables
		TabHost tabHost = getTabHost();  // The activity TabHost
		TabHost.TabSpec spec;  // Resusable TabSpec for each tab
		Intent intent;  // Reusable Intent for each tab
		
		try{
			session=(TablePlusAndroid) this.getApplication();
			JSONObject request = ProxyUtils.queryTable(session.getUserKey(), tableKey);
			jsTable = request.getJSONObject("results");
			JSONArray jsDocs= jsTable.getJSONArray("documents");
			
			//create an arrayList to contain Document for pass them next activity
			tableDocs=new ArrayList<ViewDoc>();
			for(int i=0;i<jsDocs.length();i++){
				JSONObject currentDoc=(JSONObject) jsDocs.get(i);
				tableDocs.add(new ViewDoc(currentDoc.getString("docId"),currentDoc.getString("title"),currentDoc.getString("link")));
			}
			
			//create a string contains the table information
			List<Long> usersKeys=new ArrayList<Long>();
			usersKeys.add(jsTable.getLong("creator"));
			usersKeys.add(jsTable.getLong("owner"));
			
			//Proxy request
			JSONObject request1 = ProxyUtils.proxyCall("queryUsers",usersKeys);
			JSONArray users = request1.getJSONArray("results");
			Log.i("Members","Users: "+users);
		
			String[] newMail=new String[2];
			//load data on the return list
			for(int i=0;i<users.length();i++){
				JSONObject jo = users.getJSONObject(i);
				String email=jo.getString("email");
				newMail[i]=ProxyUtils.clearMail(email);
			}
			
			info="Table Name: "+jsTable.getString("name")+" - "+"Creator: "+newMail[0]+" - "+"Owner: "+newMail[1];
			Log.i("queryTable INFO HelloTab",info);
			
			
			Log.i("queryTable response HelloTab",jsTable.toString());
			Log.i("queryTable response jsDocs",jsDocs.toString());
			Log.i("queryTable response tableDocs",tableDocs.toString());
			
		} catch (Exception e) {
			Log.i("Eccezione", e.toString());
			noException=false;
		}
		
		if(noException){
			
			// Create an Intent to launch an Activity the first tab
			intent = new Intent().setClass(this, ExistantTableActivity.class);
	
			
			intent.putExtra("TableDoc",tableDocs);
			intent.putExtra("infoCurrentTable",info);
			// Initialize a TabSpec for each tab and add it to the TabHost
			spec = tabHost.newTabSpec("Objects").setIndicator("Objects",
					res.getDrawable(R.drawable.tab_existanttable))
					.setContent(intent);
			tabHost.addTab(spec);
	
			
			// second tab
			intent = new Intent().setClass(this, BlackBoardActivity.class);
	
			intent.putExtra("infoCurrentTable", info);
			intent.putExtra("key", key);		
			spec = tabHost.newTabSpec("Messages").setIndicator("Messages",
					res.getDrawable(R.drawable.message))
					.setContent(intent);
			tabHost.addTab(spec);
	
			
			//third tab
			intent = new Intent().setClass(this, MembersActivity.class);
	
	
			intent.putExtra("infoCurrentTable", info);
			intent.putExtra("key", key);
			spec = tabHost.newTabSpec("Members").setIndicator("Members",
					res.getDrawable(R.drawable.tab_info))
					.setContent(intent);
			tabHost.addTab(spec);
	
	
			//This is the number of the tab which is the first Activity opened when we call HelloTabWidget
			tabHost.setCurrentTab(0);
		}else
			Toast.makeText(this, "Exception Occurred", Toast.LENGTH_LONG).show();
	}

}