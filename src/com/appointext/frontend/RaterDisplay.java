package com.appointext.frontend;

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
		addListenerOnButton();
	}

	 
	  public void addListenerOnButton() {
		  
		txtRatingValue = (TextView) findViewById(R.id.txtRatingValue);
		ratingBar = (RatingBar) findViewById(R.id.ratingBar);
		btnSubmit = (Button) findViewById(R.id.btnSubmit);
		btnSubmit.setOnClickListener(new OnClickListener() {
	 
			@Override
			public void onClick(View v) {
				float rating = ratingBar.getRating();
				AlertDialog alertDialog = new AlertDialog.Builder(RaterDisplay.this).create();
		        alertDialog.setTitle("AppoinText");
		        String toAdd;
		        if (rating < 3.0)
		        	toAdd = "We shall try our best to improve!";
		        else
		        	toAdd = "We are happy that you liked it!";
		        
		        alertDialog.setMessage("Thank you for your feedback! :)" + toAdd);
		        alertDialog.setButton (DialogInterface.BUTTON_POSITIVE, "OK", new DialogInterface.OnClickListener() {
		                public void onClick(DialogInterface dialog, int which) {
		                }
		        });
		        alertDialog.show();
		    }
	  });
	  }
}
