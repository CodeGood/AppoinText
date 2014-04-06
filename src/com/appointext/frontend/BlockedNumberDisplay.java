package com.appointext.frontend;

import java.util.ArrayList;

import com.appointext.database.DatabaseManager;
import com.bmsce.appointext.R;
import android.widget.LinearLayout;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;
import android.view.View;
import android.app.Activity;
import android.os.Bundle;

public class BlockedNumberDisplay extends Activity {

	@Override
	protected void onCreate(Bundle savedInstanceState) { //Demo
		super.onCreate(savedInstanceState);
		setContentView(R.layout.blockednumberdisplay);
		LinearLayout linear = (LinearLayout)findViewById(R.id.linearLayout);
		LinearLayout.LayoutParams params = new LinearLayout.LayoutParams(
	            LinearLayout.LayoutParams.MATCH_PARENT,
	            LinearLayout.LayoutParams.WRAP_CONTENT);
		DatabaseManager db = new DatabaseManager(this);
		db.open();
		ArrayList<Object> row;
		row = db.getRowAsArray("settingsTable", "BlockedNumbers");
		if(row.isEmpty())	{
			TextView text = new TextView(this);
			text.setText("No Numbers Have Been Excluded!");
		}
		else	{
			int i=0;
			String[] numbers = row.get(1).toString().split(",");
			//.setText(numbers);
			for(String number : numbers)	{
				i++;
			    Button btn = new Button(this);
			    btn.setId(i);
			    final int id_ = btn.getId();
			    btn.setText(number);
			    linear.addView(btn, params);
			    Button btn1 = ((Button) findViewById(id_));
			    btn1.setOnClickListener(new View.OnClickListener() {
			        public void onClick(View view) {
			            Toast.makeText(view.getContext(),
			                    "Button clicked index = " + id_, Toast.LENGTH_SHORT)
			                    .show();
			}
		});
	}
}
	}
}
