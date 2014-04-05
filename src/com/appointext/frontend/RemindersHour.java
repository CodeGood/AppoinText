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

public class RemindersHour extends Activity {
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		String event;
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
			Log.i("End Time",endTime);
			Log.i("Start Time",startTime);
			event = ((GetCalendarEvents.getEvent(this, startTime, endTime, new String[] {Events.TITLE})).replaceAll(",","\t")).replaceAll("#","\n");
			Log.i("Called Event", event);
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
				
			Log.i("End Time",endTime);
			Log.i("Start Time",startTime);
			event = ((GetCalendarEvents.getEvent(this, startTime, endTime, new String[] {Events.TITLE})).replaceAll(",","\t")).replaceAll("#","\n");
			Log.i("Called Event", event);
		}
		super.onCreate(savedInstanceState);
		setContentView(R.layout.remindersdisplay);
		TextView text = (TextView) findViewById(R.id.remindersDisplay);
		text.setText(event);
	}

}
