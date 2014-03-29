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
import android.widget.RatingBar.OnRatingBarChangeListener;
import android.widget.TextView;
import android.widget.Toast;

public class RaterDisplay extends Activity {
	
	private RatingBar ratingBar;
	  private TextView txtRatingValue;
	  private Button btnSubmit;
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		Log.i("Display", "Reached Rater Method");
		super.onCreate(savedInstanceState);
		setContentView(R.layout.rate);
		
	/*	RatingBar rb = (RatingBar) findViewById(R.id.ratingBar);
		Log.i("AppoinText", "You rated me at " + String.valueOf(rb.getRating()));*/
		//addListenerOnRatingBar();
		addListenerOnButton();
	}

	/*public void addListenerOnRatingBar() {
		 
		ratingBar = (RatingBar) findViewById(R.id.ratingBar);
		txtRatingValue = (TextView) findViewById(R.id.txtRatingValue);
	 
		//if rating value is changed,
		//display the current rating value in the result (textview) automatically
		ratingBar.setOnRatingBarChangeListener(new OnRatingBarChangeListener() {
			public void onRatingChanged(RatingBar ratingBar, float rating,
				boolean fromUser) {
	 
				txtRatingValue.setText(String.valueOf(rating));
	 
			}
		});
	  }*/
	 
	  public void addListenerOnButton() {
		  
		txtRatingValue = (TextView) findViewById(R.id.txtRatingValue);
		ratingBar = (RatingBar) findViewById(R.id.ratingBar);
		btnSubmit = (Button) findViewById(R.id.btnSubmit);
	 
		//if click on me, then display the current rating value.
		btnSubmit.setOnClickListener(new OnClickListener() {
	 
			@Override
			public void onClick(View v) {
				String rating = String.valueOf(ratingBar.getRating()).substring(0,1);
				AlertDialog alertDialog = new AlertDialog.Builder(RaterDisplay.this).create();
		        alertDialog.setTitle("Thank you");
		        alertDialog.setMessage("Thank you for giving us " + rating +"\u000059");
		        alertDialog.setButton (DialogInterface.BUTTON_POSITIVE, "OK", new DialogInterface.OnClickListener() {
		                public void onClick(DialogInterface dialog, int which) {
		                }
		        });
		        alertDialog.show();
		    }
	  });
	  }
}
