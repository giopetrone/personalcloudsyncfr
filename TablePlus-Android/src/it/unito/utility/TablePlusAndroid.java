package it.unito.utility;

import android.app.Application;

/**
 * Salva i dati dell'utente in nel contesto dell'applicazione
 */
public class TablePlusAndroid extends Application{
	private Long userKey;
	private String userName;

	   
	public Long getUserKey(){ return userKey;}
	
	public void setUserKey(Long key){ userKey=key;}

	public String getUserName() {return userName;}

	public void setUserName(String userName) {this.userName = userName;}
	
	
	
}
