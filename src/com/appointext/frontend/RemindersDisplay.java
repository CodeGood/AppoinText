package com.appointext.frontend;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;

import com.appointext.database.GetCalendarEvents;
import com.bmsce.appointext.R;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.provider.CalendarContract.Events;
import android.util.Log;
import android.view.View;

@SuppressLint("SimpleDateFormat")
public class RemindersDisplay extends Activity{
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.reminders);
	}

	public void viewRemindersToday(View V)	{
		Calendar c= Calendar.getInstance();
		Date today = c.getTime();
		DateFormat dateFormat = new SimpleDateFormat("dd/MM/yyyy");
		String todayAsString = dateFormat.format(today);
		Log.i("Today Date",todayAsString);
		String startTime = todayAsString + " 00:00";
		String endTime = todayAsString + " 23:59";
		String event = ((GetCalendarEvents.getEvent(this, startTime, endTime, new String[] {Events.TITLE})).replaceAll(",","\t")).replaceAll("#","\n");
		Log.i("Reminders check", event);
		Log.i("Reminders Start Time ", startTime);
		Log.i("Reminders End Time", endTime);
	}

	public void viewRemindersHour(View V)	{
		Calendar c = Calendar.getInstance();
		Date today = c.getTime();
		DateFormat dateFormat = new SimpleDateFormat("dd/MM/yyyy");
		String todayAsString = dateFormat.format(today);
		int startHour = c.get(Calendar.HOUR_OF_DAY);
		Log.i("startHour", Integer.toString(startHour));
		int startMinute = c.get(Calendar.MINUTE);
		if((startHour + 1) == 24)	{
			Log.d("AppoinText", "Inside 24 Hour Condition");
			String endMinute = Integer.toString(startMinute);
			c.add(Calendar.DAY_OF_YEAR, 1);
			Date tomorrow = c.getTime();
			String endTime, startTime;
			if(startMinute < 10)	{
				endTime = dateFormat.format(tomorrow) + " 00:0" + endMinute;
				startTime = todayAsString + " " + Integer.toString(startHour) + ":0" + Integer.toString(startMinute);
			}
			else	{
				endTime = dateFormat.format(tomorrow) + " 00:" + endMinute;
				startTime = todayAsString + " " + Integer.toString(startHour) + ":" + Integer.toString(startMinute);
			}
			Log.i("End Time",endTime);
			Log.i("Start Time",startTime);
			String event = ((GetCalendarEvents.getEvent(this, startTime, endTime, new String[] {Events.TITLE})).replaceAll(",","\t")).replaceAll("#","\n");
		}
		else	{
			String endTime, startTime;
			if(startMinute < 10)	{
				endTime = todayAsString + " " + Integer.toString(startHour + 1) + ":0" + Integer.toString(startMinute);
				startTime = todayAsString + " " + Integer.toString(startHour) + ":0" + Integer.toString(startMinute);
			}
			else	{
				endTime = todayAsString + " " + Integer.toString(startHour + 1) + ":" + Integer.toString(startMinute);
				startTime = todayAsString + " " + Integer.toString(startHour) + ":" + Integer.toString(startMinute);
			}
			String event = ((GetCalendarEvents.getEvent(this, startTime, endTime, new String[] {Events.TITLE})).replaceAll(",","\t")).replaceAll("#","\n");
		}

	}

	public void viewRemindersPeriod(View V)	{
		Intent intent = new Intent(this, ReminderPeriodPicker.class);
		startActivity(intent); 

	}
}
