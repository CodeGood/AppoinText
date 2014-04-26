package com.appointext.frontend;

import java.util.ArrayList;

import com.appointext.database.DatabaseManager;
import com.bmsce.appointext.R;
import android.widget.LinearLayout;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;
import android.util.Log;
import android.view.View;
import android.app.Activity;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;

public class BlockedNumberDisplay extends Activity {

	@Override
	protected void onCreate(Bundle savedInstanceState) { //Demo
		super.onCreate(savedInstanceState);
		setContentView(R.layout.blockednumberdisplay);
		LinearLayout linear = (LinearLayout)findViewById(R.id.blockedNumberDisplay);
		LinearLayout.LayoutParams params = new LinearLayout.LayoutParams(
				LinearLayout.LayoutParams.MATCH_PARENT,
				LinearLayout.LayoutParams.WRAP_CONTENT);
		final DatabaseManager db = new DatabaseManager(this);
		db.open();
		ArrayList<Object> row;
		row = db.getRowAsArray("settingsTable", "BlockedNumbers");
		db.close();
		if(row.get(1) == null)	{
			AlertDialog alertDialog = new AlertDialog.Builder(
					BlockedNumberDisplay.this).create();
			 alertDialog.setTitle("Excluded Numbers");
			 alertDialog.setMessage("No Excluded Numbers!");
			 alertDialog.show();
		}
		else	{
			int i=0;
			final String num = row.get(1).toString();
			Log.i("Blocked Number String from DB",num);
			final String[] numbers = num.split(",");
			//.setText(numbers);
			for(String number : numbers)	{
				Button btn = new Button(this);
				btn.setId(i);
				final int id_ = btn.getId();
				i++;
				btn.setText(number);
				linear.addView(btn, params);
				Button btn1 = ((Button) findViewById(id_));
				btn1.setOnLongClickListener(new View.OnLongClickListener() {
					public boolean onLongClick(View view) {
						AlertDialog.Builder alertDialog = new AlertDialog.Builder(BlockedNumberDisplay.this);
						alertDialog.setTitle("Deletion");
						alertDialog.setMessage("Delete this number from Excluded List?");
						alertDialog.setPositiveButton("Yes", new DialogInterface.OnClickListener() {
							public void onClick(DialogInterface dialog, int which) {
								Toast.makeText(getApplicationContext(), numbers[id_] + " has been deleted", Toast.LENGTH_SHORT).show();
								// User pressed YES button. Write Logic Here
								String update = "";
								for(String temp: numbers)	{
									if(!temp.equals(numbers[id_]))
										if(update.equals(""))
											update = update + temp;
										else
											update = update + "," + temp;
								}
								db.open();
								db.updateRow("settingsTable", "BlockedNumbers", update);
								Log.i("Blocked Number DB Updated with", update);
								db.close();
								Intent intent = getIntent();
								finish();
								startActivity(intent);
							}
						});

						// Setting Negative "NO" Button
						alertDialog.setNegativeButton("No", new DialogInterface.OnClickListener() {
							public void onClick(DialogInterface dialog, int which) {
								//Do nothing! :P
							}
						});
						// Showing Alert Message
						alertDialog.show();
						return false;
					}
				});
			}
		}
	}
}
