package com.appointext.frontend;

import com.appointext.database.GetCalendarEvents;
import com.bmsce.appointext.R;

import android.app.Activity;
import android.os.Bundle;
import android.provider.CalendarContract.Events;
import android.widget.TextView;

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
		String event = ((GetCalendarEvents.getEvent(this, startingTime, endingTime, new String[] {Events.TITLE})).replaceAll(",","\t")).replaceAll("#","\n");
		super.onCreate(savedInstanceState);
		setContentView(R.layout.remindersdisplay);
		TextView text = (TextView) findViewById(R.id.remindersDisplay);
		text.setText(event);
	}
}
