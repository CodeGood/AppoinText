package com.appointext.frontend;

import com.bmsce.appointext.R;
//Android Imports
import android.os.Bundle;
import android.app.Activity;
import android.view.Menu;
import android.widget.TextView;

public class AppoinTextActivity extends Activity {

	private TextView op;
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_appoin_text);
		
		op = (TextView)findViewById(R.id.op);
		op.setText("Just started");
		
	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		// Inflate the menu; this adds items to the action bar if it is present.
		getMenuInflater().inflate(R.menu.appoin_text, menu);
		return true;
	}

}
