package com.example.spike3android_gae;

import java.util.LinkedList;
import java.util.List;

import it.unito.json.*;

import android.app.AlertDialog;
import android.app.ListActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.widget.ArrayAdapter;
import android.widget.Toast;

public class MainActivity extends ListActivity {

	String key;
	ArrayAdapter<ViewDoc> array;

	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		// setContentView(R.layout.activity_main);
		Log.i("sono in onCreate", "ciao");
		ProxyUtils.init();
		Log.i("sono in onCreate", "ciao dopo init");
	}

	public void onResume() {
		super.onResume();
		Log.i("sono in onResume", "ciao");
		// prove annagio

		loadData();
	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		getMenuInflater().inflate(R.menu.activity_main, menu);
		return true;
	}

	/*
	 * public void serverCall(){ try{ key=getIntent().getStringExtra("key");
	 * JSONObject request = ProxyUtils.proxyCall("firstTest",key); JSONObject
	 * rispo = request.getJSONObject("results");
	 * 
	 * //Toast.makeText(this, "No Members on this table",
	 * Toast.LENGTH_LONG).show();
	 * 
	 * } catch (Exception e) { Log.i("Eccezione", e.toString());} }
	 */
	public void loadData() {

		try {
			Log.i("sono in loadData", "ciao");
			// key=getIntent().getStringExtra("key");
			key = "AnnaGio";
			List<ViewDoc> viewResults = new LinkedList<ViewDoc>();

			Log.i("loadData", key);
			String fromServlet = "";
			JSONObject result = ProxyUtils.proxyCall("firstTest", key);
			fromServlet = result.getString("status");

			// JSONObject rispo = result.getJSONObject("status");
			Log.i("loadData fromServlet", fromServlet.toString());
			// JSONArray online= rispo.getJSONArray("online");

			// List<Long> usersKeys=new LinkedList<Long>();
			// load members status
			// for(int i=0;i<online.length();i++){
			// long onlineMemberKey = (long) online.getInt(i);
			// members.add(new ViewDoc(onlineMemberKey,"online"));

			// }

			// members.add( new ViewMembers((long)1,"online"));
			viewResults.add(new ViewDoc("id", fromServlet, "dd"));
			// load data on adapter
			array = new MyAdapter(this, viewResults);
			setListAdapter(array);
		} catch (Exception e) {
			Log.i("Eccezione", e.toString());
		}
	}

}
