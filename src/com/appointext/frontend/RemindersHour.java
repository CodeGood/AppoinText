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

public class RemindersHour extends Activity {
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		String[] event;
		Calendar c = Calendar.getInstance();
		Date today = c.getTime();
		DateFormat dateFormat = new SimpleDateFormat("dd/MM/yyyy");
		String todayAsString = dateFormat.format(today);
		int startHour = c.get(Calendar.HOUR_OF_DAY);
		Log.i("startHour", Integer.toString(startHour));
		int startMinute = c.get(Calendar.MINUTE);
		String endTime = "", startTime = "";
		if((startHour + 1) == 24)	{
			Log.d("AppoinText", "Inside 24 Hour Condition");
			String endMinute = Integer.toString(startMinute);
			c.add(Calendar.DAY_OF_YEAR, 1);
			Date tomorrow = c.getTime();
			if(startMinute < 10)	{
				endTime = dateFormat.format(tomorrow) + " 00:0" + endMinute;
				startTime = todayAsString + " " + Integer.toString(startHour) + ":0" + Integer.toString(startMinute);
			}
			else	{
				endTime = dateFormat.format(tomorrow) + " 00:" + endMinute;
				startTime = todayAsString + " " + Integer.toString(startHour) + ":" + Integer.toString(startMinute);
			}
			event = ((GetCalendarEvents.getEvent(this, startTime, endTime, new String[] {Events.TITLE})).replaceAll(",","\t")).split("#");
			LinearLayout text = (LinearLayout) findViewById(R.id.remindersDisplay);
			LinearLayout.LayoutParams params = new LinearLayout.LayoutParams(
					LinearLayout.LayoutParams.MATCH_PARENT,
					LinearLayout.LayoutParams.WRAP_CONTENT);
			if(event.length == 1 && event[0].equals(""))	{
				AlertDialog alertDialog = new AlertDialog.Builder(
						RemindersHour.this).create();
				alertDialog.setTitle("Reminders");
				alertDialog.setMessage("No Reminders Set!");
				alertDialog.show();
			}
			else	{
				for(String events : event){
					Button btn = new Button(this);
					btn.setClickable(false);
					btn.setText(events);
					text.addView(btn, params);
				}
			}
		}
		else	{
				String hourStart, minuteStart, hourEnd;
				if(startHour < 10)
					hourStart = "0" + startHour;
				else
					hourStart = Integer.toString(startHour);
				if(startMinute < 10)
					minuteStart = "0" + startMinute;
				else
					minuteStart = Integer.toString(startMinute);
				if(startHour + 1 < 10)
					hourEnd = "0" + (startHour + 1);
				else
					hourEnd = Integer.toString(startHour + 1);
				startTime = todayAsString + " " + hourStart + ":" + minuteStart;
				endTime = todayAsString + " " + hourEnd + ":" + minuteStart;
			event = ((GetCalendarEvents.getEvent(this, startTime, endTime, new String[] {Events.TITLE})).replaceAll(",","\t")).split("#");
		}
		super.onCreate(savedInstanceState);
		setContentView(R.layout.remindersdisplay);
		LinearLayout text = (LinearLayout) findViewById(R.id.remindersDisplay);
		LinearLayout.LayoutParams params = new LinearLayout.LayoutParams(
				LinearLayout.LayoutParams.MATCH_PARENT,
				LinearLayout.LayoutParams.WRAP_CONTENT);
		int i = 0;
		if(event.length == 1 && event[0].equals(""))	{
			AlertDialog alertDialog = new AlertDialog.Builder(
					RemindersHour.this).create();
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
