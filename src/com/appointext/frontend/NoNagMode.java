package com.appointext.frontend;

import java.util.ArrayList;

import com.appointext.database.DatabaseManager;
import com.bmsce.appointext.R;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Toast;
import android.widget.ToggleButton;

public class NoNagMode extends Activity {
	
	@Override
	protected void onCreate(Bundle savedInstanceState) { //Demo
		super.onCreate(savedInstanceState);
		setContentView(R.layout.no_nag);
		final ToggleButton TogButton = (ToggleButton) findViewById(R.id.NoNagStatusToggle);
		DatabaseManager db = new DatabaseManager(NoNagMode.this);
		db.open();
		ArrayList<Object> row;		
	    row = db.getRowAsArray("settingsTable", "NoNagStatusToggle");

	    if(!row.isEmpty())	{

	    	if((row.get(1).toString()).equalsIgnoreCase("On"))
	    		TogButton.setChecked(true);
	    	else
	    		TogButton.setChecked(false);
	    }
	    db.close();
		TogButton.setOnClickListener(new OnClickListener()
		{
			
		    @Override
		    public void onClick(View v)
		    {boolean on = (TogButton.isChecked()); 
		    DatabaseManager db = new DatabaseManager(NoNagMode.this);
Log.i("AppoinText Onclick", "Came into clicking of the toggle button");
		    db.open();
		    ArrayList<Object> row;
		    row = db.getRowAsArray("settingsTable", "NoNagStatusToggle");
		    if(row.isEmpty())	{
		    	if(on)
		    		db.addRow("settingsTable", "NoNagStatusToggle", "On");
		    	else
		    		db.addRow("settingsTable", "NoNagStatusToggle", "Off");
		    }
		    else	{
		    	if(on)
		    		db.updateRow("settingsTable", "NoNagStatusToggle", "On");
		    	else
		    		db.updateRow("settingsTable", "NoNagStatusToggle", "Off");
		    }
		    String result = ((db.getRowAsArray("settingsTable", "NoNagStatusToggle")).get(1)).toString();
Log.i("Result of the toggle", result);
		    db.close();
		    
		    if (on) {
		    	Toast.makeText(getApplicationContext(), "No Nag Turned On", Toast.LENGTH_SHORT).show();
		    } 
		    
		    else {
		    	Toast.makeText(getApplicationContext(), "No Nag Turned Off", Toast.LENGTH_SHORT).show();
		    }
		    }
		});
	}
	
	public void NoNagSchedule(View V)	{
		Intent intent = new Intent(this, NoNagScheduler.class);
		startActivity(intent); 
			
	}	
}
