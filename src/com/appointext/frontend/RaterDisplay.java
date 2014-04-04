package com.appointext.frontend;

import java.util.ArrayList;

import com.appointext.database.DatabaseManager;
import com.bmsce.appointext.R;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.RatingBar;
import android.widget.TextView;

public class RaterDisplay extends Activity {
	
	private RatingBar ratingBar;
	  @SuppressWarnings("unused")
	private TextView txtRatingValue;
	  private Button btnSubmit;
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		Log.i("Display", "Reached Rater Method");
		super.onCreate(savedInstanceState);
		setContentView(R.layout.rate);
		ratingBar = (RatingBar) findViewById(R.id.ratingBar);
		DatabaseManager db = new DatabaseManager(RaterDisplay.this);
		db.open();
		ArrayList<Object> row;
		row = db.getRowAsArray("settingsTable","RatingBar");
		if(!row.isEmpty())	{
			int stars = Integer.parseInt((row.get(1).toString()).split("\\.")[0]);
			ratingBar.setRating(stars);
		}
			
		addListenerOnButton();
	}

	 
	  public void addListenerOnButton() {
		  
		txtRatingValue = (TextView) findViewById(R.id.txtRatingValue);
		btnSubmit = (Button) findViewById(R.id.btnSubmit);
		btnSubmit.setOnClickListener(new OnClickListener() {
	 
			@Override
			public void onClick(View v) {
				float rating = ratingBar.getRating();
				DatabaseManager db = new DatabaseManager(RaterDisplay.this);
				db.open();
				ArrayList<Object> row;
				row = db.getRowAsArray("settingsTable","RatingBar");
				if(row.isEmpty())
					db.addRow("settingsTable", "RatingBar", Float.toString(rating));
				else
					db.updateRow("settingsTable", "RatingBar", Float.toString(rating));
				db.close();
				AlertDialog alertDialog = new AlertDialog.Builder(RaterDisplay.this).create();
		        alertDialog.setTitle("AppoinText");
		        String toAdd;
		        if (rating < 3.0)
		        	toAdd = "We shall try our best to improve!";
		        else
		        	toAdd = "We are happy that you liked it!";
		     
		        alertDialog.setMessage("Thank you for your feedback! :)" + "\n" + toAdd);
		        alertDialog.setButton (DialogInterface.BUTTON_POSITIVE, "OK", new DialogInterface.OnClickListener() {
		                public void onClick(DialogInterface dialog, int which) {

		                }
		        });
		        alertDialog.show();
		    }
	  });
	  }
}
