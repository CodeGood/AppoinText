package com.appointext.frontend;

import java.util.ArrayList;

import com.appointext.database.DatabaseManager;
import com.bmsce.appointext.R;
import android.app.Activity;
import android.os.Bundle;
import android.util.Log;
import android.view.KeyEvent;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.TextView.OnEditorActionListener;
import android.widget.Toast;

public class ManualBlockedNumber extends Activity {
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.manualblockednumber);
		final EditText editText = (EditText) findViewById(R.id.ManualBlockNumber);
		editText.setOnEditorActionListener(new OnEditorActionListener() {
			@Override
			public boolean onEditorAction(TextView v, int actionId, KeyEvent event) {
				String number = (v.getText().toString()).replaceAll("[^0-9]", "");
				DatabaseManager db = new DatabaseManager(ManualBlockedNumber.this);
				db.open();
				ArrayList<Object> row;
				row = db.getRowAsArray("settingsTable", "BlockedNumbers");
				if(!number.equals(""))	{
					if(row.isEmpty())	{
						Log.i("onEditorAction", "null case");
						if(number.length() < 11)
							number = "91" + number;
						db.addRow("settingsTable", "BlockedNumbers", number);
						db.close();
						Toast.makeText(getApplicationContext(), number + " added to excluded list!", Toast.LENGTH_SHORT).show();
						editText.setText("");
						return false;
					}

					else if(row.get(1).toString().equals("NoNumbers"))	{
						Log.i("onEditorAction", "null case");
						if(number.length() < 11)
							number = "91" + number;
						db.updateRow("settingsTable", "BlockedNumbers", number);
						db.close();
						Toast.makeText(getApplicationContext(), number + " added to excluded list!", Toast.LENGTH_SHORT).show();
						editText.setText("");
						return false;
					}

					else	{
						int flag = 0;
						String retrieveNumber = row.get(1).toString();
						String[] existingNumber = retrieveNumber.split(",");
						for(String temp: existingNumber)
							if(number.equals(temp))	
								flag = 1;
						if(flag == 0)	{
							if(number.length() < 11)
								number = "91" + number;
							db.updateRow("settingsTable", "BlockedNumbers", retrieveNumber + "," + number);
							Log.i("Number that is being added", retrieveNumber + "," + number);
							db.close();
							Toast.makeText(getApplicationContext(), number + " added to excluded list!", Toast.LENGTH_SHORT).show();
							editText.setText("");
						}
						else
							Toast.makeText(getApplicationContext(), number + " already exists in excluded list!", Toast.LENGTH_SHORT).show();		
					}
				}
				return false;
			}
		});
	}
}