package it.unito.gui;

import it.unito.json.JSONArray;
import it.unito.json.JSONObject;
import it.unito.utility.MembersAdapter;
import it.unito.utility.ProxyUtils;
import it.unito.utility.TablePlusAndroid;
import it.unito.utility.ViewMembers;

import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;

import android.app.AlertDialog;
import android.app.ListActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.widget.ArrayAdapter;
import android.widget.Toast;

//Function of the class: show a list member of the member of the table (where we are in that moment) highlighting online and offline members 
public class MembersActivity extends ListActivity {
	String infoCurrentTable;
	String key;
	ArrayAdapter<ViewMembers> array;
	private TablePlusAndroid session;


    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        /**
         * Memebers is on onCreate because Proxy on server dont reload user status
         */
        
   }
    
   public void onResume(){
	   super.onResume();
	   loadMembers();
   }
      
	//Classic Menu: here we can only see the info about the table (with an AlertDialog)
	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
	    MenuInflater inflater = getMenuInflater();
	    inflater.inflate(R.menu.members_menu, menu);
	    return true;
	}
    
 
	@Override
    public boolean onOptionsItemSelected(MenuItem item) {
		AlertDialog.Builder builder=new AlertDialog.Builder(this);
		AlertDialog alert;
        switch (item.getItemId()) {
        case R.id.info_tab:
        	builder.setTitle("Inform Table"); 
        	builder.setMessage(getIntent().getStringExtra("infoCurrentTable"));
        	builder.setCancelable(true);
	        alert=builder.create();
	        alert.show();
        	return true;
        case R.id.hide_tab:
        	builder.setTitle("Hide status"); 
        	//change my status in Hide
        	hideMe();
    		builder.setMessage("Your status has changed in Hide/Unhide");
        	builder.setCancelable(true);
	        alert=builder.create();
	        alert.show();
	        //reload member list
	        loadMembers();
        	return true;
        default:
            return super.onOptionsItemSelected(item);
        }
    }   

	public void hideMe(){
		try{
			session=(TablePlusAndroid) this.getApplication();
			JSONObject request = ProxyUtils.UserStatus("toggleHide", session.getUserKey().toString(),session.getCurrentTableKey().toString());
		} catch (Exception e) {
			Log.i("Eccezione", e.toString());
		}
	}
	
	public void loadMembers(){
		Log.i("Members","Load Members");
    	try{
    		key=getIntent().getStringExtra("key");
			JSONObject request = ProxyUtils.proxyCall("queryUsersStatus",key);
			JSONObject rispo = request.getJSONObject("results");
			Log.i("Members","key: "+key+" Risposta: "+rispo);
			JSONArray online= rispo.getJSONArray("online");
			JSONArray offline= rispo.getJSONArray("offline");
			List<ViewMembers> members=new LinkedList<ViewMembers>();
			List<Long> usersKeys=new LinkedList<Long>();
			//load members status
			for(int i=0;i<online.length();i++){
				long onlineMemberKey = (long) online.getInt(i);
				members.add(new ViewMembers(onlineMemberKey,"online"));
			}
			for(int j=0;j<offline.length();j++){
				long offlineMemberKey = (long) offline.getInt(j);
				members.add(new ViewMembers(offlineMemberKey,"offline"));
			}
			
			
			if(members.isEmpty())
				Toast.makeText(this, "No Members on this table", Toast.LENGTH_LONG).show(); 
			else{
				//load members attributes
				members=loadUsers(members);
			}
				
			//load data on adapter
			array=new MembersAdapter(this,members);
			setListAdapter(array);			
		} catch (Exception e) {	Log.i("Eccezione", e.toString());}
	}
	
	/**
	 * @param prevMembers, a list of ViewMembers they have only the status
	 * @return  a list of ViewMembers they have all attribute full
	 */
	public List<ViewMembers> loadUsers(List<ViewMembers> members){
		//get users keys
		List<Long> usersKeys=new ArrayList<Long>();
		for (ViewMembers m : members){
			usersKeys.add(m.getKey());
		}
		
		//JSON request for user name
		JSONArray users=null;
		Log.i("Members","usersKeys: "+usersKeys);
		try{
			//Proxy request
			JSONObject request = ProxyUtils.proxyCall("queryUsers",usersKeys);
			users = request.getJSONArray("results");
			Log.i("Members","Users: "+users);
		
			//load data on the return list
			for(int i=0;i<users.length();i++){
				JSONObject jo = users.getJSONObject(i);
				String email=jo.getString("email");
				
				String newMail=ProxyUtils.clearMail(email);
				Log.i("Members","email: "+newMail);
				ViewMembers current=members.get(i);
				current.setEmail(newMail);
			}
		}catch(Exception e){Log.i("Eccezione", e.toString());}
		return members;
	}
	

}