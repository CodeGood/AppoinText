package com.appointext.frontend;

import com.bmsce.appointext.R;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;

public class HelpDisplay extends Activity {
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.help);
	}
	
	public void helpAbout(View V) {
		Intent intent = new Intent(this, HelpAbout.class);
		startActivity(intent);        
	}
	
	public void helpSettings(View V) {
		Intent intent = new Intent(this, HelpSettings.class);
		startActivity(intent);        
	}
	
	
	public void helpContact(View V) {
		AlertDialog alertDialog = new AlertDialog.Builder(this).create();
        alertDialog.setTitle("Contact Us!");
        alertDialog.setMessage("Have complaints, suggestions or just want to talk to us?\nMail us on appointextteam@googlegroups.com right away!");
        alertDialog.setButton (DialogInterface.BUTTON_POSITIVE, "OK", new DialogInterface.OnClickListener() {
                public void onClick(DialogInterface dialog, int which) {
                }
        });
        alertDialog.show();       
	}
}
