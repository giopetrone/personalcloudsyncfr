package com.vimaltuts.android.notificationexample;

import android.app.Activity;
import android.app.Notification;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;

public class NotificationExampleActivity extends Activity {

	  private static final int NOTIFY_ME_ID=1986;
	  private int count=0;
	  private NotificationManager mgr=null;
	  
	  @Override
		public void onCreate(Bundle savedInstanceState) {
			super.onCreate(savedInstanceState);
			setContentView(R.layout.main);
			System.out.println("1!");

			mgr = (NotificationManager) getSystemService(NOTIFICATION_SERVICE);
			System.out.println("2!");
		}
	  
	  public void notifyMe(View v) {
		  long[] pattern = new long[] { 500L, 200L, 200L, 500L };
			Context contesto = getApplicationContext();
			Notification.Builder builder2 = new Notification.Builder(contesto);
			builder2.setContentTitle("Table+!!!");
			builder2.setContentText("Hai una nuova richiesta");
			//builder2.setContentInfo("info");
			builder2.setSmallIcon(R.drawable.stat_notify_chat);

			builder2.setAutoCancel(true);
			builder2.setVibrate(pattern);
			builder2.setNumber(++count);
			//builder2.set
			// builder2.set...

			Notification note = builder2.getNotification();
			//note.flags|=Notification.FLAG_AUTO_CANCEL;
			
			
			System.out.println("notifica!");
			// mgr.notify(NOTIFY_ME_ID, note);
			 mgr.notify(NOTIFY_ME_ID, note);
			System.out.println("notifica2!");
	  }
	  
	  public void clearNotification(View v) {
	    mgr.cancel(NOTIFY_ME_ID);
	  }
	}