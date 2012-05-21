package it.unito.gui;

import it.unito.json.JSONObject;
import it.unito.utility.ProxyUtils;
import it.unito.utility.TablePlusAndroid;
import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.view.Window;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.Toast;

public class LoginActivity extends Activity {
	
	private EditText mail;
	private EditText password;
	private CheckBox restaConnesso;
	private TablePlusAndroid session;
	private static final String MY_PREFERENCES = "MyPref";
	private static final String LOGIN = "yourmail@gmail.com";
	private static final String PWD = "0000";
	private String currentLogin;
	private String currentPwd;
	
	public void onCreate(Bundle savedInstanceState)
	{
		super.onCreate(savedInstanceState);
		  //requestWindowFeature(Window.FEATURE_CUSTOM_TITLE);
	      setContentView(R.layout.main);
	      //getWindow().setFeatureInt(Window.FEATURE_CUSTOM_TITLE, R.layout.mytitle);
		
		setContentView(R.layout.login);
		ProxyUtils.init();
		mail= (EditText) findViewById(R.id.mail);
		password= (EditText) findViewById(R.id.password);
		restaConnesso= (CheckBox) findViewById(R.id.resta_connesso);
		// carica le preferences
		updatePreferencesData();
		Log.i("LOGIN "," PASSA QUI");
/*		try{
			JSONObject user = ProxyUtils.UserStatus("toggleStatus", ,);
		}catch(Exception e){ Log.i("Eccezione Login",e.toString());}
	*/	
	}
	
	  public void onResume(){
		   super.onResume();
			Log.i("LOGIN "," PASSA QUI 2");
	   }
	
	  public void invia(View v)
		{
			String mailString=(mail.getText()).toString();
			String pass=(password.getText()).toString();
			if(!mailString.equals("")){//mettere funzione per LOGIN,(!mailString.equals("")) && (!pass.equals(""))
				try{
					JSONObject user = ProxyUtils.proxyCall("queryUser", mailString);
					JSONObject jsUser = user.getJSONObject("results");
					//Inizializzo la Session
					session=(TablePlusAndroid) this.getApplication();
					session.setUserKey(jsUser.getLong("key"));
					if (jsUser.getString("email").equals(mailString))
					{
						Toast.makeText(this, "Login effettuato", Toast.LENGTH_LONG).show();
						if (restaConnesso.isChecked()) {
							//save login and preferences in session 
							savePreferencesData(v);
				        }
						
						Intent intent = new Intent(this,TableListActivity.class);
						intent.putExtra("user",jsUser.toString());
						startActivity(intent);
					}
					else Toast.makeText(this, "Login errato", Toast.LENGTH_LONG).show();
				}catch(Exception e){ Log.i("Eccezione Login",e.toString());}
			}
		//Put user Status online/Offline
	/*	try{
			JSONObject user = ProxyUtils.UserStatus("toggleStatus", session.getUserKey().toString(),"offline");
		}catch(Exception e){ Log.i("Eccezione Login",e.toString());
		}
	
		*/
	}

	public void reset(View v)
	{
		mail.setText("");
		password.setText("");
		restaConnesso.setChecked(false);
	}
	


	private void updatePreferencesData() {
		// Leggiamo le Preferences
		SharedPreferences prefs = getSharedPreferences(MY_PREFERENCES,
				Context.MODE_PRIVATE);
		// Leggiamo l'informazione associate a Login e Password nelle preferences
		currentLogin = prefs.getString(LOGIN, "yourmail@gmail.com");
		currentPwd=prefs.getString(PWD, "0000");
		mail.setText(currentLogin);
		password.setText(currentPwd);
	}
	
	/** Metodo di gestione del pulsante che salva le preferences */
	public void savePreferencesData(View view) {
		// Otteniamo il riferimento alle Preferences
		SharedPreferences prefs = getSharedPreferences(MY_PREFERENCES,
				Context.MODE_PRIVATE);
		// Otteniamo il corrispondente Editor
		SharedPreferences.Editor editor = prefs.edit();
		// Modifichiamo il valore con quello inserito nell'EditText
		CharSequence textData = mail.getText();
		if (textData != null) {
			// Lo salviamo nelle Preferences
			editor.putString(LOGIN, textData.toString());
			editor.commit();
		}
		
		CharSequence textData1 = password.getText();
		if (textData != null) {
			// Lo salviamo nelle Preferences
			editor.putString(PWD, textData1.toString());
			editor.commit();
		}
		
		updatePreferencesData();
	}

}

