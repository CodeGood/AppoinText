package com.appointext.frontend;

import java.text.SimpleDateFormat;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.text.Html;
import android.util.Log;
import android.widget.TextView;

import com.bmsce.appointext.R;


public class ConflictDetected extends Activity {

	@Override
	protected void onCreate(Bundle savedInstanceState) {

		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_appoin_text);
		
		TextView op = (TextView)findViewById(R.id.op);
		op.setText(Html.fromHtml("<b>AppoinText :: <u>Conflict Detected</u></b>"));	
		final Intent callingIntent = getIntent();
		op.append("\n\nYou seem to have scheduled two meetings at the same time, or very near to each other\n");
		op.append("A reminder for your last message was not set.\n");
		
		Log.d("AppoinTextConflict", "Called activity");
		
		long tOld = callingIntent.getLongExtra("timeOld", 0);
		long tNew = callingIntent.getLongExtra("timeNew", 0);
		SimpleDateFormat formatter = new SimpleDateFormat("dd/MM/yyyy HH:mm");
		String timeOld = formatter.format(tOld);
		String timeNew = formatter.format(tNew);
		
		op.append("\n\nYou attempted to schedule a " + callingIntent.getStringExtra("titleNew") + " with " + callingIntent.getStringExtra("attendeesNew") + " at " + timeNew + "\n\n");
		op.append("But you already have " + callingIntent.getStringExtra("titleOld") + " scheduled with " + callingIntent.getStringExtra("attendeesOld") + " at " + timeOld + "\n");
		op.append("\n\nPlease reschedule your appointments manually via the calendar app.");
		
	}
	
}
