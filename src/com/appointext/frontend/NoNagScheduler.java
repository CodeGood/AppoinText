package com.appointext.frontend;

import java.util.ArrayList;
import java.util.Calendar;

import com.appointext.database.DatabaseManager;
import com.bmsce.appointext.R;

import android.app.Activity;
import android.os.Bundle;
import android.widget.TimePicker;
import android.widget.TimePicker.OnTimeChangedListener;

public class NoNagScheduler extends Activity{
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.nonagscheduler);
		
		TimePicker startTime;
		TimePicker endTime;
		
		startTime = (TimePicker)findViewById(R.id.NoNagSchedulerStartTime);
		endTime = (TimePicker)findViewById(R.id.NoNagSchedulerEndTime);

		ArrayList<Object> returnedTime;
		DatabaseManager db = new DatabaseManager(NoNagScheduler.this);
		db.open();
		returnedTime = db.getRowAsArray("settingsTable", "NoNagSchedulerStartTime");
		if(!returnedTime.isEmpty())	{
			String[] time = (returnedTime.get(1)).toString().split(":");
			startTime.setCurrentHour(Integer.parseInt(time[0]));
			startTime.setCurrentMinute(Integer.parseInt(time[1]));
		}
		else	{
			Calendar c = Calendar.getInstance();
			startTime.setCurrentHour(c.get(Calendar.HOUR));
			startTime.setCurrentMinute(c.get(Calendar.MINUTE));
		}

		returnedTime = db.getRowAsArray("settingsTable", "NoNagSchedulerEndTime");
		db.close();
		if(!returnedTime.isEmpty())	{
			String[] time = (returnedTime.get(1)).toString().split(":");
			endTime.setCurrentHour(Integer.parseInt(time[0]));
			endTime.setCurrentMinute(Integer.parseInt(time[1]));
		}
		else	{
			Calendar c = Calendar.getInstance();
			endTime.setCurrentHour(c.get(Calendar.HOUR));
			endTime.setCurrentMinute(c.get(Calendar.MINUTE));
		}
		
		
		
		startTime.setOnTimeChangedListener(new OnTimeChangedListener(){

			   @Override
			   public void onTimeChanged(TimePicker view, int hourOfDay, int minute) {
				   String time = hourOfDay + ":" + minute;
				   DatabaseManager db = new DatabaseManager(NoNagScheduler.this);
				   db.open();
				    ArrayList<Object> row;		
				    row = db.getRowAsArray("settingsTable", "NoNagSchedulerStartTime");
				    if(row.isEmpty())
				    		db.addRow("settingsTable", "NoNagSchedulerStartTime",time);
				    else	
				    		db.updateRow("settingsTable", "NoNagSchedulerStartTime",time);
				    db.close();
				    }
			   
			   });
		
		endTime.setOnTimeChangedListener(new OnTimeChangedListener(){

			   @Override
			   public void onTimeChanged(TimePicker view, int hourOfDay, int minute) {
				   String time = hourOfDay + ":" + minute;
				   DatabaseManager db = new DatabaseManager(NoNagScheduler.this);
				   db.open();
				    ArrayList<Object> row;		
				    row = db.getRowAsArray("settingsTable", "NoNagSchedulerEndTime");
				    if(row.isEmpty())
				    		db.addRow("settingsTable", "NoNagSchedulerEndTime",time);
				    else	
				    		db.updateRow("settingsTable", "NoNagSchedulerEndTime",time);
				    db.close();
			   }});
}
}
