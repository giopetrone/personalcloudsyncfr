package it.unito.gui;

import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;

import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.entity.StringEntity;
import org.apache.http.impl.client.DefaultHttpClient;

import android.app.AlertDialog;
import android.app.Dialog;
import android.app.ListActivity;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.view.ContextMenu;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.ContextMenu.ContextMenuInfo;
import android.view.ViewGroup.LayoutParams;
import android.view.View;

import it.unito.json.*;

import android.app.Activity;	
import android.net.Uri;
import android.os.Bundle;
import android.util.Log;
import android.widget.ArrayAdapter;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.ListAdapter;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import it.unito.model.*;
import it.unito.utility.MyObjectListAdapter;
import it.unito.utility.ProxyUtils;
import it.unito.utility.ViewTable;

/**
 * 
 * 
 *@idea Classe che visualizza un tavolo esistente
 */
public class ExistantTableActivity extends ListActivity{
	private TextView nomeTavolo;
	private String infoCurrentTable;
	private ArrayAdapter<String> array;
	private EditText newUrl;
	public void onCreate(Bundle savedInstanceState)
	{
		super.onCreate(savedInstanceState);
		registerForContextMenu(getListView());
	}
	
	/*
	 *@idea onResume fa la connessione con proxy invia una richiesta specificando la chiave del tavolo sul quale 
	 * mi trovo trovo e riceverà un risposta contenente un'oggetto tavolo in formato json che al suo interno
	 * conterrà tutto ciò che è presente sul tavolo.
	 */
	 @Override
		protected void onResume()
		{
		 super.onResume();
	        array=new MyObjectListAdapter(this,getIntent().getStringArrayListExtra("TableDoc"));
			setListAdapter(array);
		}
	 		
	 		//----------------OnClick aprire documenti---------------------------------
	 		@Override
	 		protected void onListItemClick(ListView l, View v, int position, long id)
	 		{
	 			System.out.println("position: "+position);
	 			System.out.println("id: "+id);
	 			String url="http://"+l.getItemAtPosition(position).toString();
	 			Intent myIntent = new Intent(Intent.ACTION_VIEW, Uri.parse(url));
	 			startActivity(myIntent);
	 		}
	 		
	    
	 		
	 		//-----------------------------Menu su LongClick--------------------------------------------
	 		public void onCreateContextMenu(ContextMenu menu, View v,ContextMenuInfo menuInfo) {  
		    super.onCreateContextMenu(menu, v, menuInfo);  
		        menu.setHeaderTitle("Context Menu");  
		        menu.add(0, v.getId(), 0, "Rename");  
		        menu.add(0, v.getId(), 0, "Delete");  
		    }  
	 		
	 		  @Override  
			    public boolean onContextItemSelected(MenuItem item) {  
			        if(item.getTitle()=="Rename"){function1(item.getItemId());}  
			        else if(item.getTitle()=="Delete"){function2(item.getItemId());}  
			        else {return false;}  
			    return true;  
			    }  
			  
			    public void function1(int id){ 
			        Toast.makeText(this, "Rename", Toast.LENGTH_SHORT).show();  
			    }  
			    public void function2(int id){
			    	Toast.makeText(this, "Delete", Toast.LENGTH_SHORT).show();  
			    }  

				/**
				 * Chiamato dal sistema la prima volta per creare il menu
				 */
				@Override
				public boolean onCreateOptionsMenu(Menu menu) {
				    MenuInflater inflater = getMenuInflater();
				    inflater.inflate(R.menu.tab_menu, menu);
				    return true;
				}
			    
			    /**
				 * Chiamato dal sistema quando viene cliccato un item del menu
				 */
				@Override
			    public boolean onOptionsItemSelected(MenuItem item) {
			        // Handle item selection
			        switch (item.getItemId()) {
			        case R.id.add_obj:
			        	AlertDialog.Builder myDialog=new AlertDialog.Builder(ExistantTableActivity.this);
				        myDialog.setTitle("Add Object"); 
				        TextView textView = new TextView(ExistantTableActivity.this);
				        textView.setText("Insert new web address");
				        LayoutParams textViewLayoutParams=new LayoutParams(LayoutParams.WRAP_CONTENT, LayoutParams.WRAP_CONTENT);
				        textView.setLayoutParams(textViewLayoutParams);
				        newUrl = new EditText(ExistantTableActivity.this);
				        LayoutParams editTextLayoutParams=new LayoutParams(LayoutParams.FILL_PARENT, LayoutParams.WRAP_CONTENT);
				        newUrl.setLayoutParams(editTextLayoutParams);
				        LinearLayout layout=new LinearLayout(ExistantTableActivity.this);
				        layout.setOrientation(LinearLayout.VERTICAL);
				        layout.addView(textView);
				        layout.addView(newUrl);
				        myDialog.setView(layout);
				        myDialog.setPositiveButton("OK", new DialogInterface.OnClickListener() {
				            public void onClick(DialogInterface arg0, int arg1) {
				              String url=newUrl.getText().toString();   
				              array.add(url);
				            }
				            });
				        
				        myDialog.setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
				            // do something when the button is clicked
				            public void onClick(DialogInterface arg0, int arg1) {
				             }
				            });
			        	myDialog.show();    
			        	return true;
			        case R.id.info_tab:
			        	Log.i("Info","info_table");
			        	AlertDialog.Builder builder=new AlertDialog.Builder(ExistantTableActivity.this);
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