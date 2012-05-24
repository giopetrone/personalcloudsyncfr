package it.unito.gui;

import it.unito.utility.MyObjectListAdapter;
import it.unito.utility.ViewDoc;

import java.util.ArrayList;

import android.app.AlertDialog;
import android.app.ListActivity;
import android.content.DialogInterface;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.util.Log;
import android.view.ContextMenu;
import android.view.ContextMenu.ContextMenuInfo;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup.LayoutParams;
import android.widget.ArrayAdapter;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

//Function of the class: Show an ExistantTable and its objects
public class ExistantTableActivity extends ListActivity{
	private ArrayAdapter<ViewDoc> array;
	private EditText newLocalDoc;
	public void onCreate(Bundle savedInstanceState)
	{
		super.onCreate(savedInstanceState);
		registerForContextMenu(getListView());
	}
	
	 @Override
		protected void onResume()
		{
		 super.onResume();
		 	@SuppressWarnings("unchecked")
			ArrayList<ViewDoc> tableDocs= (ArrayList<ViewDoc>) getIntent().getSerializableExtra("TableDoc");
		 	
		 	if(tableDocs.isEmpty())
		 		Toast.makeText(this, "No Object Uploaded", Toast.LENGTH_LONG).show(); 
		 	
	 		array=new MyObjectListAdapter(this,tableDocs);
			setListAdapter(array);
		 	
	      
		}
	 		
	 		//OnClick method open the object as url
	 		@Override
	 		protected void onListItemClick(ListView l, View v, int position, long id)
	 		{
	 			System.out.println("position: "+position);
	 			System.out.println("id: "+id);
	 			//Log.i("CLick",l.getItemAtPosition(position).getClass().toString());
	 			ViewDoc clickedItem=(ViewDoc) l.getItemAtPosition(position);
	 			String url=clickedItem.getLink();
	 			if(url!=""){
	 				Intent myIntent = new Intent(Intent.ACTION_VIEW, Uri.parse(url));
		 			startActivity(myIntent);
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

			//called first time from system for creating menu
			@Override
			public boolean onCreateOptionsMenu(Menu menu) {
				    MenuInflater inflater = getMenuInflater();
				    inflater.inflate(R.menu.object_menu, menu);
				    return true;
				}
			    
			    //ClassicMenu with 2 function:
				// 1:open and AlertDialog and is possible add a new object (this MUST be an existant url and the function is not linked with server)
				// 2:show the info of the table
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
				        newLocalDoc = new EditText(ExistantTableActivity.this);
				        LayoutParams editTextLayoutParams=new LayoutParams(LayoutParams.FILL_PARENT, LayoutParams.WRAP_CONTENT);
				        newLocalDoc.setLayoutParams(editTextLayoutParams);
				        LinearLayout layout=new LinearLayout(ExistantTableActivity.this);
				        layout.setOrientation(LinearLayout.VERTICAL);
				        layout.addView(textView);
				        layout.addView(newLocalDoc);
				        myDialog.setView(layout);
				        myDialog.setPositiveButton("OK", new DialogInterface.OnClickListener() {
				            public void onClick(DialogInterface arg0, int arg1) {
				              String title=newLocalDoc.getText().toString();   
				              ViewDoc current=new ViewDoc();
				              current.setTitle(title);
				              array.add(current);  
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