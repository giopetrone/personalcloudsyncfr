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
import android.view.Window;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ListAdapter;
import android.widget.ListView;
import android.widget.Toast;

//Function of the class: show a list of existant table and their attribute
public class TableListActivity extends ListActivity {
	private final String TAG = "ListActivity";
	private ArrayAdapter<ViewTable> adapter;
	private TablePlusAndroid session;
	
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		
		   //requestWindowFeature(Window.FEATURE_CUSTOM_TITLE);
	        //getWindow().setFeatureInt(Window.FEATURE_CUSTOM_TITLE, R.layout.mytitle2);

		
		registerForContextMenu(getListView());
		//custom title of activity
		
	}

	protected void onResume() {
		super.onResume();
		try {
			JSONObject jsUser=new JSONObject(getIntent().getStringExtra("user"));
			JSONArray tablesKeys = jsUser.getJSONArray("tables");
			JSONObject tables = ProxyUtils.proxyCall("queryTables", tablesKeys);
			JSONArray jsTables = tables.getJSONArray("results");
			List<ViewTable> viewTables=ProxyUtils.convertToViewTableList(jsTables);
			adapter=new MyTablesListAdapter(this,R.layout.tables_row,R.id.title_table,viewTables);
			setListAdapter(adapter);
		} catch (Exception e) {
			Log.i("Eccezione", e.toString());
		}		
		
		/**Quando funziona switchTable cancellare il try..catch()*/
		try{
		session=(TablePlusAndroid) this.getApplication();
		Log.i("session.getCurrentTableKey()",session.getCurrentTableKey().toString());
		}catch(Exception e1){
			Log.i("Eccezione current table", e1.toString());
		}
	}
	
	//OnClick open that table and go to HelloTabActivity
	@Override
	protected void onListItemClick(ListView l, View v, int position, long id) {
		
		ViewTable tmp = (ViewTable) adapter.getItem(position);
		String s = tmp.getKey().toString();
		//call method switchTable in ProxyUtils for change presence on table 
		try {
			JSONObject switchTables =ProxyUtils.switchTable("switchTable", session.getUserKey().toString(),session.getCurrentTableKey(),tmp.getKey());//TavoloCliccato);
			Log.i("SWITCHTABLE "," ris:"+switchTables);
		} catch (Exception e) {
			Log.i("Eccezione", e.toString());
		}
		
		
		//imposto tavolo corrente
		session=(TablePlusAndroid) this.getApplication();
		session.setCurrentTableKey(tmp.getKey());
		Log.i("session.getCurrentTableKey()",session.getCurrentTableKey().toString());
		
		Intent intent = new Intent(this, HelloTabWidgetActivity.class);
		intent.putExtra("key", s);
		startActivity(intent);
	}
	
	
	//Classic Menu: permit to crate a nerTable (not implemented)
	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
	    MenuInflater inflater = getMenuInflater();
	    inflater.inflate(R.menu.tables_menu, menu);
	    return true;
	}
	
	
	@Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
        case R.id.create_table:
            Log.i(TAG,"create_table");
            /*
             * 
             * INSTERT HERE CODE FOR DELETE A TABLE
             * 
             * */
            return true;
        default:
            return super.onOptionsItemSelected(item);
        }
    }

	
	//OnLongClick menu with 2 function: Rename and Delete Object (both the function are not implemented!)
		public void onCreateContextMenu(ContextMenu menu, View v,ContextMenuInfo menuInfo) {  
    super.onCreateContextMenu(menu, v, menuInfo);  
        menu.setHeaderTitle("Context Menu");  
        menu.add(0, v.getId(), 0, "Rename");  
        menu.add(0, v.getId(), 0, "Delete");  
    }  
		
		  @Override  
	    public boolean onContextItemSelected(MenuItem item) {  
	        if(item.getTitle()=="Rename"){Rename(item.getItemId());}  
	        else if(item.getTitle()=="Delete"){Delete(item.getItemId());}  
	        else {return false;}  
	    return true;  
	    }  
	  
	    public void Rename(int id){ 
	        Toast.makeText(this, "Rename", Toast.LENGTH_SHORT).show(); 
	        /**
	         * 
	         *INSERT HERE CODE FOR RENAME THE OBJECT  
	         * 
	         * 
	         */
	    }  
	    public void Delete(int id){
	    	Toast.makeText(this, "Delete", Toast.LENGTH_SHORT).show();  
	    	 /**
	         * 
	         *INSERT HERE CODE FOR DELETE THE OBJECT  
	         * 
	         * 
	         */
	   }  
	
}
