package com.example.spike3android_gae;

import java.util.LinkedList;
import java.util.List;

import org.json.JSONArray;
import org.json.JSONObject;


import android.app.AlertDialog;
import android.app.ListActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.widget.ArrayAdapter;
import android.widget.Toast;


public class MainActivity extends ListActivity {

	String key;
	ArrayAdapter<ViewDoc> array;
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
      //  setContentView(R.layout.activity_main);
     //   ProxyUtils.init();
    }
    
    public void onResume(){
        super.onResume();
        Log.i("sono in onResume", "ciao");
     // prove annagio        
             
        loadData();
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
    
    public void loadData(){
    	
    	try {
    		Log.i("sono in loadData", "ciao");
    	//  key=getIntent().getStringExtra("key");
    		key = "9";
    		List<ViewDoc> members=new LinkedList<ViewDoc>();
   /*       JSONObject request = ProxyUtils.proxyCall("queryUsersStatus",key);
          JSONObject rispo = request.getJSONObject("results");
          JSONArray online= rispo.getJSONArray("online");
          
          List<Long> usersKeys=new LinkedList<Long>();  
          //load members status
          for(int i=0;i<online.length();i++){
                  long onlineMemberKey = (long) online.getInt(i);
                  members.add(new ViewMembers(onlineMemberKey,"online"));
                 
          }
          */
        //  members.add( new ViewMembers((long)1,"online"));
    		members.add( new ViewDoc( "ii","online", "dd"));
          //load data on adapter
          array=new MyAdapter(this,members);
          setListAdapter(array);        
    	 } catch (Exception e) { Log.i("Eccezione", e.toString());}
    }

	
    
  
}
