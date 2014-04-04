package com.appointext.backend;

//FIXME - Check if this actually works 

import android.app.AlarmManager;
import android.app.IntentService;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.os.Handler;
import android.text.format.DateUtils;
import android.text.format.Time;
import android.util.Log;

public class MyUpdateService extends IntentService {

  public MyUpdateService() {
	super(MyUpdateService.class.getSimpleName());
  }

  @Override
  protected void onHandleIntent(Intent intent) {
	  
	  SMSListenerSent smsObserver = new SMSListenerSent(new Handler(), this);
	  
Log.d("AppoinText UpdateService", "Started. Registering ContentObserver");

	  this.getContentResolver().registerContentObserver(Uri.parse("content://sms/"), true, smsObserver);
	  scheduleNextUpdate();
	  
  }

  private void scheduleNextUpdate()  {
	  
    Intent intent = new Intent(this, this.getClass());
    PendingIntent pendingIntent = PendingIntent.getService(this, 0, intent, PendingIntent.FLAG_UPDATE_CURRENT);

    // The update frequency should often be user configurable.  This is not.

    long currentTimeMillis = System.currentTimeMillis();
    long nextUpdateTimeMillis = currentTimeMillis + 10* DateUtils.MINUTE_IN_MILLIS; //Updates every 1 minute.
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