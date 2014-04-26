package com.appointext.frontend;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;

import android.app.Activity;
import android.app.AlertDialog;
import android.os.Bundle;
import android.provider.CalendarContract.Events;
import android.util.Log;
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
		String[] event = ((GetCalendarEvents.getEvent(this, startTime, endTime, new String[] {Events.TITLE})).replaceAll(",","\t")).split("#");
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
				btn.setClickable(false);
				btn.setText(events);
				text.addView(btn, params);
			}
		}

	}
}
