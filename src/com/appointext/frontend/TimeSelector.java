package com.appointext.frontend;

import com.bmsce.appointext.R;

import android.app.Activity;
import android.os.Bundle;
import android.util.Log;
import android.widget.TextView;
import android.widget.TimePicker;
import android.widget.Toast;
import android.widget.TimePicker.OnTimeChangedListener;

public class TimeSelector extends Activity {
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.time_picker);
		
		TimePicker MorningTime;
		TimePicker EveningTime;
		TimePicker NightTime;
		TextView info;

		MorningTime = (TimePicker)findViewById(R.id.timePickerMorning);
		EveningTime = (TimePicker)findViewById(R.id.timePickerEvening);
		NightTime = (TimePicker)findViewById(R.id.timePickerNight);
		
		MorningTime.setOnTimeChangedListener(new OnTimeChangedListener(){

			   @Override
			   public void onTimeChanged(TimePicker view, int hourOfDay, int minute) {
				   String time = hourOfDay + ":" + minute;
				    Toast.makeText(getApplicationContext(), "Morning Default Time:  " + time, Toast.LENGTH_SHORT).show(); 
			   }});
		
		EveningTime.setOnTimeChangedListener(new OnTimeChangedListener(){

			   @Override
			   public void onTimeChanged(TimePicker view, int hourOfDay, int minute) {
				   String time = hourOfDay + ":" + minute;
				    Toast.makeText(getApplicationContext(), "Evening Default Time: " + time, Toast.LENGTH_SHORT).show(); 
			   }});
		
		NightTime.setOnTimeChangedListener(new OnTimeChangedListener(){

			   @Override
			   public void onTimeChanged(TimePicker view, int hourOfDay, int minute) {
			    String time = hourOfDay + ":" + minute;
			    Toast.makeText(getApplicationContext(), "Night Default Time: " + time, Toast.LENGTH_SHORT).show(); 
			   }});
		
	}
	
	
}
