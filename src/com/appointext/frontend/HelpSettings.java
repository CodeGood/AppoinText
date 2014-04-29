package com.appointext.frontend;

import com.bmsce.appointext.R;

import android.app.Activity;
import android.graphics.Color;
import android.os.Bundle;
import android.widget.TextView;

public class HelpSettings extends Activity {
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.helpsettings);
		TextView text = (TextView)findViewById(R.id.helpSettings);
		text.setText("Need help with the myriad of settings AppoinText offers you? You're in the right place!\n\n" +
					"Excluded Numbers: Manage the numbers you wish to exclude from the purview of AppoinText\n" +
					"	Enter a Number Manually: Manually enter number that is not in your contacts you wish to exclude.\n" +
					"	Enter From Contacts: Choose a number to exclude from your contacts." +
					"	Show Excluded Numbers: Shows you all the numbers you have excluded. To delete a number from the excluded list, just long press the number you wish to delete.\n\n" +
					"Prompt Control: AppoinText is smart enough to do most things by itself without your intervention. But sometime, it needs your input through prompts to be at its best behaviour. This settings helps you choose when you want to be presented with prompts.\n" +
					"	Always: AppoinText will always raise a prompt no matter how obvious things are.\n" +
					"	Never: AppoinText will never raise a prompt no matter how vague things are. \n" +
					"	In Case of Ambiguity: AppoinText will only raise prompt in case it's confused. This won't happen often, we promise!\n\n" +
					"Day Timings:  Here, you get to choose your definition of Morning, Afternoon, Evening and Night. This helps us tailor make the reminders according to what time you feel the morning should begin it. Caters to diurnal and nocturnal beings alike. ;)\n\n" +
					"Quiet Hours: There maybe times you wouldn't want AppoinText to bother you with the reminders it has set. In such times, Quiet Hours comes to your rescue.\n" +
					"	Quiet Hours Toggle: Turn On and Turn Off Quiet Hours whenever you like using this simple switch.\n" +
					"	Quiet Hours Scheduler: If you wish to turn on Quiet Hours everyday for a particular period, you can use the scheduler. Just choose the start time and end time of the scheduler and you're good to go!\n\n");
		text.setShadowLayer(5, 0, 0, Color.BLACK);
	}
}
