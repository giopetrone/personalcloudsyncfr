package it.unito.gui;

import java.io.IOException;
import java.io.InputStream;
import java.util.LinkedList;
import java.util.List;

import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.entity.StringEntity;


import it.unito.model.*;
import it.unito.json.*;
import it.unito.utility.*;


import android.app.ListActivity;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.ContextMenu;
import android.view.ContextMenu.ContextMenuInfo;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ListAdapter;
import android.widget.ListView;
import android.widget.Toast;

/**
 * 
 * @author hp
 * @idea Classe che visualizza la lista dei tavoli presenti
 */
public class TableListActivity extends ListActivity {
	private final String TAG = "ListActivity";
	private List<Group> groups;
	private ArrayAdapter<ViewTable> adapter;

	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		System.out.println("Ho eseguito ListaTavoliGUI");
		registerForContextMenu(getListView());
	}
	/**
	 * @idea Visualizza una lista dei tavoli
	 */
	protected void onResume() {
		super.onResume();
		Log.i(TAG,"onResume richiamata");

		try {
			/**Recuperare lista dei tavoli*/
			JSONObject jsUser=new JSONObject(getIntent().getStringExtra("user"));
			JSONArray tablesKeys = jsUser.getJSONArray("tables");
			Log.i("tables",tablesKeys.toString());
			
			
			JSONObject tables = ProxyUtils.proxyCall("queryTables", tablesKeys);
			JSONArray jsTables = tables.getJSONArray("results");
			
			Log.i("tables", tables.toString());
			Log.i("jsArray", jsTables.toString());
		
			List<ViewTable> viewTables=ProxyUtils.convertToViewTableList(jsTables);
			
			adapter=new MyTablesListAdapter(this,R.layout.tables_row,R.id.title_table,viewTables);
			setListAdapter(adapter);
			
		} catch (Exception e) {
			Log.i("Eccezione", e.toString());
		}
		
		
		
	}

	@Override
	protected void onListItemClick(ListView l, View v, int position, long id) {
		System.out.println("position: " + position);
		System.out.println("id: " + id);
		ViewTable tmp = (ViewTable) adapter.getItem(position);
		System.out.println("Nome tavolo: " + tmp.toString());
		System.out.println("Chiave tavolo: " + tmp.getKey());
		String s = tmp.getKey().toString();
		System.out.println("Chiave tavolo: " + s);
		Intent intent = new Intent(this, HelloTabWidgetActivity.class);
		intent.putExtra("key", s);
		startActivity(intent);
	}
	
	//provare
	protected void onLongListItemClick(View v, int pos, long id) {
		
		CharSequence text = "Long Click!";
		int duration = Toast.LENGTH_SHORT;
		Toast toast = Toast.makeText(getApplicationContext(), text, duration);
		toast.show();
	}
	
	/**
	 * Chiamato dal sistema la prima volta per creare il menu
	 */
	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
	    MenuInflater inflater = getMenuInflater();
	    inflater.inflate(R.menu.below_tab_list, menu);
	    return true;
	}
	
	/**
	 * Chiamato dal sistema quando viene cliccato un item del menu
	 */
	@Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle item selection
        switch (item.getItemId()) {
        case R.id.create_table:
            Log.i(TAG,"create_table");
            return true;
        default:
            return super.onOptionsItemSelected(item);
        }
    }

	
}
