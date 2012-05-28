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
	private String key;
	private String UserKey;
	private String testo;
	private EditText newMess;
	private RadioButton rbInfo;
	private RadioButton rbProblem;
	private RadioButton rbTodo;
	private RadioButton rbGeneric;
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

		key=getIntent().getStringExtra("key");
		TablePlusAndroid session=(TablePlusAndroid) this.getApplicationContext();
		UserKey = session.getUserKey().toString();
	}

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

		if(flag && !testo.equals("")){
			try{	
				JSONObject request = ProxyUtils.newMessage(UserKey ,key ,tipo, testo);
			} catch (Exception e) {
				Log.i("Eccezione", e.toString());
			}
			//finish(), stop the current activity
			finish();
		}else
			Toast.makeText(AddMessageActivity.this,"Check a Type or isert text", Toast.LENGTH_LONG).show();

	}

}

