package com.appointext.frontend;

import com.bmsce.appointext.R;
import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.DatePicker;
import android.widget.TimePicker;

public class ReminderPeriodPicker extends Activity {
	String startT, endT;
	String startD, endD;
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.reminderperiod);
	}

	public void ShowReminders(View V)	{

		TimePicker startTime;
		TimePicker endTime;
		DatePicker startDate;
		DatePicker endDate;

		startTime = (TimePicker)findViewById(R.id.startTimeReminders);
		endTime = (TimePicker)findViewById(R.id.endTimeReminders);
		startDate = (DatePicker)findViewById(R.id.startDateReminders);
		endDate = (DatePicker)findViewById(R.id.endDateReminders);

		startTime.clearFocus();
		int startHour = startTime.getCurrentHour();
		int startMinute = startTime.getCurrentMinute();

		endTime.clearFocus();
		int endHour = endTime.getCurrentHour();
		int endMinute = endTime.getCurrentMinute();

		int startingDate = startDate.getDayOfMonth();
		int startingMonth = startDate.getMonth() + 1;
		int startingYear = startDate.getYear();

		int endingDate = endDate.getDayOfMonth();
		int endingMonth = endDate.getMonth() + 1;
		int endingYear = endDate.getYear();
		String hourStart,minuteStart,hourEnd,minuteEnd,dateStarting,monthStarting, dateEnding, monthEnding;
		String starting, ending;
		if(startHour < 10)
			hourStart = "0" + startHour;
		else
			hourStart = Integer.toString(startHour);
		if(startMinute < 10)
			minuteStart = "0" + startMinute;
		else
			minuteStart = Integer.toString(startMinute);
		if(endHour < 10)
			hourEnd = "0" + endHour;
		else
			hourEnd = Integer.toString(endHour);
		if(endMinute < 10)
			minuteEnd = "0" + endMinute;
		else
			minuteEnd = Integer.toString(endMinute);
		if(startingDate < 10)
			dateStarting = "0" + startingDate;
		else
			dateStarting = Integer.toString(startingDate);
		if(startingMonth < 10)
			monthStarting = "0" + startingMonth;
		else
			monthStarting = Integer.toString(startingMonth);
		if(endingDate < 10)
			dateEnding = "0" + endingDate;
		else
			dateEnding = Integer.toString(endingDate);
		if(endingMonth < 10)
			monthEnding = "0" + endingMonth;
		else
			monthEnding = Integer.toString(endingMonth);
		
		String dateOfStart = dateStarting + "/" + monthStarting + "/" + Integer.toString(startingYear);
		String dateOfEnd = dateEnding + "/" + monthEnding + "/" + Integer.toString(endingYear);
		String timeOfStart = hourStart + ":" + minuteStart;
		String timeOfEnd = hourEnd + ":" + minuteEnd;
		
		starting = dateOfStart + " " + timeOfStart;
		ending = dateOfEnd + " " + timeOfEnd;
		
		Log.i("Start Time",starting);
		Log.i("End Time",ending);

		Intent intent = new Intent(this, RemindersPeriodDisplay.class);
		intent.putExtra("startTime", starting);
		intent.putExtra("endTime", ending);

		startActivity(intent);

	}
}