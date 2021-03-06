package com.appointext.frontend;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.provider.CalendarContract.Events;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.LinearLayout;

import com.appointext.database.GetCalendarEvents;
import com.bmsce.appointext.R;

public class RemindersPeriodDisplay extends Activity {
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		String startingTime;
		Bundle extras;
		if (savedInstanceState == null) {
			extras = getIntent().getExtras();
			if(extras == null) {
				startingTime= null;
			} 
			else {
				startingTime= extras.getString("startTime");
			}
		} 
		else {
			startingTime= (String) savedInstanceState.getSerializable("startTime");
		}
		String endingTime;
		if (savedInstanceState == null) {
			extras = getIntent().getExtras();
			if(extras == null) {
				endingTime= null;
			} 
			else {
				endingTime= extras.getString("endTime");
			}
		} 
		else {
			endingTime= (String) savedInstanceState.getSerializable("endTime");
		}
		
		String[] event = GetCalendarEvents.getEvent(this, startingTime, endingTime, new String[] {Events._ID, Events.TITLE}).split("#");//).replaceAll(",","\t")).split("#");
		
		super.onCreate(savedInstanceState);
		setContentView(R.layout.remindersdisplay);
		LinearLayout text = (LinearLayout) findViewById(R.id.remindersDisplay);
		LinearLayout.LayoutParams params = new LinearLayout.LayoutParams(
				LinearLayout.LayoutParams.MATCH_PARENT,
				LinearLayout.LayoutParams.WRAP_CONTENT);
		int i = 0;
		if(event.length == 1 && event[0].equals(""))	{
			AlertDialog alertDialog = new AlertDialog.Builder(
					RemindersPeriodDisplay.this).create();
			alertDialog.setTitle("Reminders");
			alertDialog.setMessage("No Reminders Set!");
			alertDialog.show();
		}
		else	{
			for(String events : event){
				Button btn = new Button(this);
				btn.setId(i);

				/*EDIT STARTS */
				
				final String[] parts = events.split(",");
				
				btn.setClickable(true); //make it clickable.
				btn.setOnClickListener(new View.OnClickListener() { //Set what to do on touch!
					@Override
					public void onClick(View v) { //Raise an Intent for the Calendar to handle.
						
						Log.d("AppoinTextReminder", "Touched my dear sir, touched! " + parts[1]);
						Intent intent = new Intent(Intent.ACTION_VIEW);
						Uri.Builder uri = Events.CONTENT_URI.buildUpon();
						uri.appendPath(parts[0]);
						intent.setData(uri.build());
						startActivity(intent);
						
					}
				});
				
				btn.setText(parts[1]);
				
				/*EDIT ENDS */
				
				text.addView(btn, params);
			}
		}
		
	}
}
