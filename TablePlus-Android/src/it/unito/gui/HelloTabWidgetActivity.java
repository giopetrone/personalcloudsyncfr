package it.unito.gui;

import java.util.ArrayList;

import it.unito.json.JSONObject;
import it.unito.model.Group;
import it.unito.utility.ProxyUtils;
import android.app.TabActivity;
import android.content.Intent;
import android.content.res.Resources;
import android.os.Bundle;
import android.util.Log;
import android.widget.TabHost;

/**
 * Contenitore delle activity sul tavolo, funge da controller per smistare i dati del tavolo
 */
public class HelloTabWidgetActivity extends TabActivity {
	//String key;
	
	/** Called when the activity is first created. */
	@Override
	public void onCreate(Bundle savedInstanceState) {
		Group currentTable=null;
		super.onCreate(savedInstanceState);
		setContentView(R.layout.tab);
		
		String key=getIntent().getStringExtra("key");
		Long realKey=Long.parseLong(key);

		Resources res = getResources(); // Resource object to get Drawables
		TabHost tabHost = getTabHost();  // The activity TabHost
		TabHost.TabSpec spec;  // Resusable TabSpec for each tab
		Intent intent;  // Reusable Intent for each tab

		
		try{
			JSONObject request = ProxyUtils.proxyCall("queryTable", realKey);
			JSONObject jsTable = request.getJSONObject("results");
			currentTable=ProxyUtils.convertToGroup(jsTable);
			Log.i("jsTable= ",  jsTable.toString());
			Log.i("currentTable= ",  currentTable.toString());
		} catch (Exception e) {
			Log.i("Eccezione", e.toString());
		}

		// Create an Intent to launch an Activity for the tab (to be reused)
		intent = new Intent().setClass(this, ExistantTableActivity.class);
		intent.putStringArrayListExtra("TableDoc",(ArrayList<String>) currentTable.getDocuments());
		intent.putExtra("infoCurrentTable", currentTable.toString());
		// Initialize a TabSpec for each tab and add it to the TabHost
		spec = tabHost.newTabSpec("Object").setIndicator("Object",
				res.getDrawable(R.drawable.tab_existanttable))
				.setContent(intent);
		tabHost.addTab(spec);

		//Log.i("infoCurrentTable= ", currentTable.toString() );
		// Do the same for the other tabs
		intent = new Intent().setClass(this, BlackBoardActivity.class);
		intent.putExtra("infoCurrentTable", currentTable.toString());
		intent.putExtra("key", key);		
		spec = tabHost.newTabSpec("BlackBoard").setIndicator("Blackboard",
				res.getDrawable(R.drawable.message))
				.setContent(intent);
		tabHost.addTab(spec);

		intent = new Intent().setClass(this, Members.class);
		intent.putExtra("infoCurrentTable", currentTable.toString());
		intent.putExtra("key", currentTable.getKey());
		spec = tabHost.newTabSpec("Members").setIndicator("Members",
				res.getDrawable(R.drawable.tab_info))
				.setContent(intent);
		tabHost.addTab(spec);

		tabHost.setCurrentTab(0);
	}

}