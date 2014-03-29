package com.appointext.frontend; 
//import android.app.ActionBar;


import com.bmsce.appointext.R;

import android.os.Bundle;
import android.app.Activity;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.util.Log;
//import android.view.Menu;
//import android.view.MenuItem;
import android.view.View;
//import android.view.MenuInflater;


public class AppoinTextActivity extends Activity  {

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_main);
		Log.i("Display", "CREATED.");
	}
	
	public void viewSettings(View V) {
		Intent intent = new Intent(this, SettingsDisplay.class);
		startActivity(intent);        
	}
	
	public void viewReminders(View v) {
		Intent intent = new Intent(this, RemindersDisplay.class);
		startActivity(intent); 
    }
	
	public void viewInviter(View v) {
		Intent shareIntent = new Intent(android.content.Intent.ACTION_SEND);
	    shareIntent.setType("text/plain");
	    shareIntent.putExtra(android.content.Intent.EXTRA_SUBJECT, "Try AppoinText for Android!");
	    shareIntent.putExtra(android.content.Intent.EXTRA_TEXT, "I'm using AppoinText for Android and I recommend it! Go get it on the Play Store! :)");

	    Intent chooserIntent = Intent.createChooser(shareIntent, "Share with");
	    chooserIntent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
	    startActivity(chooserIntent);   
    }
    
    public void viewRater(View v) {
    	Intent intent = new Intent(this, RaterDisplay.class);
		startActivity(intent); 
    }
    
    public void viewTnc(View v) {
    	Intent intent = new Intent(this, TNCDisplay.class);
		startActivity(intent); 
    }
    
	public void viewAbout(View v) {
    	AlertDialog alertDialog = new AlertDialog.Builder(this).create();
        alertDialog.setTitle("AppoinText v2 ®™");
        alertDialog.setMessage("Developed by AAAM for BMSCE.\n 2013-2014");
        alertDialog.setButton (DialogInterface.BUTTON_POSITIVE, "OK", new DialogInterface.OnClickListener() {
                public void onClick(DialogInterface dialog, int which) {
                }
        });
        alertDialog.show();
    }
	
}

