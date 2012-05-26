package it.unito.gui;

import it.unito.json.JSONObject;
import it.unito.model.MessageType;
import it.unito.utility.ProxyUtils;
import it.unito.utility.TablePlusAndroid;
import android.app.Activity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.EditText;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.Toast;

//Function of the class: Add a message at the BlackBoard
public class AddMessageActivity extends Activity {
	private String infoCurrentTable;
	private String key;
	private String UserKey;
	private String testo;
	private EditText newMess;
	private RadioButton rbInfo;
	private RadioButton rbProblem;
	private RadioButton rbTodo;
	private RadioButton rbGeneric;
	private RadioGroup rg;
	private MessageType tipo;

	public void onCreate(Bundle savedInstanceState)
	{
		super.onCreate(savedInstanceState);
		setContentView(R.layout.addblackboard);
		newMess= (EditText) findViewById(R.id.nuovomess);
		rbInfo = (RadioButton) findViewById(R.id.rbInfo);
		rbGeneric = (RadioButton) findViewById(R.id.rbGeneric);
		rbProblem = (RadioButton) findViewById(R.id.rbProblem);
		rbTodo = (RadioButton) findViewById(R.id.rbTodo);
		/*
		 //Layout con spinner
		setContentView(R.layout.prova_add);
	    Spinner spinner = (Spinner) findViewById(R.id.spinner1);
	    ArrayAdapter<CharSequence> adapter = ArrayAdapter.createFromResource(
	            this, R.array.type_array, android.R.layout.simple_spinner_item);
	    adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
	    spinner.setAdapter(adapter);*/
	    
        rg= (RadioGroup) findViewById(R.id.radiogroup);
        infoCurrentTable=getIntent().getStringExtra("infoCurrentTable");
	 	key=getIntent().getStringExtra("key");
		TablePlusAndroid session=(TablePlusAndroid) this.getApplicationContext();
		UserKey = session.getUserKey().toString();
	}
	//Choose type of Message with a Rabio Button in Radio Group
	 RadioButton.OnClickListener myOptionOnClickListener =
		   new RadioButton.OnClickListener()
		  {
		  @Override
		  public void onClick(View v) {
		   // TODO Auto-generated method stub
		   Toast.makeText(AddMessageActivity.this,
		     "Option 1 : " + rbInfo.isChecked() + "\n"+
		     "Option 2 : " + rbGeneric.isChecked() + "\n" +
		     "Option 3 : " + rbProblem.isChecked() + "\n" +
		     "Option 3 : " + rbTodo.isChecked(),
		     Toast.LENGTH_LONG).show();
		  }	   
		  };
		  //Method for adding the message
		public void add (View v)
		{
			boolean flag=true;
			testo=newMess.getText().toString();  	    
		    if(rbInfo.isChecked()){
		    	tipo= MessageType.INFO;
		    }else if(rbProblem.isChecked()){
		    	tipo= MessageType.PROBLEM;
		    }else if(rbTodo.isChecked()){
		    	tipo= MessageType.TODO;
		    }else if(rbGeneric.isChecked()){
		    	tipo= MessageType.GENERIC;
		    }else{flag=false; }
		    	
		    if(flag && !testo.isEmpty()){
		    	try{	
			    	JSONObject request = ProxyUtils.newMessage(UserKey ,key ,tipo, testo);//addBlackBoard
				} catch (Exception e) {
					Log.i("Eccezione", e.toString());
				}
		    	//finish(), stop the current activity
				finish();
		    }else
		    	Toast.makeText(AddMessageActivity.this,"Check a Type or isert text", Toast.LENGTH_LONG).show();
			
		}
	
}

