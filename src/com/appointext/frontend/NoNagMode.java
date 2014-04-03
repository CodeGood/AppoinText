package com.appointext.frontend;

import com.appointext.database.DatabaseManager;
import com.bmsce.appointext.R;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Toast;
import android.widget.ToggleButton;

public class NoNagMode extends Activity {
	
	@Override
	protected void onCreate(Bundle savedInstanceState) { //Demo
		super.onCreate(savedInstanceState);
		setContentView(R.layout.no_nag);
		final ToggleButton b = (ToggleButton) findViewById(R.id.NoNagStatusToggle);

		// attach an OnClickListener
		b.setOnClickListener(new OnClickListener()
		{
			
		    @Override
		    public void onClick(View v)
		    {boolean on = (b.isChecked());
		    
		    if (on) {
		    	Toast.makeText(getApplicationContext(), "No Nag Turned On", Toast.LENGTH_SHORT).show();
		    } else {
		    	Toast.makeText(getApplicationContext(), "No Nag Turned Off", Toast.LENGTH_SHORT).show();
		    }
		    DatabaseManager db = new DatabaseManager(NoNagMode.this);
	        db.open();
		    }
		});
	}
	public void NoNagSchedule(View V)	{
		Intent intent = new Intent(this, NoNagScheduler.class);
		startActivity(intent); 
			
	}	
}
