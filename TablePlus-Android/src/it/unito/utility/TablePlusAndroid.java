package it.unito.utility;

import android.app.Application;

//Save User Data in  application's context
public class TablePlusAndroid extends Application{
	private Long userKey;
	private String userName;
	private Long currentTableKey;

	   
	public Long getCurrentTableKey() {
		return currentTableKey;
	}

	public void setCurrentTableKey(Long currentTableKey) {
		this.currentTableKey = currentTableKey;
	}

	public Long getUserKey(){ return userKey;}
	
	public void setUserKey(Long key){ userKey=key;}

	public String getUserName() {return userName;}

	public void setUserName(String userName) {this.userName = userName;}
	
	
	
}
