package it.unito.gui;

import it.unito.json.JSONArray;
import it.unito.json.JSONObject;
import it.unito.model.Message;
import it.unito.utility.MyMessageListAdapter;
import it.unito.utility.ProxyUtils;
import it.unito.utility.ViewMessage;

import java.util.LinkedList;
import java.util.List;

import android.app.AlertDialog;
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
import android.widget.AdapterView.AdapterContextMenuInfo;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.Toast;

public class BlackBoardActivity extends ListActivity{
	private String tablekey;
	private String infoCurrentTable;
	private ArrayAdapter<ViewMessage> array;
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
		loadMessage();
	}

	/**
	 *Call in the first time for create OptionMenu
	 */
	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		MenuInflater inflater = getMenuInflater();
		inflater.inflate(R.menu.blackboard_menu, menu);
		return true;
	}



	public void add (View v)
	{
		Intent intent = new Intent(this,AddMessageActivity.class);
		intent.putExtra("key",tablekey);
		startActivity(intent);
	}


	/** Menu on LongClick*/
	public void onCreateContextMenu(ContextMenu menu, View v,ContextMenuInfo menuInfo) {  
		super.onCreateContextMenu(menu, v, menuInfo);  
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
			ViewMessage deletingMex=(ViewMessage)listItem;
			try{
				JSONObject del = ProxyUtils.proxyCall("deleteMessage",deletingMex.getKey());
				status=del.getString("status");
			} catch (Exception e) {
				Log.i("Eccezione", e.toString());
			}
			if(status.equals("OK")){
				loadMessage();
				Toast.makeText(this, "Delete avvenuta", Toast.LENGTH_SHORT).show();
			}else
				Toast.makeText(this, "Delete non riuscita", Toast.LENGTH_SHORT).show();
		} else {return false;}  
		return true;  
	}  

	
	/**
	 * Chiamato dal sistema quando viene cliccato un item del menu
	 */
	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
		switch (item.getItemId()) {
		case R.id.info_tab:
			AlertDialog.Builder builder=new AlertDialog.Builder(this);
			builder.setTitle("Inform Table"); 
			builder.setMessage(infoCurrentTable);
			builder.setCancelable(true);
			AlertDialog alert=builder.create();
			alert.show();
			return true;
		default:
			return super.onOptionsItemSelected(item);
		}
	}   
	/**
	 * Load ListView for show message
	 */
	public void loadMessage(){
		List<Message> listMessage=null;
		List<Long> usersKeys=new LinkedList<Long>();
		List<ViewMessage> listViewMessage=new LinkedList<ViewMessage>();

		try{
			JSONObject request = ProxyUtils.proxyCall("queryMessages", tablekey);
			JSONArray jsMessages = request.getJSONArray("results");
			listMessage=ProxyUtils.convertToMessagesList(jsMessages);
			if(listMessage.isEmpty()){
				Toast.makeText(this, "No Messages uploaded", Toast.LENGTH_LONG).show(); 
			}
			//load keys
			for(Message m:listMessage){
				usersKeys.add(m.getAuthor());
				ViewMessage tmp=new ViewMessage();
				tmp.setContent(m.getContent());
				tmp.setKey(m.getKey());
				tmp.setType(m.getType());
				listViewMessage.add(tmp);
			}
			//Proxy request
			JSONObject request1 = ProxyUtils.proxyCall("queryUsers",usersKeys);
			JSONArray users = request1.getJSONArray("results");
			//load data in ViewMessage list
			for(int i=0;i<listViewMessage.size();i++){
				JSONObject jo = users.getJSONObject(i);
				String email=jo.getString("email");
				String author=ProxyUtils.clearMail(email);
				listViewMessage.get(i).setAuthor(author);
			}
			//Add Object to List
			array=new MyMessageListAdapter(this,R.layout.tables_row,R.id.title_table,listViewMessage);
			setListAdapter(array);
		} catch (Exception e) {
			Log.i("Eccezione", e.toString());
		}

		addi.setOnClickListener(new View.OnClickListener() {
			public void onClick(View view) { 
				add(view); 		  }
		});
	}
}