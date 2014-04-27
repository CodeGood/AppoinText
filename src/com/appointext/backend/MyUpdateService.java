package com.appointext.backend;

//FIXME - Check if this actually works 

import android.app.AlarmManager;
import android.app.PendingIntent;
import android.app.Service;
import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.os.Handler;
import android.os.IBinder;
import android.text.format.DateUtils;
import android.text.format.Time;
import android.util.Log;

public class MyUpdateService extends Service {


	@Override
	public IBinder onBind(Intent arg0) {
		// TODO Auto-generated method stub
		return null;
	}
	
	public void onStart(Intent intent, int startID) {
		
		Log.d("AppoinTextUpdateService", "Started");
		smsObserver = new SMSListenerSent(new Handler(), this);
		this.getContentResolver().registerContentObserver(Uri.parse("content://sms/"), true, smsObserver);
		//scheduleNextUpdate();
		
	}
	
	public int onStartCommand(Intent intent, int flags, int startID) {
		
		Log.d("AppoinText Service", "Registering content observer");
		if (smsObserver == null) smsObserver = new SMSListenerSent(new Handler(), this);
		this.getContentResolver().registerContentObserver(Uri.parse("content://sms/"), true, smsObserver);
		//scheduleNextUpdate();

		
		return START_STICKY;
		
	}
	
	public void onDestroy() {
		this.getContentResolver().unregisterContentObserver(smsObserver);
		scheduleNextUpdate();
		Log.d("AppoinText", "Buh bye :( I was killed by this merciless phone :-/");
		super.onDestroy();
	}
	
	private SMSListenerSent smsObserver = null;
	
/*  public MyUpdateService() {
	super(MyUpdateService.class.getSimpleName());
  }

  @Override
  protected void onHandleIntent(Intent intent) {
	  
	  smsObserver = new SMSListenerSent(new Handler(), this);
	  
Log.d("AppoinText UpdateService", "Started. Registering ContentObserver");

	  this.getContentResolver().registerContentObserver(Uri.parse("content://sms/"), true, smsObserver);
	  scheduleNextUpdate();
	  
  }*/

  private void scheduleNextUpdate()  {
	  
    Intent intent = new Intent(this, this.getClass());
    PendingIntent pendingIntent = PendingIntent.getService(this, 0, intent, PendingIntent.FLAG_UPDATE_CURRENT);

    // The update frequency should often be user configurable.  This is not.

    long currentTimeMillis = System.currentTimeMillis();
    long nextUpdateTimeMillis = currentTimeMillis + 5*DateUtils.SECOND_IN_MILLIS; //DateUtils.MINUTE_IN_MILLIS; //Updates every 1 minute.
    Time nextUpdateTime = new Time();
    nextUpdateTime.set(nextUpdateTimeMillis);

    if (nextUpdateTime.hour < 8 || nextUpdateTime.hour >= 18)
    {
      nextUpdateTime.hour = 8;
      nextUpdateTime.minute = 0;
      nextUpdateTime.second = 0;
      nextUpdateTimeMillis = nextUpdateTime.toMillis(false) + DateUtils.DAY_IN_MILLIS;
    }
    
    AlarmManager alarmManager = (AlarmManager) getSystemService(Context.ALARM_SERVICE);
    alarmManager.set(AlarmManager.RTC, nextUpdateTimeMillis, pendingIntent);
  }

  
}