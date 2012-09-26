package com.vimaltuts.android.notificationexample;

import android.app.Activity;
import android.os.Bundle;
import android.widget.TextView;

public class NotificationMessage extends Activity {
	
	 public void onCreate(Bundle savedInstanceState) {
		    super.onCreate(savedInstanceState);
		    
		    TextView txt=new TextView(this);
		    
		    txt.setText("This is the message!");
		    setContentView(txt);
		  }


}
