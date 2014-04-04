package com.appointext.frontend;

import java.util.ArrayList;

import com.appointext.database.DatabaseManager;
import com.bmsce.appointext.R;

import android.app.Activity;
import android.os.Bundle;
import android.widget.TextView;

public class BlockedNumberDisplay extends Activity {

	@Override
	protected void onCreate(Bundle savedInstanceState) { //Demo
		super.onCreate(savedInstanceState);
		setContentView(R.layout.blockednumberdisplay);
		TextView text = (TextView) findViewById(R.id.blockedNumberDisplayer);
		DatabaseManager db = new DatabaseManager(this);
		db.open();
		ArrayList<Object> row;
		row = db.getRowAsArray("settingsTable", "BlockedNumbers");
		if(row.isEmpty())
			text.setText("No numbers are excluded from AppoinText's jurisdiction");
		else	{
			String numbers = row.get(1).toString().replaceAll(",","\n");
			text.setText(numbers);
		}
	}
}
