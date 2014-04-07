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
				db.close();
				if(row.isEmpty())	{
					Log.i("Enter where I should't", ":(");
					db.open();
					db.addRow("settingsTable", "BlockedNumbers", number);
					db.close();
					Toast.makeText(getApplicationContext(), number + " added to excluded list!", Toast.LENGTH_SHORT).show();
					editText.setText("");
				}
				else	{
					String existingNumber = row.get(1).toString();
					if(!existingNumber.contains(number))	{
						db.open();
						db.updateRow("settingsTable", "BlockedNumbers", existingNumber + "," + number);
						db.close();
						Toast.makeText(getApplicationContext(), number + " added to excluded list!", Toast.LENGTH_SHORT).show();
						editText.setText("");
					}
					else
						Toast.makeText(getApplicationContext(), number + " already exists in excluded list!", Toast.LENGTH_SHORT).show();
					}			
				return false;
			}
		});
	}
}
