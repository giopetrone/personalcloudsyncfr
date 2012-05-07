package it.unito.gui;


import android.app.Activity;
import android.os.Bundle;
import android.widget.Toast;

public class InsertMessage extends Activity{
	
	public void onCreate(Bundle savedInstanceState)
	{
		super.onCreate(savedInstanceState);
		Toast.makeText(this, "Sei in NewTable", Toast.LENGTH_LONG).show();
	}

}
