package com.appointext.backend;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;

public class SMSListenerInbox extends BroadcastReceiver {

	@Override
	public void onReceive(Context context, Intent intent) {
		
		Bundle bundle = intent.getExtras();
		Intent i = new Intent(context, AppoinTextService.class);
		
Log.d("AppoinText InboxListener", "Came to inbox listener");

		i.putExtras(bundle);
		i.putExtra("origin", "inbox");
		context.startService(i); 
        
	}

}