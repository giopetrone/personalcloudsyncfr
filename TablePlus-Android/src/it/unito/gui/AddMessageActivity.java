package it.unito.gui;

import it.unito.json.JSONObject;
import it.unito.model.MessageType;
import it.unito.utility.ProxyUtils;
import it.unito.utility.TablePlusAndroid;
import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

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
        rg= (RadioGroup) findViewById(R.id.radiogroup);
/*        rbInfo.setOnClickListener(myOptionOnClickListener);
        rbGeneric.setOnClickListener(myOptionOnClickListener);
        rbProblem.setOnClickListener(myOptionOnClickListener);
        rbTodo.setChecked(true);*/
        infoCurrentTable=getIntent().getStringExtra("infoCurrentTable");
	 	key=getIntent().getStringExtra("key");
		TablePlusAndroid session=(TablePlusAndroid) this.getApplicationContext();
		UserKey = session.getUserKey().toString();
		Log.i("This is AddToBlackBoard, your key: ",UserKey);
        Log.i("This is AttToBlackBoard, this table key is:",key);
	}
	
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
		
 

		public void add (View v)
	{
			testo=newMess.getText().toString();  
		    Log.i("NUOVO MESSAGGIO:",testo);		    
		/*    int checkedRadioButton = rg.getCheckedRadioButtonId();
		    String radioButtonSelected = "";   
		    switch (checkedRadioButton) {
		      case R.id.rbInfo : radioButtonSelected = "rbInfo";
		      tipo= MessageType.INFO;;
		      case R.id.rbProblem : radioButtonSelected = "rbProblem";
		    	tipo= MessageType.PROBLEM;
		      case R.id.rbGeneric : radioButtonSelected = "rbGeneric";
		    	tipo= MessageType.GENERIC;
		      case R.id.rbTodo : radioButtonSelected = "rbTodo";
		    	tipo= MessageType.TODO;
		    }
		    Log.i("TIPO MESSAGGIO",tipo.toString());*/
		    if(rbInfo.isChecked()){
		    	tipo= MessageType.INFO;
		    }else if(rbProblem.isChecked()){
		    	tipo= MessageType.PROBLEM;
		    }else if(rbTodo.isChecked()){
		    	tipo= MessageType.TODO;
		    }else if(rbGeneric.isChecked()){
		    	tipo= MessageType.GENERIC;
		    }
		  
		    try{	
		    	JSONObject request = ProxyUtils.proxyCallM("addBlackBoard",UserKey ,key ,tipo, testo);
				Log.i("STRINGA DI RICHIESTA",request.toString());
			//	JSONObject jsTable = request.getJSONObject("results");
				//currentTable=ProxyUtils.convertToGroup(jsTable);
				//Log.i("jsTable= ",  jsTable.toString());
				//Log.i("currentTable= ",  currentTable.toString());
			} catch (Exception e) {
				Log.i("Eccezione", e.toString());
			}
			Intent intent = new Intent(this,AddMessageActivity.class);
			startActivity(intent);

			
	}



	
}

