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

//Function of the class: create tab for see and use 3 different Activity (relating the same table): ExistantTable, Members and BlackBoard
@SuppressWarnings("deprecation")
public class HelloTabWidgetActivity extends TabActivity {
	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		Group currentTable=null;
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
		} catch (Exception e) {
			Log.i("Eccezione", e.toString());
		}


		// Create an Intent to launch an Activity the first tab
		intent = new Intent().setClass(this, ExistantTableActivity.class);

		// Create an Intent to launch an Activity for the tab (to be reused)
		intent = new Intent().setClass(this, ExistantTableActivity.class);

		intent.putStringArrayListExtra("TableDoc",(ArrayList<String>) currentTable.getDocuments());
		intent.putExtra("infoCurrentTable", currentTable.toString());
		// Initialize a TabSpec for each tab and add it to the TabHost
		spec = tabHost.newTabSpec("Objects").setIndicator("Objects",
				res.getDrawable(R.drawable.tab_existanttable))
				.setContent(intent);
		tabHost.addTab(spec);

		
		// second tab
		intent = new Intent().setClass(this, BlackBoardActivity.class);


		//Log.i("infoCurrentTable= ", currentTable.toString() );
		// Do the same for the other tabs
		intent = new Intent().setClass(this, BlackBoardActivity.class);

		intent.putExtra("infoCurrentTable", currentTable.toString());
		intent.putExtra("key", key);		
		spec = tabHost.newTabSpec("Messages").setIndicator("Messages",
				res.getDrawable(R.drawable.message))
				.setContent(intent);
		tabHost.addTab(spec);

		
		//third tab
		intent = new Intent().setClass(this, MembersActivity.class);


		intent = new Intent().setClass(this, MembersActivity.class);

		intent.putExtra("infoCurrentTable", currentTable.toString());
		intent.putExtra("key", key);
		spec = tabHost.newTabSpec("Members").setIndicator("Members",
				res.getDrawable(R.drawable.tab_info))
				.setContent(intent);
		tabHost.addTab(spec);


		//This is the number of the tab which is the first Activity opened when we call HelloTabWidget
		tabHost.setCurrentTab(0);

		tabHost.setCurrentTab(0);

	}

}