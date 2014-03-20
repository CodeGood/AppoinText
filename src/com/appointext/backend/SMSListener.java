package com.appointext.backend;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.util.Log;

public class SMSListener extends BroadcastReceiver {

	@Override
	public void onReceive(Context context, Intent intent) {

		Log.i("AppoinTextListener", "Attempting to start service");
		
		Intent i = new Intent(context, AppoinTextService.class);
		context.startService(i); 
        
	}

}
