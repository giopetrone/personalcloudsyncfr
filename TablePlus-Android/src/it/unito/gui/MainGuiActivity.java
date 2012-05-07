package it.unito.gui;



import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;

public class MainGuiActivity extends Activity {
	private static final String MY_PREFERENCES = "MyPref";
	private static final String TEXT_DATA_KEY = "textData";
	private Button button;
	private EditText inputView;
	private String textData;

	private TextView outputView;

	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.main);
		//inizializza gli oggetti statici: HttpClient e HttpPost
		 //StaticObject.init();
		// carica le preferences
			updatePreferencesData();
		// sceglie l'activity
		redirect();
	}

	@Override
	public void onResume() {
		super.onResume();
		System.out.println("Ho eseguito ManiGUI");
		button = (Button) findViewById(R.id.bottone1);

		button.setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View v) {
				savePreferencesData(v);
				redirect();
			}
		});
	}

	/** Metodo di gestione del pulsante che salva le preferences */
	public void savePreferencesData(View view) {
		// Otteniamo il riferimento alle Preferences
		SharedPreferences prefs = getSharedPreferences(MY_PREFERENCES,
				Context.MODE_PRIVATE);
		// Otteniamo il corrispondente Editor
		SharedPreferences.Editor editor = prefs.edit();
		// Modifichiamo il valore con quello inserito nell'EditText
		inputView = (EditText) findViewById(R.id.inputData);
		CharSequence textData = inputView.getText();
		if (textData != null) {
			// Lo salviamo nelle Preferences
			editor.putString(TEXT_DATA_KEY, textData.toString());
			editor.commit();
		}
		updatePreferencesData();
	}	

	private void updatePreferencesData() {
		// Leggiamo le Preferences
		SharedPreferences prefs = getSharedPreferences(MY_PREFERENCES,
				Context.MODE_PRIVATE);
		// Leggiamo l'informazione associata alla proprietà TEXT_DATA
		textData = prefs.getString(TEXT_DATA_KEY, "No Preferences!");
		outputView = (TextView) findViewById(R.id.outData);
		outputView.setText(textData);
	}

	private void redirect(){
    	Intent intent0; 
		
		if(textData.equalsIgnoreCase("loggato")){
			//redirect ListaTavoliGUI
			intent0=new Intent(this,TableList.class);
			startActivity(intent0);
		}else if(textData.equalsIgnoreCase("sloggato")){
			//redirect LoginGUI
			intent0=new Intent(this,LoginActivity.class);
			startActivity(intent0);
		}else{
			outputView= (TextView) findViewById(R.id.outData);
			outputView.setText("Scrivi: loggato o sloggato"); 
			 inputView = (EditText) findViewById(R.id.inputData);
			 inputView.setText("");
			
		}
}
}