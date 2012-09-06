package com.example.spike3android_gae;

import org.json.JSONObject;


import android.os.Bundle;
import android.app.Activity;
import android.util.Log;
import android.view.Menu;

public class MainActivity extends Activity {

	String key;
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
// prove annagio        
        ProxyUtils.init();
        serverCall();
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.activity_main, menu);
        return true;
    }
    public void serverCall(){
        try{
                key=getIntent().getStringExtra("key");
                JSONObject request = ProxyUtils.proxyCall("firstTest",key);
                JSONObject rispo = request.getJSONObject("results");

                //Toast.makeText(this, "No Members on this table", Toast.LENGTH_LONG).show();
                        
           } catch (Exception e) { Log.i("Eccezione", e.toString());}
        }
  
}
