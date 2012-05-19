package it.unito.gui;

import java.util.LinkedList;
import java.util.List;

import it.unito.json.JSONArray;
import it.unito.json.JSONObject;
import it.unito.utility.MembersAdapter;
import it.unito.utility.MyObjectListAdapter;
import it.unito.utility.MyTablesListAdapter;
import it.unito.utility.ProxyUtils;
import it.unito.utility.ViewMembers;
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


public class MembersActivity extends ListActivity {
	String infoCurrentTable;
	ArrayAdapter<ViewMembers> array;
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        /**
         * Richiesta Members è da fare nella onCreate perchè il Proxy sul server non aggiorna 
         * i dati.
         */
    	try{
			JSONObject request = ProxyUtils.MemberStatus("queryUserStatus");
			Log.i("Members","RICHIESTA= "+ request.toString());
			JSONObject rispo = request.getJSONObject("results");
			Log.i("Members","RISPOSTA= "+rispo.toString());
			JSONArray online= rispo.getJSONArray("ok");
			Log.i("Members","ARRAY ONLINE= "+ online.toString());
			JSONArray offline= rispo.getJSONArray("no");
			Log.i("Members","ARRAY OFFLINE= "+  offline.toString());
	
			List<ViewMembers> members=new LinkedList<ViewMembers>();
			for(int i=0;i<online.length();i++){
				Long onlineMemberKey = (Long) online.get(i);
				members.add(new ViewMembers(onlineMemberKey,"online"));
			}
			
			for(int j=0;j<offline.length();j++){
				Long offlineMemberKey = (Long) offline.get(j);
				members.add(new ViewMembers(offlineMemberKey,"offline"));
			}
				
			array=new MembersAdapter(this,members);
			setListAdapter(array);			
		} catch (Exception e) {
			Log.i("Eccezione", e.toString());
		}
	

		
		
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