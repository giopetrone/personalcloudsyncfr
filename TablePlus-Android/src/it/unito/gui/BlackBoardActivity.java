package it.unito.gui;

import java.util.List;

import it.unito.json.JSONArray;
import it.unito.json.JSONObject;
import it.unito.utility.MyMessageListAdapter;
import it.unito.utility.MyObjectListAdapter;
import it.unito.utility.ProxyUtils;
import it.unito.utility.TablePlusAndroid;
import it.unito.model.Message;
import android.app.Activity;
import android.app.AlertDialog;
import android.app.ListActivity;
import android.content.Intent;
import android.database.Cursor;
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
import android.widget.Adapter;
import android.widget.AdapterView.AdapterContextMenuInfo;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.CursorAdapter;
import android.widget.EditText;
import android.widget.SimpleCursorAdapter;
import android.widget.TextView;
import android.widget.Toast;

public class BlackBoardActivity extends ListActivity{
	private String tablekey;
	private String infoCurrentTable;
	private ArrayAdapter<Message> array;
	private Button addi;

	public void onCreate(Bundle savedInstanceState)
	{
		super.onCreate(savedInstanceState);
		addi=new Button(this);
		addi.setText("Add Message");
		getListView().addFooterView( addi);
		
		registerForContextMenu(getListView());
		tablekey=getIntent().getStringExtra("key");
		infoCurrentTable=getIntent().getStringExtra("infoCurrentTable");
        
	}

	
	 @Override
		protected void onResume()
		{
		 super.onResume();
		 	    	
		 infoCurrentTable=getIntent().getStringExtra("infoCurrentTable");
	     tablekey=getIntent().getStringExtra("key");
	     Log.i("TABLEKEY",tablekey);
	     loadMessage();
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
		    // Get the info on which item was selected
		    AdapterContextMenuInfo info = (AdapterContextMenuInfo) menuInfo;

		    // Get the Adapter behind your ListView (this assumes you're using
		    // a ListActivity; if you're not, you'll have to store the Adapter yourself
		    // in some way that can be accessed here.)
		    Adapter adapter = getListAdapter();

		    // Retrieve the item that was clicked on
		    Object item = adapter.getItem(info.position);
	        Log.i("Balckboard","hai cliccato: "+item.toString());
	        
	        menu.setHeaderTitle("Context Menu");  
	        menu.add(0, v.getId(), 0, "Delete");  
	    }  
	  @Override  
	    public boolean onContextItemSelected(MenuItem item) {  
	        if(item.getTitle()=="Delete"){
	        	String status="";
	        	// Here's how you can get the correct item in onContextItemSelected()
	            AdapterContextMenuInfo info = (AdapterContextMenuInfo) item.getMenuInfo();
	            Object listItem = getListAdapter().getItem(info.position);
	            Message deletingMex=(Message)listItem;
	        	Log.i("BalckBoard","listItem :"+listItem.toString());
	        	Log.i("BalckBoard","deletingMex :"+deletingMex.toString());
		    	try{
		    		JSONObject del = ProxyUtils.proxyCall("deleteMessage",deletingMex.getKey());
		    		Log.i("BalckBoard","del :"+del.toString());
		    		status=del.getString("status");
					Log.i("status= ",  status);
				} catch (Exception e) {
					Log.i("Eccezione", e.toString());
				}
		    	if(status.equals("OK")){
		    		Log.i("End ",  "elemento cancellato");
		    		loadMessage();
		    		Toast.makeText(this, "Delete avvenuta", Toast.LENGTH_SHORT).show();
		    	}else
		    		Toast.makeText(this, "Delete non riuscita", Toast.LENGTH_SHORT).show();
	        } else {return false;}  
	    return true;  
	    }  
	    public void function2(int id){
	    	
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
	/**
	 * Carica la listView con i messaggi del tavolo.
	 */
	public void loadMessage(){
		 List<Message> listMessage=null;
			try{
				JSONObject request = ProxyUtils.proxyCall("queryMessages", tablekey);
		        JSONArray jsMessages = request.getJSONArray("results");
				Log.i("jsMessages= ",  jsMessages.toString());
			    listMessage=ProxyUtils.convertToMessagesList(jsMessages);
				Log.i("jsMessages2= ",  listMessage.toString());
			} catch (Exception e) {
				Log.i("Eccezione", e.toString());
			}
			
			//aggiungo gli oggetti alla lista
			array=new MyMessageListAdapter(this,R.layout.tables_row,R.id.title_table,listMessage);
			setListAdapter(array);
			 
			
			addi.setOnClickListener(new View.OnClickListener() {
		  		  public void onClick(View view) { 
		  			add(view); 		  }
		  		});
	}
}