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
import android.widget.Toast;

@SuppressLint("SimpleDateFormat")
public class RemindersDisplay extends Activity{
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.reminders);
	}

	public void viewRemindersToday(View V)	{
		Intent intent = new Intent(this, RemindersToday.class);
		startActivity(intent); 
	}

	public void viewRemindersHour(View V)	{
		Intent intent = new Intent(this, RemindersHour.class);
		startActivity(intent);

	}

	public void viewRemindersPeriod(View V)	{
		Intent intent = new Intent(this, ReminderPeriodPicker.class);
		startActivity(intent); 

	}
}
