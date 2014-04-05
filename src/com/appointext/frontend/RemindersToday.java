package com.appointext.frontend;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;

import com.appointext.database.GetCalendarEvents;
import com.bmsce.appointext.R;

import android.app.Activity;
import android.os.Bundle;
import android.provider.CalendarContract.Events;
import android.util.Log;
import android.widget.TextView;

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
		String event = ((GetCalendarEvents.getEvent(this, startTime, endTime, new String[] {Events.TITLE})).replaceAll(",","\t")).replaceAll("#","\n");
		super.onCreate(savedInstanceState);
		setContentView(R.layout.remindersdisplay);
		TextView text = (TextView) findViewById(R.id.remindersDisplay);
		text.setText(event);
	}
	}
