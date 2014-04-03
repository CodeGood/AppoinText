package com.appointext.backend;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;

public class SMSListenerSent extends BroadcastReceiver {

	@Override
	public void onReceive(Context context, Intent intent) {
		
		Bundle bundle = intent.getExtras();
		Intent i = new Intent(context, AppoinTextService.class);
		i.putExtras(bundle);
		i.putExtra("origin", "sent");
		context.startService(i); 
        
	}

}
