package it.unito.gui;




import it.unito.json.JSONArray;
import it.unito.json.JSONObject;
import it.unito.utility.ProxyUtils;
import it.unito.utility.TablePlusAndroid;
import android.app.Activity;
import android.content.Intent;
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
	public void onCreate(Bundle savedInstanceState)
	{
		super.onCreate(savedInstanceState);
		setContentView(R.layout.login);
		ProxyUtils.init();
		mail = (EditText) findViewById(R.id.mail);
		password = (EditText) findViewById(R.id.password);
		restaConnesso = (CheckBox) findViewById(R.id.resta_connesso);
		
	}
	

	public void invia(View v)
	{
		String mailString="marcoprova91@gmail.com";/*(mail.getText()).toString();*//*"leonard87@gmail.com";*/
		String pass=(password.getText()).toString();
		
		try{
			JSONObject user = ProxyUtils.proxyCall("queryUser", mailString);
			Log.i("ciao","ciao");
			JSONObject jsUser = user.getJSONObject("results");
			Log.i("loginActivity", "USER= " + jsUser.toString());
			Log.i("loginActivity", "email= " + jsUser.getString("email"));
			//Inizializzo la Session
			session=(TablePlusAndroid) this.getApplication();
			session.setUserKey(jsUser.getLong("key"));
			Log.i("session.getUserKey()",session.getUserKey().toString());
			
			if (jsUser.getString("email").equals(mailString))
			{
				Toast.makeText(this, "Login effettuato", Toast.LENGTH_LONG).show();
				Intent intent = new Intent(this,TableList.class);
				intent.putExtra("user",jsUser.toString());
				startActivity(intent);
			}
			else Toast.makeText(this, "Login errato", Toast.LENGTH_LONG).show();
		}catch(Exception e){ Log.i("Eccezione Login",e.toString());}
	}

	public void reset(View v)
	{
		mail.setText("");
		password.setText("");
		restaConnesso.setChecked(false);
	}

}

