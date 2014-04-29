package com.appointext.frontend;

import com.bmsce.appointext.R;

import android.app.Activity;
import android.graphics.Color;
import android.os.Bundle;
import android.widget.TextView;

public class HelpAbout extends Activity {
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.helpabout);
		TextView text = (TextView)findViewById(R.id.helpAbout);
		text.setText("Welcome to Appointext!\n\n" +
					 "AppoinText is a simple to use(not to develop. :P) application that reads your messages and auto generates reminders if it finds that a reminder will be necessary");
		text.setShadowLayer(5, 0, 0, Color.BLACK);
		
	}
}
