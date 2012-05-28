package it.unito.gui;

import it.unito.json.JSONArray;
import it.unito.json.JSONObject;
import it.unito.utility.MyTablesListAdapter;
import it.unito.utility.ProxyUtils;
import it.unito.utility.TablePlusAndroid;
import it.unito.utility.ViewTable;

import java.util.List;

import android.app.ListActivity;
import android.app.Notification;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.ContextMenu;
import android.view.ContextMenu.ContextMenuInfo;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.widget.Toast;

//Function of the class: show a list of existant table and their attribute
public class TableListActivity extends ListActivity {

	private ArrayAdapter<ViewTable> adapter;
	private TablePlusAndroid session;
	private static final int HELLO_ID = 1;
	
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		registerForContextMenu(getListView());	
		
		//notification-bar standard message
		String ns = Context.NOTIFICATION_SERVICE;
		NotificationManager mNotificationManager = (NotificationManager) getSystemService(ns);
		
		int icon = android.R.drawable.stat_notify_chat;
		CharSequence tickerText = "Hello";
		long when = System.currentTimeMillis();

		Notification notification = new Notification(icon, tickerText, when);

		Context context = getApplicationContext();
		CharSequence contentTitle = "TablePlus-Android";
		CharSequence contentText = "Now you are Online!";
		Intent notificationIntent = new Intent(this, TableListActivity.class);
		PendingIntent contentIntent = PendingIntent.getActivity(this, 0, notificationIntent, 0);

		notification.setLatestEventInfo(context, contentTitle, contentText, contentIntent);
		mNotificationManager.notify(HELLO_ID, notification);
	}

	public void onResume() {
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
		

		try{
		session=(TablePlusAndroid) this.getApplication();
		JSONObject switchTables =ProxyUtils.setPresence(session.getUserKey(),session.getCurrentTableKey(),false);
		}catch(Exception e1){
			Log.i("Eccezione current table", e1.toString());
		}
	}
	
	//OnClick open that table and go to HelloTabActivity
	@Override
	protected void onListItemClick(ListView l, View v, int position, long id) {
		
		ViewTable tmp = (ViewTable) adapter.getItem(position);
		String s = tmp.getKey().toString();
		
		//imposed current table in session
		session=(TablePlusAndroid) this.getApplication();
		session.setCurrentTableKey(tmp.getKey());
				
		//call method switchTable in ProxyUtils for change presence on table 
		try {
			JSONObject currentPres =ProxyUtils.setPresence(session.getUserKey(),session.getCurrentTableKey(),true);
		} catch (Exception e) {
			Log.i("Eccezione", e.toString());
		}
		
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
        	Toast.makeText(this, "Create Table", Toast.LENGTH_SHORT).show(); 
            /**
             * 
             * INSTERT HERE CODE FOR DELETE A TABLE
             * 
             */
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
