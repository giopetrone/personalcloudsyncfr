package it.unito.gui;

import it.unito.utility.TablePlusAndroid;
import android.app.AlertDialog;
import android.app.ListActivity;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;


public class BlackBoard extends ListActivity {
	private String infoCurrentTable;
	private String key;
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.blackboard);
		infoCurrentTable=getIntent().getStringExtra("infoCurrentTable");
		key=getIntent().getStringExtra("key");
		TablePlusAndroid session=(TablePlusAndroid) this.getApplicationContext();

		Log.i("BlackBoard", "your key: "+session.getUserKey().toString());
		Log.i("BlackBoard","Current table key: "+key);

	}

	public void onResume() {
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

	public void add (View v)
	{
		Intent intent = new Intent(this,AddMessageActivity.class);
		intent.putExtra("infoCurrentTable",infoCurrentTable);
		intent.putExtra("key",key);
		startActivity(intent);
	}


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