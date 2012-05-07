package it.unito.gui;




import it.unito.json.JSONObject;
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
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

public class AddMessageActivity extends Activity {
	private String infoCurrentTable;
	private String key;
	private String UserKey;
	private String testo;
	private EditText newMess;
	private CheckBox cbInfo;
	private CheckBox cbProblem;
	private CheckBox cbTodo;
	private CheckBox cbGeneric;
	private enum ok{INFO,TODO,PROBLEM,GENERIC};
//    private Spinner spinner;
	private enum MyEnum{
		 ENUM1("INFO"),
		 ENUM2("TODO"),
		 ENUM3("PROBLEM"),
		 ENUM4("GENERIC");
		    private String friendlyName;
		    private MyEnum(String friendlyName){
		        this.friendlyName = friendlyName;
		    }
		    @Override public String toString(){
		        return friendlyName;
		    }
		 }

	public void onCreate(Bundle savedInstanceState)
	{
		super.onCreate(savedInstanceState);
		setContentView(R.layout.addblackboard);
		newMess= (EditText) findViewById(R.id.nuovomess);
	//	spinner = (Spinner) findViewById(R.id.spinner1);
		//ArrayAdapter<String> adapter = new ArrayAdapter<String>(this , android.R.layout.simple_spinner_item );
        //adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        //spinner.setAdapter(adapter);
	//	spinner.setAdapter(new ArrayAdapter<MyEnum>(this, android.R.layout.simple_spinner_item, MyEnum.values()));
		cbInfo = (CheckBox) findViewById(R.id.cinfo);
		cbProblem = (CheckBox) findViewById(R.id.cproblem);
		cbProblem = (CheckBox) findViewById(R.id.cproblem);
		cbTodo = (CheckBox) findViewById(R.id.ctodo);
        infoCurrentTable=getIntent().getStringExtra("infoCurrentTable");
	 	key=getIntent().getStringExtra("key");
		TablePlusAndroid session=(TablePlusAndroid) this.getApplicationContext();
		UserKey = session.getUserKey().toString();
		Log.i("This is AddToBlackBoard, your key: ",UserKey);
        Log.i("This is AttToBlackBoard, this table key is:",key);
	}
	
	 /* public void onItemSelected(AdapterView<?> Av, View v, int position, long id) 
	  {
          String item = (String) spinner.getSelectedItem();
          Toast toast = Toast.makeText(AddMessageActivity.this, "Hai selezionato " + item, Toast.LENGTH_SHORT);
          toast.show();
	  }*/
 

		public void add (View v)
	{
			testo=newMess.getText().toString();  
		    Log.i("NUOVO MESSAGGIO:",testo);
	//	    Log.i("SPINNER",spinner.);
	//		if(spinner.equals("PROBLEM")){
	//				Log.i("SPINNER","PROBLEM");}
		    if(cbInfo.isChecked()){
		    	ok n=ok.INFO;
		    }else if(cbProblem.isChecked()){
		    	ok n=ok.PROBLEM;
		    }else if(cbTodo.isChecked()){
		    	ok n=ok.TODO;
		    }else if(cbGeneric.isChecked()){
		    	ok n=ok.GENERIC;
		    	n=ok.TODO;
		    }
		    try{	
		    	JSONObject request = ProxyUtils.proxyCallM("addBlackBoard",UserKey ,key ,ok.INFO	 , testo);
				Log.i("STRINGA DI RICHIESTA",request.toString());
			//	JSONObject jsTable = request.getJSONObject("results");
				//currentTable=ProxyUtils.convertToGroup(jsTable);
				//Log.i("jsTable= ",  jsTable.toString());
				//Log.i("currentTable= ",  currentTable.toString());
			} catch (Exception e) {
				Log.i("Eccezione", e.toString());
			}
		    
		    Intent intent = new Intent(this,BlackBoard.class);
			startActivity(intent);
			
			
	}



	
}

