package it.unito.gui;

import it.unito.json.JSONObject;
import it.unito.utility.ProxyUtils;
import android.app.Activity;
import android.app.AlertDialog;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.widget.TextView;


public class Members extends Activity {
	String infoCurrentTable;
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        String 	tablekey=getIntent().getStringExtra("key");
        TextView textview = new TextView(this);
        textview.setText("This is the Members Area.");
        setContentView(textview);
    
    /*    infoCurrentTable=getIntent().getStringExtra("infoCurrentTable");
    	try{
			JSONObject request = ProxyUtils.proxyCall("deleteMessage", "ag10YWJsZXBsdXNwbHVzchgLEgVHcm91cBgCDAsSB01lc3NhZ2UYBgw2");
			JSONObject jsTable = request.getJSONObject("results");
			Log.i("jsTable= ",  jsTable.toString());
		} catch (Exception e) {
			Log.i("Eccezione", e.toString());
		}*/

        //ag10YWJsZXBsdXNwbHVzchgLEgVHcm91cBgCDAsSB01lc3NhZ2UYBgw
    
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