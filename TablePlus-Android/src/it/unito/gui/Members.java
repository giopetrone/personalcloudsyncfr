package it.unito.gui;

import java.util.List;

import it.unito.json.JSONArray;
import it.unito.json.JSONObject;
import it.unito.utility.MembersAdapter;
import it.unito.utility.MyObjectListAdapter;
import it.unito.utility.ProxyUtils;
import android.app.Activity;
import android.app.AlertDialog;
import android.app.ListActivity;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.widget.ArrayAdapter;
import android.widget.ListAdapter;
import android.widget.TextView;


public class Members extends ListActivity {
	String infoCurrentTable;

    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    	try{
			JSONObject request = ProxyUtils.MemberStatus("queryUserStatus", 3);
			Log.i("RICHIESTA= ",  request.toString());
			JSONObject rispo = request.getJSONObject("results");
			Log.i("RISPOSTA= ",  rispo.toString());
			JSONArray online= rispo.getJSONArray("ok");
			Log.i("ARRAY ONLINE= ", online.toString());
			JSONArray offline = rispo.getJSONArray("no");
			Log.i("ARRAY OFFLINE= ",  offline.toString());
	
			
		} catch (Exception e) {
			Log.i("Eccezione", e.toString());
		}
	//	String []utenti =new String[online.length()];

		
		//ListAdapter array=new ArrayAdapter<String>(this, android.R.layout.simple_list_item_1, android.R.id.text1,online);
		//setListAdapter(array);
   }
      
    
	protected void onResume()
	{
	 super.onResume();
	}
    
	/**
	 * Chiamato dal sistema la prima volta per creare il menu
	 */
	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
	    MenuInflater inflater = getMenuInflater();
	    inflater.inflate(R.menu.members_message_menu, menu);
	    return true;
	}
    
    /**
	 * Chiamato dal sistema quando viene cliccato un item del menu
	 */
	@Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle item selection
        switch (item.getItemId()) {
        case R.id.info_tab:
        	Log.i("Info","info_table");
        	AlertDialog.Builder builder=new AlertDialog.Builder(this);
        	builder.setTitle("Inform Table"); 
        	builder.setMessage(getIntent().getStringExtra("infoCurrentTable"));
        	builder.setCancelable(true);
	        AlertDialog alert=builder.create();
	        alert.show();
        	return true;
        default:
            return super.onOptionsItemSelected(item);
        }
    }   

}