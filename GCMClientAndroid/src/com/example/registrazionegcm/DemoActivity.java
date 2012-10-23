package com.example.registrazionegcm;

import static com.example.registrazionegcm.CommonUtilities.SENDER_ID;

import com.google.android.gcm.GCMRegistrar;

import android.os.Bundle;
import android.app.Activity;
import android.util.Log;
import android.view.Menu;
import android.widget.TextView;

public class DemoActivity extends Activity {
	private String TAG = "** pushAndroidActivity **";
	private TextView mDisplay;
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        GCMRegistrar.checkDevice(this);
        GCMRegistrar.checkManifest(this);
        
        setContentView(R.layout.main);
		mDisplay = (TextView) findViewById(R.id.display);
		mDisplay.setText("ciao");
		final String regId = GCMRegistrar.getRegistrationId(this);
		Log.i(TAG, "registration id =====&nbsp; " + regId);

		if (regId.equals("")) {
			GCMRegistrar.register(this, SENDER_ID);
		
			
		} else {
			Log.v(TAG, "Already registered");
		}

		String registrazioneId = GCMRegistrar.getRegistrationId(this);
		System.out.println("reg id " + registrazioneId);
		mDisplay.setText("Id registrazione --->>>>>>>>>>>>> "+ regId);

    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.main, menu);
        return true;
    }
}
