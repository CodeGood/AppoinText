package com.appointext.frontend;

import java.util.ArrayList;
import com.appointext.database.DatabaseManager;
import com.bmsce.appointext.R;
import android.app.Activity;
import android.os.Bundle;
import android.widget.TimePicker;
import android.widget.TimePicker.OnTimeChangedListener;

public class TimeSelector extends Activity {

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.time_picker);

		TimePicker MorningTime;
		TimePicker EveningTime;
		TimePicker NightTime;
		TimePicker AfternoonTime;
		
		MorningTime = (TimePicker)findViewById(R.id.timePickerMorning);
		AfternoonTime = (TimePicker)findViewById(R.id.timePickerAfternoon);
		EveningTime = (TimePicker)findViewById(R.id.timePickerEvening);
		NightTime = (TimePicker)findViewById(R.id.timePickerNight);

		//Setting the time for the morning clock

		ArrayList<Object> returnedMorningTime;
		final DatabaseManager db = new DatabaseManager(TimeSelector.this);
		db.open();
		returnedMorningTime = db.getRowAsArray("settingsTable", "DayTimeMorning");
		if(!returnedMorningTime.isEmpty())	{
			String[] time = (returnedMorningTime.get(1)).toString().split(":");
			MorningTime.setCurrentHour(Integer.parseInt(time[0]));
			MorningTime.setCurrentMinute(Integer.parseInt(time[1]));
		}
		else	{
			MorningTime.setCurrentHour(8);
			MorningTime.setCurrentMinute(00);
		}
		db.close();
		
		//Setting the time for the afternoon clock

				ArrayList<Object> returnedAfternoonTime;
				db.open();
				returnedAfternoonTime = db.getRowAsArray("settingsTable", "DayTimeMorning");
				if(!returnedAfternoonTime.isEmpty())	{
					String[] time = (returnedAfternoonTime.get(1)).toString().split(":");
					AfternoonTime.setCurrentHour(Integer.parseInt(time[0]));
					AfternoonTime.setCurrentMinute(Integer.parseInt(time[1]));
				}
				else	{
					AfternoonTime.setCurrentHour(13);
					AfternoonTime.setCurrentMinute(00);
				}
				db.close();

		//Setting the clock for the evening clock

		ArrayList<Object> returnedEveningTime;
		db.open();
		returnedEveningTime = db.getRowAsArray("settingsTable", "DayTimeEvening");
		if(!returnedEveningTime.isEmpty())	{
			String[] time = (returnedEveningTime.get(1)).toString().split(":");
			EveningTime.setCurrentHour(Integer.parseInt(time[0]));
			EveningTime.setCurrentMinute(Integer.parseInt(time[1]));
		}
		else	{
			EveningTime.setCurrentHour(18);
			EveningTime.setCurrentMinute(00);
		}
		db.close();

		//Setting the clock for the night clock

		ArrayList<Object> returnedNightTime;
		db.open();
		returnedNightTime = db.getRowAsArray("settingsTable", "DayTimeNight");
		if(!returnedNightTime.isEmpty())	{
			String[] time = (returnedNightTime.get(1)).toString().split(":");
			NightTime.setCurrentHour(Integer.parseInt(time[0]));
			NightTime.setCurrentMinute(Integer.parseInt(time[1]));
		}
		else	{
			NightTime.setCurrentHour(21);
			NightTime.setCurrentMinute(00);
		}
		db.close();


		//Listener for the morning time

		MorningTime.setOnTimeChangedListener(new OnTimeChangedListener(){

			@Override
			public void onTimeChanged(TimePicker view, int hourOfDay, int minute) {
				String time = hourOfDay + ":" + minute;

				//Updating the Morning Time
				db.open();
				ArrayList<Object> morningtime;
				morningtime = db.getRowAsArray("settingsTable", "DayTimeMorning");
				if(morningtime.isEmpty())
					db.addRow("settingsTable", "DayTimeMorning", time);
				else
					db.updateRow("settingsTable", "DayTimeMorning", time);
				db.close();
			}});
		
		//Listener for the afternoon time

				AfternoonTime.setOnTimeChangedListener(new OnTimeChangedListener(){

					@Override
					public void onTimeChanged(TimePicker view, int hourOfDay, int minute) {
						String time = hourOfDay + ":" + minute;

						//Updating the Afternoon Time
						db.open();
						ArrayList<Object> afternoontime;
						afternoontime = db.getRowAsArray("settingsTable", "DayTimeAfternoon");
						if(afternoontime.isEmpty())
							db.addRow("settingsTable", "DayTimeAfternoon", time);
						else
							db.updateRow("settingsTable", "DayTimeAfternoon", time);
						db.close();
					}});


		//Listening for the evening time

		EveningTime.setOnTimeChangedListener(new OnTimeChangedListener(){

			@Override
			public void onTimeChanged(TimePicker view, int hourOfDay, int minute) {
				String time = hourOfDay + ":" + minute;

				//Updating DB for Evening Time
				db.open();
				ArrayList<Object> eveningtime;
				eveningtime = db.getRowAsArray("settingsTable", "DayTimeEvening");
				if(eveningtime.isEmpty())
					db.addRow("settingsTable", "DayTimeEvening", time);
				else
					db.updateRow("settingsTable", "DayTimeEvening", time);
				db.close();
			}});


		//Listening for the Night Time
		NightTime.setOnTimeChangedListener(new OnTimeChangedListener(){

			@Override
			public void onTimeChanged(TimePicker view, int hourOfDay, int minute) {
				String time = hourOfDay + ":" + minute;

				//Saving it in the DB for night time
				db.open();
				ArrayList<Object> nighttime;
				nighttime = db.getRowAsArray("settingsTable", "DayTimeNight");
				if(nighttime.isEmpty())
					db.addRow("settingsTable", "DayTimeNight", time);
				else
					db.updateRow("settingsTable", "DayTimeNight", time);
				db.close();

			}});

	}


}
