package com.appointext.frontend;

import com.bmsce.appointext.R;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;

public class NoNagMode extends Activity {
	
	@Override
	protected void onCreate(Bundle savedInstanceState) { //Demo
		super.onCreate(savedInstanceState);
		setContentView(R.layout.no_nag);
	}
	
	public void NoNagSchedule(View V)	{
		Intent intent = new Intent(this, NoNagScheduler.class);
		startActivity(intent); 
			
	}
	
	
	
	
}
