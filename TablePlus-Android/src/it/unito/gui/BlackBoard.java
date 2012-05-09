package it.unito.gui;

import it.unito.json.JSONArray;
import it.unito.json.JSONObject;
import it.unito.utility.ProxyUtils;
import it.unito.utility.TablePlusAndroid;
import android.app.Activity;
import android.app.AlertDialog;
import android.app.ListActivity;
import android.content.Intent;
import android.graphics.Color;
import android.net.Uri;
import android.os.Bundle;
import android.util.Log;
import android.view.ContextMenu;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ContextMenu.ContextMenuInfo;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

public class BlackBoard extends ListActivity{
	private String tablekey;
	private String infoCurrentTable;
	private ArrayAdapter<String> array;
	private Button addi;

	public void onCreate(Bundle savedInstanceState)
	{
		super.onCreate(savedInstanceState);
		addi=new Button(this);
	/*	(Button) findViewById(R.id.addbutton);
	 eliminaSelezionati = new Button(this);*/
		addi.setText("Add Message");
		getListView().addFooterView( addi);
		registerForContextMenu(getListView());
		tablekey=getIntent().getStringExtra("key");
		infoCurrentTable=getIntent().getStringExtra("infoCurrentTable");
        registerForContextMenu(getListView());
        Log.i("ARRIVA QUI","qui");
        registerForContextMenu(getListView());
        Log.i("ARRIVA QUI","qui");
	}

	
	 @Override
		protected void onResume()
		{
		 super.onResume();
	        Log.i("ARRIVA QUI","qui2");
	    	
		 infoCurrentTable=getIntent().getStringExtra("infoCurrentTable");
	     tablekey=getIntent().getStringExtra("key");
	        Log.i("ARRIVA QUI","qui2");

	     Log.i("TABLEKEY",tablekey);

			try{
				JSONObject request = ProxyUtils.proxyCall("queryMessages", tablekey);
		        Log.i("TEST","2");
		        JSONArray jsTable = request.getJSONArray("results");
				Log.i("jsTable= ",  jsTable.toString());
			} catch (Exception e) {
				Log.i("Eccezione", e.toString());
			}
		/*	add.setOnClickListener(new View.OnClickListener() {
		  		  public void onClick(View view) { 
		  			add(view); 		  }
		  		});*/

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
    

	
	public void add (View v)
	{
		Intent intent = new Intent(this,AddMessageActivity.class);
		intent.putExtra("infoCurrentTable",infoCurrentTable);
		intent.putExtra("key",tablekey);
		startActivity(intent);
	}

	//-------------------MENU SU LONGCLICK--------------------------------------
	public void onCreateContextMenu(ContextMenu menu, View v,ContextMenuInfo menuInfo) {  
	    super.onCreateContextMenu(menu, v, menuInfo);  
	        menu.setHeaderTitle("Context Menu");  
	        menu.add(0, v.getId(), 0, "Delete");  
	    }  
	  @Override  
	    public boolean onContextItemSelected(MenuItem item) {  
	        if(item.getTitle()=="Delete"){
	        	function2(item.getItemId());
	        } else {return false;}  
	    return true;  
	    }  
	    public void function2(int id){
	    	Toast.makeText(this, "Delete", Toast.LENGTH_SHORT).show(); 
	    	try{
				JSONObject del = ProxyUtils.proxyCall("deleteMessage", id);
				JSONObject jsTable = del.getJSONObject("results");
				Log.i("jsTable= ",  jsTable.toString());
			} catch (Exception e) {
				Log.i("Eccezione", e.toString());
			}
	    } 
	    
	    
	    //------------------------------------MENU DA TASTO MENU---------------------------------------------
    /**
	 * Chiamato dal sistema quando viene cliccato un item del menu
	 */
	@Override
    public boolean onOptionsItemSelected(MenuItem item) {
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