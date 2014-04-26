package com.appointext.frontend;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;

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

public class RemindersToday extends Activity {
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		Calendar c= Calendar.getInstance();
		Date today = c.getTime();
		DateFormat dateFormat = new SimpleDateFormat("dd/MM/yyyy");
		String todayAsString = dateFormat.format(today);
		Log.i("Today Date",todayAsString);
		String startTime = todayAsString + " 00:00";
		String endTime = todayAsString + " 23:59";
		Log.i("Start Time",startTime);
		Log.i("End Time",endTime);
		String[] event = GetCalendarEvents.getEvent(this, startTime, endTime, new String[] {Events._ID, Events.TITLE}).split("#");
		super.onCreate(savedInstanceState);
		setContentView(R.layout.remindersdisplay);
		LinearLayout text = (LinearLayout) findViewById(R.id.remindersDisplay);
		LinearLayout.LayoutParams params = new LinearLayout.LayoutParams(
				LinearLayout.LayoutParams.MATCH_PARENT,
				LinearLayout.LayoutParams.WRAP_CONTENT);
		int i = 0;
		if(event.length == 1 && event[0].equals(""))	{
			AlertDialog alertDialog = new AlertDialog.Builder(
					RemindersToday.this).create();
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
						
						Log.d("AppoinTextReminder", "Touched my dear sir, touched!" + parts[1]);
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
