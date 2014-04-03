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
import android.widget.Toast;

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
				String rating = String.valueOf(ratingBar.getRating()).substring(0,1);
				AlertDialog alertDialog = new AlertDialog.Builder(RaterDisplay.this).create();
		        alertDialog.setTitle("AppoinText");
		        alertDialog.setMessage("Thank you for your feedback! :)");
		        alertDialog.setButton (DialogInterface.BUTTON_POSITIVE, "OK", new DialogInterface.OnClickListener() {
		                public void onClick(DialogInterface dialog, int which) {
		                }
		        });
		        alertDialog.show();
		    }
	  });
	  }
}
