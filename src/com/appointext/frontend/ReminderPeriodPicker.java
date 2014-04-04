package com.appointext.frontend;

import java.util.Calendar;

import com.bmsce.appointext.R;

import android.app.Activity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.DatePicker;
import android.widget.DatePicker.OnDateChangedListener;
import android.widget.TimePicker;
import android.widget.TimePicker.OnTimeChangedListener;

public class ReminderPeriodPicker extends Activity {
	String startT, endT;
	String startD, endD;
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.reminderperiod);
		Calendar c = Calendar.getInstance();

		TimePicker startTime;
		TimePicker endTime;
		DatePicker startDate;
		DatePicker endDate;

		startTime = (TimePicker)findViewById(R.id.startTimeReminders);
		endTime = (TimePicker)findViewById(R.id.endTimeReminders);
		startDate = (DatePicker)findViewById(R.id.startDateReminders);
		endDate = (DatePicker)findViewById(R.id.endDateReminders);

		startTime.setOnTimeChangedListener(new OnTimeChangedListener(){

			@Override
			public void onTimeChanged(TimePicker view, int hourOfDay, int minute) {
				startT = new String(hourOfDay + ":" + minute);
			}
		});
		endTime.setOnTimeChangedListener(new OnTimeChangedListener(){

			@Override
			public void onTimeChanged(TimePicker view, int hourOfDay, int minute) {
				endT = new String(hourOfDay + ":" + minute);
			}
		});
		startDate.init(c.get(Calendar.YEAR), c.get(Calendar.MONTH), c.get(Calendar.DAY_OF_MONTH), new OnDateChangedListener(){

			public void onDateChanged(DatePicker view, int year, int monthOfYear,int dayOfMonth)	{
				startD = new String(Integer.toString(dayOfMonth) + "/" + Integer.toString(monthOfYear) + "/" + Integer.toString(year));
			}
		});

		endDate.init(c.get(Calendar.YEAR), c.get(Calendar.MONTH), c.get(Calendar.DAY_OF_MONTH), new OnDateChangedListener(){

			public void onDateChanged(DatePicker view, int year, int monthOfYear,int dayOfMonth)	{
				endD = new  String(Integer.toString(dayOfMonth) + "/" + Integer.toString(monthOfYear) + "/" + Integer.toString(year));
			}
		});
	}
	
	public void ShowReminders(View V)	{
		String start = startD + " " + startT;
		String end = endD + " " + endT;
		Log.i("Start Time",start);
		Log.i("End Time", end);
	}
}