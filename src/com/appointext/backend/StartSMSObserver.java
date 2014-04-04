package com.appointext.backend;

//FIXME - Check if this actually works

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.util.Log;


public class StartSMSObserver extends BroadcastReceiver {

	@Override
	public void onReceive(Context context, Intent intent) {
		Intent startServiceIntent = new Intent(context, MyUpdateService.class);
        context.startService(startServiceIntent);
        Log.d("AppoinText Start Update", "Boot signal received. Started Update Scheduler");
	}

}
