package com.appointext.frontend;

import com.bmsce.appointext.R;

import android.app.Activity;
import android.os.Bundle;
import android.view.View;
import android.widget.TextView;
import android.widget.TimePicker;
import android.widget.TimePicker.OnTimeChangedListener;
import android.widget.Toast;

public class NoNagScheduler extends Activity{
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.nonagscheduler);
		
		TimePicker StartTime;
		TimePicker EndTime;
		
		StartTime = (TimePicker)findViewById(R.id.NoNagSchedulerStartTime);
		EndTime = (TimePicker)findViewById(R.id.NoNagSchedulerEndTime);
		
		StartTime.setOnTimeChangedListener(new OnTimeChangedListener(){

			   @Override
			   public void onTimeChanged(TimePicker view, int hourOfDay, int minute) {
				   String time = hourOfDay + ":" + minute;
				    Toast.makeText(getApplicationContext(), "Start" + time, Toast.LENGTH_SHORT).show(); 
			   }});
		
		EndTime.setOnTimeChangedListener(new OnTimeChangedListener(){

			   @Override
			   public void onTimeChanged(TimePicker view, int hourOfDay, int minute) {
				   String time = hourOfDay + ":" + minute;
				    Toast.makeText(getApplicationContext(), "End" + time, Toast.LENGTH_SHORT).show(); 
			   }});
}
}
