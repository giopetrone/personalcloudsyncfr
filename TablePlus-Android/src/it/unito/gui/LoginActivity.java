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
		setContentView(R.layout.login);
		
		ProxyUtils.init();
		mail= (EditText) findViewById(R.id.mail);
		password= (EditText) findViewById(R.id.password);
		restaConnesso= (CheckBox) findViewById(R.id.resta_connesso);
		// carica le preferences
		updatePreferencesData();
		Log.i("LOGIN ","ONCREATE");
					
	}
	
	  public void onResume(){
		   super.onResume();
			 //notify user presence offline at server
		   try{
			   session=(TablePlusAndroid) this.getApplication();
			   if(session.getUserKey()!=null){
				   JSONObject userStatus = ProxyUtils.UserStatus("toggleStatus", session.getUserKey(),"false");
				   Log.i("LOGIN", "OnResume, userStatus offline: "+userStatus);
			   }

		   }catch(Exception e){ Log.i("Exception Login",e.toString());}
		   Log.i("LOGIN ","ONRESUME");
	   }
	
	  public void invia(View v)
		{
			String mailString=(mail.getText()).toString();
			String pass=(password.getText()).toString();
			if(!mailString.equals("")){//mettere funzione per LOGIN,(!mailString.equals("")) && (!pass.equals(""))
				try{
					JSONObject user = ProxyUtils.proxyCall("queryUser", mailString);
					JSONObject jsUser = user.getJSONObject("results");
					long userKey=jsUser.getLong("key");
					//get session from Application
					session=(TablePlusAndroid) this.getApplication();
					session.setUserKey(userKey);
					if (jsUser.getString("email").equals(mailString))
					{
						Toast.makeText(this, "Login success", Toast.LENGTH_LONG).show();
						if (restaConnesso.isChecked()) {
							//save login and preferences in session 
							savePreferencesData(v);
				        }
						
						//notify current user presence online at server
						JSONObject status = ProxyUtils.UserStatus("toggleStatus", userKey,"true");	
						Log.i("Login","userStatus online: "+status);
						
						Intent intent = new Intent(this,TableListActivity.class);
						intent.putExtra("user",jsUser.toString());
						startActivity(intent);
					}
					else Toast.makeText(this, "Login fail", Toast.LENGTH_LONG).show();
				}catch(Exception e){ Log.i("Exception Login",e.toString());}
			}
		
	}

	public void reset(View v)
	{
		mail.setText("");
		password.setText("");
		restaConnesso.setChecked(false);
	}
	


	private void updatePreferencesData() {
		// Read Preferences
		SharedPreferences prefs = getSharedPreferences(MY_PREFERENCES,
				Context.MODE_PRIVATE);
		//Read preferences about Login and Password in preferences
		currentLogin = prefs.getString(LOGIN, "yourmail@gmail.com");
		currentPwd=prefs.getString(PWD, "0000");
		mail.setText(currentLogin);
		password.setText(currentPwd);
	}
	
	/** Management method of the button that saves your preferences */
	public void savePreferencesData(View view) {
		// get current Preferences
		SharedPreferences prefs = getSharedPreferences(MY_PREFERENCES,
				Context.MODE_PRIVATE);
		// get Editor
		SharedPreferences.Editor editor = prefs.edit();
		// change value whith EditText value
		CharSequence textData = mail.getText();
		if (textData != null) {
			// save value in Preferences
			editor.putString(LOGIN, textData.toString());
			editor.commit();
		}
		
		CharSequence textData1 = password.getText();
		if (textData != null) {
			// save value in Preferences
			editor.putString(PWD, textData1.toString());
			editor.commit();
		}
		
		updatePreferencesData();
	}

}

